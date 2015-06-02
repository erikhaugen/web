package biz.neustar.webmetrics.plugins.neustar_s3_maven_plugin;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.internal.StaticCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.transfer.Transfer;
import com.amazonaws.services.s3.transfer.TransferManager;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kevin.murdoff@neustar.biz">Kevin F. Murdoff</a>
 */
@Mojo(name = "upload")
public class S3UploadMojo extends AbstractMojo {
	
	/** Access key for S3. */
	@Parameter(alias = "accessKey")
	private String accessKey;
	
	/** Secret key for S3. */
	@Parameter(alias = "secretKey")
	private String secretKey;
	
	/**
	 *  Execute all steps up except the upload to the S3.
	 *  This can be set to true to perform a "dryRun" execution.
	 */
	@Parameter(alias = "doNotUpload", defaultValue = "false")
	private boolean doNotUpload;
	
	/** Force override of endpoint for S3 regions such as EU. */
	@Parameter(alias = "endpoint")
	private String endpoint;
	
	/** In the case of a directory upload, recursively upload the contents. */
	@Parameter(alias = "recursive", defaultValue = "false")
	private boolean recursive;
	
	/** */
	@Parameter(alias = "files")
	private List<UploadFile> files;
	
	/** */
	@Parameter(alias = "buckets")
	private List<String> buckets;
	
	/** */
	@Parameter(alias = "failOnError", defaultValue = "true")
	private boolean failOnError;
	
	/** */
	public void execute() throws MojoExecutionException {
    	
		getLog().debug("accessKey:  " + accessKey);
		getLog().debug("secretKey:  " + secretKey);
//		getLog().debug("bucketName:  " + bucketName);
		
		if ((files == null) || (files.isEmpty())) {
			throw new MojoExecutionException("No files configured to upload!");
		}
		
		if ((buckets == null) || (buckets.isEmpty())) {
			throw new MojoExecutionException("No buckets configured to upload files to!");
		}
		
		AmazonS3 s3 = getS3Client(accessKey, secretKey);

		for (String bucket : buckets) {
			
	    	for (UploadFile uf : files) {
	    		File source = new File(uf.getSource());
	    		if (!source.exists()) {
	    			throw new MojoExecutionException("File/folder '" + source.getAbsolutePath() + "' doesn't exist.");
	    		}
	    		
	    		if (!doNotUpload) {
	    			
		    		//	Upload the source File to the S3 bucket.
		    		boolean success = upload(s3, bucket, source, uf.getTarget());
		    		
		    		if (!success) {
			    		if (failOnError) {
			    			throw new MojoExecutionException("S3 file upload failed!");
			    		}
		    		}
	    		} else {
	    			getLog().debug("File " + source.getAbsolutePath() + " would have been uploaded to " + bucket + "/" + uf.getTarget());
	    		}
	    	}
		}
    }
    
	/** */
    private boolean upload(AmazonS3 s3, String bucketName, File sourceFile, String targetPath) throws MojoExecutionException {
    	
    	TransferManager mgr = new TransferManager(s3);
    	Transfer transfer = null;
    	transfer = mgr.upload(bucketName, targetPath, sourceFile);
    	
    	try {
			transfer.waitForCompletion();
			getLog().info("Transferred " + transfer.getProgress().getBytesTransfered() + " bytes.");
		} catch (AmazonServiceException e) {
			e.printStackTrace();
			return false;
		} catch (AmazonClientException e) {
			e.printStackTrace();
			return false;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}
    	
    	return true;
    }
    
	/** */
    private static AmazonS3 getS3Client(String accessKey, String secretKey) {
    	
    	AWSCredentialsProvider provider = null;
    	
    	if (accessKey != null && secretKey != null) {
    		AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
    		provider = new StaticCredentialsProvider(credentials);
    	} else {
    		provider = new DefaultAWSCredentialsProviderChain();
    	}
    	return new AmazonS3Client(provider);
    }

	/** */
	public String getAccessKey() {
		return accessKey;
	}

	/** */
	public String getSecretKey() {
		return secretKey;
	}

	/** */
	public boolean isDoNotUpload() {
		return doNotUpload;
	}

	/** */
	public String getEndpoint() {
		return endpoint;
	}

	/** */
	public boolean isRecursive() {
		return recursive;
	}

	/** */
	public List<UploadFile> getFiles() {
		return files;
	}

	/** */
	public List<String> getBuckets() {
		return buckets;
	}

	/** */
	public boolean isFailOnError() {
		return failOnError;
	}

	/** */
	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	/** */
	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	/** */
	public void setDoNotUpload(boolean doNotUpload) {
		this.doNotUpload = doNotUpload;
	}

	/** */
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	/** */
	public void setRecursive(boolean recursive) {
		this.recursive = recursive;
	}

	/** */
	public void setFiles(List<UploadFile> files) {
		this.files = files;
	}

	/** */
	public void setBuckets(List<String> buckets) {
		this.buckets = buckets;
	}

	/** */
	public void setFailOnError(boolean failOnError) {
		this.failOnError = failOnError;
	}
}

/**
 * Created: May 29, 2015 4:27:25 PM
 *      by: kmurdoff
 */
package biz.neustar.webmetrics.plugins.neustar_s3_maven_plugin;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.internal.StaticCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.SetBucketPolicyRequest;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kevin.murdoff@neustar.biz">Kevin F. Murdoff</a>
 */
@Mojo(name = "apply-policy")
public class S3ApplyPolicyMojo extends AbstractMojo {

	/** Access key for S3. */
	@Parameter(alias = "accessKey")
	private String accessKey;
	
	/** Secret key for S3. */
	@Parameter(alias = "secretKey")
	private String secretKey;

	/**	Name of the file containing the S3 policy. */
	@Parameter(alias = "policyFilename")
	private String policyFilename;
	
	/** List of bucket names to apply the policy to. */
	@Parameter(alias = "buckets")
	private List<String> buckets;
	
	/* (non-Javadoc)
	 * @see org.apache.maven.plugin.Mojo#execute()
	 */
	public void execute() throws MojoExecutionException, MojoFailureException {
		
		if ((buckets == null) || (buckets.isEmpty())) {
			throw new MojoExecutionException("No buckets configured to apply policy to!");
		}
		
		//	Read the policy file contents.
		String policyText = null;
		try {
			policyText = readPolicyFile(policyFilename);
		} catch (FileNotFoundException fnfe) {
			throw new MojoExecutionException("Plugin execution failed.", fnfe);
		} catch (IOException ioe) {
			throw new MojoExecutionException("Plugin execution failed.", ioe);
		}
		
		//	Check for missing policy content.
		if ((policyText == null) || ("".equals(policyText))) {
			throw new MojoExecutionException("Plugin execution failed.  No content found in policy file.");
		}
		
		AmazonS3 s3 = getS3Client(accessKey, secretKey);
		
		for (String bucket : buckets) {
			SetBucketPolicyRequest req = new SetBucketPolicyRequest(bucket, policyText);
			s3.setBucketPolicy(req);
		}
	}

    /**
     * <p></p>
     * 
     * @param accessKey
     * @param secretKey
     * 
     * @return
     */
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

    /**
     * <p></p>
     * 
     * @param filename
     * 
     * @return
     * 
     * @throws FileNotFoundException
     * @throws IOException
     */
    private static String readPolicyFile(String filename) throws FileNotFoundException, IOException {
    	
    	InputStream in = S3ApplyPolicyMojo.class.getClassLoader().getResourceAsStream(filename);
    	if (in == null) {
    		throw new FileNotFoundException("Unable to find file '" + filename + "'.");
    	}
    	BufferedReader reader = new BufferedReader(new InputStreamReader(in));
    	String line = "";
    	StringBuilder sb = new StringBuilder();
    	while ((line = reader.readLine()) != null) {
    		sb.append(line);
    	}
    	
    	return sb.toString();
    }
    
    public String getAccessKey() {
		return accessKey;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public String getPolicyFilename() {
		return policyFilename;
	}

	public List<String> getBuckets() {
		return buckets;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public void setPolicyFilename(String policyFilename) {
		this.policyFilename = policyFilename;
	}

	public void setBuckets(List<String> buckets) {
		this.buckets = buckets;
	}

}

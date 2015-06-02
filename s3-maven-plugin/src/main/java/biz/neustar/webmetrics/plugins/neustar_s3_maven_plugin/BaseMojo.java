/**
 * Created: May 29, 2015 4:34:19 PM
 *      by: kmurdoff
 */
package biz.neustar.webmetrics.plugins.neustar_s3_maven_plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.internal.StaticCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kevin.murdoff@neustar.biz">Kevin F. Murdoff</a>
 */
public abstract class BaseMojo extends AbstractMojo {

	/* (non-Javadoc)
	 * @see org.apache.maven.plugin.Mojo#execute()
	 */
	public void execute() throws MojoExecutionException, MojoFailureException {}

    protected static AmazonS3 getS3Client(String accessKey, String secretKey) {
    	
    	AWSCredentialsProvider provider = null;
    	
    	if (accessKey != null && secretKey != null) {
    		AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
    		provider = new StaticCredentialsProvider(credentials);
    	} else {
    		provider = new DefaultAWSCredentialsProviderChain();
    	}
    	return new AmazonS3Client(provider);
    }
}

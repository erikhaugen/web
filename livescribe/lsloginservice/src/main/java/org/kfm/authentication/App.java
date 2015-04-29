/**
 * Created:  Oct 24, 2013 4:42:54 PM
 */
package org.kfm.authentication;

import org.apache.log4j.Logger;
import org.jasypt.digest.StandardStringDigester;

/**
 * <p></p>
 * 
 * <p>To invoke this class from the command line after building, use the 
 * following from the <code>target/lsloginservice-1.3-SNAPSHOT/WEB-INF</code>
 * directory:</p>
 * 
 * <p><code>java -cp classes:lib/* org.kfm.authentication.App letmein</code></p>
 * 
 * <p>The response will be something like:</p>
 * 
 * <p><code>Resulting digest:  538FrW1HX1mxaqLXOqakPuERkqPfj60T0XwEfA==</code></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class App {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	private static final int SALT_LENGTH = 12;
	
	private StandardStringDigester digester = null;
	
	/**
	 * <p>Default constructor that instantiates and initializes an internal
	 * <code>StandardStringDigester</code> to use the SHA-1 algorithm with
	 * a random 12-character salt and iterate 50000 times.</p>
	 * 
	 */
	public App() {
		
		digester = new StandardStringDigester();
		digester.setAlgorithm("SHA-1");
		digester.setSaltSizeBytes(SALT_LENGTH);
		digester.setIterations(50000);
	}

	/**
	 * <p></p>
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		String method = "main()";

		if ((args.length == 0) || (args.length > 1)) {
			printUsage();
			System.exit(1);
		}
		
		App app = new App();
		
		String message = args[0];
		
		String digest = app.encrypt(message);
		boolean result = app.match(message, digest);
		
		String salt = digest.substring(0, SALT_LENGTH);
		
		System.out.println("\nResulting digest:  " + digest + "\n");
		System.out.println("Salt must be:  " + salt + "\n");
	}

	/**
	 * <p>Compares the given message to the given stored digest to determine
	 * if they cryptographically match.</p>
	 * 
	 * @param message The message to match.
	 * @param digest The stored digest to match against.
	 * 
	 * @return <code>true</code> if they match; <code>false</code> if they do not.
	 */
	public boolean match(String message, String digest) {
		
		if (digester.matches(message, digest)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * <p>Generates a Base64-encoded, SHA-1 hash of the given message.</p>
	 * 
	 * @param message The message to hash.
	 * 
	 * @return a message digest for use in storing in the database.
	 */
	public String encrypt(String message) {
		
		String digest = digester.digest(message);
		
		return digest;
	}
	
	/**
	 * <p></p>
	 * 
	 */
	private static void printUsage() {
		
		String method = "printUsage()";
		
		StringBuilder sb = new StringBuilder();
		sb.append("\nUSAGE:\n");
		sb.append("    java org.kfm.authentication.Main < message >\n\n");
		System.out.println(sb.toString());
	}
}

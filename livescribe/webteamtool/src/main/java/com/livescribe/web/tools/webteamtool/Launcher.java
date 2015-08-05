/*
 * Created:  Aug 3, 2011
 *      By:  kmurdoff
 */
/**
 * 
 */
package com.livescribe.web.tools.webteamtool;

import com.jdotsoft.jarloader.JarClassLoader;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class Launcher {

	/**
	 * <p></p>
	 * 
	 */
	public Launcher() {
	}

	/**
	 * <p></p>
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		JarClassLoader jcl = new JarClassLoader();
		try {
			jcl.invokeMain("com.livescribe.web.tools.webteamtool.Main", args);
		}
		catch (Throwable e) {
			e.printStackTrace();

		}
	}
}

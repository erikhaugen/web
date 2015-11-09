package com.livescribe.importsecretkeytool;


public class AppLauncher {
	public static void main(String[] args) throws InterruptedException {
		JarInJarLoader jcl = new JarInJarLoader();
		try {
			jcl.invokeMain("com.livescribe.importsecretkeytool.Client", args);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

}
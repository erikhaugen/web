package com.livescribe.framework.lsconfiguration;

/**
 * <p>Enumerates all supported environment names.</p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public enum Env {
	
    LOCAL("local"),
    TEST("test"),
    DEVELOPMENT("dev"),
    QA("qa"),
    STAGE("stage"),
    PRODUCTION("prod");
    
    String name;
    
    private Env(String name) {
        this.name = name;
    }
    
    public String getEnvName(){
        return name;
    }
    
    public String toString(){
        return getEnvName();
    }
    
    static public Env getEnv(String name){
    	
    	if (name != null) {
	        for(Env env: Env.values()){
	            if (env.getEnvName().equals(name.toLowerCase())) {
	                return env;
	            }
	        }
    	}
        return null;
    }
}

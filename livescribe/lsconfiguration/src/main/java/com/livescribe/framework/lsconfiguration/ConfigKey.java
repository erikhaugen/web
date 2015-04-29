package com.livescribe.framework.lsconfiguration;

public enum ConfigKey {
    NONE("NONE", "NONE"),
//    DEFAULT_ENV ("lsconfiguration.defaultEnv","dev"),
    APP_NAME ("appName",""),
    APP_GROUP ("appGroup",""),
    RETURN_GLOBAL_WHEN_ENV_EMPTY ("lsconfiguration.returnGlobalWhenEnvEmpty","false");
    
    
    private String key, defValue;
    
    ConfigKey(String key, String defValue){
        this.key = key;
        this.defValue = defValue;
    }
    
    public String getKeyName(){
        return key;
    }
    
    public String getDefaultValue(){
        return defValue;
    }
}

/**
 * Created:  Dec 13, 2010 11:25:12 PM
 */
package com.livescribe.framework.version.response;

import java.util.ArrayList;
import java.util.List;

import com.livescribe.framework.version.dto.VersionDTO;
import com.livescribe.framework.web.response.ServiceResponse;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * <p></p>
 *
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
@XStreamAlias("configs")
public class ConfigResponse extends ServiceResponse {
	@XStreamAlias("config")
	static public class Config {
		@XStreamAlias("name")
	    @XStreamAsAttribute
		private String name;
		
		@XStreamAlias("value")
	    @XStreamAsAttribute
		private String value;

		public Config(String name, String value) {
			super();
			this.name = name;
			this.value = value;
		}
		
	}
	
	@XStreamImplicit
	private List<Config> configs = new ArrayList<Config>();
	
	@XStreamAlias("env")
    @XStreamAsAttribute
	private String env;
	
	public ConfigResponse() {
		super();
	}

	public List<Config> getConfigs() {
		return configs;
	}

	public String getEnv() {
		return env;
	}

	public void setEnv(String env) {
		this.env = env;
	}
	
}

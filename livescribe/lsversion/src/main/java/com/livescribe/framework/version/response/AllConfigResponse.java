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
@XStreamAlias("allconfigs")
public class AllConfigResponse extends ServiceResponse {
	
	@XStreamImplicit
	private List<ConfigResponse> configs = new ArrayList<ConfigResponse>();
	
	
	public AllConfigResponse() {
		super();
	}

	public List<ConfigResponse> getAllConfigs() {
		return configs;
	}

}

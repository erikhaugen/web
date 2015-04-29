/*
 * Created:  Oct 17, 2011
 *      By:  kmurdoff
 */
/**
 * 
 */
package com.livescribe.framework.version.service;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.livescribe.framework.version.dto.VersionDTO;
import com.livescribe.framework.version.exception.JarManifestNotFoundException;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
//@Service("versionService")
public interface VersionService {

	/**
	 * <p></p>
	 * 
	 * @param request
	 * 
	 * @return
	 */
	public VersionDTO getVersion(HttpServletRequest request) throws JarManifestNotFoundException, IOException;
	public void postVersion();
}

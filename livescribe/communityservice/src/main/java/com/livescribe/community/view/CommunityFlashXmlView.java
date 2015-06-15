/**
 * 
 */
package com.livescribe.community.view;

import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.io.XMLWriter;
import org.springframework.web.servlet.view.AbstractView;

import com.livescribe.community.CommunityConstants;
import com.livescribe.community.dto.Pencast;

/**
 * <p>Generates an XML feed of the <code>flash.xml</code> file.</p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class CommunityFlashXmlView extends AbstractView {

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.view.AbstractView#renderMergedOutputModel(java.util.Map, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		
		resp.setContentType(getContentType());
		
		PrintWriter pw = resp.getWriter();
		Pencast pencast = (Pencast)model.get("pencastItem");
		Document doc = pencast.getFlashXmlDom();
		if (doc != null) {
			XMLWriter writer = new XMLWriter(pw);
			writer.write(doc);
		}
	}

	/**
	 * <p>Returns the content type <code>text/plain+text</code>.</p>
	 * 
	 * @see org.springframework.web.servlet.view.AbstractView#getContentType()
	 */
	@Override
	public String getContentType() {
		
		return CommunityConstants.LINK_TYPE_PCC;
	}
}

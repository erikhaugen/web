/**
 * Created:  Dec 23, 2014 4:15:43 PM
 */
package org.kfm.camel.enml;

import java.io.StringWriter;
import java.util.ArrayList;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.PropertyInject;
import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.kfm.camel.entity.evernote.EvernoteMedia;
import org.kfm.camel.util.CAKHelper;
import org.kfm.jpa.Document;
import org.kfm.jpa.Page;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class StrokesENMLProcessor implements Processor {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	@PropertyInject("player.url.prefix")
	private String playerUrlPrefix;

	/**
	 * <p></p>
	 * 
	 */
	public StrokesENMLProcessor() {
	}

	/* (non-Javadoc)
	 * @see org.apache.camel.Processor#process(org.apache.camel.Exchange)
	 */
	@Override
	public void process(Exchange exchange) throws Exception {

		String method = "process()";

		Page page = exchange.getIn().getBody(Page.class);
		Document document = page.getDocument();
		
		VelocityEngine ve = new VelocityEngine();
		ve.setProperty(VelocityEngine.RUNTIME_LOG_LOGSYSTEM, this);
		ve.init();
		
		VelocityContext context = new VelocityContext();
		context.put("livescribe_home_url_href", "");

		context.put("player_launch_url_href", playerUrlPrefix + "/int/player/notesdesktop.htm?" + CAKHelper.CAK_NAME + "=" + Long.toHexString(cakId) + ";docId=" + document.getDocumentId() + ";pageId=" + page.getPageId());

		//	get image resource hash from page 
		
		String pageImageHash = page.getEnImageResourceHash();
		ArrayList<EvernoteMedia> enMediaList = new ArrayList<EvernoteMedia>();
		EvernoteMedia enMedia = new EvernoteMedia(pageImageHash, "image/png");
		context.put("enMediaList", enMediaList);
		
//		context.put("en_media_hash", value);
//		context.put("en_media_type", "image/png");

		Template t = ve.getTemplate("evernoteTemplate.vm");
		StringWriter writer = new StringWriter();
		t.merge(context, writer);
		
		String enml = writer.toString();
	}

}

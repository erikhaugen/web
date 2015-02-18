/**
 * Created:  Dec 23, 2014 11:08:28 AM
 */
package org.kfm.camel.enml;

import java.io.StringWriter;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.PropertyInject;
import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.kfm.camel.entity.UploadTransaction;
import org.kfm.camel.message.MessageHeader;
import org.kfm.camel.util.CAKHelper;
import org.kfm.jpa.Document;
import org.kfm.jpa.Page;
import org.kfm.jpa.Session;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class ENMLProcessor implements Processor {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	@PropertyInject("player.url.prefix")
	private String playerUrlPrefix;
	
	//	PLAYER_LAUNCH_URL_HREF
	//	EVERNOTE_MEDIA_HASH_AND_TYPE
	//	LIVESCRIBE_HOME_URL_HREF
	//	EVERNOTE_MEDIA_LOGO_INACTIVE_FROST_RESOURCE_HASH_AND_TYPE
	//	LIVESCRIBE_HOME_URL_HREF_PREPEND
	//	EVERNOTE_MEDIA_UI_SETTING_RESOURCE_HASH_AND_TYPE
	//	PAGELIST_MODIFY
	//	EVERNOTE_LOGO_INACTIVE_FROST_RESOURCE_HASH
	//	EVERNOTE_LOGO_INACTIVE_FROST_RESOURCE_TYPE
	//	EVERNOTE_UI_SETTING_RESOURCE_HASH
	//	EVERNOTE_UI_SETTING_RESOURCE_TYPE
	//	EVERNOTE_MEDIA_UI_SETTING_RESOURCE_HASH_AND_TYPE
	//	LIVESCRIBE_HOME_URL_HREF_PREPEND
	//	PAGELIST_MODIFY_EVERNOTE_PAGE_NOTE_URL_HREF
	//	PAGELIST_MODIFY_PAGE_LABEL_TEXT
	//	AUDIOLIST_MODIFY
	//	AUDIOLIST_MODIFY_EVERNOTE_AUDIO_NOTE_URL_HREF
	//	AUDIOLIST_MODIFY_AUDIO_LABEL_TEXT
	
	/**
	 * <p></p>
	 * 
	 */
	public ENMLProcessor() {
	}

	/* (non-Javadoc)
	 * @see org.apache.camel.Processor#process(org.apache.camel.Exchange)
	 */
	@Override
	public void process(Exchange exchange) throws Exception {

		String method = "process()";

		Long enUserId = exchange.getIn().getHeader(MessageHeader.EN_USER_ID.getHeader(), Long.class);
		
		//	Needs:
		//	- Page or Session
		//	- Document
		//	- ContentAccess
		//	- EN User ID
		
		UploadTransaction upTx = (UploadTransaction)exchange.getIn().getBody();
		
		Document document = null;
		
		VelocityEngine ve = new VelocityEngine();
		ve.setProperty(VelocityEngine.RUNTIME_LOG_LOGSYSTEM, this);
		ve.init();
		
		VelocityContext context = new VelocityContext();
		context.put("livescribe_home_url_href", "");
		
		//	TODO:  Pass in either a Session or Page object to create the image.
		
		if (obj instanceof Session) {
			Session session = (Session)obj;
			document = session.getDocument();
			context.put("player_launch_url_href", playerUrlPrefix + "/int/player/notesdesktop.htm?" + CAKHelper.CAK_NAME + "=" + Long.toHexString(cakId) + ";docId=" + document.getDocumentId() + ";sessionId=" + session.getSessionId());
			
			//	get session audio from DB
			//	get audio resource hash from 'audio' table.
			
			context.put("en_media_hash", value);		//	@hash
			context.put("en_media_type", "audio/m4a");	//	@type
			
			//	get logo inactive frost resource hash from 'session'
			context.put("", value);			//	@hash
			context.put("", "image/gif");	//	@type
			
			//	get UI Setting image resource hash
			context.put("", value);			//	@hash
			context.put("", "image/png");	//	@type

			//	get livescribe home href
			context.put("livescribe_home_url_href", playerUrlPrefix);
			
			//	get livescribe home href prepend
			context.put("livescribe_home_href_prepend", playerUrlPrefix);
			
		} else if (obj instanceof Page) {
			Page page = (Page)obj;
			document = page.getDocument();
			context.put("player_launch_url_href", playerUrlPrefix + "/int/player/notesdesktop.htm?" + CAKHelper.CAK_NAME + "=" + Long.toHexString(cakId) + ";docId=" + document.getDocumentId() + ";pageId=" + page.getPageId());

			//	get image resource hash from page 
			
			context.put("en_media_hash", value);
			context.put("en_media_type", "image/png");

		} else {
			
		}
		
		Template t = ve.getTemplate("evernoteTemplate.vm");
		StringWriter writer = new StringWriter();
		t.merge(context, writer);
		
		String enml = writer.toString();
	}

}

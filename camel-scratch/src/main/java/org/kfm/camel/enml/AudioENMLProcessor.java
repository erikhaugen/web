/**
 * Created:  Dec 23, 2014 4:16:01 PM
 */
package org.kfm.camel.enml;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.PropertyInject;
import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.kfm.camel.entity.UploadTransaction;
import org.kfm.camel.entity.evernote.EvernoteMedia;
import org.kfm.camel.message.MessageHeader;
import org.kfm.camel.util.CAKHelper;
import org.kfm.jpa.Audio;
import org.kfm.jpa.Document;
import org.kfm.jpa.Session;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class AudioENMLProcessor implements Processor {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	@PropertyInject("player.url.prefix")
	private String playerUrlPrefix;

	/**
	 * <p></p>
	 * 
	 */
	public AudioENMLProcessor() {
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
		
//		UploadTransaction upTx = (UploadTransaction)exchange.getIn().getBody();
		Session session = exchange.getIn().getBody(Session.class);
		Document document = session.getDocument();
		
		VelocityEngine ve = new VelocityEngine();
		ve.setProperty(VelocityEngine.RUNTIME_LOG_LOGSYSTEM, this);
		ve.init();
		
		VelocityContext context = new VelocityContext();
		context.put("livescribe_home_url_href", "");

		context.put("player_launch_url_href", playerUrlPrefix + "/int/player/notesdesktop.htm?" + CAKHelper.CAK_NAME + "=" + Long.toHexString(cakId) + ";docId=" + document.getDocumentId() + ";sessionId=" + session.getSessionId());
		
		//	get session 'audio' from DB
		//	get audio resource hash from 'audio' table.
		
		ArrayList<EvernoteMedia> enMediaList = new ArrayList<EvernoteMedia>();
		List<Audio> audioList = session.getAudios();
		for (Audio audio : audioList) {
			String audioResourceHash = audio.getEnAudioResourceHash();
			EvernoteMedia enMedia = new EvernoteMedia(audioResourceHash, "audio/m4a");
			enMediaList.add(enMedia);
		}
		context.put("enMediaList", enMediaList);
//		context.put("en_media_hash", value);		//	@hash
//		context.put("en_media_type", "audio/m4a");	//	@type
		
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

		Template t = ve.getTemplate("evernoteTemplate.vm");
		StringWriter writer = new StringWriter();
		t.merge(context, writer);
		
		String enml = writer.toString();
	}

}

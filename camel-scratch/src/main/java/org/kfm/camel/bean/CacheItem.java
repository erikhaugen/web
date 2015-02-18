/**
 * Created:  Nov 21, 2014 2:42:02 PM
 */
package org.kfm.camel.bean;

import java.util.Calendar;

import org.apache.log4j.Logger;

import com.evernote.edam.notestore.NoteStore;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class CacheItem {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	private Calendar entryTime;
	private NoteStore.Client client;
	
	/**
	 * <p></p>
	 * 
	 */
	public CacheItem() {
		entryTime = Calendar.getInstance();
	}

	/**
	 * <p></p>
	 * 
	 * @return the entryTime
	 */
	public Calendar getEntryTime() {
		return entryTime;
	}

	/**
	 * <p></p>
	 * 
	 * @return the client
	 */
	public NoteStore.Client getClient() {
		return client;
	}

	/**
	 * <p></p>
	 * 
	 * @param client the client to set
	 */
	public void setClient(NoteStore.Client client) {
		this.client = client;
	}

}

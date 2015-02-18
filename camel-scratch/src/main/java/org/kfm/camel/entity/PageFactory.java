/**
 * Created:  Dec 22, 2014 12:42:34 PM
 */
package org.kfm.camel.entity;

import java.math.BigInteger;

import org.apache.log4j.Logger;
import org.kfm.jpa.Document;
import org.kfm.jpa.Page;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class PageFactory {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	public static Page create(Document document, int pageIndex) {
		
		Page page = new Page();
		page.setDocument(document);
		page.setStartTime(BigInteger.valueOf(0L));
		page.setEndTime(BigInteger.valueOf(0L));
		page.setPageIndex(pageIndex);
		page.setUpdateCounter(BigInteger.valueOf(0L));
		return page;
	}
}

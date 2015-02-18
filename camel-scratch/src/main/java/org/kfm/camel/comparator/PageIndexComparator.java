/**
 * Created:  Dec 18, 2014 5:41:05 PM
 */
package org.kfm.camel.comparator;

import java.util.Comparator;

import org.apache.log4j.Logger;
import org.kfm.jpa.Page;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class PageIndexComparator implements Comparator<Page> {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * <p></p>
	 * 
	 */
	public PageIndexComparator() {
	}

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Page p0, Page p1) {
		
		if (p0.getPageIndex() < p1.getPageIndex()) {
			return -1;
		} else if (p0.getPageIndex() < p1.getPageIndex()) {
			return 0;
		} else {
			return 1;
		}
	}

}

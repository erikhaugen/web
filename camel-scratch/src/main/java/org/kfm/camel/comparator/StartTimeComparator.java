/**
 * Created:  Nov 23, 2014 4:10:48 PM
 */
package org.kfm.camel.comparator;

import java.util.Comparator;

import org.apache.log4j.Logger;
import org.kfm.camel.entity.TimeBasedDTO;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class StartTimeComparator implements Comparator<TimeBasedDTO> {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * <p></p>
	 * 
	 */
	public StartTimeComparator() {
	}

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(TimeBasedDTO tbd0, TimeBasedDTO tbd1) {
		
		String method = "compare()";
		
		long st0 = tbd0.getStartTime();
		long st1 = tbd1.getStartTime();
		long diff = st1 - st0;
		if (diff < 0) {
			return -1;
		} else if (diff == 0) {
			return 0;
		} else {
			return 1;
		}
	}
}

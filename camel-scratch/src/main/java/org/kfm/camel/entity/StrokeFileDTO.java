/**
 * Created:  Mar 12, 2014 5:07:20 PM
 */
package org.kfm.camel.entity;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
@XStreamAlias("json-stroke-file")
public class StrokeFileDTO extends FileDTO {

	/**
	 * <p></p>
	 * 
	 */
	public StrokeFileDTO() {
		super();
		this.mimeType = "application/xml";
	}
}

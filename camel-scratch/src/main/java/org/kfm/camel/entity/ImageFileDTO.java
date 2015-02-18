/**
 * Created:  Mar 12, 2014 5:12:28 PM
 */
package org.kfm.camel.entity;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
@XStreamAlias("json-image-file")
public class ImageFileDTO extends FileDTO {

	/**
	 * <p></p>
	 * 
	 */
	public ImageFileDTO() {
		super();
		this.mimeType = "image/png";
	}
}

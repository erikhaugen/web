/*
 * Created:  Oct 24, 2011
 *      By:  kmurdoff
 */
/**
 * 
 */
package com.livescribe.aws.tokensvc.dto;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
@XStreamAlias("device")
public class RegisteredDeviceDTO {

	@XStreamAlias("penName")
	@XStreamAsAttribute
	private String penName;
	
	@XStreamAlias("type")
	@XStreamAsAttribute
	private String deviceType;
	
	@XStreamAlias("lang")
	@XStreamAsAttribute
	private String lang;
	
	@XStreamAlias("penSerial")
	@XStreamAsAttribute
	private String partialSerialNumber;
	
	@XStreamAlias("time")
	@XStreamAsAttribute
	private Long time;
	 
	/**
	 * <p></p>
	 * 
	 */
	public RegisteredDeviceDTO() {
	}

	/**
	 * <p></p>
	 * 
	 * @param name
	 * @param type
	 * @param lang
	 * @param serial
	 * @param time
	 */
	public RegisteredDeviceDTO(String name, String type, String lang, String serial, Long time) {
		
		this.penName = name;
		this.deviceType = type;
		this.lang = lang;
		this.partialSerialNumber = serial;
		this.time = time;
	}
}

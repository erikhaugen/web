/**
 * Created:  Dec 7, 2014 4:05:43 PM
 */
package org.kfm.camel.converter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.apache.camel.Converter;
import org.apache.log4j.Logger;

import com.livescribe.datatype.basic.ValueType;
import com.livescribe.web.lssettingsservice.client.Setting;
import com.thoughtworks.xstream.XStream;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
@Converter
public class SettingConverter {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * <p></p>
	 * 
	 */
	public SettingConverter() {
	}

	@Converter
	public Setting fromXmlString(String xml) {
		
		logger.debug("fromString() - Converting XML String to Setting ...");

		XStream xstream = new XStream();
		xstream.autodetectAnnotations(true);
		Setting setting = (Setting)xstream.fromXML(xml);
		String settingName = setting.getKey();
//		String settingValue = setting.getValue();
		String settingMeta = setting.getMeta();
		
		return setting;
	}
	
	@Converter
	public String toXmlString(Setting setting) {
		
		XStream xstream = new XStream();
		xstream.processAnnotations(Setting.class);
		String xml = xstream.toXML(setting);
		return xml;
	}
	
	@Converter
	public Setting fromInputStream(InputStream is) {
		
		logger.debug("fromInputStream() - Converting XML InputStream to Setting ...");
//		logger.debug("fromInputStream(); - Number of bytes available: " + is.available());
		
		Setting setting = null;
		try {
			XStream xstream = new XStream();
//			xstream.autodetectAnnotations(true);
			xstream.processAnnotations(Setting.class);
			Object obj = xstream.fromXML(is);
			if (obj instanceof InputStream) {
				setting = (Setting)obj;
				if (setting != null) {
					String settingName = setting.getKey();
					String settingValue = (String)setting.getValue();
					String settingMeta = setting.getMeta();
					String type = setting.getType();
					logger.debug("fromInputStream() - name: " + settingName);
					logger.debug("fromInputStream() - meta: " + settingMeta);
					logger.debug("fromInputStream() - value: " + settingValue);
					logger.debug("fromInputStream() - type: " + type);
				}
			} else if (obj instanceof Setting) {
				setting = (Setting)obj;
				if (setting != null) {
					String settingName = setting.getKey();
					String settingValue = (String)setting.getValue();
					String settingMeta = setting.getMeta();
					String type = setting.getType();
					logger.debug("fromInputStream() - name: " + settingName);
					logger.debug("fromInputStream() - meta: " + settingMeta);
					logger.debug("fromInputStream() - value: " + settingValue);
					logger.debug("fromInputStream() - type: " + type);
				}
			} else {
				logger.error("fromInputStream() - Found '" + obj.getClass().getCanonicalName() + "' instead of Setting.");
			}
		} catch (Exception e) {
			logger.error("fromInputStream() - An Exception was thrown:  " + e.getClass().getCanonicalName());
			e.printStackTrace();
		}
		
		logger.debug("fromInputStream() - Done.");
		
		return setting;
	}
	
	@Converter
	public InputStream toInputStream(Setting setting) {
		
		XStream xstream = new XStream();
		xstream.processAnnotations(Setting.class);
		String xml = xstream.toXML(setting);
		InputStream is = null;
		try {
			is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return is;
	}
}

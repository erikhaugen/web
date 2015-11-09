/**
 * Created:  Mar 28, 2014 3:39:36 PM
 */
package com.livescribe.scratch.velocity;

import java.io.IOException;
import java.io.StringWriter;

import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.exception.VelocityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.velocity.VelocityEngineFactoryBean;

import com.livescribe.framework.lsconfiguration.AppProperties;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class EvernoteProcessorImpl implements EvernoteProcessor {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	private static final String PROP_EVERNOTE_TEMPLATE = "velocity.template.evernote";
	
	@Autowired
	private AppProperties appProperties;
	
	@Autowired
	private VelocityEngineFactoryBean velocityEngine;
	
	/**
	 * <p></p>
	 * 
	 */
	public EvernoteProcessorImpl() {
		
//		Velocity.init();
	}

	public StringWriter merge(Context context) throws VelocityException, IOException {
		
		String templateName = appProperties.getProperty(PROP_EVERNOTE_TEMPLATE);
		
		VelocityEngine ve = velocityEngine.createVelocityEngine();
		Template template = ve.getTemplate(templateName);
		
		StringWriter writer = new StringWriter();
		template.merge(context, writer);
		
		return writer;
	}
}

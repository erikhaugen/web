/**
 * Created:  Jul 27, 2013 9:39:59 PM
 */
package com.livescribe;

import java.io.IOException;
import java.io.StringWriter;

import org.apache.log4j.xml.DOMConfigurator;
import org.apache.velocity.exception.VelocityException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.livescribe.scratch.velocity.EvernoteProcessor;
import com.livescribe.scratch.velocity.mock.MockVelocityContext;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class App {

	private static final ApplicationContext ac = new ClassPathXmlApplicationContext("application-context.xml");
	
	/**
	 * <p></p>
	 * 
	 */
	public App() {
	}

	public static void main(String[] args) throws VelocityException, IOException {
		
		DOMConfigurator.configureAndWatch("log4j.xml");
		
		MockVelocityContext context = new MockVelocityContext();
		EvernoteProcessor processor = (EvernoteProcessor)ac.getBean("evernoteProcessor");
		StringWriter writer = processor.merge(context);
		
		System.out.println(writer.toString());
	}
}

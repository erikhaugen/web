package com.livescribe.web.utils.spring.mvc;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.springframework.web.servlet.View;

/**
 * To use this view,
 * 
 * - define these beans in your spring context
 * <bean id="beanNameViewResolver" class="org.springframework.web.servlet.view.BeanNameViewResolver">
 *	 <property name="order"><value>1</value></property> 
 * </bean>
 *  <bean id="xmlView" class="com.livescribe.web.utils.spring.mvc.XmlView"/>
 *  
 *  - From a controller, 
 *  ...........
 *  Document xomDoc = myXMLDocument; 
 *	
 *  .......................
 *	model.put("xml", xomDoc);
 *	return new ModelAndView("xmlView", model);
 *
 * You may need to adjust the value of the order property based on what
 * other view resolvers are being used. Please refer to the
 * @author smukker
 *
 */
public class XmlView implements View {

	@Override
	public String getContentType() {
		return "text/xml";
	}

	@Override
	public void render(Map<String, ?> model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		// Write the XML document to the response
    	XMLWriter xmlWriter = new XMLWriter(response.getWriter(), new OutputFormat("  ", true, "utf-8"));
		xmlWriter.write((Document) model.get("xml"));
	}

}

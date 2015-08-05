/**
 * Created:  Feb 3, 2014 2:44:22 PM
 */
package com.livescribe.web.tools.webteamtool.sax;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import net.opticode.xsdtools.om.Element;
import net.opticode.xsdtools.om.Elements;
import net.opticode.xsdtools.om.Enum;
import net.opticode.xsdtools.parser.XSDParser;

import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.tools.generic.DateTool;
import org.dom4j.DocumentException;
import org.xml.sax.SAXException;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class Generator {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	private String xsdFile = "src/main/resources/xml/lsinkml.xsd";
	
    private String handlerTemplateFile = "src/main/resources/templates/HandlerTemplate.vm";
    private String elementTemplateFile = "src/main/resources/templates/ElementTemplate.vm";
    private String enumTemplateFile = "src/main/resources/templates/EnumTemplate.vm";

    private boolean enabled = true;
    
	/**
	 * <p></p>
	 * 
	 */
	public Generator() {
		Velocity.init();
	}

	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, DocumentException {
		
		Generator generator = new Generator();
		String schema = args[0];
		String pkg = args[1];
//		boolean valid = generator.validate(schema);
		boolean valid = true;
		if (valid) {
			System.out.println("The '" + schema + "' is VALID.");
			FileInputStream fis = new FileInputStream(schema);
			generator.generate(fis, "target/generated-sources", pkg);
			fis.close();
		} else {
			System.out.println("The '" + schema + "' is INVALID.");
		}
	}
	
	/**
	 * <p></p>
	 * 
	 * @param schema
	 * @param pkg
	 * 
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws DocumentException 
	 */
	public void generate(InputStream schema, String outputDirectory, String pkg) throws ParserConfigurationException, SAXException, IOException, DocumentException {
		
        Template handlerTemplate = Velocity.getTemplate(handlerTemplateFile);
        Template elementTemplate = Velocity.getTemplate(elementTemplateFile);
        Template enumTemplate = Velocity.getTemplate(enumTemplateFile);
        
        XSDParser xsdp = new XSDParser();
        Elements elements = xsdp.parse(schema);
        ArrayList<Enum> enums = xsdp.getEnums();
        
        System.out.println("Parsed " + elements.size() + " elements.");
        
//        VelocityContext context = new VelocityContext();
//        context.put("elements", elements);
//        context.put("package", pkg);
        
        //	Construct the final directory path.
        StringBuilder pkgDirBuilder = new StringBuilder();
        String pkgDir = pkg.replace(".", "/");
        pkgDirBuilder.append(outputDirectory).append("/").append(pkgDir).append("/");
if (enabled) {        
        //	Remove any old versions from output directory.
        File pkgFile = new File(pkgDirBuilder.toString());
        pkgFile.delete();
        
        //	Create the directory structure.
        pkgFile.mkdirs();
        
        System.out.println("Generating classes to:  " + pkgDirBuilder);
        
        for (int i = 0; i < elements.size(); i++) {
        	
        	Element element = elements.get(i);
        	
//        	System.out.print(element.getName());
        	
        	if ((i == 0) || (element.hasChildren()) || (element.hasAttributes())) {
        		
        		VelocityContext context = new VelocityContext();
	        	context.put("elements", elements);
	            context.put("element", element);
	            context.put("package", pkg);
	            DateTool dateTool = new DateTool();
	            context.put("date", dateTool);
	            Calendar now = Calendar.getInstance(TimeZone.getTimeZone("PST"));
	            context.put("calendar", now);
	            
	            //	Write the 'handler' class.
	        	String fullClassPath = pkgDirBuilder.toString() + element.getClassName() + "Handler.java";
	        	FileWriter fw = new FileWriter(fullClassPath);
	        	
	        	//	Write the 'type' class.
	        	String typesDirStr = pkgDirBuilder.toString() + "/types/";
	        	String elementClassPath = typesDirStr + element.getClassName() + ".java";
	        	File typesDir = new File(typesDirStr);
	        	typesDir.mkdirs();
	        	FileWriter elemfw = new FileWriter(elementClassPath);
	        	
	            handlerTemplate.merge(context, fw);
	            elementTemplate.merge(context, elemfw);
	            
	            fw.flush();
	            elemfw.flush();
	            
	            fw.close();
	            elemfw.close();
	            
        	} else {
//        		System.out.println("");
        	}
        	
        }
        
        System.out.println(enums.size());
        
        for (int k = 0; k < enums.size(); k++) {
        	Enum enumType = enums.get(k);
        	
        	VelocityContext context = new VelocityContext();
        	context.put("enum", enumType);
            context.put("package", pkg);
        	
        	String fullClassPath = pkgDirBuilder.toString() + enumType.getClassName() + ".java";
        	FileWriter fw = new FileWriter(fullClassPath);
        	
        	System.out.println("Writing '" + fullClassPath + "' ...");
        	
        	enumTemplate.merge(context, fw);
        	
        	fw.flush();
        	fw.close();
        }
}
//        SchemaParser schemaParser = new SchemaParser();
//        
//        SAXParserFactory factory = SAXParserFactory.newInstance();
//		SAXParser parser = factory.newSAXParser();
//		XMLReader xmlReader = parser.getXMLReader();
//		FileInputStream fis = new FileInputStream (schema);
//		InputSource input = new InputSource(fis);
//		xmlReader.parse(input);
		
	}
	
	/**
	 * <p></p>
	 * 
	 * @param filename
	 * 
	 * @return
	 * 
	 * @throws SAXException
	 * @throws IOException
	 */
	public boolean validate(String filename) throws SAXException, IOException {
		
		SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		URL url = new URL("http://www.w3.org/2001/XMLSchema.xsd");
		Schema schema = factory.newSchema(url);
		
		Validator validator = schema.newValidator();
		Source source = new StreamSource(filename);
		
		try {
			validator.validate(source);
			return true;
		} catch (SAXException saxe) {
			String msg = "SAXException thrown";
		}
		
		return false;
	}
}

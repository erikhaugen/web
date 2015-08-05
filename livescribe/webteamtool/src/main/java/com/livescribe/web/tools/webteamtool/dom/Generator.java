/**
 * Created:  Feb 6, 2014 2:20:44 PM
 */
package com.livescribe.web.tools.webteamtool.dom;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import net.opticode.xsdtools.om.Element;

import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.app.Velocity;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.XPath;
import org.dom4j.datatype.DatatypeDocumentFactory;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.DefaultElement;
import org.xml.sax.SAXException;

import com.livescribe.web.tools.webteamtool.util.Utils;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class Generator {

	private Logger logger = Logger.getLogger(this.getClass().getName());

    private String handlerTemplateFile = "src/main/resources/templates/HandlerTemplate.vm";
    private String elementTemplateFile = "src/main/resources/templates/ElementTemplate.vm";
    private String enumTemplateFile = "src/main/resources/templates/EnumTemplate.vm";

    HashMap<String, Element> mappedElements = new HashMap<String, Element>();
    
    /**
	 * <p></p>
	 * 
	 */
	public Generator() {
		Velocity.init();
	}

	/**
	 * <p></p>
	 * 
	 * @param args
	 * @throws DocumentException 
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, DocumentException {

		String method = "main()";

		Generator generator = new Generator();
		String schema = args[0];
		String pkg = args[1];
		FileInputStream fis = new FileInputStream(schema);
		generator.generate(fis, "target/generated-sources", pkg);
		fis.close();
	}

	public void generate(InputStream schema, String outputDirectory, String pkg) throws ParserConfigurationException, SAXException, IOException, DocumentException {
		
        Template handlerTemplate = Velocity.getTemplate(handlerTemplateFile);
        Template elementTemplate = Velocity.getTemplate(elementTemplateFile);
        Template enumTemplate = Velocity.getTemplate(enumTemplateFile);
        
		SAXReader saxReader = new SAXReader();
		saxReader.setDocumentFactory(DatatypeDocumentFactory.getInstance());
		Document xsd = saxReader.read(schema);
		
        //	Construct the final directory path.
        StringBuilder pkgDirBuilder = new StringBuilder();
        String pkgDir = pkg.replace(".", "/");
        pkgDirBuilder.append(outputDirectory).append("/").append(pkgDir).append("/");
        
        //	Create the XPath selector.
//        XPath namedElementSelector = DocumentHelper.createXPath("xsd:schema/xsd:complexType//xsd:element[@name]");
        XPath namedElementSelector = DocumentHelper.createXPath("xsd:schema/xsd:complexType[@name]");
        List<DefaultElement> xsdElements = (List<DefaultElement>)namedElementSelector.selectNodes(xsd);

//        List xsd.selectNodes("xsd:complexType");
        System.out.println("Number of types found:  " + xsdElements.size() + "\n");
        
        for (DefaultElement defElem : xsdElements) {
        	
        	Attribute nameAttr = defElem.attribute("name");
        	Attribute typeAttr = defElem.attribute("type");
        	
        	String name = Utils.capitalizeName(nameAttr.getValue());
        	String type = null;
        	
        	if (typeAttr == null) {
        		type = "< null >";
        	} else {
        		type = Utils.parseReference(typeAttr.getValue());
        	}
        	System.out.println(name + " - " + type);
        }
	}        
}

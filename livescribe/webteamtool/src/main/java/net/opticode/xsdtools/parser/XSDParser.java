package net.opticode.xsdtools.parser;

// JDK Classes
import java.util.*;
import java.io.*;

// Xerces Classes
import org.xml.sax.*;
import org.apache.commons.lang.WordUtils;
import org.apache.xerces.parsers.*;
import org.xml.sax.helpers.DefaultHandler;

// Local Classes
import net.opticode.xsdtools.om.*;
import net.opticode.xsdtools.om.Enum;

public class XSDParser extends DefaultHandler {
	
	private static final String ATTR_ID						= "id";
	private static final String TYPE_DECIMAL				= "decimal";
	private static final String TYPE_INT					= "int";
	private static final String TYPE_STRING					= "string";
	private static final String TYPE_ANY_URI				= "anyURI";
	private static final String ENUM_STD_CHANNEL_NAME		= "standardChannelName.type";
	private static final String ENUM_STD_CHANNEL_PROP_NAME	= "standardChannelPropertyName.type";
	private static final String ENUM_STD_BRUSH_PROP_NAME	= "standardBrushPropertyName.type";
	private static final String ENUM_STD_UNITS				= "standardUnits.type";
	private static final String UNBOUNDED					= "unbounded";
	
    CharArrayWriter text = new CharArrayWriter();
    SAXParser parser = new SAXParser();
    private ArrayList<ComplexType> typeList = new ArrayList<ComplexType>();
    private ArrayList<Element> elementList = new ArrayList<Element>();
    Elements elements = new Elements();
    ArrayList<Enum> enums = new ArrayList<Enum>();
    
    //	Stack of 'complexType' and 'element' Elements.
    Stack<ComplexType> typeStack = new Stack<ComplexType>();
    Stack<Element> elementStack = new Stack<Element>();
    Stack<Enum> enumStack = new Stack<Enum>();
    Stack<String> attrStack = new Stack<String>();
    
    public String getText() {
        return text.toString().trim();
    }

    public Elements parse (InputStream is) throws SAXException, IOException {
    	
        System.out.println("Stack size:  " + elementStack.size());

        parser.setContentHandler (this);
        parser.parse( new InputSource (is));
        
        System.out.println("NEW Stack size:  " + elementStack.size());

        return elements;
    }

    public void startDocument() throws SAXException {
    }

    public void endDocument() throws SAXException {
    }

    public void startElement(java.lang.String uri, java.lang.String localName, java.lang.String qName, Attributes attributes) throws SAXException {
    	
        text.reset();

//        System.out.println("Stack size:  " + stack.size());
        
        if (qName.equals("xsd:complexType")) {
            String typeName = attributes.getValue("name");
            
            if ((typeName == null) || (typeName.isEmpty())) {
            	return;
            }
            
            //	String the '.type' text off the end of the name.
            int idx = typeName.indexOf(".");
            String name = typeName.substring(0, idx);
            System.out.println("name:  " + name);
            
            //	Create a 'camel-case' version of the name.
            String capitalized = capitalizeName(name);
            
            //	Create a new Element and add it to the list of Elements.
            Element element = new Element(elements, name, capitalized);
            
            //	
//            elementList.add(element);
            
            elementStack.push(element);
            
        } else if (qName.equals("xsd:element")) {
        	
        	String name = attributes.getValue("name");
        	String elemType = attributes.getValue("type");
    		String capitalized = capitalizeName(name);
    		
    		Element existingElement = null;
    		if (elemType != null) {
	            //	Strip the '.type' text off the end of the name.
	            int idx = elemType.indexOf(".");
	            String typeName = elemType.substring(0, idx);
	    		existingElement = elements.getElement(typeName);
    		}
			boolean isList = isList(attributes);
    		Element element = null;
    		if (existingElement != null) {
    			element = existingElement;
    		} else {
    			element = new Element(elements, name, capitalized);
    		}

    		//	If nothing is on the stack, this is an element outside of a
            //	'complexType'.
    		if (elementStack.isEmpty()) {
	            //	If this element can occur more than once, make it a List.
	            element.setIsList(isList);
	            
	            System.out.println("element:  " + capitalized + (element.isList()? "  < List >":""));
	    		
	            elementStack.push(element);
    		}
            else {
            	//	If the element stack is not empty, add this element as 
            	//	a 'child' element.
            	System.out.println("    element:  " + capitalized);
            	elementStack.peek().addElement(capitalized, new Boolean(isList));
            }
            
        } else if (qName.equals("xsd:attribute")) {
            
        	if (!elementStack.empty()) {
            	String name = null;
            	
            	//	Handle references (e.g.  ref="xml:id")
            	String ref = attributes.getValue("ref");
            	if ((ref != null) && (!ref.isEmpty())) {
            		String[] parts = ref.split(":");
            		name = parts[1];
            	} else {
            		name = attributes.getValue("name");
            	}
            	
            	//	Convert some XML types to Java types.
            	String type = null;
            	if (ATTR_ID.equals(name)) {
            		type = "Integer";
            	} else {
	            	String typeAttr = attributes.getValue("type");
	            	type = parseReference(typeAttr);
	            	System.out.println("    att:  " + name + "    type:  " + type);
	            	if (TYPE_STRING.equals(type)) {
	            		type = "String";
	            	} else if (TYPE_ANY_URI.equals(type)) {
	            		type = "String";
	            	} else if (TYPE_DECIMAL.equals(type)) {
	            		type = "Double";
	            	} else if (TYPE_INT.equals(type)) {
	            		type = "Integer";
	            	} else if (type != null) {
	            		type = capitalizeName(type);
	            	}
            	}
            	
            	String className = capitalizeName(name);
                
            	elementStack.peek().addAttribute(name, className, type);
                attrStack.push(className);
            } else {
            	System.out.println("Element stack was unexpectedly empty.");
            }
        } else if (qName.equals("xsd:simpleType")) {
        	Enum enumType = new Enum();
        	String name = attributes.getValue("name");
        	if (name != null) {
        		String[] parts = name.split("\\.");
	        	String capitalizedName = capitalizeName(parts[0]);
	        	enumType.setClassName(capitalizedName);
	        	if (ENUM_STD_CHANNEL_NAME.equals(name)) {
	        		enumStack.push(enumType);
	        	} else if (ENUM_STD_CHANNEL_PROP_NAME.equals(name)) {
	        		enumStack.push(enumType);
	        	} else if (ENUM_STD_BRUSH_PROP_NAME.equals(name)) {
	        		enumStack.push(enumType);
	        	} else if (ENUM_STD_UNITS.equals(name)) {
	        		enumStack.push(enumType);
	        	}
        	} else {
        		//	Here we have found an unnamed <xsd:simpleType> attached
        		//	to an 'attribute', which translates to an 'enum'.
        		if (!attrStack.isEmpty()) {
        			String attrClassName = attrStack.peek();
        			enumType.setClassName(attrClassName);
        			System.out.println("Adding '" + attrClassName + "' as new Enum ...");
        			enumStack.push(enumType);
        		}
        	}
        } else if (qName.equals("xsd:enumeration")) {
        	String value = attributes.getValue("value");
        	String capitalizedValue = null;
        	if ((value != null) && (!value.isEmpty())) {
           		if (value.equals("+ve")) {
           			capitalizedValue = "VE_POSITIVE";
           		} else if (value.equals("-ve")) {
           			capitalizedValue = "VE_NEGATIVE";
        		} else {
	       			capitalizedValue = value.replace("1/", "PER_").toUpperCase();
	           		capitalizedValue = capitalizedValue.replace("/", "_PER_");
	           		capitalizedValue = capitalizedValue.replace("%", "PERCENT");
        		}
        	}
       		        	
        	if (!enumStack.isEmpty()) {
            	System.out.println("Adding '" + capitalizedValue + "' to '" + enumStack.peek().getClassName() + "' ...");
        		enumStack.peek().addValue(capitalizedValue, value);
        	}
        }
    }

	/**
	 * <p></p>
	 * 
	 * @param attributes
	 * @param element
	 */
	private boolean isList(Attributes attributes) {
		
		String maxOccurs = attributes.getValue("maxOccurs");
		if ((maxOccurs != null) && (!maxOccurs.isEmpty())) {
			if (UNBOUNDED.equals(maxOccurs)) {
				return true;
			}
		}
		return false;
	}

    public void endElement(java.lang.String uri, java.lang.String localName, java.lang.String qName) throws SAXException {
    	
        if (qName.equals("xsd:complexType")) {
        	Element element = elementStack.pop();
        	elements.add(element);
        } else if (qName.equals("xsd:attribute")) {
        	attrStack.pop();
        } else if (qName.equals("xsd:simpleType")) {
        	
        	if (!enumStack.isEmpty()) {
	        	Enum enumType = enumStack.pop();
	        	enums.add(enumType);
        	}
        }
    }

    public void characters(char[] ch, int start, int length) {
        text.write (ch,start,length);
    }
    
    private String capitalizeName(String name) {
    	
    	if (name == null) {
    		return "";
    	}
    	
    	String elementName = null;
    	if (name.contains("_")) {
    		String[] words = name.split("_");
    		StringBuilder builder = new StringBuilder();
    		for (String word : words) {
    			builder.append(WordUtils.capitalize(word));
    		}
    		elementName = builder.toString();
    	}
    	else {
    		elementName = WordUtils.capitalize(name);
    	}
    	return elementName;
    }
    
    /**
     * <p>Parses a reference string (e.g.  <code>ref="xml:id"</code>) and
     * returns the type name (e.g.  "id").</p>
     * 
     * @param reference
     * 
     * @return
     */
    private String parseReference(String reference) {
    	
    	if ((reference == null) || (reference.isEmpty())) {
    		return null;
    	}
    	
    	String typeName = null;
    	if (reference.contains(":")) {
    		String[] parts = reference.split(":");
    		typeName = parts[1];
    	}
    	
    	String name = null;
    	if (typeName.contains(".")) {
    		String[] pieces = typeName.split("\\.");
    		name = pieces[0];
    	} else {
    		name = typeName;
    	}
    	return name;
    }

    private ComplexType findTypeByName(String name) {
    	
    	for (ComplexType cplxType : typeList) {
    		String ctName = cplxType.getName();
    		if (ctName.equals(name)) {
    			return cplxType;
    		}
    	}
    	return null;
    }
    
	/**
	 * <p></p>
	 * 
	 * @return the enums
	 */
	public ArrayList<Enum> getEnums() {
		return enums;
	}

	/**
	 * <p></p>
	 * 
	 * @return the typeList
	 */
	public ArrayList<ComplexType> getTypeList() {
		return typeList;
	}
}

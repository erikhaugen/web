package net.opticode.xsdtools.om;

// JDK Classes
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class Element {
	
    private Elements elements;
    private String elementName;
    private String className;
    private List<String> childNames = new ArrayList<String>();
    private HashMap<String, Boolean> isChildAList = new HashMap<String, Boolean>();
    private List<Map<String, String>> attributes = new ArrayList<Map<String, String>>();
    private Boolean isList = new Boolean(false);
    
    /**
     * <p></p>
     * 
     * @param elements
     * @param elementName
     */
    public Element(Elements elements, String elementName, String className) {
    	
        this.elements = elements;
        this.elementName = elementName;
        this.className = className;
        
    	elements.add(this);
    }

    /**
     * <p>Adds the given attribute name and type to the list of attributes for
     * this <code>Element</code>.</p>
     * 
     * @param attribute
     */
    public void addAttribute (String attribute, String className, String type) {
    	
    	LinkedHashMap<String, String> attr = new LinkedHashMap<String, String>();
    	attr.put("name", attribute);
    	attr.put("className", className);
    	attr.put("type", type);
	    attributes.add(attr);
	}

	/**
     * <p>Adds the given element name to the list of child elements of
     * this <code>Element</code>.</p>
     * 
     * @param name
     */
    public void addElement(String name, Boolean isAList) {
        childNames.add(name);
        isChildAList.put(name, isAList);
    }

    /**
     * <p></p>
     * 
     * @return
     */
    public List<Map<String, String>> getAttributes() {
	    return attributes;
	}

	/**
	 * <p></p>
	 * 
	 * @return
	 */
	public List<Element> getChildren2() {
		
	    ArrayList<Element> result = new ArrayList<Element>();
	    
	    for (int i = 0; i < childNames.size(); i++) {
	        result.add(elements.getElement(childNames.get(i)));
	    }
	    return result;
	}

	/**
	 * <p></p>
	 * 
	 * @return
	 */
	public List<Element> getChildren() {
		
	    ArrayList<Element> result = new ArrayList<Element>();
	    
	    for (int i = 0; i < childNames.size(); i++) {
	    	String childName = childNames.get(i);
	    	Element element = elements.getElement(childName);
	    	if (element != null) {
		    	Boolean isList = isChildAList.get(childName);
		    	if (isList != null) {
		    		element.setIsList(isList);
		    	}
		        result.add(element);
	    	}
	    }
	    return result;
	}

	/**
	 * <p></p>
	 * 
	 * @return
	 */
	public List<String> getChildrenNames() {
	    return childNames;
	}

	/**
	 * <p></p>
	 * 
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * <p></p>
	 * 
	 * @return
	 */
	public String getElementName() {
        return elementName;
    }

	/**
	 * <p></p>
	 * 
	 * @return
	 */
	public boolean hasAttributes() {
		return (attributes.size() > 0);
	}
	
    /**
     * <p></p>
     * 
     * @return
     */
    public boolean hasChildren() {
	    return (childNames.size() > 0);
	}

	/**
	 * <p></p>
	 * 
	 * @param v
	 */
	public void setElementName (String v) {
        elementName = v;
    }

    /**
	 * <p></p>
	 * 
	 * @param className the className to set
	 */
	public void setClassName(String className) {
		this.className = className;
	}

	/**
	 * <p></p>
	 * 
	 * @return the isAList
	 */
	public boolean isList() {
		return isList;
	}

	/**
	 * <p></p>
	 * 
	 * @param isAList the isAList to set
	 */
	public void setIsList(Boolean isList) {
		this.isList = isList;
	}
}

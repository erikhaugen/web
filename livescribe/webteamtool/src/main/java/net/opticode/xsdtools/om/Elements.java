package net.opticode.xsdtools.om;

// JDK Classes
import java.util.*;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class Elements {
	
    private List<Element> elements = new ArrayList<Element>();
    private Map<String, Element> elementsNamed = new HashMap<String, Element>();
    private Element rootElement;
    
    public void add(Element element) {
    	
    	if (elements.isEmpty()) {
    		rootElement = element;
    	}
        elements.add (element);
        elementsNamed.put (element.getElementName(), element);
    }
    
    public Element getRootElement() {
//    	System.out.println("Returning " + rootElement.getClassName());
//        return rootElement;
    	return elements.get(0);
    }
    
    public Element getElement(String name) {
        return elementsNamed.get(name);
    }
    
    public int size() {
        return elements.size();
    }
    
    public Element get(int id) {
        return elements.get(id);
    }
}

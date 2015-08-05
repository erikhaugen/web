/**
 * Created:  Feb 6, 2014 3:15:16 PM
 */
package net.opticode.xsdtools.om;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class ComplexType {

	private String name;
	private String capitalizedName;
	private ArrayList<Element> elements = new ArrayList<Element>();
    private List<Map<String, String>> attributes = new ArrayList<Map<String, String>>();
	private boolean moreThanOne = false;
	
	/**
	 * <p></p>
	 * 
	 */
	public ComplexType(String capitalizedName, String name) {
		this.capitalizedName = capitalizedName;
		this.name = name;
	}

	/**
	 * <p></p>
	 * 
	 * @param element
	 */
	public void add(Element element) {
		this.elements.add(element);
	}
	
	public boolean hasChildren() {
		return elements.size() > 0;
	}
	
	/**
	 * <p></p>
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * <p></p>
	 * 
	 * @return the elements
	 */
	public ArrayList<Element> getElements() {
		return elements;
	}

	/**
	 * <p></p>
	 * 
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * <p></p>
	 * 
	 * @param elements the elements to set
	 */
	public void setElements(ArrayList<Element> elements) {
		this.elements = elements;
	}

	/**
	 * <p></p>
	 * 
	 * @return the capitalizedName
	 */
	public String getCapitalizedName() {
		return capitalizedName;
	}

	/**
	 * <p></p>
	 * 
	 * @param capitalizedName the capitalizedName to set
	 */
	public void setCapitalizedName(String capitalizedName) {
		this.capitalizedName = capitalizedName;
	}

	/**
	 * <p></p>
	 * 
	 * @return the moreThanOne
	 */
	public boolean isMoreThanOne() {
		return moreThanOne;
	}

	/**
	 * <p></p>
	 * 
	 * @param moreThanOne the moreThanOne to set
	 */
	public void setMoreThanOne(boolean moreThanOne) {
		this.moreThanOne = moreThanOne;
	}

	/**
	 * <p></p>
	 * 
	 * @return the attributes
	 */
	public List<Map<String, String>> getAttributes() {
		return attributes;
	}

	/**
	 * <p></p>
	 * 
	 * @param attributes the attributes to set
	 */
	public void setAttributes(List<Map<String, String>> attributes) {
		this.attributes = attributes;
	}

}

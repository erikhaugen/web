/**
 * Created:  Feb 4, 2014 9:25:08 PM
 */
package net.opticode.xsdtools.om;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class Enum {

	private String className;
	private ArrayList<LinkedHashMap<String, String>> values = new ArrayList<LinkedHashMap<String, String>>();

	/**
	 * <p></p>
	 * 
	 */
	public Enum() {
	}

	/**
	 * <p></p>
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		String method = "main()";

	}

	public void addValue(String valueName, String value) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("valueName", valueName);
		map.put("value", value);
		values.add(map);
	}
	
	/**
	 * <p></p>
	 * 
	 * @return the values
	 */
	public ArrayList<LinkedHashMap<String, String>> getValues() {
		return values;
	}

	/**
	 * <p></p>
	 * 
	 * @param values the values to set
	 */
	public void setValues(ArrayList<LinkedHashMap<String, String>> values) {
		this.values = values;
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
	 * @param className the className to set
	 */
	public void setClassName(String className) {
		this.className = className;
	}

}

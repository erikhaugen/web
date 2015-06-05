/**
 * Created by Trung Chau.
 * Date: 3/1/12
 * Time: 1:54 PM
 */
package com.livescribe.datatype.basic;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Vector;

/**
 * The ValueType Array. Each element is a ValueType value.
 * It's same as ValueArrayType, but it has unknown upfront size, and it ends with TerminatorType value.
 */
public class TermArrayType extends ValueType {
    private Vector values;

    public byte getType() {
        return Types.TERM_ARRAY;
    }

    /**
     * Initialize instance
     * @param values The list of elements. Each element is ValueType value.
     */
    public TermArrayType(Vector values) {
        this.values = values;
    }

    public void writeData(OutputStream os) throws IOException {
        Enumeration e = values.elements();
        while (e.hasMoreElements()) {
            ValueType value = (ValueType)e.nextElement();
            value.write(os);
        }
        TerminatorType terminator = new TerminatorType();
        terminator.write(os);
    }

    public void readData(InputStream is) throws IOException {
        values = new Vector();
        while (true)  {
            ValueType value = TypeFactory.read(is);
            if (value instanceof TerminatorType)
                break;
            values.addElement(value);
        }
    }

    /**
     * Get list of elements. Each element is ValueType.
     * The TerminateType value is not included in this list.
     * @return  list of elements.
     */
    public Vector getValue() {
        return values;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof TermArrayType))
            return false;

        Vector v = ((TermArrayType)obj).values;
        if (v.size() != values.size())
            return false;

        for (int i=0; i<v.size(); i++) {
            ValueType v1 = (ValueType)values.elementAt(i);
            ValueType v2 = (ValueType)v.elementAt(i);
            if (!v1.equals(v2))
                return false;
        }
        return true;
    }

    public String toString() {
        String tmp = "[";
        if (values != null) {
            for (int i=0; i<values.size(); i++) {
                ValueType value = (ValueType)values.elementAt(i);
                tmp += "{" + value.toString() + "} ";
            }
        }
        return tmp;
    }

    public void setValue(Object value) {
		if (value instanceof Vector) {
			this.values = (Vector)value;
		}
	}

}

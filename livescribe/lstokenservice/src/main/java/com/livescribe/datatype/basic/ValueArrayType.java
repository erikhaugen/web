/**
 * Created by Trung Chau.
 * Date: 3/1/12
 * Time: 10:32 AM
 */
package com.livescribe.datatype.basic;

import com.livescribe.util.ArrayTools;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Vector;

/**
 * The ValueType Array. Each element is a ValueType value.
 * This has known up front size.
 */
public class ValueArrayType extends ValueType {
    private Vector values;

    public byte getType() {
        return Types.VALUE_ARRAY;
    }

    /**
     * Initialize instance
     * @param values The list of elements. Each element is ValueType value.
     */
    public ValueArrayType(Vector values) {
        this.values = values;
    }

    public void writeData(OutputStream os) throws IOException {
        byte[] buf = new byte[2];
        ArrayTools.setShort(buf, 0, (short)values.size());
        os.write(buf);
        Enumeration e = values.elements();
        while (e.hasMoreElements()) {
            ValueType value = (ValueType)e.nextElement();
            value.write(os);
        }
    }

    public void readData(InputStream is) throws IOException {
        int type;
        byte[] buf = new byte[2];
        is.read(buf);
        int size = ArrayTools.getShort(buf, 0);
        values = new Vector();
        for (int i=0; i<size; i++) {
            ValueType value = TypeFactory.read(is);
            values.addElement(value);
        }
    }

    /**
     * Get list of elements. Each element is ValueType.
     * @return  list of elements.
     */
    public Vector getValue() {
        return values;
    }
    
    public void setValue(Object value) {
		if (value instanceof Vector) {
			this.values = (Vector)value;
		}
	}

    public boolean equals(Object obj) {
        if (!(obj instanceof ValueArrayType))
            return false;

        Vector v = ((ValueArrayType)obj).values;
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

}

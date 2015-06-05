/**
 * Created by Trung Chau.
 * Date: 3/1/12
 * Time: 11:58 AM
 */
package com.livescribe.datatype.basic;

import com.livescribe.util.ArrayTools;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Vector;

/**
 * The Data Array value
 */
public class DataArrayType extends ValueType {
    private Vector values;
    private byte type;

    public byte getType() {
        return Types.DATA_ARRAY;
    }

    public DataArrayType(Vector values, byte type) {
        this.values = values;
        this.type = type;
    }

    public void writeData(OutputStream os) throws IOException {
        byte[] buf = new byte[2];
        ArrayTools.setShort(buf, 0, (short) values.size());
        os.write(buf);
        os.write(type);
        Enumeration e = values.elements();
        Object tmp = TypeFactory.create(type);
        while (e.hasMoreElements()) {
            ValueType value = (ValueType)e.nextElement();
            if (!value.getClass().equals(tmp.getClass()))
                throw new IOException("Write invalid element type");
            value.writeData(os);
        }
    }

    public void readData(InputStream is) throws IOException {
        int type;
        byte[] buf = new byte[2];
        is.read(buf);
        int size = ArrayTools.getShort(buf, 0);
        type = is.read();
        values = new Vector();
        for (int i=0; i<size; i++) {
            ValueType value = TypeFactory.create(type);
            value.readData(is);
            values.addElement(value);
        }
    }

    public Vector getValue() {
        return values;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof DataArrayType))
            return false;

        Vector v = ((DataArrayType)obj).values;
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

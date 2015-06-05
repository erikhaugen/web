/**
 * Created by Trung Chau.
 * Date: 2/29/12
 * Time: 5:58 PM
 */
package com.livescribe.datatype.basic;

import com.livescribe.util.ArrayTools;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * The IntType value
 */
public class IntType extends ValueType {
    private int value;

    public byte getType() {
        return Types.INT;
    }

    public IntType(int value) {
        this.value = value;
    }

    public void writeData(OutputStream os) throws IOException {
        byte[] buf = new byte[4];
        ArrayTools.setInt(buf, 0, value);
        os.write(buf);
    }

    public void readData(InputStream is) throws IOException {
        byte[] buf = new byte[4];
        is.read(buf);
        value = ArrayTools.getInt(buf, 0);
    }

    public int getValue() {
        return value;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof IntType))
            return false;
        return value == ((IntType)(obj)).value;
    }

    public String toString() {
        return ""+value;
    }

	public void setValue(Object value) {
		if (value instanceof Number) {
			this.value = ((Number)value).intValue();
		} else if (value instanceof String) {
			this.value = Integer.parseInt((String)value);
		}
	}

}

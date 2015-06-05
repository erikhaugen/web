/**
 * Created by Trung Chau.
 * Date: 6/6/12
 * Time: 11:08 PM
 */
package com.livescribe.datatype.basic;

import com.livescribe.util.ArrayTools;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * FloatType value
 */
public class FloatType extends ValueType {
    private float value;

    public byte getType() {
        return Types.FLOAT;
    }

    public FloatType(float value) {
        this.value = value;
    }

    public void writeData(OutputStream os) throws IOException {
        byte[] buf = new byte[4];
        ArrayTools.setFloat(buf, 0, value);
        os.write(buf);
    }

    public void readData(InputStream is) throws IOException {
        byte[] buf = new byte[4];
        is.read(buf);
        value = ArrayTools.getFloat(buf, 0);
    }

    public float getValue() {
        return value;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof FloatType))
            return false;
        return value == ((FloatType)(obj)).value;
    }

    public String toString() {
        return ""+value;
    }

	@Override
	public void setValue(Object value) {
		if (value instanceof Number) {
			this.value = ((Number)value).floatValue();
		} else if (value instanceof String) {
			this.value = Float.parseFloat((String)value);
		}
	}

}

/**
 * Created by Trung Chau.
 * Date: 2/29/12
 * Time: 6:01 PM
 */
package com.livescribe.datatype.basic;

import com.livescribe.util.ArrayTools;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * DoubleType value
 */
public class DoubleType extends ValueType {
    private double value;

    public byte getType() {
        return Types.DOUBLE;
    }

    public DoubleType(double value) {
        this.value = value;
    }

    public void writeData(OutputStream os) throws IOException {
        byte[] buf = new byte[8];
        ArrayTools.setDouble(buf, 0, value);
        os.write(buf);
    }

    public void readData(InputStream is) throws IOException {
        byte[] buf = new byte[8];
        is.read(buf);
        value = ArrayTools.getDouble(buf, 0);
    }

    public double getValue() {
        return value;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof DoubleType))
            return false;
        return value == ((DoubleType)(obj)).value;
    }

    public String toString() {
        return ""+value;
    }

	@Override
	public void setValue(Object value) {
		if (value instanceof Number) {
			this.value = ((Number)value).doubleValue();
		} else if (value instanceof String) {
			this.value = Double.parseDouble((String)value);
		}
		
	}

}

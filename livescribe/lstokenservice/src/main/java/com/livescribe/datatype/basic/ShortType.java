/**
 * Created by Trung Chau.
 * Date: 2/29/12
 * Time: 5:55 PM
 */
package com.livescribe.datatype.basic;

import com.livescribe.util.ArrayTools;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * The ShortType value
 */
public class ShortType extends ValueType {
    private short value;

    public byte getType() {
        return Types.SHORT;
    }

    public ShortType(short value) {
        this.value = value;
    }

    public void writeData(OutputStream os) throws IOException {
        byte[] buf = new byte[2];
        ArrayTools.setShort(buf, 0, value);
        os.write(buf);
    }

    public void readData(InputStream is) throws IOException {
        byte[] buf = new byte[2];
        is.read(buf);
        value = ArrayTools.getShort(buf, 0);
    }

    public short getValue() {
        return value;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof ShortType))
            return false;
        return value == ((ShortType)(obj)).value;
    }

    public String toString() {
        return ""+value;
    }
    
    public void setValue(Object value) {
    		if (value instanceof Number) {
			this.value = ((Number)value).shortValue();
		} else if (value instanceof String) {
			this.value = Short.parseShort((String)value);
		}
	}

}

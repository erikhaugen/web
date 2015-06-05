/**
 * Created by Trung Chau.
 * Date: 2/29/12
 * Time: 5:59 PM
 */
package com.livescribe.datatype.basic;

import com.livescribe.util.ArrayTools;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * The LongType value
 */
public class LongType extends ValueType {
    private long value;

    public byte getType() {
        return Types.LONG;
    }

    public LongType(long value) {
        this.value = value;
    }

    public void writeData(OutputStream os) throws IOException {
        byte[] buf = new byte[8];
        ArrayTools.setLong(buf, 0, value);
        os.write(buf);
    }

    public void readData(InputStream is) throws IOException {
        byte[] buf = new byte[8];
        is.read(buf);
        value = ArrayTools.getLong(buf, 0);
    }

    public long getValue() {
        return value;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof LongType))
            return false;
        return value == ((LongType)(obj)).value;
    }

    public String toString() {
        return ""+value;
    }
    
    public void setValue(Object value) {
    		if (value instanceof Number) {
			this.value = ((Number)value).longValue();
		} else if (value instanceof String) {
			this.value = Long.parseLong((String)value);
		}
	}
}

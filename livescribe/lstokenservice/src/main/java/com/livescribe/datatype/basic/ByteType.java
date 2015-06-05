/**
 * Created by Trung Chau.
 * Date: 2/29/12
 * Time: 5:42 PM
 */
package com.livescribe.datatype.basic;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * The ByteType value
 */
public class ByteType extends ValueType {
    private byte value;

    public byte getType() {
        return Types.BYTE;
    }

    public ByteType(byte value) {
        this.value = value;
    }

    public void writeData(OutputStream os) throws IOException {
        os.write(value);
    }

    public void readData(InputStream is) throws IOException {
        value = (byte)is.read();
    }

    public byte getValue() {
        return value;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof ByteType))
            return false;
        return value == ((ByteType)(obj)).value;
    }

    public String toString() {
        return ""+value;
    }

	@Override
	public void setValue(Object value) {
		if (value instanceof Number) {
			this.value = ((Number)value).byteValue();
		} else if (value instanceof String) {
			this.value = Byte.parseByte((String)value);
		}
	}

}

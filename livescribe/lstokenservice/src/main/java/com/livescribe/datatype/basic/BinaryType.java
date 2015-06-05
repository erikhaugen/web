/**
 * Created by Trung Chau.
 * Date: 2/29/12
 * Time: 6:06 PM
 */
package com.livescribe.datatype.basic;

import com.livescribe.util.ArrayTools;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BinaryType extends ValueType {
    private byte[] value;

    public byte getType() {
        return Types.BINARY;
    }

    public BinaryType(byte[] value) {
        this.value = value;
    }

    public void writeData(OutputStream os) throws IOException {
        byte[] buf = new byte[4];
        ArrayTools.setInt(buf, 0, value.length);
        os.write(buf);
        os.write(value);
    }

    public void read(InputStream is) throws IOException {
        int type = is.read();
        if (type != Types.BINARY)
            throw new IOException("Invalid Type");
        readData(is);
    }

    public void readData(InputStream is) throws IOException {
        byte[] buf = new byte[4];
        is.read(buf);
        int size = ArrayTools.getInt(buf, 0);
        value = new byte[size];
        is.read(value);
    }

    public byte[] getValue() {
        return value;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof BinaryType))
            return false;

        byte[] bin = ((BinaryType)obj).value;
        if (value.length != bin.length)
            return false;

        for (int i=0; i<bin.length; i++) {
            if (value[i] != bin[i])
                return false;
        }
        return true;
    }

	@Override
	public void setValue(Object value) {
		if (value instanceof byte[]) {
			this.value = (byte[]) value;
		}
	}

}

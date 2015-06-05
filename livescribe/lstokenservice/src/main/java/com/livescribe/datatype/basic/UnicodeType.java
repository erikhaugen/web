/**
 * Created by Trung Chau.
 * Date: 2/29/12
 * Time: 6:02 PM
 */
package com.livescribe.datatype.basic;

import com.livescribe.util.ArrayTools;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * The UnicodeType value
 */
public class UnicodeType extends ValueType {
    private String value;

    public byte getType() {
        return Types.UNICODE;
    }

    public UnicodeType(String value) {
        this.value = value;
    }

    public void write(OutputStream os) throws IOException {
        os.write(Types.UNICODE);
        writeData(os);
    }

    public void writeData(OutputStream os) throws IOException {
        byte[] buf = new byte[2];
        ArrayTools.setShort(buf, 0, (short)(value.length()+1));
        os.write(buf);
        os.write(value.getBytes("UTF-8"));
        os.write(0);
    }

    public void read(InputStream is) throws IOException {
        int type = is.read();
        if (type != Types.UNICODE)
            throw new IOException("Invalid Type");
        readData(is);
    }

    public void readData(InputStream is) throws IOException {
        byte[] buf = new byte[2];
        is.read(buf);
        int size = ArrayTools.getShort(buf, 0)-1;
        if (size < 0)
            throw new IOException("Invalid String Length");
        buf = new byte[size];
        is.read(buf);
        byte zero = (byte)is.read();
        if (zero != 0)
            throw new IOException("Invalid Ending ByteType");
        value = new String(buf, "UTF-8");
    }

    public String getValue() {
        return value;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof UnicodeType))
            return false;
        return value.equals(((UnicodeType)(obj)).value);
    }

    public String toString() {
        return value;
    }

    public void setValue(Object value) {
		if (value instanceof String) {
			this.value = (String)value;
		}
	}
}

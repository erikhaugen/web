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
 * The NULL value
 */
public class NullType extends ValueType {
    public byte getType() {
        return Types.NULL;
    }

    public void writeData(OutputStream os) throws IOException {
    }

    public void readData(InputStream is) throws IOException {
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof NullType))
            return false;
        return true;
    }

    public String toString() {
        return "null";
    }

	@Override
	public void setValue(Object value) {
		
	}

}

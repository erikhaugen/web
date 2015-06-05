/**
 * Created by Trung Chau.
 * Date: 6/5/12
 * Time: 11:26 AM
 */
package com.livescribe.datatype.basic;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * The TerminatorType value. This indicates the end of TermArrayType.
 */
class TerminatorType extends ValueType {
    public byte getType() {
        return Types.TERMINATOR;
    }

    public void writeData(OutputStream os) throws IOException {
    }

    public void readData(InputStream is) throws IOException {
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof TerminatorType))
            return false;
        return true;
    }

    public String toString() {
        return "terminator";
    }

	@Override
	public void setValue(Object value) {
		// TODO Auto-generated method stub
		
	}

}

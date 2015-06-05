/**
 * Created by Trung Chau.
 * Date: 2/29/12
 * Time: 5:50 PM
 */
package com.livescribe.datatype.basic;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * The BooleanType value
 */
public class BooleanType extends ValueType {
    private boolean value;

    public byte getType() {
        return Types.BOOLEAN;
    }

    public BooleanType(boolean value) {
        this.value = value;
    }

    public void writeData(OutputStream os) throws IOException {
        os.write(value?1:0);
    }

    public void readData(InputStream is) throws IOException {
        value = is.read()==1 ? true:false;
    }

    public boolean getValue() {
        return value;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof BooleanType))
            return false;
        return value == ((BooleanType)(obj)).value;
    }

    public String toString() {
        return ""+value;
    }

	@Override
	public void setValue(Object value) {
		if (value instanceof Boolean) {
			this.value = ((Boolean)value).booleanValue();
		} else if (value instanceof String) {
			this.value = Boolean.parseBoolean((String)value);
		}
		return;
	}

}

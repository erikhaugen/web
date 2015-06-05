/**
 * Created by Trung Chau.
 * Date: 2/29/12
 * Time: 5:43 PM
 */
package com.livescribe.datatype.basic;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This is base class of encoded value.
 * In general, the value includes 2 parts: [Type Identifier] [Data]
 */
public abstract class ValueType {
    /**
     * Get the ValueType Type Identifier
     * @return the value type Identifier
     */
    public abstract byte getType();
    
    public abstract void setValue(Object value);

    /**
     * Write value to output stream.
     * This method write both Type Identifier and Data to the output stream
     * @param os The output stream to write data
     * @throws IOException if error happens
     */
    public void write(OutputStream os) throws IOException {
        os.write(getType());
        writeData(os);
    }

    /**
     * Write value to output stream.
     * Same as the write() method, but it just writes the Data, not Type Identifier.
     * @param os The output stream to write data
     * @throws IOException if error happens
     */
    public abstract void writeData(OutputStream os) throws IOException;

    /**
     * Read value from input stream.
     * This method read both Type Identifier and Data from the input stream
     * @param is The input stream to read data
     * @throws IOException if error happens
     */
    public void read(InputStream is) throws IOException {
        int type = is.read();
        if (type != getType())
            throw new IOException("Invalid Type. Expect=" + getType() + ", actual=" + type);
        readData(is);
    }

    /**
     * Read value from input stream
     * Same as read(), but it just reads the Data, not Type Identifier
     * @param is The input stream to read data.
     * @throws IOException of error happens
     */
    public abstract void readData(InputStream is) throws IOException;
}

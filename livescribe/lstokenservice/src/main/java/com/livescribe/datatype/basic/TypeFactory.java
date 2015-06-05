/**
 * Created by Trung Chau.
 * Date: 5/28/12
 * Time: 2:55 PM
 */
package com.livescribe.datatype.basic;

import java.io.IOException;
import java.io.InputStream;

/**
 * Provide ability to create instance of ValueType
 */
public class TypeFactory {
    /**
     * Create ValueType based on type identifier
     * @param type the type identifier
     * @return the ValueType
     * @throws java.io.IOException
     */
    public static ValueType create(int type) throws IOException {
        switch (type) {
            case Types.NULL:
                return new NullType();
            case Types.BOOLEAN:
                return new BooleanType(false);
            case Types.BYTE:
                return new ByteType((byte)0);
            case Types.SHORT:
                return new ShortType((short)0);
            case Types.INT:
                return new IntType(0);
            case Types.LONG:
                return new LongType(0);
            case Types.FLOAT:
                return new FloatType(0);
            case Types.DOUBLE:
                return new DoubleType(0);
            case Types.UNICODE:
                return new UnicodeType(null);
            case Types.BINARY:
                return new BinaryType(null);
            case Types.VALUE_ARRAY:
                return new ValueArrayType(null);
            case Types.DATA_ARRAY:
                return new DataArrayType(null, (byte)0);
            case Types.TERM_ARRAY:
                return new TermArrayType(null);
            case Types.TERMINATOR:
                return new TerminatorType();
            default:
                throw new IOException("Create invalid type (" + type + ")");
        }
    }

    /**
     * Read ValueType from input stream
     * @param is the stream to read
     * @return the ValueType
     * @throws IOException if error happens
     */
    public static ValueType read(InputStream is) throws IOException {
        int type = is.read();
        ValueType value = create(type);
        value.readData(is);
        return value;
    }
}

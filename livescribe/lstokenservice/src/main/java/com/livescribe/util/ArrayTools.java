/*
 * User: ssilverman
 * Date: 8/2/11
 * Time: 12:34 PM
 * (c) 2011 Livescribe Inc.
 */
package com.livescribe.util;

/**
 * Miscellaneous array tools.
 *
 * @author Shawn Silverman
 */
public class ArrayTools {
    private static ThreadLocal localBuf = new ThreadLocal();

    /**
     * Disallow instantiation.
     */
    private ArrayTools() {
    }

    /**
     * Gets a buffer of the specified size or bigger.  The buffer is local to
     * the current thread.
     *
     * @param size the minimum buffer size.
     * @return a buffer of at least the specified size.
     * @throws IllegalArgumentException if the size &lt; zero.
     */
    public static byte[] getThreadLocalBuffer(int size) {
        if (size < 0) throw new IllegalArgumentException("size < 0: " + size);

        byte[] buf = (byte[])localBuf.get();
        if (buf == null || size > buf.length) {
            buf = new byte[size];
            localBuf.set(buf);
        }

        return buf;
    }

    /**
     * Gets a 64-bit long value from the array at the specified offset.  The
     * value is read in big-endian order.
     *
     * @param b read from this byte array
     * @param off the offset
     * @return a 64-bit long value.
     */
    public static long getLong(byte[] b, int off) {
        return ((long)getInt(b, off) << 32) | (getInt(b, off + 4) & 0xFFFFFFFFL);
    }

    /**
     * Gets a 32-bit int value from the array at the specified offset.  The
     * value is read in big-endian order.
     *
     * @param b read from this byte array
     * @param off the offset
     * @return a 32-bit int value.
     */
    public static int getInt(byte[] b, int off) {
        return ((b[off++] & 0xff) << 24)
             | ((b[off++] & 0xff) << 16)
             | ((b[off++] & 0xff) <<  8)
             |  (b[off]   & 0xff);
    }

    /**
     * Sets a 32-bit int value at the given location in the array.  This
     * writes the value in big-endian order.
     *
     * @param b write to this byte array
     * @param off the offset
     * @param val the 32-bit value
     */
    public static void setInt(byte[] b, int off, int val) {
        b[off++] = (byte)(val >> 24);
        b[off++] = (byte)(val >> 16);
        b[off++] = (byte)(val >>  8);
        b[off]   = (byte) val;
    }

    /**
     * Sets a 64-bit long value at the given location in the array.  This
     * writes the value in big-endian order.
     *
     * @param b write to this byte array
     * @param off the offset
     * @param val the 64-bit value
     */
    public static void setLong(byte[] b, int off, long val) {
        b[off++] = (byte)(val >> 56);
        b[off++] = (byte)(val >> 48);
        b[off++] = (byte)(val >> 40);
        b[off++] = (byte)(val >> 32);
        b[off++] = (byte)(val >> 24);
        b[off++] = (byte)(val >> 16);
        b[off++] = (byte)(val >>  8);
        b[off]   = (byte) val;
    }

    /**
     * Gets a 16-bit int value from the array at the specified offset.  The
     * value is read in big-endian order.
     *
     * @param b read from this byte array
     * @param off the offset
     * @return a 16-bit int value.
     */
    public static short getShort(byte[] b, int off) {
        return (short)(((b[off++] & 0xff) <<  8)
                     |  (b[off] & 0xff));
    }

    /**
     * Sets a 16-bit int value at the given location in the array.  This
     * writes the value in big-endian order.
     *
     * @param b write to this byte array
     * @param off the offset
     * @param val the 16-bit value
     */
    public static void setShort(byte[] b, int off, short val) {
        b[off++] = (byte)(val >>  8);
        b[off]   = (byte) val;
    }


    /**
     * Gets a double value from the array at the specified offset.
     *
     * @param b read from this byte array
     * @param off the offset
     * @return a double value.
     */
    public static double getDouble(byte[] b, int off) {
        long val = getLong(b, off);
        return Double.longBitsToDouble(val);
    }

    /**
     * Sets a double value at the given location in the array.
     *
     * @param b write to this byte array
     * @param off the offset
     * @param val the double value
     */
    public static void setDouble(byte[] b, int off, double val) {
        setLong(b, off, Double.doubleToLongBits(val));
    }

    /**
     * Gets a float value from the array at the specified offset.
     *
     * @param b read from this byte array
     * @param off the offset
     * @return a float value.
     */
    public static float getFloat(byte[] b, int off) {
        int val = getInt(b, off);
        return Float.intBitsToFloat(val);
    }

    /**
     * Sets a float value at the given location in the array.
     *
     * @param b write to this byte array
     * @param off the offset
     * @param val the float value
     */
    public static void setFloat(byte[] b, int off, float val) {
        setInt(b, off, Float.floatToIntBits(val));
    }
}

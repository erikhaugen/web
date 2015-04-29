/*
 * User: ssilverman
 * Date: 12/13/11
 * Time: 1:15 AM
 * (c) 2011 Livescribe Inc.
 */
package com.livescribe.aws.login.util;

/**
 * Utility class for parsing pen ID's.
 * <p>
 * Notes:</p>
 * <ul>
 * <li>Based on the string format, the 64-bit ID can only be in a certain
 *     range.  The top 32 bits can be in the range 0&ndash;(30<sup>3</sup>&minus;1)
 *     and the bottom 32 bits can be in the range 0&ndash;(30<sup>6</sup>&minus;1).
 *     </li>
 * <li>The serial form is expressed in base 30 with digits coming from the set,
 *     "ABCDEFGHJKMNPQRSTUWXYZ23456789".</li>
 * </ul>
 *
 * @see <a href="http://gallardo:8182/index.php?title=Decoding_Pen_Serial_Numbers_or_PenIds">Decoding Pen Serial Numbers or PenIds</a>
 *
 * @since 3.0
 * @author Shawn Silverman
 */
@Deprecated
public class PenId {
    // Alphabet for decoding and encoding

    //                                                11111111112222222222
    //                                      012345678901234567890123456789
    private static final char[] ALPHABET = "ABCDEFGHJKMNPQRSTUWXYZ23456789".toCharArray();
    private static final int[] DECODED = new int[128];
    static {
        // Any unset entries are set to -1

        for (int i = DECODED.length; --i >= 0; ) {
            DECODED[i] = -1;
        }

        for (int i = ALPHABET.length; --i >= 0; ) {
            DECODED[ALPHABET[i]] = i;
        }
    }

    private static final int BASE = 30;

    /** The max. manufacturer ID. */
    public static final int MAX_MAN_ID = BASE*BASE*BASE - 1;

    /** The max. pen ID. */
    public static final int MAX_PEN_ID = BASE*BASE*BASE*BASE*BASE*BASE - 1;

    // From penid.h (BALIFW\components\afp\arm-elf-gcc\include\common\penid.h)

    private static final long UNDEFINED_ID = 0L;
    private static final long INVALID_ID   = -2L;
    private static final long WILDCARD_ID  = UNDEFINED_ID;

    // Fields

    private long id;

    /**
     * Creates a new pen ID from the given manufacturer and individual pen ID.
     *
     * @param manufacturerId the manufacturer ID
     * @param penId the individual pen ID
     * @throws IllegalArgumentException if either value is out of range.
     * @see #MAX_MAN_ID
     * @see #MAX_PEN_ID
     */
    public PenId(int manufacturerId, int penId) {
        check(manufacturerId, penId);

        this.id = ((long)manufacturerId << 32) | (penId & 0xffffffffL);
    }

    /**
     * Creates a new pen ID from a serial string.
     *
     * @param serial the serial number
     * @throws IllegalArgumentException if the serial is invalid.
     * @throws NullPointerException if the serial is {@code null}.
     * @see #toId(String)
     */
    public PenId(String serial) {
        this(toId(serial));
    }

    /**
     * Creates a new pen ID.
     *
     * @param id the numeric form of the ID
     * @throws IllegalArgumentException if the ID contains values that are
     *         out of range.
     * @see #MAX_MAN_ID
     * @see #MAX_PEN_ID
     */
    public PenId(long id) {
        check(id);

        this.id = id;
    }

    /**
     * Gets the 64-bit ID.
     *
     * @return the 64-bit ID.
     * @see #toString()
     */
    public long getId() {
        return id;
    }

    /**
     * Returns a hash code.  This is computed by {@code penId^manId}.
     *
     * @return a hash code.
     */
    public int hashCode() {
        return (int)id ^ (int)(id >>> 32);
    }

    /**
     * Returns whether the given object is considered equal to this one.
     *
     * @param obj object to compare
     * @return whether the given object equals this one.
     */
    public boolean equals(Object obj) {
        return (obj instanceof PenId) && (((PenId)obj).id == this.id);
    }

    /**
     * Returns the serial form of this ID.
     *
     * @return the serial form.
     * @see #getId()
     * @see #toSerial(long)
     */
    public String toString() {
        return toSerial(id);
    }

    /**
     * Checks a 64-bit ID.
     *
     * @param id the ID to check
     * @throws IllegalArgumentException if the ID is out of range.
     */
    private static void check(long id) {
        check((int)(id >>> 32), (int)id);
    }

    /**
     * Checks the ID parts.
     *
     * @param manId the manufacturer ID
     * @param penId the individual pen ID
     * @throws IllegalArgumentException if the ID is out of range.
     */
    private static void check(int manId, int penId) {
        if (manId < 0 || MAX_MAN_ID < manId) {
            throw new IllegalArgumentException("Manufacturer ID range: " + manId);
        }
        if (penId < 0 || MAX_PEN_ID < penId) {
            throw new IllegalArgumentException("Pen ID range: " + penId);
        }
    }

    /**
     * Decodes a string into a numeric pen ID.  The string can either be in
     * the serial form or the numeric form.
     *
     * @param s decode this string
     * @return the numeric form of the ID.
     * @throws IllegalArgumentException if the string does not contain a
     *         valid pen ID.
     * @throws NullPointerException if the serial is {@code null}.
     * @see #toId(String)
     * @see #PenId(long)
     */
    public static PenId decode(String s) {
        long id;

        // String or number form

        if (s.indexOf('-') >= 0) {
            id = toId(s);
        } else {
            try {
                id = Long.parseLong(s);
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("Bad ID: " + s);
            }
        }

        return new PenId(id);
    }

    /**
     * Converts a pen serial string to a number.  A serial is considered
     * invalid if it contains unrecognized symbols, is in an unexpected
     * format, or if the checksum does not match.
     * <p>
     * The format is "XXX-YYY-YYY-ZZ", where:</p>
     * <dl>
     * <dt>XXX</dt>
     * <dd>The top 32 bits, the manufacturer ID</dd>
     * <dt>YYY-YYY</dt>
     * <dd>The bottom 32 bits, the pen ID</dd>
     * <dt>ZZ</dt>
     * <dd>Checksum</dd>
     * </dl>
     *
     * @param serial the serial number as a string
     * @return the serial as a number.
     * @throws IllegalArgumentException if the serial is invalid.
     * @throws NullPointerException if the serial is {@code null}.
     * @see #toSerial(long)
     */
    public static long toId(String serial) {
        if (serial.length() != 14) {
            throw new IllegalArgumentException("Bad length:" + serial);
        }
        if (serial.charAt(3) != '-' || serial.charAt(7) != '-' || serial.charAt(11) != '-') {
            throw new IllegalArgumentException("Bad format:" + serial);
        }
        for (int i = serial.length(); --i >= 0; ) {
            if (i == 3 || i == 7 || i == 11) continue;

            char c = serial.charAt(i);
            if (DECODED[c] == -1) {
                throw new IllegalArgumentException("Bad characters: " + serial);
            }
        }

        int check = 30*DECODED[serial.charAt(12)] + DECODED[serial.charAt(13)];

        // a + 30*(b + 30*c)
        // a + 30(b + 30*c)
        // a + 30*b + 30^2*c

        int manId = 30*(
                        30*DECODED[serial.charAt(0)] + DECODED[serial.charAt(1)]
                    ) + DECODED[serial.charAt(2)];

        int penId = 30*(
                        30*(
                            30*(
                                30*(
                                    30*DECODED[serial.charAt(4)] + DECODED[serial.charAt(5)]
                                ) + DECODED[serial.charAt(6)]
                            ) + DECODED[serial.charAt(8)]
                        ) + DECODED[serial.charAt(9)]
                    ) + DECODED[serial.charAt(10)];

        long id = ((long)manId << 32) | (penId & 0xffffffffL);

        // Do an unsigned modulus
        // Note that we don't have to check for negative because the manufacturer ID is always positive

        if (id % 877 != check) {
//            System.out.println("toId: id       = " + id);
//            System.out.println("toId: penId    = " + penId);
//            System.out.println("toId: manId    = " + manId);
//            System.out.println("toId: id % 877 = " + (id % 877));
//            System.out.println("toId: check    = " + check);
            throw new IllegalArgumentException("Checksum mismatch: " + serial);
        }

        return id;
    }

    /**
     * Converts a pen ID to a serial string.
     *
     * @param id the pen ID
     * @return the ID as a serial string.
     * @throws IllegalArgumentException if the ID is out of range.
     * @see #toId(String)
     */
    public static String toSerial(long id) {
        int manId = (int)(id >>> 32);
        int penId = (int)id;

        check(manId, penId);

        char[] serial = new char[14];
        serial[3] = serial[7] = serial[11] = '-';

        encode(manId, serial, 0, 3);
        int n = encode(penId, serial, 8, 3);
        encode(n, serial, 4, 3);

//        System.out.println("toSerial: id % 877=" + (id % 877));
        encode((int)(id % 877), serial, 12, 2);

        return new String(serial);
    }

    /**
     * Encodes a number into base 30.  This returns the number after lopping
     * off the bottom {@code len} base 30 digits.
     *
     * @param number the number
     * @param buf store here
     * @param off starting buffer offset
     * @param len number of base 30 digits.
     * @return the number shifted by {@code len} base 30 digits.
     * @throws IndexOutOfBoundsException if the offset or length is out of range.
     */
    private static int encode(int number, char[] buf, int off, int len) {
        // Some unsigned handling

        if (number < 0) {
            // Do one iteration in long-space

            long n = number & 0xffffffffL;
            buf[off + len - 1] = ALPHABET[(int)(n % 30)];
            number = (int)(n / 30);

            len--;
        }

        for (int i = off + len; --i >= off; ) {
            buf[i] = ALPHABET[number % 30];
            number /= 30;
        }

        return number;
    }

    /**
     * Convert penID in a form of ABCD-EFGH or ABCDEFGH to AYE-ABC-DEF-GH
     * 
     * @param code
     * @return
     */
    public static String convert8CharCodeToPenId(String code) {    		
    	// replacing dash separator 
    	if (code.indexOf("-") > 0) {
    		code = code.replaceAll("-", "");
    	}
    	
    	// validating code length    	
    	if (code.length() != 8) {
    		throw new IllegalArgumentException("Bad length: " + code);
    	}
    	
    	// convert code to upper case
    	code = code.toUpperCase();
    	
    	// build AYE-ABC-DEF-GH format
    	StringBuilder sb = new StringBuilder();
    	sb.append("AYE-");
    	sb.append(code.substring(0, 3));
    	sb.append("-");
    	sb.append(code.substring(3, 6));
    	sb.append("-");
    	sb.append(code.substring(6, 8));
    	
    	return sb.toString();
    }
    
//    public static void main(String[] args) {
//        long id;
//        do { id = nextLong(); }
//        while ((id >>> 32) >= 27000 || (id & 0xffffffffL) >= 729000000);
////        long id = toId("AYE-APN-RTC-YR");
//        id = -2L;
//        String serial = toSerial(id);
//
//        System.out.println("id     = " + id);
//        System.out.println("serial = " + serial);
//        try {
//            System.out.println("toId() = " + toId(serial));
//        } catch (IllegalArgumentException ex) {
//            System.out.println("Bad serial: " + ex);
//        }
//    }
//
//    private static long seed = System.currentTimeMillis();
//
//    /**
//     * Produces a medium-quality sequence of random numbers.
//     *
//     * @return the next number in the sequence.
//     * @see <a href="http://www.javamex.com/tutorials/random_numbers/xorshift.shtml">XORShift random number generators in Java</a>
//     * @see <a href="http://www.javamex.com/java_equivalents/unsigned.shtml">The Java equivalent of 'unsigned'</a>
//     */
//    public static synchronized long nextLong() {
//        long x = seed;
//        x ^= (x << 21);
//        x ^= (x >>> 35);
//        x ^= (x << 4);
//        return seed = x;
//    }
}

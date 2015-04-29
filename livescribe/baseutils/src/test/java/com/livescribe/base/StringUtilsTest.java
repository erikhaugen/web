package com.livescribe.base;

import junit.framework.TestCase;

public class StringUtilsTest extends TestCase {

	@Override
    protected void setUp() {
    	
    }
	
	@Override
    protected void tearDown() {
		
	}
	
	public void testescapeNewlines() {
		
		String[] inputs = new String[] {"abcdef", "abc\ndef"};
		String[] outputs = new String[] {"abcdef", "abc\\ndef"};

		for ( int i = 0; i < inputs.length; i++ ) {
			assertEquals(outputs[i], StringUtils.escapeNewlines(inputs[i]));
		}
	}
	
	public void testisBlank() {
		String[] inputs = new String[] { null, "", "         " };
		for ( String input : inputs ) {
			assertTrue(StringUtils.isBlank(input));
		}
		String input = "  abc ";
		assertFalse(StringUtils.isBlank(input));
	}

	public void testisNotBlank() {
		String[] inputs = new String[] { null, "", "         " };
		
		for ( String input : inputs ) {
			assertFalse(StringUtils.isNotBlank(input));
		}
		String input = "  abc ";
		assertTrue(StringUtils.isNotBlank(input));
	}
	
	public void testRemoveInvalidXMLCharacters() {
		
		StringBuilder in = new StringBuilder();
		in.append("abc");
		in.append(Character.toChars(0x0));
		in.append(Character.toChars(0x1));
		in.append(Character.toChars(0x2));
		in.append("def");
		
		String out = StringUtils.removeInvalidXMLCharacters(in.toString());
		
		assertTrue("Incorrect number of characters returned from method.",out.length() == 6);
		assertEquals("The returned String was incorrect.", "abcdef", out);
	}
}

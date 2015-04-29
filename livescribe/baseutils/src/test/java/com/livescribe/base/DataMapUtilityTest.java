package com.livescribe.base;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import com.livescribe.base.anno.DataMapUtility;

public class DataMapUtilityTest extends TestCase {
	@Override
    protected void setUp() {
    	
    }
	
	@Override
    protected void tearDown() {
		
	}
	
	public void testMapCreation() {
		MappableData mappable = new MappableData(5, "test");
		
		try {
			Map<String, Object> map = DataMapUtility.convertToMap(mappable);
			assertEquals(mappable.getIntValue(), map.get("int"));
			assertEquals(mappable.getStrValue(), map.get("str"));
		} catch ( Exception ex ) {
			assert(false);
		}
	}
	
	public void testObjectReconstruct() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("int", 7);
		map.put("str", "testString");
		
		try {
			Object obj = DataMapUtility.createObjectFromMap(map, MappableData.class);
			assertTrue ( obj instanceof MappableData );
			MappableData mappable = (MappableData) obj;
			assertEquals(map.get("int"), mappable.getIntValue());
			assertEquals(map.get("str"), mappable.getStrValue());
		} catch (Exception e) {
			assert(false);
		}
	}
	
	public void testObjectRead() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("int", 7);
		map.put("str", "testString");
		
		try {
			MappableData data = new MappableData();
			DataMapUtility.readObjectFromMap(map, data);
			assertTrue ( data instanceof MappableData );
			MappableData mappable = (MappableData) data;
			assertEquals(map.get("int"), mappable.getIntValue());
			assertEquals(map.get("str"), mappable.getStrValue());
		} catch (Exception e) {
			assert(false);
		}
	}
}

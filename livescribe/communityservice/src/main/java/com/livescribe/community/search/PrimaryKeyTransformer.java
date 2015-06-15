/**
 * Created:  Oct 13, 2010 11:06:35 AM
 */
package com.livescribe.community.search;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.solr.handler.dataimport.Context;
import org.apache.solr.handler.dataimport.Transformer;

import com.livescribe.base.utils.WOAppMigrationUtils;

/**
 * <p></p>
 *
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class PrimaryKeyTransformer extends Transformer {

	private static final String PRIMARY_KEY = "primaryKey";
	private static final String CATEGORY_KEY = "categoryKey";
	private static final String USER_PROFILE_KEY = "userProfileKey";
	
	//	Necessary to handle 'null' keys in rows.  
	//	Solr's DataImportHandler needs each String length to match.  Using "" causes an error.
	private static final String EMPTY_KEY = "000000000000000000000000000000000000000000000000";
	
	/**
	 * <p></p>
	 * 
	 */
	public PrimaryKeyTransformer() {
	}

	/* (non-Javadoc)
	 * @see org.apache.solr.handler.dataimport.Transformer#transformRow(java.util.Map, org.apache.solr.handler.dataimport.Context)
	 */
	@Override
	public Object transformRow(Map<String, Object> values, Context context) {
		
//		HashMap<String, Object> newRow = new HashMap<String, Object>();
		
		Set keys = values.keySet();
		if (keys.contains(PRIMARY_KEY)) {
			Object obj = values.get(PRIMARY_KEY);
			if (obj instanceof byte[]) {
				byte[] pk = (byte[])obj;
				String pkStr = WOAppMigrationUtils.convertPrimaryKeyToString(pk);
				values.put(PRIMARY_KEY, pkStr);
			}
		}
		if (keys.contains(CATEGORY_KEY)) {
			Object obj = values.get(CATEGORY_KEY);
			if (obj == null) {
				values.put(CATEGORY_KEY, EMPTY_KEY);
			}
			else if (obj instanceof byte[]) {
				byte[] catKey = (byte[])obj;
				String catKeyStr = WOAppMigrationUtils.convertPrimaryKeyToString(catKey);
				values.put(CATEGORY_KEY, catKeyStr);
			}
		}
		if (keys.contains(USER_PROFILE_KEY)) {
			Object obj = values.get(USER_PROFILE_KEY);
			if (obj == null) {
				values.put(USER_PROFILE_KEY, EMPTY_KEY);
			}
			else if (obj instanceof byte[]) {
				byte[] upKey = (byte[])obj;
				String upKeyStr = WOAppMigrationUtils.convertPrimaryKeyToString(upKey);
				values.put(USER_PROFILE_KEY, upKeyStr);
			}
		}
		
		return values;
	}

}

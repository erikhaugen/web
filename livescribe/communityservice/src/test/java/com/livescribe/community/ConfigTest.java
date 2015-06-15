/**
 * 
 */
package com.livescribe.community;

import static org.junit.Assert.*;

import java.util.List;

import org.hibernate.Query;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author kmurdoff
 *
 */
public class ConfigTest extends BaseTest {

	/**
	 * 
	 */
	public ConfigTest() {
		super();
	}

	@Test
	@Transactional
	public void testConfiguration() {
		
		if (!skipTests) {
			Query query = testSessionFactory.getCurrentSession().createQuery("from Country");
			
			List list = query.list();
			
			assertNotNull("The returned List was 'null'.", list);
		}
	}
}

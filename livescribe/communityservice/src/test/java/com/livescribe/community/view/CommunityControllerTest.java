/**
 * 
 */
package com.livescribe.community.view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.ModelAndView;

import com.livescribe.community.dao.PencastDao;
import com.livescribe.community.service.PencastService;
import com.livescribe.community.view.vo.PencastVo;

/**
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class CommunityControllerTest extends AbstractControllerTest {
	
	private static String HOSTNAME	= "localhost";
	private static String PORT		= ":8080";
	
	private CommunityController controller;
	private PencastDao mockDao;
	private PencastService mockService;
	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	
	/**
	 * <p>Default class constructor.</p>
	 */
	public CommunityControllerTest() {
		super();
	}
	
	@Before
	public void setUp() throws Exception {
		
		this.controller = new CommunityController();
		
		//	Create mock objects of the Service and DAO beans.
		this.mockService = EasyMock.createMock(PencastService.class);
		this.mockDao = EasyMock.createMock(PencastDao.class);

		//	Substitute mock objects for Autowired beans.
		ReflectionTestUtils.setField(this.mockService, "pencastDao", this.mockDao);
		ReflectionTestUtils.setField(this.controller, "pencastService", this.mockService);
		
		this.request = new MockHttpServletRequest("GET", "/pencast.atom");
		this.request.addHeader("Content-Type", "application/atom+xml");
		
		this.response = new MockHttpServletResponse();		
	}
	
	/**
	 * @throws Exception
	 */
//	@Test
	public void testHandleGetAllPencastsRequest() throws Exception {
		
		String method = "testHandleGetAllPencastsRequest(): ";
		
		DispatcherServlet dServlet = getServletInstance();
		
		this.request = new MockHttpServletRequest("GET", "/community/community/pencast");
		this.request.addHeader("Accept", "application/atom+xml");
		
		dServlet.service(this.request, this.response);
		
		logger.debug(method + "response code: " + this.response.getStatus());
		
		assertTrue("Response status code: " + this.response.getStatus(), this.response.getStatus() == 200);
	}
	
	/**
	 * @throws Exception
	 */
//	@Test
	public void testHandleGetTopPencastsRequest() throws Exception {
		
		String method = "testHandleGetTopPencastsRequest(): ";
		
		DispatcherServlet dServlet = getServletInstance();
		
		this.request = new MockHttpServletRequest("GET", "/community/community/pencast/top.atom");
		this.request.addHeader("Accept", "application/atom+xml");
		
		dServlet.service(this.request, this.response);
		
		logger.debug(method + "response code: " + this.response.getStatus());
		
		assertTrue("Response status code: " + this.response.getStatus(), this.response.getStatus() == 200);
	}
	
	/**
	 * 
	 */
	@Test
	public void testGetAllPencasts() {
		
		ArrayList<PencastVo> pList = new ArrayList<PencastVo>();
		EasyMock.expect(this.mockService.getAllPencasts(0, 20)).andReturn(pList);
		EasyMock.replay(this.mockService);
		
		ModelAndView mv = this.controller.getAllPencasts(this.request);
		
		assertNotNull("The returned ModelAndView object was 'null'.", mv);
		assertEquals("The returned view name was incorrect: '" + mv.getViewName() + "'.", "pencastListView", mv.getViewName());
		assertNotNull("The returned List of pencasts was 'null'.", mv.getModel().get("pencastList"));
	}
	
	public void testGetTopPencasts() {
		
		ArrayList<PencastVo> pList = new ArrayList<PencastVo>();
		EasyMock.expect(this.mockService.getTopPencasts(0, 20)).andReturn(pList);
		EasyMock.replay(this.mockService);
		
		ModelAndView mv = this.controller.getTopPencasts(this.request);
		
		
	}
}

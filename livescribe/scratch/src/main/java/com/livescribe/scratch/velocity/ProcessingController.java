/**
 * Created:  Mar 28, 2014 5:14:26 PM
 */
package com.livescribe.scratch.velocity;

import java.io.IOException;
import java.io.StringWriter;

import org.apache.log4j.Logger;
import org.apache.velocity.exception.VelocityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.livescribe.framework.web.response.ErrorResponse;
import com.livescribe.framework.web.response.ResponseCode;
import com.livescribe.scratch.velocity.mock.MockVelocityContext;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
@Controller
public class ProcessingController {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	@Autowired
	private EvernoteProcessor evernoteProcessor;
	
	/**
	 * <p></p>
	 * 
	 */
	public ProcessingController() {
	}

	@RequestMapping("/merge/evernote")
	public ModelAndView mergeEvernote() {
		
		ModelAndView mv = new ModelAndView();

		MockVelocityContext context = new MockVelocityContext();
		
		
		StringWriter writer = null;
		
		try {
			writer = evernoteProcessor.merge(context);
		} catch (VelocityException ve) {
			String msg = "VelocityException thrown";
			ErrorResponse response = new ErrorResponse(ResponseCode.SERVER_ERROR, ve.getMessage());
			mv.setViewName("xmlResponseView");
			mv.addObject("response", response);
			return mv;
		} catch (IOException ioe) {
			String msg = "IOException thrown";
			ErrorResponse response = new ErrorResponse(ResponseCode.SERVER_ERROR, ioe.getMessage());
			mv.setViewName("xmlResponseView");
			mv.addObject("response", response);
			return mv;
		}
		
		MergeResponse response = new MergeResponse(ResponseCode.SUCCESS, writer.toString());
		mv.setViewName("xmlResponseView");
		mv.addObject("response", response);
		
		return mv;
	}
}

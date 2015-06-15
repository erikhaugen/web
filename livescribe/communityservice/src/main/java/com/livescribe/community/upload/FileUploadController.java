/**
 * 
 */
package com.livescribe.community.upload;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

/**
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
@Controller
public class FileUploadController {
	
	/**
	 * <p>Default class constructor.</p>
	 */
	public FileUploadController() {
		
	}
	
	/**
	 * @return
	 */
	@RequestMapping(value = "/pencast/upload", method = RequestMethod.POST)
	public ModelAndView uploadPencast(@RequestParam("file") MultipartFile file) {
		
		ModelAndView mv = new ModelAndView();
		
		return mv;
	}
}

/**
 * 
 */
package com.livescribe.community.view;

import java.util.List;

import com.sun.syndication.feed.module.Module;

/**
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public interface PencastModule extends Module {
	
	public static final String URI = "http://www.livescribe.com/module/pencast/1.0";
	
//	public List getAudio();
//	public void setAudio(List audio);
	
	public Long getDuration();
	public void setDuration(Long milliseconds);
	
//	public List getStrokes();
//	public void setStrokes(List strokes);
}

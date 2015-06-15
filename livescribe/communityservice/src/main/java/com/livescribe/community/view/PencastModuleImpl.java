/**
 * 
 */
package com.livescribe.community.view;

import java.util.ArrayList;
import java.util.List;

import com.sun.syndication.feed.module.ModuleImpl;

/**
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class PencastModuleImpl extends ModuleImpl implements PencastModule {

//	private List audio = new ArrayList();
	private Long duration;
//	private List strokes = new ArrayList();
	
	/**
	 * 
	 */
	public PencastModuleImpl() {
		super(PencastModule.class, PencastModule.URI);
	}

	/* (non-Javadoc)
	 * @see com.sun.syndication.feed.CopyFrom#copyFrom(java.lang.Object)
	 */
	@Override
	public void copyFrom(Object obj) {
		
		PencastModule penMod = (PencastModule)obj;
//		setAudio(penMod.getAudio());
		setDuration(penMod.getDuration());
//		setStrokes(penMod.getStrokes());
	}

//	@Override
//	public List getAudio() {
//		
//		return audio;
//	}

	/* (non-Javadoc)
	 * @see com.livescribe.community.view.PencastModule#getDuration()
	 */
	@Override
	public Long getDuration() {
		
		return duration;
	}

	/* (non-Javadoc)
	 * @see com.sun.syndication.feed.CopyFrom#getInterface()
	 */
	@Override
	public Class getInterface() {
		
		return PencastModule.class;
	}

//	@Override
//	public List getStrokes() {
//		
//		return strokes;
//	}

//	/* (non-Javadoc)
//	 * @see com.sun.syndication.feed.module.Module#getUri()
//	 */
//	@Override
//	public String getUri() {
//		
//		return URI;
//	}

//	@Override
//	public void setAudio(List audio) {
//		// TODO Auto-generated method stub
//		
//	}

	/* (non-Javadoc)
	 * @see com.livescribe.community.view.PencastModule#setDuration(long)
	 */
	@Override
	public void setDuration(Long milliseconds) {
		
		this.duration = milliseconds;
	}

//	@Override
//	public void setStrokes(List strokes) {
//		
//		
//	}

}

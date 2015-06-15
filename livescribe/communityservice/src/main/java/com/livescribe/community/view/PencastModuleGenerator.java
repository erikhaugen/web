/**
 * 
 */
package com.livescribe.community.view;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.Namespace;

import com.sun.syndication.feed.module.Module;
import com.sun.syndication.io.ModuleGenerator;

/**
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class PencastModuleGenerator implements ModuleGenerator {

	private Logger logger = Logger.getLogger(PencastModuleGenerator.class.getName());
	
	private static final Namespace PENCAST_NS  = Namespace.getNamespace("pcc", PencastModule.URI);
	private static final Set NAMESPACES;
	
	static {
		Set nss = new HashSet();
		nss.add(PENCAST_NS);
		NAMESPACES = Collections.unmodifiableSet(nss);
	}
	
	/**
	 * 
	 */
	public PencastModuleGenerator() {

	}

	/* (non-Javadoc)
	 * @see com.sun.syndication.io.ModuleGenerator#generate(com.sun.syndication.feed.module.Module, org.jdom.Element)
	 */
	@Override
	public void generate(Module module, Element element) {

		logger.debug("generate():  Begin");
		
        //	This is not necessary, it is done to avoid the namespace definition in every item.
        Element root = element;
        while (root.getParent() != null && root.getParent() instanceof Element) {
            root = (Element) element.getParent();
        	logger.debug("generate():  'root' is now:  " + root.getName());
        }
        root.addNamespaceDeclaration(PENCAST_NS);

        PencastModule pm = (PencastModule)module;
        
//        List audio = pm.getAudio();
//        for (int i = 0; i < audio.size(); i++) {
//            element.addContent(generateSimpleElement("audio", audio.get(i).toString()));
//        }
//        List strokes = pm.getStrokes();
//        for (int i = 0; i < strokes.size(); i++) {
//            element.addContent(generateSimpleElement("strokes",strokes.get(i).toString()));
//        }
        if (pm.getDuration() != null) {
            element.addContent(generateSimpleElement("duration", pm.getDuration().toString()));
        }
	}

	protected Element generateSimpleElement(String name, String value) {
		
		logger.debug("generateSimpleElement():  Adding " + name + " with " + value);
		
		Element element = new Element(name, PENCAST_NS);
		element.setAttribute(name, value);
		return element;
	}
	
	/* (non-Javadoc)
	 * @see com.sun.syndication.io.ModuleGenerator#getNamespaceUri()
	 */
	@Override
	public String getNamespaceUri() {

		return PencastModule.URI;
	}

	/* (non-Javadoc)
	 * @see com.sun.syndication.io.ModuleGenerator#getNamespaces()
	 */
	@Override
	public Set getNamespaces() {

		return NAMESPACES;
	}

}

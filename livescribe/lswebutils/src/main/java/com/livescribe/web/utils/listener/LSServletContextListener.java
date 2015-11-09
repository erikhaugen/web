package com.livescribe.web.utils.listener;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Usage of this class, Can add this class as the ServletContextListener 
 * in web.xml. 
 * 
 * <listener>
 *   <listener-class>com.livescribe.web.utils.listener.LSServletContextListener</listener-class>
 * </listener>
 * Add your code as a listener to this class
 * LSServletContextListener.addShutdownListener(new ShutdownListener() {
 * 			public void shutdown () {
 * 				// Code to be executed for shutdown 
 * 				........ 
 * 			}
 * });
 * 
 * You can add as many listeners to the LSServletContextListener. They will all
 * be called in the order they were added. To enfornce an ordering of multiple
 * listeners, build your own array in the order in which you want them called
 * and then add them using the LSServletContextListener.addShutdownListeners method
 * 
 * @author smukker
 *
 */

public class LSServletContextListener implements ServletContextListener {

	private static List<ShutdownListener> listeners = new ArrayList<ShutdownListener>();
	
	public final void contextDestroyed(ServletContextEvent event) {
		for ( ShutdownListener listener : listeners) {
			listener.shutdown(event);
		}
	}

	public void contextInitialized(ServletContextEvent event) {

	}
	
	public static void addShutdownListener(ShutdownListener listener) {
		listeners.add(listener);
	}
	
	public static void addShutdownListeners(List<ShutdownListener> listeners) {
		listeners.addAll(listeners);
	}

}


package com.livescribe.web.utils.listener;

import javax.servlet.ServletContextEvent;

public interface ShutdownListener {

	public void shutdown(ServletContextEvent event);
}

package de.alichs.osgi.restdemo.webapp.osgi;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.osgi.framework.launch.Framework;

public class OSGiServletContextListener implements ServletContextListener {

	/**
	 * The OSGi context
	 */
	private Framework framework;

	@Override
	public void contextInitialized(ServletContextEvent event) {
		ServletContext servletContext = event.getServletContext();
		servletContext.log("Starting OSGi framework...");
			
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		ServletContext servletContext = event.getServletContext();
		servletContext.log("OSGi framework [" + framework.getSymbolicName()
				+ "] is shutting down...");
	}
}

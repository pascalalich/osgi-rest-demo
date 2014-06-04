package de.alichs.osgi.restdemo.v2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * {@link DispatcherServlet} that gets the {@link WebApplicationContext} right
 * in OSGi environment.
 */
@Component("osgiDispatcherServlet")
public class OSGiDispatcherServlet extends DispatcherServlet {
	private static final long serialVersionUID = 1;

	private final Logger LOG = LoggerFactory
			.getLogger(OSGiDispatcherServlet.class);

	@Autowired
	private ApplicationContext bundleApplicationContext;

	public OSGiDispatcherServlet() {
		setNamespace("osgiDispatcherServlet");
	}

	/**
	 * Creates an empty WebApplicationContext with the parent set to this
	 * bundle's OSGi ApplicationContext. The latter must be configured for
	 * Spring MVC and request scope.
	 */
	@Override
	protected WebApplicationContext createWebApplicationContext(
			final ApplicationContext parent) {

		LOG.info("Creating WebApplicationContext for REST API implementation v2...");
		final GenericWebApplicationContext webApplicationContext = new GenericWebApplicationContext();
		webApplicationContext.setParent(bundleApplicationContext);
		webApplicationContext.refresh();

		return webApplicationContext;
	}

}

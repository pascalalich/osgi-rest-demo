package de.alichs.osgi.restdemo.webapp.osgi;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.felix.framework.Felix;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.launch.Framework;

public class OSGiServletContextListener implements ServletContextListener {

	private static final String OSGI_PROPERTIES_PATH = "/WEB-INF/felix.properties";
	private static final String EMBEDDED_BUNDLES_PATH = "/WEB-INF/bundles/";
	// 30 seconds
	private static final int FRAMEWORK_STOP_TIMEOUT = 30 * 1000;

	/**
	 * The OSGi framework context
	 */
	private Framework framework;

	@Override
	public void contextInitialized(ServletContextEvent event) {
		ServletContext servletContext = event.getServletContext();
		servletContext.log("Starting OSGi framework...");
		try {
			startFramework(servletContext);
		} catch (IOException | BundleException e) {
			throw new RuntimeException("Failed to start OSGi framework", e);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		ServletContext servletContext = event.getServletContext();
		servletContext.log("OSGi framework is shutting down...");
		try {
			stopFramework(servletContext);
		} catch (BundleException e) {
			throw new RuntimeException("Failed to stop OSGi framework", e);
		}
	}

	private void startFramework(ServletContext servletContext)
			throws IOException, BundleException {
		final Map<String, Object> configuration = createFrameworkConfiguration(servletContext);
		framework = new Felix(configuration);
		framework.init();
		framework.start();
		registerBundleContext(servletContext);
		servletContext.log("OSGi framework successfully started");

	}

	private Map<String, Object> createFrameworkConfiguration(
			final ServletContext servletContext) throws IOException {
		final Map<String, Object> configuration = new HashMap<>();

		Properties properties = loadFrameworkProperties(servletContext);
		for (final Entry<Object, Object> entry : properties.entrySet()) {
			configuration.put((String) entry.getKey(), entry.getValue());
		}

		// Trying to start all bundles (for now)
		configuration.put("felix.auto.start.4",
				getURLString(findBundles(servletContext)));

		return configuration;
	}

	/**
	 * Detects bundles in the web application's bundle directory. These are the
	 * bundles included in the WAR file.
	 * 
	 * @param servletContext
	 * @return bundle URLs, never <code>null</code>
	 * 
	 */
	private Collection<URL> findBundles(final ServletContext servletContext) {
		final List<URL> bundleURLs = new LinkedList<URL>();
		final Set<String> paths = servletContext
				.getResourcePaths(EMBEDDED_BUNDLES_PATH);
		if (paths == null) {
			throw new IllegalStateException("No bundles found in '"
					+ EMBEDDED_BUNDLES_PATH + "'");
		}
		for (final String path : paths) {
			if (isBundleResource(path)) {
				bundleURLs.add(asBundleURL(servletContext, path));
			}
		}
		return bundleURLs;
	}

	/**
	 * @param path
	 *            resource path as provided by the {@link ServletContext}
	 * @return <code>true</code> if the path represents a bundle,
	 *         <code>false</code> otherwise
	 */
	private boolean isBundleResource(final String path) {
		return path.endsWith(".jar");
	}

	/**
	 * Converts the bundle resource path to an {@link URL}
	 * 
	 * @param servletContext
	 * @param bundlePath
	 *            resource path pointing to a bundle, as provided by the
	 *            {@link ServletContext}
	 * @return an {@link URL} for the given bundle path
	 */
	private URL asBundleURL(final ServletContext servletContext,
			final String bundlePath) {
		try {
			final URL url = servletContext.getResource(bundlePath);
			if (url == null) {
				throw new IllegalStateException("Bundle not found for path '"
						+ bundlePath + "'");
			}
			return url;
		} catch (final MalformedURLException e) {
			throw new IllegalStateException("Invalid bundle path '"
					+ bundlePath + "'", e);
		}
	}

	private static String getURLString(final Collection<URL> urls) {
		final StringBuilder b = new StringBuilder();

		for (final URL url : urls) {
			b.append(url.toExternalForm()).append(" ");
		}

		return b.toString();
	}

	private Properties loadFrameworkProperties(
			final ServletContext servletContext) throws IOException {
		final URL url = servletContext.getResource(OSGI_PROPERTIES_PATH);
		try (final InputStream frameworkProperties = url.openStream()) {
			final Properties properties = new Properties();
			properties.load(frameworkProperties);
			return properties;
		}
	}

	/**
	 * Register the {@link BundleContext} as context attribute. This is required
	 * for the ProxyServlet to work.
	 * 
	 * @param servletContext
	 */
	private void registerBundleContext(final ServletContext servletContext) {
		servletContext.setAttribute(BundleContext.class.getName(),
				framework.getBundleContext());
	}

	private void stopFramework(ServletContext servletContext)
			throws BundleException {
		framework.stop();
		try {
			final FrameworkEvent stopEvent = framework
					.waitForStop(FRAMEWORK_STOP_TIMEOUT);

			if (stopEvent.getType() == FrameworkEvent.WAIT_TIMEDOUT) {
				servletContext.log("Timed out after waiting "
						+ FRAMEWORK_STOP_TIMEOUT
						+ " ms for OSGi framework to shut down properly");
			} else {
				servletContext.log("OSGi framework has shut down properly");
			}
		} catch (final InterruptedException e) {
			servletContext
					.log("Interrupted while waiting for OSGi framework to shut down properly",
							e);
		}
	}
}

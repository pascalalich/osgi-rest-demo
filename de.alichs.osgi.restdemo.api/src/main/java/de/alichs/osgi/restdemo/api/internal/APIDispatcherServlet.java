package de.alichs.osgi.restdemo.api.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("dispatcherServlet")
public class APIDispatcherServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static Logger LOG = LoggerFactory
			.getLogger(APIDispatcherServlet.class);

	@Autowired
	private BundleContext bundleContext;

	private Set<HttpServlet> initialized = new HashSet<>();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		HttpServlet delegate = findDelegate(req);
		if (delegate != null) {
			delegate.service(req, resp);
		} else {
			resp.sendError(503,
					"Currently updating the service. Try again in a few seconds.");
		}
	}

	private HttpServlet findDelegate(HttpServletRequest req) {

		// example: /de.alichs.osgi.restdemo.webapp/api/v1/
		String requestURI = req.getRequestURI();

		try {
			Collection<ServiceReference<Servlet>> refs = bundleContext
					.getServiceReferences(Servlet.class, null);
			List<ServiceReference<Servlet>> candidates = new ArrayList<>();
			for (ServiceReference<Servlet> ref : refs) {
				String symbolicName = ref.getBundle().getSymbolicName();
				if ((symbolicName.equals("de.alichs.osgi.restdemo.v1") && requestURI
						.contains("/api/v1"))
						|| (symbolicName.equals("de.alichs.osgi.restdemo.v2") && requestURI
								.contains("/api/v2"))) {
					candidates.add(ref);
				}
			}

			HttpServlet servlet = null;
			if (!candidates.isEmpty()) {
				Collections.sort(candidates,
						new Comparator<ServiceReference<Servlet>>() {
							@Override
							public int compare(ServiceReference<Servlet> o1,
									ServiceReference<Servlet> o2) {
								return o1.getBundle().getVersion()
										.compareTo(o2.getBundle().getVersion());
							}
						});
				ServiceReference<Servlet> latest = candidates.get(candidates
						.size() - 1);
				servlet = (HttpServlet) bundleContext.getService(latest);
				initDelegateIfRequired(servlet);
			}

			return servlet;
		} catch (InvalidSyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	private synchronized void initDelegateIfRequired(HttpServlet delegate) {
		try {
			if (!initialized.contains(delegate)) {
				delegate.init(getServletConfig());
				initialized.add(delegate);
			}
		} catch (ServletException e) {
			throw new RuntimeException(e);
		}

	}
}

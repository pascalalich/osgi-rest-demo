package de.alichs.osgi.restdemo.v1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Implementation of RAS REST service using Spring MVC.
 */
@Controller
@RequestMapping("/test")
public class RestServiceV1 {
	private final Logger LOG = LoggerFactory.getLogger(RestServiceV1.class);

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody
	String helloWorld() {
		LOG.info("helloWorld called");
		return "Hello World from Spring REST Service";
	}
}

package de.alichs.osgi.restdemo.v2;

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
@RequestMapping("/v2")
public class RestServiceV2 {
	private final Logger LOG = LoggerFactory.getLogger(RestServiceV2.class);

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody
	String helloWorld() {
		LOG.info("helloWorld v2 called");
		return "Hello World from Spring REST Service V2";
	}
}

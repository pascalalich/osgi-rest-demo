package de.alichs.osgi.restdemo.v1;

import javax.annotation.PostConstruct;

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
@RequestMapping("/v1")
public class RestServiceV1 {
	private final Logger LOG = LoggerFactory.getLogger(RestServiceV1.class);

	@PostConstruct
	public void simulateTimeConsumingInitialization() {
		LOG.info("Simulating time consuming initialization...");
		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			LOG.error(e.getMessage(), e);
		}
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody
	String helloWorld() {
		LOG.info("helloWorld v1 called");
		return "Hello World from Spring REST Service V1";
	}
}

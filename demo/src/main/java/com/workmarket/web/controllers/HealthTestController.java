package com.workmarket.web.controllers;

import com.workmarket.utility.FileUtilities;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 3/27/12
 * Time: 3:56 PM
 *
 * This controller is used as a server "health test" by puppet. It checks for the existence of a file in the filesystem and returns success/fail.
 * It should be kept as lightweight as possible since it will be hit every 30 seconds or so.
 */
@Controller
@RequestMapping(value = "/health_test")
public class HealthTestController {

	@Value("${healthtest.filename}") private String filename;
	@Value("${healthtest.success}") private String successMsg;
	@Value("${healthtest.error}") private String errorMsg;

	@RequestMapping(value = {"/", ""}, method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
	public @ResponseBody String healthTest(HttpServletResponse response) throws IOException {

		if (FileUtilities.fileExists(filename)) {
			response.setStatus(HttpServletResponse.SC_OK);
			return successMsg;
		}
		response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		return errorMsg;
	}
}

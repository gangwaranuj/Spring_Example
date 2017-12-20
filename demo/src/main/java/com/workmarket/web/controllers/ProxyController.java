package com.workmarket.web.controllers;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * This controller is a simple HTTP GET proxy (currently only used to access Google Maps).
 */
@Controller
@RequestMapping("/proxy")
public class ProxyController extends BaseController {

	private static final Log logger = LogFactory.getLog(ProxyController.class);

	@Value("${google.maps.url}") private String mapsUrl;



	@RequestMapping(value = "/gmaps", method = RequestMethod.GET)
	public void gmaps(HttpServletRequest request, HttpServletResponse response) {

		String queryString = request.getQueryString();
		if (StringUtils.isEmpty(queryString))
			return;

		String url = String.format("%s?%s", mapsUrl, queryString);
		try {
			OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream());
			HttpClient client = new HttpClient();
			GetMethod get = new GetMethod(url);
			client.executeMethod(get);

			writer.write(get.getResponseBodyAsString());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			logger.error(e);
		}
	}

}

package com.workmarket.web.controllers;

import com.google.common.collect.ImmutableMap;
import com.workmarket.redis.RedisAdapter;
import com.workmarket.redis.RedisFilters;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Controller
@RequestMapping("/uploadProgress")
public class UploadProgressController extends BaseController {
	@Autowired private RedisAdapter redisAdapter;

	@RequestMapping(
		value = "/progress",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public AjaxResponseBuilder getUploadProgress() throws Exception {
		String uploadProgressKey = RedisFilters.userBulkUploadProgressKey(getCurrentUser().getId());
		double uploadProgress = Double.valueOf((String) redisAdapter.get(uploadProgressKey).or("1"));
		return new AjaxResponseBuilder().setSuccessful(true)
			.setData(ImmutableMap.<String, Object>of("uploadProgress", uploadProgress));
	}
}

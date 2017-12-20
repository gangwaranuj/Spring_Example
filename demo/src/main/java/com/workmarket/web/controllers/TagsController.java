
package com.workmarket.web.controllers;

import com.google.common.collect.Lists;
import com.workmarket.service.business.TagService;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;


@Controller
@RequestMapping("/tags")
public class TagsController extends BaseController {

	@Autowired private TagService tagService;
	@Autowired private MessageBundleHelper messageHelper;

	@RequestMapping(
		value = "/suggest_user_tags",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public void suggestUserTags(Model model, HttpServletRequest httpRequest) throws Exception {
		model.addAttribute("response", Collections.EMPTY_LIST);
	}

	@RequestMapping(
		value = "/tag_user",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody
	AjaxResponseBuilder tagUser(HttpServletRequest httpRequest) {

		AjaxResponseBuilder response = AjaxResponseBuilder.fail();

		Map<String, List<String>> map = CollectionUtilities.getTypedParameterMap(httpRequest.getParameterMap());
		List<String> tags = ObjectUtils.firstNonNull(map.get("tags_list[tags][]"), Lists.<String>newArrayList());

		try {
			tagService.setCompanyTags(
				getCurrentUser().getCompanyId(),
				NumberUtils.createLong(httpRequest.getParameter("resource_id")),
				tags.toArray(new String[0])
			);
			messageHelper.addMessage(response, "profile.edit.tags.success");
			return response.setSuccessful(true);

		} catch (Exception e) {
			messageHelper.addMessage(response, "profile.edit.tags.error");
			return response.setSuccessful(false);
		}
	}

	@RequestMapping(
		value = "/tag_company_user",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public void tagCompanyUser(
		Model model,
		HttpServletRequest httpRequest) {

		Map<String, List<String>> map = CollectionUtilities.getTypedParameterMap(httpRequest.getParameterMap());
		List<String> tags = map.get("tags_list[tags][]");

		tagService.setCompanyAdminTags(
			getCurrentUser().getCompanyId(),
			NumberUtils.createLong(httpRequest.getParameter("resource_id")),
			tags.toArray(new String[0])
		);

		Map<String, Object> response = new HashMap<>();
		response.put("successful", true);
		model.addAttribute("response", response);
	}

}

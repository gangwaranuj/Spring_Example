
package com.workmarket.web.controllers.service.rest;

import com.google.common.collect.Maps;
import com.workmarket.domains.model.User;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.workmarket.web.controllers.BaseController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Map;

@Controller
@RequestMapping("/service/rest/user")
public class RestUserController extends BaseController {

	@Autowired private AuthenticationService authenticationService;
	@Autowired private MessageBundleHelper messageHelper;

	@RequestMapping(value="/login", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public void login(Model model, HttpServletRequest httpRequest) {

		Map<String, Object> response = Maps.newHashMap();

		User user = authenticationService.auth(httpRequest.getParameter("email"), httpRequest.getParameter("password"));
		if (user != null) {
			// Disallow any unconfirmed users from getting in.
			if (!authenticationService.getEmailConfirmed(user)) {
				response.put("success", false);
				response.put("errors", new ArrayList<String>() {{
					add(messageHelper.getMessage("user.validation.emailNotConfirmed"));
				}});
			} else {
				response.put("success", true);
			}
		} else {
			response.put("success", false);
			response.put("errors", new ArrayList<String>() {{
				add(messageHelper.getMessage("user.validation.invalidLogin"));
			}});
		}

		model.addAttribute("response", response);
	}

}

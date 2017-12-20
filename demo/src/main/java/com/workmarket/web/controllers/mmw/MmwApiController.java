
package com.workmarket.web.controllers.mmw;

import com.workmarket.api.internal.service.ApiService;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.MessageBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/mmw/api")
public class MmwApiController extends BaseController {

	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private ApiService apiService;
	
	@RequestMapping(method=RequestMethod.GET)
	public String index(Model model) throws Exception {
		model.addAttribute("apiTokens", apiService.getRequestTokens());

		return "web/pages/mmw/api/index";
	}
	
	@RequestMapping(value="/generate", method=RequestMethod.GET)
	public String generate(Model model, RedirectAttributes redirectAttributes) throws Exception {
		apiService.createRequestToken(getCurrentUser().getCompanyId());

		MessageBundle bundle = messageHelper.newBundle();
		messageHelper.addSuccess(bundle, "mmw.api.generate.success");
		redirectAttributes.addFlashAttribute("bundle", bundle);

		return "redirect:/mmw/api";
	}
}

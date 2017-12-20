package com.workmarket.web.controllers.settings;

import com.workmarket.common.service.helpers.ServiceMessageHelper;
import com.workmarket.domains.compliance.model.ComplianceRuleSet;
import com.workmarket.domains.compliance.service.ComplianceRuleSetsService;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/settings/manage/compliance_rule_sets")
public class SettingsComplianceRuleSetsController extends BaseController {
	@Autowired ComplianceRuleSetsService complianceRuleSetsService;
	@Autowired private ServiceMessageHelper serviceMessageHelper;

	@RequestMapping(value = {"/", ""}, method = GET)
	@PreAuthorize("hasAnyRole('ACL_ADMIN')")
	public String displayCompliance(Model model) {
		return "web/pages/settings/manage/compliancerulesets/index";
	}

	@RequestMapping(value = "/list", method = GET, produces = APPLICATION_JSON_VALUE)
	public @ResponseBody ComplianceRuleSet list() {
		// for now, there's only one, so we will create it if it doesn't exist
		return complianceRuleSetsService.findOrInitializeDefault();
	}

	@RequestMapping(value = "/list", method = POST, produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder create(@RequestBody ComplianceRuleSet complianceRuleSet) {
		complianceRuleSetsService.saveOrUpdate(complianceRuleSet);
		return AjaxResponseBuilder.success().addMessage(serviceMessageHelper.getMessage("mmw.manage.compliance_rules_save_success"));
	}
}

package com.workmarket.web.controllers.settings;

import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.forms.funds.LowBalanceAlertsForm;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.MessageBundle;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/settings/alerts")
public class SettingsAlertsController extends BaseController {

	@Autowired private MessageBundleHelper messageHelper;

	private static final String viewRedirect = "redirect:/settings/alerts";

	@RequestMapping(method = GET)
	@PreAuthorize("(!principal.userFundsAccessBlocked) AND hasAnyRole('PERMISSION_MANAGEBANK')")
	public String showAlertForm(ModelMap model) {

		String view = "web/pages/settings/alerts";

		model.addAttribute("lowBalanceAlertsForm", new LowBalanceAlertsForm());
		model.addAttribute("company", companyService.findCompanyById(getCurrentUser().getCompanyId()));
		model.addAttribute("enabled", Boolean.TRUE);

		return view;
	}

	@RequestMapping(method = POST)
	@PreAuthorize("(!principal.userFundsAccessBlocked) AND hasAnyRole('PERMISSION_MANAGEBANK')")
	public String saveAlertSettings(
			@Valid @ModelAttribute("lowBalanceAlertsForm") LowBalanceAlertsForm form,
			BindingResult bind,
			RedirectAttributes flash) {

		MessageBundle bundle = messageHelper.newFlashBundle(flash);

		if (bind.hasErrors() || (form.getCustom_low_balance_flag() && StringUtils.isBlank(form.getLow_balance_amount()))) {
			messageHelper.addError(bundle, "funds.alerts.invalid");
			return viewRedirect;
		}

		try {
			Float.parseFloat(form.getLow_balance_amount());
		} catch (NumberFormatException e) {
			messageHelper.addError(bundle, "funds.alerts.invalid");
			return viewRedirect;
		}

		Map<String, String> properties = new HashMap<>();
		properties.put("lowBalanceAmount", form.getLow_balance_amount());
		properties.put("customLowBalanceFlag", form.getCustom_low_balance_flag().toString());

		try {
			companyService.updateCompanyProperties(getCurrentUser().getCompanyId(), properties);
		} catch (Exception e) {
			messageHelper.addError(bundle, "funds.alerts.exception");
			return viewRedirect;
		}

		messageHelper.addSuccess(bundle, "funds.alerts.success");

		return viewRedirect;
	}
}



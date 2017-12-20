package com.workmarket.web.controllers.mmw;

import com.workmarket.domains.model.authnz.AuthorizedInetAddress;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.JsonSerializationService;
import com.workmarket.service.infra.business.AuthorizationService;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.forms.mmw.SecurityForm;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.MessageBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.beans.PropertyEditorSupport;
import java.util.Collection;

@Controller
@RequestMapping("/mmw/security")
public class MmwSecurityController extends BaseController {

	@Autowired private AuthorizationService authnz;
	@Autowired private JsonSerializationService json;
	@Autowired private CompanyService companyService;
	@Autowired private MessageBundleHelper messageHelper;

	@InitBinder("securityForm")
	public void initFormBinder(WebDataBinder binder) {
		// TODO Bind validator

		binder.registerCustomEditor(AuthorizedInetAddress.class, new PropertyEditorSupport() {
			@Override
			public String getAsText() {
				AuthorizedInetAddress o = (AuthorizedInetAddress)getValue();
				return (o.getId() != null) ? o.getId().toString() : o.getInetAddress();
			}

			@Override
			public void setAsText(String s) throws IllegalArgumentException {
				final AuthorizedInetAddress o = new AuthorizedInetAddress();
				o.setInetAddress(s);
				o.setCompany(companyService.findCompanyById(getCurrentUser().getCompanyId()));
				setValue(o);
			}
		});
	}

	@RequestMapping(method=RequestMethod.GET)
	public String index(Model model) {
		final Long companyId = getCurrentUser().getCompanyId();
		final Collection<AuthorizedInetAddress> ips = authnz.findAuthorizedInetAddressess(companyId);
		final SecurityForm form = new SecurityForm();
		form.setIps(ips);
		form.setAuthorizeByInetAddress(authnz.isCompanyAuthByInetAddress(companyId));

		return render(model, form);
	}

	@RequestMapping(method=RequestMethod.POST)
	public String save(
		Model model,
		@Valid @ModelAttribute("securityForm") SecurityForm form,
		BindingResult bindingResult) throws Exception {

		if (bindingResult.hasErrors()) {
			MessageBundle messages = messageHelper.newBundle(model);
			messageHelper.setErrors(messages, bindingResult);
			return render(model, form);
		}

		final boolean authorizeByInetAddress = form.isAuthorizeByInetAddress();
		final Long companyId = getCurrentUser().getCompanyId();
		authnz.setCompanyAuthByIp(companyId, authorizeByInetAddress);
		authnz.setAuthorizedInetAddresses(companyId, form.getIps());
		return "redirect:/mmw/security";
	}

	private String render(Model model, SecurityForm form) {
		model.addAttribute("ipsJson", json.toJson(CollectionUtilities.extractPropertiesList(form.getIps(), "id", "inetAddress")));
		model.addAttribute("securityForm", form);

		return "web/pages/mmw/security/index";
	}
}

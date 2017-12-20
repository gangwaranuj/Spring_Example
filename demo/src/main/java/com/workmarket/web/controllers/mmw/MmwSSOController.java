
package com.workmarket.web.controllers.mmw;

import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.web.WebRequestContextProvider;
import com.workmarket.velvetrope.VelvetRope;
import com.workmarket.velvetrope.Venue;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.forms.mmw.SSOConfigurationForm;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.workmarket.common.core.RequestContext;
import com.workmarket.domains.model.company.SSOConfiguration;
import com.workmarket.service.business.SSOConfigurationService;
import com.workmarket.service.business.integration.sso.SSOService;
import com.workmarket.sso.SSOServiceClient;
import com.workmarket.sso.dto.SSOMetadataDTO;
import com.workmarket.web.models.MessageBundle;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@VelvetRope(venue = Venue.SINGLE_SIGN_ON)
@RequestMapping("/mmw/sso")
public class MmwSSOController extends BaseController {

	protected static final Log logger = LogFactory.getLog(MmwSSOController.class);

	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private SSOServiceClient ssoServiceClient;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private CompanyService companyService;
	@Autowired private SSOService ssoService;
	@Autowired private SSOConfigurationService ssoConfigurationService;
	@Autowired private WebRequestContextProvider webRequestContextProvider;

	@RequestMapping(method = RequestMethod.GET)
	public String index(Model model) throws Exception {
		final RequestContext ctx = webRequestContextProvider.getRequestContext();

		SSOConfigurationForm ssoConfigForm = new SSOConfigurationForm();

		Company company = companyService.findById(getCurrentUser().getCompanyId());

		ssoConfigForm.setRoles(getCompanyRoles(company.getId()));

		SSOMetadataDTO idpMetadata = ssoServiceClient.getCompanyMetadata(company.getUuid(),
				webRequestContextProvider.getRequestContext())
				.toBlocking().first();
		if (idpMetadata != null) {
			logger.info("Found idpMetadata for company: " + company.getUuid());
			ssoConfigForm.setIdpMetadata(idpMetadata.getMetadata());
			ssoConfigForm.setEntityId(idpMetadata.getEntityId());
		} else {
			logger.info("No idpMetadata found for company: " + company.getUuid());
		}

		SSOMetadataDTO spMetadata = ssoService.getSPMetadata();
		if (spMetadata != null) {
			ssoConfigForm.setSpMetadata(formatXML(spMetadata.getMetadata()));
		}

		SSOConfiguration configuration = ssoConfigurationService.findByCompanyId(company.getId());
		if (configuration != null) {
			ssoConfigForm.setDefaultRoleId(configuration.getDefaultRole().getId());
		}
		model.addAttribute("ssoConfigForm", ssoConfigForm);

		return "web/pages/mmw/sso/index";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String saveSSOSettings(
		@ModelAttribute("ssoConfigForm") SSOConfigurationForm form,
		BindingResult bind,
		RedirectAttributes flash) {

		MessageBundle bundle = messageHelper.newFlashBundle(flash);
		if (bind.hasErrors()) {
			messageHelper.setErrors(bundle, bind);
			return "redirect:/mmw/sso";
		}

		List<Error> errors = ssoConfigurationService.saveSSOConfiguration(form, getCurrentUser().getCompanyId());
		if(!errors.isEmpty()) {
			messageHelper.addError(bundle, errors.iterator().next().getMessage());
		}
		else {
			messageHelper.addSuccess(bundle, "sso.integration.configuration_saved");
		}
		return "redirect:/mmw/sso";
	}

	private Map<Long, String> getCompanyRoles(Long companyId) {
		List<AclRole> roles = authenticationService.findAllAvailableAclRolesByCompany(companyId);
		Map<Long, String> roleMap = new LinkedHashMap<>();
		for (AclRole role : roles) {
			roleMap.put(role.getId(), role.getName());
		}
		return roleMap;
	}

	private String formatXML(String input) {
		try {
			Document doc = DocumentHelper.parseText(input);
			StringWriter sw = new StringWriter();
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setIndent(true);
			format.setIndentSize(3);
			XMLWriter xw = new XMLWriter(sw, format);
			xw.write(doc);

			return sw.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return input;
		}
	}
}

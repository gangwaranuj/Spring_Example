package com.workmarket.web.controllers.assignments;

import com.google.common.collect.Sets;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.model.ManageMyWorkMarket;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.pricing.PricingStrategyType;
import com.workmarket.domains.work.service.validator.WorkSaveRequestValidator;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.JsonSerializationService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.VendorService;
import com.workmarket.service.infra.business.InvariantDataService;
import com.workmarket.service.search.SearchPreferencesService;
import com.workmarket.service.thrift.TWorkFacadeService;
import com.workmarket.service.thrift.transactional.work.WorkRequestInfo;
import com.workmarket.thrift.core.ConstraintViolation;
import com.workmarket.thrift.work.AuthorizationContext;
import com.workmarket.thrift.work.Resource;
import com.workmarket.thrift.work.Work;
import com.workmarket.thrift.work.WorkRequest;
import com.workmarket.thrift.work.WorkResponse;
import com.workmarket.thrift.work.WorkSaveRequest;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.MessageBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collection;
import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.filter;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static com.workmarket.utility.CollectionUtilities.containsAny;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@RequestMapping({"/assignments/contact/{workNumber}", "/assignments/{workNumber}/contact"})
@PreAuthorize("hasAnyRole('PERMISSION_MANAGEMYWORK', 'PERMISSION_MANAGECOWORK', 'PERMISSION_CREATEWORK')")

public class WorkResourceSearchController extends BaseController {

	@Autowired private InvariantDataService invariantService;
	@Autowired private CompanyService companyService;
	@Autowired private JsonSerializationService jsonService;
	@Autowired protected TWorkFacadeService tWorkFacadeService;
	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private WorkSaveRequestValidator workSaveRequestValidator;
	@Autowired private SearchPreferencesService searchPreferencesService;
	@Autowired private UserService userService;
	@Autowired private VendorService vendorService;

	@RequestMapping(method = GET)
	public String contact(
		@PathVariable String workNumber,
		Model model,
		RedirectAttributes flash) throws Exception {

		WorkRequest workRequest = new WorkRequest()
			.setUserId(getCurrentUser().getId())
			.setWorkNumber(workNumber)
			.setIncludes(Sets.newHashSet(
				WorkRequestInfo.CONTEXT_INFO,
				WorkRequestInfo.STATUS_INFO,
				WorkRequestInfo.INDUSTRY_INFO,
				WorkRequestInfo.PROJECT_INFO,
				WorkRequestInfo.BUYER_INFO,
				WorkRequestInfo.COMPANY_INFO,
				WorkRequestInfo.LOCATION_INFO,
				WorkRequestInfo.SCHEDULE_INFO,
				WorkRequestInfo.PRICING_INFO,
				WorkRequestInfo.MANAGE_MY_WORK_MARKET_INFO,
				WorkRequestInfo.CUSTOM_FIELDS_VALUES_AND_DATA_INFO,
				WorkRequestInfo.RESOURCES_INFO
			));

		WorkResponse workResponse = tWorkFacadeService.findWork(workRequest);
		if (workResponse.isInWorkBundle()) {
			// only get here if someone's hacking the url
			return "redirect:/assignments";
		}
		Work work = workResponse.getWork();

		final ExtendedUserDetails currentUser = getCurrentUser();
		final boolean isDispatcher = containsAny(workResponse.getAuthorizationContexts(), AuthorizationContext.DISPATCHER);
		final boolean isAssignToFirstToAcceptVendor = isDispatcher ? vendorService.getAssignToFirstToAcceptVendorIdsByWork(work.getId()).contains(currentUser.getCompanyId()) : false;
		MessageBundle messages = messageHelper.newFlashBundle(flash);

		if (work.getStatus().getCode().equals(WorkStatusType.DRAFT)) {
			// Make sure we have all the valid assignment fields filled out before allowing them to send to a resource.

			WorkSaveRequest workSaveRequest = new WorkSaveRequest()
					.setUserId(currentUser.getId())
					.setWork(work);

			List<ConstraintViolation> violations = workSaveRequestValidator.getConstraintViolations(workSaveRequest);

			if (!violations.isEmpty()) {
				messageHelper.addError(messages, "work.form.incomplete");
				return "redirect:/assignments/{workNumber}/edit";
			}
		}

		if (!containsAny(workResponse.getAuthorizationContexts(), AuthorizationContext.BUYER, AuthorizationContext.ADMIN, AuthorizationContext.DISPATCHER)) {
			messageHelper.addError(messages, "work.form.not_authorized");
			return "redirect:/error/no_access";
		}

		if (!containsAny(work.getStatus().getCode(), WorkStatusType.DRAFT, WorkStatusType.SENT, WorkStatusType.DECLINED)) {
			messageHelper.addError(messages, "work.form.invalid_status");
			return "redirect:/assignments";
		}

		if (getCurrentUser().getCompanyIsLocked()) {
			messageHelper.addNotice(messages, "work.form.company_locked");
		}

		boolean isInternal = PricingStrategyType.INTERNAL.ordinal() == (work.getPricing().getId() - 1L);
		model.addAttribute("pricing_internal", isInternal);

		ManageMyWorkMarket mmw = companyService.getManageMyWorkMarket(getCurrentUser().getCompanyId());

		Collection<String> workerNumbers = extract(work.getResources(), on(Resource.class).getUser().getUserNumber());
		model.addAttribute("worker_numbers", jsonService.toJson(workerNumbers));

		Collection<Resource> declinedResources =
			filter(having(on(Resource.class).isSetDeclinedOn()), work.getResources());
		Collection<String> declinedWorkedNumbers =
			extract(declinedResources, on(Resource.class).getUser().getUserNumber());
		model.addAttribute("declined_worker_numbers", jsonService.toJson(declinedWorkedNumbers));

		Collection<Resource> appliedResources =
			filter(having(on(Resource.class).isSetPendingNegotiation()), work.getResources());
		Collection<String> appliedWorkerNumbers =
			extract(appliedResources, on(Resource.class).getUser().getUserNumber());
		model.addAttribute("applied_worker_numbers", jsonService.toJson(appliedWorkerNumbers));

		model.addAttribute("is_admin", getCurrentUser().hasAnyRoles("ACL_ADMIN", "ACL_MANAGER"));

		List<String> existingVendorNumbers = vendorService.getNotDeclinedVendorNumbersByWork(workResponse.getWork().getId());
		model.addAttribute("existingVendorNumbers", jsonService.toJson(existingVendorNumbers));

		List<String> declinedVendorNumbers = vendorService.getDeclinedVendorNumbersByWork(workResponse.getWork().getId());
		model.addAttribute("declinedVendorNumbers", jsonService.toJson(declinedVendorNumbers));

		if (workResponse.isWorkBundle()) {
			model.addAttribute("expanded_search_radius", true);
		}

		model.addAttribute("work", work);
		model.addAttribute("companyIsLocked", getCurrentUser().getCompanyIsLocked());
		model.addAttribute("expand_industries", work.isSetIndustry());
		model.addAttribute("instant_worker_pool", mmw.isInstantWorkerPoolEnabled());
		model.addAttribute("states", invariantService.getStateDTOs());
		model.addAttribute("isDispatch", isDispatcher);
		model.addAttribute("isAssignToFirstToAcceptVendor", isAssignToFirstToAcceptVendor);
		model.addAttribute("isBundle", workResponse.isWorkBundle());
		String workJson;
		try {
			workJson = jsonService.toJson(work, "[]");
		} catch (Exception e) {
			workJson = "{}";
		}
		model.addAttribute("work_encoded", workJson);
		model.addAttribute("preferences", searchPreferencesService.get(getCurrentUser().getId()).get("search_preferences"));

		return "web/pages/assignments/contact";
	}
}

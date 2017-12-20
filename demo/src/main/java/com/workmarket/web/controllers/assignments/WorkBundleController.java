package com.workmarket.web.controllers.assignments;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.acl.Permission;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.WorkBundle;
import com.workmarket.domains.work.service.WorkValidationService;
import com.workmarket.domains.work.service.actions.WorkListFetcherService;
import com.workmarket.domains.work.service.route.WorkBundleRouting;
import com.workmarket.service.business.VendorService;
import com.workmarket.service.business.dto.WorkBundleDTO;
import com.workmarket.service.business.wrapper.AcceptWorkResponse;
import com.workmarket.service.helpers.ServiceResponseBuilder;
import com.workmarket.service.infra.business.SuggestionService;
import com.workmarket.service.infra.dto.WorkBundleSuggestionDTO;
import com.workmarket.service.infra.security.WorkContext;
import com.workmarket.service.search.work.WorkSearchService;
import com.workmarket.service.thrift.transactional.work.WorkRequestInfo;
import com.workmarket.service.thrift.work.WorkResponseAuthorization;
import com.workmarket.service.thrift.work.WorkResponseContextBuilder;
import com.workmarket.thrift.work.WorkActionException;
import com.workmarket.thrift.work.WorkRequest;
import com.workmarket.thrift.work.WorkResponse;
import com.workmarket.web.exceptions.HttpException401;
import com.workmarket.web.exceptions.HttpException404;
import com.workmarket.web.forms.work.WorkForm;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.helpers.WorkBundleValidationHelper;
import com.workmarket.web.helpers.WorkBundleValidationResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/assignments")
public class WorkBundleController extends BaseWorkController {

	private static final Log logger = LogFactory.getLog(WorkBundleController.class);

	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private SuggestionService suggestionService;
	@Autowired private WorkSearchService workSearchService;
	@Autowired private WorkBundleValidationHelper workBundleValidationHelper;
	@Autowired private WorkValidationService workValidationService;
	@Autowired private WorkBundleRouting workBundleRouting;
	@Autowired private WorkListFetcherService workListFetcherService;
	@Autowired private WorkResponseContextBuilder workResponseContextBuilder;
	@Autowired private VendorService vendorService;

	@RequestMapping(
		value = "/suggest_work_bundle.json",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public void getSuggestedBundles(
		@RequestParam("term") String term,
		Model model) throws Exception {

		List<Map<String, String>> response = Lists.newArrayList();
		for (WorkBundleSuggestionDTO dto : suggestionService.suggestWorkBundle(term, getCurrentUser().getId())) {
			response.add(ImmutableMap.of(
				"id", String.valueOf(dto.getId()),
				"workNumber", dto.getWorkNumber(),
				"title", dto.getTitle()
			));
		}
		model.addAttribute("response", response);
	}

	@RequestMapping(
		value = "/get_all_draft_bundles.json",
		method = GET, produces = APPLICATION_JSON_VALUE)
	public void getAllBundles(Model model) throws Exception {

		List<Map<String, String>> response = Lists.newArrayList();
		List<WorkBundle> workBundles = workBundleService.findAllDraftBundles(getCurrentUser().getId());
		for (WorkBundle bundle : workBundles) {
			response.add(ImmutableMap.of(
				"id", String.valueOf(bundle.getId()),
				"workNumber", bundle.getWorkNumber(),
				"title", bundle.getTitle()
			));
		}
		model.addAttribute("response", response);
	}

	@RequestMapping(
		value = "/get_all_bundle_info.json",
		method = GET, produces = APPLICATION_JSON_VALUE)
	public void getAllBundleAddresses(
		@RequestParam("bundle_id") Long bundleId,
		Model model) throws Exception {

		ServiceResponseBuilder response = workBundleService.getWorkWithLocations(bundleId);
		model.addAttribute("response", response.getData());
	}

	@RequestMapping(
		value = "/create_bundle",
		method = POST,
		consumes = APPLICATION_JSON_VALUE,
		produces = APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyRole('PERMISSION_MANAGEMYWORK', 'PERMISSION_MANAGECOWORK', 'PERMISSION_REPORTCOWORK', 'PERMISSION_REPORTMYWORK')")
	public @ResponseBody AjaxResponseBuilder createBundle(@RequestBody WorkBundleDTO workBundleDTO) {

		AjaxResponseBuilder response = new AjaxResponseBuilder();

		workBundleDTO.setWorkNumbers(workListFetcherService.fetchWorkNumbers(getCurrentUser().getId(), workBundleDTO.getWorkNumbers()));
		long workCount = workBundleDTO.getId() == null ?
			0 : workBundleService.getAllWorkIdsInBundle(workBundleDTO.getId()).size();
		workBundleDTO.setWorkCount(workCount);

		WorkBundleValidationResult result = workBundleValidationHelper.validateWorkBundle(workBundleDTO, getCurrentUser(), response);
		if (result.isSuccessful()) {
			response.addData("id", result.getBundle().getId());
		}

		return response;
	}

	@RequestMapping(
		value = "/remove_from_bundle/{parentId}/{workNumber}",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyRole('PERMISSION_MANAGEMYWORK', 'PERMISSION_MANAGECOWORK', 'PERMISSION_REPORTCOWORK', 'PERMISSION_REPORTMYWORK')")
	public @ResponseBody AjaxResponseBuilder removeFromBundle(
		@PathVariable("parentId") Long parentId,
		@PathVariable("workNumber") String workNumber) {

		AjaxResponseBuilder response = new AjaxResponseBuilder();

		List<WorkContext> workContexts = workService.getWorkContext(parentId, getCurrentUser().getId());

		// ONLY owners can remove work. Maybe should be others?
		if (
			workContexts.contains(WorkContext.OWNER) ||
				(workContexts.contains(WorkContext.COMPANY_OWNED) && authenticationService.authorizeUserByAclPermission(getCurrentUser().getId(), Permission.VIEW_AND_MANAGE_MY_ASSIGNMENTS)) ||
				getCurrentUser().hasAnyRoles("ROLE_INTERNAL")
			) {

			Long workId = workService.findWorkId(workNumber);

			if (!workBundleRouting.isWorkBundlePendingRouting(parentId)) {
				workBundleService.removeFromBundle(parentId, workId);
				workSearchService.reindexWorkAsynchronous(workId);
				response.setSuccessful(true);
			} else {
				messageHelper.addMessage(response.setSuccessful(false), "assignment_bundle.remove.fail.pending_routing");
			}
		} else {
			messageHelper.addMessage(response.setSuccessful(false), "assignment_bundle.view.unauthorized");
		}

		return response;
	}

	// this endpoint is used to re-accept work in a bundle that failed to accept when the bundle was accepted
	@RequestMapping(
		value = "/accept_to_bundle/{parentId}/{workNumber}",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyRole('PERMISSION_MANAGEMYWORK', 'PERMISSION_MANAGECOWORK', 'PERMISSION_REPORTCOWORK', 'PERMISSION_REPORTMYWORK')")
	public @ResponseBody AjaxResponseBuilder acceptToBundle(
		@PathVariable("parentId") Long parentId,
		@PathVariable("workNumber") String workNumber) {

		AjaxResponseBuilder response = new AjaxResponseBuilder();

		Work parent = workService.findWork(parentId);

		if (!parent.getWorkStatusType().getCode().equals((WorkStatusType.ACTIVE))) {
			messageHelper.addMessage(response.setSuccessful(false), "assignment_bundle.accept_assignment.invalid");
			return response;
		}

		WorkResource worker = workService.findActiveWorkResource(parent.getId());
		Long workerId = worker.getUser().getId();

		List<WorkContext> workContexts = workService.getWorkContext(parentId, getCurrentUser().getId());

		if (
			workContexts.contains(WorkContext.OWNER) ||
				(workContexts.contains(WorkContext.COMPANY_OWNED) && authenticationService.authorizeUserByAclPermission(getCurrentUser().getId(), Permission.VIEW_AND_MANAGE_MY_ASSIGNMENTS)) ||
				getCurrentUser().hasAnyRoles("ROLE_INTERNAL")
			) {

			Long workId = workService.findWorkId(workNumber);

			AcceptWorkResponse acceptWorkResponse = tWorkFacadeService.acceptWork(workerId, workId);
			if (acceptWorkResponse.isSuccessful()) {
				response.setSuccessful(true);
			} else {
				messageHelper.addMessage(response.setSuccessful(false), acceptWorkResponse.getMessages().get(0));
			}
		} else {
			messageHelper.addMessage(response.setSuccessful(false), "assignment_bundle.accept_assignment.unauthorized");
		}

		return response;
	}

	@RequestMapping(
		value = "/view_bundle/{parentId}",
		method = GET)
	@PreAuthorize("hasAnyRole('PERMISSION_MANAGEMYWORK', 'PERMISSION_MANAGECOWORK', 'PERMISSION_REPORTCOWORK', 'PERMISSION_REPORTMYWORK')")

	public String viewBundle(@PathVariable Long parentId, ModelMap model, HttpServletRequest request) {
		Work parent = workService.findWork(parentId);

		if (parent == null) {
			throw new HttpException404().setMessageKey("assignment.details.notfound").setRedirectUri("redirect:/assignments");
		}

		model.addAttribute("parent", parent);

		String workNumber = parent.getWorkNumber();

		ExtendedUserDetails user = getCurrentUser();
		if (!workBundleService.authorizeBundleView(parent.getId(), user)) {
			throw new HttpException401()
				.setMessageKey("assignment_bundle.view.unauthorized")
				.setRedirectUri("redirect:/assignments");
		}

		if (!workBundleService.authorizeBundlePendingRouting(parentId, user.getId())) {
			throw new HttpException401()
				.setMessageKey("assignment_bundle.view.fail.pending_routing")
				.setRedirectUri("redirect:/assignments");
		}

		WorkRequest workRequest = new WorkRequest(user.getId(), workNumber, ImmutableSet.of(WorkRequestInfo.NOTES_INFO));

		try {
			WorkResponse workResponse = tWorkFacadeService.findWorkDetailLight(workRequest);
			detailsModelHelper(workNumber, model, request, workResponse);
		} catch (WorkActionException e) {
			String message = String.format("Issue for work number %s and userId %d", workNumber, user.getId());
			logger.error(message, e);
		}

		WorkForm form = new WorkForm();

		form.setId(parentId);
		form.setAssign_to_first_resource(parent.isAssignToFirstResourceEnabled());
		form.setShow_in_feed(parent.isShownInFeed());

		model.addAttribute("form", form);
		model.addAttribute("is_bundle", true);
		model.addAttribute("google_api_key", Constants.GOOGLE_API_KEY_RESTRICTED_REFERRERS);
		model.addAttribute("isAcceptableOrApplyable", workValidationService.isWorkResourceValidForWork(getCurrentUser().getId(), getCurrentUser().getCompanyId(), parent.getCompany().getId()));

		List<WorkContext> workContexts = workService.getWorkContext(parentId, user.getId());
		model.addAttribute("is_dispatcher", workContexts.contains(WorkContext.DISPATCHER));

		model.addAttribute("routable_groups", getRoutableGroups());
		model.addAttribute("mmw", parent.getManageMyWorkMarket());
		model.addAttribute("hasInvitedAtLeastOneVendor", vendorService.hasInvitedAtLeastOneVendor(parent.getId()));

		return "web/pages/assignments/bundles/view_bundle";
	}

	@RequestMapping(
		value = "/bundle_overview/{parentId}",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder bundleOverview(
		@PathVariable("parentId") Long parentId) {

		ServiceResponseBuilder serviceResponse = workBundleService.getBundleData(getCurrentUser().getId(), parentId);
		AjaxResponseBuilder response = new AjaxResponseBuilder();

		response.setSuccessful(serviceResponse.isSuccessful());
		response.setData(serviceResponse.getData());
		response.setMessages(serviceResponse.getMessages());

		return response;
	}
}

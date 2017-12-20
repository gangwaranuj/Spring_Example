package com.workmarket.web.controllers.assignments;

import com.google.common.collect.ImmutableSet;
import com.workmarket.api.v2.worker.model.GeoPoint;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.search.worker.VendorHydrator;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.service.business.VendorService;
import com.workmarket.service.infra.security.WorkContext;
import com.workmarket.service.thrift.transactional.work.WorkRequestInfo;
import com.workmarket.thrift.work.AuthorizationContext;
import com.workmarket.thrift.work.Work;
import com.workmarket.thrift.work.WorkResponse;
import com.workmarket.utility.CollectionUtilities;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Controller for generating the Vendors tab on the assignment detail page
 */
@Controller
@RequestMapping("/assignments")
public class WorkDetailsVendorController extends BaseWorkController {

	@Autowired VendorService vendorService;
	@Autowired VendorHydrator vendorHydrator;

	@RequestMapping(
		value = "/{workNumber}/vendors",
		method = GET)
	public @ResponseBody
	Map<String, Object> getVendors(
		@PathVariable String workNumber) throws Exception {

		final ExtendedUserDetails currentUser = getCurrentUser();
		final WorkResponse workResponse;
		try {
			workResponse = getWork(workNumber, ImmutableSet.of(
				WorkRequestInfo.CONTEXT_INFO,
				WorkRequestInfo.LOCATION_INFO
			), ImmutableSet.of(
				AuthorizationContext.ADMIN
			), "vendor_tab");
		} catch (Exception e) {
			return CollectionUtilities.newObjectMap();
		}

		List<Long> invitedVendors = vendorService.getNotDeclinedVendorIdsByWork(workResponse.getWork().getId());
		List<Long> declinedVendors = vendorService.getDeclinedVendorIdsByWork(workResponse.getWork().getId());
		List<Long> assignToFirstToAcceptVendors = vendorService.getAssignToFirstToAcceptVendorIdsByWork(workResponse.getWork().getId());
		if (!declinedVendors.isEmpty()) {
			invitedVendors.addAll(declinedVendors);
		}
		if (CollectionUtils.isEmpty(invitedVendors)) {
			return CollectionUtilities.newObjectMap();
		}

		Work work = workResponse.getWork();
		GeoPoint.Builder workLocation = new GeoPoint.Builder();
		if (work.getLocation() != null && work.getLocation().getAddress() != null && work.getLocation().getAddress().getPoint() != null) {
			workLocation.withLatitude(work.getLocation().getAddress().getPoint().getLatitude());
			workLocation.withLongitude(work.getLocation().getAddress().getPoint().getLongitude());
		}
		List<Map<String, Object>> results = vendorHydrator.hydrateVendors(invitedVendors, workLocation.build(), currentUser);
		decorateBooleanField(results, "declined", declinedVendors);
		decorateBooleanField(results, "assign_to_first_to_accept", assignToFirstToAcceptVendors);

		return CollectionUtilities.newObjectMap(
			"results", results,
			"total_results", invitedVendors.size()
		);
	}

	@RequestMapping(
		value = "/{workNumber}/vendors/{companyNumber}/removeautoassign",
		method = POST)
	@ResponseBody
	public Map<String, Object>  removeAutoAssignLabel(
			@PathVariable String workNumber,
			@PathVariable String companyNumber) {

		ExtendedUserDetails user = getCurrentUser();
		AbstractWork work = workService.findWorkByWorkNumber(workNumber);

		List<WorkContext> contexts = workService.getWorkContext(work.getId(), user.getId());
		if (!contexts.contains(WorkContext.OWNER) &&
				!(contexts.contains(WorkContext.COMPANY_OWNED) && user.hasAnyRoles("ACL_ADMIN", "ACL_DEPUTY"))) {
			return CollectionUtilities.newObjectMap("error", messageHelper.getMessage("assignment.resources.remove_auto_assign_label.not_authorized"));
		}

		Company vendor = companyService.findCompanyByNumber(companyNumber);
		vendorService.removeAutoAssign(vendor.getId(), work.getId());
		return CollectionUtilities.newObjectMap("success", messageHelper.getMessage("assignment.resources.remove_auto_assign_label.successful", vendor.getEffectiveName()));
	}

	private void decorateBooleanField(final List<Map<String, Object>> results, final String field, final List<Long> idsSetToTrue) {
		for (Map<String, Object> result : results) {
			Long vendorId = (Long) result.get("id");
			if (idsSetToTrue.contains(vendorId)) {
				result.put(field, true);
			}
		}
	}
}

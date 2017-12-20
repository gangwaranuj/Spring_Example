package com.workmarket.web.controllers.companies;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.asset.UserAssetAssociation;
import com.workmarket.domains.reports.model.CustomReportCustomFieldGroupDTO;
import com.workmarket.service.business.CustomFieldService;
import com.workmarket.service.business.UserService;
import com.workmarket.web.controllers.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@RequestMapping("/companies")
public class CompaniesController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(CompaniesController.class);

	@Autowired
	private CustomFieldService customFieldService;
	@Autowired
	private UserService userService;

	@RequestMapping(
		value = "/{companyId}/reports/{reportId}/custom_field_groups",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody List<CustomReportCustomFieldGroupDTO> getCustomFieldGroups(@PathVariable Long companyId, @PathVariable Long reportId) {
		return customFieldService.findCustomReportCustomFieldGroupsForCompanyAndReport(companyId, reportId);
	}

	@RequestMapping(
		value = "/{companyId}/employees",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody List<Map<String, Object>> getEmployees(@PathVariable Long companyId, HttpServletResponse response) {
		final List<Map<String, Object>> results;

		if (getCurrentUser() != null &&
			getCurrentUser().getCompanyId() != null &&
			getCurrentUser().getCompanyId().equals(companyId)) {

			final List<User> users = userService.findAllActiveEmployees(companyId);
			results = Lists.newArrayListWithExpectedSize(users.size());

			for (User user : users) {
				if (!user.isApiEnabled()) {
					final UserAssetAssociation avatar = userService.findUserAvatars(user.getId());
					results.add(new ImmutableMap.Builder<String, Object>()
							.put("id", user.getUserNumber())
							.put("firstName", user.getFirstName())
							.put("lastName", user.getLastName())
							.put("fullName", user.getFullName())
							.put("thumbnail", avatar != null && avatar.getAsset() != null ? avatar.getAsset().getDownloadableUri() : "")
							.put("isCurrentUser", getCurrentUser().getId().equals(user.getId()))
							.build());
				}

			}
		} else {
			results = Lists.newArrayList();
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			logger.error(String.format("Unauthorized request for companyId %d", companyId));
		}

		return results;
	}
}

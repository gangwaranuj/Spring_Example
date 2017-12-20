package com.workmarket.web.controllers.requirementsets;

import com.workmarket.domains.groups.model.UserGroupPagination;
import com.workmarket.service.business.UserGroupSerializationService;
import com.workmarket.service.business.UserGroupService;
import com.workmarket.web.controllers.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by ianha on 12/26/13
 */
@Controller
@RequestMapping("/group_memberships")
public class GroupMembershipsController extends BaseController {
	@Autowired private UserGroupService userGroupservice;
	@Autowired private UserGroupSerializationService jsonService;

	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody String list() {
		UserGroupPagination pagination = new UserGroupPagination();
		pagination.setStartRow(0);
		pagination.setReturnAllRows(true);

		UserGroupPagination result = userGroupservice.findAllGroupsByCompanyId(getCurrentUser().getCompanyId(), pagination);

		return jsonService.toJson(result.getResults());
	}
}

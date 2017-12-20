package com.workmarket.web.controllers.projects;

import com.workmarket.domains.work.service.project.ProjectBudgetService;
import com.workmarket.domains.work.service.project.ProjectService;
import com.workmarket.service.infra.security.RequestContext;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.exceptions.HttpException401;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by nick on 2013-01-09 4:54 PM
 */
public class BaseProjectsController extends BaseController {

	@Autowired protected ProjectService projectService;
	@Autowired protected ProjectBudgetService projectBudgetService;
	@Autowired protected MessageBundleHelper messageHelper;

	protected void authorize(Long projectId) {
		List<RequestContext> contexts = projectService.getRequestContext(projectId);

		if (!CollectionUtilities.containsAny(contexts, RequestContext.OWNER, RequestContext.COMPANY_OWNED)) {
			throw new HttpException401()
					.setMessageKey("projects.not_authorized")
					.setRedirectUri("redirect:/projects");
		}
	}
}

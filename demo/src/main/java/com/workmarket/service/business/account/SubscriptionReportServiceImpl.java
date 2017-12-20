package com.workmarket.service.business.account;

import com.workmarket.service.infra.business.UserRoleService;
import com.workmarket.dao.report.subscription.SubscriptionReportDAO;
import com.workmarket.domains.model.reporting.subscriptions.SubscriptionReportPagination;
import com.workmarket.service.infra.business.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class SubscriptionReportServiceImpl implements SubscriptionReportService {

	private @Autowired SubscriptionReportDAO subscriptionReportDAO;
	private @Autowired AuthenticationService authenticationService;
	private @Autowired UserRoleService userRoleService;

	@Override
	public SubscriptionReportPagination getStandardReport(SubscriptionReportPagination pagination) {
		Assert.notNull(pagination);
		Assert.isTrue(userRoleService.isInternalUser(authenticationService.getCurrentUser()), "Unauthorized user");
		pagination = subscriptionReportDAO.getStandardReport(pagination);
		pagination.setSubscriptionAggregate(subscriptionReportDAO.getSubscriptionAggregateReport(pagination));
		return pagination;
	}

	@Override
	public SubscriptionReportPagination getUsageReport(SubscriptionReportPagination pagination) {
		Assert.notNull(pagination);
		Assert.isTrue(userRoleService.isInternalUser(authenticationService.getCurrentUser()), "Unauthorized user");
		return subscriptionReportDAO.getUsageReport(pagination);
	}
}

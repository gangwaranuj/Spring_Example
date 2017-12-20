package com.workmarket.service.business.scheduler;

import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.work.model.WorkBundle;
import com.workmarket.domains.work.service.WorkBundleService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.infra.business.AuthenticationService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * User: micah
 * Date: 1/8/15
 * Time: 4:47 PM
 */
@Service
@ManagedResource(objectName = "bean:name=workBundles", description = "work bundle tasks")
public class WorkBundleProcessor {
	@Qualifier("jdbcTemplate") @Autowired private NamedParameterJdbcTemplate jdbcTemplate;
	@Autowired UserService userService;
	@Autowired AuthenticationService authenticationService;
	@Autowired WorkBundleService workBundleService;

	private static final Logger logger = LoggerFactory.getLogger(WorkBundleProcessor.class);

	@ManagedOperation(description = "updateAllWorkBundleCompleteStatus")
	public void updateAllWorkBundleCompleteStatus() {
		User currentUser = userService.findUserById(Constants.WORKMARKET_SYSTEM_USER_ID);
		authenticationService.setCurrentUser(currentUser);

		List<WorkBundle> bundles = workBundleService.findAllBundlesByStatus(WorkStatusType.ACTIVE);
		if (CollectionUtils.isEmpty(bundles)) { return; }
		for (WorkBundle bundle : bundles) {
			boolean complete = workBundleService.updateBundleComplete(bundle.getId());
			if (complete) { logger.info("Bundle: " + bundle.getId() + " is complete."); }
		}
	}

	@ManagedOperation(description = "updateAllWorkBundleVoidStatus")
	public void updateAllWorkBundleVoidStatus() {
		User currentUser = userService.findUserById(Constants.WORKMARKET_SYSTEM_USER_ID);
		authenticationService.setCurrentUser(currentUser);

		String sql =
			"SELECT id " +
			"FROM work " +
			"WHERE type='B' AND work_status_type_code <> 'void' AND work_status_type_code <> 'complete' "+
			"AND id NOT IN (SELECT DISTINCT bp.id FROM work bp JOIN work bc ON bc.parent_id = bp.id)";

		List<Long> emptyBundles = jdbcTemplate.queryForList(sql, new MapSqlParameterSource(), Long.class);

		if (CollectionUtils.isEmpty(emptyBundles)) { return; }

		for (Long parentId : emptyBundles) {
			boolean voidWorked = workBundleService.updateBundleVoid(parentId);
			if (voidWorked) { logger.info("Bundle: " + parentId + " has been voided."); }
		}
	}
}

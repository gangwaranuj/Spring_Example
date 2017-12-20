package com.workmarket.service.business.scheduler;

import com.workmarket.service.infra.business.AuthenticationService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;

@Service
@ManagedResource(objectName="bean:name=refreshAllActiveSessions", description="Refreshes all active sessions")
public class RefreshAllActiveSessionsExecutor {

	@Autowired private AuthenticationService authenticationService;

	private static final Log logger = LogFactory.getLog(RefreshAllActiveSessionsExecutor.class);

	@SuppressWarnings("unchecked")
	@ManagedOperation(description = "Refresh All Active Sessions")
	public void execute() {
		DateTime now = new DateTime(DateTimeZone.UTC);
		Integer hour = now.get(DateTimeFieldType.hourOfDay());

		logger.info(String.format("******  Running RefreshAllActiveSessionsExecutor for time slot %d (UTC)", hour));

		authenticationService.refreshSessionForAll();
	}
}

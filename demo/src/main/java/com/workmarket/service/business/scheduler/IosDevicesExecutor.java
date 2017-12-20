package com.workmarket.service.business.scheduler;

import com.workmarket.service.infra.communication.PushAdapter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
@ManagedResource(objectName="bean:name=iosDevicesExecutor", description="check apple push devices")
public class IosDevicesExecutor implements ScheduledExecutor {

	private static final Log logger = LogFactory.getLog(IosDevicesExecutor.class);

	@Autowired private PushAdapter pushAdapter;

	@ManagedOperation(description = "check apple push devices")
	public void execute() {
		logger.debug("[alert] IosDevicesExecutor: start");
		Map<String, Date> unusedDevices = pushAdapter.removeUnusedIos();
		logger.debug(String.format("[alert] removing %s devices: %s", unusedDevices.size(), unusedDevices.keySet()));
		logger.debug("[alert] IosDevicesExecutor: done");
	}
}
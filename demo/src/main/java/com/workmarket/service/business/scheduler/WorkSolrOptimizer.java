package com.workmarket.service.business.scheduler;

import com.workmarket.data.solr.indexer.work.WorkIndexer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;

/**
 * Author: rocio
 */
@Service
@ManagedResource(objectName="bean:name=workSolrOptimizer", description="optimizer for solr work")
public class WorkSolrOptimizer implements ScheduledExecutor {

	@Autowired private WorkIndexer workIndexer;

	@Override
	@ManagedOperation(description = "optimizer for solr work")
	public void execute() {
		workIndexer.optimize();
	}
}

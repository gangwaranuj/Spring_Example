package com.workmarket.service.business.scheduler;

import com.workmarket.data.solr.indexer.work.WorkIndexer;
import com.workmarket.data.solr.indexer.work.WorkIndexerReconcile;
import com.workmarket.utility.DateUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;

import java.util.Calendar;

/**
 * Author: rocio
 */
@Service
@ManagedResource(objectName="bean:name=solrIndexReconciliation", description="reconcile mismatches in the solr index")
public class SolrIndexReconciliation implements ScheduledExecutor {

	@Autowired private WorkIndexer workIndexer;
	@Autowired private WorkIndexerReconcile workIndexerReconcile;

	@Override
	@ManagedOperation(description = "reconcile work index")
	public void execute() {
		workIndexerReconcile.reconcileWork();
	}

	public void reindex() {
		workIndexer.reindexWorkByLastModifiedDate(DateUtilities.getMidnightYesterday());
	}

	public void reindexLastHour() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR, -1);
		workIndexer.reindexWorkByLastModifiedDate(calendar);
	}
}

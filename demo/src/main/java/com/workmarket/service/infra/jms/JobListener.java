package com.workmarket.service.infra.jms;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.workmarket.common.metric.WMMetricRegistryFacade;
import com.workmarket.domains.search.solr.SolrThreadLocal;
import com.workmarket.logging.NRTrace;
import com.workmarket.service.business.JobService;
import org.apache.activemq.command.ActiveMQBytesMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import javax.jms.Message;
import javax.jms.MessageListener;

@Service
public class JobListener implements MessageListener {

	private static final Log logger = LogFactory.getLog(JobListener.class);

	@Autowired private JobService jobService;
	@Autowired private MetricRegistry metricRegistry;
	private Meter consumeMeter;
	
	@PostConstruct
	private void init() {
		final WMMetricRegistryFacade wmMetricRegistryFacade = new WMMetricRegistryFacade(metricRegistry, "jms.listener.job");
		consumeMeter = wmMetricRegistryFacade.meter("consume");
	}

	@NRTrace(dispatcher=true)
	public void onMessage(Message message) {
		consumeMeter.mark();
		if (message instanceof ActiveMQBytesMessage) {

			try {
				ActiveMQBytesMessage am = (ActiveMQBytesMessage) message;

				String cmd = new String(am.getContent().getData());

				Assert.hasText(cmd);

				// see if we are doing a directed reindex - a directed reindex will be a message
				// of the form REINDEX_USERS:workerSearchService to direct the reindex request
				// to just the workerSearchService
				String[] cmdArray = cmd.split(":");
				cmd = cmdArray[0];
				String directedTowards = cmdArray.length == 1 ? null : cmdArray[1];
				SolrThreadLocal.setDirectedTowards(directedTowards);

				switch (cmd) {
					case JobService.PROCESS_PENDING_LANE_REMOVALS:
						jobService.processPendingLaneRemovals();
						break;
					case JobService.REINDEX_ALL_DATA:
						jobService.reindexAllData();
						break;
					case JobService.REINDEX_USERS:
						jobService.reindexUsers();
						break;
					case  JobService.REINDEX_GROUPS:
						jobService.reindexGroups();
						break;
					case JobService.REINDEX_WORK:
						jobService.reindexWork();
						break;
					case JobService.CLEAN_WORK_INDEX:
						jobService.pruneWork();
						break;
					case  JobService.REINDEX_6_MONTHS_WORK:
						jobService.reindexLast6MonthsWork();
						break;
					case JobService.REINDEX_VENDORS:
						jobService.reindexVendors();
				}
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			} finally {
				SolrThreadLocal.clear();
			}
		} else {
			throw new IllegalArgumentException("Message must be of type ActiveMQBytesMessage");
		}
	}
}
package com.workmarket.data.solr.indexer;

import com.workmarket.domains.work.dao.WorkResourceDAO;
import com.workmarket.dao.customfield.WorkCustomFieldDAO;
import com.workmarket.dao.state.WorkSubStatusDAO;
import com.workmarket.domains.work.dao.RoutingStrategyDAO;
import com.workmarket.domains.work.dao.WorkNegotiationDAO;
import com.workmarket.data.report.work.CustomFieldReportFilters;
import com.workmarket.data.solr.indexer.work.SolrWorkDataDecorator;
import com.workmarket.data.solr.model.SolrWorkData;
import com.workmarket.domains.work.dao.WorkVendorInvitationDAO;
import com.workmarket.domains.work.service.follow.WorkFollowService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.util.collections.Sets;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SolrWorkDataDecoratorTest {

	@Mock private WorkResourceDAO workResourceDAO;
	@Mock private WorkSubStatusDAO workSubStatusDAO;
	@Mock private WorkCustomFieldDAO workCustomFieldDAO;
	@Mock private WorkFollowService workFollowService;
	@Mock private WorkNegotiationDAO workNegotiationDAO;
	@Mock private RoutingStrategyDAO routingStrategyDAO;
	@Mock private WorkVendorInvitationDAO workVendorInvitationDAO;
	@InjectMocks SolrWorkDataDecorator solrWorkDataDecorator;

	@Before
	public void setUp() throws Exception {

	}

	@Test
	public void decorate_withEmptySetOfWorks() {
		Collection<SolrWorkData> emptySet = Collections.EMPTY_SET;
		Collection<SolrWorkData> decoratedList = solrWorkDataDecorator.decorate(Collections.EMPTY_SET);
		assertTrue(decoratedList.equals(emptySet));
	}

	@Test
	public void decorate_withList() {
		SolrWorkData solrWorkData = mock(SolrWorkData.class);
		Collection<SolrWorkData> workToDecorate = Sets.newSet(solrWorkData);
		Collection<SolrWorkData> decoratedList = solrWorkDataDecorator.decorate(workToDecorate);
		assertTrue(decoratedList.size() == 1);

		verify(workCustomFieldDAO, times(1)).getWorkCustomFieldsMap(any(CustomFieldReportFilters.class));
		verify(workSubStatusDAO, times(1)).findAllUnresolvedSubStatusType(anyList());
		verify(routingStrategyDAO, times(1)).findAllGroupsRoutedByWork(anyList());
		verify(workVendorInvitationDAO, times(1)).getNotDeclinedVendorIdsByWork(anyLong());
	}

	@Test
	public void decorate() {
		SolrWorkData solrWorkData = mock(SolrWorkData.class);
		solrWorkDataDecorator.decorate(solrWorkData);

		verify(workCustomFieldDAO, times(1)).getWorkCustomFieldsMap(any(CustomFieldReportFilters.class));
		verify(workSubStatusDAO, times(1)).findAllUnresolvedSubStatusType(anyList());
		verify(routingStrategyDAO, times(1)).findAllGroupsRoutedByWork(anyList());
		verify(workVendorInvitationDAO, times(1)).getNotDeclinedVendorIdsByWork(anyLong());
	}
}

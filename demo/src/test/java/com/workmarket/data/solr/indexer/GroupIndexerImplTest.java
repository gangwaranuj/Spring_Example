package com.workmarket.data.solr.indexer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.workmarket.domains.search.group.indexer.dao.SolrGroupDAO;
import com.workmarket.domains.search.group.indexer.service.GroupIndexerImpl;
import com.workmarket.domains.search.group.indexer.model.GroupSolrData;
import com.workmarket.service.business.UserGroupService;
import com.workmarket.service.business.event.Event;
import com.workmarket.service.business.event.group.GroupUpdateSearchIndexEvent;
import com.workmarket.service.infra.event.EventRouter;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.ConcurrentUpdateSolrServer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.anyCollection;
import static org.mockito.Mockito.anyListOf;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.any;

@RunWith(MockitoJUnitRunner.class)
public class GroupIndexerImplTest {

	@Mock SolrGroupDAO solrGroupDAO;
	@Mock EventRouter eventRouter;
	@Mock UserGroupService userGroupService;
	@Mock ConcurrentUpdateSolrServer groupSolrServer;

	@InjectMocks GroupIndexerImpl groupIndexer;

	final private String UUID_ONE = "UUID_ONE", UUID_TWO = "UUID_TWO";
	final private Long ID_ONE = 1L, ID_TWO = 2L;
	private List<String> uuidsToIndex;
	Map<String,Long> uuidIdPairMap;

	@Before
	public void setUp() throws Exception {
		groupIndexer.setBufferSize(100);
		when(solrGroupDAO.getMaxGroupId()).thenReturn(1000);

		uuidsToIndex = ImmutableList.of(UUID_ONE, UUID_TWO);
		uuidIdPairMap = ImmutableMap.of(UUID_ONE, ID_ONE, UUID_TWO, ID_TWO);
		when(userGroupService.findUserGroupUuidIdPairsByUuids(uuidsToIndex)).thenReturn(uuidIdPairMap);
	}

	@Test
	public void reindexAllGroups_success() {
		groupIndexer.reindexAll();
		verify(eventRouter, times(10)).sendEvent(any(Event.class));
	}

	@Test
	public void reindexGroups_withNothingToIndex_success() throws IOException, SolrServerException {
		groupIndexer.reindexById(Lists.newArrayList(1L, 4L, 67L));
		verify(solrGroupDAO, times(1)).getSolrDataById(anyListOf(Long.class));
		verify(groupSolrServer, never()).addBeans(anyCollection(), anyInt());
	}

	@Test
	public void reindexGroups_success() throws IOException, SolrServerException {
		GroupSolrData group = new GroupSolrData();
		group.setId(1L);
		group.setName("Group Name");
		List<GroupSolrData> userGroupList = Lists.newArrayList(group);
		when(solrGroupDAO.getSolrDataById(anyListOf(Long.class))).thenReturn(userGroupList);

		groupIndexer.reindexById(Lists.newArrayList(1L, 4L, 67L));
		verify(solrGroupDAO, times(1)).getSolrDataById(anyListOf(Long.class));
		verify(groupSolrServer, times(1)).addBeans(anyCollection(), anyInt());
	}

	@Test
	public void reindexByUUID_UUIDsProvided_success() {
		groupIndexer.reindexByUUID(uuidsToIndex);

		verify(userGroupService, times(1)).findUserGroupUuidIdPairsByUuids(uuidsToIndex);
		verify(eventRouter, times(1)).sendEvent(any(GroupUpdateSearchIndexEvent.class));
	}

	@Test
	public void reindexByUUID_noUUIDsProvided_nothingCalled() {
		groupIndexer.reindexByUUID(Lists.<String>newArrayList());

		verify(userGroupService, never()).findUserGroupUuidIdPairsByUuids(uuidsToIndex);
		verify(eventRouter, never()).sendEvent(any(GroupUpdateSearchIndexEvent.class));
	}

	@Test
	public void reindexByUUID_nullUuidsToIndex_nothingCalled() {
		groupIndexer.reindexByUUID(null);

		verify(userGroupService, never()).findUserGroupUuidIdPairsByUuids(uuidsToIndex);
		verify(eventRouter, never()).sendEvent(any(GroupUpdateSearchIndexEvent.class));
	}

	@Test
	public void reindexByUUID_emptyUuidIdPairsMapReturned_reindexEventNotThrown() {
		when(userGroupService.findUserGroupUuidIdPairsByUuids(uuidsToIndex)).thenReturn(Maps.<String,Long>newHashMap());

		groupIndexer.reindexByUUID(uuidsToIndex);

		verify(userGroupService, times(1)).findUserGroupUuidIdPairsByUuids(uuidsToIndex);
		verify(eventRouter, never()).sendEvent(any(GroupUpdateSearchIndexEvent.class));
	}

	@Test
	public void reindexByUUID_nullUuidIdPairsMapReturned_reindexEventNotThrown() {
		when(userGroupService.findUserGroupUuidIdPairsByUuids(uuidsToIndex)).thenReturn(null);

		groupIndexer.reindexByUUID(uuidsToIndex);

		verify(userGroupService, times(1)).findUserGroupUuidIdPairsByUuids(uuidsToIndex);
		verify(eventRouter, never()).sendEvent(any(GroupUpdateSearchIndexEvent.class));
	}
}

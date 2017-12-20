package com.workmarket.data.solr.indexer.user;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.workmarket.business.talentpool.TalentPoolClient;
import com.workmarket.business.talentpool.gen.Messages.ParticipantType;
import com.workmarket.business.talentpool.gen.Messages.Status;
import com.workmarket.business.talentpool.gen.Messages.TalentPoolMembership;
import com.workmarket.business.talentpool.gen.Messages.TalentPoolMembershipList;
import com.workmarket.business.talentpool.gen.Messages.TalentPoolMembershipsRequest;
import com.workmarket.business.talentpool.gen.Messages.TalentPoolMembershipsResponse;
import com.workmarket.business.talentpool.gen.Messages.TalentPoolParticipation;
import com.workmarket.common.core.RequestContext;
import com.workmarket.data.solr.model.SolrGroupData;
import com.workmarket.data.solr.model.SolrUserData;
import com.workmarket.data.solr.model.SolrVendorData;
import com.workmarket.service.business.UserGroupService;
import com.workmarket.service.web.WebRequestContext;
import com.workmarket.service.web.WebRequestContextProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.util.collections.Sets;
import org.mockito.runners.MockitoJUnitRunner;
import rx.Observable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test cases for solrVendorData decorator.
 */
@RunWith(MockitoJUnitRunner.class)
public class SolrVendorDataDecoratorTest {
	@Mock private SolrUserDataDecorator userDataDecorator;
	@Mock private TalentPoolClient talentPoolClient;
	@Mock private WebRequestContextProvider webRequestContextProvider;
	@Mock private UserGroupService userGroupService;
	@InjectMocks private SolrVendorDataDecorator vendorDataDecorator;

	private static final String VENDOR_UUID = "VENDOR_UUID";
	private static final String TP1_UUID = "TP1_UUID";
	private static final String TP2_UUID = "TP2_UUID";
	private static final String TP3_UUID = "TP3_UUID";
	private static Long TP1_ID = 1L;
	private static Long TP2_ID = 2L;
	private static Long TP3_ID = 3L;
	private static Map<String, Long> TP_UUID_ID_PAIRS =
		ImmutableMap.of(TP1_UUID, TP1_ID, TP2_UUID, TP2_ID, TP3_UUID, TP3_ID);
	private static final TalentPoolParticipation APPROVED = TalentPoolParticipation
		.newBuilder()
		.setParticipantType(ParticipantType.VENDOR)
		.setApprovedOn(UUID.randomUUID().toString()).build();
	private static final TalentPoolParticipation INVITED = TalentPoolParticipation
		.newBuilder()
		.setParticipantType(ParticipantType.VENDOR)
		.setInvitedOn(UUID.randomUUID().toString()).build();
	private static final TalentPoolMembershipList MEMBERSHIP_LIST = TalentPoolMembershipList
		.newBuilder()
		.setParticipantUuid(VENDOR_UUID)
		.addAllTalentPoolMembership(Lists.newArrayList(
			TalentPoolMembership.newBuilder().setTalentPoolUuid(TP1_UUID).setTalentPoolParticipation(APPROVED).build(),
			TalentPoolMembership.newBuilder().setTalentPoolUuid(TP2_UUID).setTalentPoolParticipation(APPROVED).build(),
			TalentPoolMembership.newBuilder().setTalentPoolUuid(TP3_UUID).setTalentPoolParticipation(INVITED).build()))
		.build();
	private static final TalentPoolMembershipsResponse RESPONSE = TalentPoolMembershipsResponse
		.newBuilder()
		.setStatus(Status.newBuilder().setSuccess(true).setMessage("OK"))
		.addAllTalentPoolMembershipList(Lists.newArrayList(MEMBERSHIP_LIST))
		.build();

	@Before
	public void setup() {
		when(webRequestContextProvider.getWebRequestContext()).thenCallRealMethod();
		doCallRealMethod().when(webRequestContextProvider).setWebRequestContext(any(WebRequestContext.class));
		when(talentPoolClient.getMemberships(any(TalentPoolMembershipsRequest.class), any(RequestContext.class)))
			.thenReturn(Observable.just(RESPONSE));
		when(userGroupService.findUserGroupUuidIdPairsByUuids(anyCollection())).thenReturn(TP_UUID_ID_PAIRS);
	}

	@Test
	public void decorate_withNullVendorData() {
		SolrVendorData vendorData = null;
		SolrVendorData decorated = vendorDataDecorator.decorate(vendorData);
		assertNull(decorated);

	}

	@Test
	public void decorate_withEmptyCollectionOfVendorData() {
		Collection<SolrVendorData> emptySet = Collections.EMPTY_SET;
		Collection<SolrVendorData> decoratedSet = vendorDataDecorator.decorate(emptySet);
		assertTrue(decoratedSet.isEmpty());
	}

	@Test
	public void decorate_withList() {
		SolrVendorData vendorData = new SolrVendorData();
		SolrUserData userData = mock(SolrUserData.class);
		List<SolrUserData> employees = Lists.newArrayList(userData);
		vendorData.setUuid(VENDOR_UUID);
		vendorData.setEmployees(employees);
		Collection<SolrVendorData> vendorsToDecorate = Sets.newSet(vendorData);
		Collection<SolrVendorData> decoratedSet = vendorDataDecorator.decorate(vendorsToDecorate);

		assertTrue(decoratedSet.size() == 1);
		verify(userDataDecorator, times(1)).decorate(anyList());
	}

	@Test
	public void decorate() {
		SolrVendorData vendorData = new SolrVendorData();
		SolrUserData userData = mock(SolrUserData.class);
		List<SolrUserData> employees = Lists.newArrayList(userData);
		vendorData.setUuid(VENDOR_UUID);
		vendorData.setEmployees(employees);
		vendorDataDecorator.decorate(vendorData);

		verify(userDataDecorator, times(1)).decorate(anyList());
	}

	@Test
	public void decorate_withTalentPools() {
		SolrVendorData vendorData = new SolrVendorData();
		SolrUserData userData = mock(SolrUserData.class);
		List<SolrUserData> employees = Lists.newArrayList(userData);
		vendorData.setUuid(VENDOR_UUID);
		vendorData.setEmployees(employees);
		Collection<SolrVendorData> vendorsToDecorate = Sets.newSet(vendorData);
		Collection<SolrVendorData> decoratedSet = vendorDataDecorator.decorate(vendorsToDecorate);

		verify(userDataDecorator, times(1)).decorate(anyList());
		assertEquals(1, decoratedSet.size());
		assertEquals(2, vendorData.getGroupMember().size());
		assertEquals(1, vendorData.getGroupInvited().size());
		assertEquals(0, vendorData.getGroupDeclined().size());
		assertEquals(0, vendorData.getGroupPending().size());
		verifyTalentPoolDecoration(Lists.newArrayList(TP1_ID, TP2_ID), Lists.newArrayList(TP1_UUID, TP2_UUID), vendorData.getGroupMember());
		verifyTalentPoolDecoration(Lists.newArrayList(TP3_ID), Lists.newArrayList(TP3_UUID), vendorData.getGroupInvited());
	}

	private void verifyTalentPoolDecoration(
		final List<Long> expectedGroupIds,
		final List<String> expectedGroupUuids,
		final List<SolrGroupData> groupDataList
	) {
		for (SolrGroupData groupData : groupDataList) {
			assertTrue(expectedGroupIds.contains(groupData.getGroupId()));
			assertTrue(expectedGroupUuids.contains(groupData.getGroupUuid()));
		}
	}
}

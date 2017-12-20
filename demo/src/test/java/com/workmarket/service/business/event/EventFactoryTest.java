package com.workmarket.service.business.event;

import com.google.common.collect.Lists;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.groups.model.UserUserGroupAssociation;
import com.workmarket.domains.model.User;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.negotiation.WorkNegotiation;
import com.workmarket.search.request.user.GroupPeopleSearchRequest;
import com.workmarket.search.response.user.PeopleSearchResponse;
import com.workmarket.service.business.dto.CloseWorkDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EventFactoryTest {


	@InjectMocks EventFactoryImpl eventFactory;

	private Work work;
	private WorkNegotiation workNegotiation;
	private UserUserGroupAssociation userUserGroupAssociation;

	@Before
	public void setUp() throws Exception {
		work = mock(Work.class);
		workNegotiation = mock(WorkNegotiation.class);
		userUserGroupAssociation = mock(UserUserGroupAssociation.class);
		when(work.getId()).thenReturn(1L);
		when(workNegotiation.getId()).thenReturn(1L);
		when(userUserGroupAssociation.getId()).thenReturn(1L);
	}

	@Test(expected = IllegalArgumentException.class)
	public void buildInviteToGroupEvent_withNullArguments_throwsException() {
		eventFactory.buildInviteToGroupEvent(null, null, null);
	}

	@Test
	public void buildInviteToGroupEvent_success() {
		assertNotNull(eventFactory.buildInviteToGroupEvent(Lists.<Long>newArrayList(), 1L, 1L));
	}

	@Test(expected = IllegalArgumentException.class)
	public void buildInviteToGroupFromCartEvent_withNullArguments_throwsException() {
		eventFactory.buildInviteToGroupFromCartEvent(null, null, null);
	}

	@Test
	public void buildInviteToGroupFromCartEvent_success() {
		assertNotNull(eventFactory.buildInviteToGroupEvent(Lists.<Long>newArrayList(), 1L, 1L));
	}

	@Test(expected = IllegalArgumentException.class)
	public void buildWorkClosedEvent_withNullArguments_throwsException() {
		eventFactory.buildWorkClosedEvent(1L, null);
	}

	@Test
	public void buildWorkClosedEvent_success() {
		assertNotNull(eventFactory.buildWorkClosedEvent(1L, new CloseWorkDTO()));
	}

	@Test(expected = IllegalArgumentException.class)
	public void buildRoutingStrategyScheduledEvent_withNullArguments_throwsException() {
		eventFactory.buildRoutingStrategyScheduledEvent(null);
	}

	@Test
	public void buildRoutingStrategyExecuteEvent() {
		assertNotNull(eventFactory.buildExecuteRoutingStrategyGroupEvent(1L, 0));
	}

	@Test
	public void buildSearchRequestEvent_withNullArguments_returnsNull() {
		assertNull(eventFactory.buildSearchRequestEvent(null, null, 1L));
	}

	@Test
	public void buildSearchRequestEvent_withNoRequest_returnsNull() {
		assertNull(eventFactory.buildSearchRequestEvent(new GroupPeopleSearchRequest(), new PeopleSearchResponse(), 1L));
	}

	@Test(expected = IllegalArgumentException.class)
	public void buildValidateResourceCheckInScheduledEvent_withNullArguments_throwsException() {
		eventFactory.buildValidateResourceCheckInScheduledEvent(null, null);
	}

	@Test
	public void buildValidateResourceCheckInScheduledEvent_success() {
		assertNotNull(eventFactory.buildValidateResourceCheckInScheduledEvent(work, Calendar.getInstance()));
	}

	@Test(expected = IllegalArgumentException.class)
	public void buildResourceConfirmationRequiredScheduledEvent_withNullArguments_throwsException() {
		eventFactory.buildResourceConfirmationRequiredScheduledEvent(null, null);
	}

	@Test
	public void buildResourceConfirmationRequiredScheduledEvent_success() {
		assertNotNull(eventFactory.buildResourceConfirmationRequiredScheduledEvent(work, Calendar.getInstance()));
	}

	@Test(expected = IllegalArgumentException.class)
	public void buildUserGroupAssociationUpdateEvent_withNullArguments_throwsException() {
		eventFactory.buildUserGroupAssociationUpdateEvent(null);
	}

	@Test
	public void buildUserGroupAssociationUpdateEvent_success() {
		UserGroup userGroup = mock(UserGroup.class);
		when(userGroup.getId()).thenReturn(1L);
		User user = mock(User.class);
		when(user.getId()).thenReturn(1L);
		when(userUserGroupAssociation.getUser()).thenReturn(user);
		when(userUserGroupAssociation.getUserGroup()).thenReturn(userGroup);
		assertNotNull(eventFactory.buildUserGroupAssociationUpdateEvent(userUserGroupAssociation));
	}

	@Test(expected = IllegalArgumentException.class)
	public void buildWorkNegotiationExpiredScheduledEvent_withNullArguments_throwsException() {
		eventFactory.buildWorkNegotiationExpiredScheduledEvent(null, null);
	}

	@Test
	public void buildWorkNegotiationExpiredScheduledEvent_success() {
		assertNotNull(eventFactory.buildWorkNegotiationExpiredScheduledEvent(workNegotiation, Calendar.getInstance()));
	}

	@Test
	public void buildInviteToGroupEvent_verify_split() {
		List<Long> invitedUserIds = new ArrayList<>(250);
		for (long l = 0; l < 250; l++) {
			invitedUserIds.add(l);
		}

		List<InviteToGroupEvent> invites = eventFactory.buildInviteToGroupEvent(invitedUserIds, 1l, 1l);
		assertNotNull(invites);
		assertEquals(5, invites.size());
	}
}

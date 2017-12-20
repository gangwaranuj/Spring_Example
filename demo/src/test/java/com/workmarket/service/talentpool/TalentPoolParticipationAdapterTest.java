package com.workmarket.service.talentpool;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.workmarket.business.talentpool.gen.Messages.TalentPoolParticipation;
import com.workmarket.domains.groups.model.GroupMemberRequestType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class TalentPoolParticipationAdapterTest {

	@InjectMocks private TalentPoolParticipationAdapterImpl talentPoolParticipationAdapter;

	@Test
	public void talentPoolParticipation_invited() {
		final TalentPoolParticipation talentPoolParticipation =
				TalentPoolParticipation.newBuilder()
						.setInvitedOn("value")
						.build();

		Optional<GroupMemberRequestType> status = talentPoolParticipationAdapter.getGroupMemberRequestType(talentPoolParticipation);
		assertEquals(GroupMemberRequestType.INVITED, status.get());
	}

	@Test
	public void talentPoolParticipation_pendingPassed() {
		final TalentPoolParticipation talentPoolParticipation =
				TalentPoolParticipation.newBuilder()
						.setAppliedOn("value")
						.build();

		Optional<GroupMemberRequestType> status = talentPoolParticipationAdapter.getGroupMemberRequestType(talentPoolParticipation);
		assertEquals(GroupMemberRequestType.PENDING_PASSED, status.get());
	}

	@Test
	public void talentPoolParticipation_declined() {
		final TalentPoolParticipation talentPoolParticipation =
				TalentPoolParticipation.newBuilder()
						.setAppliedOn("value")
						.setDeclinedOn("value")
						.build();

		Optional<GroupMemberRequestType> status = talentPoolParticipationAdapter.getGroupMemberRequestType(talentPoolParticipation);
		assertEquals(GroupMemberRequestType.DECLINED, status.get());
	}

	@Test
	public void talentPoolParticipation_memberPassed() {
		final TalentPoolParticipation talentPoolParticipation =
				TalentPoolParticipation.newBuilder()
						.setApprovedOn("value")
						.build();

		Optional<GroupMemberRequestType> status = talentPoolParticipationAdapter.getGroupMemberRequestType(talentPoolParticipation);
		assertEquals(GroupMemberRequestType.MEMBER_PASSED, status.get());
	}


	@Test
	public void talentPoolParticipation_memberOverride() {
		final TalentPoolParticipation talentPoolParticipation =
				TalentPoolParticipation.newBuilder()
						.setApprovedOn("value")
						.setOverrideGrantedOn("value")
						.build();

		Optional<GroupMemberRequestType> status = talentPoolParticipationAdapter.getGroupMemberRequestType(talentPoolParticipation);
		assertEquals(GroupMemberRequestType.MEMBER_OVERRIDE, status.get());
	}

	@Test
	public void allTalentPoolParticipationPropertiesAreCaptured() {
		final TalentPoolParticipation talentPoolParticipation =
				TalentPoolParticipation.newBuilder()
						.setInvitedOn("value")
						.setAppliedOn("value")
						.setApprovedOn("value")
						.setDeclinedOn("value")
						.setOverrideGrantedOn("value")
						.build();

		ImmutableSet<TalentPoolParticipationAdapterImpl.ParticipationProperty> setProperties =
				talentPoolParticipationAdapter.getSetProperties(talentPoolParticipation);

		assertTrue(setProperties.containsAll(Arrays.asList(TalentPoolParticipationAdapterImpl.ParticipationProperty.values())));
	}

}

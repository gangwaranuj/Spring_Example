package com.workmarket.service.talentpool;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.workmarket.business.talentpool.TalentPoolClient;
import com.workmarket.business.talentpool.gen.Messages.TalentPoolAndParticipants;
import com.workmarket.business.talentpool.gen.Messages.TalentPoolsAndParticipantsResp;
import com.workmarket.business.talentpool.gen.Messages.GetTalentPoolParticipantsRequest;
import com.workmarket.business.talentpool.gen.Messages.ParticipantType;
import com.workmarket.business.talentpool.gen.Messages.TalentPoolGetParticipantsRequest;
import com.workmarket.business.talentpool.gen.Messages.TalentPoolParticipant;
import com.workmarket.business.talentpool.gen.Messages.TalentPoolParticipantsResponse;
import com.workmarket.common.core.RequestContext;
import com.workmarket.domains.groups.model.GroupMemberRequestType;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.UserGroupService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.web.WebRequestContextProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rx.functions.Action1;

import java.util.List;
import java.util.Map;
import com.google.common.collect.Lists;


@Component
public class TalentPoolServiceImpl implements TalentPoolService {

	private static final Logger logger = LoggerFactory.getLogger(TalentPoolServiceImpl.class);

	@Autowired private CompanyService companyService;
	@Autowired private TalentPoolParticipationAdapter talentPoolParticipationAdapter;
	@Autowired private TalentPoolClient talentPoolClient;
	@Autowired private UserGroupService userGroupService;
	@Autowired private UserService userService;
	@Autowired private WebRequestContextProvider webRequestContextProvider;

	@Override
	public Map<Long, String> getVendorGroupMemberRequestTypeStatuses(final long userGroupId, final List<Long> companyIds) {
		final String talentPoolUuid = userGroupService.findUserGroupUuidById(userGroupId);
		final List<String> vendorUuids = companyService.getCompanyUuidsForCompanyIds(companyIds);
		final TalentPoolGetParticipantsRequest request =
				TalentPoolGetParticipantsRequest.newBuilder()
						.setTalentPoolUuid(talentPoolUuid)
						.addAllParticipantUuid(vendorUuids)
						.build();

		final ImmutableList.Builder<TalentPoolParticipant> builder = ImmutableList.builder();

		RequestContext context = webRequestContextProvider.getRequestContext();
		talentPoolClient.getParticipants(request, context).subscribe(
				new Action1<TalentPoolParticipantsResponse>() {
					@Override
					public void call(final TalentPoolParticipantsResponse response) {
						builder.addAll(response.getTalentPoolParticipantList());
					}
				},
				new Action1<Throwable>() {
					@Override
					public void call(Throwable throwable) {
						logger.warn("Failed to get talent pool vendor participants: {}", throwable.getMessage());
					}
				});

		return getGroupMemberRequestTypeStatuses(builder.build());
	}

	private Map<Long, String> getGroupMemberRequestTypeStatuses(final List<TalentPoolParticipant> talentPoolParticipants) {
		final ImmutableMap.Builder<Long, String> statuses = ImmutableMap.builder();

		for (final TalentPoolParticipant talentPoolParticipant : talentPoolParticipants) {
			final Optional<GroupMemberRequestType> status =
					talentPoolParticipationAdapter.getGroupMemberRequestType(talentPoolParticipant.getTalentPoolParticipation());
			if (status.isPresent()) {
				final String displayStatus = status.get().toString();
				final String participantUuid = talentPoolParticipant.getParticipantUuid();
				final Long participantId = talentPoolParticipant
						.getTalentPoolParticipation().getParticipantType().equals(ParticipantType.VENDOR) ?
						companyService.findCompanyIdByUuid(participantUuid) :
						userService.findUserIdByUuid(participantUuid);
				statuses.put(participantId, displayStatus);
			}
		}

		return statuses.build();
	}

	@Override
	public boolean isAlreadyParticipatingInTalentPool(final String talentPoolUuid, final String participantUuid) {
		final TalentPoolGetParticipantsRequest talentPoolGetParticipantsRequest =
				TalentPoolGetParticipantsRequest.newBuilder()
						.setTalentPoolUuid(talentPoolUuid)
						.addParticipantUuid(participantUuid)
						.build();

		final ImmutableList.Builder<TalentPoolParticipant> talentPoolParticipantBuilder = ImmutableList.builder();
		talentPoolClient.getParticipants(talentPoolGetParticipantsRequest, webRequestContextProvider.getRequestContext())
				.subscribe(new Action1<TalentPoolParticipantsResponse>() {
					@Override
					public void call(final TalentPoolParticipantsResponse response) {
						talentPoolParticipantBuilder.addAll(response.getTalentPoolParticipantList());
					}
				});
		final List<TalentPoolParticipant> participants = talentPoolParticipantBuilder.build();
		return !participants.isEmpty();
	}

	@Override
	public Map<String, List<TalentPoolParticipant>> getTalentPoolAndParticipants(List<String> talentPoolUuids) {
		final Map<String, List<TalentPoolParticipant>> talentPoolsAndParticipants = Maps.newHashMap();

		GetTalentPoolParticipantsRequest request =
			GetTalentPoolParticipantsRequest.newBuilder()
				.addAllTalentPoolUuid(talentPoolUuids)
				.build();

		talentPoolClient.getParticipants(request, webRequestContextProvider.getRequestContext()).subscribe(
			new Action1<TalentPoolsAndParticipantsResp>() {
				@Override
				public void call(final TalentPoolsAndParticipantsResp response) {
					for (TalentPoolAndParticipants tpap : response.getTalentPoolAndParticpantsList()) {
						List<TalentPoolParticipant> vendors = Lists.newArrayList();
						for (TalentPoolParticipant tpp : tpap.getTalentPoolParticpantList()) {
							if (tpp.getTalentPoolParticipation().getParticipantType().equals(ParticipantType.VENDOR))
								vendors.add(tpp);
						}
						talentPoolsAndParticipants.put(tpap.getUuid(), vendors);
					}
				}
			},
			new Action1<Throwable>() {
				@Override
				public void call(Throwable throwable) {
					logger.warn("Failed to get talent pool participants: " + throwable.getMessage());
				}
			}
		);
		return talentPoolsAndParticipants;
	}
}

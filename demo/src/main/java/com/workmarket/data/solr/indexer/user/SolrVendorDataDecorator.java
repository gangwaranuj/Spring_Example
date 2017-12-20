package com.workmarket.data.solr.indexer.user;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.workmarket.business.talentpool.TalentPoolClient;
import com.workmarket.business.talentpool.gen.Messages.TalentPoolMembership;
import com.workmarket.business.talentpool.gen.Messages.TalentPoolMembershipList;
import com.workmarket.business.talentpool.gen.Messages.TalentPoolMembershipsRequest;
import com.workmarket.business.talentpool.gen.Messages.TalentPoolMembershipsResponse;
import com.workmarket.data.solr.indexer.SolrDataDecorator;
import com.workmarket.data.solr.model.SolrGroupData;
import com.workmarket.data.solr.model.SolrUserData;
import com.workmarket.data.solr.model.SolrVendorData;
import com.workmarket.service.business.UserGroupService;
import com.workmarket.service.web.WebRequestContextProvider;
import com.workmarket.utility.StringUtilities;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rx.functions.Action1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;

/**
 * Decorator for solr vendor data.
 */
@Component
public class SolrVendorDataDecorator implements SolrDataDecorator<SolrVendorData> {

	private static final Logger logger = LoggerFactory.getLogger(SolrVendorDataDecorator.class);

	@Autowired private SolrUserDataDecorator solrUserDataDecorator;
	@Autowired private TalentPoolClient talentPoolClient;
	@Autowired private UserGroupService userGroupService;
	@Autowired private WebRequestContextProvider webRequestContextProvider;

	@Override
	public Collection<SolrVendorData> decorate(final Collection<SolrVendorData> vendors) {
		if (CollectionUtils.isEmpty(vendors)) {
			return vendors;
		}

		final List<String> vendorUuids = extract(vendors, on(SolrVendorData.class).getUuid());

		final TalentPoolMembershipsRequest request =
			TalentPoolMembershipsRequest.newBuilder()
				.addAllParticipantUuid(vendorUuids)
				.build();

		final ImmutableList.Builder<TalentPoolMembershipList> builder = ImmutableList.builder();

		talentPoolClient.getMemberships(request, webRequestContextProvider.getRequestContext()).subscribe(
			new Action1<TalentPoolMembershipsResponse>() {
				@Override
				public void call(final TalentPoolMembershipsResponse response) {
					builder.addAll(response.getTalentPoolMembershipListList());
				}
			},
			new Action1<Throwable>() {
				@Override
				public void call(Throwable throwable) {
					logger.warn("Failed to get talent pool vendor memberships: {}", throwable.getMessage());
				}
			});

		final List<TalentPoolMembershipList> membershipLists = builder.build();

		setGroupMembersForVendors(membershipLists, vendors);

		for (final SolrVendorData vendor : vendors) {
			if (vendor == null || CollectionUtils.isEmpty(vendor.getEmployees())) {
				continue;
			}

			final Collection<SolrUserData> decoratedEmployees = solrUserDataDecorator.decorate(vendor.getEmployees());
			if (CollectionUtils.isNotEmpty(decoratedEmployees)) {
				vendor.setEmployees(new ArrayList<>(decoratedEmployees));
			}
		}

		return vendors;
	}

	private TalentPoolMembershipList getVendorMembershipList(List<TalentPoolMembershipList> membershipLists, String uuid) {
		for (TalentPoolMembershipList membershipList : membershipLists) {
			if (membershipList.getParticipantUuid().equals(uuid)) {
				return membershipList;
			}
		}
		return null;
	}

	private void setGroupMembersForVendors(
		final List<TalentPoolMembershipList> membershipLists,
		final Collection<SolrVendorData> vendors
	) {
		final Set<String> talentPoolUuids = Sets.newHashSet();
		for (final TalentPoolMembershipList membershipList : membershipLists) {
			for (final TalentPoolMembership membership : membershipList.getTalentPoolMembershipList()) {
				talentPoolUuids.add(membership.getTalentPoolUuid());
			}
		}
		final Map<String, Long> talentPoolUuidIdPairs = userGroupService.findUserGroupUuidIdPairsByUuids(talentPoolUuids);

		for (final SolrVendorData vendor : vendors) {
			final TalentPoolMembershipList membershipList = getVendorMembershipList(membershipLists, vendor.getUuid());
			if (membershipList != null) {
				for (final TalentPoolMembership membership : membershipList.getTalentPoolMembershipList()) {
					if (membership.getTalentPoolParticipation() == null ||
						membership.getTalentPoolParticipation().getDeleted()) {
						continue;
					}
					if (talentPoolUuidIdPairs.containsKey(membership.getTalentPoolUuid())) {
						Long groupId = talentPoolUuidIdPairs.get(membership.getTalentPoolUuid());
						final SolrGroupData groupData = createSolrGroupData(membership.getTalentPoolUuid(), groupId);
						setGroupMembersForVendor(groupData, membership, vendor);
					}
				}
			}
		}
	}

	private void setGroupMembersForVendor(
		final SolrGroupData groupData,
		final TalentPoolMembership membership,
		final SolrVendorData vendor
	) {
		if (vendor.getGroupMember() == null) {
			vendor.setGroupMember(Lists.<SolrGroupData>newArrayList());
		}
		if (vendor.getGroupPending() == null) {
			vendor.setGroupPending(Lists.<SolrGroupData>newArrayList());
		}
		if (vendor.getGroupDeclined() == null) {
			vendor.setGroupDeclined(Lists.<SolrGroupData>newArrayList());
		}
		if (vendor.getGroupInvited() == null) {
			vendor.setGroupInvited(Lists.<SolrGroupData>newArrayList());
		}
		if (StringUtilities.isNotEmpty(membership.getTalentPoolParticipation().getApprovedOn())) {
			vendor.getGroupMember().add(groupData);
		} else if (StringUtilities.isNotEmpty(membership.getTalentPoolParticipation().getDeclinedOn())) {
			vendor.getGroupDeclined().add(groupData);
		} else if (StringUtilities.isNotEmpty(membership.getTalentPoolParticipation().getAppliedOn())) {
			vendor.getGroupPending().add(groupData);
		} else if (StringUtilities.isNotEmpty(membership.getTalentPoolParticipation().getInvitedOn())) {
			vendor.getGroupInvited().add(groupData);
		}
	}

	private SolrGroupData createSolrGroupData(final String groupUuid, final Long groupId) {
		final SolrGroupData groupData = new SolrGroupData();
		groupData.setGroupUuid(groupUuid);
		groupData.setGroupId(groupId);
		return groupData;
	}

	@Override
	public SolrVendorData decorate(final SolrVendorData vendor) {
		if (vendor != null) {
			final Collection<SolrVendorData> decoratedVendors = decorate(Lists.newArrayList(vendor));
			if (CollectionUtils.isNotEmpty(decoratedVendors)) {
				return decoratedVendors.iterator().next();
			}
		}
		return vendor;
	}
}

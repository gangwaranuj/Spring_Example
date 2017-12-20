package com.workmarket.domains.search.group.indexer.dao;

import com.google.common.collect.Lists;
import com.workmarket.dao.UserDAO;
import com.workmarket.dao.asset.CompanyAssetAssociationDAO;
import com.workmarket.dao.network.UserGroupNetworkAssociationDAO;
import com.workmarket.dao.skill.UserGroupSkillAssociationDAO;
import com.workmarket.dao.summary.group.UserGroupSummaryDAO;
import com.workmarket.domains.groups.dao.UserGroupDAO;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.asset.CompanyAssetAssociation;
import com.workmarket.domains.model.geocoding.Coordinate;
import com.workmarket.domains.model.network.Network;
import com.workmarket.domains.model.requirementset.AbstractRequirement;
import com.workmarket.domains.model.requirementset.RequirementSet;
import com.workmarket.domains.model.requirementset.backgroundcheck.BackgroundCheckRequirement;
import com.workmarket.domains.model.requirementset.certification.CertificationRequirable;
import com.workmarket.domains.model.requirementset.certification.CertificationRequirement;
import com.workmarket.domains.model.requirementset.drugtest.DrugTestRequirement;
import com.workmarket.domains.model.requirementset.industry.IndustryRequirable;
import com.workmarket.domains.model.requirementset.industry.IndustryRequirement;
import com.workmarket.domains.model.requirementset.insurance.InsuranceRequirable;
import com.workmarket.domains.model.requirementset.insurance.InsuranceRequirement;
import com.workmarket.domains.model.requirementset.license.LicenseRequirable;
import com.workmarket.domains.model.requirementset.license.LicenseRequirement;
import com.workmarket.domains.model.requirementset.rating.RatingRequirement;
import com.workmarket.domains.model.requirementset.resourcetype.ResourceType;
import com.workmarket.domains.model.requirementset.resourcetype.ResourceTypeRequirable;
import com.workmarket.domains.model.requirementset.resourcetype.ResourceTypeRequirement;
import com.workmarket.domains.model.requirementset.test.TestRequirable;
import com.workmarket.domains.model.requirementset.test.TestRequirement;
import com.workmarket.domains.model.requirementset.traveldistance.TravelDistanceRequirement;
import com.workmarket.domains.model.skill.Skill;
import com.workmarket.domains.model.summary.group.UserGroupSummary;
import com.workmarket.domains.search.group.indexer.model.GroupSolrData;
import com.workmarket.utility.StringUtilities;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.flatten;
import static ch.lambdaj.Lambda.on;
import static com.workmarket.utility.CollectionUtilities.isEmpty;

@Repository
public class SolrGroupDAOImpl implements SolrGroupDAO {

	@Autowired private UserGroupDAO userGroupDAO;
	@Autowired private UserGroupSummaryDAO userGroupSummaryDAO;
	@Autowired private CompanyAssetAssociationDAO companyAssetAssociationDAO;
	@Autowired private UserDAO userDAO;
	@Autowired private UserGroupNetworkAssociationDAO userGroupNetworkAssociationDAO;
	@Autowired private UserGroupSkillAssociationDAO userGroupSkillAssociationDAO;

	@Override
	public GroupSolrData getSolrDataById(Long id) {
		Assert.notNull(id);
		UserGroup group = userGroupDAO.get(id);
		Assert.notNull(group);
		return newGroupSTO(group);
	}

	@Override
	public List<GroupSolrData> getSolrDataById(List<Long> ids) {
		if (isEmpty(ids)) {
			return Collections.emptyList();
		}
		List<GroupSolrData> result = Lists.newArrayListWithExpectedSize(ids.size());
		List<UserGroup> groups = userGroupDAO.get(ids);

		for (UserGroup group : groups) {
			GroupSolrData groupSolrData = newGroupSTO(group);
			result.add(groupSolrData);
		}
		return result;
	}

	@Override
	public List<GroupSolrData> getSolrDataBetweenIds(Long fromId, Long toId) {
		if (fromId == null || toId == null) {
			return Collections.emptyList();
		}
		return getSolrDataById(userGroupDAO.findGroupIdsBetween(fromId, toId));
	}

	@SuppressWarnings("unchecked") @Override
	public List<GroupSolrData> getSolrDataChanged(Calendar from) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Integer getMaxGroupId() {
		return userGroupDAO.getMaxGroupId();
	}

	@Override
	public List<String> getSolrDataUuidsByIds(final List<Long> ids) {
		throw new UnsupportedOperationException("Uuids are not available for groupcore");
	}

	/**
	 * Populate a GroupSTO with data that is to be pushed into solr.
	 *
	 * @param group
	 * @return
	 */
	private GroupSolrData newGroupSTO(UserGroup group) {
		Assert.notNull(group);
		Assert.notNull(group.getId());
		Assert.notNull(group.getActiveFlag());

		GroupSolrData groupSolrData = new GroupSolrData()
				.setId(group.getId())
				.setUuid(group.getUuid())
				.setName(group.getName())
				.setNameSort(group.getName())
				.setDescription(group.getDescription())
				.setOpenMembership(group.getOpenMembership())
				.setRequiresApproval(group.getRequiresApproval())
				.setSearchable(group.isSearchable())
				.setDeleted(group.getDeleted())
				.setActiveFlag(group.getActiveFlag())
				.setObjective(group.getObjective())
				.setObjectiveType(group.getObjectiveType() != null ? group.getObjectiveType().getCode() : StringUtils.EMPTY)
				.setCreatedOn(group.getCreatedOn() != null ? group.getCreatedOn().getTime() : null)
				.setMemberCount(userGroupDAO.countAllActiveGroupMembers(group.getId()))
				.setAverageUserRating(0.00)
				.setMessageCount(0)
				.setCreatorId(group.getCreatorId())
				.setAutoGenerated(group.isAutoGenerated());

		final List<Skill> skills = userGroupSkillAssociationDAO.findUserGroupSkills(group);
		if (CollectionUtils.isNotEmpty(skills)) {
			groupSolrData.setSkillIds(extract(skills, on(Skill.class).getId()));
			groupSolrData.setSkillNames(extract(skills, on(Skill.class).getName()));
		}

		if (group.getCompany() != null) {
			CompanyAssetAssociation companyAvatars = companyAssetAssociationDAO.findCompanyAvatars(group.getCompany().getId());
			if (companyAvatars != null) {
				Asset avatarOriginal = companyAvatars.getAsset();
				Asset avatarLarge = companyAvatars.getTransformedLargeAsset();
				Asset avatarSmall = companyAvatars.getTransformedSmallAsset();

				if (avatarOriginal != null) {
					groupSolrData.setAvatarAssetOriginalUri(avatarOriginal.getCdnUri());
				}

				if (avatarLarge != null) {
					groupSolrData.setAvatarAssetLargeUri(avatarLarge.getCdnUri());
				}

				if (avatarSmall != null) {
					groupSolrData.setAvatarAssetSmallUri(avatarSmall.getCdnUri());
				}
			}

			groupSolrData.setCompanyId(group.getCompany().getId());
			groupSolrData.setCompanyName(group.getCompany().getName());
			groupSolrData.setEffectiveCompanyName(group.getCompany().getEffectiveName());
		}

		Map<String, Object> creatorProps = userDAO.getProjectionMapById(group.getCreatorId(), "userNumber", "firstName", "lastName");
		if (MapUtils.isNotEmpty(creatorProps)) {
			groupSolrData.setCreatorFullName(StringUtilities.fullName((String) creatorProps.get("firstName"), (String) creatorProps.get("lastName")));
			groupSolrData.setCreatorUserNumber((String) creatorProps.get("userNumber"));
		}

		if (group.getIndustry() != null) {
			groupSolrData.getIndustryIds().add(group.getIndustry().getId());
			groupSolrData.getIndustryNames().add(group.getIndustry().getName());
			groupSolrData.getIndustryDescriptions().add(null);
		}

		updateRequirementSetsForGroup(groupSolrData, group);

		List<Long> networkIds = extract(userGroupNetworkAssociationDAO.findNetworksWhereGroupIsShared(group.getId()), on(Network.class).getId());
		groupSolrData.setNetworkIds(networkIds);

		/**
		 * Populate with group summary data
		 */
		UserGroupSummary userGroupSummary = userGroupSummaryDAO.findByUserGroup(group.getId());
		if (userGroupSummary != null) {
			if (userGroupSummary.getLastRoutedOn() != null) {
				groupSolrData.setLastRoutedOn(userGroupSummary.getLastRoutedOn().getTime());
			}
			groupSolrData.setTotalThroughput(userGroupSummary.getTotalThroughput().doubleValue());
		}
		return groupSolrData;
	}

	private void updateRequirementSetsForGroup(final GroupSolrData groupSolrData, final UserGroup group) {
		final Collection<RequirementSet> requirementSets = group.getRequirementSetCollection();
		final List<AbstractRequirement> abstractRequirements =
			flatten(extract(requirementSets, on(RequirementSet.class).getRequirements()));
		for (final AbstractRequirement requirement : abstractRequirements) {
			if (requirement instanceof BackgroundCheckRequirement) {
				groupSolrData.setBackgroundCheckedFlag(true);
			} else if (requirement instanceof DrugTestRequirement) {
				groupSolrData.setDrugTestedFlag(true);
			} else if (requirement instanceof IndustryRequirement) {
				final IndustryRequirable requirable = ((IndustryRequirement) requirement).getIndustryRequirable();
				groupSolrData.getRequiredIndustryIds().add(requirable.getId());
				groupSolrData.getRequiredIndustryNames().add(requirable.getName());
				groupSolrData.setHasIndustriesRequirements(true);
			} else if (requirement instanceof InsuranceRequirement) {
				final InsuranceRequirable requirable = ((InsuranceRequirement) requirement).getInsuranceRequirable();
				groupSolrData.getRequiredInsuranceIds().add(requirable.getId());
				groupSolrData.getRequiredInsuranceNames().add(requirable.getName());
				groupSolrData.setHasInsuranceRequirements(true);
			} else if (requirement instanceof TestRequirement) {
				final TestRequirable requirable = ((TestRequirement) requirement).getTestRequirable();
				groupSolrData.getRequiredAssessmentIds().add(requirable.getId());
				groupSolrData.getRequiredAssessmentNames().add(requirable.getName());
				groupSolrData.setHasAssessmentRequirements(true);
			} else if (requirement instanceof CertificationRequirement) {
				final CertificationRequirable requirable = ((CertificationRequirement) requirement).getCertificationRequirable();
				groupSolrData.getRequiredCertificationIds().add(requirable.getId());
				groupSolrData.getRequiredCertificationNames().add(requirable.getName());
				groupSolrData.setHasCertificationRequirements(true);
			} else if (requirement instanceof LicenseRequirement) {
				final LicenseRequirable requirable = ((LicenseRequirement) requirement).getLicenseRequirable();
				groupSolrData.getRequiredLicenseIds().add(requirable.getId());
				groupSolrData.getRequiredLicenseNames().add(requirable.getName());
				groupSolrData.setHasLicenseRequirements(true);
			} else if (requirement instanceof RatingRequirement) {
				groupSolrData.setRequirementsRatingValue(((RatingRequirement) requirement).getValue());
				groupSolrData.setHasRatingRequirements(true);
			} else if (requirement instanceof ResourceTypeRequirement) {
				final ResourceTypeRequirable requirable = ((ResourceTypeRequirement) requirement).getResourceTypeRequirable();
				final ResourceType resourceType = ResourceType.getById(requirable.getId());
				if (resourceType != null) {
					switch (resourceType) {
						case EMPLOYEE:
							groupSolrData.setLane1Flag(true);
							groupSolrData.setHasLaneRequirements(true);
							break;
						case CONTRACTOR:
							groupSolrData.setLane2Flag(true);
							groupSolrData.setLane3Flag(true);
							groupSolrData.setHasLaneRequirements(true);
							break;
						default:
							break;
					}
				}
			} else if (requirement instanceof TravelDistanceRequirement) {
				final Coordinate coordinate = ((TravelDistanceRequirement) requirement).getCoordinates();
				final double maxTravelDistance = ((TravelDistanceRequirement) requirement).getDistance();
				if (coordinate != null && coordinate.getLatitude() != null && coordinate.getLongitude() != null && maxTravelDistance > 0) {
					groupSolrData.setLat(coordinate.getLatitude());
					groupSolrData.setLng(coordinate.getLongitude());
					groupSolrData.setRequirementsMaxTravelDistance(maxTravelDistance);
					groupSolrData.setHasLocationRequirements(true);
				}
			}
		}
	}
}

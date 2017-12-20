package com.workmarket.api.v2.employer.settings.services;

import com.google.api.client.util.Lists;
import com.google.common.collect.ImmutableList;
import com.workmarket.api.v2.employer.assignments.services.UseCase;
import com.workmarket.api.v2.employer.settings.models.BuyerScorecardDTO;
import com.workmarket.api.v2.employer.settings.models.CompanyProfileDTO;
import com.workmarket.api.v2.employer.settings.models.ScorecardDTO;
import com.workmarket.api.v2.employer.settings.models.SkillDTO;
import com.workmarket.api.v2.employer.settings.models.TalentPoolDTO;
import com.workmarket.api.v2.model.LocationDTO;
import com.workmarket.business.talentpool.TalentPoolClient;
import com.workmarket.business.talentpool.gen.Messages.TalentPoolMembership;
import com.workmarket.business.talentpool.gen.Messages.TalentPoolMembershipList;
import com.workmarket.business.talentpool.gen.Messages.TalentPoolMembershipsRequest;
import com.workmarket.business.talentpool.gen.Messages.TalentPoolMembershipsResponse;
import com.workmarket.common.core.RequestContext;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.model.Address;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.CompanyPreference;
import com.workmarket.domains.model.Location;
import com.workmarket.domains.model.analytics.BuyerScoreCard;
import com.workmarket.domains.model.analytics.BuyerScoreField;
import com.workmarket.domains.model.analytics.ResourceScoreField;
import com.workmarket.domains.model.analytics.VendorScoreCard;
import com.workmarket.domains.model.asset.CompanyAssetAssociation;
import com.workmarket.domains.model.skill.Skill;
import com.workmarket.service.analytics.AnalyticsService;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.UserGroupService;
import com.workmarket.service.web.WebRequestContextProvider;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.StringUtilities;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import rx.functions.Action1;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Component
@Scope("prototype")
public abstract class AbstractGetCompanyProfileUseCase
	implements UseCase<AbstractGetCompanyProfileUseCase, CompanyProfileDTO> {

	private static final Logger logger = LoggerFactory.getLogger(AbstractGetCompanyProfileUseCase.class);

	@Autowired private CompanyService companyService;
	@Autowired private AnalyticsService analyticsService;
	@Autowired private UserGroupService userGroupService;
	@Autowired private TalentPoolClient talentPoolClient;
	@Autowired private WebRequestContextProvider webRequestContextProvider;

	protected CompanyProfileDTO.Builder companyProfileDTOBuilder = new CompanyProfileDTO.Builder();
	protected String companyNumber;

	public AbstractGetCompanyProfileUseCase(String companyNumber) {
		this.companyNumber = companyNumber;
	}

	public AbstractGetCompanyProfileUseCase() {}

	public void populateBaseCompanyProfile() {
		Company company = companyService.findCompanyByNumber(this.companyNumber);
		Assert.notNull(company);

		CompanyAssetAssociation avatars = companyService.findCompanyAvatars(company.getId());
		Address address = company.getAddress();

		CompanyPreference companyPreference = companyService.getCompanyPreference(company.getId());
		companyProfileDTOBuilder
			.setName(company.getName())
			.setOverview(company.getOverview())
			.setWebsite(company.getWebsite())
			.setYearFounded(company.getYearFounded())
			.setInVendorSearch(company.isInVendorSearch())
			.setDrugTest(companyPreference.isDrugTest())
			.setBackgroundCheck(companyPreference.isBackgroundCheck());

		if (avatars != null) {
			if (avatars.getSmall() != null) {
				companyProfileDTOBuilder.setAvatarSmall(avatars.getSmall().getCdnUri());
			}

			if (avatars.getLarge() != null) {
				companyProfileDTOBuilder.setAvatar(avatars.getLarge().getCdnUri());
			}
		}

		if (address != null) {
			companyProfileDTOBuilder.setLocation(
				new LocationDTO.Builder()
					.setAddressLine1(address.getAddress1())
					.setAddressLine2(address.getAddress2())
					.setCity(address.getCity())
					.setState(address.getState().getShortName())
					.setCountry(address.getCountry().getISO3())
					.setZip(address.getPostalCode())
					.setLatitude(address.getLatitude().doubleValue())
					.setLongitude(address.getLongitude().doubleValue())
			);
		}

		ArrayList<TalentPoolDTO> talentPoolMemberships = getTalentPoolMemberships(company.getUuid());

		VendorScoreCard scorecard = (VendorScoreCard) analyticsService.getVendorScoreCard(company.getId());
		ScorecardDTO.Builder vendorScorecardDTO = getScorecardDTOBuilder(scorecard);

		BuyerScoreCard buyerScorecard = (BuyerScoreCard) analyticsService.getBuyerScoreCardByCompanyId(company.getId());
		BuyerScorecardDTO.Builder buyerScorecardDTO = getBuyerScorecardDTOBuilder(buyerScorecard);

		companyProfileDTOBuilder
			.setVendorScorecard(vendorScorecardDTO)
			.setCreatedOn(DateUtilities.format("MM/dd/yyyy", company.getCreatedOn()))
			.setEmployees(companyService.findWorkerNumbers(company.getCompanyNumber()).size())
			.setBuyerScorecard(buyerScorecardDTO)
			.setTalentPoolMemberships(talentPoolMemberships);

		List<LocationDTO.Builder> locations = Lists.newArrayList();
		for(Location location : companyService.getCompanyLocations(company.getId())) {
			locations.add(new LocationDTO.Builder().setName(location.getName())
				.setId(location.getId())
				.setAddressLine1(location.getAddress().getAddress1())
				.setAddressLine2(location.getAddress().getAddress2())
				.setCity(location.getAddress().getCity())
				.setZip(location.getAddress().getPostalCode())
				.setState(location.getAddress().getState().getShortName())
				.setCountry(location.getAddress().getCountry().getId())
				.setLatitude(location.getAddress().getLatitude() != null ? location.getAddress().getLatitude().doubleValue() : 0)
				.setLongitude(location.getAddress().getLatitude() != null ? location.getAddress().getLongitude().doubleValue() : 0));
		}
		companyProfileDTOBuilder.setLocationsServiced(locations);

		List<SkillDTO.Builder> skills = Lists.newArrayList();
		for(Skill skill : companyService.getCompanySkills(company.getId())) {
			skills.add(new SkillDTO.Builder().setId(skill.getId()).setName(skill.getName()));
		}
		companyProfileDTOBuilder.setSkills(skills);
	}

	@Override
	public CompanyProfileDTO andReturn() {
		return companyProfileDTOBuilder.build();
	}

	protected ScorecardDTO.Builder getScorecardDTOBuilder(VendorScoreCard scorecard) {
		Double completedWork = scorecard.getValueForField(ResourceScoreField.COMPLETED_WORK).getAll();
		Double cancelledWork = scorecard.getValueForField(ResourceScoreField.CANCELLED_WORK).getAll();
		Double abandonedWork = scorecard.getValueForField(ResourceScoreField.ABANDONED_WORK).getAll();
		Double onTime = scorecard.getValueForField(ResourceScoreField.ON_TIME_PERCENTAGE).getAll();
		Double deliverableOnTime = scorecard.getValueForField(ResourceScoreField.DELIVERABLE_ON_TIME_PERCENTAGE).getAll();
		Double satisfaction = scorecard.getValueForField(ResourceScoreField.SATISFACTION_OVER_ALL).getAll();

		return new ScorecardDTO.Builder()
			.setWorkCompletedCount(completedWork != null ? completedWork.intValue() : 0)
			.setWorkCancelledCount(cancelledWork != null ? cancelledWork.intValue() : 0)
			.setWorkAbandonedCount(abandonedWork != null ? abandonedWork.intValue() : 0)
			.setOnTimePercentage(BigDecimal.valueOf(onTime != null ? onTime : 0).movePointRight(2).setScale(2, RoundingMode.HALF_UP).intValue())
			.setDeliverableOnTimePercentage(BigDecimal.valueOf(deliverableOnTime != null ? deliverableOnTime : 0).movePointRight(2).setScale(2, RoundingMode.HALF_UP).intValue())
			.setSatisfactionRate(BigDecimal.valueOf(satisfaction != null ? satisfaction : 0).movePointRight(2).setScale(2, RoundingMode.HALF_UP).intValue());
	}

	protected BuyerScorecardDTO.Builder getBuyerScorecardDTOBuilder(BuyerScoreCard scorecard) {
		return new BuyerScorecardDTO.Builder()
			.setPaidWorkCount(scorecard.getValueForField(BuyerScoreField.PAID_WORK).getNet90().intValue())
			.setAvgTimeToApproveWorkInDays(BigDecimal.valueOf(scorecard.getValueForField(BuyerScoreField.AVERAGE_TIME_TO_APPROVE_WORK_IN_DAYS).getNet90()).setScale(2, RoundingMode.HALF_UP).doubleValue())
			.setAvgTimeToPayWorkInDays(BigDecimal.valueOf(scorecard.getValueForField(BuyerScoreField.AVERAGE_TIME_TO_PAY_WORK_IN_DAYS).getNet90()).setScale(2, RoundingMode.HALF_UP).doubleValue())
			.setSatisfactionRate(BigDecimal.valueOf(scorecard.getValueForField(BuyerScoreField.PERCENTAGE_RATINGS_OVER_4_STARS).getNet90()).setScale(2, RoundingMode.HALF_UP).doubleValue());
	}

	protected ArrayList<TalentPoolDTO> getTalentPoolMemberships(String companyUuid) {
		final TalentPoolMembershipsRequest request =
			TalentPoolMembershipsRequest.newBuilder()
				.addAllParticipantUuid(ImmutableList.of(companyUuid))
				.build();

		final ImmutableList.Builder<TalentPoolMembershipList> builder = ImmutableList.builder();

		RequestContext context = webRequestContextProvider.getRequestContext();
		talentPoolClient.getMemberships(request, context).subscribe(
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

		ArrayList<TalentPoolDTO> talentPoolMemberships = new ArrayList<>();
		final List<TalentPoolMembershipList> membershipLists = builder.build();

		if (membershipLists.isEmpty()) {
			return talentPoolMemberships;
		}

		for (TalentPoolMembership membership : membershipLists.get(0).getTalentPoolMembershipList()) {
			if (membership.getTalentPoolParticipation() == null) {
				continue;
			}

			if (StringUtils.isNotEmpty(membership.getTalentPoolParticipation().getApprovedOn())) {
				UserGroup userGroup = userGroupService.getByUuid(membership.getTalentPoolUuid());
				if (userGroup != null && userGroup.getOpenMembership()) {
					talentPoolMemberships.add(
						new TalentPoolDTO.Builder()
							.setId(userGroup.getId())
							.setName(userGroup.getName())
							.setDescription(StringUtilities.stripHTML(userGroup.getDescription()))
							.build()
					);
				}
			}
		}

		return talentPoolMemberships;
	}
}

package com.workmarket.service.business.recommendation;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.workmarket.business.recommendation.RecommendationClient;
import com.workmarket.business.recommendation.gen.Messages;
import com.workmarket.business.recommendation.gen.Messages.GeoPoint;
import com.workmarket.business.recommendation.gen.Messages.RecommendTalentToTalentPoolRequest;
import com.workmarket.business.recommendation.gen.Messages.RecommendTalentToTalentPoolResponse;
import com.workmarket.business.recommendation.gen.Messages.RecommendTalentToWorkRequest;
import com.workmarket.business.recommendation.gen.Messages.RecommendTalentToWorkType;
import com.workmarket.business.recommendation.gen.Messages.Talent;
import com.workmarket.business.recommendation.gen.Messages.WorkAttribute;
import com.workmarket.common.core.RequestContext;
import com.workmarket.data.solr.model.SolrUserType;
import com.workmarket.domains.model.qualification.WorkToQualification;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.route.Recommendation;
import com.workmarket.domains.work.model.route.RecommendedResource;
import com.workmarket.search.qualification.Qualification;
import com.workmarket.search.qualification.QualificationType;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.CompanyIdentityDTO;
import com.workmarket.service.business.dto.UserIdentityDTO;
import com.workmarket.service.business.qualification.QualificationAssociationService;
import com.workmarket.service.business.qualification.QualificationRecommender;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rx.functions.Action1;

import java.util.List;
import java.util.UUID;

/**
 * Implementation of recommendation service.
 */
@Service
public class RecommendationServiceImpl implements RecommendationService {
	private static final Logger logger = LoggerFactory.getLogger(RecommendationServiceImpl.class);

	@Autowired
	private RecommendationClient recommendationClient;
	@Autowired
	private UserService userService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private QualificationAssociationService qualificationAssociationService;
	@Autowired
	private QualificationRecommender qualificationRecommender;

	@Override
	public List<Talent> recommendTalentForTalentPool(final Long talentPoolId, final RequestContext context) {
		final RecommendTalentToTalentPoolRequest request =
			RecommendTalentToTalentPoolRequest.newBuilder()
				.setTalentPoolId(talentPoolId.intValue())
				.setTalentPoolUuid(UUID.randomUUID().toString()) // we will move this to talent pool uuid
				.build();

		final ImmutableList.Builder<Talent> talentBuilder = ImmutableList.builder();

		recommendationClient.recommendTalentToTalentPool(request, context)
			.subscribe(
				new Action1<RecommendTalentToTalentPoolResponse>() {
					@Override public void call(RecommendTalentToTalentPoolResponse response) {
						talentBuilder.addAll(response.getTalentList());
					}
				},
				new Action1<Throwable>() {
					@Override public void call(Throwable throwable) {
						logger.error("Failed to get talent pool recommendation from service: {}", throwable.getMessage());
					}
				}
			);
		return talentBuilder.build();
	}

	@Override
	public Recommendation recommendTalentForWork(
		final Work work,
		final RecommendTalentToWorkType type,
		final RequestContext context
	) {
		logger.info("recommend to work {} with type {}", work.getId(), type.name());
		final RecommendTalentToWorkRequest request = createRecommendTalentToWorkRequest(work, type, context);
		final ImmutableList.Builder<Talent> talentBuilder = ImmutableList.builder();
		recommendationClient.recommendTalentToWork(request, context)
			.subscribe(
				new Action1<Messages.RecommendTalentToWorkResponse>() {
					@Override public void call(Messages.RecommendTalentToWorkResponse response) {
						if (response.getStatus().getSuccess()) {
							talentBuilder.addAll(response.getTalentList());
							logger.info("recommend {} talent to work {} with type {}", response.getTalentCount(), work.getId(), type.name());
						}
					}
				},
				new Action1<Throwable>() {
					@Override public void call(Throwable throwable) {
						logger.error("Failed to get recommendation for work: {}, error: {}", work.getId(), throwable.getMessage());
					}
				}
			);
		return createRecommendation(work.getId(), talentBuilder.build());
	}

	@VisibleForTesting
	RecommendTalentToWorkRequest createRecommendTalentToWorkRequest(
		final Work work,
		final RecommendTalentToWorkType type,
		final RequestContext context
		) {
		final WorkAttribute.Builder workAttributeBuilder = WorkAttribute.newBuilder();
		workAttributeBuilder.setTitle(work.getTitle()).setDescription(work.getDescription());
		if (work.getAddress() != null && work.getAddress().getLongitude() != null && work.getAddress().getLatitude() != null) {
			workAttributeBuilder.setGeoPoint(GeoPoint.newBuilder()
				.setLatitude(work.getAddress().getLatitude().doubleValue())
				.setLongitude(work.getAddress().getLongitude().doubleValue())
				.build());
		}
		if (work.getIndustry() != null && work.getIndustry().getId() != null) {
			workAttributeBuilder.setIndustryId(work.getIndustry().getId());
		}
		if (RecommendTalentToWorkType.POLYMATH.equals(type)) {
			final List<String> jobTitles = getJobFunctionsForWork(work, context);
			if (CollectionUtils.isNotEmpty(jobTitles)) {
				workAttributeBuilder.setQualification(
					Messages.Qualification.newBuilder().addAllJobFunction(jobTitles).build());
			}
		}

		return RecommendTalentToWorkRequest.newBuilder()
			.setRecommendTalentToWorkType(type)
			.setWork(Messages.Work.newBuilder()
				.setUuid(work.getUuid())
				.setId(work.getId())
				.setCompanyId(work.getCompany().getId())
				.setBuyerId(work.getBuyer().getId())
				.setWorkAttribute(workAttributeBuilder.build())
				.build())
			.build();
	}

	private Recommendation createRecommendation(final Long workId, final List<Talent> talents) {
		final List<String> vendorUuids = Lists.newArrayList();
		final List<String> workerUuids = Lists.newArrayList();
		for (Talent talent : talents) {
			switch (talent.getUserType()) {
				case WORKER:
					workerUuids.add(talent.getUuid());
					break;
				case VENDOR:
					vendorUuids.add(talent.getUuid());
					break;
				default:
					logger.warn("unknown user type for uuid {}", talent.getUuid());
			}
		}
		final List<UserIdentityDTO> userIdentities = userService.findUserIdentitiesByUuids(workerUuids);
		final List<CompanyIdentityDTO> vendorIdentities = companyService.findCompanyIdentitiesByUuids(vendorUuids);
		final List<RecommendedResource> resources = Lists.newArrayList();
		for (UserIdentityDTO userIdentity : userIdentities) {
			resources.add(new RecommendedResource(userIdentity.getUserId(), userIdentity.getUserNumber(), SolrUserType.WORKER));
		}
		for (CompanyIdentityDTO vendorIdentity : vendorIdentities) {
			resources.add(new RecommendedResource(vendorIdentity.getCompanyId(), vendorIdentity.getCompanyNumber(), SolrUserType.VENDOR));
		}
		return new Recommendation(workId, resources, null);
	}

	@VisibleForTesting
	ImmutableList<String> getJobFunctionsForWork(final Work work, final RequestContext requestContext) {
		if (work.getId() == null) {
			return ImmutableList.of();
		}
		final List<WorkToQualification> workToQualifications =
			qualificationAssociationService.findWorkQualifications(work.getId(), QualificationType.job_title, false);
		if (workToQualifications.size() == 0) {
			return ImmutableList.of();
		}

		final ImmutableList.Builder<String> bundledJobTitles = ImmutableList.builder();
		final String qualificationUuid = workToQualifications.get(0).getQualificationUuid();
		qualificationRecommender.searchSimilarQualifications(qualificationUuid, requestContext)
			.subscribe(
				new Action1<Qualification>() {
					@Override
					public void call(Qualification qualification) {
						if (StringUtils.isNotBlank(qualification.getName())) {
							bundledJobTitles.add(qualification.getName());
						}
					}
				},
				new Action1<Throwable>() {
					@Override
					public void call(Throwable throwable) {
						logger.error("Failed to fetch job titles from qualification service: " + throwable.getMessage());
					}
				});
		return bundledJobTitles.build();
	}
}

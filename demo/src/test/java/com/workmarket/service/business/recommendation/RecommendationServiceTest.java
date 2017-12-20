package com.workmarket.service.business.recommendation;

import com.google.common.collect.Lists;
import com.workmarket.business.recommendation.RecommendationClient;
import com.workmarket.business.recommendation.gen.Messages.RecommendTalentToTalentPoolRequest;
import com.workmarket.business.recommendation.gen.Messages.RecommendTalentToTalentPoolResponse;
import com.workmarket.business.recommendation.gen.Messages.RecommendTalentToWorkRequest;
import com.workmarket.business.recommendation.gen.Messages.RecommendTalentToWorkResponse;
import com.workmarket.business.recommendation.gen.Messages.RecommendTalentToWorkType;
import com.workmarket.business.recommendation.gen.Messages.Status;
import com.workmarket.business.recommendation.gen.Messages.Talent;
import com.workmarket.business.recommendation.gen.Messages.UserType;
import com.workmarket.common.core.RequestContext;
import com.workmarket.data.solr.model.SolrUserType;
import com.workmarket.domains.model.Address;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.Industry;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.qualification.WorkToQualification;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.route.Recommendation;
import com.workmarket.search.qualification.Qualification;
import com.workmarket.search.qualification.QualificationType;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.CompanyIdentityDTO;
import com.workmarket.service.business.dto.UserIdentityDTO;
import com.workmarket.service.business.qualification.QualificationAssociationService;
import com.workmarket.service.business.qualification.QualificationRecommender;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import rx.Observable;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test cases for RecommendationService.
 */
@RunWith(MockitoJUnitRunner.class)
public class RecommendationServiceTest {

	private static final String WORKER_UUID = "WORKER-uuid";
	private static final Long WORKER_ID = 1234L;
	private static final String WORKER_NUMBER = "4321";
	private static final String VENDOR_UUID = "VENDOR-uuid";
	private static final Long VENDOR_ID = 4567L;
	private static final String VENDOR_NUMBER = "7654";
	private static final UserIdentityDTO USER_IDENTITY = new UserIdentityDTO(WORKER_ID, WORKER_NUMBER, WORKER_UUID);
	private static final CompanyIdentityDTO COMPANY_IDENTITY = new CompanyIdentityDTO(VENDOR_ID, VENDOR_NUMBER, VENDOR_UUID);
	private static final Talent WORKER = Talent.newBuilder().setUuid(WORKER_UUID).setUserType(UserType.WORKER).build();
	private static final Talent VENDOR = Talent.newBuilder().setUuid(VENDOR_UUID).setUserType(UserType.VENDOR).build();
	private static final List<Talent> TALENTS = Lists.newArrayList(WORKER, VENDOR);
	private static final Long WORK_ID = 1L;
	private static final String WORK_UUID = "work-uuid";
	private static final Long COMPANY_ID = 1L;
	private static final Long BUYER_ID = 2L;
	private static final String WORK_TITLE = "work title";
	private static final String WORK_DESC = "work desc";
	private static final String JOB_TITLE_1_UUID = "job-title-1";
	private static final String JOB_TITLE_1 = "job title 1";
	private static final String JOB_TITLE_2 = "job title 2";

	@Mock private Work work;
	@Mock private User buyer;
	@Mock private Company company;
	@Mock private Industry industry;
	@Mock private Address address;
	@Mock private RequestContext context;
	@Mock private Throwable t;

	@Mock private UserService userService;
	@Mock private CompanyService companyService;
	@Mock private RecommendationClient recommendationClient;
	@Mock private QualificationAssociationService qualificationAssociationService;
	@Mock private QualificationRecommender qualificationRecommender;
	@InjectMocks private RecommendationServiceImpl recommendationService;

	@Before
	public void setUp() {
		when(userService.findUserIdentitiesByUuids(Lists.newArrayList(WORKER_UUID)))
			.thenReturn(Lists.newArrayList(USER_IDENTITY));
		when(companyService.findCompanyIdentitiesByUuids(Lists.newArrayList(VENDOR_UUID)))
			.thenReturn(Lists.newArrayList(COMPANY_IDENTITY));
		when(work.getId()).thenReturn(WORK_ID);
		when(work.getUuid()).thenReturn(WORK_UUID);
		when(work.getCompany()).thenReturn(company);
		when(work.getBuyer()).thenReturn(buyer);
		when(company.getId()).thenReturn(COMPANY_ID);
		when(buyer.getId()).thenReturn(BUYER_ID);
		when(work.getTitle()).thenReturn(WORK_TITLE);
		when(work.getDescription()).thenReturn(WORK_DESC);
		when(work.getIndustry()).thenReturn(industry);
		when(work.getAddress()).thenReturn(address);
	}
	/**
	 * Tests for recommending talents for talent pool.
	 *
	 * @throws Exception
	 */
	@Test
	public void testRecommendTalentForTalentPoolSuccess() throws Exception {
		final String uuid = UUID.randomUUID().toString();
		final RecommendTalentToTalentPoolResponse response =
			RecommendTalentToTalentPoolResponse.newBuilder()
				.setStatus(Status.newBuilder().setSuccess(true).build())
				.addAllTalent(
					Lists.newArrayList(
						Talent.newBuilder()
							.setUuid(uuid)
							.setUserType(UserType.WORKER)
							.build()))
				.build();

		when(recommendationClient.recommendTalentToTalentPool(any(RecommendTalentToTalentPoolRequest.class), any(RequestContext.class)))
			.thenReturn(Observable.just(response));

		final List<Talent> talents = recommendationService.recommendTalentForTalentPool(anyLong(), any(RequestContext.class));
		verify(recommendationClient, times(1))
			.recommendTalentToTalentPool(any(RecommendTalentToTalentPoolRequest.class), any(RequestContext.class));
		assertEquals(1, talents.size());
		assertEquals(uuid, talents.get(0).getUuid());
	}

	/**
	 * Tests recommending talent for talent pool when service fails.
	 *
	 * @throws Exception
	 */
	@Test
	public void testRecommendTalentForTalentPoolFailure() throws Exception {
		final Observable<RecommendTalentToTalentPoolResponse> err = Observable.error(t);
		when(recommendationClient.recommendTalentToTalentPool(any(RecommendTalentToTalentPoolRequest.class), any(RequestContext.class)))
			.thenReturn(err);

		final List<Talent> talents = recommendationService.recommendTalentForTalentPool(anyLong(), any(RequestContext.class));
		verify(recommendationClient, times(1))
			.recommendTalentToTalentPool(any(RecommendTalentToTalentPoolRequest.class), any(RequestContext.class));
		verify(t, times(1)).getMessage();
		assertEquals(0, talents.size());
	}

	/**
	 * Tests recommending talent for work success.
	 *
	 * @throws Exception
	 */
	@Test
	public void testRecommendTalentForWorkSuccess() throws Exception {
		final RecommendTalentToWorkResponse resp = RecommendTalentToWorkResponse.newBuilder()
			.setStatus(Status.newBuilder().setSuccess(true).setMessage("OK").build())
			.addAllTalent(TALENTS)
			.build();
		when(recommendationClient.recommendTalentToWork(any(RecommendTalentToWorkRequest.class), any(RequestContext.class)))
			.thenReturn(Observable.just(resp));

		final Recommendation recommendation =
			recommendationService.recommendTalentForWork(work, RecommendTalentToWorkType.LIKEWORK, context);

		assertEquals(WORK_ID, recommendation.getWorkId());
		assertNull(recommendation.getExplain());
		assertEquals(2, recommendation.getRecommendedResources().size());
		assertEquals(1, recommendation.getRecommendedResourceIdsByUserType(SolrUserType.WORKER).size());
		assertEquals(WORKER_ID, recommendation.getRecommendedResourceIdsByUserType(SolrUserType.WORKER).get(0));
		assertEquals(1, recommendation.getRecommendedResourceIdsByUserType(SolrUserType.VENDOR).size());
		assertEquals(VENDOR_ID, recommendation.getRecommendedResourceIdsByUserType(SolrUserType.VENDOR).get(0));
	}

	/**
	 * Tests recommending talent for work when service fails.
	 *
	 * @throws Exception
	 */
	@Test
	public void testRecommendTalentForWorkFailure() throws Exception {
		final Observable<RecommendTalentToWorkResponse> err = Observable.error(t);
		when(recommendationClient.recommendTalentToWork(any(RecommendTalentToWorkRequest.class), any(RequestContext.class)))
			.thenReturn(err);

		final Recommendation recommendation =
			recommendationService.recommendTalentForWork(work, RecommendTalentToWorkType.LIKEWORK, context);

		verify(t, times(1)).getMessage();
		assertEquals(WORK_ID, recommendation.getWorkId());
		assertNull(recommendation.getExplain());
		assertEquals(0, recommendation.getRecommendedResources().size());
	}

	/**
	 * Tests get job functions for work success.
	 *
	 * @throws Exception
	 */
	@Test
	public void testGetJobFunctionsForWorkSuccess() throws Exception {
		when(qualificationAssociationService.findWorkQualifications(WORK_ID, QualificationType.job_title, false))
			.thenReturn(Lists.newArrayList(new WorkToQualification(WORK_ID, JOB_TITLE_1_UUID, QualificationType.job_title)));
		when(qualificationRecommender.searchSimilarQualifications(JOB_TITLE_1_UUID, context))
			.thenReturn(Observable.from(Arrays.asList(
				Qualification.builder().setName(JOB_TITLE_1).setUuid(JOB_TITLE_1_UUID).build(),
				Qualification.builder().setName(JOB_TITLE_2).setUuid("job-uuid-2").build()
			)));
		List<String> jobTitlesForWork = recommendationService.getJobFunctionsForWork(work, context);
		assertEquals(2, jobTitlesForWork.size());
		assertTrue(jobTitlesForWork.contains(JOB_TITLE_1));
		assertTrue(jobTitlesForWork.contains(JOB_TITLE_2));
	}

	/**
	 * Tests get job functions for work failed at qualification bundle fetch.
	 *
	 * @throws Exception
	 */
	@Test
	public void testGetJobFunctionsForWorkFetchQualificationBundleFailure() throws Exception {
		final Observable<Qualification> err = Observable.error(t);
		when(qualificationAssociationService.findWorkQualifications(WORK_ID, QualificationType.job_title, false))
			.thenReturn(Lists.newArrayList(new WorkToQualification(WORK_ID, JOB_TITLE_1_UUID, QualificationType.job_title)));
		when(qualificationRecommender.searchSimilarQualifications(JOB_TITLE_1_UUID, context))
			.thenReturn(err);
		List<String> jobTitlesForWork = recommendationService.getJobFunctionsForWork(work, context);
		verify(qualificationAssociationService, times(1))
			.findWorkQualifications(anyLong(), any(QualificationType.class), anyBoolean());
		verify(qualificationRecommender, times(1))
			.searchSimilarQualifications(anyString(), any(RequestContext.class));
		verify(t, times(1)).getMessage();
		assertEquals(0, jobTitlesForWork.size());
	}

	/**
	 * Tests get job functions for work returns none because there is no job function associated with the work.
	 *
	 * @throws Exception
	 */
	@Test
	public void testGetJobFunctionsForWorkNoJobFunctionsForWork() throws Exception {
		when(qualificationAssociationService.findWorkQualifications(WORK_ID, QualificationType.job_title, false))
			.thenReturn(Lists.<WorkToQualification>newArrayList());
		List<String> jobTitlesForWork = recommendationService.getJobFunctionsForWork(work, context);
		verify(qualificationAssociationService, times(1))
			.findWorkQualifications(anyLong(), any(QualificationType.class), anyBoolean());
		verify(qualificationRecommender, times(0))
			.searchSimilarQualifications(anyString(), any(RequestContext.class));
		assertEquals(0, jobTitlesForWork.size());
	}

	/**
	 * Tests create request with basic required info.
	 */
	@Test
	public void testCreateRecommendTalentToWorkRequestBasic() {
		when(work.getIndustry()).thenReturn(null);
		when(work.getAddress()).thenReturn(null);

		final RecommendTalentToWorkRequest request =
			recommendationService.createRecommendTalentToWorkRequest(work, RecommendTalentToWorkType.LIKEGROUP, context);

		assertEquals(RecommendTalentToWorkType.LIKEGROUP, request.getRecommendTalentToWorkType());
		assertEquals(WORK_ID.longValue(), request.getWork().getId());
		assertEquals(WORK_UUID, request.getWork().getUuid());
		assertEquals(COMPANY_ID.longValue(), request.getWork().getCompanyId());
		assertEquals(BUYER_ID.longValue(), request.getWork().getBuyerId());
		assertEquals(WORK_TITLE, request.getWork().getWorkAttribute().getTitle());
		assertFalse(request.getWork().getWorkAttribute().hasGeoPoint());
		assertEquals(0, request.getWork().getWorkAttribute().getIndustryId());
		assertTrue(CollectionUtils.isEmpty(request.getWork().getWorkAttribute().getQualification().getJobFunctionList()));
	}

	/**
	 * Tests create request with all fields available.
	 */
	@Test
	public void testCreateRecommendTalentToWorkRequestComplete() {
		final Long industryId = 1L;
		final BigDecimal latitude = new BigDecimal(49.123456);
		final BigDecimal longitude = new BigDecimal(-79.654321);
		final RequestContext context = mock(RequestContext.class);
		when(industry.getId()).thenReturn(industryId);
		when(address.getLatitude()).thenReturn(latitude);
		when(address.getLongitude()).thenReturn(longitude);
		when(qualificationAssociationService.findWorkQualifications(WORK_ID, QualificationType.job_title, false))
			.thenReturn(Lists.newArrayList(new WorkToQualification(WORK_ID, JOB_TITLE_1_UUID, QualificationType.job_title)));
		when(qualificationRecommender.searchSimilarQualifications(JOB_TITLE_1_UUID, context))
			.thenReturn(Observable.from(Arrays.asList(
				Qualification.builder().setName(JOB_TITLE_1).setUuid(JOB_TITLE_1_UUID).build(),
				Qualification.builder().setName(JOB_TITLE_2).setUuid("job-uuid-2").build()
			)));

		final RecommendTalentToWorkRequest request =
			recommendationService.createRecommendTalentToWorkRequest(work, RecommendTalentToWorkType.POLYMATH, context);

		assertEquals(RecommendTalentToWorkType.POLYMATH, request.getRecommendTalentToWorkType());
		assertEquals(WORK_ID.longValue(), request.getWork().getId());
		assertEquals(WORK_UUID, request.getWork().getUuid());
		assertEquals(COMPANY_ID.longValue(), request.getWork().getCompanyId());
		assertEquals(BUYER_ID.longValue(), request.getWork().getBuyerId());
		assertEquals(WORK_TITLE, request.getWork().getWorkAttribute().getTitle());
		assertEquals(industryId.longValue(), request.getWork().getWorkAttribute().getIndustryId());
		assertEquals(2, request.getWork().getWorkAttribute().getQualification().getJobFunctionList().size());
		assertEquals(latitude.doubleValue(), request.getWork().getWorkAttribute().getGeoPoint().getLatitude(), 0.001);
		assertEquals(longitude.doubleValue(), request.getWork().getWorkAttribute().getGeoPoint().getLongitude(), 0.001);
	}
}

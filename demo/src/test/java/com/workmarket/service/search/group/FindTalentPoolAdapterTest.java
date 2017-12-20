package com.workmarket.service.search.group;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.lane.LaneType;
import com.workmarket.search.gen.Common.FacetCount;
import com.workmarket.search.gen.Common.Facet;
import com.workmarket.search.gen.GroupMessages.FindTalentPoolResponse;
import com.workmarket.search.gen.GroupMessages.SortField;
import com.workmarket.search.gen.GroupMessages.FindTalentPoolRequest;
import com.workmarket.search.gen.GroupMessages.Industry;
import com.workmarket.search.gen.GroupMessages.SearchType;
import com.workmarket.search.gen.GroupMessages.TalentPool;
import com.workmarket.search.gen.Common.SortDirectionType;
import com.workmarket.service.business.IndustryService;
import com.workmarket.service.business.LaneService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.infra.security.LaneContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FindTalentPoolAdapterTest {

	private TalentPool talentPool;

	@Mock private LaneContext laneContext;
	@Mock private LaneService laneService;
	@Mock private IndustryService industryService;
	@Mock private UserService userService;
	@InjectMocks private FindTalentPoolAdapter findTalentPoolAdapter;

	@Test
	public void testIsAuthorizedToJoinNonAllOpenGroupsReturnFalse() {
		talentPool = TalentPool.newBuilder().build();
		boolean isAuthorizedToJoin = findTalentPoolAdapter.isAuthorizedToJoin(
			talentPool,
			SearchType.SEARCH_COMPANY_GROUPS,
			1L, false);
		assertFalse(isAuthorizedToJoin);
	}

	@Test
	public void testIsAuthorizedToJoinLane0OR1OR2ReturnTrue() {
		talentPool = TalentPool.newBuilder()
			.setLane0Flag(true)
			.build();
		boolean isAuthorizedToJoin = findTalentPoolAdapter.isAuthorizedToJoin(
			talentPool,
			SearchType.SEARCH_ALL_OPEN_GROUPS,
			1L, false);
		assertTrue(isAuthorizedToJoin);
	}

	@Test
	public void testIsAuthorizedToJoinLane3FlagReturnLane3Active() {
		talentPool = TalentPool.newBuilder()
			.setLane3Flag(true)
			.build();
		boolean lane3Active = false;
		boolean isAuthorizedToJoin = findTalentPoolAdapter.isAuthorizedToJoin(
			talentPool,
			SearchType.SEARCH_ALL_OPEN_GROUPS,
			1L, lane3Active);
		assertEquals(lane3Active, isAuthorizedToJoin);
	}

	@Test
	public void testIsAuthorizedToJoinLaneContext() {
		Long searcherId = 1L;
		talentPool = TalentPool.newBuilder()
			.setCompanyId(2L)
			.build();
		when(laneService.getLaneContextForUserAndCompany(searcherId, talentPool.getCompanyId()))
			.thenReturn(laneContext);
		when(laneContext.isInWorkerPool()).thenReturn(true);
		boolean isAuthorizedToJoin = findTalentPoolAdapter.isAuthorizedToJoin(
			talentPool,
			SearchType.SEARCH_ALL_OPEN_GROUPS,
			1L, false);
		assertTrue(isAuthorizedToJoin);
	}

	@Test
	public void testConvertISODateString() {
		String iso8601 = "2017-01-11T01:10:11Z";
		String expected = "01/11/2017";
		String converted = findTalentPoolAdapter.convertISODateString(iso8601);
		assertEquals(expected, converted);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testExtractIndustries() {
		Industry industry1 = Industry.newBuilder()
			.setId(1000)
			.setName("industry1")
			.build();
		Industry industry2 = Industry.newBuilder()
			.setId(1001)
			.setName("industry2")
			.build();
		talentPool = TalentPool.newBuilder()
			.addAllIndustries(Lists.newArrayList(industry1, industry2))
			.build();

		List<Object> industries = findTalentPoolAdapter.extractIndustries(talentPool);
		assertEquals(2, industries.size());
		for (Object industry : industries) {
			assertTrue(industry instanceof Map);
			if (((Map<String, Object>) industry).get("id").equals(1000)) {
				assertEquals("industry1", ((Map<String, Object>) industry).get("name"));
			} else if (((Map<String, Object>) industry).get("id").equals(1001)) {
				assertEquals("industry2", ((Map<String, Object>) industry).get("name"));
			}
		}
	}

	@Test
	public void testBuildFindTalentRequest() {
		User searcher = mock(User.class);
		Company company = mock(Company.class);
		int start = 0;
		int limit = 50;
		String keyword = "abc";
		String objectiveType = "internal";
		String sortby = "NAME";
		Integer[] industryIds = {1000, 1001};
		List<Long> industryList = Lists.newArrayList(1000L, 1001L);

		when(searcher.getId()).thenReturn(1L);
		when(searcher.getCompany()).thenReturn(company);
		when(company.getId()).thenReturn(11L);
		when(searcher.isUserExclusive()).thenReturn(true);
		when(laneService.findAllCompaniesWhereUserIsResource(1L, LaneType.LANE_2))
			.thenReturn(Lists.newArrayList(2000L));
		when(userService.findBlockedOrBlockedByCompanyIdsByUserId(1L)).thenReturn(Lists.<Long>newArrayList());

		FindTalentPoolRequest request = findTalentPoolAdapter.buildFindTalentPoolRequest(
			start, limit, keyword, objectiveType,
			industryIds, sortby,
			searcher, false, false);

		assertEquals(start, request.getStart());
		assertEquals(limit, request.getRows());
		assertEquals(keyword, request.getKeyword());
		assertEquals(objectiveType, request.getObjectiveType());
		assertFalse(request.getIndustryFilter().getShowAll());
		assertEquals(2, request.getIndustryFilter().getIndustryIdsCount());
		assertTrue(request.getIndustryFilter().getIndustryIdsList().containsAll(industryList));
		assertEquals(1, request.getCompanyIdsExclusiveToUserCount());
		assertEquals(2000L, request.getCompanyIdsExclusiveToUser(0));
		assertEquals(0, request.getBlockedByCompanyIdsCount());
		assertEquals(SearchType.SEARCH_ALL_OPEN_GROUPS, request.getSearchType());
		assertEquals(SortField.NAME, request.getSortField());
		assertEquals(SortDirectionType.asc, request.getSortDirection());
	}

	@Test
	public void testConvertTalentPoolToMap() {
		talentPool = TalentPool.newBuilder()
			.setId(1L)
			.setName("great pool")
			.setCompanyName("workmarket")
			.setAvatarAssetLargeUri("uri")
			.setDescription("desc")
			.addAllIndustries(Lists.<Industry>newArrayList())
			.setMemberCount(1)
			.setCreatedOn("2017-01-11T11:11:11Z")
			.setRequiresApproval(false)
			.setActiveFlag(true)
			.build();
		Map<String, Object> talentPoolMap = findTalentPoolAdapter.convertTalentPoolToMap(
			talentPool, SearchType.SEARCH_COMPANY_GROUPS, 1L, false, true, false);

		assertEquals(1L, talentPoolMap.get("id"));
		assertEquals("great pool", talentPoolMap.get("name"));
		assertEquals("workmarket", talentPoolMap.get("company_name"));
		assertEquals("uri", talentPoolMap.get("avatar_uri"));
		assertEquals("desc", talentPoolMap.get("description_no_html"));
		assertEquals(0, ((List) talentPoolMap.get("industries")).size());
		assertEquals("01/11/2017", talentPoolMap.get("created_on"));
		assertFalse((Boolean) talentPoolMap.get("requires_approval"));
		assertFalse((Boolean) talentPoolMap.get("has_requirements"));
		assertTrue((Boolean) talentPoolMap.get("active_flag"));
		assertTrue((Boolean) talentPoolMap.get("is_admin"));
		assertFalse((Boolean) talentPoolMap.get("authorized_to_join"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testPopulateFilters() {
		List<String> industryIds = Lists.newArrayList("1000", "1001");
		com.workmarket.domains.model.Industry industry1 = mock(com.workmarket.domains.model.Industry.class);
		com.workmarket.domains.model.Industry industry2 = mock(com.workmarket.domains.model.Industry.class);
		when(industryService.getIndustryById(1000L)).thenReturn(industry1);
		when(industryService.getIndustryById(1001L)).thenReturn(industry2);
		when(industry1.getName()).thenReturn("tech service");
		when(industry2.getName()).thenReturn("communication");
		FindTalentPoolRequest req = FindTalentPoolRequest.newBuilder().build();
		FindTalentPoolResponse resp = FindTalentPoolResponse.newBuilder()
			.addFacets(Facet.newBuilder()
				.setField("industryIds")
				.addAllFacetCounts(Lists.newArrayList(
					FacetCount.newBuilder().setCount(100).setValue("1000").build(),
					FacetCount.newBuilder().setCount(50).setValue("1001").build()
				))
				.build())
			.build();
		Map<String, Object> filters = findTalentPoolAdapter.populateFilters(req, resp);
		assertTrue(filters.containsKey("industries"));
		List<Map<String, Object>> industries = (List<Map<String, Object>>) filters.get("industries");
		for (Map<String, Object> industry : industries) {
			assertTrue(industryIds.contains(industry.get("id")));
			if ("1000".equals(industry.get("id"))) {
				assertEquals(100L, industry.get("count"));
			} else if ("1001".equals(industry.get("id"))) {
				assertEquals(50L, industry.get("count"));
			}
		}
		assertTrue(filters.containsKey("labels"));
		Map<String, Object> labels = (Map<String, Object>) filters.get("labels");
		assertTrue(labels.containsKey("industries"));
		Map<String, Object> industryLabels = (Map<String, Object>) labels.get("industries");
		assertEquals("tech service", industryLabels.get("1000"));
		assertEquals("communication", industryLabels.get("1001"));
	}
}

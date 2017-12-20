package com.workmarket.search.model.query;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.workmarket.data.solr.repository.WorkSearchableFields;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.search.model.SearchUser;
import com.workmarket.search.model.WorkSearchTransientData;
import com.workmarket.search.request.work.WorkSearchRequest;
import com.workmarket.search.request.work.WorkSearchRequestUserType;
import com.workmarket.search.response.work.DashboardClient;
import com.workmarket.search.response.work.DashboardInternalOwner;
import com.workmarket.search.response.work.DashboardProject;
import com.workmarket.search.response.work.DashboardResource;
import com.workmarket.search.response.work.DashboardStatusFilter;
import com.workmarket.search.response.work.WorkMilestoneFilter;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(BlockJUnit4ClassRunner.class)
public class WorkSearchQueryTest {

	private WorkSearchRequest request = new WorkSearchRequest();
	private long userId = 1L;
	private long companyId = 2L;

	@Test
	public void buildMultipleWorkStatusFilterQuery() {
		WorkSearchQuery query = new WorkSearchQuery(request);
		request.setWorkStatusFilters(Sets.newHashSet(new WorkStatusType(WorkStatusType.DRAFT), new WorkStatusType(WorkStatusType.ACTIVE)));
		query = query.addMultipleWorkStatusFilterQuery();
		assertNotNull(query);
		assertTrue(query.getFilterQueries().length > 0);
		assertFoundFilterQuery(query, WorkSearchableFields.WORK_STATUS_TYPE_CODE.getName());
	}

	@Test
	public void buildIgnoreVirtualWorkQuery() {
		WorkSearchQuery query = new WorkSearchQuery(request);
		request.setIgnoreVirtual(true);
		query = query.addIgnoreVirtualWorkQuery();
		assertNotNull(query);
		assertTrue(query.getFilterQueries().length > 0);
		assertFoundFilterQuery(query, WorkSearchableFields.LATITUDE.getName());
		assertFoundFilterQuery(query, "longitude");
	}

	@Test
	public void addWorkSubStatusTypeFilter() {
		WorkSearchQuery query = new WorkSearchQuery(request);
		DashboardStatusFilter subStatusFilter = new DashboardStatusFilter();
		subStatusFilter.setStatusCode("1");
		request.setSubStatusFilter(subStatusFilter);

		query = query.addWorkSubStatusTypeFilter();
		assertNotNull(query);
		assertTrue(query.getFilterQueries().length > 0);
		assertFoundFilterQuery(query, WorkSearchableFields.BUYER_LABELS_ID.getName());
	}

	@Test
	public void addWorkBeginEndDateFilter_approvedDate() {
		WorkSearchQuery query = new WorkSearchQuery(request);
		DateRange dateRange = new DateRange(Calendar.getInstance(), DateUtilities.getMidnightYesterday());
		request.setWorkMilestoneFilter(WorkMilestoneFilter.APPROVED_DATE);
		request.setDateRange(dateRange);

		query = query.addWorkBeginEndDateFilter();
		assertNotNull(query);
		assertTrue(query.getFilterQueries().length > 0);
		assertFoundFilterQuery(query, WorkSearchableFields.APPROVED_DATE.getName());
	}


	@Test
	public void addWorkBeginEndDateFilter_completedDate() {
		WorkSearchQuery query = new WorkSearchQuery(request);
		DateRange dateRange = new DateRange(Calendar.getInstance(), DateUtilities.getMidnightYesterday());
		request.setWorkMilestoneFilter(WorkMilestoneFilter.COMPLETED_DATE);
		request.setDateRange(dateRange);

		query = query.addWorkBeginEndDateFilter();
		assertNotNull(query);
		assertTrue(query.getFilterQueries().length > 0);
		assertFoundFilterQuery(query, WorkSearchableFields.COMPLETED_DATE.getName());
	}

	@Test
	public void addWorkBeginEndDateFilter_paidDate() {
		WorkSearchQuery query = new WorkSearchQuery(request);
		DateRange dateRange = new DateRange(Calendar.getInstance(), DateUtilities.getMidnightYesterday());
		request.setWorkMilestoneFilter(WorkMilestoneFilter.PAID_DATE);
		request.setDateRange(dateRange);

		query = query.addWorkBeginEndDateFilter();
		assertNotNull(query);
		assertTrue(query.getFilterQueries().length > 0);
		assertFoundFilterQuery(query, WorkSearchableFields.PAID_DATE.getName());
	}

	@Test
	public void addWorkClientFilter() {
		WorkSearchQuery query = new WorkSearchQuery(request);
		request.setClients(Sets.newHashSet(new DashboardClient(1L, "Presidio")));
		query = query.addWorkClientFilter();
		assertFoundFilterQuery(query, WorkSearchableFields.CLIENT_COMPANY_ID.getName());
	}

	@Test
	public void addWorkProjectFilter() {
		WorkSearchQuery query = new WorkSearchQuery(request);
		request.setProjects(Sets.newHashSet(new DashboardProject(1L, "Project")));

		query = query.addWorkProjectFilter();
		assertTrue(query.getFilterQueries().length > 0);
		assertFoundFilterQuery(query, WorkSearchableFields.PROJECT_ID.getName());
	}

	@Test
	public void addWorkInternalOwnerFilter() {
		WorkSearchQuery query = new WorkSearchQuery(request);
		request.setInternalOwners(Sets.newHashSet(new DashboardInternalOwner(1L, "Rocio")));
		query = query.addWorkInternalOwnerFilter();
		assertNotNull(query);
		assertTrue(query.getFilterQueries().length > 0);
		assertFoundFilterQuery(query, WorkSearchableFields.BUYER_USER_ID.getName());
	}

	@Test
	public void setFilters() {
		WorkSearchQuery query = new WorkSearchQuery(request);
		request.addFilterQueries(CollectionUtilities.newStringMap(
			"someField", "some value",
			"someOtherField", "some other value"
		));
		query = query.setFilters();
		assertNotNull(query);
		assertTrue(query.getFilterQueries().length > 0);
		assertFoundFilterQuery(query, "+someField:some");
		assertFoundFilterQuery(query, "+someField:value");
		assertFoundFilterQuery(query, "+someOtherField:some");
		assertFoundFilterQuery(query, "+someOtherField:other");
		assertFoundFilterQuery(query, "+someOtherField:value");
	}

	@Test
	public void addFacetFieldsWithDefaults() {
		request.setFacetFields(
				ImmutableSet.of(
						WorkSearchableFields.COUNTY_ID,
						WorkSearchableFields.COUNTY_NAME));
		WorkSearchQuery query = new WorkSearchQuery(request);
		query.addFacetFieldsWithDefaults();
		assertFoundFacetField(query, WorkSearchableFields.COUNTY_NAME.getName());
		assertFoundFacetField(query, WorkSearchableFields.COUNTY_ID.getName());
	}

	@Test
	public void buildBlockedFilter() {
		request.setBlockedCompanyIds(Lists.newArrayList(1L));
		request.setWorkSearchRequestUserType(WorkSearchRequestUserType.RESOURCE);
		WorkSearchQuery query = new WorkSearchQuery(request);
		query.buildBlockedFilter();
		assertFoundFilterQuery(query, "-(companyId:(1) AND resourceWorkStatusTypeCode:available)");
	}

	/**
	 * Test to make sure that unicode chars are not stripped away for "type=all"
	 */
	@Test
	public void buildWorkKeywordFilterWithUtf8Chars() {
		request.setWorkSearchRequestUserType(WorkSearchRequestUserType.CLIENT);
		request.setShowAllAtCompany(true);
		request.setType("all");
		request.setKeyword("乌鸦食物");
		WorkSearchTransientData transientData = buildTransientData();
		WorkSearchQuery query = new WorkSearchQuery(request).addWorkKeywordFilter(transientData);
		assertTrue(query.get("q").equals("乌鸦食物"));
	}

	@Test
	public void getRoleSpecificFilterQuery_isBuyerSearch_isShowAllAtCompany() {
		request.setWorkSearchRequestUserType(WorkSearchRequestUserType.CLIENT);
		request.setShowAllAtCompany(true);
		WorkSearchQuery query = new WorkSearchQuery(request);
		WorkSearchTransientData transientData = buildTransientData();
		assertEquals(
			String.format("companyId:(%s)", companyId),
			query.getRoleSpecificFilterQuery(transientData)
		);
	}

	@Test
	public void getRoleSpecificFilterQuery_isBuyerSearch_isNotShowAllAtCompany() {
		request.setWorkSearchRequestUserType(WorkSearchRequestUserType.CLIENT);
		request.setShowAllAtCompany(false);
		WorkSearchQuery query = new WorkSearchQuery(request);
		WorkSearchTransientData transientData = buildTransientData();
		assertEquals(
			String.format("buyerUserId:(%s)", userId),
			query.getRoleSpecificFilterQuery(transientData)
		);
	}

	@Test
	public void getRoleSpecificFilterQuery_isWorkerSearch_isShowAllAtCompany_isDispatcher() {
		request.setWorkSearchRequestUserType(WorkSearchRequestUserType.RESOURCE);
		request.setShowAllAtCompany(true);
		request.setDispatcher(true);
		WorkSearchQuery query = new WorkSearchQuery(request);
		WorkSearchTransientData transientData = buildTransientData();
		assertEquals(
			String.format(
				"((assignedResourceCompanyId:(%s) OR (-assignedResourceCompanyId:(*) AND resourceWorkStatusTypeCode:available AND workResourceCompanyIds:(%s))) OR ((workResourceIds:(%s)  OR applicantIds:(%s))  AND -cancelledWorkResourceIds:(%s)  AND resourceWorkStatusTypeCode:%s))",
				companyId, companyId, userId, userId, userId, WorkStatusType.AVAILABLE
			),
			query.getRoleSpecificFilterQuery(transientData)
		);
	}

	@Test
	public void getRoleSpecificFilterQuery_isWorkerSearch_isShowAllAtCompany_isNotDispatcher() {
		request.setWorkSearchRequestUserType(WorkSearchRequestUserType.RESOURCE);
		request.setShowAllAtCompany(true);
		request.setDispatcher(false);
		WorkSearchQuery query = new WorkSearchQuery(request);
		WorkSearchTransientData transientData = buildTransientData();
		assertEquals(
			String.format(
				"(assignedResourceCompanyId:(%s) OR ((workResourceIds:(%s)  OR applicantIds:(%s))  AND -cancelledWorkResourceIds:(%s)  AND resourceWorkStatusTypeCode:%s))",
				companyId, userId, userId, userId, WorkStatusType.AVAILABLE
			),
			query.getRoleSpecificFilterQuery(transientData)
		);
	}

	@Test
	public void getRoleSpecificFilterQuery_isWorkerSearch_isNotShowAllAtCompany() {
		request.setWorkSearchRequestUserType(WorkSearchRequestUserType.RESOURCE);
		request.setShowAllAtCompany(false);
		WorkSearchQuery query = new WorkSearchQuery(request);
		WorkSearchTransientData transientData = buildTransientData();
		assertEquals(
			String.format(
				"(assignedResourceId:(%s) OR ((workResourceIds:(%s)  OR applicantIds:(%s))  AND -cancelledWorkResourceIds:(%s)  AND resourceWorkStatusTypeCode:%s))",
				userId, userId, userId, userId, WorkStatusType.AVAILABLE
			),
			query.getRoleSpecificFilterQuery(transientData)
		);
	}

	@Test
	public void testAddWorkAssignedResourceFilter_containsWorker() {
		DashboardResource resource = new DashboardResource();
		resource.setResourceId(1L);
		request.setAssignedResources(Sets.newHashSet(resource));
		WorkSearchQuery query = new WorkSearchQuery(request);
		query.addWorkAssignedResourceFilter();
		assertTrue(StringUtils.join(query.getFilterQueries(), " ").contains("assignedResourceId:1"));
	}

	@Test
	public void testAddWorkAssignedResourceFilter_containsVendor() {
		DashboardResource resource = new DashboardResource();
		resource.setResourceCompanyId(1L);
		request.setAssignedResources(Sets.newHashSet(resource));
		WorkSearchQuery query = new WorkSearchQuery(request);
		query.addWorkAssignedResourceFilter();
		assertTrue(StringUtils.join(query.getFilterQueries(), " ").contains("assignedResourceCompanyId:1"));
	}

	@Test
	public void testAddWorkAssignedResourceFilter_empty() {
		DashboardResource resource = new DashboardResource();
		request.setAssignedResources(Sets.newHashSet(resource));
		WorkSearchQuery query = new WorkSearchQuery(request);
		query.addWorkAssignedResourceFilter();
		assertNull(query.getFilterQueries());
	}

	@Test
	public void testAddWorkAssignedResourceFilter_inValidId() {
		DashboardResource resource = new DashboardResource();
		resource.setResourceId(0L); // id should not be zero
		request.setAssignedResources(Sets.newHashSet(resource));
		WorkSearchQuery query = new WorkSearchQuery(request);
		query.addWorkAssignedResourceFilter();
		assertNull(query.getFilterQueries());
	}

	private void assertFoundFilterQuery(WorkSearchQuery query, String fqName) {
		boolean found = false;
		for (String fq: query.getFilterQueries()) {
			if (fq.contains(fqName)) {
				found = true;
				break;
			}
		}
		assertTrue(found);
	}

	private void assertFoundFacetField(WorkSearchQuery query, String fqName) {
		boolean found = false;
		for (String fq : query.getFacetFields()) {
			if (fq.contains(fqName)) {
				found = true;
				break;
			}
		}
		assertTrue(found);
	}

	private WorkSearchTransientData buildTransientData() {
		WorkSearchTransientData transientData = new WorkSearchTransientData();
		SearchUser searchUser = new SearchUser();
		searchUser.setCompanyId(companyId);
		searchUser.setId(userId);
		transientData.setCurrentUser(searchUser);
		return transientData;
	}
}

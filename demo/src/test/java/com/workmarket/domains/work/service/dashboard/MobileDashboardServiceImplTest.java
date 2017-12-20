package com.workmarket.domains.work.service.dashboard;

import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.search.request.SearchSortDirection;
import com.workmarket.search.request.work.WorkSearchRequest;
import com.workmarket.search.request.work.WorkSearchSortType;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class MobileDashboardServiceImplTest {
  private MobileDashboardServiceImpl mobileDashboardService = new MobileDashboardServiceImpl();

  @Test
  public void shouldBuildRequestDueDateAscending() {
    final WorkSearchRequest request =
        mobileDashboardService
            .buildSearchRequest("user_number", new WorkStatusType(WorkStatusType.ACTIVE), 1, 10, "dueDate");

    assertEquals(WorkSearchSortType.DUE_DATE, request.getSortBy());
    assertEquals(SearchSortDirection.ASCENDING, request.getSortDirection());
  }

  @Test
  public void shouldBuildRequestDueDateDescending() {
    final WorkSearchRequest request =
        mobileDashboardService
            .buildSearchRequest("user_number", new WorkStatusType(WorkStatusType.ACTIVE), 1, 10, "-dueDate");

    assertEquals(WorkSearchSortType.DUE_DATE, request.getSortBy());
    assertEquals(SearchSortDirection.DESCENDING, request.getSortDirection());
  }

  @Test
  public void shouldBuildRequestDefaultSortOnUnrecognizableSortField() {
    final WorkSearchRequest request =
        mobileDashboardService
            .buildSearchRequest("user_number", new WorkStatusType(WorkStatusType.ACTIVE), 1, 10, "foobar");

    assertEquals(WorkSearchSortType.SCHEDULED_FROM, request.getSortBy());
    assertEquals(SearchSortDirection.DESCENDING, request.getSortDirection());
  }

  @Test
  public void shouldBuildRequestDefaultSortOnEmptyString() {
    final WorkSearchRequest request =
        mobileDashboardService
            .buildSearchRequest("user_number", new WorkStatusType(WorkStatusType.ACTIVE), 1, 10, "");

    assertEquals(WorkSearchSortType.SCHEDULED_FROM, request.getSortBy());
    assertEquals(SearchSortDirection.DESCENDING, request.getSortDirection());
  }
}
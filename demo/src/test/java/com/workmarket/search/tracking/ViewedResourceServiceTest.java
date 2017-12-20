package com.workmarket.search.tracking;

import com.workmarket.dao.ViewedResourceTrackingDAO;
import com.workmarket.domains.model.VisitedResource;
import com.workmarket.service.tracking.ViewedResourceTrackingService;
import com.workmarket.service.tracking.ViewedResourceTrackingServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ViewedResourceServiceTest {

	@Mock ViewedResourceTrackingDAO viewedResourceTrackingDAO;

	@InjectMocks ViewedResourceTrackingService viewedResourceTrackingService = new ViewedResourceTrackingServiceImpl();

	VisitedResource visitedResource;
	List visitedResources;

	public static final Long EMPLOYEE_USER_ID = 9999999L;

	@Before
	public void setup() {
		visitedResources = mock(List.class);

		when(viewedResourceTrackingDAO.getViewedResourcesListByUserId(EMPLOYEE_USER_ID)).thenReturn(visitedResources);
		when(viewedResourceTrackingDAO.merge(visitedResource)).thenReturn(visitedResource);
	}

	@Test
	public void getViewedResourcesListByUserId_withUserId_GetsViewedResourcesListByUserId() {
		viewedResourceTrackingService.getViewedResourcesListByUserId(EMPLOYEE_USER_ID);
		verify(viewedResourceTrackingDAO).getViewedResourcesListByUserId(EMPLOYEE_USER_ID);
	}

	@Test
	public void getViewedResourcesListByUserId_withUserId_ReturnsViewedResourcesList() {
		List<String> actual = viewedResourceTrackingService.getViewedResourcesListByUserId(EMPLOYEE_USER_ID);
		assertThat(actual, is(visitedResources));
	}

	@Test
	public void merge_withVisitedResource_callsMerge() {
		viewedResourceTrackingService.merge(visitedResource);
		verify(viewedResourceTrackingDAO).merge(visitedResource);
	}

	@Test
	public void merge_withVisitedResource_returnsVisitedResource() {
		VisitedResource saved = viewedResourceTrackingService.merge(visitedResource);
		assertThat(saved, is(visitedResource));
	}

}

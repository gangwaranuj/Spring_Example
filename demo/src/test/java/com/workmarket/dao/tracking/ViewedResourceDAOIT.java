package com.workmarket.dao.tracking;

import com.workmarket.dao.ViewedResourceTrackingDAO;
import com.workmarket.domains.model.VisitedResource;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.test.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * Created by ant on 9/23/14.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
@Transactional
public class ViewedResourceDAOIT extends BaseServiceIT {

	@Autowired protected ViewedResourceTrackingDAO viewedResourceTrackingDAO;

	@Test(expected=IllegalArgumentException.class)
	public void getViewedResourcesListByUserId_withNullUserId_throwsIllegalArgumentException() {
		viewedResourceTrackingDAO.getViewedResourcesListByUserId(null);
	}

	@Test
	public void getViewedResourcesListByUserId_withUserId_returnsEmptyStringList() {
		List<String> viewedResources = viewedResourceTrackingDAO.getViewedResourcesListByUserId(EMPLOYEE_USER_ID);
		assertThat(viewedResources, is(notNullValue()));
	}

	@Test
	public void merge_WithANewViewedResource_savesTheViewedResource_returnsMergedViewedResourceWithId() {
		VisitedResource viewedResource = new VisitedResource();
		viewedResource.setResourceName("an example visited resource test");
		viewedResource.setUserId(EMPLOYEE_USER_ID);

		VisitedResource savedViewedResource = viewedResourceTrackingDAO.merge(viewedResource);
		assertThat(savedViewedResource.getId(), is(notNullValue()));
	}

	@Test
	public void merge_WithANewViewedResource_savesTheViewedResource_verifiesViewedResourceWasSaved() {
		VisitedResource viewedResource = new VisitedResource();
		viewedResource.setResourceName("an example visited resource");
		viewedResource.setUserId(EMPLOYEE_USER_ID);

		VisitedResource savedViewedResource = viewedResourceTrackingDAO.merge(viewedResource);
		assertThat(savedViewedResource.getResourceName(), equalTo("an example visited resource"));
	}

	@Test(expected=IllegalArgumentException.class)
	public void merge_withNullUserId_throwsIllegalArgumentException() {
		VisitedResource viewedResource = new VisitedResource();
		viewedResource.setResourceName("example visited resource merge_withNullUserId_throwsIllegalArgumentException");
		viewedResource.setUserId(null);

		viewedResourceTrackingDAO.merge(viewedResource);
	}

	@Test(expected=IllegalArgumentException.class)
	public void merge_withNullVisitedResource_throwsIllegalArgumentException() {
		viewedResourceTrackingDAO.merge(null);
	}

	@Test(expected=IllegalArgumentException.class)
	public void merge_withNullResourceName_throwsIllegalArgumentException() {
		VisitedResource viewedResource = new VisitedResource();
		viewedResource.setResourceName(null);
		viewedResource.setUserId(EMPLOYEE_USER_ID);

		viewedResourceTrackingDAO.merge(viewedResource);
	}

	@Test(expected=IllegalArgumentException.class)
	public void merge_withEmptyString_throwsIllegalArgumentException() {
		VisitedResource viewedResource = new VisitedResource();
		viewedResource.setResourceName("");
		viewedResource.setUserId(EMPLOYEE_USER_ID);

		viewedResourceTrackingDAO.merge(viewedResource);
	}

}
package com.workmarket.dao.tracking;

import com.workmarket.dao.ViewedResourceTrackingDAO;
import com.workmarket.dao.ViewedResourceTrackingDAOImpl;
import com.workmarket.domains.model.VisitedResource;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

/**
 * Created by ant on 9/23/14.
 */
@RunWith(MockitoJUnitRunner.class)
public class ViewedResourceDAOTest {

	public static final Long EMPLOYEE_USER_ID = 1L;

	@Mock SessionFactory factory;
	@Mock Session session;
	@Mock Query query;

	VisitedResource visitedResource = new VisitedResource();

	@InjectMocks ViewedResourceTrackingDAO viewedResourceTrackingDAO = new ViewedResourceTrackingDAOImpl();

	@Before
	public void setup() {
		factory =  mock(SessionFactory.class);
		session = mock(Session.class);
		query = mock(Query.class);
		viewedResourceTrackingDAO.setSessionFactory(factory);
		when(factory.getCurrentSession()).thenReturn(session);
		when(session.getNamedQuery("VisitedResource.getByUserId")).thenReturn(query);


		visitedResource.setUserId(EMPLOYEE_USER_ID);
		visitedResource.setResourceName("an example visited resource test");
	}

	@Test
	public void getViewedResourcesListByUserId_withUserId_returnsStringList() {
		viewedResourceTrackingDAO.getViewedResourcesListByUserId(EMPLOYEE_USER_ID);
		verify(session).getNamedQuery("VisitedResource.getByUserId");
	}

	@Test(expected=IllegalArgumentException.class)
	public void getViewedResourcesListByUserId_withUserId_throwsIllegalArgumentexception() {
		viewedResourceTrackingDAO.getViewedResourcesListByUserId(null);
	}

	@Test
	public void merge_withUserId_callsMergeOnVisitedResource() {
		viewedResourceTrackingDAO.merge(visitedResource);
		verify(session).merge(visitedResource);
	}

	@Test(expected=IllegalArgumentException.class)
	public void merge_withNullUserId_throwsIllegalArgumentException() {
		visitedResource.setUserId(null);
		viewedResourceTrackingDAO.merge(visitedResource);
	}

	@Test(expected=IllegalArgumentException.class)
	public void merge_withNullResourceName_throwsIllegalArgumentException() {
		visitedResource.setResourceName(null);
		viewedResourceTrackingDAO.merge(visitedResource);
	}

	@Test(expected=IllegalArgumentException.class)
	public void merge_withEmptyResourceName_throwsIllegalArgumentException() {
		visitedResource.setResourceName("");
		viewedResourceTrackingDAO.merge(visitedResource);
	}

}

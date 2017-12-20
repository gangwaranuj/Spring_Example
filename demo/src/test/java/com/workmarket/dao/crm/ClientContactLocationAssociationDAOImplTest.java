package com.workmarket.dao.crm;

import com.workmarket.domains.model.crm.ClientContact;
import com.workmarket.domains.model.crm.ClientContactLocationAssociation;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projection;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * User: alexsilva Date: 4/21/14 Time: 3:00 PM
 */
@RunWith(MockitoJUnitRunner.class)
public class ClientContactLocationAssociationDAOImplTest {

	@Mock SessionFactory sessionFactory;
	@InjectMocks ClientContactLocationAssociationDAOImpl dao;

	Criteria criteria;
	Criterion criterion;
	Session session;
	ClientContact clientContact;

	Long TEST_ID = 1L;

	@Before
	public void setup() {
		session = mock(Session.class);
		criteria = mock(Criteria.class, RETURNS_DEEP_STUBS);
		criterion = mock(Criterion.class);
		clientContact = mock(ClientContact.class);

		when(sessionFactory.getCurrentSession()).thenReturn(session);
		when(session.createCriteria(ClientContactLocationAssociation.class)).thenReturn(criteria);
		when(criteria.add(any(Criterion.class))).thenReturn(criteria);
		when(criteria.setProjection(any(Projection.class))).thenReturn(criteria);
		when(criteria.setFirstResult(anyInt())).thenReturn(criteria);
		when(criteria.setMaxResults(anyInt())).thenReturn(criteria);
		when(criteria.createAlias(anyString(), anyString(), anyInt())).thenReturn(criteria);
		when(clientContact.getFullName()).thenReturn("Jorbin Kerns");
	}

	@Test
	public void test_getLocationCountByClientContact() {
		dao.getLocationCountByClientContact(TEST_ID);
	}

	@Test
	public void test_getContactCountByClientLocation() {
		dao.getContactCountByClientLocation(TEST_ID);
	}

	@Test
	public void test_findFirstContactNameByClientLocation() {
		when(criteria.list().get(0)).thenReturn(clientContact);
		dao.findFirstContactNameByClientLocation(TEST_ID);
	}
}

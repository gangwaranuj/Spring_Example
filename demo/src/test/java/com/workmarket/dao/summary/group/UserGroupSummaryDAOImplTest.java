package com.workmarket.dao.summary.group;

import com.workmarket.domains.model.summary.group.UserGroupSummary;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Criterion;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserGroupSummaryDAOImplTest {

	@Mock SessionFactory sessionFactory;
	@InjectMocks UserGroupSummaryDAOImpl dao = spy(new UserGroupSummaryDAOImpl());

	Criteria criteria;
	Criterion criterion;
	UserGroupSummary userGroupSummary;
	Session session;

	@Before
	public void setup() {
		userGroupSummary = mock(UserGroupSummary.class);

		session = mock(Session.class);

		criteria = mock(Criteria.class);
		criterion = mock(Criterion.class);
		when(criteria.add(any(Criterion.class))).thenReturn(criteria);
		when(criteria.uniqueResult()).thenReturn(userGroupSummary);
		doReturn(criterion).when(dao).getRestrictions(any(String.class), any(long.class));

		when(sessionFactory.getCurrentSession()).thenReturn(session);
		when(session.createCriteria(UserGroupSummary.class)).thenReturn(criteria);
	}

	@Test
	public void findByUserGroup_dao_returnsUserGroupSummary() {
		UserGroupSummary ugs = dao.findByUserGroup(1);
		assertEquals(ugs, userGroupSummary);
	}

	@Test
	public void findByUserGroup_dao_restrictById() {
		long id = 1;
		dao.findByUserGroup(id);
		verify(dao).getRestrictions("userGroup.id", id);
	}
}

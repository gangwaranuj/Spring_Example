package com.workmarket.dao;

import com.workmarket.domains.model.VisitedResource;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class ViewedResourceTrackingDAOImpl implements ViewedResourceTrackingDAO {

	@Resource(name = "sessionFactory")
	protected SessionFactory factory;

	// exposed to allow for mocking only. do not use.
	public void setSessionFactory(SessionFactory factory) {
		this.factory = factory;
	}

	public List<String> getViewedResourcesListByUserId(Long userId) {
		Assert.notNull(userId);
		Query query = factory.getCurrentSession().getNamedQuery("VisitedResource.getByUserId");
		query.setParameter("userId", userId);
		return query.list();
	}

	public VisitedResource merge(VisitedResource visitedResource) {
		Assert.notNull(visitedResource);
		Assert.notNull(visitedResource.getUserId());
		Assert.notNull(visitedResource.getResourceName());
		Assert.isTrue(!visitedResource.getResourceName().equals(""), "visited resource name must not be empty");
		return (VisitedResource) factory.getCurrentSession().merge(visitedResource);
	}

}

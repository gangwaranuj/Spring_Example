package com.workmarket.dao;

import com.workmarket.domains.model.VisitedResource;
import org.hibernate.SessionFactory;

import java.util.List;

public interface ViewedResourceTrackingDAO {
	public List<String> getViewedResourcesListByUserId(Long userId);
	public VisitedResource merge(VisitedResource visitedResource);
	public void setSessionFactory(SessionFactory factory);
}

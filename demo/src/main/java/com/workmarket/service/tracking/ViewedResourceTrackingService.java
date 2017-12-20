package com.workmarket.service.tracking;

import com.workmarket.domains.model.VisitedResource;

import java.util.List;

public interface ViewedResourceTrackingService {
	public List<String> getViewedResourcesListByUserId(Long userId);
	public VisitedResource merge(VisitedResource visitedResource);
}

package com.workmarket.service.tracking;

import com.workmarket.dao.ViewedResourceTrackingDAO;
import com.workmarket.domains.model.VisitedResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ViewedResourceTrackingServiceImpl implements ViewedResourceTrackingService {

	@Autowired protected ViewedResourceTrackingDAO viewedResourceTrackingDAO;

	public List<String> getViewedResourcesListByUserId(Long userId) {
		return viewedResourceTrackingDAO.getViewedResourcesListByUserId(userId);
	}

	public VisitedResource merge(VisitedResource visitedResource) {
		return viewedResourceTrackingDAO.merge(visitedResource);
	}

}

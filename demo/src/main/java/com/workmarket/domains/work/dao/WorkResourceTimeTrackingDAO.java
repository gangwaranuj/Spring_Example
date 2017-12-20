package com.workmarket.domains.work.dao;

import java.util.List;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.work.model.WorkResourceTimeTracking;

public interface WorkResourceTimeTrackingDAO extends DAOInterface<WorkResourceTimeTracking> {

	WorkResourceTimeTracking findById(long id);
	
	WorkResourceTimeTracking findLatestByWorkResource(long workResourceId);
	
	List<WorkResourceTimeTracking> findAllByWorkResourceId(long workResourceId);
}

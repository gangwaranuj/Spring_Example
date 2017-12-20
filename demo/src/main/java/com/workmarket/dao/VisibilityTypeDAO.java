package com.workmarket.dao;

import com.workmarket.domains.model.VisibilityType;
import java.util.List;

public interface VisibilityTypeDAO extends DAOInterface<VisibilityType> {
	List<VisibilityType> getVisibilityTypes();
}

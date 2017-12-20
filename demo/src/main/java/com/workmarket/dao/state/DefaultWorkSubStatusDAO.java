package com.workmarket.dao.state;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.DefaultWorkSubStatusType;

import java.util.List;

public interface DefaultWorkSubStatusDAO extends DAOInterface<DefaultWorkSubStatusType> {

	List<DefaultWorkSubStatusType> findAll();
}

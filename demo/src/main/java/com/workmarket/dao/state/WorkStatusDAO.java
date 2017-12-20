package com.workmarket.dao.state;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.WorkStatusType;

import java.util.List;


public interface WorkStatusDAO extends DAOInterface<WorkStatusType> {
	WorkStatusType findByCode(String code);
	List<WorkStatusType> findByCode(String... codes);

}

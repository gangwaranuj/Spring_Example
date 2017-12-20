package com.workmarket.dao;

import com.workmarket.domains.model.DressCode;

import java.util.List;

@Deprecated
public interface DressCodeDAO extends DAOInterface<DressCode>{

	DressCode findDressCodeById(Long id);
	
	List<DressCode> findAllDressCodes();

}

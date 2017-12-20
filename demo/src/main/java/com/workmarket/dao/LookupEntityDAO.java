package com.workmarket.dao;

import java.util.List;

import com.workmarket.domains.model.LookupEntity;

public interface LookupEntityDAO extends DAOInterface<LookupEntity>{
	<T extends LookupEntity> List<T> findLookupEntities(Class<T> clazz);
	<T extends LookupEntity> T findByCode(Class<? extends LookupEntity> clazz, String code);
	<T extends LookupEntity> T findByCodeWithDefault(Class<? extends LookupEntity> clazz, String code, T defaultResult);
}

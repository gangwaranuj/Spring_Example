package com.workmarket.dao;

import com.workmarket.dto.SuggestionDTO;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DAOInterface<T> {
	T getOrInitializeBy(Object... objects);
	T findBy(Object... objects);
	List<T> findAllBy(Object... objects);
	List<Long> findAllEntityIdsBy(Object... objects);

	Map<String, Object> getProjectionMapById(Long id, String... properties);
	Map<Long, Map<String, Object>> getProjectionMapByIds(List<Long> ids, String... properties);

	void saveOrUpdate(T entity);
	void saveAll(Collection<T> entities);
	void persist(T entity);

	T get(Long primaryKey);
	List<T> get(Long... primaryKeys);
	List<T> get(Collection<Long> primaryKeys);

	List<T> getAll();
	List<T> getAll(int start, int limit);
	List<Long> getAllIds();

	void initialize(T entity);
	void initialize(Collection<? extends T> collection);

	void delete(T entity);

	void delete(Set<T> entities);

	void refresh(T entity);

	List<SuggestionDTO> suggest(String prefix, String property);

	boolean existsBy(Object... objects);
}

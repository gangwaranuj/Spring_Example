package com.workmarket.domains.work.dao;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.work.model.WorkResourceAggregateFilter;
import com.workmarket.domains.work.model.WorkResourceLabel;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface WorkResourceLabelDAO extends DAOInterface<WorkResourceLabel> {

	WorkResourceLabel findByLabelCodeAndWorkResourceId(String workResourceLabelTypeCode, Long workResourceId);

	List<WorkResourceLabel> findByWorkResource(Long workResourceId);

	Integer countConfirmedWorkResourceLabelByUserId(List<Long> userIds, WorkResourceAggregateFilter filter);

	Map<Long,List<WorkResourceLabel>> findVisibleForWork(Long workId);

	Map<String, Integer> countAllConfirmedWorkResourceLabelsByUserId(WorkResourceAggregateFilter filter, List<Long> userIds);

	Map<Long,List<WorkResourceLabel>> findConfirmedForUserByCompanyInWork(Long userId, Long viewingUserId, Long viewingCompanyId, Collection<Long> workIds);
}

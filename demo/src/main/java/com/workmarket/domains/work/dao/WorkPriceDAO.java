package com.workmarket.domains.work.dao;

import com.google.common.base.Optional;
import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.WorkPrice;

import java.util.List;

public interface WorkPriceDAO extends DAOInterface<WorkPrice> {

	public List<WorkPrice> findPriceHistoryForWork(Long workId);

	public Optional<WorkPrice> findOriginalPriceHistoryForWork(Long workId);
}

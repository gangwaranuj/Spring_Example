package com.workmarket.dao.summary;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.summary.HistorySummaryEntity;
import org.springframework.stereotype.Repository;

/**
 * Author: rocio
 */
@Repository
public class HistorySummaryEntityDAOImpl<T extends HistorySummaryEntity> extends AbstractDAO<T> implements HistorySummaryEntityDAO<T> {

	@Override
	protected Class<HistorySummaryEntity> getEntityClass() {
		return HistorySummaryEntity.class;
	}

}

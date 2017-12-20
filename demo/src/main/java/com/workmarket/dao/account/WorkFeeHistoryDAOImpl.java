package com.workmarket.dao.account;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.account.WorkFeeHistory;
import org.springframework.stereotype.Repository;

@Repository
public class WorkFeeHistoryDAOImpl extends AbstractDAO<WorkFeeHistory> implements WorkFeeHistoryDAO  {

	protected Class<WorkFeeHistory> getEntityClass() {
        return WorkFeeHistory.class;
    }

}

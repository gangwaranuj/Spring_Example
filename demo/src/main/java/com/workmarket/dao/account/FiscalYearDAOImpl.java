package com.workmarket.dao.account;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.account.FiscalYear;
import org.springframework.stereotype.Repository;

@Repository
public class FiscalYearDAOImpl extends AbstractDAO<FiscalYear> implements FiscalYearDAO {

	protected Class<FiscalYear> getEntityClass() {
		return FiscalYear.class;
	}
}

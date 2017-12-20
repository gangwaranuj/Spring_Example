package com.workmarket.domains.payments.dao;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.invoice.CreditMemo;
import org.springframework.stereotype.Repository;

@Repository
public class CreditMemoDAOImpl extends AbstractDAO<CreditMemo> implements CreditMemoDAO {

	@Override
	protected Class<?> getEntityClass() {
		return CreditMemo.class;
	}
}

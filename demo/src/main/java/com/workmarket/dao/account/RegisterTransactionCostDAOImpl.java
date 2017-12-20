package com.workmarket.dao.account;

import org.springframework.stereotype.Repository;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.account.RegisterTransactionCost;


@Repository
public class RegisterTransactionCostDAOImpl extends AbstractDAO<RegisterTransactionCost> implements RegisterTransactionCostDAO {
	
	protected Class<RegisterTransactionCost> getEntityClass() {
        return RegisterTransactionCost.class;
    }
	
}

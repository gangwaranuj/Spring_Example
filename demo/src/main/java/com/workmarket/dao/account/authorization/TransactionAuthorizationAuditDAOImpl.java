package com.workmarket.dao.account.authorization;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.account.authorization.TransactionAuthorizationAudit;
import org.springframework.stereotype.Repository;

/**
 * author: rocio
 */
@Repository
public class TransactionAuthorizationAuditDAOImpl extends AbstractDAO<TransactionAuthorizationAudit> implements TransactionAuthorizationAuditDAO {

	@Override
	protected Class<TransactionAuthorizationAudit> getEntityClass() {
		return TransactionAuthorizationAudit.class;
	}
}

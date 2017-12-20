package com.workmarket.domains.payments.dao;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.account.FastFundsReceivableCommitment;

public interface FastFundsReceivableCommitmentDAO extends DAOInterface<FastFundsReceivableCommitment> {

	FastFundsReceivableCommitment findCommitmentByWorkId(long workId);
}

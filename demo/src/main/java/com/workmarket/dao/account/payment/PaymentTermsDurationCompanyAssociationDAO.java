package com.workmarket.dao.account.payment;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.account.payment.PaymentTermsDurationCompanyAssociation;

import java.util.List;

/**
 * Created by nick on 9/18/12 12:29 PM
 */
public interface PaymentTermsDurationCompanyAssociationDAO extends DAOInterface<PaymentTermsDurationCompanyAssociation> {

	@SuppressWarnings("unchecked") List<PaymentTermsDurationCompanyAssociation> findPaymentTermsDurationCompanyAssociationByCompanyId(Long companyId);
}

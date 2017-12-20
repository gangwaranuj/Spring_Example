package com.workmarket.dao.tax;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.tax.TaxVerificationRequest;

import java.util.List;

/**
 * Created by nick on 11/29/12 4:29 PM
 */
public interface TaxVerificationRequestDAO extends DAOInterface<TaxVerificationRequest>{

	List<TaxVerificationRequest> findTaxVerificationRequests();

}

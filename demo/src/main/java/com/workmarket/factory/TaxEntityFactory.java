package com.workmarket.factory;

import com.workmarket.domains.model.tax.AbstractTaxEntity;
import com.workmarket.service.business.dto.TaxEntityDTO;

public interface TaxEntityFactory {
	AbstractTaxEntity newInstance(String country) throws InstantiationException;

	AbstractTaxEntity newInstance(TaxEntityDTO dto) throws InstantiationException;
}

package com.workmarket.api.v2.employer.settings.services;

import com.workmarket.api.v2.employer.settings.models.TaxInfoDTO;
import com.workmarket.thrift.core.ValidationException;

public interface TaxInfoService {
	TaxInfoDTO save(TaxInfoDTO taxInfoDTO) throws ValidationException;
}

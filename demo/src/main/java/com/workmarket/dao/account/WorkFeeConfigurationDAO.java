package com.workmarket.dao.account;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.account.WorkFeeConfiguration;

public interface WorkFeeConfigurationDAO extends DAOInterface<WorkFeeConfiguration>{
	
	WorkFeeConfiguration findWithWorkFeeBands(Long accountRegisterId);

}

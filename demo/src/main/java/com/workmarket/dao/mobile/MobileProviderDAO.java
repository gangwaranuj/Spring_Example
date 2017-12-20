package com.workmarket.dao.mobile;

import java.util.List;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.MobileProvider;

public interface MobileProviderDAO extends DAOInterface<MobileProvider>{

	List<MobileProvider> findAllMobileProviders();

	MobileProvider findMobileProviderById(Long mobileProviderId);
}

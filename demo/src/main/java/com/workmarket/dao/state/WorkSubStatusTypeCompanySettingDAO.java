package com.workmarket.dao.state;

import java.util.List;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.work.model.state.WorkSubStatusTypeCompanySetting;

public interface WorkSubStatusTypeCompanySettingDAO extends DAOInterface<WorkSubStatusTypeCompanySetting> {

	WorkSubStatusTypeCompanySetting findWorkSubStatusTypeCompanySettingByWorkSubStatusAndCompany(Long workSubStatusTypeId, Long companyId);
	
	List<WorkSubStatusTypeCompanySetting> findWorkSubStatusTypeCompanySettingByCompany(Long companyId);
}

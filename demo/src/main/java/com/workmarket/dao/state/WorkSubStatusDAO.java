package com.workmarket.dao.state;

import com.workmarket.dao.DAOInterface;
import com.workmarket.data.report.work.WorkSubStatusTypeCompanyConfig;
import com.workmarket.data.report.work.WorkSubStatusTypeReportRow;
import com.workmarket.domains.model.filter.WorkSubStatusTypeFilter;
import com.workmarket.domains.work.model.state.WorkSubStatusType;

import java.util.List;
import java.util.Map;

public interface WorkSubStatusDAO extends DAOInterface<WorkSubStatusType> {

	WorkSubStatusType findByCode(String code);

	WorkSubStatusType findById(long workSubStatusTypeId);

	WorkSubStatusType findByIdAndCompany(long workSubStatusTypeId, long companyId);

	WorkSubStatusType findByCodeAndCompany(String code, long companyId);

	WorkSubStatusType findSystemWorkSubStatus(String code);

	WorkSubStatusType findCustomWorkSubStatus(String code, Long companyId);

	List<WorkSubStatusType> findByCode(String... codes);

	List<WorkSubStatusType> findAllSubStatusesByCompany(WorkSubStatusTypeFilter filter, long companyId);

	List<WorkSubStatusType> findAllUnresolvedSubStatusTypeWithColorByWork(Long workId);

	List<WorkSubStatusType> findAllUnresolvedSubStatusTypeByWork(Long workId);

	Map<Long, List<WorkSubStatusTypeReportRow>> getUnresolvedWorkSubStatusTypeWorkMap(WorkSubStatusTypeFilter filter, List<Long> workIds);

	Map<Long, List<WorkSubStatusType>> findAllUnresolvedSubStatusType(List<Long> workIds);

	Map<String, WorkSubStatusTypeCompanyConfig> findAllWorkSubStatusColorConfiguration();

}

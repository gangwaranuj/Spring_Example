package com.workmarket.dao;

import com.workmarket.domains.model.reporting.ReportingCriteria;

import java.util.List;


/**
 * @since 8/2/2011
 *
 */
public interface ReportingCriteriasDAO extends DAOInterface<ReportingCriteria>{

	/**
	 *
	 *
	 * @param reportKey
	 * @return
	 */
	public ReportingCriteria findByReportKey(Long reportKey);

	/**
	 *
	 * @param reportKeys
	 * @return
	 */
	public List<ReportingCriteria> findByReportKeys(List<Long> reportKeys);

	/**
	 * @param companyId
	 * @return
	 * @throws Exception
	 */
	public List<ReportingCriteria> findByCompanyId(Long companyId);
	
	/**
	 * @return
	 * @throws Exception
	 */
	public List<ReportingCriteria> findAll();


}

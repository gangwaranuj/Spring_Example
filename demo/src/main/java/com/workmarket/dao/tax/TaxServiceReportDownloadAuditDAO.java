package com.workmarket.dao.tax;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.tax.TaxServiceReportDownloadAudit;

/**
 * Created by zhe
 */
public interface TaxServiceReportDownloadAuditDAO extends DAOInterface<TaxServiceReportDownloadAudit> {

	TaxServiceReportDownloadAudit findByTaxServiceReportId(long taxServiceReportId);
}

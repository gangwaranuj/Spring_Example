package com.workmarket.dao.tax;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.tax.EarningReportDownloadAudit;
import org.springframework.stereotype.Component;

@Component
public class EarningReportDownloadAuditDAOImpl extends AbstractDAO<EarningReportDownloadAudit> implements EarningReportDownloadAuditDAO {
	
	protected Class<EarningReportDownloadAudit> getEntityClass() {
		return EarningReportDownloadAudit.class;
	}

}

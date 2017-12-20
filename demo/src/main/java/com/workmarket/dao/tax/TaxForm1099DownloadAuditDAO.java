package com.workmarket.dao.tax;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.tax.TaxForm1099;
import com.workmarket.domains.model.tax.TaxForm1099DownloadAudit;

import java.util.List;

public interface TaxForm1099DownloadAuditDAO extends DAOInterface<TaxForm1099DownloadAudit> {

	TaxForm1099DownloadAudit findByTaxForm1099Id(long taxForm1099Id);

	List<TaxForm1099> findAllUndownloadedTaxForm1099ByTaxForm1099SetId(long taxFormSetId);

}
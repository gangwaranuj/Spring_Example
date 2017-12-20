package com.workmarket.dao.tax;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.tax.AbstractTaxReportSet;
import java.util.List;

public interface TaxReportSetDAO<T extends AbstractTaxReportSet> extends DAOInterface<T> {

	List<T> findAllTaxReportSets();

	List<T> findAllTaxReportSetsByYear(int year);

	T findPublishedTaxReportForYear(Integer year);

	T findLatestPublishedTaxReport();

}
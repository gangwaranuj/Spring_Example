package com.workmarket.service.business;

import com.google.common.base.Optional;
import com.workmarket.dao.ReportingCriteriasDAO;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.reporting.AbstractReportFilterBuilder;
import com.workmarket.domains.model.reporting.CustomReportUpdateResponse;
import com.workmarket.domains.model.reporting.Entity;
import com.workmarket.domains.model.reporting.EntityResponseForReport;
import com.workmarket.domains.model.reporting.ReportFilter;
import com.workmarket.domains.model.reporting.ReportRequestData;
import com.workmarket.domains.model.reporting.ReportingContext;
import com.workmarket.domains.model.reporting.ReportingCriteria;
import com.workmarket.domains.model.reporting.ReportingCriteriaFiltering;
import com.workmarket.reporting.format.CurrencyFormat;
import com.workmarket.reporting.format.DateFormat;
import com.workmarket.reporting.format.Format;
import com.workmarket.reporting.format.HyperTextFormat;
import com.workmarket.reporting.format.TitleLinkFormat;
import com.workmarket.reporting.format.WMDecimalFormat;
import com.workmarket.reporting.service.WorkReportGeneratorServiceImpl;
import com.workmarket.thrift.work.display.FilteringEntityRequest;
import com.workmarket.thrift.work.display.ReportResponse;
import com.workmarket.thrift.work.display.ReportRow;
import com.workmarket.thrift.work.display.ReportingReportType;
import com.workmarket.thrift.work.display.WorkDisplay;
import com.workmarket.thrift.work.display.WorkDisplayException;
import com.workmarket.thrift.work.display.WorkReportEntityBucketsCompositeResponse;
import com.workmarket.thrift.work.report.WorkReportColumnType;
import com.workmarket.web.models.DataTableColumnHeader;
import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
public class CustomReportServiceImpl implements CustomReportService {

	@Autowired private ReportingCriteriasDAO reportingCriteriasDAO;
	@Autowired private WorkReportGeneratorServiceImpl workReportGeneratorService;
	@Autowired private WorkDisplay.Iface workDisplayHandler;
	@Autowired private ReportingContext workReportingContext;
	@Resource(name = "reporting_context") private BidiMap reportingContext;
	@Resource(name = "work_report_column_types") private BidiMap workReportColumnTypes;


	@Override
	public ReportResponse generateSavedCustomReport(Long reportId, Company company) {
		Assert.notNull(reportId);
		Assert.notNull(company);
		ReportingCriteria reportingCriteria = reportingCriteriasDAO.get(reportId);
		ReportRequestData reportData = getReportRequestData(reportingCriteria);
		reportData.setGenerateReport(false);  //we want it now, we don't want to process async/offline on queue
		translateWorkCustomFieldFilteringCriteriaToIds(reportData);
		Optional<EntityResponseForReport> response = workReportGeneratorService.generate(reportData);
		if (!response.isPresent()) {
			return new ReportResponse();
		}
		return new ReportResponse(response.get().getFileName(),
				new ReportRow(0, response.get().getHeaders()),
				getReportRows(response.get()));
	}

	/**
	 * Create a ReportRequestData object with custom fields set correctly
	 */
	@Override
	public ReportRequestData getReportRequestData(ReportingCriteria reportingCriteria){
		return new ReportRequestData(reportingCriteria, reportingCriteria.getCompany(), Locale.ENGLISH);
	}

	private void translateWorkCustomFieldFilteringCriteriaToIds(ReportRequestData reportData) {
		List<Long> workCustomFieldIds = new ArrayList<>();
		List<ReportFilter> filters = reportData.getReportFilterL();

		if (CollectionUtils.isNotEmpty(filters)) {
			String workCustomFieldIdWorkReportColumnType = (String) AbstractReportFilterBuilder.workReportColumnTypes.get(WorkReportColumnType.WORK_CUSTOM_FIELD_ID);
			for (int i = filters.size()-1; i >= 0; i--) {
				if (workCustomFieldIdWorkReportColumnType.equals(filters.get(i).getProperty())) {
					workCustomFieldIds.add(Long.parseLong(filters.get(i).getFieldValue()));
					filters.remove(i);
				}
			}
		}

		reportData.setWorkCustomFieldIds(workCustomFieldIds);
	}

	@Override
	public ReportResponse generateAdhocCustomReport(ReportRequestData reportData) {
		Assert.notNull(reportData);
		reportData.setGenerateReport(false);  //we want it now, we don't want to process async/offline on queue
		Optional<EntityResponseForReport> response = workReportGeneratorService.generate(reportData);
		if (!response.isPresent()) {
			return new ReportResponse();
		}
		return new ReportResponse(response.get().getFileName(),
				new ReportRow(0, response.get().getHeaders()),
				getReportRows(response.get()));
	}

	@Override
	public List<DataTableColumnHeader> createDataTableColumnHeaders(Long reportId, List<String> columnHeaders) {
		Assert.notNull(reportId);
		Assert.notNull(columnHeaders);
		List<DataTableColumnHeader> dataTableColumnHeaders = new ArrayList<>();
		ReportingCriteria reportingCriteria = reportingCriteriasDAO.get(reportId);
		if (reportingCriteria == null) {
			return dataTableColumnHeaders;
		}
		for (String header : columnHeaders) {
			DataTableColumnHeader columnHeader = new DataTableColumnHeader();
			columnHeader.setsTitle(header);

			for (ReportingCriteriaFiltering filter : reportingCriteria.getReportingCriteriaFiltering()) {
				Entity filterBean = workReportingContext.getEntities().get(filter.getProperty());
				if (filterBean == null || !filterBean.getDisplayNameM().get(Locale.ENGLISH).equals(header)) continue;
				columnHeader.setsType(getType(filterBean.getFormat()));
				columnHeader.setaTargets(new Integer[]{dataTableColumnHeaders.size()});
				dataTableColumnHeaders.add(columnHeader);
				break;
			}
			// if the column header wasn't set, it's either a custom field or legacy data - add it as a string
			if (columnHeader.getaTargets() == null) {
				columnHeader.setsType("string");
				columnHeader.setaTargets(new Integer[]{dataTableColumnHeaders.size()});
				dataTableColumnHeaders.add(columnHeader);
			}
		}
		return dataTableColumnHeaders;
	}

	@Override
	public boolean hasAccessToCustomReport(long reportId, long companyId) {
		ReportingCriteria reportingCriteria = reportingCriteriasDAO.get(reportId);
		if (reportingCriteria != null) {
			return reportingCriteria.getCompany().getId().equals(companyId);
		}
		return false;
	}

	private String getType(Format format) {
		if (format instanceof DateFormat) {
			return "date";
		} else if (format instanceof HyperTextFormat || format instanceof TitleLinkFormat) {
			return "html";
		} else if (format instanceof WMDecimalFormat) {
			return "numeric";
		} else if (format instanceof CurrencyFormat) {
			return "currency";
		} else {
			return "string";
		}
	}

	//used to generate a csv offline
	@Override
	public void generateAsyncCustomReport(Long reportId, Company company, String userNumber, Long masqueradeId) {
		Assert.notNull(reportId);
		Assert.notNull(company);
		Assert.notNull(userNumber);

		ReportingCriteria reportingCriteria = reportingCriteriasDAO.get(reportId);
		ReportRequestData reportData = new ReportRequestData(reportingCriteria, company, Locale.ENGLISH, userNumber, masqueradeId);
		reportData.setGenerateReport(true);
		workReportGeneratorService.generateAsyncCustomReport(reportData, reportId);
	}

	@Override
	public void generateAsyncAdhocCustomReport(FilteringEntityRequest filteringEntityRequest, Long companyId) throws WorkDisplayException {
		Assert.notNull(filteringEntityRequest);
		Assert.notNull(companyId);
		ReportRequestData reportData = new ReportRequestData(filteringEntityRequest, companyId);
		reportData.generateReportFilters(filteringEntityRequest);
		reportData.setGenerateReport(true);
		workReportGeneratorService.generateAsyncCustomReport(reportData, null);
	}

	//Fetches filters that already exist for a report and converts them to displayable format
	@Override
	public WorkReportEntityBucketsCompositeResponse fetchFiltersForDisplay(Long reportId, Company company) throws Exception {
		Assert.notNull(reportId);
		Assert.notNull(company);
		ReportingCriteria reportingCriteria = reportingCriteriasDAO.get(reportId);
		ReportRequestData reportData = new ReportRequestData(reportingCriteria, company, Locale.ENGLISH);
		ReportingContext workReportingContext = (ReportingContext) reportingContext.get(ReportingReportType.WORK_ASSIGNMENTS);
		workReportingContext.setCompanyId(reportData.getCompanyId());
		translateWorkCustomFieldFilteringCriteriaToIds(reportData);
		return workDisplayHandler.constructWorkReportEntityBucketsCompositeResponse(reportData, ReportingReportType.WORK_ASSIGNMENTS);
	}

	//Takes inbound display-formatted filters, looks to see if this filter exists in the database and updates the values accordingly
	@Override
	public void updateFilters(Long reportId, Map<String, Object> filterMaps) {
		ReportingCriteria reportingCriteria = reportingCriteriasDAO.get(reportId);
		for (Map.Entry<String, Object> entry : filterMaps.entrySet()) {
			ReportingCriteriaFiltering filter = findMatchingSavedFilter(entry.getKey(), reportingCriteria.getReportingCriteriaFiltering());
			if (filter == null) continue;
			Integer filterType = Integer.parseInt(entry.getKey().split("_")[0]);
			AbstractReportFilterBuilder filterBuilder = AbstractReportFilterBuilder.getReportFilterBuilder(filterType);
			CustomReportUpdateResponse response = filterBuilder.updateReportingCriteriaFilter(entry.getKey(), entry.getValue(), filterMaps, filter);
		}
		reportingCriteriasDAO.saveOrUpdate(reportingCriteria);
	}

	private ReportingCriteriaFiltering findMatchingSavedFilter(String filterKey, List<ReportingCriteriaFiltering> filters) {
		if (!isFilter(filterKey)) return null;
		Set<String> filterKeys = new HashSet<>(Arrays.asList(filterKey.split("_")));
		for (ReportingCriteriaFiltering filter : filters) {
			List<String> existingKeys = Arrays.asList(filter.getProperty().split("\\."));
			if (CollectionUtils.containsAny(existingKeys, filterKeys) && !"display".equals(filter.getFilteringType())) {
				return filter;
			}
		}
		return null;
	}


	//real filters start with an integer "2_blah_blah
	//display only variables start with the column name
	private boolean isFilter(String filterKey) {
		String[] filterKeys = filterKey.split("_");
		try {
			Integer.parseInt(filterKeys[0]);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}


	private List<ReportRow> getReportRows(EntityResponseForReport entityResponseForReport) {
		List<ReportRow> reportRows = new ArrayList<>();

		if (entityResponseForReport.getRows() != null) {
			for (int i = 0; i < entityResponseForReport.getRows().size(); i++) {
				ReportRow reportRow = new ReportRow(i + 1, entityResponseForReport.getRows().get(i));
				reportRows.add(reportRow);
			}
		}
		return reportRows;
	}
}
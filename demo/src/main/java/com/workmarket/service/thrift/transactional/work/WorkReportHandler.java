package com.workmarket.service.thrift.transactional.work;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.workmarket.reporting.mapping.FilteringType;
import com.workmarket.reporting.mapping.RelationalOperator;
import com.workmarket.reporting.service.WorkReportGeneratorServiceImpl;
import com.workmarket.thrift.work.report.FilteringTypeThrift;
import com.workmarket.thrift.work.report.RelationalOperatorThrift;
import com.workmarket.thrift.work.report.WorkReportColumnType;


@Component
public class WorkReportHandler {

	@Autowired
	private WorkReportGeneratorServiceImpl workReportGeneratorServiceImpl;
	@Resource(name="work_report_column_types")
	private Map<WorkReportColumnType, String> workReportColumnTypes;
	@Resource(name="work_report_input_types")
	private Map<FilteringTypeThrift, FilteringType>filteringTypeThrifts;
	@Resource(name="relational_operator_options")
	private Map<RelationalOperatorThrift, RelationalOperator>relationalOperatorOptions;

	public WorkReportGeneratorServiceImpl getAssignmentReportGeneratorServiceImpl() {
		return workReportGeneratorServiceImpl;
	}

	public void setAssignmentReportGeneratorServiceImpl(WorkReportGeneratorServiceImpl workReportGeneratorServiceImpl) {
		this.workReportGeneratorServiceImpl = workReportGeneratorServiceImpl;
	}

	public Map<WorkReportColumnType, String> getWorkReportColumnTypes() {
		return workReportColumnTypes;
	}

	public void setWorkReportColumnTypes(Map<WorkReportColumnType, String> workAssignmentEntities) {
		this.workReportColumnTypes = workAssignmentEntities;
	}

	public Map<FilteringTypeThrift, FilteringType> getFilteringTypeThrifts() {
		return filteringTypeThrifts;
	}

	public void setFilteringTypeThrifts(Map<FilteringTypeThrift, FilteringType> filteringTypeThrifts) {
		this.filteringTypeThrifts = filteringTypeThrifts;
	}

	public Map<RelationalOperatorThrift, RelationalOperator> getRelationalOperatorOptions() {
		return relationalOperatorOptions;
	}

	public void setRelationalOperatorOptions(Map<RelationalOperatorThrift, RelationalOperator> relationalOperatorOptions) {
		this.relationalOperatorOptions = relationalOperatorOptions;
	}
}

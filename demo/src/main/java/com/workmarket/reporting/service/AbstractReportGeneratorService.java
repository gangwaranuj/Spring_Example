package com.workmarket.reporting.service;

import com.google.common.base.Optional;
import com.workmarket.domains.model.reporting.*;
import com.workmarket.reporting.exception.ReportingException;
import com.workmarket.reporting.output.AbstractReportHandler;
import com.workmarket.reporting.query.AbstractQueryBuilder;
import com.workmarket.reporting.query.AbstractSQLExecutor;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractReportGeneratorService {

	/*
	 * Instance variables and constants
	 */
	private ReportingContext reportingContext;
	private AbstractReportHandler reportHandler;
	private AbstractQueryBuilder abstractQueryBuilder;
	protected AbstractSQLExecutor sqlExecutor;

	/**
	 * @return
	 */
	public abstract String getReportContextName();

	
	/**
	 * @param entityKeyReport
	 * @return
	 * @throws ReportingException
	 *
	 */
	public abstract Optional<EntityResponseForReport> generate(ReportRequestData entityKeyReport) throws Exception;

    public abstract void generateAsyncCustomReport(ReportRequestData reportRequestData,Long reportId);


    /**
	 * @return the abstractQueryBuilder
	 */
	@SuppressWarnings("unchecked")
	public <T extends AbstractQueryBuilder> T getAbstractQueryBuilder() {
		return (T)abstractQueryBuilder;
	}



	/**
	 * @param abstractQueryBuilder the abstractQueryBuilder to set
	 */
	public <T extends AbstractQueryBuilder> void setAbstractQueryBuilder(T abstractQueryBuilder) {
		this.abstractQueryBuilder = abstractQueryBuilder;
	}


	/**
	 * @return the sqlExecutor
	 */
	@SuppressWarnings("unchecked")
	public <T extends AbstractSQLExecutor> T getSQLExecutor() {
		return (T) sqlExecutor;
	}

	/**
	 * @param <T>
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends AbstractReportHandler> T getReportHandler(){
		return (T)reportHandler;
	}

	/**
	 * @param <T>
	 * @return
	 */
	@SuppressWarnings(value = { "unchecked" })
	public <T extends ReportingContext> T getReportingContext() {
		return (T)reportingContext;
	}

	/**
	 * @param <T>
	 * @param reportHandler
	 */
	public <T extends AbstractReportHandler> void setReportHandler(T reportHandler){
		this.reportHandler = reportHandler;
	}

	/**
	 * @param <T>
	 * @param context
	 */
	public <T extends ReportingContext> void setReportingContext(T reportingContext) {
		this.reportingContext = reportingContext;
	}

	/**
	 * @param rows
	 * @return
	 */
	protected Pagination constructPagination(Integer rows, Integer pageSize){
		Pagination pagination = new Pagination();
		pagination.setTotal(rows);
		List<PaginationPag> paginationPags = new ArrayList<PaginationPag>();
		
		if(rows != null && rows > pageSize){
			int remainder = rows % pageSize;
			int numberOfPages = rows / pageSize;
			if(remainder > 0)
				numberOfPages++;
			
			for(int i = 0; i < numberOfPages; i++){
				PaginationPag paginationPag = new PaginationPag();
				paginationPag.setStartRow(pageSize * i);
				paginationPag.setPageSize(pageSize);
				paginationPags.add(paginationPag);
			}
		}else{
			PaginationPag paginationPag = new PaginationPag();
			paginationPag.setStartRow(0);
			paginationPag.setPageSize(pageSize);				
			paginationPags.add(paginationPag);
		}
		pagination.setPaginationPags(paginationPags);
		return pagination;
	}
}
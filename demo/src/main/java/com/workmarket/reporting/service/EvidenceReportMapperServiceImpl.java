package com.workmarket.reporting.service;

import com.google.common.collect.Lists;
import com.workmarket.reporting.model.EvidenceReport;
import com.workmarket.reporting.model.EvidenceReportRow;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Service
public class EvidenceReportMapperServiceImpl implements EvidenceReportMapperService {

	private static final String DATE_FORMAT_CERTIFICATE = "MM/dd/yyyy";
	private final static String generatePDFUrl = "/reports/evidence/generatepdf?userId=";
	private final static String[] headers = {"Name","Company Name","Certificate Date"};

	//Maps to a format that can be shown by jquery datatable
	@Override
	public List<EvidenceReportRow> mapEvidenceReportToDataTable(Long groupId,
	                                                            List<EvidenceReport> evidenceReports,
	                                                            String screeningType){
		Assert.notNull(evidenceReports);
		List<EvidenceReportRow> evidenceReportRows = Lists.newArrayList();
		for(EvidenceReport evidenceReport : evidenceReports){
			EvidenceReportRow reportRow = new EvidenceReportRow();
			List<String> row = new ArrayList<String>();
			row.add(extractName(evidenceReport));
			row.add(extractCompanyName(evidenceReport));
			row.add(extractResponseDate(evidenceReport.getResponseDate()));
			row.add(generateCertificateUrl(groupId,evidenceReport,screeningType));
			reportRow.setRow(row);
			evidenceReportRows.add(reportRow);
		}
		return evidenceReportRows;
	}

	//Maps to a format that can be shown by jquery datatable
	@Override
	public List<String[]> mapEvidenceReportToCSV(List<EvidenceReport> evidenceReports){
		Assert.notNull(evidenceReports);
		List<String[]> evidenceReportRows = Lists.newArrayList();
		evidenceReportRows.add(headers);
		for (EvidenceReport evidenceReport : evidenceReports) {
			List<String> row = new ArrayList<>();
			row.add(extractName(evidenceReport));
			row.add(extractCompanyName(evidenceReport));
			row.add(extractResponseDate(evidenceReport.getResponseDate()));
			evidenceReportRows.add(row.toArray(new String[row.size()]));
		}
		return evidenceReportRows;
	}

	static public String extractName(EvidenceReport evidenceReport){
		String name = "";
		name += evidenceReport.getFirstName() == null ? "":evidenceReport.getFirstName();
		if(StringUtils.isNotEmpty(name) && evidenceReport.getLastName() != null) name += " ";
		name += evidenceReport.getLastName() == null ? "":evidenceReport.getLastName();
		return name;
	}

	static public String extractCompanyName(EvidenceReport evidenceReport){
		return evidenceReport.getCompanyName() == null ? "":evidenceReport.getCompanyName();
	}

	static public String extractResponseDate(Calendar responseDate){
		if(responseDate != null){
			return new SimpleDateFormat(DATE_FORMAT_CERTIFICATE).format(responseDate.getTime());
		}
		return("n/a");
	}

	private String generateCertificateUrl(Long groupId,EvidenceReport evidenceReport,String screeningType){
		return "<a target='_blank' href='" +
				generatePDFUrl + evidenceReport.getUserId() +
				"&groupId=" + groupId +
				"&screeningType=" + screeningType +
				"'> View Certificate </a>";
	}
}

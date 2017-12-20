package com.workmarket.reporting.model;


import java.util.List;

/*******
* for datatable response: name, company, date of evidence, url to generate
********/
public class EvidenceReportRow {

	List<String> row;

	public List<String> getRow() {
		return row;
	}

	public void setRow(List<String> row) {
		this.row = row;
	}
}

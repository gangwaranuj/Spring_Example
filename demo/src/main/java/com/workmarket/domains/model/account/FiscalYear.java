package com.workmarket.domains.model.account;

import com.workmarket.domains.model.AbstractEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Calendar;

/**
 * Author: rocio
 */
@Entity(name = "fiscalYear")
@Table(name = "fiscal_year")
public class FiscalYear extends AbstractEntity {

	private static final long serialVersionUID = 1L;

	private Calendar startDate;
	private Integer year;

	public FiscalYear() {
	}

	public FiscalYear(Calendar startDate, Integer year) {
		this.startDate = startDate;
		this.year = year;
	}

	@Column(name = "fiscal_year_start_date", nullable = false, updatable = false)
	public Calendar getStartDate() {
		return startDate;
	}

	public void setStartDate(Calendar startDate) {
		this.startDate = startDate;
	}

	@Column(name = "year", nullable = false, updatable = false)
	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}
}

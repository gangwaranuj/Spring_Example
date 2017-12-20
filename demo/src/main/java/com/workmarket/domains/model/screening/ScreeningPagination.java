package com.workmarket.domains.model.screening;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;

public class ScreeningPagination extends AbstractPagination<Screening> implements Pagination<Screening> {
	
	public enum FILTER_KEYS {}
	public enum SORTS {}
	
	public ScreeningPagination() {}
	public ScreeningPagination(boolean returnAllRows) {
		super(returnAllRows);
	}
}

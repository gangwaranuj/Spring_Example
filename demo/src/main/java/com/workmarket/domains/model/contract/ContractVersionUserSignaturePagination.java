package com.workmarket.domains.model.contract;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;

public class ContractVersionUserSignaturePagination extends AbstractPagination<ContractVersionUserSignature> implements Pagination<ContractVersionUserSignature> {
	public enum FILTER_KEYS {}
	public enum SORTS {}
	
	public ContractVersionUserSignaturePagination() {}
	public ContractVersionUserSignaturePagination(boolean returnAllRows) {
		super(returnAllRows);
	}
}

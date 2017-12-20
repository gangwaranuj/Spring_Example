package com.workmarket.web.helpers;

import com.workmarket.helpers.ResponseBuilderBase;

import java.util.ArrayList;
import java.util.List;

public class BulkActionAjaxResponseBuilder extends ResponseBuilderBase<BulkActionAjaxResponseBuilder> {

	private List<String> partialErrors = new ArrayList<>();

	public BulkActionAjaxResponseBuilder  setPartialErrors(List<String> partialErrors){
		this.partialErrors = partialErrors;
		return this;
	}

	public List<String>  getPartialErrors() {
		return partialErrors;
	}


	public static BulkActionAjaxResponseBuilder success() {
		return new BulkActionAjaxResponseBuilder().setSuccessful(true);
	}

	public static BulkActionAjaxResponseBuilder fail() {
		return new BulkActionAjaxResponseBuilder().setSuccessful(false);
	}
}

package com.workmarket.web.helpers;

import com.workmarket.helpers.ResponseBuilderBase;

public class AjaxResponseBuilder extends ResponseBuilderBase<AjaxResponseBuilder> {
	private String redirect;

	public static AjaxResponseBuilder success() {
		return new AjaxResponseBuilder().setSuccessful(true);
	}

	public static AjaxResponseBuilder fail() {
		return new AjaxResponseBuilder().setSuccessful(false);
	}

	public String getRedirect() {
		return redirect;
	}

	public AjaxResponseBuilder setRedirect(String redirect) {
		this.redirect = redirect;
		return this;
	}
}

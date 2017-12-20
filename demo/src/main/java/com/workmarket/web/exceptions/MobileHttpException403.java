package com.workmarket.web.exceptions;

public class MobileHttpException403 extends HttpException403 {
	public MobileHttpException403() {
		super("Forbidden");
		this.redirectUri = "mobile/pages/error/403";
	}
}

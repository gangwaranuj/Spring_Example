package com.workmarket.web.exceptions;

public class MobileHttpException400 extends HttpException400 {
	public MobileHttpException400() {
		this.redirectUri = "mobile/pages/error/400";
	}
}

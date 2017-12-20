package com.workmarket.web.exceptions;

public class MobileHttpException401 extends HttpException401 {
	public MobileHttpException401() {
		this.redirectUri = "mobile/pages/error/401";
	}
}

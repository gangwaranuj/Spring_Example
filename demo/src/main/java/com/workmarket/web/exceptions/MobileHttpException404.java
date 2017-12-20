package com.workmarket.web.exceptions;

public class MobileHttpException404 extends HttpException404 {
	public MobileHttpException404() {
		this.redirectUri = "mobile/pages/error/404";
	}
}

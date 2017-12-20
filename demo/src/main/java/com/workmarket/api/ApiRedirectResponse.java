package com.workmarket.api;

import org.springframework.web.servlet.view.RedirectView;

/**
 * Created by joshlevine on 12/27/16.
 */
public class ApiRedirectResponse extends RedirectView {
	public ApiRedirectResponse(String destination) {
		super(destination);
	}
}

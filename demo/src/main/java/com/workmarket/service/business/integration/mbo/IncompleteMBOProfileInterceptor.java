package com.workmarket.service.business.integration.mbo;

import com.workmarket.domains.model.MboProfile;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.authentication.services.SecurityContextFacade;
import com.workmarket.web.interceptors.ExcludableInterceptor;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class IncompleteMBOProfileInterceptor extends ExcludableInterceptor {
	@Autowired private SecurityContextFacade securityContext;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {

		if (isExcluded(request)) {
			return true;
		}

		ExtendedUserDetails user = securityContext.getCurrentUser();

		if (user != null) {
			if (user.isMbo()) {
				MboProfile mboProfile = user.getMboProfile();

				if (mboProfile != null && Boolean.TRUE.equals(mboProfile.getMissingAddress())) {
					response.sendRedirect("/mbo/completeprofile");
					return false;
				}
			}
		}

		return true;
	}
}


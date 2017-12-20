package com.workmarket.web.helpers;

import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;

public interface WorkDetailsControllerHelperService {
	AjaxResponseBuilder acceptWorkOnBehalf(String workNumber, String note, String workerNumber, HttpServletRequest request, Model model, ExtendedUserDetails extendedUserDetails);
}

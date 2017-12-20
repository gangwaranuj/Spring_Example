package com.workmarket.domains.work.service.actions;

import com.workmarket.helpers.ResponseBuilderBase;
import com.workmarket.domains.model.User;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.infra.security.WorkContext;

import java.util.List;
import java.util.Set;

public interface WorkEventAuthService {

	public <T extends ResponseBuilderBase>  List<Work> validateAndAuthorizeWork(final User user,
	                                           final List<Work> works,
	                                           T response,
	                                           final String messageKey,
	                                           final Set<WorkContext> authz);


}

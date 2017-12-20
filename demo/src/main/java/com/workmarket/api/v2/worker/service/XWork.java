package com.workmarket.api.v2.worker.service;

import com.google.common.collect.Sets;
import com.workmarket.api.exceptions.MessageSourceApiException;
import com.workmarket.api.v2.worker.ex.WorkInvalidException;
import com.workmarket.api.v2.worker.ex.WorkNotAuthorizedException;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.service.thrift.TWorkFacadeService;
import com.workmarket.service.thrift.transactional.work.WorkRequestInfo;
import com.workmarket.thrift.work.AuthorizationContext;
import com.workmarket.thrift.work.RequestContext;
import com.workmarket.thrift.work.WorkRequest;
import com.workmarket.thrift.work.WorkResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class XWork {

	private static final Log logger = LogFactory.getLog(XWork.class);

	@Autowired private WorkService workService;
	@Autowired private TWorkFacadeService tWorkFacadeService;

	public Long getWorkId(final String workNumber) {
		AbstractWork work = getWorkByNumber(workNumber);
		return work.getId();
	}

	public AbstractWork getWorkByNumber(final String workNumber) {

		if (!StringUtils.isNumeric(workNumber)) {
			throw new MessageSourceApiException("api.v1.assignments.invalid.workNumber");
		}

		final AbstractWork work = workService.findWorkByWorkNumber(workNumber);
		if (work == null) {
			throw new MessageSourceApiException("api.v1.assignments.invalid.workNumber");
		}

		return work;
	}

	public WorkResponse getWork(
		final ExtendedUserDetails extendedUserDetails,
		final String workNumber,
		Set<WorkRequestInfo> includes,
		Set<AuthorizationContext> authz
	) {
		return getWork(extendedUserDetails,workNumber,includes,authz,true);
	}

	// throwOnUnauthorized - results returned from solr feed query can return workNumbers which fail the auth check
	// temporarily trust solr results and do the lookup without the check. kenmc
	public WorkResponse getWork(
		final ExtendedUserDetails extendedUserDetails,
		final String workNumber,
		Set<WorkRequestInfo> includes,
		Set<AuthorizationContext> authz,
		final boolean throwOnUnauthorized
	) {
		if (includes == null) {
			includes = Sets.newHashSetWithExpectedSize(1);
		}

		if (!includes.contains(WorkRequestInfo.CONTEXT_INFO)) {
			includes = Sets.newHashSet(includes);
			includes.add(WorkRequestInfo.CONTEXT_INFO);
		}

		final WorkRequest workRequest = new WorkRequest()
			.setUserId(extendedUserDetails.getId())
			.setWorkNumber(workNumber)
			.setIncludes(includes);

		WorkResponse workResponse;

		try {
			workResponse = tWorkFacadeService.findWork(workRequest);
		}
		catch (final Exception e) {
			throw new WorkInvalidException();
		}

		if (extendedUserDetails.isInternal()) {
			return workResponse;
		}

		if (workResponse.getRequestContexts().contains(RequestContext.UNRELATED)) {
			logger.warn("WorkNotAuthorizedException::UNRELATED[" + workNumber);
			if (throwOnUnauthorized) {
				throw new WorkNotAuthorizedException();
			}
		}

		if (workResponse.getAuthorizationContexts().isEmpty()) {
			logger.warn("WorkNotAuthorizedException::empty authcontext[" + workNumber);
			if (throwOnUnauthorized) {
				throw new WorkNotAuthorizedException();
			}
		}

		return workResponse;
	}
}

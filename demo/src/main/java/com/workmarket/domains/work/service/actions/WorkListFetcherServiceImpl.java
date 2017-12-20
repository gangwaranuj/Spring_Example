package com.workmarket.domains.work.service.actions;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.workmarket.domains.work.dao.WorkDAO;
import com.workmarket.helpers.ResponseBuilderBase;
import com.workmarket.domains.model.User;
import com.workmarket.domains.work.model.Work;
import com.workmarket.redis.repositories.WorkSearchRequestRepository;
import com.workmarket.search.request.work.WorkSearchRequest;
import com.workmarket.service.business.SelectService;
import com.workmarket.service.infra.security.WorkContext;
import com.workmarket.service.thrift.transactional.work.WorkResponseBuilder;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

@Service
public class WorkListFetcherServiceImpl implements WorkListFetcherService {

	private static final Logger logger = LoggerFactory.getLogger(WorkListFetcherService.class);

	@Autowired WorkSearchRequestRepository workSearchRequestRepository;
	@Autowired SelectService selectService;
	@Autowired WorkDAO workDAO;
	@Autowired WorkResponseBuilder responseBuilder;
	@Autowired MessageBundleHelper messageBundleHelper;
	@Autowired WorkEventAuthService workEventAuthService;


	@Override
	public List<String> fetchWorkNumbers(Long userId, List<String> workNumbers) {
		Assert.notNull(userId);
		Assert.notNull(workNumbers);

		if(isAllWorkSelectedByUser(userId)){
			return searchForWorkNumbers(userId);
		}
		return workNumbers;
	}

	@Override
	public <T extends ResponseBuilderBase>  List<Work> fetchValidatedWork(User user, List<String> workNumbers,T response, String messageKey) {
		Assert.notNull(user);
		Assert.notNull(workNumbers);
		Assert.notNull(response);
		Assert.notNull(messageKey);

		List<String> tempWorkNumbers = workNumbers;
		if(isAllWorkSelectedByUser(user.getId())){
			tempWorkNumbers = searchForWorkNumbers(user.getId());
		}

		return workEventAuthService.validateAndAuthorizeWork(
				user,
				fetchWork(tempWorkNumbers, response, messageKey),
				response,
				messageKey,
				ImmutableSet.of(
					WorkContext.OWNER,
					WorkContext.COMPANY_OWNED,
					WorkContext.ACTIVE_RESOURCE
				)
		);
	}

	private boolean isAllWorkSelectedByUser(Long userId){
		Optional<WorkSearchRequest> opt = workSearchRequestRepository.get(userId);
		return opt.isPresent() && opt.get().isFullSelectAll();
	}

	private List<String> searchForWorkNumbers(final Long userId) {
		return selectService.fetchAllWorkBySearchFilter(userId);
	}

	@Override
	public <T extends ResponseBuilderBase>  List<Work> fetchWork(List<String> workNumbers, T response, String messageKey) {
		Assert.notNull(response);
		Assert.notNull(messageKey);

		List<Work> works = workDAO.findWorkByWorkNumber(workNumbers);
		if (works.isEmpty()) {
			messageBundleHelper.addMessage(response.setSuccessful(false), messageKey + ".exception");
		}

		return works;
	}

}

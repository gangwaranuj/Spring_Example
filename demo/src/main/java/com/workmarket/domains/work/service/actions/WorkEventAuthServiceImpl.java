package com.workmarket.domains.work.service.actions;

import com.google.common.collect.Lists;
import com.workmarket.service.infra.business.UserRoleService;
import com.workmarket.helpers.ResponseBuilderBase;
import com.workmarket.domains.model.User;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.service.infra.security.WorkContext;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Set;

@Service
public class WorkEventAuthServiceImpl implements WorkEventAuthService {

	@Component
	public static class FactoryHelper {
		public List<Work> makeValidWorkList() {
			return Lists.newArrayList();
		}
	}

	@Autowired WorkService workService;
	@Autowired MessageBundleHelper messageBundleHelper;
	@Autowired FactoryHelper factoryHelper;
	@Autowired UserRoleService userRoleService;

	@Override
	public <T extends ResponseBuilderBase> List<Work> validateAndAuthorizeWork(final User user,
											   final List<Work> works,
											   T response,
											   final String messageKey,
											   final Set<WorkContext> authz) {
		Assert.notNull(user);
		Assert.notNull(works);
		Assert.notNull(authz);
		Assert.notNull(response);
		Assert.notNull(messageKey);
		if (userRoleService.isInternalUser(user)) {
			return works;
		}

		List<Work> validWork = factoryHelper.makeValidWorkList();
		for (Work work : works) {
			List<WorkContext> contexts = workService.getWorkContext(work, user);
			if (CollectionUtilities.contains(contexts, WorkContext.UNRELATED) ||
					!CollectionUtilities.contains(contexts, authz.toArray(new WorkContext[authz.size()]))) {
				messageBundleHelper.addMessage(response.setSuccessful(false),
						messageKey + ".single.permissions",
						work.getWorkNumber());
			} else {
				validWork.add(work);
			}
		}
		return validWork;
	}


}

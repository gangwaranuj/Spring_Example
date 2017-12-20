package com.workmarket.service.infra.interceptor;

import com.google.common.collect.Queues;
import com.workmarket.service.business.event.search.IndexerEvent;
import com.workmarket.service.business.event.user.UserSearchIndexEvent;
import com.workmarket.service.business.event.work.WorkUpdateSearchIndexEvent;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Set;

import static org.apache.commons.lang3.ArrayUtils.isNotEmpty;

public class EventRouterInterceptor {

	private static final String PACKAGE_NAME = "com.workmarket.service.business";

	private final Log logger = LogFactory.getLog(EventRouterInterceptor.class);

	public Object log(ProceedingJoinPoint pjp) throws Throwable {
		Object[] arguments = pjp.getArgs();
		if (isNotEmpty(arguments)) {
			Object firstArgument = arguments[0];

			if (firstArgument instanceof WorkUpdateSearchIndexEvent) {
				WorkUpdateSearchIndexEvent event = (WorkUpdateSearchIndexEvent) firstArgument;
				List<Long> workIds = event.getWorkIds();
				if (CollectionUtils.isNotEmpty(workIds) && workIds.size() == 1) {
					getBusinessStackTrace(event);
				}
			} else if (firstArgument instanceof UserSearchIndexEvent) {
				UserSearchIndexEvent event = (UserSearchIndexEvent) firstArgument;
				Set<Long> userIds = event.getUserIds();
				if (CollectionUtils.isNotEmpty(userIds) && userIds.size() == 1) {
					getBusinessStackTrace(event);
				}
			}
		}

		return pjp.proceed();
	}

	private <T extends IndexerEvent> void getBusinessStackTrace(T event) {
		ArrayDeque<String> stack = Queues.newArrayDeque();

		for (StackTraceElement stackTraceElement : Thread.currentThread().getStackTrace()) {
			if (stackTraceElement.getClassName().contains(PACKAGE_NAME)) {
				stack.add("[caller] " + StringUtils.substringAfter(stackTraceElement.getClassName(), PACKAGE_NAME) + "." + stackTraceElement.getMethodName() + "()" + stackTraceElement.getLineNumber());
			}
		}

		if (CollectionUtils.isNotEmpty(stack)) {
			logger.info(event);
			do {
				logger.info(stack.removeLast());
			} while (CollectionUtils.isNotEmpty(stack));

		}
	}

}

package com.workmarket.service.infra.index;


import com.workmarket.service.business.event.user.UserSearchIndexEvent;
import com.workmarket.service.infra.event.EventRouter;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class UpdateUserSearchIndexAspect {

	@Autowired private EventRouter eventRouter;

	@Pointcut(value="@annotation(com.workmarket.service.infra.index.UpdateUserSearchIndex))")
	public void annotatedBean() {}

	@After(
		value = "annotatedBean() && @annotation(updateUserSearchIndex)",
		argNames = "updateUserSearchIndex"
	)
	public void reindexById(JoinPoint joinPoint, UpdateUserSearchIndex updateUserSearchIndex) throws Throwable {
		long userId = JoinPointUtils.getLongArgument(joinPoint, updateUserSearchIndex.userIdArgumentPosition());
		eventRouter.sendEvent(new UserSearchIndexEvent(userId));
	}
}

package com.workmarket.service.infra.index;


import com.workmarket.service.business.event.work.WorkUpdateSearchIndexEvent;
import com.workmarket.service.infra.event.EventRouter;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Aspect
@Component
public class UpdateWorkSearchIndexAspect {

	@Autowired private EventRouter eventRouter;

	@Pointcut(value="@annotation(com.workmarket.service.infra.index.UpdateWorkSearchIndex))")
	public void annotatedBean() {}

	@After(
		value = "annotatedBean() && @annotation(updateWorkSearchIndex)",
		argNames = "updateWorkSearchIndex"
	)
	public void sendEvent(JoinPoint joinPoint, UpdateWorkSearchIndex updateWorkSearchIndex) throws Throwable {
		long workId = JoinPointUtils.getLongArgument(joinPoint, updateWorkSearchIndex.workIdArgumentPosition());
		eventRouter.sendEvent(new WorkUpdateSearchIndexEvent(workId));
	}
}

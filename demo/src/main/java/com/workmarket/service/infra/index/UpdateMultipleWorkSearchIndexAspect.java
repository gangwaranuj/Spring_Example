package com.workmarket.service.infra.index;


import com.workmarket.service.business.event.work.WorkUpdateSearchIndexEvent;
import com.workmarket.service.infra.event.EventRouter;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Aspect
@Component
public class UpdateMultipleWorkSearchIndexAspect {

	@Autowired private EventRouter eventRouter;

	@Pointcut(value="@annotation(com.workmarket.service.infra.index.UpdateMultipleWorkSearchIndex))")
	public void annotatedBean() {}

	@After(
		value = "annotatedBean() && @annotation(updateMultipleWorkSearchIndex)",
		argNames = "updateMultipleWorkSearchIndex"
	)
	public void sendEvent(JoinPoint joinPoint, UpdateMultipleWorkSearchIndex updateMultipleWorkSearchIndex) throws Throwable {
		List<Long> workIds = JoinPointUtils.getLongsArgument(joinPoint, updateMultipleWorkSearchIndex.workIdsArgumentPosition());
		eventRouter.sendEvent(new WorkUpdateSearchIndexEvent(workIds));
	}
}

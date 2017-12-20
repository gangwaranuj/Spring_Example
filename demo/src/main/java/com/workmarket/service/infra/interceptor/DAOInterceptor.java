package com.workmarket.service.infra.interceptor;

import com.workmarket.domains.model.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.util.StopWatch;

public class DAOInterceptor {

	private Log logger = LogFactory.getLog(DAOInterceptor.class);

	public Object logQueryTimes(ProceedingJoinPoint pjp) throws Throwable {
	
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
	
		Object returnValue = pjp.proceed();
		String userUuid = null;
		if (returnValue instanceof User) {
			userUuid = ((User) returnValue).getUuid();
		}
	
		stopWatch.stop();
	
		String str = pjp.getTarget().toString();
	
		long time = stopWatch.getTotalTimeMillis();
		if (time > 400) {
			logger.info(str.substring(str.lastIndexOf(".")+1, str.lastIndexOf("@")) + " - " + pjp.getSignature().getName() + ": " + "userUuId=" + userUuid + " " + time + "ms");
		}
		
		return returnValue;
	}
}

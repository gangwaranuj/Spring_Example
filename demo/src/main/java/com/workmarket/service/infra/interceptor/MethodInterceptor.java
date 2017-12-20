package com.workmarket.service.infra.interceptor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.util.StopWatch;

public class MethodInterceptor {

	private Log logger = LogFactory.getLog(MethodInterceptor.class);

	public Object logProfile(ProceedingJoinPoint pjp) throws Throwable {
	
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
	
		Object returnValue = pjp.proceed();
	
		stopWatch.stop();
	
		String str = pjp.getTarget().toString();

		long time = stopWatch.getTotalTimeMillis();
		if (time > 300) {
			logger.info(str.substring(str.lastIndexOf(".")+1, str.lastIndexOf("@")) + " - " + pjp.getSignature().getName() + ": " + time + "ms");
		}
		return returnValue;
	}
}

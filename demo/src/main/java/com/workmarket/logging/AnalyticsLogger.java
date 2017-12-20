package com.workmarket.logging;

import com.workmarket.service.infra.business.AuthenticationService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class AnalyticsLogger {

	public AnalyticsLogger() {}
	@Autowired private AuthenticationService authenticationService;
	private static final String logFormat = "%s|%s|%s";

	@Before("@annotation(com.workmarket.service.infra.analytics.Loggable)")
	public void logBefore(JoinPoint joinPoint) {
		Log targetLogger = LogFactory.getLog( joinPoint.getSignature().getDeclaringTypeName() );
		targetLogger.info(String.format(logFormat, authenticationService.getCurrentUser().getId(),
				joinPoint.getSignature().toShortString(), Arrays.toString(joinPoint.getArgs())));
	}

	@AfterReturning(pointcut = "@annotation(com.workmarket.service.infra.analytics.Loggable)", returning = "response")
	public void logAfter(JoinPoint joinPoint , Object response) {
		Log targetLogger = LogFactory.getLog( joinPoint.getSignature().getDeclaringTypeName() );
		targetLogger.info(String.format(logFormat, authenticationService.getCurrentUser().getId(),
				joinPoint.getSignature().toShortString(), response));
	}
}

package com.workmarket.logging;

import java.util.Arrays;

import com.codahale.metrics.MetricRegistry;
import com.workmarket.common.metric.WMMetricRegistryFacade;
import com.workmarket.service.web.WebRequestContextProvider;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import javax.annotation.PostConstruct;

public class ExceptionLogger implements Ordered {

	@Autowired
	private WebRequestContextProvider webRequestContextProvider;

	@Autowired private MetricRegistry metricRegistry;
	private WMMetricRegistryFacade wmMetricRegistryFacade;

	int order;
	@PostConstruct
	private void init() {
		wmMetricRegistryFacade = new WMMetricRegistryFacade(metricRegistry, "exception");
	}

	public ExceptionLogger() {}

	// this method is the after advice
	public void logStack( JoinPoint joinpoint, Exception stack ) {
		Log targetLogger = LogFactory.getLog( joinpoint.getSignature().getDeclaringTypeName() );
		StringBuilder sb = new StringBuilder( "\n" );
		sb.append( "------------------[ oops ]------------------\n" );
		sb.append( "requestId=" ).append(webRequestContextProvider.getWebRequestContext().getRequestId()).append(" userUuid=").append(
			webRequestContextProvider.getWebRequestContext().getUserUuid()).append(" companyUuid=").append(
			webRequestContextProvider.getWebRequestContext().getCompanyUuid()).append( "\n" );
		sb.append( "Class              : " ).append( joinpoint.getSignature().getDeclaringTypeName() ).append( "\n" );
		sb.append( "Method called      : " ).append( joinpoint.getSignature().toShortString() ).append( "\n" );
		sb.append( "Method arguments   : " );
		/* If one or more of the arguments is an object that's is not initialized */
		try {
			sb.append( Arrays.toString(joinpoint.getArgs()) ).append( "\n" );
		} catch(HibernateException ex) {
			sb.append("Unable to get Arguments!!! \n");
		}

		sb.append( "Exception class    : " ).append( stack.getClass().getCanonicalName() ).append( "\n" );
		sb.append( "Exception message  : " ).append( stack.getMessage() ).append( "\n" );
		sb.append( "Exception stack    : " ).append( "\n" ).append( ExceptionUtils.getStackTrace(stack) ).append( "\n" );
		sb.append( "--------------------------------------------\n" );
		targetLogger.error( sb.toString() );

		postExceptionMetric(joinpoint, stack);
	}

	private void postExceptionMetric(JoinPoint joinpoint, Exception stack) {
		String clazz = joinpoint.getSignature().getDeclaringTypeName().replaceAll("\\.", "_");
		String method = joinpoint.getSignature().getName();
		String exceptionClass = stack.getClass().getCanonicalName().replaceAll("\\.", "_");

		String metricPath = StringUtils.join(Arrays.asList(clazz, method, exceptionClass), ".");
		wmMetricRegistryFacade.meter(metricPath).mark();
	}

	public int getOrder() {
		return this.order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

}

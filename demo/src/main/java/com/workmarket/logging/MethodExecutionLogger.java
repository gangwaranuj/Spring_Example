package com.workmarket.logging;

import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.springframework.core.Ordered;


public class MethodExecutionLogger implements Ordered {

	int order;

	public MethodExecutionLogger() {}

	public void logMethodExecution( JoinPoint joinpoint ) {
		Log targetLogger = LogFactory.getLog( joinpoint.getSignature().getDeclaringTypeName() );
		StringBuilder sb = new StringBuilder( "\n" );
		sb.append( "------------------[ Call ]------------------\n" );
		sb.append( "Class              : " ).append( joinpoint.getSignature().getDeclaringTypeName() ).append( "\n" );
		sb.append( "Method called      : " ).append( joinpoint.getSignature().toShortString() ).append( "\n" );
		sb.append( "Method arguments   : " ).append( Arrays.toString(joinpoint.getArgs()) ).append( "\n" );
		sb.append( "--------------------------------------------\n" );
		targetLogger.error( sb.toString() );
	}

	public int getOrder() {
		return this.order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

}

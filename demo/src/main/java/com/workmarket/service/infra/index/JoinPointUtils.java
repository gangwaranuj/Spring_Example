package com.workmarket.service.infra.index;

import org.aspectj.lang.JoinPoint;

import java.util.Collections;
import java.util.List;

public class JoinPointUtils {

	public static long getLongArgument(JoinPoint joinpoint, int index) {
		Object[] arguments = joinpoint.getArgs();

		if (index <= arguments.length) {
			return (long) arguments[index - 1];
		}

		return 0L;
	}

	public static List<Long> getLongsArgument(JoinPoint joinpoint, int index) {
		Object[] arguments = joinpoint.getArgs();

		if (index <= arguments.length) {
			return (List<Long>) arguments[index - 1];
		}

		return Collections.emptyList();
	}

}

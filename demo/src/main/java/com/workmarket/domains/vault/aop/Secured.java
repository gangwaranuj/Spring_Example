package com.workmarket.domains.vault.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Secured {
	public static final String OBSCURED = "obscured";
	public static final String PARTIALLY_OBSCURED = "partial";
	String value() default OBSCURED;
	int exposedDigits() default 4;
}
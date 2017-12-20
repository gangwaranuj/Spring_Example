package com.workmarket.vault.models;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Secured {
	public static final String UNSECURED = "unsecured";
	public static final String OBSCURED = "obscured";
	public static final String PARTIALLY_OBSCURED = "partial";
	public static final String MASKING_PATTERN = "x";
	public static final String PREPEND_MASKING_PATTERN = "XxXx";
	public static final String PREPEND = "prepend";
	String mode() default OBSCURED;
	int numExposed() default 4;
	String maskingPattern() default MASKING_PATTERN;
}


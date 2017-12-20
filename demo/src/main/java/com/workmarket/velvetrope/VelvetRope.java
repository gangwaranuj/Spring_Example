package com.workmarket.velvetrope;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Component
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface VelvetRope {
	String ROOT_PATH = "/";
	String DEFAULT_MESSAGE = "No Dogs Allowed!";

	Venue venue();
	boolean bypass() default false;
	String redirectPath() default ROOT_PATH;
	String message() default DEFAULT_MESSAGE;
}

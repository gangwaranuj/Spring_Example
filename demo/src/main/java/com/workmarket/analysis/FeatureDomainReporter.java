package com.workmarket.analysis;

import com.workmarket.splitter.WorkmarketComponent;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * Created by ant on 10/16/14.
 */
public class FeatureDomainReporter {

	public static void main(String [] args) {

		Reflections reflections = new Reflections("com.workmarket");

		Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(WorkmarketComponent.class);

		for(Class c : annotated) {

			System.out.println("Class --> " + c.getName());

			Reflections methodReflections = new Reflections (
				c.getName(),
				new MethodAnnotationsScanner()
			);
			Set<Method> methods = methodReflections.getMethodsAnnotatedWith(WorkmarketComponent.class);

			System.out.println("  Found " + methods.size() + " methods");

			for(Method m : methods) {

				System.out.println("    Method --> " + m.getName());

			}
		}

	}

}

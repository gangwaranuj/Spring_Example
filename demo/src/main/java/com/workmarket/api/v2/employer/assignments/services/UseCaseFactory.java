package com.workmarket.api.v2.employer.assignments.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

@Service
public class UseCaseFactory {
	@Autowired private WebApplicationContext context;

	/**
	 *
	 * Gets a new instance of a bean that implements the UseCase interface
	 *
	 * @param useCaseType - {@link UseCase<T>}
	 * @param useCaseConstructorArgs - {@link Object...}
	 * @param <T> - concrete {@link UseCase}
	 * @return useCase - implements {@link UseCase<T>}
	 *
	 */
	@SuppressWarnings("unchecked")
	public <T extends UseCase> T getUseCase(Class<T> useCaseType, Object... useCaseConstructorArgs) {
		// TODO[Jim]: Fix this after Spring 4 upgrade
		// Converts the class name to a bean name
		// In future version of Spring, this is unnecessary because there is an
		//   implementation of the getBean method that accepts a Type with args
		//   like this:
		//     context.getBean(useCaseType, args)
		//   The future version of the method also resolves the unchecked cast
		char c[] = useCaseType.getSimpleName().toCharArray();
		c[0] = Character.toLowerCase(c[0]);
		String beanName = new String(c);

		return (T) context.getBean(beanName, useCaseConstructorArgs);
	}
}

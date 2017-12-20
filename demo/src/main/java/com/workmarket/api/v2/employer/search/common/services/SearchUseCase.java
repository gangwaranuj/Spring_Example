package com.workmarket.api.v2.employer.search.common.services;

import com.workmarket.api.v2.employer.assignments.services.UseCase;
import com.workmarket.service.business.UserService;
import com.workmarket.service.exception.search.SearchException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Template for a use case related to search.
 *
 * @param <T> The type of template
 * @param <K> The value the template returns
 */
public abstract class SearchUseCase<T, K> implements UseCase<T, K> {

	@Autowired protected UserService userService;

	protected Exception exception;


	/**
	 * Constructor.
	 */
	public SearchUseCase() {

	}


	/**
	 * Return an instance of ourselves.
	 * @return T Ourselves
	 */
	protected abstract T me();

	/**
	 * Validates our input parameters before trying to do anything.
	 */
	protected void failFast() {
		// no op by default - behavior should be added by derived class
	}

	/**
	 * Initialize our use case.
	 */
	protected void init() {
		// no op by default - behavior should be added by derived class
	}

	/**
	 * Map our input data to our search criteria.
	 */
	protected void prepare() {
		// no op by default - behavior should be added by derived class
	}

	/**
	 * Executes our search.
	 */
	protected void process() {
		// no op by default - behavior should be added by derived class
	}

	/**
	 * Map our search results in to our response
	 */
	protected void finish() {
		// no op by default - behavior should be added by derived class
	}

	/**
	 * Handle any errors.
	 * @throws Exception Thrown when handling exceptions
	 */
	protected  abstract T handleExceptions() throws Exception;

	@Override
	public T execute() throws Exception {
		try {
			failFast();
			init();
			prepare();
			process();
			finish();
		} catch (Throwable t) {
			if (t instanceof RuntimeException) {
				if (t.getCause() != null && t.getCause() instanceof Exception) {
					exception = (Exception) t.getCause();
				} else {
					exception = new SearchException("Failed executing our use case", t);
				}
			} else {
				exception = (Exception) t;
			}
		}
		return me();
	}
}

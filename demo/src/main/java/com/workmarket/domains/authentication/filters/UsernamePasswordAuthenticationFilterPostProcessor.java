package com.workmarket.domains.authentication.filters;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * A BeanPostProcessor implementation to set custom username and password
 * parameters on UsernamePasswordAuthenticationFilter.
 * 
 * @see https://jira.springsource.org/browse/SEC-1445 for more information on this.
 * @see http://www.aravindanr.com/post/10138991000/custom-username-password-parameter-names-in-spring
 * 
 * @author Aravindan Ramkumar
 */
public class UsernamePasswordAuthenticationFilterPostProcessor implements BeanPostProcessor, Ordered {

	private final String usernameParameter;
	private final String passwordParameter;

	/**
	 * Constructor to initialize the parameter values.
	 * 
	 * @param usernameParameter The name of the username parameter.
	 * @param passwordParameter The name of the password parameter.
	 */
	public UsernamePasswordAuthenticationFilterPostProcessor(String usernameParameter, String passwordParameter) {
		this.usernameParameter = usernameParameter;
		this.passwordParameter = passwordParameter;
	}

	/**
	 * If the bean is an instance of UsernamePasswordAuthenticationFilter, its
	 * username and password parameters are set as the username and password
	 * values in this post processor.
	 * 
	 * @param bean The Spring bean to process.
	 * @param beanName The name of the Spring bean.
	 * @return Returns the processed bean.
	 * @throws BeansException in case of any error.
	 */
	@Override
	public Object postProcessBeforeInitialization(final Object bean, final String beanName) throws BeansException {
		if (bean instanceof UsernamePasswordAuthenticationFilter) {
			UsernamePasswordAuthenticationFilter filter = (UsernamePasswordAuthenticationFilter) bean;
			filter.setUsernameParameter(this.usernameParameter);
			filter.setPasswordParameter(this.passwordParameter);
		}
		return bean;
	}

	/**
	 * Does no post processing simply returns the bean.
	 * 
	 * @param bean The Spring bean to process.
	 * @param beanName The name of the Spring bean.
	 * @return Returns the processed bean.
	 * @throws BeansException in case of any error.
	 */
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	/**
	 * Returns the order of this post processor.
	 * 
	 * @return LOWEST_PRECEDENCE always.
	 */
	@Override
	public int getOrder() {
		return LOWEST_PRECEDENCE;
	}
}
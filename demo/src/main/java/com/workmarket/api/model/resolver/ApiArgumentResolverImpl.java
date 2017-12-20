package com.workmarket.api.model.resolver;

import com.google.common.collect.Maps;
import com.workmarket.web.exceptions.WebException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

public class ApiArgumentResolverImpl implements HandlerMethodArgumentResolver {
	private static final Logger logger = LoggerFactory.getLogger(ApiArgumentResolverImpl.class);

	private List<HandlerMethodArgumentResolver> argumentResolvers;
	private ConcurrentMap<MethodParameter, HandlerMethodArgumentResolver> cachedArgumentResolvers = Maps.newConcurrentMap();

	public ApiArgumentResolverImpl(List<HandlerMethodArgumentResolver> argumentResolvers) {
		this.argumentResolvers = argumentResolvers;
	}

	// for tests
	public void setArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		this.argumentResolvers = new ArrayList<>(argumentResolvers);
	}

	@Override
	public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
		if (methodParameter.hasParameterAnnotation(ApiArgumentResolver.class)) {
			HandlerMethodArgumentResolver cachedResolver = cachedArgumentResolvers.get(methodParameter);

			if (cachedResolver == null) {
				for (HandlerMethodArgumentResolver resolver : argumentResolvers) {
					try {
						Object response = resolver.resolveArgument(methodParameter, modelAndViewContainer, nativeWebRequest, webDataBinderFactory);

						if (response != null) {
							cachedArgumentResolvers.putIfAbsent(methodParameter, resolver);
							return response;
						}
					} catch (WebException e) {
						throw e;
					} catch (Exception ex) {
						logger.error("failed to resolve argument!", ex);
						return null;
					}
				}
			} else {
				return cachedResolver.resolveArgument(methodParameter, modelAndViewContainer, nativeWebRequest, webDataBinderFactory);
			}
		}

		return null;
	}

	@Override
	public boolean supportsParameter(MethodParameter methodParameter) {
		if (!methodParameter.hasParameterAnnotation(ApiArgumentResolver.class)) {
			return false;
		}
		for (HandlerMethodArgumentResolver resolver : argumentResolvers) {
			if (resolver.supportsParameter(methodParameter)) {
				return true;
			}
		}
		return false;
	}

}

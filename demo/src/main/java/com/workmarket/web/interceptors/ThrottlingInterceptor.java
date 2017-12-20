package com.workmarket.web.interceptors;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.RateLimiter;
import com.workmarket.web.exceptions.HttpException403;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Component
@ManagedResource(objectName = "Interceptors:name=ThrottlingInterceptor", description = "Throttles Requests")
public class ThrottlingInterceptor {
	private static final Log logger = LogFactory.getLog(ThrottlingInterceptor.class);

	private Map<String, RateLimiter> limiters = Maps.newHashMap();
	private Double requestsPerSecond = 10.0;

	public Object throttle(ProceedingJoinPoint pjp) throws Throwable {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
		String token = request.getParameter("access_token");

		if (limiters.get(token) == null) {
			limiters.put(token, RateLimiter.create(this.requestsPerSecond));
		}

		if (limiters.get(token).tryAcquire()) {
			return pjp.proceed();
		} else {
			logger.info("Too Many Requests from token: " + token);
			throw new HttpException403("Too Many Requests");
		}
	}

	@ManagedAttribute
	public void setRequestsPerSecond(Double requestsPerSecond) {
		this.requestsPerSecond = requestsPerSecond;
		limiters.clear();
	}

	@ManagedAttribute
	public Double getRequestsPerSecond() {
		return this.requestsPerSecond;
	}
}

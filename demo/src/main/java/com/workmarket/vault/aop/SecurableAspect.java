package com.workmarket.vault.aop;

import com.workmarket.domains.authentication.features.FeatureEvaluator;
import com.workmarket.domains.model.AbstractEntity;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.vault.models.Securable;
import com.workmarket.vault.services.VaultHelper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Order(2)
@Component
public class SecurableAspect {
	@Autowired FeatureEvaluator featureEvaluator;
	@Autowired AuthenticationService authenticationService;
	@Autowired VaultHelper vaultHelper;

	@Around("execution(* com.workmarket.dao.DAOInterface.saveOrUpdate(*)) && args(entity)")
	protected Object saveVaultedProperties(ProceedingJoinPoint joinPoint, Object entity) throws Throwable {
		final boolean isSecurable = entity.getClass().isAnnotationPresent(Securable.class);
		if (!isSecurable
			|| !featureEvaluator.hasFeature(authenticationService.getCurrentUserCompanyId(), "vaultObfuscate")) {
			return joinPoint.proceed();
		}

		vaultHelper.secureEntity((AbstractEntity)entity);

		return joinPoint.proceed();
	}
}

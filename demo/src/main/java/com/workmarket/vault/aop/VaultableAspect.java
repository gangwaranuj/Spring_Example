package com.workmarket.vault.aop;

import com.google.common.base.Joiner;
import com.workmarket.domains.authentication.features.FeatureEvaluator;
import com.workmarket.domains.model.AbstractEntity;
import com.workmarket.domains.model.tax.AbstractTaxEntity;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.vault.exceptions.VaultDuplicateTaxNumberException;
import com.workmarket.vault.exceptions.VaultRuntimeException;
import com.workmarket.vault.models.VaultKeyValuePair;
import com.workmarket.vault.models.Vaultable;
import com.workmarket.vault.services.VaultHelper;
import com.workmarket.vault.services.VaultServerService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Aspect
@Order(1)
@Component
public class VaultableAspect {
	private static final Log logger = LogFactory.getLog(VaultableAspect.class);

	@Autowired VaultHelper vaultHelper;
	@Autowired VaultServerService vaultServerService;
	@Autowired FeatureEvaluator featureEvaluator;
	@Autowired AuthenticationService authenticationService;

	@Around("execution(* com.workmarket.dao.DAOInterface.saveOrUpdate(*)) && args(entity)")
	protected Object saveVaultedProperties(ProceedingJoinPoint joinPoint, Object entity) throws Throwable {
		final boolean isVaultable = entity.getClass().isAnnotationPresent(Vaultable.class);
		if (!isVaultable) {
			return joinPoint.proceed();
		}
		if (!featureEvaluator.hasFeature(authenticationService.getCurrentUserCompanyId(), "vaultWrite")) {
			return joinPoint.proceed();
		}

		final AbstractEntity ent = (AbstractEntity) entity;
		final Long userId = authenticationService.getCurrentUserId();
		final Long userCompanyId = authenticationService.getCurrentUserCompanyId();

		// If this is an update, we do not want to put values in the vault as by now all the db values are obfuscated
		// so short circuit and return
		if (isUpdate(ent)) {
			logger.info(String.format("User %s[companyId=%s] updating entity %s[%s]",
					userId, userCompanyId, ent.getClass().getSimpleName(), ent.getId()));
			return joinPoint.proceed();
		}

		// Fetch the un-obfuscated pairs before they become obfuscated ...
		List<VaultKeyValuePair> pairsWithValues = vaultHelper.getVaultedValues(ent);

		Object result = joinPoint.proceed();

		try {
			// ... make the call again to get the vault keys. Fully-formed keys are only possible when the
			// entity has a non-null ID, and that only happens when the entity is saved. So now use the fully-formed
			// keys and the un-obfuscated values we cached before the save to save the vault values.
			final List<VaultKeyValuePair> pairsWithKeys = vaultHelper.getVaultedValues(ent);

			final List<VaultKeyValuePair> pairs = new ArrayList<>();
			for (int i = 0; i < pairsWithValues.size(); i++) {
				for (VaultKeyValuePair p : pairsWithKeys) {
					if (vaultHelper.getFieldNameFromId(p.getId()).equals(vaultHelper.getFieldNameFromId(pairsWithValues.get(i).getId()))) {
						final String unencryptedValue = pairsWithValues.get(i).getValue();
						pairs.add(new VaultKeyValuePair(p.getId(), unencryptedValue));

						// Business rules dictate can't have duplicate tax numbers in a given country. This check
						// is done by creating a new entry in the vault whose key is the tax number and country and
						// value is the company number. If the key/value pair exists, we have found a duplicate. However, it's still
						// allowed for a company that owns a tax number to create as many tax entities with the same tax number.
						if (ent instanceof AbstractTaxEntity
							&& vaultHelper.getFieldNameFromId(p.getId()).equals("taxNumber")) {
							AbstractTaxEntity e = (AbstractTaxEntity) entity;

							VaultKeyValuePair duplicateEntry = vaultHelper.getDuplicateKeyValueEntry(e.getIsoCountry().getISO3(), unencryptedValue);

							if (vaultHelper.isDuplicateOutsideCompany(duplicateEntry, e.getCompany().getCompanyNumber())) {
								throw new VaultDuplicateTaxNumberException("Duplicate tax number found for company "
									+ e.getCompany().getCompanyNumber() + " for country " + e.getIsoCountry()
									+ "; duplicate key/value: " + duplicateEntry.toString());
							}

							if (duplicateEntry.isEmpty()) {
								pairs.add(vaultHelper.buildDuplicateTaxNumberCheckPair(e, unencryptedValue));
							}
						}
					}
				}
			}

			vaultServerService.post(pairs);
			audit(pairs);
		} catch (final VaultDuplicateTaxNumberException e) {
			throw e;
		} catch (final Exception e) {
			// Don't want the aspect throwing an UndeclaredThrowableException, we want more info on error
			throw new VaultRuntimeException("there was an error saving vault values", e);
		}

		return result;
	}

	private boolean isUpdate(final AbstractEntity entity) {
		return entity.getId() != null;
	}

	private void audit(final List<VaultKeyValuePair> pairs) {
		final List<String> keys = new ArrayList<>();
		for (final VaultKeyValuePair p : pairs) {
			keys.add(p.getId());
		}
		final Long userId = authenticationService.getCurrentUserId();
		final Long userCompanyId = authenticationService.getCurrentUserCompanyId();
		logger.info(String.format("User %s[companyId=%s] creating vault entries [%s]",
				userId, userCompanyId, Joiner.on(",").join(keys)));
	}
}

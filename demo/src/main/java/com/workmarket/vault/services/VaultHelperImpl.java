package com.workmarket.vault.services;

import com.google.common.base.Joiner;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import com.workmarket.common.exceptions.ServiceUnavailableException;
import com.workmarket.domains.authentication.features.FeatureEvaluator;
import com.workmarket.domains.model.AbstractEntity;
import com.workmarket.domains.model.tax.AbstractTaxEntity;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.utility.DigestUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.vault.exceptions.VaultRuntimeException;
import com.workmarket.vault.models.Securable;
import com.workmarket.vault.models.Secured;
import com.workmarket.vault.models.VaultKeyValuePair;
import com.workmarket.vault.models.Vaultable;
import com.workmarket.vault.models.Vaulted;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class VaultHelperImpl implements VaultHelper {
	private static final Log logger = LogFactory.getLog(VaultHelperImpl.class);
	private static final String KEY_FORMAT = "%s:%s:%s"; // <class name>:<entity id>:<field name>
	private static final String COUNTRY_TAX_NUMBER_FORMAT = "taxNumber:%s:%s";
	private static final String COUNTRY_TAX_NUMBER_FORMAT_SALT = "1c261b59-e49d-4a4a-985d-079d9683f052";
	private static final int MAX_MULTI_GET_ENTRIES = 35; // conservative limit of IDs that can be placed in URL

	@Autowired VaultServerService vaultServerService;
	@Autowired FeatureEvaluator featureEvaluator;
	@Autowired AuthenticationService authenticationService;

	@Override
	public List<VaultKeyValuePair> getVaultedValues(AbstractEntity entity) throws RuntimeException {
		List<VaultKeyValuePair> results = new ArrayList<>();

		if (entity == null) {
			return results;
		}

		for (Field field : getVaultedFields(entity.getClass())) {
			Object value;
			try {
				field.setAccessible(true);
				value = field.get(entity);
			} catch (Exception e) {
				logger.error("error invoking getter", e);
				throw new RuntimeException("error invoking getter");
			}

			if (value != null && StringUtils.isNotEmpty(value.toString())) {
				String key = buildKey(entity, field.getName());
				results.add(new VaultKeyValuePair(key, StringUtilities.removePrepend(value.toString())));
			}

		}

		return results;
	}

	@Override
	public void setVaultedValues(AbstractEntity entity) {
		if (entity == null || entity.getId() == null) {
			return;
		}

		for (Field field : getVaultedFields(entity.getClass())) {
			try {
				field.setAccessible(true);
				String key = buildKey(entity, field.getName());
				VaultKeyValuePair pair = vaultServerService.get(key);
				if (!pair.isEmpty()) {
					field.set(entity, pair.getValue());
				}
			} catch (IllegalAccessException e) {
				logger.error("error invoking setter", e);
				throw new RuntimeException("error invoking setter");
			} catch (ServiceUnavailableException e) {
				logger.error(e);
				throw new RuntimeException("vault service unavailable when setting values");
			}
		}
	}

	@Override
	public String buildKey(AbstractEntity entity, String fieldName) {
		if (entity != null) {
			return String.format(KEY_FORMAT, entity.getClass().getSimpleName(), entity.getId(), fieldName);
		}

		return "";
	}

	@Override
	public String buildCountryTaxNumberKey(String iso3Country, String taxNumber) {
		return DigestUtilities.digestAsHex("SHA-256", String.format(COUNTRY_TAX_NUMBER_FORMAT, iso3Country.toLowerCase(), taxNumber),
			COUNTRY_TAX_NUMBER_FORMAT_SALT);
	}

	@Override
	public VaultKeyValuePair buildDuplicateTaxNumberCheckPair(AbstractTaxEntity entity, String taxNumber) {
		String key = buildCountryTaxNumberKey(entity.getIsoCountry().getISO3(), taxNumber);
		String value = entity.getCompany().getCompanyNumber();

		return new VaultKeyValuePair(key, value);
	}

	@Override
	public boolean isDuplicateOutsideCompany(String iso3Country, String taxNumber, String companyNumber) {
		return isDuplicateOutsideCompany(getDuplicateKeyValueEntry(iso3Country, taxNumber), companyNumber);
	}

	@Override
	public boolean isDuplicateOutsideCompany(VaultKeyValuePair pair, String companyNumber) {
		if (pair.isEmpty()) {
			return false;
		} else {
			return !pair.getValue().equals(companyNumber);
		}
	}

	@Override
	public VaultKeyValuePair removeEntityIdFromDuplicatePair(VaultKeyValuePair duplicateEntry, String id) {
		List<String> ids = getIdsFromDuplicateEntryValue(duplicateEntry.getValue());

		ids.remove(id);
		if (ids.isEmpty()) {
			return new VaultKeyValuePair();
		} else {
			String companyNumber = getCompanyNumberFromDuplicateEntryValue(duplicateEntry.getValue());
			return new VaultKeyValuePair(duplicateEntry.getId(), buildDuplicateEntryValue(companyNumber, ids));
		}
	}

	private String buildDuplicateEntryValue(String companyNumber, List<String> ids) {
		return String.format("%s:%s", companyNumber, Joiner.on(",").join(ids));
	}

	@Override
	public VaultKeyValuePair addEntityIdToDuplicatePair(VaultKeyValuePair duplicateEntry, String id) {
		List<String> ids = getIdsFromDuplicateEntryValue(duplicateEntry.getValue());
		if (ids.contains(id)) {
			return duplicateEntry.makeCopy();
		} else {
			return new VaultKeyValuePair(duplicateEntry.getId(),
				addIdToDuplicateEntryValue(duplicateEntry.getValue(), id));
		}
	}

	private String addIdToDuplicateEntryValue(String value, String id) {
		return String.format("%s,%s", value, id);
	}

	@Override
	public String getCompanyNumberFromDuplicateEntryValue(String value) {
		return value.split(":")[0];
	}

	private List<String> getIdsFromDuplicateEntryValue(String value) {
		List<String> list = new ArrayList<>();
		for (String s : value.split(":")[1].split(",")) {
			list.add(s);
		}
		return list;
	}

	@Override
	public VaultKeyValuePair getDuplicateKeyValueEntry(String iso3Country, String taxNumber) {
		String key = buildCountryTaxNumberKey(iso3Country, taxNumber);

		try {
			return vaultServerService.get(key);
		} catch (Exception e) {
			throw new RuntimeException("Error fetching vault values", e);
		}
	}

	@Override
	public VaultKeyValuePair getDuplicateKeyValueEntryFromVault(AbstractTaxEntity ent, String fieldName) {
		VaultKeyValuePair vaultPair = get(ent, fieldName, "");
		if (vaultPair.isEmpty()) {
			throw new VaultRuntimeException("missing empty vault value for key " + vaultPair.getId());
		}

		return buildDuplicateTaxNumberCheckPair(ent, vaultPair.getValue());
	}

	@Override
	public List<VaultKeyValuePair> multiGet(List<? extends AbstractEntity> partition, String field) {
		TreeSet<String> keys = new TreeSet<>();
		for (AbstractEntity ent : partition) {
			keys.add(buildKey(ent, field));
		}
		try {
			return vaultServerService.get(new ArrayList<>(keys));
		} catch (Exception e) {
			throw new RuntimeException("Error Vault multi get", e);
		}
	}

	@Override
	public <T extends AbstractEntity> Map<Long, String> mapEntityIdToFieldValue(List<T> entities, Class<T> type, String field) {
		Map<Long, String> map = new HashMap<>();
		List<List<T>> partitions = partitionWrapper(entities, MAX_MULTI_GET_ENTRIES);

		for (List<T> partition : partitions) {
			if (featureEvaluator.hasFeature(authenticationService.getCurrentUserCompanyId(), "vaultRead")) {
				List<VaultKeyValuePair> pairs = multiGet(partition, field);
				for (VaultKeyValuePair pair : pairs) {
					map.put(getIdFromKey(pair.getId()), pair.getValue());
				}
			} else {
				Field f = org.springframework.util.ReflectionUtils.findField(type, field);
				f.setAccessible(true);
				for (T item : partition) {
					String value = "";
					try {
						value = (String) f.get(item);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
					map.put(item.getId(), value);
				}
			}
		}

		return map;
	}

	@Override
	public <T extends AbstractEntity> void unobfuscateEntityFields(final List<T> entities) {
			if (CollectionUtils.isEmpty(entities)) {
				return;
			}

			for (final AbstractEntity ent : entities) {
				if (featureEvaluator.hasFeature(authenticationService.getCurrentUserCompanyId(), "vaultObfuscate")) {
					for (final Field field : getSecuredFields(ent.getClass())) {
						unobfuscateField(ent, field);
					}
				}
			}
	}

	@Override
	public <T extends AbstractEntity> void unobfuscateEntityFields(final T entity) {
			unobfuscateEntityFields(ImmutableList.of(entity));
	}

	private void unobfuscateField(final AbstractEntity ent, final Field field) {
		try {
			final String unobfuscatedValue = get(ent, field.getName(), "").getValue();
			if (StringUtils.isNotBlank(unobfuscatedValue)) {
				field.setAccessible(true);
				field.set(ent, unobfuscatedValue);
			}
		} catch (final Exception e) {
			logger.error("error unobfuscating field", e);
		}
	}

	public <T extends AbstractEntity> List<List<T>> partitionWrapper(List<T> entities, int size) {
		return Lists.partition(entities, size);
	}

	@Override
	public void secureEntity(AbstractEntity entity) {
		if (entity == null) {
			return;
		}

		if (featureEvaluator.hasFeature(authenticationService.getCurrentUserCompanyId(), "vaultObfuscate")) {
			for (Field field : getSecuredFields(entity.getClass())) {
				secureField(field, entity);
			}
		}
	}

	private void secureField(Field field, AbstractEntity entity) {
		try {
			field.setAccessible(true);
			String value = (String) field.get(entity);
			Secured ann = AnnotationUtils.getAnnotation(field, Secured.class);
			field.set(entity, obscureValue(value, ann));
		} catch (Exception e) {
			logger.error("error invoking getter", e);
			throw new RuntimeException("error invoking getter");
		}
	}

	private String obscureValue(String value, Secured annotation) {
		if (value == null) {
			return value;
		}

		if (featureEvaluator.hasGlobalFeature("vaultObfuscatePrepend")) {
			if (value.startsWith(Secured.PREPEND_MASKING_PATTERN)) {
				return value;
			}

			return Secured.PREPEND_MASKING_PATTERN + value;
		}

		switch (annotation.mode()) {
			case Secured.UNSECURED:
				return value;
			case Secured.OBSCURED:
				return StringUtilities.showLastNCharacters(value, annotation.maskingPattern(), 0);
			case Secured.PARTIALLY_OBSCURED:
				return StringUtilities.showLastNCharacters(value, annotation.maskingPattern(), annotation.numExposed());
			case Secured.PREPEND:
				return Secured.PREPEND_MASKING_PATTERN + value;
			default: // partial
				return StringUtilities.showLastNCharacters(value, annotation.maskingPattern(), annotation.numExposed());
		}
	}

	@Override
	public VaultKeyValuePair get(AbstractEntity entity, String fieldName, String defaultValue) {
		Long companyId = authenticationService.getCurrentUserCompanyId();
		String key = buildKey(entity, fieldName);
		VaultKeyValuePair pair = null;

		try {
			pair = vaultServerService.get(key);
		} catch (ServiceUnavailableException e) {
			logger.error("Missing fault value");
		}

		// for logging purposes
		if (!featureEvaluator.hasFeature(companyId, "vaultObfuscate")) {
			if (pair != null && !pair.isEmpty() && StringUtils.isNotEmpty(defaultValue)
				&& !pair.getValue().equals(defaultValue)) {
				String salt = UUID.randomUUID().toString();
				String vaultHash = DigestUtilities.digestAsHex("SHA-256", pair.getValue(), salt);
				String dbHash = DigestUtilities.digestAsHex("SHA-256", defaultValue, salt);
				String logKey = "log_" + salt;
				String log = String.format("vault values unequal: (%d %s) (%d %s) %s %s %s",
					pair.getValue().length(), vaultHash, (defaultValue == null ? 0 : defaultValue.length()), dbHash, salt, pair.getId(), logKey);
				logger.error(log);
				logger.error(Joiner.on("\n").join(Thread.currentThread().getStackTrace()));

				try {
					vaultServerService.post(new VaultKeyValuePair(logKey, log + " ---- " + defaultValue));
				} catch (Exception e) {
					logger.error("error logging to vault");
					// noop
				}
			}
		}

		if (featureEvaluator.hasFeature(companyId, "vaultRead")) {
			if (pair != null && !pair.isEmpty()) {
				return pair;
			}

			return new VaultKeyValuePair(key, "");
		}

		return new VaultKeyValuePair(key, defaultValue);
	}

	@Override
	public String getFieldNameFromId(String id) {
		Matcher matcher = Pattern.compile(KEY_FORMAT.replace("%s", "(\\S+)")).matcher(id);
		if (matcher.matches()) {
			return matcher.group(3);
		}
		return "";
	}

	@Override
	public Long getIdFromKey(String key) {
		Matcher matcher = Pattern.compile(KEY_FORMAT.replace("%s", "(\\S+)")).matcher(key);
		if (matcher.matches()) {
			return Long.parseLong(matcher.group(2));
		}
		return null;
	}

	private List<Field> getFieldsWithAnnotations(Class<?> clazz, Class<? extends Annotation> classAnnotation, Class<? extends Annotation> fieldAnnotation) {
		List<Field> results = new ArrayList<>();

		while (clazz != null) {
			if (!AnnotationUtils.isAnnotationDeclaredLocally(classAnnotation, clazz)) {
				clazz = clazz.getSuperclass(); // go to next class up in heirarchy
				continue;
			}

			for (Field field : clazz.getDeclaredFields()) {
				if (field.isAnnotationPresent(fieldAnnotation)) { // look for fields with this annotation
					results.add(field);
				}
			}

			clazz = clazz.getSuperclass(); // go to next class up in heirarchy
		}

		return results;

	}

	private List<Field> getSecuredFields(Class<?> clazz) {
		return getFieldsWithAnnotations(clazz, Securable.class, Secured.class);
	}

	private List<Field> getVaultedFields(Class<?> clazz) {
		return getFieldsWithAnnotations(clazz, Vaultable.class, Vaulted.class);
	}
}

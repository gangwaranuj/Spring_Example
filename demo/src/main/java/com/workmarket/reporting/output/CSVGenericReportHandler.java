package com.workmarket.reporting.output;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.workmarket.domains.model.customfield.WorkCustomField;
import com.workmarket.domains.model.reporting.Entity;
import com.workmarket.domains.model.reporting.EntityResponseForReport;
import com.workmarket.domains.model.reporting.ReportRequestData;
import com.workmarket.domains.model.reporting.ReportingContext;
import com.workmarket.reporting.util.ReportingUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CSVGenericReportHandler extends AbstractReportHandler {

	private static final Log logger = LogFactory.getLog(CSVGenericReportHandler.class);

	private String batchReportDirectory;
	private char delimiter = DELIMITER;
	private String fileExtension = DEFAULT_FILE_EXTENSION;
	private final static char DELIMITER = ',';
	private final static String DEFAULT_FILE_EXTENSION = "csv";

	/*
	 * (non-Javadoc)
	 *
	 */
	public EntityResponseForReport generateReport(ReportingContext context, ReportRequestData entityRequestForReport, List<?> rows, List<WorkCustomField> workCustomFields) throws Exception {

		// generate rows and headers
		EntityResponseForReport entityResponseForReport = populateEntityResponseForReport(
			entityRequestForReport, rows, getSortedEntitiesWithCustomFieldEntities(entityRequestForReport, context.getEntities(), workCustomFields));
		entityResponseForReport.setFileName("");

		return entityResponseForReport;
	}

	private List<Entity> getSortedEntitiesWithCustomFieldEntities(ReportRequestData entityRequestForReport, final Map<String, Entity> entities, List<WorkCustomField> workCustomFields) throws Exception {
		Assert.notNull(entities);
		List<Entity> result = ReportingUtil.buildSortedEntities(entityRequestForReport, entities);

		if (CollectionUtils.isNotEmpty(workCustomFields)) {
			final Locale locale = getInferredLocaleFromEntities(entities);
			for (WorkCustomField field : workCustomFields) {
				final String fieldName = field.getName();

				Entity foundEntity = Iterables.find(result, new Predicate<Entity>() {
					@Override
					public boolean apply(Entity entity) {
						return entity.getKeyName().equals(fieldName);
					}
				}, null);

				if (foundEntity == null) { // we don't want to add dupes
					Entity entity = new Entity();
					entity.setKeyName(fieldName);
					entity.setDisplayNameM(ImmutableMap.of(locale, fieldName));
					result.add(entity);
				}
			}
			Collections.sort(result);
		}

		return result;
	}

	private Locale getInferredLocaleFromEntities(Map<String, Entity> entities) {
		if (CollectionUtils.isNotEmpty(entities.keySet())) {
			String key = (String) entities.keySet().toArray()[0]; // arbitrarly take the first one
			if (entities.get(key) != null && entities.get(key).getDisplayNameM() != null && CollectionUtils.isNotEmpty(entities.get(key).getDisplayNameM().keySet())) {
				return (Locale)entities.get(entities.keySet().toArray()[0]).getDisplayNameM().keySet().toArray()[0];
			}
		}

		return Locale.ENGLISH; // default to English
	}

	/**
	 * @return
	 */
	public String getBatchReportDirectory() {
		return batchReportDirectory;
	}

	/**
	 * @param batchReportDirectory
	 */
	@Required
	public void setBatchReportDirectory(String batchReportDirectory) {
		this.batchReportDirectory = batchReportDirectory;
	}

	/**
	 * @return
	 */
	public char getDelimiter() {
		return delimiter;
	}

	/**
	 * @param delimiter
	 */
	public void setDelimiter(char delimiter) {
		this.delimiter = delimiter;
	}

	/**
	 * @return
	 */
	public String getFileExtension() {
		return fileExtension;
	}

	/**
	 * @param fileExtension
	 */
	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}
}

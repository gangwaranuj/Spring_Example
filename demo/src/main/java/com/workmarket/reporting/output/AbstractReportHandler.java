package com.workmarket.reporting.output;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.customfield.WorkCustomField;
import com.workmarket.domains.model.reporting.*;
import com.workmarket.reporting.format.DateFormat;
import com.workmarket.reporting.query.AbstractReportingQuery;
import org.apache.commons.beanutils.NestedNullException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

public abstract class AbstractReportHandler {

	private static final Log logger = LogFactory.getLog(AbstractReportHandler.class);

	/**
	 * @param entities
	 * @param entityRequestForReport
	 * @param rows
	 * @param workCustomFields
	 * @return
	 * @throws Exception
	 */
	public abstract EntityResponseForReport generateReport(ReportingContext context, ReportRequestData entityRequestForReport, List<?> rows, List<WorkCustomField> workCustomFields) throws Exception;


	/**
	 * @param entityRequestForReport
	 * @param rows
	 * @param entities
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected EntityResponseForReport populateEntityResponseForReport(
			ReportRequestData entityRequestForReport,
			List<?> rows,
			List<Entity> sortedEntities) throws Exception {

		List<Map<String, GenericField>> genericFields = (List<Map<String, GenericField>>) rows;
		EntityResponseForReport entityResponseForReport = new EntityResponseForReport();

		List<String> headers = buildHeader(entityRequestForReport, sortedEntities);
		entityResponseForReport.setHeaders(headers);

		List<List<String>> allRows = buildRows(genericFields, sortedEntities);
		entityResponseForReport.setRows(allRows);

		return entityResponseForReport;
	}

	protected List<String> buildHeader(ReportRequestData entityRequestForReport, List<Entity> sortedEntities) throws Exception {
		List<String> header = new ArrayList<String>();

		for (Entity entity : sortedEntities) {
			header.add(entity.getDisplayNameM().get(entityRequestForReport.getLocale()));
		}

		return header;
	}

	/**
	 * @param entityRequestForReport
	 * @param genericFields
	 * @param entities
	 * @return
	 * @throws Exception
	 */
	protected List<List<String>> buildRows(List<Map<String, GenericField>> genericFields, List<Entity> sortedEntities) throws Exception {

		List<List<String>> rows = new ArrayList<List<String>>();

		for (Map<String, GenericField> genericFieldMap : genericFields) {
			rows.add(buildRow(genericFieldMap, sortedEntities));
		}
		return rows;
	}

	public List<String> buildRow(Map<String, GenericField> fieldMap, List<Entity> sortedEntities) {

		List<String> row = Lists.newArrayList();

		for (Entity entity : sortedEntities) {

			GenericField genericField = null;
			try {
				genericField = fieldMap.get(entity.getKeyName());
			} catch (NestedNullException nne) {
				logger.warn(nne.getMessage());
			}

			// TODO if there isn't a configured formatter, can use type within Entity to determine.....
			if (genericField != null && genericField.getValue() != null) {
				String formattedData;
				if (entity.getFormat() instanceof DateFormat) {
					GenericField genericFieldTimeZone = fieldMap.get(AbstractReportingQuery.TIME_ZONE_ID);
					DateFormat dateFormat = (DateFormat) entity.getFormat();
					formattedData = dateFormat.format(genericField, (String) genericFieldTimeZone.getValue());
				} else {
					if (entity.getFormat() != null) {
						formattedData = entity.getFormat().format(genericField);
					} else {
						formattedData = (String) genericField.getValue();
					}
				}

				row.add(formattedData);
			} else {
				row.add("");
			}
		}
		return row;
	}

}

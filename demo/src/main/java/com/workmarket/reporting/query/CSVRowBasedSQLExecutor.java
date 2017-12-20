package com.workmarket.reporting.query;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.workmarket.data.report.work.CustomFieldReportFilters;
import com.workmarket.data.report.work.CustomFieldReportRow;
import com.workmarket.domains.model.reporting.GenericField;
import com.workmarket.reporting.exception.ReportingFormatException;
import com.workmarket.reporting.util.CSVReportWriter;
import com.workmarket.utility.sql.SQLBuilder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

public class CSVRowBasedSQLExecutor extends AbstractSQLExecutor {

	private static final Log logger = LogFactory.getLog(CSVRowBasedSQLExecutor.class);
	private final static String WORK_WORK_ID = "work_work_id";
	private static final long serialVersionUID = -9193288036317817963L;

	private CSVReportWriter csvReportWriter;


	public CSVRowBasedSQLExecutor() {
		super();
	}


	@Override
	@SuppressWarnings("unchecked")
	public <T> Optional<List<T>> query() throws IOException, ReportingFormatException {
		if (sqlBuilder == null || csvReportWriter == null) return Optional.absent();

		// Bring results
		List<Map<String, GenericField>> genericFieldList = jdbcTemplate.query(builtSql, sqlBuilder.getParams(), this.rowMapper);
		List<Map<String, GenericField>> fullFieldList = new ArrayList();

		List<Long> workIds = new ArrayList<>();
		for (Map<String, GenericField> row : genericFieldList) {
			workIds.add(Long.valueOf(row.get(WORK_WORK_ID).getValue().toString()));
		}

		// Process Work Custom Fields if they are enabled
		if (csvReportWriter.getEntityRequest().getHasWorkCustomFields() && isNotEmpty(workIds)) {
			CustomFieldReportFilters filters = new CustomFieldReportFilters();
			filters.setWorkIds(workIds);
			filters.setVisibleToBuyer(true);
			filters.setWorkCustomFieldIds(csvReportWriter.getEntityRequest().getWorkCustomFieldIds());

			SQLBuilder builder = buildWorkCustomFieldSQL(null, csvReportWriter.getEntityRequest().getCompanyId(), filters);
			// Return actual custom field values from the database
			List<Map<String, Object>> customFields = jdbcTemplate.queryForList(builder.build(), builder.getParams());

			// Collate custom field values indexed by work ID.
			Map<Long, List<CustomFieldReportRow>> customFieldsMap = Maps.newLinkedHashMap();
			for (Map<String, Object> row : customFields) {
				Long workId = ((Integer) row.get("workId")).longValue();

				if (!customFieldsMap.containsKey(workId))
					customFieldsMap.put(workId, Lists.<CustomFieldReportRow>newArrayList());

				customFieldsMap.get(workId).add(new CustomFieldReportRow(
						((Integer) row.get("fieldId")).longValue(),
						(String) row.get("name"),
						(String) row.get("value")));
			}

			for (Map<String, GenericField> genericField : genericFieldList) {
				genericField = new LinkedHashMap<>(genericField);
				Long workId = Long.valueOf(genericField.get(WORK_WORK_ID).getValue().toString());
				List<CustomFieldReportRow> customFieldReportRows = customFieldsMap.get(workId);

				if (CollectionUtils.isNotEmpty(csvReportWriter.getEntityRequest().getWorkCustomFieldNames())) {
					for (final String workCustomFiledName : csvReportWriter.getEntityRequest().getWorkCustomFieldNames()) {
						CustomFieldReportRow r = customFieldReportRows != null ? Iterables.find(customFieldReportRows, new Predicate<CustomFieldReportRow>() {
							@Override
							public boolean apply(CustomFieldReportRow customFieldReportRow) {
								return workCustomFiledName.equals(customFieldReportRow.getFieldName());
							}
						}, null) : null;

						genericField.put(workCustomFiledName, new GenericField(workCustomFiledName, r == null ? "" : r.getFieldValue()));
						csvReportWriter.getCustomFieldsNames().add(workCustomFiledName);
					}
				} else if (customFieldReportRows != null) {
					for (CustomFieldReportRow customFieldReportRow : customFieldReportRows) {
						String fieldName = customFieldReportRow.getFieldName();
						csvReportWriter.getCustomFieldsNames().add(fieldName);
						genericField.put(fieldName, new GenericField(fieldName, customFieldReportRow.getFieldValue()));
					}
				}

				fullFieldList.add(new LinkedHashMap<>(genericField));
			}
		} else {
			fullFieldList.addAll(genericFieldList);
		}

		// need to augment fullFieldList with missing field values

		csvReportWriter.open();
		// Write genericFieldList to CSV
		for (Map<String, GenericField> row : fullFieldList) {
			try {
				csvReportWriter.addRow(row);
			} catch (IOException e) {
				logger.error(String.format("logging failed for report %s on row [%s]", csvReportWriter.getFilename(), row));
			}
		}

		try {
			csvReportWriter.close();
		} catch (IOException e) {
			logger.error(String.format("file save failed for report %s", csvReportWriter.getFilename()));
		}
		return Optional.absent();
	}

	public void setCSVReportWriter(CSVReportWriter fileWriter) {
		this.csvReportWriter = fileWriter;
	}

	public CSVReportWriter getCsvReportWriter() {
		return csvReportWriter;
	}

	private SQLBuilder buildWorkCustomFieldSQL(Long userId, Long companyId, CustomFieldReportFilters filters) {
		SQLBuilder builder = new SQLBuilder();
		builder.setDistinct(true);
		builder.addColumns("work.id AS workId", "field.id AS fieldId", "field.name")
				.addTable("work")
				.addJoin("INNER JOIN work_custom_field_group_association association ON work.id = association.work_id")
				.addJoin("INNER JOIN work_custom_field_group g ON g.id = association.work_custom_field_group_id")
				.addJoin("INNER JOIN work_custom_field field ON g.id = field.work_custom_field_group_id")
				.addJoin("INNER JOIN work_custom_field_saved saved ON saved.work_custom_field_id = field.id " +
						" AND saved.work_custom_field_group_association_id = association.id");

		builder.addWhereClause(" g.deleted = false")
				.addWhereClause(" field.deleted = false")
				.addParam("userId", userId)
				.addParam("companyId", companyId)
				.addAscOrderBy("g.id")
				.addAscOrderBy("field.position")
				.addAscOrderBy("field.id");

		if (userId == null && companyId == null) {
			builder.addColumn("IF(field.visible_to_resource_flag = true, saved.value, '') AS value");
		} else if (userId == null) {
			builder.addColumn("IF((work.company_id = :companyId), saved.value, IF(field.visible_to_resource_flag = true, saved.value, '')) AS value");
		} else if (companyId == null) {
			builder.addColumn("IF((work.buyer_user_id = :userId), saved.value, IF(field.visible_to_resource_flag = true, saved.value, '')) AS value");
		} else {
			builder.addColumn("IF((work.buyer_user_id = :userId OR work.company_id = :companyId), saved.value, IF(field.visible_to_resource_flag = true, saved.value, '')) AS value");
		}

		if (filters != null) {
			if (filters.hasShowOnDashboardFilter()) {
				builder.addWhereClause(" field.show_on_dashboard = :showOnDashboard")
						.addParam("showOnDashboard", filters.getShowOnDashboard());
			}
			if (filters.hasShowOnInvoiceFilter()) {
				builder.addWhereClause(" field.show_on_invoice = :showOnInvoice")
						.addParam("showOnInvoice", filters.getShowOnInvoice());
			}
			if (filters.hasVisibleToBuyerFilter()) {
				builder.addWhereClause(" field.visible_to_owner_flag = :visibleToBuyer ")
						.addParam("visibleToBuyer", filters.getVisibleToBuyer());
			}
			if (filters.hasVisibleToResourceFilter()) {
				builder.addWhereClause(" field.visible_to_resource_flag = :visibleToResource ")
						.addParam("visibleToResource", filters.getVisibleToResource());
			}
			if (filters.hasShowOnSentStatusFilter()) {
				builder.addWhereClause(" field.show_on_sent_status = :showOnSentStatus ")
						.addParam("showOnSentStatus", filters.getShowOnSentStatus());
			}
			if (filters.hasWorkFilter()) {
				builder.addWhereClause(" work.id IN (" + StringUtils.join(filters.getWorkIds(), ",") + ")");
			}
			if (filters.hasShowOnEmailFilter()) {
				builder.addWhereClause(" field.show_in_assignment_email = :showOnEmail ")
						.addParam("showOnEmail", filters.getShowOnEmail());
			}
			if (filters.hasCustomFieldIdFilter()) {
				builder.addWhereClause(" field.id IN (" + StringUtils.join(filters.getWorkCustomFieldIds(), ",") + ")");
			}
		}
		return builder;
	}
}

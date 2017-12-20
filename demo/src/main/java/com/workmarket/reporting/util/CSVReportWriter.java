package com.workmarket.reporting.util;

import au.com.bytecode.opencsv.CSVWriter;
import com.google.common.collect.Lists;
import com.workmarket.domains.model.reporting.Entity;
import com.workmarket.domains.model.reporting.GenericField;
import com.workmarket.domains.model.reporting.ReportRequestData;
import com.workmarket.reporting.exception.ReportingFormatException;
import com.workmarket.reporting.format.DateFormat;
import com.workmarket.reporting.format.HyperTextFormat;
import com.workmarket.reporting.format.LastNameFormat;
import com.workmarket.reporting.format.TitleLinkFormat;
import com.workmarket.reporting.query.AbstractReportingQuery;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by nick on 6/26/12 12:07 PM
 */
public class CSVReportWriter extends CSVBaseWriter implements Serializable {

	private static final long serialVersionUID = 6512268290649753412L;
	private Map<String, Entity> entityMap;
	private static final Log logger = LogFactory.getLog(CSVReportWriter.class);

	private ReportRequestData entityRequest;
	private List<Entity> sortedEntities;
	private Set<String> customFieldsNames;

	public CSVReportWriter(ReportRequestData entityRequest, Map<String, Entity> entityMap, String filename, String directory) throws ReportingFormatException {
		super(directory, filename);
		this.entityRequest = entityRequest;
		this.entityMap = entityMap;
		this.sortedEntities = ReportingUtil.buildSortedEntities(entityRequest, entityMap);
		this.customFieldsNames = new LinkedHashSet<>();
	}

	public void open() throws IOException, ReportingFormatException {
		setFileWriter(new CSVWriter(new FileWriter(new File(getAbsolutePath()))));

		// write header
		List<String> headerFields = new ArrayList<String>();
		for (Entity entity : sortedEntities) {
			headerFields.add(entity.getDisplayNameM().get(entityRequest.getLocale()));
		}

		// write Work Custom Fields headers if they are enabled
		if(this.entityRequest.getHasWorkCustomFields()){
			headerFields.addAll(customFieldsNames);
		}
		writeRowToCSV(headerFields);
	}

	public void addRow(Map<String, GenericField> row) throws IOException {
		List<String> csvRow = formatRow(row);
		writeRowToCSV(csvRow);
	}

	public List<String> formatRow(Map<String, GenericField> row) {
		List<String> result = Lists.newArrayList();
		for (Entity entity : sortedEntities) {
			GenericField field = null;
			if (entity.getKeyName() != null)
				field = row.get(entity.getKeyName());

			if (field == null) continue;

			String formattedData = StringUtils.EMPTY;
			if (field.getValue() != null) {
				// TODO if there isn't a configured formatter, can use type within Entity to determine.....
				try {
					if (entity.getFormat() instanceof DateFormat) {
						GenericField genericFieldTimeZone = row.get(AbstractReportingQuery.TIME_ZONE_ID);
						DateFormat dateFormat = (DateFormat) entity.getFormat();
						formattedData = dateFormat.format(field, (String) genericFieldTimeZone.getValue());

					} else if ((entity.getFormat() instanceof TitleLinkFormat)
							|| (entity.getFormat() instanceof HyperTextFormat)
							|| (entity.getFormat() instanceof LastNameFormat)) {
						formattedData = (String) field.getValue();

					} else {
						formattedData = entity.getFormat().format(field);
					}

				} catch (Exception e) {
					logger.warn(e);
				}
			}
			result.add(formattedData);
		}

		// write Work Custom Fields value if they are enabled
		if (this.entityRequest.getHasWorkCustomFields() && customFieldsNames != null){
			for (String customField : customFieldsNames) {
				GenericField field = row.get(customField);

				if (field != null && field.getValue() != null) {
					result.add(field.getValue().toString());
				} else {
					result.add(StringUtils.EMPTY);
				}
			}
		}

		return result;
	}

	public ReportRequestData getEntityRequest() {
		return entityRequest;
	}

	public void setEntityRequest(ReportRequestData entityRequest) {
		this.entityRequest = entityRequest;
	}

	public List<Entity> getSortedEntities() {
		return sortedEntities;
	}

	public Set<String> getCustomFieldsNames() {
		return customFieldsNames;
	}

	public void setCustomFieldsNames(Set<String> customFieldsNames) {
		this.customFieldsNames = customFieldsNames;
	}

	public Map<String, Entity> getEntityMap() {
		return entityMap;
	}

	public void setEntityMap(Map<String, Entity> entityMap) {
		this.entityMap = entityMap;
	}
}

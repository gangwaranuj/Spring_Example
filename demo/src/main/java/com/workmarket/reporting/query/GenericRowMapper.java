package com.workmarket.reporting.query;

import com.workmarket.domains.model.reporting.*;
import com.workmarket.reporting.mapping.FilteringType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @since 08/1/2011
 * 
 */
public class GenericRowMapper extends AbstractReportingQuery implements RowMapper<Map<String, GenericField>> {

	/**
	 * @param reportingContext
	 * @param entityRequestForReport
	 */
	public GenericRowMapper(ReportingContext reportingContext, ReportRequestData entityRequestForReport) {
		super(reportingContext, entityRequestForReport);
	}

	/*
	 * Instance variables and constants
	 */
	private static final Log logger = LogFactory.getLog(GenericRowMapper.class);

	@Override
	public Map<String, GenericField> mapRow(ResultSet resultSet, int rowNum) throws SQLException {
		Map<String, GenericField> genericFieldM = new HashMap<String, GenericField>();

		WorkReportingContext workReportingContext = (WorkReportingContext) getReportingContext();

		if (getReportRequestData().getReportFilterL() != null) {
			for (ReportFilter entityRequestForFiltering : getReportRequestData().getReportFilterL()) {
				if (entityRequestForFiltering.getFilteringType().equals(FilteringType.DISPLAY)) {
					Entity entity = workReportingContext.getEntities().get(entityRequestForFiltering.getProperty());
					if (entity != null) {
						try {
							Object objectValue = getValue(entity.getFieldType(), entity.getDbFieldNameAlias(), resultSet);
							populateGenericField(entity, genericFieldM, objectValue);
							if (entity.getReferencedEntities() != null && entity.getReferencedEntities().size() > 0)
								populateReferenceEntities(entity, genericFieldM, resultSet);
						} catch (Exception e) {
							//logger.error(e.getMessage(), e);
						}
					}
				}
			}
		}
		
		try {
			GenericField genericFieldTimeZone = populateTimeZone(resultSet);
			genericFieldM.put(genericFieldTimeZone.getName(), genericFieldTimeZone);
			GenericField genericFieldWorkWorkId = populateWorkWorkID(resultSet);
			genericFieldM.put(genericFieldWorkWorkId.getName(), genericFieldWorkWorkId);
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
			// do nothing
		}

		return genericFieldM;
	}

	private void populateGenericField(Entity entity, Map<String, GenericField> genericFieldM, Object objectValue) {
		try {
			GenericField genericField = new GenericField(entity.getKeyName(), objectValue);
			genericField.setOrderBy(entity.getOrderBy());
			genericField.setRowOfGenericFields(genericFieldM);
			genericFieldM.put(entity.getKeyName(), genericField);
		} catch (Exception e) {
			//logger.error(e.getMessage(), e);
		}
	}

	private void populateReferenceEntities(Entity entity, Map<String, GenericField> genericFieldM, ResultSet resultSet) throws Exception {
		for (Entity referencedEntity : entity.getReferencedEntities()) {
			Object objectValue = getValue(referencedEntity.getFieldType(), referencedEntity.getDbFieldNameAlias(), resultSet);
			populateGenericField(referencedEntity, genericFieldM, objectValue);
		}		
	}

	protected GenericField populateWorkWorkID(ResultSet resultSet) throws Exception {
		return new GenericField(GenericQueryBuilderSqlImpl.WORK_WORK_ID,
				getValue("int(11)", GenericQueryBuilderSqlImpl.WORK_WORK_ID, resultSet));
	}
}

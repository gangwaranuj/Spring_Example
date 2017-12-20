/**
 * 
 */
package com.workmarket.domains.model.reporting;

/**
 * @since 8/10/2011
 *
 */
public class CustomFieldEntity extends Entity {


	public static final String WORK_CUSTOM_FIELDS = "workCustomFields";
	public static final String WORK_CUSTOM_FIELD_ID = "workCustomField.id";

	/**
	 * Instance variables and constants
	 */
	
	public Object clone(){
		CustomFieldEntity customFieldEntity = new CustomFieldEntity();
		//Fine with same instances... for these
		customFieldEntity.setAbstractFilter(getAbstractFilter());
		customFieldEntity.setDbFieldName(getDbFieldName());
		customFieldEntity.setDbFieldNameAlias(getDbFieldNameAlias());
		customFieldEntity.setDbTable(getDbTable());
		//customFieldEntity.setDisplayNameM(getDisplayNameM());
		customFieldEntity.setFieldType(getFieldType());
		customFieldEntity.setFilterInputTag(getFilterInputTag());
		customFieldEntity.setFormat(getFormat());
		customFieldEntity.setJoinClass(getJoinClass());
		customFieldEntity.setKeyName(getKeyName());
		customFieldEntity.setLocationOrder(getLocationOrder());
		customFieldEntity.setOrder(getOrder());
		customFieldEntity.setOrderBy(getOrderBy());
		customFieldEntity.setSqlJoin(getSqlJoin());
		customFieldEntity.setWhereClause(getWhereClause());
		return customFieldEntity;
	}
	
	private static final long serialVersionUID = 621147664915729890L;

}

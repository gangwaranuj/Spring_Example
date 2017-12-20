package com.workmarket.domains.model.reporting;

import com.workmarket.reporting.format.Format;
import com.workmarket.reporting.query.AbstractFilter;
import com.workmarket.reporting.query.CustomSql;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Entity extends Column implements Serializable {

	/**
	 * Instance variables and constants
	 */
	private String keyName;
	private FilterInputTag filterInputTag;
	private LocationOrder locationOrder;
	private Format format;
	private Map<Locale, String> displayNameM = new HashMap<>();//Internationalization, also no collection modifications
	private int order;//perhaps added to an other class that extends this
	private AbstractFilter abstractFilter;
	private String toolTip;
	private List<Entity> referencedEntities;
	private CustomSql customSql;

	//displayFilter....

	private static final long serialVersionUID = -2114869368447190289L;
	
	//Convenience method
	public Boolean isSqlJoin(){
		if(getSqlJoin() != null && getSqlJoin().size() > 0)
			return Boolean.TRUE;

		return Boolean.FALSE;
	}
	
	public String getKeyNameAlias(){
		return this.getKeyName().replaceAll(".", "_");
	}
	
	//Convenience method
	public Boolean hasWhereClause(){
		if(this.getWhereClause() != null && this.getWhereClause().length() > 9)
			return Boolean.TRUE;

		return Boolean.FALSE;
	}


	/**
	 * @return the displayNameM
	 */
	public Map<Locale, String> getDisplayNameM() {
		return displayNameM;
	}
	/**
	 * @param displayNameM the displayNameM to set
	 */
	public void setDisplayNameM(Map<Locale, String> displayNameM) {
		this.displayNameM = displayNameM;
	}
	
	/**
	 * Convenience method to obtain internationalized displayName
	 */
	public String getDisplayName(String key){
		return displayNameM.get(key);
	}

	/**
	 * @return the filterInputTag
	 */
	public FilterInputTag getFilterInputTag() {
		return filterInputTag;
	}
	/**
	 * @param filterInputTag the filterInputTag to set
	 */
	public void setFilterInputTag(FilterInputTag filterInputTag) {
		this.filterInputTag = filterInputTag;
	}
	/**
	 * @return the locationOrder
	 */
	public LocationOrder getLocationOrder() {
		return locationOrder;
	}
	/**
	 * @param locationOrder the locationOrder to set
	 */
	public void setLocationOrder(LocationOrder locationOrder) {
		this.locationOrder = locationOrder;
	}
	/**
	 * @return the keyName
	 */
	public String getKeyName() {
		return keyName;
	}
	/**
	 * @param keyName the keyName to set
	 */
	@Required
	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}
	/**
	 * @return the order
	 */
	public int getOrder() {
		return order;
	}
	/**
	 * @param order the order to set
	 */
	public void setOrder(int order) {
		this.order = order;
	}
	/**
	 * @return the format
	 */
	public Format getFormat() {
		return format;
	}
	/**
	 * @param format the format to set
	 */
	public void setFormat(Format format) {
		this.format = format;
	}

	public AbstractFilter getAbstractFilter() {
		return abstractFilter;
	}

	public void setAbstractFilter(AbstractFilter abstractFilter) {
		this.abstractFilter = abstractFilter;
	}

	/**
	 * @return
	 * Convenience method
	 */
	public String getFullKeyName(){
		if (StringUtils.isNotBlank(getDbTable())) {
			return getDbTable() + "." + getKeyName();
		}
		return getKeyName();
	}

	
	public String getFullColumnName() {
		String name = getDbFieldName();
		
		if (StringUtils.isNotBlank(getDbTable())) {
			name = String.format("%s.%s", getDbTable(), name);
		}
		
		if(StringUtils.isNotBlank(getDbFieldNameAlias())) {
			name = String.format("%s AS %s", name, getDbFieldNameAlias());
		}
		
		return name;
	}

	/**
	 * @return the toolTip
	 */
	public String getToolTip() {
		return toolTip;
	}

	/**
	 * @param toolTip the toolTip to set
	 */
	public void setToolTip(String toolTip) {
		this.toolTip = toolTip;
	}

	/**
	 * @return the referencedEntities
	 */
	public List<Entity> getReferencedEntities() {
		return referencedEntities;
	}

	/**
	 * @param referencedEntities the referencedEntities to set
	 */
	public void setReferencedEntities(List<Entity> referencedEntities) {
		this.referencedEntities = referencedEntities;
	}

	/**
	 * @return the customSql
	 */
	public CustomSql getCustomSql() {
		return customSql;
	}

	/**
	 * @param customSql the customSql to set
	 */
	public void setCustomSql(CustomSql customSql) {
		this.customSql = customSql;
	}

	
}
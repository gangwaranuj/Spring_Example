/**
 *
 */
package com.workmarket.domains.model.reporting;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.Map;

/**
 * @since 8/1/2011
 *
 */
public class GenericField implements Serializable {

	/**
	 * Instance variables and constants
	 */
	private String name;
	private Object value;
	private Integer orderBy;
	private Map<String, GenericField> rowOfGenericFields;

	private static final long serialVersionUID = 1741399942907570303L;

	public GenericField(String name, Object value) {
		this.name = name;
		this.value = value;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(Object value) {
		this.value = value;
	}
	/**
	 * @return the orderBy
	 */
	public Integer getOrderBy() {
		return orderBy;
	}
	/**
	 * @param orderBy the orderBy to set
	 */
	public void setOrderBy(Integer orderBy) {
		this.orderBy = orderBy;
	}
	/**
	 * @return the rowOfGenericFields
	 */
	public Map<String, GenericField> getRowOfGenericFields() {
		return rowOfGenericFields;
	}
	/**
	 * @param rowOfGenericFields the rowOfGenericFields to set
	 */
	public void setRowOfGenericFields(Map<String, GenericField> rowOfGenericFields) {
		this.rowOfGenericFields = rowOfGenericFields;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder("GenericField[");
		sb.append("name:" + name);
		sb.append(", value:" + value);
		sb.append(", orderBy:" + orderBy);
		sb.append("]");
		return sb.toString();
	}

	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (obj.getClass() != getClass())
			return false;
		GenericField that = (GenericField) obj;

		return EqualsBuilder.reflectionEquals(this, that);
	}

	public int hashcode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

}

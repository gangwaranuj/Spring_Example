package com.workmarket.domains.model.reporting;

import java.io.Serializable;
import java.util.List;

import com.workmarket.reporting.mapping.FilteringType;
import com.workmarket.reporting.mapping.HtmlTagType;
import com.workmarket.reporting.mapping.RelationalOperator;

public class FilterInputTag implements Serializable {

	/**
	 * Instance variables and constants
	 */
	private String name;
	private Boolean checked = Boolean.FALSE;
	private Boolean filterable = Boolean.FALSE;
	private Boolean future = Boolean.TRUE;
	private int size;
	private String value;
	private HtmlTagType htmlTagType;
	private RelationalOperator relationalOperator;
	private RelationalOperator relationalOperatorOptional;
	private FilteringType filteringType;
	private List<SelectOption> selectOptions;
	private List<FilteringType> filteringTypes;

	private static final long serialVersionUID = -8563829439552826319L;

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
	 * @return the checked
	 */
	public Boolean getChecked() {
		return checked;
	}

	/**
	 * @param checked the checked to set
	 */
	public void setChecked(Boolean checked) {
		this.checked = checked;
	}
	
	/**
	 * @return the filterable
	 */
	public Boolean getFilterable() {
		return filterable;
	}

	/**
	 * @param filterable the filterable to set
	 */
	public void setFilterable(Boolean filterable) {
		this.filterable = filterable;
	}

	/**
	 * @return the future
	 */
	public Boolean getFuture() {
		return future;
	}

	/**
	 * @param future the future to set
	 */
	public void setFuture(Boolean future) {
		this.future = future;
	}

	/**
	 * @return the size
	 */
	public int getSize() {
		return size;
	}

	/**
	 * @param size the size to set
	 */
	public void setSize(int size) {
		this.size = size;
	}

	/**
	 * @return
	 */
	public HtmlTagType getHtmlTagType() {
		return htmlTagType;
	}

	/**
	 * @param htmlTagType
	 */
	public void setHtmlTagType(HtmlTagType htmlTagType) {
		this.htmlTagType = htmlTagType;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the relationalOperator
	 */
	public RelationalOperator getRelationalOperator() {
		return relationalOperator;
	}

	/**
	 * @param relationalOperator the relationalOperator to set
	 */
	public void setRelationalOperator(RelationalOperator relationalOperator) {
		this.relationalOperator = relationalOperator;
	}

	/**
	 * @return the relationalOperatorOptional
	 */
	public RelationalOperator getRelationalOperatorOptional() {
		return relationalOperatorOptional;
	}

	/**
	 * @param relationalOperatorOptional the relationalOperatorOptional to set
	 */
	public void setRelationalOperatorOptional(
			RelationalOperator relationalOperatorOptional) {
		this.relationalOperatorOptional = relationalOperatorOptional;
	}

	/**
	 * @return the filteringTypes
	 */
	public List<FilteringType> getFilteringTypes() {
		return filteringTypes;
	}

	/**
	 * @param filteringTypes the filteringTypes to set
	 */
	public void setFilteringTypes(List<FilteringType> filteringTypes) {
		this.filteringTypes = filteringTypes;
	}

	/**
	 * @return the filteringType
	 */
	public FilteringType getFilteringType() {
		return filteringType;
	}

	/**
	 * @param filteringType the filteringType to set
	 */
	public void setFilteringType(FilteringType filteringType) {
		this.filteringType = filteringType;
	}

	/**
	 * @return the selectOptions
	 */
	public List<SelectOption> getSelectOptions() {
		return selectOptions;
	}

	/**
	 * @param selectOptions the selectOptions to set
	 */
	public void setSelectOptions(List<SelectOption> selectOptions) {
		this.selectOptions = selectOptions;
	}

}

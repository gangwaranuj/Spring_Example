package com.workmarket.domains.model.reporting;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;

public class EntityBucket implements Serializable {

	/**
	 * Instance variables and constants
	 */
	private Map<Locale, String> displayNameM = new HashMap<Locale, String>();
	//private Locale locale;
	//private Map<String, Entity> entities = new ConcurrentSkipListMap<String, Entity>();
	private List<Entity> entities = new ArrayList<Entity>();
	private LocationOrder locationOrder;
	private String table;
	private String keyName;

	private static final long serialVersionUID = -2755616685155704664L;

	/**
	 * @return the displayName
	 */
	public Map<Locale, String> getDisplayNameM() {
		return displayNameM;
	}

	/**
	 * @param displayName the displayName to set
	 */
	public void setDisplayNameM(Map<Locale, String> displayNameM) {
		this.displayNameM = displayNameM;
	}

	/**
	 * @return the entities
	 */
	public List<Entity> getEntities() {
		return entities;
	}

	/**
	 * @param entities the entities to set
	 */
	@Required
	public void setEntities(List<Entity> entities) {
		this.entities = entities;
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
	@Required
	public void setLocationOrder(LocationOrder locationOrder) {
		this.locationOrder = locationOrder;
	}

	/**
	 * @return the table
	 */
	public String getTable() {
		return table;
	}

	/**
	 * @param table the table to set
	 */
	public void setTable(String table) {
		this.table = table;
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
	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}

}

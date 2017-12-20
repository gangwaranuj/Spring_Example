package com.workmarket.domains.model.reporting;

import org.springframework.beans.factory.annotation.Required;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ReportingContext implements Serializable {

	/**
	 * Instance variables and constants
	 */
	private List<EntityBucket> entityBuckets;
	private ConcurrentMap<String, Entity> entityBucketsM;
	private FilterBucket filterBucket;
	private Long buyerId;
	private Long companyId;
	private Integer sortOrder;
	private Map<Locale, String> displayNameM = new HashMap<Locale, String>();
	private static final long serialVersionUID = 1173313762869609089L;

	/**
	 * @return
	 */
	public List<EntityBucket> getEntityBuckets() {
		return entityBuckets;
	}

	/**
	 * @param entityBuckets
	 */
	@Required
	public void setEntityBuckets(List<EntityBucket> entityBuckets) {
		this.entityBuckets = entityBuckets;
	}

	/**
	 * @return the filterBucket
	 */
	public FilterBucket getFilterBucket() {
		return filterBucket;
	}

	/**
	 * @param filterBucket the filterBucket to set
	 */
	public void setFilterBucket(FilterBucket filterBucket) {
		this.filterBucket = filterBucket;
	}

	public Long getBuyerId() {
		return buyerId;
	}

	public void setBuyerId(Long buyerId) {
		this.buyerId = buyerId;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public Map<String, Entity> getEntities() {
		if(entityBucketsM == null && entityBuckets != null){
			entityBucketsM = new ConcurrentHashMap<String, Entity>();
			for (EntityBucket entityBucket : entityBuckets) {
				if (entityBucket.getEntities() != null) {
					for (Entity entity : entityBucket.getEntities()) {
						entityBucketsM.put(entity.getFullKeyName(), entity);
					}
				}
			}
		}
		
		return entityBucketsM;
	}

	public void setEntityBucketsM(ConcurrentMap<String, Entity> entityBucketsM) {
		this.entityBucketsM = entityBucketsM;
	}

	public Integer getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}

	public Map<Locale, String> getDisplayNameM() {
		return displayNameM;
	}

	public void setDisplayNameM(Map<Locale, String> displayNameM) {
		this.displayNameM = displayNameM;
	}

	public String toString(){
		StringBuilder sb = new StringBuilder("ReportingContext[");
		if(this.getDisplayNameM() != null)
			sb.append("en_displayName:").append(getDisplayNameM().get(new Locale("en")));
		sb.append(", sortOrder:").append(getSortOrder());
		sb.append("]");
		return sb.toString();
	}
	
}

package com.workmarket.service.business.dto;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.util.Assert;

import com.google.common.collect.Lists;
import com.workmarket.utility.SearchUtilities;
import com.workmarket.utility.StringUtilities;

public class GroupSearchFilterDTO {
	private String keywords;
	private Long userId;
	private Boolean backgroundCheckedFlag;
	private Long companyId;
	private Boolean availableToJoinOnly = Boolean.FALSE;
	private String objectiveType;
	private List<Long> companyIdsExclusiveToUser;
	private List<Long> blockedByCompanyIds;
	private FacetFieldContainer industries = new FacetFieldContainer("industryIds");

	public FacetFieldContainer getIndustries() {
		return industries;
	}

	public void setIndustries(FacetFieldContainer industries) {
		this.industries = industries;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId.longValue();
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Boolean getBackgroundCheckedFlag() {
		return backgroundCheckedFlag;
	}

	public void setBackgroundCheckedFlag(Boolean backgroundCheckedFlag) {
		this.backgroundCheckedFlag = backgroundCheckedFlag;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public Boolean getAvailableToJoinOnly() {
		return availableToJoinOnly;
	}

	public void setAvailableToJoinOnly(Boolean availableToJoinOnly) {
		this.availableToJoinOnly = availableToJoinOnly;
	}

	public String getObjectiveType() {
		return objectiveType;
	}

	public void setObjectiveType(String objectiveType) {
		this.objectiveType = objectiveType;
	}

	public List<Long> getCompanyIdsExclusiveToUser() { return companyIdsExclusiveToUser; }

	public void setCompanyIdsExclusiveToUser(List<Long> companyIdsExclusiveToUser) { this.companyIdsExclusiveToUser = companyIdsExclusiveToUser; }

	public List<Long> getBlockedByCompanyIds() {
		return blockedByCompanyIds;
	}

	public void setBlockedByCompanyIds(List<Long> blockedByCompanyIds) {
		this.blockedByCompanyIds = blockedByCompanyIds;
	}

	public static class FacetFieldContainer {
		private String field;
		private List<FacetFieldFilter> facetFieldFilters = Lists.newArrayList();
		private Boolean showAll = Boolean.TRUE;

		public FacetFieldContainer() {
		}

		public FacetFieldContainer(String field) {
			this.field = field;
		}

		public void sortLabels() {
			Collections.sort(facetFieldFilters);
		}

		public Integer getAllCount() {
			Integer count = 0;
			for (FacetFieldFilter filter : facetFieldFilters)
				count += filter.getCount();
			return count;
		}

		public String getField() {
			return field;
		}

		public void setField(String field) {
			this.field = field;
		}

		public List<FacetFieldFilter> getFacetFieldFilters() {
			return facetFieldFilters;
		}

		public void setFacetFieldFilters(List<FacetFieldFilter> facetFieldFilters) {
			this.facetFieldFilters = facetFieldFilters;
		}

		public Boolean getShowAll() {
			return showAll;
		}

		public void setShowAll(Boolean showAll) {
			if (showAll) {
				for (FacetFieldFilter filter : facetFieldFilters)
					filter.setFilterOn(false);
			}
			this.showAll = showAll;
		}

		public Integer[] getIds() {
			Integer[] ids = new Integer[facetFieldFilters.size()];
			for (int i = 0; i < facetFieldFilters.size(); i++)
				ids[i] = StringUtilities.parseInteger(facetFieldFilters.get(i).getId());
			return ids;
		}

		public void setIds(String[] ids) {
			Assert.notNull(ids);
			Assert.noNullElements(ids);
			showAll = false;
			List<String> newIds = Lists.newArrayList(ids);
			for (FacetFieldFilter filter : facetFieldFilters) {
				if (filter.getId() != null) {
					if (ArrayUtils.contains(ids, filter.getId())) {
						filter.setFilterOn(true);
					} else {
						filter.setFilterOn(false);
					}
					newIds.remove(null);
					newIds.remove(filter.getId());
				}
			}
			for (String newId : newIds) {
				FacetFieldFilter facetFieldFilter = new FacetFieldFilter(this, "no label", this.getField(), newId, Long.valueOf(0)
						.intValue(), true);
				facetFieldFilter.setFilterOn(true);
				this.getFacetFieldFilters().add(facetFieldFilter);
			}
		}

		public String[] getLabels() {
			String[] ids = new String[facetFieldFilters.size()];
			for (int i = 0; i < facetFieldFilters.size(); i++)
				ids[i] = facetFieldFilters.get(i).getLabel();
			return ids;
		}

		public void setIds(Integer[] ids) {
			Assert.notNull(ids);
			Assert.noNullElements(ids);
			showAll = false;
			List<Integer> newIds = Lists.newArrayList(ids);
			for (FacetFieldFilter filter : facetFieldFilters) {
				if (filter.getId() != null) {
					if (ArrayUtils.contains(ids, filter.getIdAsInteger())) {
						filter.setFilterOn(true);
					} else {
						filter.setFilterOn(false);
					}
					newIds.remove(null);
					newIds.remove(filter.getIdAsInteger());
				}
			}
			for (Integer newId : newIds) {
				FacetFieldFilter facetFieldFilter = new FacetFieldFilter(this, "no label", this.getField(), newId.toString(), Long.valueOf(
						0).intValue(), true);
				facetFieldFilter.setFilterOn(true);
				this.getFacetFieldFilters().add(facetFieldFilter);
			}
		}

		public void addFacetFieldFilter(Long id, String label, boolean filterOn) {
			for (FacetFieldFilter filter : getFacetFieldFilters())
				if (("" + id).equals(filter.getId()))
					return;
			FacetFieldFilter facetFieldFilter = new FacetFieldFilter(this, label, getField(), "" + id, null, true);
			facetFieldFilter.setFilterOn(filterOn);
			getFacetFieldFilters().add(facetFieldFilter);
			if (filterOn)
				showAll = false;
		}

		public String newFilterQuery() {
			String query = "";
			List<String> filters = Lists.newArrayList();

			for (FacetFieldFilter filter : getFacetFieldFilters()) {
				if (filter.getFilterOn())
					filters.add(filter.getQuery());
			}
			return "{!tag=" + getField() + "}" + SearchUtilities.joinWithOR(filters);
		}

		public String newFilterQuery(Long companyId) {
			String query = "";
			List<String> filters = Lists.newArrayList();

			for (FacetFieldFilter filter : getFacetFieldFilters()) {
				if (filter.getFilterOn())
					filters.add(companyId + "_" + filter.getQuery());
			}
			return "{!tag=" + getField() + "}" + SearchUtilities.joinWithOR(filters);
		}
	}

	public static class FacetFieldFilter implements Comparable {
		private FacetFieldContainer facetFieldContainer;
		private String label;
		private String field;
		private String id;
		private Integer count;
		private Boolean enabled = Boolean.TRUE;
		private Boolean filterOn = Boolean.FALSE;

		public FacetFieldFilter() {
		}

		public FacetFieldFilter(FacetFieldContainer facetFieldContainer, String label, String field, String id, Integer count,
		                        Boolean enabled) {
			this.facetFieldContainer = facetFieldContainer;
			this.label = label;
			this.field = field;
			this.id = id;
			this.count = count;
			this.enabled = enabled;
		}

		public String getField() {
			return field;
		}

		public void setField(String field) {
			this.field = field;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public Integer getCount() {
			return count;
		}

		public void setCount(Integer count) {
			this.count = count;
		}

		public Boolean getEnabled() {
			return enabled;
		}

		public void setEnabled(Boolean enabled) {
			this.enabled = enabled;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public Integer getIdAsInteger() {
			return StringUtilities.parseInteger(id);
		}

		public String getQuery() {
			return field + ":" + getId();
		}

		public Boolean getFilterOn() {
			return filterOn;
		}

		public void setFilterOn(Boolean filterOn) {
			if (filterOn)
				facetFieldContainer.setShowAll(false);
			this.filterOn = filterOn;
		}


		@Override
		public int hashCode() {
			return getId().hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			FacetFieldFilter fff = (FacetFieldFilter) obj;
			return getId().equals(fff.getId());
		}

		@Override
		public String toString() {
			return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE).append(id, getId()).append("count", getCount())
					.append("filterOn", getFilterOn()).toString();
		}

		@Override
		public int compareTo(Object o) {
			FacetFieldFilter f = (FacetFieldFilter) o;

			int compare = this.getLabel().compareTo(f.getLabel());
			if (compare == 0)
				return this.getId().compareTo(f.getId());
			return compare;
		}
	}


}

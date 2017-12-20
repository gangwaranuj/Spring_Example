package com.workmarket.thrift.assessment;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Assessment implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;
	private String name;
	private String description;
	private AssessmentType type;
	private com.workmarket.thrift.core.Status status;
	private int approximateDurationMinutes;
	private AssessmentOptions configuration;
	private List<com.workmarket.thrift.core.Skill> skills;
	private List<Item> items;
	private com.workmarket.thrift.core.Industry industry;
	private com.workmarket.thrift.core.Company company;
	private com.workmarket.thrift.core.User createdBy;
	private long createdOn;
	private long modifiedOn;
	private int numberOfGradedItems;
	private int numberOfManuallyGradedItems;
	private AssessmentStatistics statistics;
	private boolean isRequired;
	private boolean hasAssetItems;

	public Assessment() {
	}

	public long getId() {
		return this.id;
	}

	public Assessment setId(long id) {
		this.id = id;
		return this;
	}

	public boolean isSetId() {
		return (id > 0L);
	}

	public String getName() {
		return this.name;
	}

	public Assessment setName(String name) {
		this.name = name;
		return this;
	}

	public boolean isSetName() {
		return this.name != null;
	}

	public String getDescription() {
		return this.description;
	}

	public Assessment setDescription(String description) {
		this.description = description;
		return this;
	}

	public boolean isSetDescription() {
		return this.description != null;
	}

	public AssessmentType getType() {
		return this.type;
	}

	public Assessment setType(AssessmentType type) {
		this.type = type;
		return this;
	}

	public boolean isSetType() {
		return this.type != null;
	}

	public com.workmarket.thrift.core.Status getStatus() {
		return this.status;
	}

	public Assessment setStatus(com.workmarket.thrift.core.Status status) {
		this.status = status;
		return this;
	}

	public boolean isSetStatus() {
		return this.status != null;
	}

	public int getApproximateDurationMinutes() {
		return this.approximateDurationMinutes;
	}

	public Assessment setApproximateDurationMinutes(int approximateDurationMinutes) {
		this.approximateDurationMinutes = approximateDurationMinutes;
		return this;
	}

	public boolean isSetApproximateDurationMinutes() {
		return (approximateDurationMinutes > 0);
	}

	public AssessmentOptions getConfiguration() {
		return this.configuration;
	}

	public Assessment setConfiguration(AssessmentOptions configuration) {
		this.configuration = configuration;
		return this;
	}

	public boolean isSetConfiguration() {
		return this.configuration != null;
	}

	public int getSkillsSize() {
		return (this.skills == null) ? 0 : this.skills.size();
	}

	public java.util.Iterator<com.workmarket.thrift.core.Skill> getSkillsIterator() {
		return (this.skills == null) ? null : this.skills.iterator();
	}

	public void addToSkills(com.workmarket.thrift.core.Skill elem) {
		if (this.skills == null) {
			this.skills = new ArrayList<com.workmarket.thrift.core.Skill>();
		}
		this.skills.add(elem);
	}

	public List<com.workmarket.thrift.core.Skill> getSkills() {
		return this.skills;
	}

	public Assessment setSkills(List<com.workmarket.thrift.core.Skill> skills) {
		this.skills = skills;
		return this;
	}

	public boolean isSetSkills() {
		return this.skills != null;
	}

	public int getItemsSize() {
		return (this.items == null) ? 0 : this.items.size();
	}

	public java.util.Iterator<Item> getItemsIterator() {
		return (this.items == null) ? null : this.items.iterator();
	}

	public void addToItems(Item elem) {
		if (this.items == null) {
			this.items = new ArrayList<Item>();
		}
		this.items.add(elem);
	}

	public List<Item> getItems() {
		return this.items;
	}

	public Assessment setItems(List<Item> items) {
		this.items = items;
		return this;
	}

	public boolean isSetItems() {
		return this.items != null;
	}

	public com.workmarket.thrift.core.Industry getIndustry() {
		return this.industry;
	}

	public Assessment setIndustry(com.workmarket.thrift.core.Industry industry) {
		this.industry = industry;
		return this;
	}

	public boolean isSetIndustry() {
		return this.industry != null;
	}

	public com.workmarket.thrift.core.Company getCompany() {
		return this.company;
	}

	public Assessment setCompany(com.workmarket.thrift.core.Company company) {
		this.company = company;
		return this;
	}

	public boolean isSetCompany() {
		return this.company != null;
	}

	public com.workmarket.thrift.core.User getCreatedBy() {
		return this.createdBy;
	}

	public Assessment setCreatedBy(com.workmarket.thrift.core.User createdBy) {
		this.createdBy = createdBy;
		return this;
	}

	public boolean isSetCreatedBy() {
		return this.createdBy != null;
	}

	public long getCreatedOn() {
		return this.createdOn;
	}

	public Assessment setCreatedOn(long createdOn) {
		this.createdOn = createdOn;
		return this;
	}

	public boolean isSetCreatedOn() {
		return (createdOn > 0L);
	}

	public long getModifiedOn() {
		return this.modifiedOn;
	}

	public Assessment setModifiedOn(long modifiedOn) {
		this.modifiedOn = modifiedOn;
		return this;
	}

	public boolean isSetModifiedOn() {
		return (modifiedOn > 0L);
	}

	public int getNumberOfGradedItems() {
		return this.numberOfGradedItems;
	}

	public Assessment setNumberOfGradedItems(int numberOfGradedItems) {
		this.numberOfGradedItems = numberOfGradedItems;
		return this;
	}

	public boolean isSetNumberOfGradedItems() {
		return (numberOfGradedItems > 0);
	}

	public int getNumberOfManuallyGradedItems() {
		return this.numberOfManuallyGradedItems;
	}

	public Assessment setNumberOfManuallyGradedItems(int numberOfManuallyGradedItems) {
		this.numberOfManuallyGradedItems = numberOfManuallyGradedItems;
		return this;
	}

	public boolean isSetNumberOfManuallyGradedItems() {
		return (numberOfManuallyGradedItems > 0);
	}

	public AssessmentStatistics getStatistics() {
		return this.statistics;
	}

	public Assessment setStatistics(AssessmentStatistics statistics) {
		this.statistics = statistics;
		return this;
	}

	public boolean isSetStatistics() {
		return this.statistics != null;
	}

	public boolean isIsRequired() {
		return this.isRequired;
	}

	public Assessment setIsRequired(boolean isRequired) {
		this.isRequired = isRequired;
		return this;
	}

	public boolean isHasAssetItems() {
		return this.hasAssetItems;
	}

	public Assessment setHasAssetItems(boolean hasAssetItems) {
		this.hasAssetItems = hasAssetItems;
		return this;
	}

	public boolean isInvitationOnly() {
		return configuration != null && !configuration.isFeatured();
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof Assessment)
			return this.equals((Assessment) that);
		return false;
	}

	private boolean equals(Assessment that) {
		if (that == null)
			return false;

		boolean this_present_id = true;
		boolean that_present_id = true;
		if (this_present_id || that_present_id) {
			if (!(this_present_id && that_present_id))
				return false;
			if (this.id != that.id)
				return false;
		}

		boolean this_present_name = true && this.isSetName();
		boolean that_present_name = true && that.isSetName();
		if (this_present_name || that_present_name) {
			if (!(this_present_name && that_present_name))
				return false;
			if (!this.name.equals(that.name))
				return false;
		}

		boolean this_present_description = true && this.isSetDescription();
		boolean that_present_description = true && that.isSetDescription();
		if (this_present_description || that_present_description) {
			if (!(this_present_description && that_present_description))
				return false;
			if (!this.description.equals(that.description))
				return false;
		}

		boolean this_present_type = true && this.isSetType();
		boolean that_present_type = true && that.isSetType();
		if (this_present_type || that_present_type) {
			if (!(this_present_type && that_present_type))
				return false;
			if (!this.type.equals(that.type))
				return false;
		}

		boolean this_present_status = true && this.isSetStatus();
		boolean that_present_status = true && that.isSetStatus();
		if (this_present_status || that_present_status) {
			if (!(this_present_status && that_present_status))
				return false;
			if (!this.status.equals(that.status))
				return false;
		}

		boolean this_present_approximateDurationMinutes = true;
		boolean that_present_approximateDurationMinutes = true;
		if (this_present_approximateDurationMinutes || that_present_approximateDurationMinutes) {
			if (!(this_present_approximateDurationMinutes && that_present_approximateDurationMinutes))
				return false;
			if (this.approximateDurationMinutes != that.approximateDurationMinutes)
				return false;
		}

		boolean this_present_configuration = true && this.isSetConfiguration();
		boolean that_present_configuration = true && that.isSetConfiguration();
		if (this_present_configuration || that_present_configuration) {
			if (!(this_present_configuration && that_present_configuration))
				return false;
			if (!this.configuration.equals(that.configuration))
				return false;
		}

		boolean this_present_skills = true && this.isSetSkills();
		boolean that_present_skills = true && that.isSetSkills();
		if (this_present_skills || that_present_skills) {
			if (!(this_present_skills && that_present_skills))
				return false;
			if (!this.skills.equals(that.skills))
				return false;
		}

		boolean this_present_items = true && this.isSetItems();
		boolean that_present_items = true && that.isSetItems();
		if (this_present_items || that_present_items) {
			if (!(this_present_items && that_present_items))
				return false;
			if (!this.items.equals(that.items))
				return false;
		}

		boolean this_present_industry = true && this.isSetIndustry();
		boolean that_present_industry = true && that.isSetIndustry();
		if (this_present_industry || that_present_industry) {
			if (!(this_present_industry && that_present_industry))
				return false;
			if (!this.industry.equals(that.industry))
				return false;
		}

		boolean this_present_company = true && this.isSetCompany();
		boolean that_present_company = true && that.isSetCompany();
		if (this_present_company || that_present_company) {
			if (!(this_present_company && that_present_company))
				return false;
			if (!this.company.equals(that.company))
				return false;
		}

		boolean this_present_createdBy = true && this.isSetCreatedBy();
		boolean that_present_createdBy = true && that.isSetCreatedBy();
		if (this_present_createdBy || that_present_createdBy) {
			if (!(this_present_createdBy && that_present_createdBy))
				return false;
			if (!this.createdBy.equals(that.createdBy))
				return false;
		}

		boolean this_present_createdOn = true;
		boolean that_present_createdOn = true;
		if (this_present_createdOn || that_present_createdOn) {
			if (!(this_present_createdOn && that_present_createdOn))
				return false;
			if (this.createdOn != that.createdOn)
				return false;
		}

		boolean this_present_modifiedOn = true;
		boolean that_present_modifiedOn = true;
		if (this_present_modifiedOn || that_present_modifiedOn) {
			if (!(this_present_modifiedOn && that_present_modifiedOn))
				return false;
			if (this.modifiedOn != that.modifiedOn)
				return false;
		}

		boolean this_present_numberOfGradedItems = true;
		boolean that_present_numberOfGradedItems = true;
		if (this_present_numberOfGradedItems || that_present_numberOfGradedItems) {
			if (!(this_present_numberOfGradedItems && that_present_numberOfGradedItems))
				return false;
			if (this.numberOfGradedItems != that.numberOfGradedItems)
				return false;
		}

		boolean this_present_numberOfManuallyGradedItems = true;
		boolean that_present_numberOfManuallyGradedItems = true;
		if (this_present_numberOfManuallyGradedItems || that_present_numberOfManuallyGradedItems) {
			if (!(this_present_numberOfManuallyGradedItems && that_present_numberOfManuallyGradedItems))
				return false;
			if (this.numberOfManuallyGradedItems != that.numberOfManuallyGradedItems)
				return false;
		}

		boolean this_present_statistics = true && this.isSetStatistics();
		boolean that_present_statistics = true && that.isSetStatistics();
		if (this_present_statistics || that_present_statistics) {
			if (!(this_present_statistics && that_present_statistics))
				return false;
			if (!this.statistics.equals(that.statistics))
				return false;
		}

		boolean this_present_isRequired = true;
		boolean that_present_isRequired = true;
		if (this_present_isRequired || that_present_isRequired) {
			if (!(this_present_isRequired && that_present_isRequired))
				return false;
			if (this.isRequired != that.isRequired)
				return false;
		}

		boolean this_present_hasAssetItems = true;
		boolean that_present_hasAssetItems = true;
		if (this_present_hasAssetItems || that_present_hasAssetItems) {
			if (!(this_present_hasAssetItems && that_present_hasAssetItems))
				return false;
			if (this.hasAssetItems != that.hasAssetItems)
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_id = true;
		builder.append(present_id);
		if (present_id)
			builder.append(id);

		boolean present_name = true && (isSetName());
		builder.append(present_name);
		if (present_name)
			builder.append(name);

		boolean present_description = true && (isSetDescription());
		builder.append(present_description);
		if (present_description)
			builder.append(description);

		boolean present_type = true && (isSetType());
		builder.append(present_type);
		if (present_type)
			builder.append(type.getValue());

		boolean present_status = true && (isSetStatus());
		builder.append(present_status);
		if (present_status)
			builder.append(status);

		boolean present_approximateDurationMinutes = true;
		builder.append(present_approximateDurationMinutes);
		if (present_approximateDurationMinutes)
			builder.append(approximateDurationMinutes);

		boolean present_configuration = true && (isSetConfiguration());
		builder.append(present_configuration);
		if (present_configuration)
			builder.append(configuration);

		boolean present_skills = true && (isSetSkills());
		builder.append(present_skills);
		if (present_skills)
			builder.append(skills);

		boolean present_items = true && (isSetItems());
		builder.append(present_items);
		if (present_items)
			builder.append(items);

		boolean present_industry = true && (isSetIndustry());
		builder.append(present_industry);
		if (present_industry)
			builder.append(industry);

		boolean present_company = true && (isSetCompany());
		builder.append(present_company);
		if (present_company)
			builder.append(company);

		boolean present_createdBy = true && (isSetCreatedBy());
		builder.append(present_createdBy);
		if (present_createdBy)
			builder.append(createdBy);

		boolean present_createdOn = true;
		builder.append(present_createdOn);
		if (present_createdOn)
			builder.append(createdOn);

		boolean present_modifiedOn = true;
		builder.append(present_modifiedOn);
		if (present_modifiedOn)
			builder.append(modifiedOn);

		boolean present_numberOfGradedItems = true;
		builder.append(present_numberOfGradedItems);
		if (present_numberOfGradedItems)
			builder.append(numberOfGradedItems);

		boolean present_numberOfManuallyGradedItems = true;
		builder.append(present_numberOfManuallyGradedItems);
		if (present_numberOfManuallyGradedItems)
			builder.append(numberOfManuallyGradedItems);

		boolean present_statistics = true && (isSetStatistics());
		builder.append(present_statistics);
		if (present_statistics)
			builder.append(statistics);

		boolean present_isRequired = true;
		builder.append(present_isRequired);
		if (present_isRequired)
			builder.append(isRequired);

		boolean present_hasAssetItems = true;
		builder.append(present_hasAssetItems);
		if (present_hasAssetItems)
			builder.append(hasAssetItems);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Assessment(");
		boolean first = true;

		sb.append("id:");
		sb.append(this.id);
		first = false;
		if (!first) sb.append(", ");
		sb.append("name:");
		if (this.name == null) {
			sb.append("null");
		} else {
			sb.append(this.name);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("description:");
		if (this.description == null) {
			sb.append("null");
		} else {
			sb.append(this.description);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("type:");
		if (this.type == null) {
			sb.append("null");
		} else {
			sb.append(this.type);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("status:");
		if (this.status == null) {
			sb.append("null");
		} else {
			sb.append(this.status);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("approximateDurationMinutes:");
		sb.append(this.approximateDurationMinutes);
		first = false;
		if (!first) sb.append(", ");
		sb.append("configuration:");
		if (this.configuration == null) {
			sb.append("null");
		} else {
			sb.append(this.configuration);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("skills:");
		if (this.skills == null) {
			sb.append("null");
		} else {
			sb.append(this.skills);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("items:");
		if (this.items == null) {
			sb.append("null");
		} else {
			sb.append(this.items);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("industry:");
		if (this.industry == null) {
			sb.append("null");
		} else {
			sb.append(this.industry);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("company:");
		if (this.company == null) {
			sb.append("null");
		} else {
			sb.append(this.company);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("createdBy:");
		if (this.createdBy == null) {
			sb.append("null");
		} else {
			sb.append(this.createdBy);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("createdOn:");
		sb.append(this.createdOn);
		first = false;
		if (!first) sb.append(", ");
		sb.append("modifiedOn:");
		sb.append(this.modifiedOn);
		first = false;
		if (!first) sb.append(", ");
		sb.append("numberOfGradedItems:");
		sb.append(this.numberOfGradedItems);
		first = false;
		if (!first) sb.append(", ");
		sb.append("numberOfManuallyGradedItems:");
		sb.append(this.numberOfManuallyGradedItems);
		first = false;
		if (!first) sb.append(", ");
		sb.append("statistics:");
		if (this.statistics == null) {
			sb.append("null");
		} else {
			sb.append(this.statistics);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("isRequired:");
		sb.append(this.isRequired);
		first = false;
		if (!first) sb.append(", ");
		sb.append("hasAssetItems:");
		sb.append(this.hasAssetItems);
		first = false;
		sb.append(")");
		return sb.toString();
	}
}


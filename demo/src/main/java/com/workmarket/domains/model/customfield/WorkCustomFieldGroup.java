package com.workmarket.domains.model.customfield;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.DeletableEntity;
import com.workmarket.domains.model.audit.AuditChanges;
import org.springframework.beans.BeanUtils;

import javax.persistence.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.google.common.collect.Iterables.filter;
import static org.apache.commons.lang3.BooleanUtils.isFalse;

@Entity(name="work_custom_field_group")
@Table(name="work_custom_field_group")
@NamedQueries({
	@NamedQuery(name="work_custom_field_group.byCompany", query="from work_custom_field_group where company.id = :companyId AND deleted = false order by name asc"),
	@NamedQuery(name="inactive_work_custom_field_group.byCompany", query="from work_custom_field_group where company.id = :companyId AND active = false AND deleted = false order by name asc"),
	@NamedQuery(name="active_work_custom_field_group.byCompany", query="from work_custom_field_group where company.id = :companyId AND active = true AND deleted = false order by name asc")
})
@AuditChanges
public class WorkCustomFieldGroup extends DeletableEntity {

	public static final int MAX_NAME_LENGTH = 256;
	private static final long serialVersionUID = 1L;

	private String name;
	private List<WorkCustomField> workCustomFields;
	private Boolean required = false;
	private Boolean active = true;
	private Company company;

	public static WorkCustomFieldGroup copy(WorkCustomFieldGroup fromGroup) {
		WorkCustomFieldGroup newGroup = new WorkCustomFieldGroup();
		// copy everything except associations
		BeanUtils.copyProperties(fromGroup, newGroup, new String[] {
			"id", "createdOn", "creatorId", "modifiedOn", "modifier", "deleted", "workCustomFields"
		});
		for (WorkCustomField field : fromGroup.getWorkCustomFields()) {
			newGroup.addWorkCustomField(WorkCustomField.copy(field));
		}
		return newGroup;
	}

	@ManyToOne(optional = false, cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name="company_id", nullable = false)
	public Company getCompany() {
		return company;
	}
	public void setCompany(Company company) {
		this.company = company;
	}

	@Column(name="name", nullable=false)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}


	/* NOTE THAT THIS WILL INCLUDE DELETED ENTRIES */
	/* USE WorkCustomFieldDAO.findAllFieldsForCustomFieldGroup(Long customFieldGroupId) */
	/* OR WorkService.findRequiredWorkCustomFieldGroup(Long companyId) INSTEAD
	*  OR use getActiveWorkCustomFields() below */
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "work_custom_field_group_id", nullable = false)
	public List<WorkCustomField> getWorkCustomFields() {
		return Ordering
				.from(new Comparator<WorkCustomField>() {
					@Override
					public int compare(final WorkCustomField o1, final WorkCustomField o2) {
						return o1.compareTo(o2);
					}
				})
				.immutableSortedCopy(workCustomFields);
	}
	public void setWorkCustomFields(List<WorkCustomField> workCustomFields) {
		this.workCustomFields = workCustomFields;
	}

	public void addWorkCustomField(WorkCustomField workCustomField) {
		if (workCustomFields == null) {
			workCustomFields = Lists.newArrayList();
		}
		workCustomFields.add(workCustomField);
	}

	@Column(name="required", nullable=false)
	public Boolean isRequired() {
		return required;
	}
	public Boolean getRequired() {
		return required;
	}
	public void setRequired(Boolean required) {
		this.required = required;
	}

	@Column(name="active", nullable=false)
	public Boolean isActive() {
		return active;
	}
	public Boolean getActive() {
		return active;
	}
	public void setActive(Boolean active) {
		this.active = active;
	}

	// this filters out deleted fields
	@Transient
	public List<WorkCustomField> getActiveWorkCustomFields() {
		return Lists.newArrayList(filter(getWorkCustomFields(), new Predicate<WorkCustomField>() {
			@Override public boolean apply(WorkCustomField workCustomField) {
				return isFalse(workCustomField.getDeleted());
			}
		}));
	}
}

package com.workmarket.domains.model.customfield;

import com.workmarket.domains.model.DeletableEntity;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@AuditChanges
@Entity(name = "workCustomFieldGroupAssociation")
@Table(name = "work_custom_field_group_association")
@NamedQueries({
		@NamedQuery(name = "workCustomFieldGroupAssociation.byWorkAndWorkCustomFieldGroup", query = "from workCustomFieldGroupAssociation where work.id = :work_id and workCustomFieldGroup.id = :work_custom_field_group_id"),
		@NamedQuery(name = "workCustomFieldGroupAssociation.byWorkAndWorkCustomFieldGroupPosition", query = "from workCustomFieldGroupAssociation as a where a.work.id = :work_id and a.position = :position and a.deleted = false"),
		@NamedQuery(name = "workCustomFieldGroupAssociation.byWork", query = "from workCustomFieldGroupAssociation as a inner join fetch a.workCustomFieldGroup as workCustomFieldGroup" +
				"  where a.work.id = :work_id and a.deleted = false and workCustomFieldGroup.deleted = false order by a.position asc"),
		@NamedQuery(name = "workCustomFieldGroupAssociation.findAllByWork", query = "from workCustomFieldGroupAssociation as a inner join fetch a.workCustomFieldGroup as workCustomFieldGroup " +
				"  join fetch a.savedWorkCustomFields as savedWorkCustomFields where a.work.id = :work_id")
})
public class WorkCustomFieldGroupAssociation extends DeletableEntity implements Comparable<WorkCustomFieldGroupAssociation> {

	private static final long serialVersionUID = 1L;

	private AbstractWork work;
	private WorkCustomFieldGroup workCustomFieldGroup;
	private Set<SavedWorkCustomField> savedWorkCustomFields = new HashSet<>();
	private Integer position;

	public WorkCustomFieldGroupAssociation() {
	}

	public WorkCustomFieldGroupAssociation(AbstractWork work, WorkCustomFieldGroup workCustomFieldGroup) {
		this.work = work;
		this.workCustomFieldGroup = workCustomFieldGroup;
		this.position = 0;
	}

	public WorkCustomFieldGroupAssociation(AbstractWork work, WorkCustomFieldGroup workCustomFieldGroup, int position) {
		this(work, workCustomFieldGroup);
		this.position = position;
	}

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "work_id", updatable = false)
	public AbstractWork getWork() {
		return work;
	}

	public void setWork(AbstractWork work) {
		this.work = work;
	}

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "work_custom_field_group_id")
	public WorkCustomFieldGroup getWorkCustomFieldGroup() {
		return workCustomFieldGroup;
	}

	public void setWorkCustomFieldGroup(WorkCustomFieldGroup workCustomFieldGroup) {
		this.workCustomFieldGroup = workCustomFieldGroup;
	}

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "workCustomFieldGroupAssociation")
	public Set<SavedWorkCustomField> getSavedWorkCustomFields() {
		return savedWorkCustomFields;
	}

	public void setSavedWorkCustomFields(Set<SavedWorkCustomField> savedWorkCustomFields) {
		this.savedWorkCustomFields = savedWorkCustomFields;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	@Override
	public int compareTo(WorkCustomFieldGroupAssociation association) {
		return this.getPosition().compareTo(association.getPosition());
	}
}

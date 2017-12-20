package com.workmarket.domains.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name = "callingcodes")
@Table(name = "calling_codes")
@NamedQueries({
		@NamedQuery(name = "CallingCode.findAllActiveCallingCodes", query = "select cc from callingcodes cc where cc.deleted = false order by sortOrder asc"),
		@NamedQuery(name = "CallingCode.findAllActiveCallingCodesAlphabetically", query = "select cc from callingcodes cc where cc.deleted = false order by name asc"),
		@NamedQuery(name = "CallingCode.findCallingCodesByCallingCodesId", query = "select cc from callingcodes cc where cc.callingCodeId = :calling_code_id"),
		@NamedQuery(name = "CallingCode.findCallingCodesById", query = "select cc from callingcodes cc where cc.id = :id"),
		@NamedQuery(name = "CallingCode.getAllUniqueActiveCallingCodeIds", query = "select distinct cc.callingCodeId from callingcodes cc where cc.deleted = false order by cc.callingCodeId")
})
@AuditChanges
public class CallingCode  extends AbstractEntity{
	private static final long serialVersionUID = 1L;

	@NotNull
	@Size(min = Constants.NAME_MIN_LENGTH, max = Constants.NAME_MAX_LENGTH)
	private String callingCodeId;

	@NotNull
	@Size(min = Constants.NAME_MIN_LENGTH, max = Constants.NAME_MAX_LENGTH)
	private String name;

	@NotNull
	private Integer sortOrder;

	private Boolean deleted = Boolean.FALSE;

	@Column(name = "deleted", nullable = false)
	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	@Column(name = "calling_code_id", nullable = false, length = Constants.NAME_MAX_LENGTH)
	public String getCallingCodeId() {
		return callingCodeId;
	}

	public void setCallingCodeId(String callingCodeId) {
		this.callingCodeId = callingCodeId;
	}

	@Column(name = "calling_code_text", nullable = false, length = Constants.NAME_MAX_LENGTH)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "sort_order", nullable = false)
	public Integer getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}
}

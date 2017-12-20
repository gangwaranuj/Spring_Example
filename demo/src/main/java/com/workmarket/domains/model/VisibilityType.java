package com.workmarket.domains.model;

import com.google.common.collect.ImmutableList;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

/**
 * Created by alejandrosilva on 1/19/15.
 */

@Entity(name = "visibilityType")
@Table(name = "visibility_type")
@NamedQueries({
	@NamedQuery(name="visibilityType.find", query="select a from visibilityType a order by level asc")
})
public class VisibilityType extends LookupEntity {

	private static final long serialVersionUID = -8209914306218601999L;
	public static final String
		INTERNAL = "internal",
		ASSIGNED_WORKER = "assigned_worker",
		PUBLIC = "public";
	public static final List<String> VISIBILITY_TYPE_CODES = ImmutableList.of(INTERNAL, ASSIGNED_WORKER, PUBLIC);
	public static final String DEFAULT_VISIBILITY = ASSIGNED_WORKER;

	private short level;

	public VisibilityType() {
		super();
	}

	public VisibilityType(String code) {
		super(code);
	}

	// Persists the order of the different visibility levels. The lower the level the lower the visibility.
	@Column(name = "level", insertable = false, updatable = false)
	public short getLevel() {
		return this.level;
	}

	public void setLevel(short level) {
		this.level = level;
	}

	@Transient
	public boolean isInternal() {
		return getCode().equals(INTERNAL);
	}

	@Transient
	public boolean isAssignedWorker() {
		return getCode().equals(ASSIGNED_WORKER);
	}

	@Transient
	public boolean isPublic() {
		return getCode().equals(PUBLIC);
	}

	public static boolean isValidTypeCode(String code) {
		return VISIBILITY_TYPE_CODES.contains(code);
	}

	public static VisibilityType createDefaultVisibility() {
		return new VisibilityType(DEFAULT_VISIBILITY);
	}

}

package com.workmarket.domains.model.datetime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.workmarket.domains.model.DeletableEntity;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name = "timeZone")
@Table(name = "time_zone")
@NamedQueries({
		@NamedQuery(name = "TimeZone.findAllActiveTimeZones", query = "select tz from timeZone tz where tz.deleted = false order by sortOrder asc"),
		@NamedQuery(name = "TimeZone.findTimeZonesByTimeZoneId", query = "select tz from timeZone tz where tz.timeZoneId = :timeZoneId")

})
@AuditChanges
public class TimeZone extends DeletableEntity {
	private static final long serialVersionUID = 1L;

	@NotNull
	@Size(min = Constants.NAME_MIN_LENGTH, max = Constants.NAME_MAX_LENGTH)
	private String timeZoneId;
	@NotNull
	@Size(min = Constants.NAME_MIN_LENGTH, max = Constants.NAME_MAX_LENGTH)
	private String name;

	@NotNull
	private Integer sortOrder;

	@Column(name = "time_zone_id", nullable = false, length = Constants.NAME_MAX_LENGTH)
	public String getTimeZoneId() {
		return timeZoneId;
	}

	public void setTimeZoneId(String timeZoneId) {
		this.timeZoneId = timeZoneId;
	}

	@Column(name = "name", nullable = false, length = Constants.NAME_MAX_LENGTH)
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

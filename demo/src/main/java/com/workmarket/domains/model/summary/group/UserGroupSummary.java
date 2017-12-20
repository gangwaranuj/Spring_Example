package com.workmarket.domains.model.summary.group;

import com.workmarket.domains.model.audit.AuditedEntity;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.utility.DateUtilities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Calendar;

/**
 * Author: ianha
 *
 * Date: 11/22/13
 * Time: 2:22 PM
 */
@Entity(name = "userGroupSummary")
@Table(name = "user_group_summary")
@AuditChanges
public class UserGroupSummary extends AuditedEntity {

	private BigDecimal totalThroughput = new BigDecimal(0.0);
	private Calendar lastRoutedOn = DateUtilities.getCalendarFromMillis(DateUtilities.WM_EPOCH);
	private UserGroup userGroup;
	private Calendar lastUpdateOn = DateUtilities.getCalendarFromMillis(DateUtilities.WM_EPOCH);

	public UserGroupSummary() {
	}

	@OneToOne
	@JoinColumn(name = "user_group_id", referencedColumnName = "id", nullable = false)
	public UserGroup getUserGroup() {


		return this.userGroup;
	}

	public void setUserGroup(UserGroup userGroup) {
		this.userGroup = userGroup;
	}

	@Column(name = "total_throughput")
	public BigDecimal getTotalThroughput() {
		return totalThroughput;
	}

	public void setTotalThroughput(BigDecimal totalThroughput) {
		this.totalThroughput = totalThroughput;
	}

	@Column(name = "last_routed_on", nullable = true)
	public Calendar getLastRoutedOn() {
		return lastRoutedOn;
	}

	public void setLastRoutedOn(Calendar lastRoutedOn) {
		this.lastRoutedOn = lastRoutedOn;
	}

	@Column(name = "last_update_on", nullable = false)
	public Calendar getLastUpdateOn() {
		return lastUpdateOn;
	}

	public void setLastUpdateOn(Calendar lastUpdateOn) {
		this.lastUpdateOn = lastUpdateOn;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof UserGroupSummary)) return false;
		if (!super.equals(o)) return false;

		UserGroupSummary that = (UserGroupSummary) o;

		if (lastRoutedOn != null ? !lastRoutedOn.equals(that.lastRoutedOn) : that.lastRoutedOn != null) return false;
		if (lastUpdateOn != null ? !lastUpdateOn.equals(that.lastUpdateOn) : that.lastUpdateOn != null) return false;
		if (totalThroughput != null ? !totalThroughput.equals(that.totalThroughput) : that.totalThroughput != null)
			return false;
		if (userGroup != null ? !userGroup.getId().equals(that.userGroup.getId()) : that.userGroup != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (totalThroughput != null ? totalThroughput.hashCode() : 0);
		result = 31 * result + (lastRoutedOn != null ? lastRoutedOn.hashCode() : 0);
		result = 31 * result + (userGroup != null ? userGroup.getId().hashCode() : 0);
		result = 31 * result + (lastUpdateOn != null ? lastUpdateOn.hashCode() : 0);
		return result;
	}
}

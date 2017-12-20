package com.workmarket.domains.model.user;

import com.workmarket.domains.model.DeletableEntity;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.utility.DateUtilities;
import org.hibernate.annotations.DiscriminatorOptions;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Calendar;

@Entity(name="availability")
@Table(name="user_availability")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="type", discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue("base")
@DiscriminatorOptions(force=true)
@NamedQueries({
	@NamedQuery(name="availability.activeDailyWorkingHoursByUser", query="from workAvailability where user.id = :userId and week_day = :weekDay and deleted = 0"),
	@NamedQuery(name="availability.activeWeeklyWorkingHoursByUser", query="from workAvailability where user.id = :userId and deleted = 0"),
	@NamedQuery(name="availability.dailyWorkingHoursByUser", query="from workAvailability where user.id = :userId and week_day = :weekDay"),
	@NamedQuery(name="availability.weeklyWorkingHoursByUser", query="from workAvailability where user.id = :userId"),

	@NamedQuery(name="availability.activeDailyNotificationHoursByUser", query="from notificationAvailability where user.id = :userId and weekDay = :weekDay and deleted = 0"),
	@NamedQuery(name="availability.activeWeeklyNotificationHoursByUser", query="from notificationAvailability where user.id = :userId and deleted = 0"),
	@NamedQuery(name="availability.dailyNotificationHoursByUser", query="from notificationAvailability where user.id = :userId and week_day = :weekDay"),
	@NamedQuery(name="availability.weeklyNotificationHoursByUser", query="from notificationAvailability where user.id = :userId"),

	@NamedQuery(name="availability.dailyHoursByClientLocation", query="from clientLocationAvailability where clientLocation.id = :clientLocationId and week_day = :weekDay"),
	@NamedQuery(name="availability.weeklyHoursByClientLocation", query="from clientLocationAvailability where clientLocation.id = :clientLocationId")

})
@AuditChanges
public abstract class UserAvailability extends DeletableEntity {

	private static final long serialVersionUID = 1L;

	public static final int SUNDAY = 0;
	public static final int MONDAY = 1;
	public static final int TUESDAY = 2;
	public static final int WEDNESDAY = 3;
	public static final int THURSDAY = 4;
	public static final int FRIDAY = 5;
	public static final int SATURDAY = 6;

	protected User user;
	private Integer weekDay;
	private Calendar fromTime;
	private Calendar toTime;
	private boolean allDayAvailable = Boolean.FALSE;

	public UserAvailability() {}

	public UserAvailability(Integer weekDay) {
		this.weekDay = weekDay;
	}

	public UserAvailability(User user, Integer weekDay) {
		this.user = user;
		this.weekDay = weekDay;
	}

	// Because of how Hibernate handles single table inheritance,
	// the user association must be defined on sub-classes.
	@Transient
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}

	@Column(name = "week_day", nullable = false)
	public Integer getWeekDay() {
		return weekDay;
	}
	public void setWeekDay(Integer weekDay) {
		this.weekDay = weekDay;
	}

	@Transient
	public String getWeekDayName() {
		return DateUtilities.getWeekdayName(weekDay);
	}

	@Column(name = "from_time")
	public Calendar getFromTime() {
		return fromTime;
	}
	public void setFromTime(Calendar fromTime) {
		this.fromTime = fromTime;
	}

    public void setToTimeFromMilitaryTime(Integer hours) {
        this.toTime = DateUtilities.newCalendarFromMilitaryTime(hours);
    }

	@Column(name = "to_time")
	public Calendar getToTime() {
		return toTime;
	}
	public void setToTime(Calendar toTime) {
		this.toTime = toTime;
	}

	public void setFromTimeFromMilitaryTime(Integer hours) {
		this.fromTime = DateUtilities.newCalendarFromMilitaryTime(hours);
	}

	@Column(name = "all_day_available", nullable = false)
	public boolean isAllDayAvailable() {
		return allDayAvailable;
	}
	public void setAllDayAvailable(boolean allDayAvailable) {
		this.allDayAvailable = allDayAvailable;
	}

	public Boolean overlaps(UserAvailability o) {
		if (!getWeekDay().equals(o.getWeekDay())) return false;
		if (isAllDayAvailable() || o.isAllDayAvailable()) return true;
		if (o.getDeleted()) return false;
		return DateUtilities.timeIntervalsOverlap(getFromTime(), getToTime(), o.getFromTime(), o.getToTime());
	}
}

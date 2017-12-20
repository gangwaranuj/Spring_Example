package com.workmarket.thrift.assessment;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AssessmentOptions implements Serializable {
	private static final long serialVersionUID = 1L;

	private double passingScore;
	private Boolean passingScoreShared;
	private int retakesAllowed;
	private int durationMinutes;
	private Boolean resultsSharedWithPassers;
	private Boolean resultsSharedWithFailers;
	private Boolean statisticsShared;
	private List<NotificationTypeConfiguration> notifications;
	private List<com.workmarket.thrift.core.User> notificationRecipients;
	private boolean featured;

	public AssessmentOptions() {
	}

	public AssessmentOptions(
			double passingScore,
			Boolean passingScoreShared,
			int retakesAllowed,
			int durationMinutes,
			Boolean resultsSharedWithPassers,
			Boolean resultsSharedWithFailers,
			Boolean statisticsShared,
			List<NotificationTypeConfiguration> notifications,
			List<com.workmarket.thrift.core.User> notificationRecipients,
			boolean featured) {
		this();
		this.passingScore = passingScore;
		this.passingScoreShared = passingScoreShared;
		this.retakesAllowed = retakesAllowed;
		this.durationMinutes = durationMinutes;
		this.resultsSharedWithPassers = resultsSharedWithPassers;
		this.resultsSharedWithFailers = resultsSharedWithFailers;
		this.statisticsShared = statisticsShared;
		this.notifications = notifications;
		this.notificationRecipients = notificationRecipients;
		this.featured = featured;
	}

	public double getPassingScore() {
		return this.passingScore;
	}

	public AssessmentOptions setPassingScore(double passingScore) {
		this.passingScore = passingScore;
		return this;
	}

	public boolean isSetPassingScore() {
		return (passingScore > 0D);
	}

	public Boolean getPassingScoreShared() {
		return this.passingScoreShared;
	}

	public boolean isPassingScoreShared() {
		return (this.passingScoreShared == null) ? false : this.passingScoreShared;
	}

	public AssessmentOptions setPassingScoreShared(Boolean passingScoreShared) {
		this.passingScoreShared = passingScoreShared;
		return this;
	}

	public int getRetakesAllowed() {
		return this.retakesAllowed;
	}

	public AssessmentOptions setRetakesAllowed(int retakesAllowed) {
		this.retakesAllowed = retakesAllowed;
		return this;
	}

	public boolean isSetRetakesAllowed() {
		return (retakesAllowed > 0);
	}

	public int getDurationMinutes() {
		return this.durationMinutes;
	}

	public AssessmentOptions setDurationMinutes(int durationMinutes) {
		this.durationMinutes = durationMinutes;
		return this;
	}

	public boolean isSetDurationMinutes() {
		return (durationMinutes > 0);
	}

	public Boolean getResultsSharedWithPassers() {
		return this.resultsSharedWithPassers;
	}

	public boolean isResultsSharedWithPassers() {
		return (this.resultsSharedWithPassers == null) ? false : this.resultsSharedWithPassers;
	}

	public AssessmentOptions setResultsSharedWithPassers(Boolean resultsSharedWithPassers) {
		this.resultsSharedWithPassers = resultsSharedWithPassers;
		return this;
	}

	public Boolean getResultsSharedWithFailers() {
		return this.resultsSharedWithFailers;
	}

	public boolean isResultsSharedWithFailers() {
		return (this.resultsSharedWithFailers == null) ? false : this.resultsSharedWithFailers;
	}

	public AssessmentOptions setResultsSharedWithFailers(Boolean resultsSharedWithFailers) {
		this.resultsSharedWithFailers = resultsSharedWithFailers;
		return this;
	}

	public Boolean getStatisticsShared() {
		return this.statisticsShared;
	}

	public boolean isStatisticsShared() {
		return (this.statisticsShared == null) ? false : this.statisticsShared;
	}

	public AssessmentOptions setStatisticsShared(Boolean statisticsShared) {
		this.statisticsShared = statisticsShared;
		return this;
	}

	public int getNotificationsSize() {
		return (this.notifications == null) ? 0 : this.notifications.size();
	}

	public java.util.Iterator<NotificationTypeConfiguration> getNotificationsIterator() {
		return (this.notifications == null) ? null : this.notifications.iterator();
	}

	public void addToNotifications(NotificationTypeConfiguration elem) {
		if (this.notifications == null) {
			this.notifications = new ArrayList<NotificationTypeConfiguration>();
		}
		this.notifications.add(elem);
	}

	public List<NotificationTypeConfiguration> getNotifications() {
		return this.notifications;
	}

	public AssessmentOptions setNotifications(List<NotificationTypeConfiguration> notifications) {
		this.notifications = notifications;
		return this;
	}

	public boolean isSetNotifications() {
		return this.notifications != null;
	}

	public int getNotificationRecipientsSize() {
		return (this.notificationRecipients == null) ? 0 : this.notificationRecipients.size();
	}

	public java.util.Iterator<com.workmarket.thrift.core.User> getNotificationRecipientsIterator() {
		return (this.notificationRecipients == null) ? null : this.notificationRecipients.iterator();
	}

	public void addToNotificationRecipients(com.workmarket.thrift.core.User elem) {
		if (this.notificationRecipients == null) {
			this.notificationRecipients = new ArrayList<com.workmarket.thrift.core.User>();
		}
		this.notificationRecipients.add(elem);
	}

	public List<com.workmarket.thrift.core.User> getNotificationRecipients() {
		return this.notificationRecipients;
	}

	public AssessmentOptions setNotificationRecipients(List<com.workmarket.thrift.core.User> notificationRecipients) {
		this.notificationRecipients = notificationRecipients;
		return this;
	}

	public boolean isSetNotificationRecipients() {
		return this.notificationRecipients != null;
	}

	public boolean isFeatured() {
		return this.featured;
	}

	public AssessmentOptions setFeatured(boolean featured) {
		this.featured = featured;
		return this;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof AssessmentOptions)
			return this.equals((AssessmentOptions) that);
		return false;
	}

	public boolean equals(AssessmentOptions that) {
		if (that == null)
			return false;

		boolean this_present_passingScore = true;
		boolean that_present_passingScore = true;
		if (this_present_passingScore || that_present_passingScore) {
			if (!(this_present_passingScore && that_present_passingScore))
				return false;
			if (this.passingScore != that.passingScore)
				return false;
		}

		boolean this_present_passingScoreShared = true;
		boolean that_present_passingScoreShared = true;
		if (this_present_passingScoreShared || that_present_passingScoreShared) {
			if (!(this_present_passingScoreShared && that_present_passingScoreShared))
				return false;
			if (this.passingScoreShared != that.passingScoreShared)
				return false;
		}

		boolean this_present_retakesAllowed = true;
		boolean that_present_retakesAllowed = true;
		if (this_present_retakesAllowed || that_present_retakesAllowed) {
			if (!(this_present_retakesAllowed && that_present_retakesAllowed))
				return false;
			if (this.retakesAllowed != that.retakesAllowed)
				return false;
		}

		boolean this_present_durationMinutes = true;
		boolean that_present_durationMinutes = true;
		if (this_present_durationMinutes || that_present_durationMinutes) {
			if (!(this_present_durationMinutes && that_present_durationMinutes))
				return false;
			if (this.durationMinutes != that.durationMinutes)
				return false;
		}

		boolean this_present_resultsSharedWithPassers = (this.resultsSharedWithPassers != null);
		boolean that_present_resultsSharedWithPassers = (that.resultsSharedWithPassers != null);
		if (this_present_resultsSharedWithPassers || that_present_resultsSharedWithPassers) {
			if (!(this_present_resultsSharedWithPassers && that_present_resultsSharedWithPassers))
				return false;
			if (this.resultsSharedWithPassers != that.resultsSharedWithPassers)
				return false;
		}

		boolean this_present_resultsSharedWithFailers = (this.resultsSharedWithFailers != null);
		boolean that_present_resultsSharedWithFailers = (that.resultsSharedWithFailers != null);
		if (this_present_resultsSharedWithFailers || that_present_resultsSharedWithFailers) {
			if (!(this_present_resultsSharedWithFailers && that_present_resultsSharedWithFailers))
				return false;
			if (this.resultsSharedWithFailers != that.resultsSharedWithFailers)
				return false;
		}

		boolean this_present_statisticsShared = (this.statisticsShared != null);
		boolean that_present_statisticsShared = (that.statisticsShared != null);
		if (this_present_statisticsShared || that_present_statisticsShared) {
			if (!(this_present_statisticsShared && that_present_statisticsShared))
				return false;
			if (this.statisticsShared != that.statisticsShared)
				return false;
		}

		if (!this.notifications.equals(that.notifications))
			return false;

		boolean this_present_notificationRecipients = true && this.isSetNotificationRecipients();
		boolean that_present_notificationRecipients = true && that.isSetNotificationRecipients();
		if (this_present_notificationRecipients || that_present_notificationRecipients) {
			if (!(this_present_notificationRecipients && that_present_notificationRecipients))
				return false;
			if (!this.notificationRecipients.equals(that.notificationRecipients))
				return false;
		}

		boolean this_present_featured = true;
		boolean that_present_featured = true;
		if (this_present_featured || that_present_featured) {
			if (!(this_present_featured && that_present_featured))
				return false;
			if (this.featured != that.featured)
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_passingScore = true;
		builder.append(present_passingScore);
		if (present_passingScore)
			builder.append(passingScore);

		boolean present_passingScoreShared = true;
		builder.append(present_passingScoreShared);
		if (present_passingScoreShared)
			builder.append(passingScoreShared);

		boolean present_retakesAllowed = true;
		builder.append(present_retakesAllowed);
		if (present_retakesAllowed)
			builder.append(retakesAllowed);

		boolean present_durationMinutes = true;
		builder.append(present_durationMinutes);
		if (present_durationMinutes)
			builder.append(durationMinutes);

		boolean present_resultsSharedWithPassers = (resultsSharedWithPassers != null);
		builder.append(present_resultsSharedWithPassers);
		if (present_resultsSharedWithPassers)
			builder.append(resultsSharedWithPassers);

		boolean present_resultsSharedWithFailers = (resultsSharedWithFailers != null);
		builder.append(present_resultsSharedWithFailers);
		if (present_resultsSharedWithFailers)
			builder.append(resultsSharedWithFailers);

		boolean present_statisticsShared = (statisticsShared != null);
		builder.append(present_statisticsShared);
		if (present_statisticsShared)
			builder.append(statisticsShared);

		boolean present_notifications = true && (isSetNotifications());
		builder.append(present_notifications);
		if (present_notifications)
			builder.append(notifications);

		boolean present_notificationRecipients = true && (isSetNotificationRecipients());
		builder.append(present_notificationRecipients);
		if (present_notificationRecipients)
			builder.append(notificationRecipients);

		boolean present_featured = true;
		builder.append(present_featured);
		if (present_featured)
			builder.append(featured);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("AssessmentOptions(");
		boolean first = true;

		sb.append("passingScore:");
		sb.append(this.passingScore);
		first = false;
		if (!first) sb.append(", ");
		sb.append("passingScoreShared:");
		sb.append(this.passingScoreShared);
		first = false;
		if (!first) sb.append(", ");
		sb.append("retakesAllowed:");
		sb.append(this.retakesAllowed);
		first = false;
		if (!first) sb.append(", ");
		sb.append("durationMinutes:");
		sb.append(this.durationMinutes);
		first = false;
		if (!first) sb.append(", ");
		sb.append("resultsSharedWithPassers:");
		sb.append(this.resultsSharedWithPassers);
		first = false;
		if (!first) sb.append(", ");
		sb.append("resultsSharedWithFailers:");
		sb.append(this.resultsSharedWithFailers);
		first = false;
		if (!first) sb.append(", ");
		sb.append("statisticsShared:");
		sb.append(this.statisticsShared);
		first = false;
		if (!first) sb.append(", ");
		sb.append("notifications:");
		if (this.notifications == null) {
			sb.append("null");
		} else {
			sb.append(this.notifications);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("notificationRecipients:");
		if (this.notificationRecipients == null) {
			sb.append("null");
		} else {
			sb.append(this.notificationRecipients);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("featured:");
		sb.append(this.featured);
		first = false;
		sb.append(")");
		return sb.toString();
	}
}


package com.workmarket.domains.model.assessment;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.annotations.Where;

import com.google.common.collect.Sets;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.notification.AssessmentNotificationPreference;

@Embeddable
public class AssessmentConfiguration implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Double passingScore = 100.0;
	private Boolean passingScoreShared = Boolean.TRUE;
	private Integer retakesAllowed;
	private Integer durationMinutes;
	private Boolean resultsSharedWithPassers = Boolean.FALSE;
	private Boolean resultsSharedWithFailers = Boolean.FALSE;
	private Boolean statisticsShared = Boolean.TRUE;
	
	private Set<AssessmentNotificationPreference> notifications = Sets.newHashSet();
	private Set<User> notificationRecipients = Sets.newHashSet();
	private boolean featured = false;
	
	@Column(name = "passing_score")
	public Double getPassingScore() {
		return passingScore;
	}
	public void setPassingScore(Double passingScore) {
		this.passingScore = passingScore;
	}
	
	@Column(name = "passing_score_shared")
	public Boolean getPassingScoreShared() {
		return passingScoreShared;
	}
	public void setPassingScoreShared(Boolean passingScoreShared) {
		this.passingScoreShared = passingScoreShared;
	}
	
	@Column(name = "retakes_allowed")
	public Integer getRetakesAllowed() {
		return retakesAllowed;
	}
	public void setRetakesAllowed(Integer retakesAllowed) {
		this.retakesAllowed = retakesAllowed;
	}
	
	@Column(name = "duration_minutes")
	public Integer getDurationMinutes() {
		return durationMinutes;
	}
	public void setDurationMinutes(Integer durationMinutes) {
		this.durationMinutes = durationMinutes;
	}
	
	@Column(name = "results_shared_with_passers")
	public Boolean getResultsSharedWithPassers() {
		return resultsSharedWithPassers;
	}
	public void setResultsSharedWithPassers(Boolean resultsSharedWithPassers) {
		this.resultsSharedWithPassers = resultsSharedWithPassers;
	}
	
	@Column(name = "results_shared_with_failers")
	public Boolean getResultsSharedWithFailers() {
		return resultsSharedWithFailers;
	}
	public void setResultsSharedWithFailers(Boolean resultsSharedWithFailers) {
		this.resultsSharedWithFailers = resultsSharedWithFailers;
	}
	
	@Column(name = "statistics_shared")
	public Boolean getStatisticsShared() {
		return statisticsShared;
	}
	public void setStatisticsShared(Boolean statisticsShared) {
		this.statisticsShared = statisticsShared;
	}
			
	@OneToMany(mappedBy = "assessment", fetch = FetchType.LAZY)
	@Where(clause = "deleted = 0")
	public Set<AssessmentNotificationPreference> getNotifications() {
		return notifications;
	}
	public void setNotifications(Set<AssessmentNotificationPreference> notifications) {
		this.notifications = notifications;
	}
	
	@ManyToMany
	@JoinTable(name = "assessment_notification_user_association",
	           joinColumns = { @JoinColumn(name = "assessment_id") },
	           inverseJoinColumns = { @JoinColumn(name = "user_id") })
	public Set<User> getNotificationRecipients() {
		return notificationRecipients;
	}
	public void setNotificationRecipients(Set<User> notificationRecipients) {
		this.notificationRecipients = notificationRecipients;
	}
	
	@Column(name="featured_flag", nullable = false)
	public boolean isFeatured() {
		return featured;
	}
	
	public void setFeatured(boolean featured) {
		this.featured = featured;
	}
	
	@Transient
	public boolean isTimed() {
		return (durationMinutes != null && durationMinutes.intValue() > 0);
	}
}

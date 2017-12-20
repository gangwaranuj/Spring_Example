package com.workmarket.domains.model.assessment;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.Industry;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.audit.AuditedEntity;
import com.workmarket.domains.model.notification.AssessmentNotificationPreference;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.model.skill.Skill;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Where;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;
import java.util.Set;

@Entity(name = "assessment")
@Table(name = "assessment")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("base")
public abstract class AbstractAssessment extends AuditedEntity {

	public final static String GRADED_ASSESSMENT_TYPE = "graded";
	public final static String SURVEY_ASSESSMENT_TYPE = "survey";

	private static final long serialVersionUID = 1L;
	private String name;
	private String description;
	private Integer approximateDurationMinutes;
	private Set<Skill> skills = Sets.newHashSet();

	private AssessmentConfiguration configuration = new AssessmentConfiguration();
	private AssessmentStatusType assessmentStatusType = new AssessmentStatusType(AssessmentStatusType.DRAFT);
	private Boolean readOnly = Boolean.FALSE;

	private List<AbstractItem> items = Lists.newArrayList();

	private User user;
	private Company company;
	private Industry industry;

	@Column(name = "name", nullable = true)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "description", nullable = true)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "approximate_duration_minutes", nullable = true)
	public Integer getApproximateDurationMinutes() {
		return approximateDurationMinutes;
	}

	public void setApproximateDurationMinutes(Integer approximateDurationMinutes) {
		this.approximateDurationMinutes = approximateDurationMinutes;
	}

	@ManyToMany
	@JoinTable(name = "assessment_skill_association", joinColumns = @JoinColumn(name = "assessment_id"), inverseJoinColumns = @JoinColumn(name = "skill_id"))
	public Set<Skill> getSkills() {
		return skills;
	}

	public void setSkills(Set<Skill> skills) {
		this.skills = skills;
	}

	@Embedded
	public AssessmentConfiguration getConfiguration() {
		return this.configuration;
	}

	public void setConfiguration(AssessmentConfiguration configuration) {
		this.configuration = configuration;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "assessment_status_type_code", referencedColumnName = "code", nullable = false)
	public AssessmentStatusType getAssessmentStatusType() {
		return assessmentStatusType;
	}

	public void setAssessmentStatusType(AssessmentStatusType assessmentStatusType) {
		this.assessmentStatusType = assessmentStatusType;
	}

	@Column(name = "read_only", nullable = false)
	public Boolean getReadOnly() {
		return readOnly;
	}

	public void setReadOnly(Boolean readOnly) {
		this.readOnly = readOnly;
	}

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "assessment_id", nullable = false)
	@OrderColumn(name = "position")
	@Where(clause = "deleted = 0")
	public List<AbstractItem> getItems() {
		return items;
	}

	public void setItems(List<AbstractItem> items) {
		this.items = items;
	}

	@Transient
	public Set<AbstractItem> getGradedItems() {
		Set<AbstractItem> items = Sets.newHashSet();
		for (AbstractItem i : getItems()) {
			// ugh - hibernate is loading deleted abstract items as null
			if (i == null) {
				continue;
			} else if (i.isGraded()) {
				items.add(i);
			}
		}
		return items;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "company_id", referencedColumnName = "id", updatable = false)
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@Fetch(FetchMode.JOIN)
	@ManyToOne(optional = false)
	@JoinColumn(name = "industry_id")
	public Industry getIndustry() {
		return industry;
	}

	public void setIndustry(Industry industry) {
		this.industry = industry;
	}

	@Transient
	@Deprecated
	public Double getPassingScore() {
		return configuration.getPassingScore();
	}

	@Deprecated
	public void setPassingScore(Double passingScore) {
		configuration.setPassingScore(passingScore);
	}

	@Transient
	@Deprecated
	public Boolean getNotifyOnComplete() {
		for (AssessmentNotificationPreference n : configuration.getNotifications()) {
			if (n.getNotificationType().getCode().equals(NotificationType.ASSESSMENT_ATTEMPT_COMPLETED)) {
				return true;
			}
		}
		return false;
	}

	@Deprecated
	public void setNotifyOnComplete(Boolean notifyOnComplete) {
		configuration.getNotifications().add(new AssessmentNotificationPreference(NotificationType.ASSESSMENT_ATTEMPT_COMPLETED));
	}

	@Transient
	public boolean isInvitationOnly() {
		return configuration != null && !configuration.isFeatured();
	}

	@Transient
	public abstract String getType();

	@Transient
	public boolean hasAssetItems() {
		for (AbstractItem i : getItems()) {
			// ugh - hibernate is loading deleted abstract items as null
			if (i != null && i.getType().equals(AbstractItem.ASSET))
				return true;
		}
		return false;
	}

	public static AbstractAssessment newInstance(String type) {
		if (type.equals(GRADED_ASSESSMENT_TYPE)) {
			return new GradedAssessment();
		} else if (type.equals(SURVEY_ASSESSMENT_TYPE)) {
			return new SurveyAssessment();
		}
		return null;
	}
}

package com.workmarket.domains.model.assessment;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.workmarket.domains.model.DeletableEntity;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name="assessmentAttemptResponse")
@Table(name="assessment_attempt_response")
@NamedQueries({
		@NamedQuery(name = "assessmentAttemptResponse.findInProgressResponsesByItem", query = "FROM assessmentAttemptResponse aar"
				+ " WHERE aar.deleted = 0 AND aar.attempt.status.code = :status AND aar.item.id = :itemId")
})
@AuditChanges
public class AttemptResponse extends DeletableEntity {

	private static final long serialVersionUID = 1L;

	private Attempt attempt;
	private AbstractItem item;
	private Choice choice;
	private String value;
	private Set<Asset> assets = new HashSet<Asset>();

	private Boolean correct;
	private User grader;
	private Calendar gradedOn;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="assessment_attempt_id", nullable=false)
	public Attempt getAttempt() {
		return attempt;
	}
	public void setAttempt(Attempt attempt) {
		this.attempt = attempt;
	}

	// NOTE Eager loading the item with the response resolves an issue where Hibernate proxies the item
	// as an AbstractItem rather than the actual subclass. If we don't want to eagerly load (which in most cases we need to anyway)
	// see the following tip: http://jwyseur.blogspot.com/2009/06/casting-hibernate-proxies-to-subclasses.html

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="assessment_item_id", nullable=false)
	public AbstractItem getItem() {
		return item;
	}
	public void setItem(AbstractItem item) {
		this.item = item;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="assessment_item_choice_id", nullable=true)
	public Choice getChoice() {
		return choice;
	}
	public void setChoice(Choice choice) {
		this.choice = choice;
	}

	@Column(name="value", nullable=true)
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

	@ManyToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinTable(name="assessment_attempt_response_asset_association",
	           joinColumns=@JoinColumn(name="entity_id"),
	           inverseJoinColumns=@JoinColumn(name="asset_id"))
	public Set<Asset> getAssets() {
		return assets;
	}
	public void setAssets(Set<Asset> assets) {
		this.assets = assets;
	}

	@Column(name="is_correct", nullable=true)
	public Boolean isCorrect() {
		return correct;
	}
	public void setCorrect(Boolean correct) {
		this.correct = correct;
	}

	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	@JoinColumn(name="grader_id")
	public User getGrader() {
		return grader;
	}
	public void setGrader(User grader) {
		this.grader = grader;
	}

	@Column(name="graded_on", nullable=true)
	public Calendar getGradedOn() {
		return gradedOn;
	}
	public void setGradedOn(Calendar gradedOn) {
		this.gradedOn = gradedOn;
	}

	@Transient
	public boolean isGraded() {
		return correct != null && grader != null && gradedOn != null;
	}
}

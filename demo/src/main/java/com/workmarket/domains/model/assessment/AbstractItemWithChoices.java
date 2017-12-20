package com.workmarket.domains.model.assessment;

import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Transient;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Where;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity
@AuditChanges
@DiscriminatorValue(AbstractItem.ITEM)
public abstract class AbstractItemWithChoices extends AbstractItem {

	private static final long serialVersionUID = 1L;

	private List<Choice> choices = Lists.newArrayList();
	private Boolean otherAllowed = Boolean.FALSE;

	@Fetch(FetchMode.JOIN)
	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumn(name="assessment_item_id", nullable=false)
	@OrderColumn(name="position")
	@Where(clause = "deleted = 0")
	public List<Choice> getChoices() {
		return choices;
	}
	public void setChoices(List<Choice> choices) {
		this.choices = choices;
	}

	@Column(name="other_allowed", nullable = false)
	public Boolean getOtherAllowed() {
		return otherAllowed;
	}
	public void setOtherAllowed(Boolean otherAllowed) {
		this.otherAllowed = otherAllowed;
	}

	@Transient
	public Set<Choice> getCorrectChoices() {
		Set<Choice> correctChoices = Sets.newHashSet();
		for (Choice choice : choices)
			if (choice.getIsCorrect())
				correctChoices.add(choice);
		return correctChoices;
	}
}

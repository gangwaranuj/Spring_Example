package com.workmarket.domains.model.assessment;

import com.workmarket.domains.model.DeletableEntity;
import com.workmarket.domains.model.assessment.item.*;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.asset.Link;
import com.workmarket.domains.model.audit.AuditChanges;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity(name="assessmentItem")
@Table(name="assessment_item")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="type", discriminatorType= DiscriminatorType.STRING)
@DiscriminatorValue("base")
@AuditChanges
public abstract class AbstractItem extends DeletableEntity {

	private static final long serialVersionUID = 1L;

	public static final String ITEM = "base";
	public static final String DIVIDER = "divider";
	public static final String MULTIPLE_CHOICE = "multichoice";
	public static final String SINGLE_CHOICE_RADIO = "singleradio";
	public static final String SINGLE_CHOICE_LIST = "singlelist";
	public static final String SINGLE_LINE_TEXT = "singletext";
	public static final String MULTIPLE_LINE_TEXT = "multitext";
	public static final String DATE = "date";
	public static final String PHONE = "phone";
	public static final String EMAIL = "email";
	public static final String NUMERIC = "numeric";
	public static final String ASSET = "asset";
	public static final String LINK = "link";

	@Size(min = 0, max = 2048, message = "Prompt length too long.")
	private String prompt;
	private String description;
	private String hint;
	private String incorrectFeedback;
	private Integer maxLength;
	private Boolean graded = Boolean.TRUE;
	private Set<Asset> assets = new HashSet<Asset>();
	private Set<Link> links = new HashSet<Link>();

	@Column(name="prompt", nullable=true, length=2048)
	public String getPrompt() {
		return prompt;
	}

	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}

	@Column(name="description")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name="hint")
	public String getHint() {
		return hint;
	}

	public void setHint(String hint) {
		this.hint = hint;
	}

	@Column(name="incorrect_feedback")
	public String getIncorrectFeedback() {
		return incorrectFeedback;
	}

	public void setIncorrectFeedback(String incorrectFeedback) {
		this.incorrectFeedback = incorrectFeedback;
	}

	@Column(name="max_length")
	public Integer getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(Integer maxLength) {
		this.maxLength = maxLength;
	}

	@Column(name="graded")
	public Boolean isGraded() {
		return graded;
	}

	public void setGraded(Boolean graded) {
		this.graded = graded;
	}

	@Fetch(FetchMode.JOIN)
	@ManyToMany(cascade=CascadeType.ALL)
	@JoinTable(name="assessment_item_asset_association",
	           joinColumns=@JoinColumn(name="assessment_item_id"),
	           inverseJoinColumns=@JoinColumn(name="asset_id"))
	public Set<Asset> getAssets() {
		return assets;
	}

	public void setAssets(Set<Asset> assets) {
		this.assets = assets;
	}

	@Fetch(FetchMode.JOIN)
	@ManyToMany(cascade=CascadeType.ALL)
	@JoinTable(name="assessment_item_link_association",
			   joinColumns=@JoinColumn(name="assessment_item_id"),
			   inverseJoinColumns=@JoinColumn(name="link_id"))
	public Set<Link> getLinks() {
		return links;
	}

	public void setLinks(Set<Link> links){
		this.links = links;
	}

	@Transient
	public abstract String getType();

	@Transient
	public abstract Boolean isManuallyGraded();

	static public AbstractItem newInstance(String type) {
		if (type.equals(DIVIDER)) return new DividerItem();
		if (type.equals(MULTIPLE_CHOICE)) return new MultipleChoiceItem();
		if (type.equals(SINGLE_CHOICE_RADIO)) return new SingleChoiceRadioItem();
		if (type.equals(SINGLE_CHOICE_LIST)) return new SingleChoiceListItem();
		if (type.equals(SINGLE_LINE_TEXT)) return new SingleLineTextItem();
		if (type.equals(MULTIPLE_LINE_TEXT)) return new MultipleLineTextItem();
		if (type.equals(DATE)) return new DateItem();
		if (type.equals(PHONE)) return new PhoneItem();
		if (type.equals(EMAIL)) return new EmailItem();
		if (type.equals(NUMERIC)) return new NumericItem();
		if (type.equals(ASSET)) return new AssetItem();
		if (type.equals(LINK)) return new LinkItem();
		return null;
	}
}

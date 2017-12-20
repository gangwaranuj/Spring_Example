package com.workmarket.thrift.assessment;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Item implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;
	private int position;
	private String prompt;
	private String description;
	private String hint;
	private ItemType type;
	private boolean otherAllowed;
	private int maxLength;
	private boolean graded;
	private boolean manuallyGraded;
	private List<com.workmarket.thrift.core.Asset> assets;
	private List<com.workmarket.thrift.assessment.Link> links;
	private List<com.workmarket.thrift.core.Upload> uploads;
	private List<Choice> choices;
	private String incorrectFeedback;
	private String embedLink;

	public Item() {
	}

	public Item(
			long id,
			int position,
			String prompt,
			String description,
			String hint,
			ItemType type,
			boolean otherAllowed,
			int maxLength,
			boolean graded,
			boolean manuallyGraded,
			List<com.workmarket.thrift.core.Asset> assets,
			List<com.workmarket.thrift.assessment.Link> links,
			List<com.workmarket.thrift.core.Upload> uploads,
			List<Choice> choices,
			String incorrectFeedback,
			String embedLink) {
		this();
		this.id = id;
		this.position = position;
		this.prompt = prompt;
		this.description = description;
		this.hint = hint;
		this.type = type;
		this.otherAllowed = otherAllowed;
		this.maxLength = maxLength;
		this.graded = graded;
		this.manuallyGraded = manuallyGraded;
		this.assets = assets;
		this.links = links;
		this.uploads = uploads;
		this.choices = choices;
		this.incorrectFeedback = incorrectFeedback;
		this.embedLink = embedLink;
	}

	public long getId() {
		return this.id;
	}

	public Item setId(long id) {
		this.id = id;
		return this;
	}

	public boolean isSetId() {
		return (id > 0L);
	}

	public int getPosition() {
		return this.position;
	}

	public Item setPosition(int position) {
		this.position = position;
		return this;
	}

	public boolean isSetPosition() {
		return (position > 0);
	}

	public String getPrompt() {
		return this.prompt;
	}

	public Item setPrompt(String prompt) {
		this.prompt = prompt;
		return this;
	}

	public boolean isSetPrompt() {
		return this.prompt != null;
	}

	public String getDescription() {
		return this.description;
	}

	public Item setDescription(String description) {
		this.description = description;
		return this;
	}

	public boolean isSetDescription() {
		return this.description != null;
	}

	public String getHint() {
		return this.hint;
	}

	public Item setHint(String hint) {
		this.hint = hint;
		return this;
	}

	public boolean isSetHint() {
		return this.hint != null;
	}

	public ItemType getType() {
		return this.type;
	}

	public Item setType(ItemType type) {
		this.type = type;
		return this;
	}

	public boolean isSetType() {
		return this.type != null;
	}

	public boolean isOtherAllowed() {
		return this.otherAllowed;
	}

	public Item setOtherAllowed(boolean otherAllowed) {
		this.otherAllowed = otherAllowed;
		return this;
	}

	public int getMaxLength() {
		return this.maxLength;
	}

	public Item setMaxLength(int maxLength) {
		this.maxLength = maxLength;
		return this;
	}

	public boolean isSetMaxLength() {
		return (maxLength > 0);
	}

	public boolean isGraded() {
		return this.graded;
	}

	public Item setGraded(boolean graded) {
		this.graded = graded;
		return this;
	}

	public boolean isManuallyGraded() {
		return this.manuallyGraded;
	}

	public Item setManuallyGraded(boolean manuallyGraded) {
		this.manuallyGraded = manuallyGraded;
		return this;
	}

	public int getLinksSize() {
		return (this.links == null) ? 0 : this.links.size();
	}

	public Iterator<com.workmarket.thrift.assessment.Link> getLinksIterator() {
		return (this.links == null) ? null : this.links.iterator();
	}

	public void addToLinks(com.workmarket.thrift.assessment.Link link){
		if (this.links == null) {
			this.links = new ArrayList<com.workmarket.thrift.assessment.Link>();
		}
		this.links.add(link);
	}

	public List<com.workmarket.thrift.assessment.Link> getLinks() {
		return this.links;
	}

	public Item setLinks(List<com.workmarket.thrift.assessment.Link> links){
		this.links = links;
		return this;
	}

	public boolean isSetLinks() {
		return this.links != null;
	}

	public int getAssetsSize() {
		return (this.assets == null) ? 0 : this.assets.size();
	}

	public java.util.Iterator<com.workmarket.thrift.core.Asset> getAssetsIterator() {
		return (this.assets == null) ? null : this.assets.iterator();
	}

	public void addToAssets(com.workmarket.thrift.core.Asset elem) {
		if (this.assets == null) {
			this.assets = new ArrayList<com.workmarket.thrift.core.Asset>();
		}
		this.assets.add(elem);
	}

	public List<com.workmarket.thrift.core.Asset> getAssets() {
		return this.assets;
	}

	public Item setAssets(List<com.workmarket.thrift.core.Asset> assets) {
		this.assets = assets;
		return this;
	}

	public boolean isSetAssets() {
		return this.assets != null;
	}

	public int getUploadsSize() {
		return (this.uploads == null) ? 0 : this.uploads.size();
	}

	public java.util.Iterator<com.workmarket.thrift.core.Upload> getUploadsIterator() {
		return (this.uploads == null) ? null : this.uploads.iterator();
	}

	public void addToUploads(com.workmarket.thrift.core.Upload elem) {
		if (this.uploads == null) {
			this.uploads = new ArrayList<com.workmarket.thrift.core.Upload>();
		}
		this.uploads.add(elem);
	}

	public List<com.workmarket.thrift.core.Upload> getUploads() {
		return this.uploads;
	}

	public Item setUploads(List<com.workmarket.thrift.core.Upload> uploads) {
		this.uploads = uploads;
		return this;
	}

	public boolean isSetUploads() {
		return this.uploads != null;
	}

	public int getChoicesSize() {
		return (this.choices == null) ? 0 : this.choices.size();
	}

	public java.util.Iterator<Choice> getChoicesIterator() {
		return (this.choices == null) ? null : this.choices.iterator();
	}

	public void addToChoices(Choice elem) {
		if (this.choices == null) {
			this.choices = new ArrayList<Choice>();
		}
		this.choices.add(elem);
	}

	public List<Choice> getChoices() {
		return this.choices;
	}

	public Item setChoices(List<Choice> choices) {
		this.choices = choices;
		return this;
	}

	public boolean isSetChoices() {
		return this.choices != null;
	}

	public String getIncorrectFeedback() {
		return this.incorrectFeedback;
	}

	public Item setIncorrectFeedback(String incorrectFeedback) {
		this.incorrectFeedback = incorrectFeedback;
		return this;
	}

	public boolean isSetIncorrectFeedback() {
		return this.incorrectFeedback != null;
	}

	public String getEmbedLink(){
		return embedLink;
	}

	public Item setEmbedLink(String embedLink) {
		this.embedLink = embedLink;
		return this;
	}

	public boolean isSetEmbedLink(){
		return this.embedLink != null && !this.embedLink.isEmpty();
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof Item)
			return this.equals((Item) that);
		return false;
	}

	private boolean equals(Item that) {
		if (that == null)
			return false;

		boolean this_present_id = true;
		boolean that_present_id = true;
		if (this_present_id || that_present_id) {
			if (!(this_present_id && that_present_id))
				return false;
			if (this.id != that.id)
				return false;
		}

		boolean this_present_position = true;
		boolean that_present_position = true;
		if (this_present_position || that_present_position) {
			if (!(this_present_position && that_present_position))
				return false;
			if (this.position != that.position)
				return false;
		}

		boolean this_present_prompt = true && this.isSetPrompt();
		boolean that_present_prompt = true && that.isSetPrompt();
		if (this_present_prompt || that_present_prompt) {
			if (!(this_present_prompt && that_present_prompt))
				return false;
			if (!this.prompt.equals(that.prompt))
				return false;
		}

		boolean this_present_description = true && this.isSetDescription();
		boolean that_present_description = true && that.isSetDescription();
		if (this_present_description || that_present_description) {
			if (!(this_present_description && that_present_description))
				return false;
			if (!this.description.equals(that.description))
				return false;
		}

		boolean this_present_hint = true && this.isSetHint();
		boolean that_present_hint = true && that.isSetHint();
		if (this_present_hint || that_present_hint) {
			if (!(this_present_hint && that_present_hint))
				return false;
			if (!this.hint.equals(that.hint))
				return false;
		}

		boolean this_present_type = true && this.isSetType();
		boolean that_present_type = true && that.isSetType();
		if (this_present_type || that_present_type) {
			if (!(this_present_type && that_present_type))
				return false;
			if (!this.type.equals(that.type))
				return false;
		}

		boolean this_present_otherAllowed = true;
		boolean that_present_otherAllowed = true;
		if (this_present_otherAllowed || that_present_otherAllowed) {
			if (!(this_present_otherAllowed && that_present_otherAllowed))
				return false;
			if (this.otherAllowed != that.otherAllowed)
				return false;
		}

		boolean this_present_maxLength = true;
		boolean that_present_maxLength = true;
		if (this_present_maxLength || that_present_maxLength) {
			if (!(this_present_maxLength && that_present_maxLength))
				return false;
			if (this.maxLength != that.maxLength)
				return false;
		}

		boolean this_present_graded = true;
		boolean that_present_graded = true;
		if (this_present_graded || that_present_graded) {
			if (!(this_present_graded && that_present_graded))
				return false;
			if (this.graded != that.graded)
				return false;
		}

		boolean this_present_manuallyGraded = true;
		boolean that_present_manuallyGraded = true;
		if (this_present_manuallyGraded || that_present_manuallyGraded) {
			if (!(this_present_manuallyGraded && that_present_manuallyGraded))
				return false;
			if (this.manuallyGraded != that.manuallyGraded)
				return false;
		}

		boolean this_present_assets = true && this.isSetAssets();
		boolean that_present_assets = true && that.isSetAssets();
		if (this_present_assets || that_present_assets) {
			if (!(this_present_assets && that_present_assets))
				return false;
			if (!this.assets.equals(that.assets))
				return false;
		}

		boolean this_present_uploads = true && this.isSetUploads();
		boolean that_present_uploads = true && that.isSetUploads();
		if (this_present_uploads || that_present_uploads) {
			if (!(this_present_uploads && that_present_uploads))
				return false;
			if (!this.uploads.equals(that.uploads))
				return false;
		}

		boolean this_present_choices = true && this.isSetChoices();
		boolean that_present_choices = true && that.isSetChoices();
		if (this_present_choices || that_present_choices) {
			if (!(this_present_choices && that_present_choices))
				return false;
			if (!this.choices.equals(that.choices))
				return false;
		}

		boolean this_present_incorrectFeedback = true && this.isSetIncorrectFeedback();
		boolean that_present_incorrectFeedback = true && that.isSetIncorrectFeedback();
		if (this_present_incorrectFeedback || that_present_incorrectFeedback) {
			if (!(this_present_incorrectFeedback && that_present_incorrectFeedback))
				return false;
			if (!this.incorrectFeedback.equals(that.incorrectFeedback))
				return false;
		}

		boolean this_present_embedLink = true && this.isSetEmbedLink();
		boolean that_present_embedLink = true && that.isSetEmbedLink();
		if (this_present_embedLink || that_present_embedLink) {
			if (!(this_present_embedLink && that_present_embedLink)) {
				return false;
			}
			if (!this.embedLink.equals(that.embedLink)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_id = true;
		builder.append(present_id);
		if (present_id)
			builder.append(id);

		boolean present_position = true;
		builder.append(present_position);
		if (present_position)
			builder.append(position);

		boolean present_prompt = true && (isSetPrompt());
		builder.append(present_prompt);
		if (present_prompt)
			builder.append(prompt);

		boolean present_description = true && (isSetDescription());
		builder.append(present_description);
		if (present_description)
			builder.append(description);

		boolean present_hint = true && (isSetHint());
		builder.append(present_hint);
		if (present_hint)
			builder.append(hint);

		boolean present_type = true && (isSetType());
		builder.append(present_type);
		if (present_type)
			builder.append(type.getValue());

		boolean present_otherAllowed = true;
		builder.append(present_otherAllowed);
		if (present_otherAllowed)
			builder.append(otherAllowed);

		boolean present_maxLength = true;
		builder.append(present_maxLength);
		if (present_maxLength)
			builder.append(maxLength);

		boolean present_graded = true;
		builder.append(present_graded);
		if (present_graded)
			builder.append(graded);

		boolean present_manuallyGraded = true;
		builder.append(present_manuallyGraded);
		if (present_manuallyGraded)
			builder.append(manuallyGraded);

		boolean present_assets = true && (isSetAssets());
		builder.append(present_assets);
		if (present_assets)
			builder.append(assets);

		boolean present_uploads = true && (isSetUploads());
		builder.append(present_uploads);
		if (present_uploads)
			builder.append(uploads);

		boolean present_choices = true && (isSetChoices());
		builder.append(present_choices);
		if (present_choices)
			builder.append(choices);

		boolean present_incorrectFeedback = true && (isSetIncorrectFeedback());
		builder.append(present_incorrectFeedback);
		if (present_incorrectFeedback)
			builder.append(incorrectFeedback);

		boolean present_embedLink = true && (isSetEmbedLink());
		builder.append(present_embedLink);
		if(present_embedLink)
			builder.append(embedLink);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Item(");
		boolean first = true;

		sb.append("id:");
		sb.append(this.id);
		first = false;
		if (!first) sb.append(", ");
		sb.append("position:");
		sb.append(this.position);
		first = false;
		if (!first) sb.append(", ");
		sb.append("prompt:");
		if (this.prompt == null) {
			sb.append("null");
		} else {
			sb.append(this.prompt);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("description:");
		if (this.description == null) {
			sb.append("null");
		} else {
			sb.append(this.description);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("hint:");
		if (this.hint == null) {
			sb.append("null");
		} else {
			sb.append(this.hint);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("type:");
		if (this.type == null) {
			sb.append("null");
		} else {
			sb.append(this.type);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("otherAllowed:");
		sb.append(this.otherAllowed);
		first = false;
		if (!first) sb.append(", ");
		sb.append("maxLength:");
		sb.append(this.maxLength);
		first = false;
		if (!first) sb.append(", ");
		sb.append("graded:");
		sb.append(this.graded);
		first = false;
		if (!first) sb.append(", ");
		sb.append("manuallyGraded:");
		sb.append(this.manuallyGraded);
		first = false;
		if (!first) sb.append(", ");
		sb.append("assets:");
		if (this.assets == null) {
			sb.append("null");
		} else {
			sb.append(this.assets);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("uploads:");
		if (this.uploads == null) {
			sb.append("null");
		} else {
			sb.append(this.uploads);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("choices:");
		if (this.choices == null) {
			sb.append("null");
		} else {
			sb.append(this.choices);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("incorrectFeedback:");
		if (this.incorrectFeedback == null) {
			sb.append("null");
		} else {
			sb.append(this.incorrectFeedback);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("embedLink:");
		if (this.embedLink == null) {
			sb.append("null");
		} else {
			sb.append(this.embedLink);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}
}


package com.workmarket.thrift.assessment;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Response implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;
	private Item item;
	private Choice choice;
	private String value;
	private boolean correct;
	private long gradedOn;
	private com.workmarket.thrift.core.User grader;
	private List<com.workmarket.thrift.core.Asset> assets;
	private List<com.workmarket.thrift.core.Upload> uploads;

	public Response() {
	}

	public Response(
			long id,
			Item item,
			Choice choice,
			String value,
			boolean correct,
			long gradedOn,
			com.workmarket.thrift.core.User grader,
			List<com.workmarket.thrift.core.Asset> assets,
			List<com.workmarket.thrift.core.Upload> uploads) {
		this();
		this.id = id;
		this.item = item;
		this.choice = choice;
		this.value = value;
		this.correct = correct;
		this.gradedOn = gradedOn;
		this.grader = grader;
		this.assets = assets;
		this.uploads = uploads;
	}

	public long getId() {
		return this.id;
	}

	public Response setId(long id) {
		this.id = id;
		return this;
	}

	public boolean isSetId() {
		return (id > 0L);
	}

	public Item getItem() {
		return this.item;
	}

	public Response setItem(Item item) {
		this.item = item;
		return this;
	}

	public boolean isSetItem() {
		return this.item != null;
	}

	public Choice getChoice() {
		return this.choice;
	}

	public Response setChoice(Choice choice) {
		this.choice = choice;
		return this;
	}

	public boolean isSetChoice() {
		return this.choice != null;
	}

	public String getValue() {
		return this.value;
	}

	public Response setValue(String value) {
		this.value = value;
		return this;
	}

	public boolean isSetValue() {
		return this.value != null;
	}

	public boolean isCorrect() {
		return this.correct;
	}

	public Response setCorrect(boolean correct) {
		this.correct = correct;
		return this;
	}

	public long getGradedOn() {
		return this.gradedOn;
	}

	public Response setGradedOn(long gradedOn) {
		this.gradedOn = gradedOn;
		return this;
	}

	public boolean isSetGradedOn() {
		return (gradedOn > 0L);
	}

	public com.workmarket.thrift.core.User getGrader() {
		return this.grader;
	}

	public Response setGrader(com.workmarket.thrift.core.User grader) {
		this.grader = grader;
		return this;
	}

	public boolean isSetGrader() {
		return this.grader != null;
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

	public Response setAssets(List<com.workmarket.thrift.core.Asset> assets) {
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

	public Response setUploads(List<com.workmarket.thrift.core.Upload> uploads) {
		this.uploads = uploads;
		return this;
	}

	public boolean isSetUploads() {
		return this.uploads != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof Response)
			return this.equals((Response) that);
		return false;
	}

	private boolean equals(Response that) {
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

		boolean this_present_item = true && this.isSetItem();
		boolean that_present_item = true && that.isSetItem();
		if (this_present_item || that_present_item) {
			if (!(this_present_item && that_present_item))
				return false;
			if (!this.item.equals(that.item))
				return false;
		}

		boolean this_present_choice = true && this.isSetChoice();
		boolean that_present_choice = true && that.isSetChoice();
		if (this_present_choice || that_present_choice) {
			if (!(this_present_choice && that_present_choice))
				return false;
			if (!this.choice.equals(that.choice))
				return false;
		}

		boolean this_present_value = true && this.isSetValue();
		boolean that_present_value = true && that.isSetValue();
		if (this_present_value || that_present_value) {
			if (!(this_present_value && that_present_value))
				return false;
			if (!this.value.equals(that.value))
				return false;
		}

		boolean this_present_correct = true;
		boolean that_present_correct = true;
		if (this_present_correct || that_present_correct) {
			if (!(this_present_correct && that_present_correct))
				return false;
			if (this.correct != that.correct)
				return false;
		}

		boolean this_present_gradedOn = true;
		boolean that_present_gradedOn = true;
		if (this_present_gradedOn || that_present_gradedOn) {
			if (!(this_present_gradedOn && that_present_gradedOn))
				return false;
			if (this.gradedOn != that.gradedOn)
				return false;
		}

		boolean this_present_grader = true && this.isSetGrader();
		boolean that_present_grader = true && that.isSetGrader();
		if (this_present_grader || that_present_grader) {
			if (!(this_present_grader && that_present_grader))
				return false;
			if (!this.grader.equals(that.grader))
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

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_id = true;
		builder.append(present_id);
		if (present_id)
			builder.append(id);

		boolean present_item = true && (isSetItem());
		builder.append(present_item);
		if (present_item)
			builder.append(item);

		boolean present_choice = true && (isSetChoice());
		builder.append(present_choice);
		if (present_choice)
			builder.append(choice);

		boolean present_value = true && (isSetValue());
		builder.append(present_value);
		if (present_value)
			builder.append(value);

		boolean present_correct = true;
		builder.append(present_correct);
		if (present_correct)
			builder.append(correct);

		boolean present_gradedOn = true;
		builder.append(present_gradedOn);
		if (present_gradedOn)
			builder.append(gradedOn);

		boolean present_grader = true && (isSetGrader());
		builder.append(present_grader);
		if (present_grader)
			builder.append(grader);

		boolean present_assets = true && (isSetAssets());
		builder.append(present_assets);
		if (present_assets)
			builder.append(assets);

		boolean present_uploads = true && (isSetUploads());
		builder.append(present_uploads);
		if (present_uploads)
			builder.append(uploads);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Response(");
		boolean first = true;

		sb.append("id:");
		sb.append(this.id);
		first = false;
		if (!first) sb.append(", ");
		sb.append("item:");
		if (this.item == null) {
			sb.append("null");
		} else {
			sb.append(this.item);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("choice:");
		if (this.choice == null) {
			sb.append("null");
		} else {
			sb.append(this.choice);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("value:");
		if (this.value == null) {
			sb.append("null");
		} else {
			sb.append(this.value);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("correct:");
		sb.append(this.correct);
		first = false;
		if (!first) sb.append(", ");
		sb.append("gradedOn:");
		sb.append(this.gradedOn);
		first = false;
		if (!first) sb.append(", ");
		sb.append("grader:");
		if (this.grader == null) {
			sb.append("null");
		} else {
			sb.append(this.grader);
		}
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
		sb.append(")");
		return sb.toString();
	}
}


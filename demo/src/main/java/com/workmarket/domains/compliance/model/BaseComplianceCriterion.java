package com.workmarket.domains.compliance.model;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.User;
import com.workmarket.domains.work.model.AbstractWork;
import org.apache.commons.lang3.builder.EqualsBuilder;

import java.util.List;

public abstract class BaseComplianceCriterion implements Comparable<BaseComplianceCriterion> {
	protected final User user;
	protected boolean met;
	protected String typeName;
	protected String name;
	protected List<String> messages = Lists.newArrayList();
	protected DateRange schedule;

	public BaseComplianceCriterion(User user, DateRange schedule) {
		this.user = user;
		this.schedule = schedule;
		met = true;
	}

	public User getUser() {
		return user;
	}

	public abstract AbstractWork getWork();

	public DateRange getSchedule() {
		return schedule;
	}

	public void setMet(boolean met) {
		this.met = met;
	}

	public boolean isMet() {
		return met;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getMessages() {
		return messages;
	}

	public void addMessage(String message) {
		this.messages.add(message);
	}


	@Override
	public boolean equals(Object obj) {
		if (obj == null) { return false; }
		if (obj == this) { return true; }
		if (obj.getClass() != getClass()) { return false; }
		BaseComplianceCriterion rhs = (BaseComplianceCriterion) obj;
		return new EqualsBuilder()
				.append(user, rhs.user)
				.append(getWork(), rhs.getWork())
				.append(schedule, rhs.schedule)
				.append(met, rhs.met)
				.append(name, rhs.name)
				.append(messages, rhs.messages)
				.isEquals();
	}

	@Override
	public int hashCode() {
		int result = user != null ? user.hashCode() : 0;

		if (schedule != null) {
			result = 31 * result + schedule.hashCode();
		}

		result = 31 * result + (met ? 1 : 0);

		if (typeName != null) {
			result = 31 * result + typeName.hashCode();
		}

		if (name != null) {
			result = 31 * result + name.hashCode();
		}

		if (messages != null) {
			result = 31 * result + messages.hashCode();
		}

		return result;
	}

	@Override
	public int compareTo(BaseComplianceCriterion that) {
		String me = this.getTypeName() + "|" + this.getName();
		String you = that.getTypeName() + "|" + that.getName();
		return me.compareTo(you);
	}
}

package com.workmarket.domains.model.requirementset;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Calendar;

public class Criterion implements Comparable<Criterion> {
	@JsonIgnore
	private final EligibilityUser user;

	@JsonIgnore
	private final RequirementSetable objWithRequirements;

	@JsonIgnore
	private Requirable requirable;

	private boolean met;
	private boolean anyOf;
	private boolean expires;
	private boolean expired;
	private boolean removeWhenExpired;
	private boolean warnWhenExpired;
	private String url;
	private String typeName;
	private String typeClassName;
	private String name;
	private Calendar expirationDate;

	public Criterion(EligibilityUser user, RequirementSetable objWithRequirements) {
		this.user = user;
		this.objWithRequirements = objWithRequirements;
	}

	@JsonIgnore
	public EligibilityUser getUser() {
		return user;
	}

	@JsonIgnore
	public RequirementSetable getRequirementSetable() {
		return objWithRequirements;
	}

	public void setRequirable(Requirable requirable) {
		this.requirable = requirable;
	}

	@JsonIgnore
	public Requirable getRequirable() {
		return this.requirable;
	}

	public boolean isAnyOf() {
		return anyOf;
	}

	public void setAnyOf(boolean anyOf) {
		this.anyOf = anyOf;
	}

	public void setExpires(boolean expires) {
		this.expires = expires;
	}

	public boolean getExpires() {
		return expires;
	}

	public void setExpired(boolean expired) {
		this.expired = expired;
	}

	public boolean isExpired() {
		return expired;
	}

	public void setRemoveWhenExpired(boolean removeWhenExpired) {
		this.removeWhenExpired = removeWhenExpired;
	}

	public boolean isRemoveWhenExpired() {
		return removeWhenExpired;
	}

	public void setWarnWhenExpired(boolean warnWhenExpired) {
		this.warnWhenExpired = warnWhenExpired;
	}

	public boolean isWarnWhenExpired() {
		return warnWhenExpired;
	}

	public void setMet(boolean met) {
		this.met = met;
	}

	public boolean isMet() {
		return met;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getTypeClassName() {
		return typeClassName;
	}

	public void setTypeClassName(String typeClassName) {
		this.typeClassName = typeClassName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Calendar getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Calendar expirationDate) {
		this.expirationDate = expirationDate;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Criterion that = (Criterion) o;

		if (!user.equals(that.user)) return false;
		if (requirable != null && that.requirable == null) return false;
		if (requirable != null && !requirable.equals(that.requirable)) return false;
		if (met != that.met) return false;
		if (anyOf != that.anyOf) return false;
		if (expires != that.expires) return false;
		if (url != null && that.url == null) return false;
		if (url != null && !url.equals(that.url)) return false;
		if (typeClassName != null && that.typeClassName == null) return false;
		if (typeClassName != null && !typeClassName.equals(that.typeClassName)) return false;
		if (typeName != null && that.typeName == null) return false;
		if (typeName != null && !typeName.equals(that.typeName)) return false;
		if (name != null && that.name == null) return false;
		if (name != null && !name.equals(that.name)) return false;
		if (expirationDate != null && that.expirationDate == null) return false;
		if (expirationDate != null && !expirationDate.equals(that.expirationDate)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = user != null ? user.hashCode() : 0;
		result = 31 * result + (met ? 1 : 0);
		result = 31 * result + (anyOf ? 1 : 0);
		result = 31 * result + (expires ? 1 : 0);

		if (requirable != null) {
			result = 31 * result + requirable.hashCode();
		}

		if (url != null) {
			result = 31 * result + url.hashCode();
		}

		if (typeName != null) {
			result = 31 * result + typeName.hashCode();
		}

		if (typeClassName != null) {
			result = 31 * result + typeClassName.hashCode();
		}

		if (expirationDate != null) {
			result = 31 * result + expirationDate.hashCode();
		}

		if (name != null) {
			result = 31 * result + name.hashCode();
		}

		return result;
	}

	@Override
	public int compareTo(Criterion that) {
		String me = this.getTypeName() + "|" + this.getName() + "|" + this.getUrl();
		String you = that.getTypeName() + "|" + that.getName() + "|" + that.getUrl();
		return me.compareTo(you);
	}
}

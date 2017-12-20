package com.workmarket.thrift.work;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;

public class ResourceLabel implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;
	private String code;
	private String description;
	private boolean ignored;
	private boolean confirmed;
	private String encryptedId;

	public long getId() {
		return id;
	}

	public ResourceLabel setId(long id) {
		this.id = id;
		return this;
	}

	public String getCode() {
		return code;
	}

	public ResourceLabel setCode(String code) {
		this.code = code;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public ResourceLabel setDescription(String description) {
		this.description = description;
		return this;
	}

	public boolean isIgnored() {
		return ignored;
	}

	public ResourceLabel setIgnored(boolean ignored) {
		this.ignored = ignored;
		return this;
	}

	public boolean isConfirmed() {
		return confirmed;
	}

	public ResourceLabel setConfirmed(boolean confirmed) {
		this.confirmed = confirmed;
		return this;
	}

	public String getEncryptedId() {
		return encryptedId;
	}

	public ResourceLabel setEncryptedId(String encryptedId) {
		this.encryptedId = encryptedId;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		return EqualsBuilder.reflectionEquals(this, o);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}

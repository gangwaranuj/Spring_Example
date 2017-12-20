package com.workmarket.api.internal;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public final class AccessToken implements Serializable {

	private static final long serialVersionUID = -6192596984495454048L;

	private String token;
	private String secret;

	public AccessToken() {}

	public AccessToken(String token, String secret) {
		this.token = token;
		this.secret = secret;
	}

	public String getToken() {
		return this.token;
	}

	public AccessToken setToken(String token) {
		this.token = token;
		return this;
	}

	public void unsetToken() {
		this.token = null;
	}

	public boolean isSetToken() {
		return this.token != null;
	}

	public String getSecret() {
		return this.secret;
	}

	public AccessToken setSecret(String secret) {
		this.secret = secret;
		return this;
	}

	public void unsetSecret() {
		this.secret = null;
	}

	public boolean isSetSecret() {
		return this.secret != null;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("AccessToken(");
		boolean first = true;

		sb.append("token:");
		if (this.token == null) {
			sb.append("null");
		} else {
			sb.append(this.token);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("secret:");
		if (this.secret == null) {
			sb.append("null");
		} else {
			sb.append(this.secret);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof AccessToken)) {
			return false;
		}

		AccessToken that = (AccessToken) o;

		if (secret != null ? !secret.equals(that.secret) : that.secret != null) return false;
		if (token != null ? !token.equals(that.token) : that.token != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
			.append(secret)
			.append(token)
			.toHashCode();
	}
}


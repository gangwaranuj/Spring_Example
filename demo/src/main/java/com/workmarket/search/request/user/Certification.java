package com.workmarket.search.request.user;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class Certification implements Serializable {
	private static final long serialVersionUID = 1L;

	private long certificationId;
	private String certificationName;
	private String certificationVendor;

	public Certification() {
	}

	public Certification(long certificationId, String certificationName, String certificationVendor) {
		this();
		this.certificationId = certificationId;
		this.certificationName = certificationName;
		this.certificationVendor = certificationVendor;
	}

	public long getCertificationId() {
		return this.certificationId;
	}

	public Certification setCertificationId(long certificationId) {
		this.certificationId = certificationId;
		return this;
	}

	public boolean isSetCertificationId() {
		return (certificationId > 0L);
	}

	public String getCertificationName() {
		return this.certificationName;
	}

	public Certification setCertificationName(String certificationName) {
		this.certificationName = certificationName;
		return this;
	}

	public boolean isSetCertificationName() {
		return this.certificationName != null;
	}

	public String getCertificationVendor() {
		return this.certificationVendor;
	}

	public Certification setCertificationVendor(String certificationVendor) {
		this.certificationVendor = certificationVendor;
		return this;
	}

	public boolean isSetCertificationVendor() {
		return this.certificationVendor != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof Certification)
			return this.equals((Certification) that);
		return false;
	}

	private boolean equals(Certification that) {
		if (that == null)
			return false;

		boolean this_present_certificationId = true;
		boolean that_present_certificationId = true;
		if (this_present_certificationId || that_present_certificationId) {
			if (!(this_present_certificationId && that_present_certificationId))
				return false;
			if (this.certificationId != that.certificationId)
				return false;
		}

		boolean this_present_certificationName = true && this.isSetCertificationName();
		boolean that_present_certificationName = true && that.isSetCertificationName();
		if (this_present_certificationName || that_present_certificationName) {
			if (!(this_present_certificationName && that_present_certificationName))
				return false;
			if (!this.certificationName.equals(that.certificationName))
				return false;
		}

		boolean this_present_certificationVendor = true && this.isSetCertificationVendor();
		boolean that_present_certificationVendor = true && that.isSetCertificationVendor();
		if (this_present_certificationVendor || that_present_certificationVendor) {
			if (!(this_present_certificationVendor && that_present_certificationVendor))
				return false;
			if (!this.certificationVendor.equals(that.certificationVendor))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_certificationId = true;
		builder.append(present_certificationId);
		if (present_certificationId)
			builder.append(certificationId);

		boolean present_certificationName = true && (isSetCertificationName());
		builder.append(present_certificationName);
		if (present_certificationName)
			builder.append(certificationName);

		boolean present_certificationVendor = true && (isSetCertificationVendor());
		builder.append(present_certificationVendor);
		if (present_certificationVendor)
			builder.append(certificationVendor);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Certification(");
		boolean first = true;

		sb.append("certificationId:");
		sb.append(this.certificationId);
		first = false;
		if (!first) sb.append(", ");
		sb.append("certificationName:");
		if (this.certificationName == null) {
			sb.append("null");
		} else {
			sb.append(this.certificationName);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("certificationVendor:");
		if (this.certificationVendor == null) {
			sb.append("null");
		} else {
			sb.append(this.certificationVendor);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}
}


package com.workmarket.thrift.assessment;

import com.google.common.collect.ImmutableList;
import com.workmarket.dao.assessment.AssessmentGroupAssociationDAO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AssessmentResponse implements Serializable {
	private static final long serialVersionUID = 1L;

	private Assessment assessment;
	private Set<RequestContext> requestContexts;
	private Set<AuthorizationContext> authorizationContexts;
	private Attempt latestAttempt;
	private Attempt requestedAttempt;

	public AssessmentResponse() {}

	public AssessmentResponse(
			Assessment assessment,
			Set<RequestContext> requestContexts,
			Set<AuthorizationContext> authorizationContexts,
			Attempt latestAttempt,
			Attempt requestedAttempt) {
		this();
		this.assessment = assessment;
		this.requestContexts = requestContexts;
		this.authorizationContexts = authorizationContexts;
		this.latestAttempt = latestAttempt;
		this.requestedAttempt = requestedAttempt;
	}

	public boolean isAuthorized(AuthorizationContext... contexts) {
		if (ArrayUtils.isEmpty(contexts)) {
			contexts = new AuthorizationContext[]{ AuthorizationContext.ADMIN, AuthorizationContext.ATTEMPT };
		}
		Set<AuthorizationContext> contextsToCheckFor = new HashSet<>(Arrays.asList(contexts));
		return CollectionUtils.containsAny(authorizationContexts, contextsToCheckFor);
	}

	public boolean shouldCheckIfInvited() {
		return assessment.isInvitationOnly() &&	!authorizationContexts.contains(AuthorizationContext.ADMIN);
	}

	public Assessment getAssessment() {
		return this.assessment;
	}

	public AssessmentResponse setAssessment(Assessment assessment) {
		this.assessment = assessment;
		return this;
	}

	public void unsetAssessment() {
		this.assessment = null;
	}

	public boolean isSetAssessment() {
		return this.assessment != null;
	}

	public void setAssessmentIsSet(boolean value) {
		if (!value) {
			this.assessment = null;
		}
	}

	public int getRequestContextsSize() {
		return (this.requestContexts == null) ? 0 : this.requestContexts.size();
	}

	public java.util.Iterator<RequestContext> getRequestContextsIterator() {
		return (this.requestContexts == null) ? null : this.requestContexts.iterator();
	}

	public void addToRequestContexts(RequestContext elem) {
		if (this.requestContexts == null) {
			this.requestContexts = new HashSet<RequestContext>();
		}
		this.requestContexts.add(elem);
	}

	public Set<RequestContext> getRequestContexts() {
		return this.requestContexts;
	}

	public AssessmentResponse setRequestContexts(Set<RequestContext> requestContexts) {
		this.requestContexts = requestContexts;
		return this;
	}

	public void unsetRequestContexts() {
		this.requestContexts = null;
	}

	public boolean isSetRequestContexts() {
		return this.requestContexts != null;
	}

	public void setRequestContextsIsSet(boolean value) {
		if (!value) {
			this.requestContexts = null;
		}
	}

	public int getAuthorizationContextsSize() {
		return (this.authorizationContexts == null) ? 0 : this.authorizationContexts.size();
	}

	public java.util.Iterator<AuthorizationContext> getAuthorizationContextsIterator() {
		return (this.authorizationContexts == null) ? null : this.authorizationContexts.iterator();
	}

	public void addToAuthorizationContexts(AuthorizationContext elem) {
		if (this.authorizationContexts == null) {
			this.authorizationContexts = new HashSet<AuthorizationContext>();
		}
		this.authorizationContexts.add(elem);
	}

	public Set<AuthorizationContext> getAuthorizationContexts() {
		return this.authorizationContexts;
	}

	public AssessmentResponse setAuthorizationContexts(Set<AuthorizationContext> authorizationContexts) {
		this.authorizationContexts = authorizationContexts;
		return this;
	}

	public void unsetAuthorizationContexts() {
		this.authorizationContexts = null;
	}

	public boolean isSetAuthorizationContexts() {
		return this.authorizationContexts != null;
	}

	public void setAuthorizationContextsIsSet(boolean value) {
		if (!value) {
			this.authorizationContexts = null;
		}
	}

	public Attempt getLatestAttempt() {
		return this.latestAttempt;
	}

	public AssessmentResponse setLatestAttempt(Attempt latestAttempt) {
		this.latestAttempt = latestAttempt;
		return this;
	}

	public void unsetLatestAttempt() {
		this.latestAttempt = null;
	}

	public boolean isSetLatestAttempt() {
		return this.latestAttempt != null;
	}

	public void setLatestAttemptIsSet(boolean value) {
		if (!value) {
			this.latestAttempt = null;
		}
	}

	public Attempt getRequestedAttempt() {
		return this.requestedAttempt;
	}

	public AssessmentResponse setRequestedAttempt(Attempt requestedAttempt) {
		this.requestedAttempt = requestedAttempt;
		return this;
	}

	public void unsetRequestedAttempt() {
		this.requestedAttempt = null;
	}

	public boolean isSetRequestedAttempt() {
		return this.requestedAttempt != null;
	}

	public void setRequestedAttemptIsSet(boolean value) {
		if (!value) {
			this.requestedAttempt = null;
		}
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof AssessmentResponse)
			return this.equals((AssessmentResponse) that);
		return false;
	}

	private boolean equals(AssessmentResponse that) {
		if (that == null)
			return false;

		boolean this_present_assessment = true && this.isSetAssessment();
		boolean that_present_assessment = true && that.isSetAssessment();
		if (this_present_assessment || that_present_assessment) {
			if (!(this_present_assessment && that_present_assessment))
				return false;
			if (!this.assessment.equals(that.assessment))
				return false;
		}

		boolean this_present_requestContexts = true && this.isSetRequestContexts();
		boolean that_present_requestContexts = true && that.isSetRequestContexts();
		if (this_present_requestContexts || that_present_requestContexts) {
			if (!(this_present_requestContexts && that_present_requestContexts))
				return false;
			if (!this.requestContexts.equals(that.requestContexts))
				return false;
		}

		boolean this_present_authorizationContexts = true && this.isSetAuthorizationContexts();
		boolean that_present_authorizationContexts = true && that.isSetAuthorizationContexts();
		if (this_present_authorizationContexts || that_present_authorizationContexts) {
			if (!(this_present_authorizationContexts && that_present_authorizationContexts))
				return false;
			if (!this.authorizationContexts.equals(that.authorizationContexts))
				return false;
		}

		boolean this_present_latestAttempt = true && this.isSetLatestAttempt();
		boolean that_present_latestAttempt = true && that.isSetLatestAttempt();
		if (this_present_latestAttempt || that_present_latestAttempt) {
			if (!(this_present_latestAttempt && that_present_latestAttempt))
				return false;
			if (!this.latestAttempt.equals(that.latestAttempt))
				return false;
		}

		boolean this_present_requestedAttempt = true && this.isSetRequestedAttempt();
		boolean that_present_requestedAttempt = true && that.isSetRequestedAttempt();
		if (this_present_requestedAttempt || that_present_requestedAttempt) {
			if (!(this_present_requestedAttempt && that_present_requestedAttempt))
				return false;
			if (!this.requestedAttempt.equals(that.requestedAttempt))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_assessment = true && (isSetAssessment());
		builder.append(present_assessment);
		if (present_assessment)
			builder.append(assessment);

		boolean present_requestContexts = true && (isSetRequestContexts());
		builder.append(present_requestContexts);
		if (present_requestContexts)
			builder.append(requestContexts);

		boolean present_authorizationContexts = true && (isSetAuthorizationContexts());
		builder.append(present_authorizationContexts);
		if (present_authorizationContexts)
			builder.append(authorizationContexts);

		boolean present_latestAttempt = true && (isSetLatestAttempt());
		builder.append(present_latestAttempt);
		if (present_latestAttempt)
			builder.append(latestAttempt);

		boolean present_requestedAttempt = true && (isSetRequestedAttempt());
		builder.append(present_requestedAttempt);
		if (present_requestedAttempt)
			builder.append(requestedAttempt);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("AssessmentResponse(");
		boolean first = true;

		sb.append("assessment:");
		if (this.assessment == null) {
			sb.append("null");
		} else {
			sb.append(this.assessment);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("requestContexts:");
		if (this.requestContexts == null) {
			sb.append("null");
		} else {
			sb.append(this.requestContexts);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("authorizationContexts:");
		if (this.authorizationContexts == null) {
			sb.append("null");
		} else {
			sb.append(this.authorizationContexts);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("latestAttempt:");
		if (this.latestAttempt == null) {
			sb.append("null");
		} else {
			sb.append(this.latestAttempt);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("requestedAttempt:");
		if (this.requestedAttempt == null) {
			sb.append("null");
		} else {
			sb.append(this.requestedAttempt);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}
}


package com.workmarket.thrift.work;

import com.google.common.collect.Sets;
import com.workmarket.domains.model.rating.Rating;
import com.workmarket.domains.work.service.resource.WorkAuthorizationResponse;
import com.workmarket.service.business.dto.WorkBundleDTO;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;

import java.io.Serializable;
import java.util.Set;

public class WorkResponse implements Serializable {

	private static final long serialVersionUID = -1087547762211293023L;

	private Work work;
	private boolean workBundle = false;
	private boolean inWorkBundle = false;
	private WorkBundleDTO workBundleParent;
	private Set<RequestContext> requestContexts;
	private Set<AuthorizationContext> authorizationContexts;
	private Set<WorkAuthorizationResponse> workAuthorizationResponses;
	private Resource viewingResource;
	private Rating buyerRatingForWork;
	private Rating resourceRatingForWork;
	private String lastRatingBuyerFullName;
	private Negotiation buyerRescheduleNegotiation;
	private WorkMilestones workMilestones;

	public WorkResponse() {
		this.setWorkAuthorizationResponses(Sets.<WorkAuthorizationResponse>newHashSet());
	}

	public WorkResponse(Work work) {
		this();
		this.work = work;
	}

	public WorkResponse(
		Work work,
		Set<RequestContext> requestContexts,
		Set<AuthorizationContext> authorizationContexts,
		Resource viewingResource,
		Rating buyerRatingForWork,
		Rating resourceRatingForWork,
		Negotiation buyerRescheduleNegotiation,
		WorkMilestones workMilestones) {
		this();
		this.work = work;
		this.requestContexts = requestContexts;
		this.authorizationContexts = authorizationContexts;
		this.viewingResource = viewingResource;
		this.buyerRatingForWork = buyerRatingForWork;
		this.resourceRatingForWork = resourceRatingForWork;
		this.buyerRescheduleNegotiation = buyerRescheduleNegotiation;
		this.workMilestones = workMilestones;
	}

	public boolean isWorkBundle() {
		return workBundle;
	}

	public void setWorkBundle(boolean workBundle) {
		this.workBundle = workBundle;
	}

	public boolean isInWorkBundle() {
		return inWorkBundle;
	}

	public void setInWorkBundle(boolean inWorkBundle) {
		this.inWorkBundle = inWorkBundle;
	}

	public WorkBundleDTO getWorkBundleParent() {
		return workBundleParent;
	}

	public void setWorkBundleParent(WorkBundleDTO workBundleParent) {
		this.workBundleParent = workBundleParent;
	}

	public Work getWork() {
		return this.work;
	}

	public WorkResponse setWork(Work work) {
		this.work = work;
		return this;
	}

	public boolean isSetWork() {
		return this.work != null;
	}

	public Set<RequestContext> getRequestContexts() {
		return this.requestContexts;
	}

	public WorkResponse setRequestContexts(Set<RequestContext> requestContexts) {
		this.requestContexts = requestContexts;
		return this;
	}

	public boolean isSetRequestContexts() {
		return this.requestContexts != null;
	}

	public Set<AuthorizationContext> getAuthorizationContexts() {
		return this.authorizationContexts;
	}

	public WorkResponse setAuthorizationContexts(Set<AuthorizationContext> authorizationContexts) {
		this.authorizationContexts = authorizationContexts;
		return this;
	}

	public boolean isSetAuthorizationContexts() {
		return this.authorizationContexts != null;
	}

	public Resource getViewingResource() {
		return this.viewingResource;
	}

	public WorkResponse setViewingResource(Resource viewingResource) {
		this.viewingResource = viewingResource;
		return this;
	}

	public boolean isSetViewingResource() {
		return this.viewingResource != null;
	}

	public Rating getBuyerRatingForWork() {
		return this.buyerRatingForWork;
	}

	public WorkResponse setBuyerRatingForWork(Rating buyerRatingForWork) {
		this.buyerRatingForWork = buyerRatingForWork;
		return this;
	}

	public boolean isSetBuyerRatingForWork() {
		return this.buyerRatingForWork != null;
	}

	public Rating getResourceRatingForWork() {
		return this.resourceRatingForWork;
	}

	public String getLastRatingBuyerFullName() {
		return lastRatingBuyerFullName;
	}

	public WorkResponse setLastRatingBuyerFullName(String lastRatingBuyerFullName) {
		this.lastRatingBuyerFullName = lastRatingBuyerFullName;
		return this;
	}

	public WorkResponse setResourceRatingForWork(Rating resourceRatingForWork) {
		this.resourceRatingForWork = resourceRatingForWork;
		return this;
	}

	public boolean isSetResourceRatingForWork() {
		return this.resourceRatingForWork != null;
	}

	public Negotiation getBuyerRescheduleNegotiation() {
		return this.buyerRescheduleNegotiation;
	}

	public WorkResponse setBuyerRescheduleNegotiation(Negotiation buyerRescheduleNegotiation) {
		this.buyerRescheduleNegotiation = buyerRescheduleNegotiation;
		return this;
	}

	public boolean isSetBuyerRescheduleNegotiation() {
		return this.buyerRescheduleNegotiation != null;
	}

	public WorkMilestones getWorkMilestones() {
		return this.workMilestones;
	}

	public WorkResponse setWorkMilestones(WorkMilestones workMilestones) {
		this.workMilestones = workMilestones;
		return this;
	}

	public boolean isSetWorkMilestones() {
		return this.workMilestones != null;
	}

	public Set<WorkAuthorizationResponse> getWorkAuthorizationResponses() {
		return workAuthorizationResponses;
	}

	public void setWorkAuthorizationResponses(Set<WorkAuthorizationResponse> workAuthorizationResponses) {
		this.workAuthorizationResponses = workAuthorizationResponses;
	}

	@Override
	public final boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof WorkResponse)) {
			return false;
		}

		WorkResponse that = (WorkResponse) obj;
		return new EqualsBuilder()
			.append(work, that.getWork())
			.append(requestContexts, that.getRequestContexts())
			.append(authorizationContexts, that.getAuthorizationContexts())
			.append(viewingResource, that.getViewingResource())
			.append(buyerRatingForWork, that.getBuyerRatingForWork())
			.append(resourceRatingForWork, that.getResourceRatingForWork())
			.append(buyerRescheduleNegotiation, that.getBuyerRescheduleNegotiation())
			.append(workMilestones, that.getWorkMilestones())
			.isEquals();
	}

	@Override
	public final int hashCode() {
		return new HashCodeBuilder(17, 37)
			.append(work)
			.append(requestContexts)
			.append(authorizationContexts)
			.append(viewingResource)
			.append(buyerRatingForWork)
			.append(resourceRatingForWork)
			.append(buyerRescheduleNegotiation)
			.append(workMilestones)
			.toHashCode();
	}
}

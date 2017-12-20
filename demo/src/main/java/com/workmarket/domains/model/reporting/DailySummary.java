package com.workmarket.domains.model.reporting;

import com.workmarket.domains.model.audit.AuditedEntity;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity(name = "daily_summary")
@Table(name = "daily_summary")
@AuditChanges
public class DailySummary extends AuditedEntity {

	private static final long serialVersionUID = -8982269223896619029L;

	private Integer assignments;
	private Integer routed;
	private Integer completed;
	private Integer assignmentsOnTerms;
	private Integer uniqueBuyers;
	private Integer newUsers;
	private Integer drugTests;
	private Integer backgroundChecks;
	private BigDecimal cashOnPlatform;
	private BigDecimal unspentAp;
	private BigDecimal totalAssignmentCost;
	private BigDecimal totalFees;
	private Integer publicGroups;
	private Integer inviteOnlyGroups;
	private Integer privateGroups;
	private Integer campaigns;
	private Integer invitations;
	private BigDecimal totalMoneyExposedOnTerms;
	private BigDecimal draftsCreated;
	private Integer voidAssignments;
	private Integer cancelledAssignments;
	private Integer paidAssignments;
	private BigDecimal totalRoutedToday;
	private Integer closedAssignments;
	private Integer uniqueRouters;
	private Integer uniqueCreators;
	private Integer newBuyers;
	private BigDecimal averagePriceCreatedAssignments;
	private BigDecimal termsExpired;
	private BigDecimal termsOverdue;

	@Column(name = "assignments")
	public Integer getAssignments() {
		return assignments;
	}

	public void setAssignments(Integer assignments) {
		this.assignments = assignments;
	}

	@Column(name = "routed")
	public Integer getRouted() {
		return routed;
	}

	public void setRouted(Integer routed) {
		this.routed = routed;
	}

	@Column(name = "completed")
	public Integer getCompleted() {
		return completed;
	}

	public void setCompleted(Integer completed) {
		this.completed = completed;
	}

	@Column(name = "assignments_on_terms")
	public Integer getAssignmentsOnTerms() {
		return assignmentsOnTerms;
	}

	public void setAssignmentsOnTerms(Integer assignmentsOnTerms) {
		this.assignmentsOnTerms = assignmentsOnTerms;
	}

	@Column(name = "unique_buyers")
	public Integer getUniqueBuyers() {
		return uniqueBuyers;
	}

	public void setUniqueBuyers(Integer uniqueBuyers) {
		this.uniqueBuyers = uniqueBuyers;
	}

	@Column(name = "new_users")
	public Integer getNewUsers() {
		return newUsers;
	}

	public void setNewUsers(Integer newUsers) {
		this.newUsers = newUsers;
	}

	@Column(name = "drug_tests")
	public Integer getDrugTests() {
		return drugTests;
	}

	public void setDrugTests(Integer drugTests) {
		this.drugTests = drugTests;
	}

	@Column(name = "background_checks")
	public Integer getBackgroundChecks() {
		return backgroundChecks;
	}

	public void setBackgroundChecks(Integer backgroundChecks) {
		this.backgroundChecks = backgroundChecks;
	}

	@Column(name = "unspent_ap")
	public BigDecimal getUnspentAp() {
		return unspentAp;
	}

	public void setUnspentAp(BigDecimal unspentAp) {
		this.unspentAp = unspentAp;
	}

	@Column(name = "total_assignment_cost")
	public BigDecimal getTotalAssignmentCost() {
		return totalAssignmentCost;
	}

	public void setTotalAssignmentCost(BigDecimal totalAssignmentCost) {
		this.totalAssignmentCost = totalAssignmentCost;
	}

	@Column(name = "total_fees")
	public BigDecimal getTotalFees() {
		return totalFees;
	}

	public void setTotalFees(BigDecimal totalFees) {
		this.totalFees = totalFees;
	}

	@Column(name = "public_groups")
	public Integer getPublicGroups() {
		return publicGroups;
	}

	public void setPublicGroups(Integer publicGroups) {
		this.publicGroups = publicGroups;
	}

	@Column(name = "private_groups")
	public Integer getPrivateGroups() {
		return privateGroups;
	}

	public void setPrivateGroups(Integer privateGroups) {
		this.privateGroups = privateGroups;
	}

	@Column(name = "cash_on_platform")
	public BigDecimal getCashOnPlatform() {
		return cashOnPlatform;
	}

	public void setCashOnPlatform(BigDecimal cashOnPlatform) {
		this.cashOnPlatform = cashOnPlatform;
	}

	@Column(name = "invite_only_groups")
	public Integer getInviteOnlyGroups() {
		return inviteOnlyGroups;
	}

	public void setInviteOnlyGroups(Integer inviteOnlyGroups) {
		this.inviteOnlyGroups = inviteOnlyGroups;
	}

	@Column(name = "campaigns")
	public Integer getCampaigns() {
		return campaigns;
	}

	public void setCampaigns(Integer campaigns) {
		this.campaigns = campaigns;
	}

	@Column(name = "invitations")
	public Integer getInvitations() {
		return invitations;
	}

	public void setInvitations(Integer invitations) {
		this.invitations = invitations;
	}

	@Column(name = "total_money_exposed_on_terms")
	public BigDecimal getTotalMoneyExposedOnTerms() {
		return totalMoneyExposedOnTerms;
	}

	public void setTotalMoneyExposedOnTerms(BigDecimal totalMoneyExposedOnTerms) {
		this.totalMoneyExposedOnTerms = totalMoneyExposedOnTerms;
	}

	@Column(name = "drafts_created")
	public BigDecimal getDraftsCreated() {
		return draftsCreated;
	}

	public void setDraftsCreated(BigDecimal draftsCreated) {
		this.draftsCreated = draftsCreated;
	}

	@Column(name = "void_assignments")
	public Integer getVoidAssignments() {
		return voidAssignments;
	}

	public void setVoidAssignments(Integer voidAssignments) {
		this.voidAssignments = voidAssignments;
	}

	@Column(name = "cancelled_assignments")
	public Integer getCancelledAssignments() {
		return cancelledAssignments;
	}

	public void setCancelledAssignments(Integer cancelledAssignments) {
		this.cancelledAssignments = cancelledAssignments;
	}

	@Column(name = "paid_assignments")
	public Integer getPaidAssignments() {
		return paidAssignments;
	}

	public void setPaidAssignments(Integer paidAssignments) {
		this.paidAssignments = paidAssignments;
	}

	@Column(name = "routed_today")
	public BigDecimal getTotalRoutedToday() {
		return totalRoutedToday;
	}

	public void setTotalRoutedToday(BigDecimal totalRoutedToday) {
		this.totalRoutedToday = totalRoutedToday;
	}

	@Column(name = "closed_assignments")
	public Integer getClosedAssignments() {
		return closedAssignments;
	}

	public void setClosedAssignments(Integer closedAssignments) {
		this.closedAssignments = closedAssignments;
	}

	@Column(name = "unique_routers")
	public Integer getUniqueRouters() {
		return uniqueRouters;
	}

	public void setUniqueRouters(Integer uniqueRouters) {
		this.uniqueRouters = uniqueRouters;
	}

	@Column(name = "unique_creators")
	public Integer getUniqueCreators() {
		return uniqueCreators;
	}

	public void setUniqueCreators(Integer uniqueCreators) {
		this.uniqueCreators = uniqueCreators;
	}

	@Column(name = "new_buyers")
	public Integer getNewBuyers() {
		return newBuyers;
	}

	public void setNewBuyers(Integer newBuyers) {
		this.newBuyers = newBuyers;
	}

	@Column(name = "average_created_price")
	public BigDecimal getAveragePriceCreatedAssignments() {
		return averagePriceCreatedAssignments;
	}

	public void setAveragePriceCreatedAssignments(BigDecimal averagePriceCreatedAssignments) {
		this.averagePriceCreatedAssignments = averagePriceCreatedAssignments;
	}

	@Column(name = "terms_expired")
	public BigDecimal getTermsExpired() {
		return termsExpired;
	}

	public void setTermsExpired(BigDecimal termsExpired) {
		this.termsExpired = termsExpired;
	}

	@Column(name = "terms_overdue")
	public BigDecimal getTermsOverdue() {
		return termsOverdue;
	}

	public void setTermsOverdue(BigDecimal termsOverdue) {
		this.termsOverdue = termsOverdue;
	}
}

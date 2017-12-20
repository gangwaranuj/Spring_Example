package com.workmarket.domains.work.model.project;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import com.workmarket.domains.model.User;
import com.workmarket.utility.StringUtilities;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.workmarket.domains.model.ActiveDeletableEntity;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.crm.ClientCompany;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name = "project")
@Table(name = "project")
@AuditChanges
public class Project extends ActiveDeletableEntity {

	private static final long serialVersionUID = 1L;

	private String name;
	private String description;
	private Calendar startDate;
	private Calendar dueDate;
	private BigDecimal expectedRevenue;
	private BigDecimal targetMargin;
	private BigDecimal anticipatedCost;
	private String code;
	private BigDecimal reservedFunds = BigDecimal.ZERO;
	private boolean reservedFundsEnabled;
	private boolean budgetEnabledFlag;
	private BigDecimal budget = BigDecimal.ZERO;
	private BigDecimal remainingBudget = BigDecimal.ZERO;

	private User owner;

	@NotNull
	private Company company;
	@NotNull
	private ClientCompany clientCompany;

	@Column(name = "name", nullable = false, length = 255)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "description", nullable = true)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "due_date", nullable = true)
	public Calendar getDueDate() {
		return dueDate;
	}

	public void setDueDate(Calendar dueDate) {
		this.dueDate = dueDate;
	}

	// Aggregate columns

	@Fetch(FetchMode.JOIN)
	@ManyToOne
	@JoinColumn(name = "owner_user_id", nullable = false)
	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	@Fetch(FetchMode.JOIN)
	@ManyToOne
	@JoinColumn(name = "company_id", nullable = false, updatable = false)
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@Fetch(FetchMode.JOIN)
	@ManyToOne
	@JoinColumn(name = "client_company_id", nullable = false)
	public ClientCompany getClientCompany() {
		return clientCompany;
	}

	public void setClientCompany(ClientCompany clientCompany) {
		this.clientCompany = clientCompany;
	}

	@Column(name = "expected_revenue")
	public BigDecimal getExpectedRevenue() {
		return expectedRevenue;
	}

	public void setExpectedRevenue(BigDecimal expectedRevenue) {
		this.expectedRevenue = expectedRevenue;
	}

	@Column(name = "target_margin")
	public BigDecimal getTargetMargin() {
		return targetMargin;
	}

	public void setTargetMargin(BigDecimal targetMargin) {
		this.targetMargin = targetMargin;
	}

	@Column(name = "anticipated_cost")
	public BigDecimal getAnticipatedCost() {
		return anticipatedCost;
	}

	public void setAnticipatedCost(BigDecimal anticipatedCost) {
		this.anticipatedCost = anticipatedCost;
	}

	@Column(name = "start_date")
	public Calendar getStartDate() {
		return startDate;
	}

	public void setStartDate(Calendar startDate) {
		this.startDate = startDate;
	}

	@Column(name = "code", length = 25)
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Column(name = "reserved_funds", length = 25)
	public BigDecimal getReservedFunds() {
		return reservedFunds;
	}

	public void setReservedFunds(BigDecimal reservedFunds) {
		this.reservedFunds = reservedFunds;
	}

	@Column(name = "enable_reserved_funds")
	public boolean isReservedFundsEnabled() {
		return reservedFundsEnabled;
	}

	public void setReservedFundsEnabled(boolean reservedFundsEnabled) {
		this.reservedFundsEnabled = reservedFundsEnabled;
	}

	@Column(name = "budget_enabled_flag", nullable = false)
	public boolean getBudgetEnabledFlag() {
		return budgetEnabledFlag;
	}

	public void setBudgetEnabledFlag(boolean budgetEnabledFlag) {
		this.budgetEnabledFlag = budgetEnabledFlag;
	}

	@Column(name = "budget", nullable = false)
	public BigDecimal getBudget() {
		return budget;
	}

	public void setBudget(BigDecimal budget) {
		this.budget = budget;
	}

	@Column(name = "remaining_budget", nullable = false)
	public BigDecimal getRemainingBudget() {
		return remainingBudget;
	}

	public void setRemainingBudget(BigDecimal remainingBudget) {
		this.remainingBudget = remainingBudget;
	}

	@Transient
	public String getRemainingBudgetForDisplay() {
		if(getBudgetEnabledFlag()) {
			int remainingBudgetPercentage = 0;
			String remainingBudget = StringUtilities.formatMoneyForDisplay(getRemainingBudget());
			if(BigDecimal.ZERO.compareTo(getBudget()) != 0) {
				remainingBudgetPercentage = (getRemainingBudget().divide(getBudget(), 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100.00)).setScale(0, BigDecimal.ROUND_HALF_UP)).intValue();
			}
			return remainingBudget.concat("(" + remainingBudgetPercentage + "%)");
		} else {
			return "-";
		}
	}


}

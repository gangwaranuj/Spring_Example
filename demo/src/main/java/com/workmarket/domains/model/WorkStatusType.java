package com.workmarket.domains.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Entity(name = "work_status_type")
@Table(name = "work_status_type")
@NamedQueries({
		@NamedQuery(name = "workStatusType.findAllWorkStatusTypesWhereCodeIn", query = "select s from work_status_type s where s.code in (:statusCodes)")
})
@AttributeOverrides({
		@AttributeOverride(name = "code", column = @Column(length = 15))
})
public class WorkStatusType extends LookupEntity {

	private static final long serialVersionUID = 1L;

	@NotNull
	private Boolean clientVisible = Boolean.FALSE;
	@NotNull
	private Boolean employeeVisible = Boolean.FALSE;
	@NotNull
	private Boolean resourceVisible = Boolean.FALSE;

	public static final String ALL = "all";
	public static final String DRAFT = "draft";
	public static final String SENT = "sent"; // routed to workers (previously OPEN)
	public static final String DECLINED = "declined";
	public static final String ACTIVE = "active"; // in progress
	public static final String COMPLETE = "complete"; // work was done by worker, but can be sent back by employee or client, should be EXCEPTION, COMPLETED
	public static final String CANCELLED = "cancelled"; // cancelled with no payment to worker
	public static final String CANCELLED_PAYMENT_PENDING = "cancelledPayPending"; // CANCELLED
	public static final String CANCELLED_WITH_PAY = "cancelledWithPay"; // cancelled, workers paid for portion

	public static final String PAID = "paid";
	public static final String PAYMENT_PENDING = "paymentPending"; // the buyer may close the assignment but is pending to be paid due to payment terms
	public static final String VOID = "void";
	public static final String REFUNDED = "refunded"; // REFUNDED
	public static final String DELETED = "deleted";
	public static final String ABANDONED = "abandoned"; //Worker cancelled
	public static final String DEACTIVATED = "deactivated"; // Inactive Templates

	// "Virtual" status types
	public static final String INPROGRESS = "inprogress";
	public static final String INPROGRESS_PAYMENT_TERMS = "inprogressPTerms";
	public static final String INPROGRESS_PREFUND = "inprogressPrefund";
	public static final String AVAILABLE = "available";
	public static final String SENT_WITH_OPEN_QUESTIONS = "sent_open_question";
	public static final String SENT_WITH_OPEN_NEGOTIATIONS = "sent_open_negotiation";
	public static final String ACTIVE_TODAY = "active_today";
	public static final String APPLIED = "applied";
	//Only used to store the time from sent to start in work_status_transition_history_summary
	public static final String START_TIME = "start";
	public static final String PENDING_MULTI_APPROVALS = "pendingMultiApprovals";

	/**
	 * Not a real status anymore. Only used to filter the assignments that have a sub-status type "alert"
	 */
	public static final String EXCEPTION = "exception";
	public static final String CLOSED = "closed";

	public final static List<String> BUYER_STATUSES_FOR_DISPLAY = ImmutableList.of(
			ALL,
			EXCEPTION,
			DRAFT,
			SENT,
			DECLINED,
			ACTIVE,
			INPROGRESS,
			COMPLETE,
			PAYMENT_PENDING,
			CANCELLED,
			REFUNDED,
			PAID
	);

	public final static List<String> BUYER_SENT_SUB_STATUSES_FOR_DISPLAY = ImmutableList.of(
			SENT_WITH_OPEN_QUESTIONS,
			SENT_WITH_OPEN_NEGOTIATIONS
	);

	public final static List<String> ALL_DASHBOARD_STATUSES_FOR_DISPLAY = ImmutableList.of(
			ALL,
			EXCEPTION,
			DRAFT,
			SENT,
			DECLINED,
			AVAILABLE,
			APPLIED,
			ACTIVE,
			INPROGRESS,
			COMPLETE,
			PAYMENT_PENDING,
			CANCELLED,
			REFUNDED,
			PAID
	);

	public static final List<String> CLOSED_WORK_STATUS_TYPES = ImmutableList.of(
			PAID,
			CANCELLED,
			VOID,
			PAYMENT_PENDING,
			CLOSED,
			CANCELLED_PAYMENT_PENDING
	);

	public static final List<String> CANCELLED_WORK_STATUS_TYPES = ImmutableList.of(
			CANCELLED,
			CANCELLED_PAYMENT_PENDING,
			CANCELLED_WITH_PAY);

	public static final List<String> HIDE_ALERT_WORK_STATUS_TYPES = ImmutableList.of(
			CANCELLED,
			CANCELLED_WITH_PAY,
			DELETED,
			VOID,
			PAID);

	public static final List<String> PAYMENT_PENDING_STATUS_TYPES = ImmutableList.of(
			PAYMENT_PENDING,
			CLOSED,
			CANCELLED_PAYMENT_PENDING);

	public static final List<String> CLOSED_WORK_STATUS_FOR_PENDING_RATING = ImmutableList.of(
			PAID,
			VOID,
			PAYMENT_PENDING,
			CLOSED);

	public static final List<String> ALL_RESOURCE_ASSIGNED_STATUS = ImmutableList.of(
			SENT,
			ACTIVE,
			COMPLETE,
			CANCELLED_PAYMENT_PENDING,
			CANCELLED_WITH_PAY,
			PAID,
			PAYMENT_PENDING,
			ABANDONED);

	public static final List<String> PAID_STATUS_TYPES = ImmutableList.of(
			PAID,
			CANCELLED_WITH_PAY);

	public static final List<String> WORK_STATUSES_FOR_DASHBOARD_ALERT_TAB_AND_FILTER = ImmutableList.of(
			SENT,
			DECLINED,
			ACTIVE,
			COMPLETE);

	public static final List<String> APPROVED_WORK_STATUS_TYPES = ImmutableList.of(
			PAID,
			PAYMENT_PENDING,
			CLOSED,
			CANCELLED,
			CANCELLED_PAYMENT_PENDING,
			CANCELLED_WITH_PAY);

	public static final List<String> SIMPLE_REPORTING_WORK_STATUS_TYPES = ImmutableList.of(
			PAID,
			SENT,
			DECLINED,
			DRAFT,
			REFUNDED,
			VOID,
			COMPLETE);

	public static final List<String> ACTIVE_REPORTING_WORK_STATUS_TYPES = ImmutableList.of(
			ACTIVE,
			INPROGRESS);

	public static final List<String> REPRICE_WORK_VALID_STATUS_TYPES = ImmutableList.of(
		DRAFT,
		SENT,
		DECLINED,
		ACTIVE,
		INPROGRESS,
		COMPLETE);

	public static final List<String> RATING_FINAL_WORK_STATUS_TYPES = ImmutableList.of(
			PAID,
			CANCELLED_WITH_PAY);

	public static final List<String> RATING_SHOWN_WORK_STATUS_TYPES = ImmutableList.of(
			PAID,
			PAYMENT_PENDING,
			CANCELLED_PAYMENT_PENDING,
			CANCELLED_WITH_PAY);

	public static final List<String> OPEN_WORK_STATUS_TYPES = ImmutableList.of(
			SENT,
			ACTIVE,
			COMPLETE,
			PAYMENT_PENDING,
			CANCELLED_PAYMENT_PENDING
	);

	public static final List<String> UNASSIGN_STATUS_TYPES = ImmutableList.of(
		ACTIVE
	);

	public enum ScopeRange {

		DRAFT("Draft", Sets.newHashSet(WorkStatusType.DRAFT)),
		SENT("Sent", Sets.newHashSet(WorkStatusType.SENT)),
		ASSIGNED("Assigned", Sets.newHashSet(WorkStatusType.ACTIVE)),
		SUBMITTED("Submitted", Sets.newHashSet(WorkStatusType.COMPLETE)),
		APPROVED("Approved", Sets.newHashSet(APPROVED_WORK_STATUS_TYPES));

		private final Set<String> workStatusTypeCodes;
		private final String code;

		ScopeRange(String code, Set<String> workStatusTypeCodes) {
			this.workStatusTypeCodes = workStatusTypeCodes;
			this.code = code;
		}

		public Set<String> getWorkStatusTypeCodes() {
			return workStatusTypeCodes;
		}

		public String getCode() {
			return code;
		}

		/**
		 * Returns a {@code Set<String>} of all WorkStatusTypeCodes of any WorkStatusType.ScopeRanges between from and to
		 *
		 * @param from The initial WorkStatusType.ScopeRange
		 * @param to   The final WorkStatusType.ScopeRange
		 * @return A {@code Set<String>} of all WorkStatusTypeCodes of any WorkStatusType.ScopeRanges between from and to
		 */
		public static Set<String> getAllWorkStatusCodesInScopeRange(int from, int to) {
			Set<String> workStatusCodes = Sets.newHashSet();
			ScopeRange[] workStatusScopes = ScopeRange.values();
			if (from >= 0 && from <= to && to < workStatusScopes.length) {
				if (to != from) {
					for (ScopeRange workScopeStatus : Arrays.copyOfRange(workStatusScopes, from, to + 1)) {
						workStatusCodes.addAll(workScopeStatus.getWorkStatusTypeCodes());
					}
				} else {
					workStatusCodes.addAll(workStatusScopes[from].getWorkStatusTypeCodes());
				}
			}
			return workStatusCodes;
		}

		/**
		 * Determines which WorkStatusType.ScopeRanges validate a given set of statuses and returns it as an array of indexes {from,to}
		 *
		 * @param workStatusTypeCodes The status codes you wish to determined
		 * @return An array of Integer containing the from and to indexes in that order
		 */
		public static Integer[] getWorkStatusTypeScopeRangeAsIndex(Set<String> workStatusTypeCodes) {
			int fromRange = 0;
			int toRange = ScopeRange.values().length - 1;
			if (CollectionUtils.isNotEmpty(workStatusTypeCodes)) {
				Iterator<ScopeRange> iterator = Arrays.asList(ScopeRange.values()).iterator();
				while (iterator.hasNext() && toRange != fromRange) {
					if (iterator.next().inRange(workStatusTypeCodes)) {
						toRange = fromRange;
					} else {
						fromRange++;
					}
				}
				while (iterator.hasNext() && iterator.next().inRange(workStatusTypeCodes)) {
					toRange++;
				}
			}
			return new Integer[]{fromRange, toRange};
		}

		/**
		 * Determines whether or not the WorkStatusType.ScopeRange is within the scope of workStatusTypeCodes
		 *
		 * @param workStatusTypeCodes The status codes you wish to determined
		 * @return True if the WorkStatusType.ScopeRange is within the scope of workStatusTypeCodes
		 */
		private boolean inRange(Set<String> workStatusTypeCodes) {
			return workStatusTypeCodes.containsAll(this.getWorkStatusTypeCodes());
		}
	}

	public WorkStatusType() {
		super();
	}

	public WorkStatusType(String code) {
		super(code);
	}

	public WorkStatusType(String code, String description) {
		super(code, description);
	}

	public static WorkStatusType newWorkStatusType(String code) {
		return new WorkStatusType(code);
	}

	@Column(name = "client_visible", nullable = false)
	public Boolean getClientVisible() {
		return clientVisible;
	}

	public void setClientVisible(Boolean clientVisible) {
		this.clientVisible = clientVisible;
	}

	@Column(name = "employee_visible", nullable = false)
	public Boolean getEmployeeVisible() {
		return employeeVisible;
	}

	public void setEmployeeVisible(Boolean employeeVisible) {
		this.employeeVisible = employeeVisible;
	}

	@Column(name = "resource_visible", nullable = false)
	public Boolean getResourceVisible() {
		return resourceVisible;
	}

	public void setResourceVisible(Boolean resourceVisible) {
		this.resourceVisible = resourceVisible;
	}

	@Transient
	public String getEmployeeFormatted() {
		switch(this.code) {
			case WorkStatusType.SENT:
				return "Available";
			case WorkStatusType.COMPLETE:
				return "Complete";
			default:
				return StringUtils.capitalize(this.code);
		}
	}

	@Transient
	public String getEmployerFormatted(boolean isWorkConfirmed, boolean isResourceConfirmationRequired) {
		if (WorkStatusType.ACTIVE.equals(this.code)) {
			if(isWorkConfirmed) {
				return "Confirmed";
			} else if(isResourceConfirmationRequired) {
				return "Unconfirmed";
			} else {
				return "Assigned";
			}
		}

		switch(this.code) {
			case WorkStatusType.COMPLETE:
				return "Pending Approval";
			case WorkStatusType.INPROGRESS:
				return "In Progress";
			case WorkStatusType.PAYMENT_PENDING:
				return "Invoiced";
			default:
				return StringUtils.capitalize(this.code);
		}
	}

	@Transient
	public boolean isVoidOrCancelled() {
		return WorkStatusType.CANCELLED_WORK_STATUS_TYPES.contains(this.getCode()) || this.getCode().equals(WorkStatusType.VOID);
	}

	@Transient
	public boolean isPaid() {
		return WorkStatusType.PAID_STATUS_TYPES.contains(this.getCode());
	}

	@Transient
	public boolean isDraft() {
		return this.getCode().equals(WorkStatusType.DRAFT);
	}

	@Transient
	public boolean isSent() {
		return this.getCode().equals(WorkStatusType.SENT);
	}

	@Transient
	public boolean isComplete() {
		return this.getCode().equals(WorkStatusType.COMPLETE);
	}

}
package com.workmarket.domains.model.changelog.work;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.workmarket.domains.work.model.negotiation.AbstractWorkNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkRescheduleNegotiation;
import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.utility.DateUtilities;

@Entity
@DiscriminatorValue(WorkChangeLog.WORK_NEGOTIATION_REQUESTED)
@AuditChanges
public class WorkNegotiationRequestedChangeLog extends WorkChangeLog {

	private static final long serialVersionUID = -696581359609499843L;
	private AbstractWorkNegotiation negotiation;

	public WorkNegotiationRequestedChangeLog() {}
	public WorkNegotiationRequestedChangeLog(Long work, Long actor, Long masqueradeActor, Long onBehalfOfActor, AbstractWorkNegotiation negotiation) {
		super(work, actor, masqueradeActor, onBehalfOfActor);
		this.negotiation = negotiation;
	}

	@Transient
	public String getDate() {
		String timeZoneId = negotiation.getWork().getTimeZone().getTimeZoneId();
		if (negotiation.getWork().getScheduleRangeFlag() && negotiation.getWork().getScheduleThrough() != null) {
			return String.format("%s to: %s", DateUtilities.formatDateForEmail(negotiation.getWork().getScheduleFrom(), timeZoneId), DateUtilities.formatDateForEmail(negotiation.getWork().getScheduleThrough(), timeZoneId));
		}
		return DateUtilities.formatDateForEmail(negotiation.getWork().getScheduleFrom(), timeZoneId);
	}

	@Transient
	public String getNegotiationDate() {
		WorkNegotiation n = getCounterofferNegotiation();

		if (n == null) return null;

		String timeZoneId = n.getWork().getTimeZone().getTimeZoneId();
		if (n.getScheduleRangeFlag() && n.getScheduleThrough() != null) {
			return String.format("%s to: %s", DateUtilities.formatDateForEmail(n.getScheduleFrom(), timeZoneId), DateUtilities.formatDateForEmail(n.getScheduleThrough(), timeZoneId));
		}
		return DateUtilities.formatDateForEmail(n.getScheduleFrom(), timeZoneId);
	}

	@Transient
	public String getPrice() {
		return (negotiation.getWork().getPricingStrategy() != null ? negotiation.getWork().getPricingStrategy().toString() : "");
	}

	// NOTE Eager fetching here because otherwise Hibernate fails to instantiate the true class,
	// instead always creating objects of the base class.
	@ManyToOne(fetch = FetchType.EAGER, optional = true, cascade = {})
	@JoinColumn(name = "work_negotiation_id")
	public AbstractWorkNegotiation getNegotiation() {
		return negotiation;
	}
	public void setNegotiation(AbstractWorkNegotiation negotiation) {
		this.negotiation = negotiation;
	}

	@Transient
	public static String getDescription() {
		return "Counteroffer requested";
	}

	@Transient
	public WorkNegotiation getCounterofferNegotiation() {
		if (!(negotiation instanceof WorkNegotiation)) {
			return null;
		}

		return (WorkNegotiation)negotiation;
	}

	@Transient
	public boolean isWorkRescheduleNegotiation() {
		return (negotiation instanceof WorkRescheduleNegotiation);
	}

	@Transient
	public boolean isTimeNegotiation() {
		return isWorkRescheduleNegotiation() || (getCounterofferNegotiation() != null
				&& getCounterofferNegotiation().isScheduleNegotiation() && !getCounterofferNegotiation().isPriceNegotiation());
	}
}

package com.workmarket.domains.model.account.pricing;

import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.DeletableEntity;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.*;

/**
 * Author: rocio
 */
@Entity(name = "paymentPeriod")
@Table(name = "payment_period")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("PP")
@AuditChanges
public class PaymentPeriod extends DeletableEntity implements Comparable<PaymentPeriod> {

	private static final long serialVersionUID = 3861792101824410832L;

	private DateRange periodDateRange;

	public PaymentPeriod() {
		super();
		this.periodDateRange = new DateRange();
	}

	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "from", column = @Column(name = "period_start_date", nullable = false)),
			@AttributeOverride(name = "through", column = @Column(name = "period_end_date", nullable = false))
	})
	public DateRange getPeriodDateRange() {
		return periodDateRange;
	}

	public void setPeriodDateRange(DateRange periodDateRange) {
		this.periodDateRange = periodDateRange;
	}

	@Override
	public int compareTo(PaymentPeriod subscriptionPaymentPeriod) {
		if (subscriptionPaymentPeriod == null) {
			return 1;
		}
		return this.getPeriodDateRange().getFrom().compareTo(subscriptionPaymentPeriod.getPeriodDateRange().getFrom());
	}
}

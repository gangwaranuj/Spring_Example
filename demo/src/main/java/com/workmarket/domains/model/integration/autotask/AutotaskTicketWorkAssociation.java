package com.workmarket.domains.model.integration.autotask;

import com.workmarket.domains.model.AbstractEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by nick on 2012-12-24 4:59 PM
 */
@Entity(name="autotaskTicketWorkAssociation")
@Table(name="autotask_ticket_work_association")
public class AutotaskTicketWorkAssociation extends AbstractEntity {

	@NotNull private Long workId; // store id only for performance

	@NotNull AutotaskTicket ticket;

	@Column(name="work_id", nullable = false)
	public Long getWorkId() {
		return workId;
	}

	public AutotaskTicketWorkAssociation setWorkId(Long workId) {
		this.workId = workId;
		return this;
	}

	@Embedded
	public AutotaskTicket getTicket() {
		return ticket;
	}

	public AutotaskTicketWorkAssociation setTicket(AutotaskTicket ticket) {
		this.ticket = ticket;
		return this;
	}
}

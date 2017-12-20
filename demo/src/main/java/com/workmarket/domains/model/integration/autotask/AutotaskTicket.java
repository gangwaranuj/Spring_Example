package com.workmarket.domains.model.integration.autotask;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Created by nick on 2012-12-24 4:57 PM
 */

@Embeddable
public class AutotaskTicket {

	private Long ticketId;
	private String ticketNumber;

	@Column(name="ticket_id", nullable = false)
	public Long getTicketId() {
		return ticketId;
	}

	@Column(name="ticket_number", nullable = true)
	public String getTicketNumber() {
		return ticketNumber;
	}

	public AutotaskTicket setTicketId(Long ticketId) {
		this.ticketId = ticketId;
		return this;
	}

	public AutotaskTicket setTicketNumber(String ticketNumber) {
		this.ticketNumber = ticketNumber;
		return this;
	}
}

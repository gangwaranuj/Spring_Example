package com.workmarket.domains.model.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
* A persona preference is an explicit declaration by a user as to the type of role
* they intend to serve on Work Market, i.e. client, worker or dispatcher. It allows us to better
* target users based on their intention and tune aspects of the application's UI
* to conditionally show or hide relevant data.
*/


@Entity
@Table(name="user_persona_preference")
public class PersonaPreference implements Serializable {
	private Long userId;
	private boolean seller = false;
	private boolean buyer = false;
	private boolean dispatcher = false;

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="user_id", nullable=false)
	public Long getUserId() {
		return userId;
	}
	public PersonaPreference setUserId(Long userId) {
		this.userId = userId;
		return this;
	}

	@Column(name="seller", nullable=false)
	public boolean isSeller() {
		return seller;
	}
	public PersonaPreference setSeller(boolean seller) {
		this.seller = seller;

		if (seller) {
			this.dispatcher = false;
			this.buyer = false;
		}

		return this;
	}

	@Column(name="buyer", nullable=false)
	public boolean isBuyer() {
		return buyer;
	}
	public PersonaPreference setBuyer(boolean buyer) {
		this.buyer = buyer;

		if (buyer) {
			this.dispatcher = false;
			this.seller = false;
		}

		return this;
	}

	@Column(name="dispatcher", nullable=false)
	public boolean isDispatcher() {
		return dispatcher;
	}
	public PersonaPreference setDispatcher(boolean dispatcher) {
		this.dispatcher = dispatcher;

		if (dispatcher) {
			this.buyer = false;
			this.seller = false;
		}

		return this;
	}
}

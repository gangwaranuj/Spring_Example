package com.workmarket.domains.model.postalcode;

import com.workmarket.domains.model.AbstractEntity;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "state")
@Table(name = "state")
@NamedQueries({
		@NamedQuery(name="state.findAll", query="from state where shortName != '' and country_id IN ('CAN', 'USA') and id < 76 order by country.id desc, name asc"),
		@NamedQuery(name="state.findByShortName", query="from state where short_name = :shortName"),
		@NamedQuery(name="state.findByCountry", query="from state where country.id = :code and shortName != '' order by name asc"),
		@NamedQuery(name="state.findByCodeAndCountry", query="from state where country.id = :country and short_name = :shortName order by name asc"),
		@NamedQuery(name="state.findByNameAndCountry", query="from state where country.id = :country and name = :name order by name asc")
})
public class State extends AbstractEntity implements Serializable {

	public static final String PR = "PR";

	private static final long serialVersionUID = 1L;

	private String name;
	private String shortName;
	private Country country;

	@Column(name = "short_name", nullable = false, length = 100)
	public String getShortName() {
		return shortName;
	}

	@Column(name = "name", nullable = false, length = 100)
	public String getName() {
		return name;
	}

	@Fetch(FetchMode.JOIN)
	@ManyToOne
	@JoinColumn(name = "country_id")
	public Country getCountry() {
		return country;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	@Override
	public String toString() {
		return getShortName();
	}

}

package com.workmarket.domains.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity(name="blacklistedEmail")
@Table(name="blacklisted_email")
@NamedQueries({
	@NamedQuery(name="blacklistedEmail.count", query="select count(*) from blacklistedEmail where email = :email"), 
	@NamedQuery(name="blacklistedEmail.delete", query="delete from blacklistedEmail where email = :email")
})
public class BlacklistedEmail extends AbstractEntity {
	
	private static final long serialVersionUID = 1L;
	
	private String email;
	
	public BlacklistedEmail() {}
	public BlacklistedEmail(String email) {
		this.email = email;
	}
	
	@Column(name="email", nullable=false, length=255)
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
}
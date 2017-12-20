package com.workmarket.domains.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.regex.Pattern;

/**
 * Created by nick on 4/21/14 6:49 PM
 */
@Entity(name = "blacklistedDomain")
@Table(name = "blacklisted_domain")
public class BlacklistedDomain {

	private static final long serialVersionUID = 1L;

	private static final String DOMAIN_MATCH_STRING = ".+@%s$";

	@Id
	private String domain;

	public BlacklistedDomain() {}

	public BlacklistedDomain(String domain) {
		this.domain = domain;
	}

	@Column(name = "domain", nullable = false, length = 255)
	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	@Transient
	public boolean isEmailMatch(String email) {
		return Pattern.compile(String.format(DOMAIN_MATCH_STRING, domain)).matcher(email).matches();
	}
}

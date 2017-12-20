package com.workmarket.service.web;

import org.jose4j.jwt.JwtClaims;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by joshlevine on 3/3/17.
 */
public class WebRequestContext implements Serializable {
	private static final long serialVersionUID = 7409137433918489711L;

	private String requestId = "monolith-" + UUID.randomUUID().toString();
	private String userUuid = "monolith";
	private Long companyId = null;
	private String companyUuid = null;
	private String clientIp = "unknown";

	public String getTenant() {
		return tenant;
	}

	public void setTenant(final String tenant) {
		this.tenant = tenant;
	}

	private String tenant = "NOT_SET";
	private long requestStartTime;
	private String jSessionId = "";
	private String userAgent = "";
	private String jwt = "";
	private transient JwtClaims claims = null;


	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getUserUuid() {
		return userUuid;
	}

	public void setUserUuid(String userUuid) {
		this.userUuid = userUuid;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public String getCompanyUuid() {
		return companyUuid;
	}

	public void setCompanyUuid(String companyUuid) {
		this.companyUuid = companyUuid;
	}

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	public long getRequestStartTime() {
		return requestStartTime;
	}

	public void setRequestStartTime(long requestStartTime) {
		this.requestStartTime = requestStartTime;
	}

	public String getjSessionId() {
		return jSessionId;
	}

	public void setjSessionId(String jSessionId) {
		this.jSessionId = jSessionId;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public String getJwt() {
		return jwt;
	}

	public void setJwt(String jwt) {
		this.jwt = jwt;
	}

	public JwtClaims getJwtClaims() {
		return claims;
	}

	public void setJwtClaims(JwtClaims claims) {
		this.claims = claims;
	}
}

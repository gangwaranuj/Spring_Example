package com.workmarket.service.business.dto;

import com.google.common.collect.Lists;
import com.workmarket.dto.AddressDTO;

import java.util.List;

public class AddressVerificationDTO {

	private boolean verified = false;
	private List<String> matches = Lists.newArrayList();
	private List<AddressDTO> componentMatches = Lists.newArrayList();

	public boolean isVerified() {
		return verified;
	}
	public void setVerified(boolean verified) {
		this.verified = verified;
	}

	public List<String> getMatches() {
		return matches;
	}
	public void setMatches(List<String> matches) {
		this.matches = matches;
	}

	public List<AddressDTO> getComponentMatches() {
		return componentMatches;
	}
	public void setComponentMatches(List<AddressDTO> componentMatches) {
		this.componentMatches = componentMatches;
	}

	public boolean hasMatches() {
		return !matches.isEmpty();
	}

}

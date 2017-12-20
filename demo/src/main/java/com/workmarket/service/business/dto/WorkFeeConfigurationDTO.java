package com.workmarket.service.business.dto;

import java.util.List;

public class WorkFeeConfigurationDTO {
	
	List<WorkFeeBandDTO> workFeeBands;

	public List<WorkFeeBandDTO> getWorkFeeBandDTOs() {
		return workFeeBands;
	}

	public void setWorkFeeBandDTOs(List<WorkFeeBandDTO> workFeeBands) {
		this.workFeeBands = workFeeBands;
	}
}

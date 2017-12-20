package com.workmarket.thrift.work;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

public class WorkPublishRequest {

	@NotNull @Size(min = 1)
	private List<String> workNumbers;

	public List<String> getWorkNumbers() {
		return workNumbers;
	}

	public void setWorkNumbers(List<String> workNumbers) {
		this.workNumbers = workNumbers;
	}

}

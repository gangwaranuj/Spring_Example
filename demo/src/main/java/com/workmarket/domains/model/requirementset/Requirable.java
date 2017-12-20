package com.workmarket.domains.model.requirementset;

import java.io.Serializable;

public interface Requirable extends Serializable {
	String getName();
	void setName(String name);
}

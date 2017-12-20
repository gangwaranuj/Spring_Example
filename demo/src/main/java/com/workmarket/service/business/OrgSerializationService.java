package com.workmarket.service.business;

import com.workmarket.business.gen.Messages.OrgUnitPath;

import java.util.List;

public interface OrgSerializationService {

	String toJson(OrgUnitPath orgUnitPath);
	String toJson(List<OrgUnitPath> orgUnitPaths);

}

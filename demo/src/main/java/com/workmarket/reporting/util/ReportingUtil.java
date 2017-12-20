package com.workmarket.reporting.util;

import com.workmarket.domains.model.reporting.Entity;
import com.workmarket.domains.model.reporting.ReportRequestData;
import com.workmarket.reporting.exception.ReportingFormatException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ReportingUtil {

	/**
	 * Instance variables and constants
	 */
	public static final String NEST_DELIMITER = ".";

	/**
	 * @return
	 */
	public static String getRootProperty(String property){
		int index = property.indexOf(NEST_DELIMITER);
		if(index > 0)
			return property.substring(index + 1, property.length());
		else
			return property;
	}

	public static List<Entity> buildSortedEntities(ReportRequestData entityRequest, Map<String, Entity> entityMap) throws ReportingFormatException {

		List<Entity> sortedEntities = new ArrayList<Entity>();

		for (String key : entityRequest.getDisplayKeys()) {
			Entity entity = entityMap.get(key);
			if (entity == null)
				entity = entityRequest.getCallM().get(key);
			if (entity == null){
				continue;
			}
			sortedEntities.add(entity);
		}
		Collections.sort(sortedEntities);

		return sortedEntities;
	}

}
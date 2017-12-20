package com.workmarket.domains.model.requirementset;

import com.workmarket.domains.model.datetime.TimeZone;

import java.util.Collection;

/**
 * Created by ianha on 2/7/14
 */
public interface RequirementSetable {
	Collection<RequirementSet> getRequirementSetCollection();
	TimeZone getRequirementSetableTimeZone();
}

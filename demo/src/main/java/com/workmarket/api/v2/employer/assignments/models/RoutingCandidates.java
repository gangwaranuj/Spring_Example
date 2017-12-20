package com.workmarket.api.v2.employer.assignments.models;

import java.util.Set;

public interface RoutingCandidates {
	Set<Long> getGroupIds();
	Set<String> getResourceNumbers();
	Set<String> getVendorCompanyNumbers();
}

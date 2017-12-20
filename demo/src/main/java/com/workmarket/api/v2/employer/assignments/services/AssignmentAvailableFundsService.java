package com.workmarket.api.v2.employer.assignments.services;

import com.workmarket.api.v2.employer.assignments.models.AvailableFundsApiDTO;
import com.workmarket.thrift.work.WorkActionException;

public interface AssignmentAvailableFundsService {
    AvailableFundsApiDTO get() throws WorkActionException;
}
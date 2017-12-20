package com.workmarket.api.v2.employer.assignments.services;

import com.workmarket.api.v2.employer.assignments.models.AvailableFundsApiDTO;
import com.workmarket.thrift.work.WorkActionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AssignmentAvailableFundsServiceImpl implements AssignmentAvailableFundsService {
    @Autowired
    UseCaseFactory useCaseFactory;

    @Override
    public AvailableFundsApiDTO get() throws WorkActionException {
        return useCaseFactory
                .getUseCase(GetAvailableFundsUseCase.class)
                .execute()
                .handleExceptions()
                .andReturn();
    }
}
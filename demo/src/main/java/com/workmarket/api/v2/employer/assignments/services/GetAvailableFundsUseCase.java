package com.workmarket.api.v2.employer.assignments.services;

import com.workmarket.api.v2.employer.assignments.models.AvailableFundsApiDTO;
import com.workmarket.thrift.work.WorkActionException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class GetAvailableFundsUseCase
        extends AbstractAssignmentUseCase<GetAvailableFundsUseCase, AvailableFundsApiDTO> {

    public GetAvailableFundsUseCase() {}

    @Override
    protected GetAvailableFundsUseCase me() {
        return this;
    }

    @Override
    protected void failFast() {}

    @Override
    protected void init() throws WorkActionException {
        getUser();
    }

    @Override
    protected void prepare() {
        getAvailableFunds();
    }

    @Override
    protected void finish() {
        loadAvailableFundsApiDTO();
    }

    @Override
    public GetAvailableFundsUseCase handleExceptions() throws WorkActionException {
        handleWorkActionException();
        return this;
    }

    @Override
    public AvailableFundsApiDTO andReturn() {
        return availableFundsApiDTOBuilder.build();
    }
}

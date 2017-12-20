package com.workmarket.api.v2.employer.assignments.services;

import com.workmarket.api.v2.employer.assignments.models.ShipmentGroupDTO;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.thrift.work.WorkActionException;

public interface AssignmentShipmentsService {
	ShipmentGroupDTO get(String id) throws WorkActionException;
	ShipmentGroupDTO update(String id, ShipmentGroupDTO shipmentGroupDTO, boolean readyToSend) throws WorkActionException, ValidationException;
}


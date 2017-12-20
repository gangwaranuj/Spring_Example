package com.workmarket.api.v2.employer.search.worker.services;

import com.workmarket.api.v2.employer.search.worker.model.Worker;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;

import java.util.Collection;
import java.util.List;

public interface WorkerHydrator {

	List<Worker> hydrateWorkersByUuids(ExtendedUserDetails currentUser, Collection<String> uuids);

	List<Worker> hydrateWorkersByUserNumbers(ExtendedUserDetails currentUser, Collection<String> userNumbers);
}

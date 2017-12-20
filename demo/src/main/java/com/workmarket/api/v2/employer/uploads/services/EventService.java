package com.workmarket.api.v2.employer.uploads.services;

import com.workmarket.api.v2.employer.uploads.events.UploadEvent;

public interface EventService {
	void emit(UploadEvent event);
}

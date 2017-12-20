package com.workmarket.domains.work.cache;

import com.google.common.base.Optional;
import com.workmarket.service.business.dto.PartDTO;

public interface PartCache {
	Optional<PartDTO> getPart(String uuid);

	PartDTO putPart(PartDTO partDTO);

	void deletePart(String id);

	void updateTrackingStatus(String id, String status);
}

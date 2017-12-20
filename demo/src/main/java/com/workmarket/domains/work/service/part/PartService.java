package com.workmarket.domains.work.service.part;

import com.workmarket.service.business.dto.PartDTO;
import com.workmarket.service.business.dto.PartGroupDTO;

import java.util.List;

public interface PartService {
	// Used by AfterShipController
	void updateTrackingStatus(PartDTO partDTO); // caller doesn't assume mutability semantics on PartDTO

	// used by various Controllers
	PartDTO saveOrUpdatePart(PartDTO partDTO, String uuid); // callers don't assume any mutability semantics on PartDTO
	List<PartDTO> getPartsByGroupUuid(String uuid);
	void deletePart(String uuid);

	// used by thrift stuff
	///  TWorkServiceImpl
	void deletePartGroup(Long workId);
	void saveOrUpdatePartGroup(PartGroupDTO partGroupDTO);
	PartGroupDTO getPartGroupByWorkId(Long workId);

}

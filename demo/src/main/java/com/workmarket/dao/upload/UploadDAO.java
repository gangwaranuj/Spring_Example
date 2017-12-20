package com.workmarket.dao.upload;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.asset.Upload;

public interface UploadDAO extends DAOInterface<Upload> {
	Upload findUploadByUUID(String uuid);
	Upload findUploadByIdOrUUID(Long id, String uuid);
}

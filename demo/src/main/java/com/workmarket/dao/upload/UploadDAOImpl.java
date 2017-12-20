package com.workmarket.dao.upload;

import org.springframework.stereotype.Repository;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.asset.Upload;

@SuppressWarnings("unchecked")
@Repository
public class UploadDAOImpl extends AbstractDAO<Upload> implements UploadDAO {
	protected Class<Upload> getEntityClass() {
		return Upload.class;
	}

	@Override
	public Upload findUploadByUUID(String uuid) {
		return (Upload) getFactory().getCurrentSession()
			.createQuery("select u from upload u where u.UUID = :uuid")
			.setParameter("uuid", uuid).uniqueResult();
	}
	
	@Override
	public Upload findUploadByIdOrUUID(Long id, String uuid) {
		if (id != null)
			return get(id);
		if (uuid != null)
			return findUploadByUUID(uuid);
		return null;
	}
}
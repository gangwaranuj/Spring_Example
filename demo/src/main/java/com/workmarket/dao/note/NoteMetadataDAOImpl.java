package com.workmarket.dao.note;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.note.NoteMetadata;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

/**
 * Author: rocio
 */
@Repository
public class NoteMetadataDAOImpl extends AbstractDAO<NoteMetadata> implements NoteMetadataDAO {

	@Override
	protected Class<NoteMetadata> getEntityClass() {
		return NoteMetadata.class;
	}

	@Override
	public NoteMetadata findByNoteId(long noteId) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass());
		criteria.add(Restrictions.eq("noteId", noteId)).setMaxResults(1);
		return (NoteMetadata)criteria.uniqueResult();
	}
}

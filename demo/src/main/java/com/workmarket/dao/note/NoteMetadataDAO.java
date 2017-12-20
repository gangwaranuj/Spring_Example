package com.workmarket.dao.note;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.note.NoteMetadata;

/**
 * Author: rocio
 */
public interface NoteMetadataDAO extends DAOInterface<NoteMetadata> {

	NoteMetadata findByNoteId(long noteId);


}

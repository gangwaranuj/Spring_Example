package com.workmarket.dao.customfield;

import org.springframework.stereotype.Repository;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.customfield.SavedWorkCustomField;

@Repository
public class SavedWorkCustomFieldDAOImpl extends AbstractDAO<SavedWorkCustomField> implements SavedWorkCustomFieldDAO {

	protected Class<SavedWorkCustomField> getEntityClass() {
		return SavedWorkCustomField.class;
	}

}
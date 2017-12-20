package com.workmarket.dao.assessment;

import org.springframework.stereotype.Repository;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.assessment.Choice;

@Repository
public class ChoiceDAOImpl extends AbstractDAO<Choice> implements ChoiceDAO {
	protected Class<Choice> getEntityClass() {
		return Choice.class;
	}
}

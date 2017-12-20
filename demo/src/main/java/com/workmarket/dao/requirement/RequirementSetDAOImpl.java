package com.workmarket.dao.requirement;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.requirementset.RequirementSet;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Repository
public class RequirementSetDAOImpl extends AbstractDAO<RequirementSet> implements RequirementSetDAO {
	@Override
	protected Class<RequirementSet> getEntityClass() {
		return RequirementSet.class;
	}

	@Override
	public void merge(RequirementSet requirementSet) {
		getFactory().getCurrentSession().merge(requirementSet);
	}

	@Override
	public void save(RequirementSet requirementSet) {
		getFactory().getCurrentSession().save(requirementSet);
	}

	@Override
	public List<RequirementSet> findAllBy(Object... objects) {
		List<RequirementSet> requirementSets = super.findAllBy(objects);
		Collections.sort(requirementSets, new Comparator<RequirementSet>() {
			@Override
			public int compare(final RequirementSet reqSet1, final RequirementSet reqSet2) {
				return reqSet1.getName().compareTo(reqSet2.getName());
			}
		});
		return requirementSets;
	}
}

package com.workmarket.dao.linkedin;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.linkedin.LinkedInEducation;
import com.workmarket.domains.model.linkedin.LinkedInPerson;
import com.workmarket.domains.model.linkedin.LinkedInPhoneNumber;
import com.workmarket.domains.model.linkedin.LinkedInPosition;

@Repository
public class LinkedInDAOImpl extends AbstractDAO<LinkedInPerson> implements LinkedInDAO {
	@Override
	protected Class<LinkedInPerson> getEntityClass() {
		return LinkedInPerson.class;
	}

	@Override
	public LinkedInPerson saveOrUpdateLinkedInPerson(LinkedInPerson linkedInPerson) {
		Assert.notNull(linkedInPerson);
		saveOrUpdate(linkedInPerson);
		return linkedInPerson;
	}

	@Override
	public LinkedInPosition saveOrUpdateLinkedInPosition(LinkedInPosition linkedInPosition) {
		Assert.notNull(linkedInPosition);
		getFactory().getCurrentSession().saveOrUpdate(linkedInPosition);
		return linkedInPosition;
	}

	@Override
	public LinkedInEducation saveOrUpdateLinkedInEducation(LinkedInEducation linkedInEducation) {
		Assert.notNull(linkedInEducation);
		getFactory().getCurrentSession().saveOrUpdate(linkedInEducation);
		return linkedInEducation;
	}

	@Override
	public LinkedInPhoneNumber saveOrUpdateLinkedInPhoneNumber(LinkedInPhoneNumber linkedInPhoneNumber) {
		Assert.notNull(linkedInPhoneNumber);
		getFactory().getCurrentSession().saveOrUpdate(linkedInPhoneNumber);
		return linkedInPhoneNumber;
	}

	private LinkedInPerson findByCriteria(Criteria criteria) {
		LinkedInPerson person = (LinkedInPerson) criteria.uniqueResult();
		if (person != null) {
			Hibernate.initialize(person.getLinkedInEducation());
			Hibernate.initialize(person.getLinkedInPositions());
			Hibernate.initialize(person.getLinkedInPhoneNumbers());
		}
		return person;
	}

	@Override
	public LinkedInPerson findMostRecentLinkedInPerson(Long userId) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(LinkedInPerson.class)
				.add(Restrictions.eq("user.id", userId))
				.add(Restrictions.eq("deleted", false))
				.addOrder(Order.desc("id"))
				.setFirstResult(0)
				.setMaxResults(1);

		return findByCriteria(criteria);
	}

	@Override
	public LinkedInPerson findMostRecentLinkedInPersonByLinkedInId(
		String linkedInId, LinkedInRestriction restriction
	) {
		Criteria criteria = getFactory().getCurrentSession()
			.createCriteria(LinkedInPerson.class)
			.add(Restrictions.eq("linkedInId", linkedInId))
			.addOrder(Order.desc("id"))
			.setFirstResult(0)
			.setMaxResults(1);

		if (restriction == LinkedInRestriction.WITH_USER)
			criteria = criteria.add(Restrictions.isNotNull("user"));

		return findByCriteria(criteria);
	}
}
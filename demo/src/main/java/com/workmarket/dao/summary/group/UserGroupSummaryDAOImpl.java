package com.workmarket.dao.summary.group;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.summary.group.UserGroupSummary;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

/**
 * Created with IntelliJ IDEA.
 * User: ianha
 * Date: 11/24/13
 * Time: 10:03 PM
 */
@Repository
public class UserGroupSummaryDAOImpl extends AbstractDAO<UserGroupSummary> implements UserGroupSummaryDAO {

	@Override
	protected Class<UserGroupSummary> getEntityClass() {
		return UserGroupSummary.class;
	}

	@Override
	public UserGroupSummary findByUserGroup(long userGroupId) {
		Assert.notNull(userGroupId);

		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass());

		return (UserGroupSummary) criteria.add(getRestrictions("userGroup.id", userGroupId)).uniqueResult();
	}

	/**
	 * Method extracted for testability of this class. Mockito has issues
	 * testing static methods like Restrictions.eq().
	 *
	 * @param field
	 * @param value
	 * @return
	 */
	public Criterion getRestrictions(String field, long value) {
		return Restrictions.eq(field, value);
	}
}

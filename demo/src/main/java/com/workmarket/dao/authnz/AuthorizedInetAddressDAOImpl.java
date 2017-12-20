package com.workmarket.dao.authnz;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.workmarket.dao.AbstractDAO;
import com.workmarket.dao.company.CompanyDAO;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.authnz.AuthorizedInetAddress;
import com.workmarket.utility.HibernateUtilities;
import org.hibernate.Criteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Repository
public class AuthorizedInetAddressDAOImpl extends AbstractDAO<AuthorizedInetAddress> implements AuthorizedInetAddressDAO {
	private static final ImmutableList<AuthorizedInetAddress> EMPTY_INET_LIST = ImmutableList.<AuthorizedInetAddress>of();
	@Autowired
	private CompanyDAO companyDAO;

	@Override
	protected Class<?> getEntityClass() {
		return AuthorizedInetAddress.class;
	}

	@Override
	@SuppressWarnings("unchecked") // For the cast at the end
	public Collection<AuthorizedInetAddress> findByCompany(String companyUuid) {
		final Company co = companyDAO.findByUuid(companyUuid);
		final Criteria c = getFactory().getCurrentSession().createCriteria(getEntityClass());
		HibernateUtilities.addRestrictionsEq(c, "deleted", false, "company.id", co.getId());
		return (Collection<AuthorizedInetAddress>) c.list();
	}

	@Override
	public void setForCompany(
			final String companyUuid,
			final Collection<AuthorizedInetAddress> newIps) {
		final Collection<AuthorizedInetAddress> existingIps = findByCompany(companyUuid);

		final Collection<AuthorizedInetAddress> safeExistingIps = existingIps == null ? EMPTY_INET_LIST : existingIps;
		final Collection<AuthorizedInetAddress> safeNewIps = newIps == null ? EMPTY_INET_LIST : newIps;

		final Map<String, AuthorizedInetAddress> existingIpsMap = indexAddrs(safeExistingIps);
		final Map<String, AuthorizedInetAddress> newIpsMap = indexAddrs(safeNewIps);

		final List<AuthorizedInetAddress> toAdd = itemsNotInHash(safeNewIps, existingIpsMap);
		final List<AuthorizedInetAddress> toDel = itemsNotInHash(safeExistingIps, newIpsMap);

		for (final AuthorizedInetAddress addr : toAdd) {
			saveOrUpdate(addr);
		}
		for (final AuthorizedInetAddress addr : toDel) {
			assert(addr.getId() != null);
			addr.setDeleted(true);
			saveOrUpdate(addr);
		}
	}

	private List<AuthorizedInetAddress> itemsNotInHash(
			final Collection<AuthorizedInetAddress> newIps,
			final Map<String, AuthorizedInetAddress> existingIpsMap) {
		final List<AuthorizedInetAddress> difference = Lists.newArrayList();
		for (final AuthorizedInetAddress addr : newIps) {
			if (!existingIpsMap.containsKey(addr.getInetAddress())) {
				difference.add(addr);
			}
		}
		return difference;
	}

	private Map<String, AuthorizedInetAddress> indexAddrs(final Collection<AuthorizedInetAddress> ips) {
		final Map<String, AuthorizedInetAddress> map = Maps.newHashMap();
		for (final AuthorizedInetAddress addr : ips) {
			map.put(addr.getInetAddress(), addr);
		}
		return map;
	}
}

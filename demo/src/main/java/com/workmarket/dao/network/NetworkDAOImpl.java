package com.workmarket.dao.network;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.network.Network;
import com.workmarket.utility.sql.SQLBuilder;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class NetworkDAOImpl extends AbstractDAO<Network> implements NetworkDAO {
	@Autowired @Resource(name = "readOnlyJdbcTemplate") private NamedParameterJdbcTemplate jdbcTemplate;

	protected Class<Network> getEntityClass() {
		return Network.class;
	}

	@Override
	public List<Network> findAllActiveNetworks(Long companyId) {
		Assert.notNull(companyId);

		return getFactory().getCurrentSession()
			.getNamedQuery("network.findActiveNetworksByCompany")
			.setParameter("companyId", companyId)
			.list();
	}

	@Override
	public void softDelete(Long networkId) {
		Assert.notNull(networkId);
		Query updateQuery = getFactory().getCurrentSession()
			.getNamedQuery("network.softDeleteNetworks")
			.setParameter("networkId", networkId);

		updateQuery.executeUpdate();
	}

	@Override
	public boolean isProfileViewableViaGroupSharing(Long companyId, Long userId) {
		SQLBuilder builder = new SQLBuilder();

		// Viewable if user belongs to a group that shares a network with companyId
		builder
			.addColumn("uuga.user_id")
			.addTable("company_network_association cna")
			.addJoin("inner join network n on n.id = cna.network_id")
			.addJoin("inner join user_group_network_association ugna on ugna.network_id = n.id")
			.addJoin("inner join user_group ug on ug.id = ugna.user_group_id")
			.addJoin("inner join user_user_group_association uuga on uuga.user_group_id = ug.id")
			.addWhereClause("cna.company_id = :companyId and uuga.user_id = :userId")
			.addParam("companyId", companyId)
			.addParam("userId", userId);

		List<Long> usersWithCompanyInNetwork = jdbcTemplate.queryForList(builder.build(), builder.getParams(), Long.class);

		return CollectionUtils.isNotEmpty(usersWithCompanyInNetwork);
	}
}

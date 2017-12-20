package com.workmarket.dao.network;

import com.google.api.client.util.Maps;
import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.model.network.Network;
import com.workmarket.domains.model.network.UserGroupNetworkAssociation;
import com.workmarket.utility.sql.SQLBuilder;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
public class UserGroupNetworkAssociationDAOImpl extends AbstractDAO<UserGroupNetworkAssociation> implements UserGroupNetworkAssociationDAO {

	@Qualifier("readOnlyJdbcTemplate")
	@Autowired private NamedParameterJdbcTemplate jdbcTemplate;

	protected Class<UserGroupNetworkAssociation> getEntityClass() {
		return UserGroupNetworkAssociation.class;
	}

	@Override
	public UserGroupNetworkAssociation findAssociationByGroupIdAndNetworkId(Long groupId, Long networkId)  {
		Criteria criteria = getFactory().getCurrentSession()
			.createCriteria(UserGroupNetworkAssociation.class)
			.add(Restrictions.eq("userGroup.id", groupId))
			.add(Restrictions.eq("network.id", networkId))
			.setMaxResults(1);

		return (UserGroupNetworkAssociation) criteria.uniqueResult();
	}

	@Override
	public void addGroupToNetwork(UserGroup group, Network network) {
		UserGroupNetworkAssociation ugnAssociation = findAssociationByGroupIdAndNetworkId(group.getId(), network.getId());

		if (ugnAssociation == null) {
			ugnAssociation = new UserGroupNetworkAssociation();
			ugnAssociation.setNetwork(network);
			ugnAssociation.setUserGroup(group);
		}

		ugnAssociation.setDeleted(false);
		ugnAssociation.setActive(true);
		saveOrUpdate(ugnAssociation);
	}

	@Override
	public void removeGroupFromNetwork(Long groupId, Long networkId) {
		Query updateQuery = getFactory().getCurrentSession().getNamedQuery("network.removeGroupFromNetwork")
			.setParameter("networkId", networkId)
			.setParameter("groupId", groupId);

		updateQuery.executeUpdate();
	}

	@Override
	public void removeGroupFromAllNetworks(Long groupId, List<Long> networkIds) {
		Query updateQuery = getFactory().getCurrentSession().getNamedQuery("network.removeGroupFromAllNetworks")
			.setParameter("groupId", groupId)
			.setParameterList("ids", networkIds);

		updateQuery.executeUpdate();
	}

	@Override
	@SuppressWarnings("unchecked")
	public Set<UserGroup> findUserGroupsInNetwork(Long networkId) {
		List<UserGroup> userGroups =  getFactory().getCurrentSession()
			.getNamedQuery("group.findUserGroupsByNetwork")
			.setParameter("networkId", networkId)
			.list();

		return new HashSet<>(userGroups);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Network> findNetworksWhereGroupIsShared(Long groupId) {
		return getFactory().getCurrentSession()
				.getNamedQuery("network.findGroupsNetworks")
				.setParameter("groupId", groupId)
				.list();
	}

	@Override
	public List<Map<String, Long>> getSharedGroupIdsByMemberId(Long memberId) {
		SQLBuilder sqlBuilder = getSharedGroupsByMemberIdSQLBuilder()
			.addWhereClause("uuga.user_id = :workerId")
			.addColumn("DISTINCT na.user_group_id, na.network_id")
			.addParam("workerId", memberId);

		return jdbcTemplate.query(sqlBuilder.build(), sqlBuilder.getParams(), new GroupMemberRowMapper());
	}

	public static SQLBuilder getSharedGroupsByMemberIdSQLBuilder() {
		return new SQLBuilder()
				.addTable("user_user_group_association uuga")
				.addJoin("INNER JOIN user_group_network_association na ON uuga.user_group_id = na.user_group_id AND uuga.deleted = false AND na.deleted = false AND na.active = true")
				.addJoin("INNER JOIN network n ON n.id = na.network_id AND n.deleted = false AND n.active = true")
				.addJoin("INNER JOIN user_group g ON uuga.user_group_id = g.id AND g.deleted = false AND g.active_flag = true");
	}

	private class GroupMemberRowMapper implements RowMapper<Map<String, Long>> {

		final String[] fields = {"user_group_id", "network_id"};

		@Override
		public Map<String, Long> mapRow(ResultSet resultSet, int i) throws SQLException {
			Map<String, Long> row = Maps.newHashMap();
			for (String field : fields) {
				row.put(field, resultSet.getLong(field));
			}
			return row;
		}
	}

}

package com.workmarket.dao.crm;

import com.google.api.client.util.Maps;
import com.google.common.collect.Lists;
import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.crm.ClientCompany;
import com.workmarket.domains.model.crm.ClientCompanyPagination;
import com.workmarket.dto.SuggestionDTO;
import com.workmarket.utility.sql.SQLBuilder;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Hibernate;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Repository
public class ClientCompanyDAOImpl extends AbstractDAO<ClientCompany> implements ClientCompanyDAO {

	@Resource private NamedParameterJdbcTemplate readOnlyJdbcTemplate;

	protected Class<ClientCompany> getEntityClass() {
		return ClientCompany.class;
	}

	@Override
	public ClientCompany findClientCompanyById(long id) {
		ClientCompany clientCompany = (ClientCompany) getFactory().getCurrentSession().get(ClientCompany.class, id);

		if (clientCompany != null) {
			eagerLoad(clientCompany);
		}

		return clientCompany;
	}

	@Override
	public ClientCompany findClientCompanyByIdAndCompany(Long clientCompanyId, Long companyId) {
		ClientCompany clientCompany = (ClientCompany) getFactory().getCurrentSession().createCriteria(getEntityClass())
				.setFetchMode("clientCompany", FetchMode.JOIN)
				.setFetchMode("company", FetchMode.JOIN)
				.add(Restrictions.eq("id", clientCompanyId))
				.add(Restrictions.eq("company.id", companyId))
				.uniqueResult();

		if (clientCompany != null) {
			eagerLoad(clientCompany);
		}

		return clientCompany;
	}

	private void eagerLoad(ClientCompany clientCompany) {
		//beware: this can be really expensive for clients associated with large companies.
		// just use get(ClientCompanyID) unless you really need access to this data
		Hibernate.initialize(clientCompany.getEmailAssociations());
		Hibernate.initialize(clientCompany.getPhoneAssociations());
		Hibernate.initialize(clientCompany.getWebsiteAssociations());
		Hibernate.initialize(clientCompany.getLocations());
		Hibernate.initialize(clientCompany.getContacts());
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<ClientCompany> findClientCompanyByNumberAndCompany(String clientCompanyNumber, Long companyId) {
		return getFactory().getCurrentSession().createCriteria(getEntityClass())
			.setFetchMode("clientCompany", FetchMode.JOIN)
			.setFetchMode("company", FetchMode.JOIN)
			.add(Restrictions.eq("customerId", clientCompanyNumber))
			.add(Restrictions.eq("company.id", companyId))
			.list();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<ClientCompany> findClientCompanyByCompanyId(Long companyId) {
		Assert.notNull(companyId);

		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass());

		criteria.setFetchMode("company", FetchMode.JOIN)
			.add(Restrictions.eq("company.id", companyId))
			.addOrder(Order.asc("name"))
			.add(Restrictions.eq("deleted", false));

		return criteria.list();
	}

	@Override
	@SuppressWarnings("unchecked")
	public ClientCompanyPagination findClientCompanyByCompanyId(Long companyId, ClientCompanyPagination pagination) {
		Assert.notNull(companyId);
		Assert.notNull(pagination);

		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass());
		Criteria count = getFactory().getCurrentSession().createCriteria(getEntityClass());
		count.setProjection(Projections.rowCount());
		criteria.setFirstResult(pagination.getStartRow());
		criteria.setMaxResults(pagination.getResultsLimit());

		criteria.setFetchMode("company", FetchMode.JOIN)
			.createAlias("websiteAssociations", "websiteAssociations", CriteriaSpecification.LEFT_JOIN, Restrictions.eq("websiteAssociations.deleted", false))
			.createAlias("phoneAssociations", "phoneAssociations", CriteriaSpecification.LEFT_JOIN, Restrictions.eq("phoneAssociations.deleted", false))
			.add(Restrictions.eq("company.id", companyId))
			.add(Restrictions.eq("deleted", false))
			.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);

		count.add(Restrictions.eq("company.id", companyId))
			.add(Restrictions.eq("deleted", false));

		String sortColumnName = "name";
		if (ClientCompanyPagination.SORTS.NAME.toString().equals(pagination.getSortColumn())) {
			sortColumnName = "name";
		} else if (ClientCompanyPagination.SORTS.CUSTOMER_ID.toString().equals(pagination.getSortColumn())) {
			sortColumnName = "customerId";
		}

		if (Pagination.SORT_DIRECTION.DESC.equals(pagination.getSortDirection())) {
			criteria.addOrder(Order.desc(sortColumnName));
		} else {
			criteria.addOrder(Order.asc(sortColumnName));
		}

		pagination.setResults(criteria.list());

		if (count.list().size() > 0)
			pagination.setRowCount(((Long) count.list().get(0)).intValue());
		else
			pagination.setRowCount(0);

		return pagination;
	}

	@Override
	public List<Map<String, Object>> findAllClientCompaniesByCompanyId(Long companyId, final String... columnNames) {
		SQLBuilder sqlBuilder = new SQLBuilder();
		sqlBuilder
			.addColumns(columnNames)
			.addTable("client_company")
			.addWhereClause("deleted = false")
			.addWhereClause("company_id = :companyId");

		return readOnlyJdbcTemplate.query(sqlBuilder.build(), new MapSqlParameterSource("companyId", companyId), new ClientCompanyRowMapper(columnNames));
	}

	@Override
	public List<Map<String, Object>> findAllClientCompaniesByCompanyWithLocationCount(Long companyId) {
		SQLBuilder sqlBuilder = new SQLBuilder();
		sqlBuilder
			.addColumns("cc.id", "cc.name", "COALESCE(COUNT(l.client_company_id), 0) as locationCount")
			.addTable("client_company cc")
			.addJoin("left join location l on cc.id = l.client_company_id and l.deleted = 0")
			.addWhereClause("cc.company_id = :companyId")
			.addWhereClause("cc.deleted = 0")
			.addGroupColumns("cc.id")
			.addOrderBy("locationCount", "desc")
			.addParam("companyId", companyId);

		final String[] columnNames = {"id", "name", "locationCount"};

		return readOnlyJdbcTemplate.query(sqlBuilder.build(), new MapSqlParameterSource("companyId", companyId), new ClientCompanyRowMapper(columnNames));
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<SuggestionDTO> suggest(String prefix, String property, Long companyId) {
		if (companyId == null)
			return Lists.newArrayList();

		return getFactory().getCurrentSession().createCriteria(getEntityClass())
			.add(Restrictions.ilike(property, prefix, MatchMode.START))
			.add(Restrictions.eq("company.id", companyId))
			.add(Restrictions.eq("deleted", false))
			.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
			.setMaxResults(10)
			.setProjection(Projections.projectionList()
				.add(Projections.property("id"), "id")
				.add(Projections.property(property), "value")
			)
			.setResultTransformer(Transformers.aliasToBean(SuggestionDTO.class))
			.list();
	}

	@Override
	public ClientCompany findClientCompanyByName(long companyId, String name) {
		return (ClientCompany) getFactory().getCurrentSession().createCriteria(getEntityClass())
			.add(Restrictions.eq("company.id", companyId))
			.add(Restrictions.ilike("name", name, MatchMode.EXACT))
			.setFetchMode("websiteAssociations", FetchMode.JOIN)
			.setFetchMode("phoneAssociations", FetchMode.JOIN)
			.setMaxResults(1)
			.uniqueResult();
	}

	private class ClientCompanyRowMapper implements RowMapper<Map<String, Object>> {

		private final String[] columnNames;

		public ClientCompanyRowMapper(final String[] columnNames) {
			this.columnNames = columnNames;
		}

		@Override
		public Map<String, Object> mapRow(ResultSet rs, int line) throws SQLException {
			Map<String, Object> row = Maps.newHashMap();
			for (String column : columnNames) {
				row.put(column, rs.getObject(column));
			}
			return row;
		}
	}

}

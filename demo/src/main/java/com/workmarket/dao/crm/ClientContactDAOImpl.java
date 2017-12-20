package com.workmarket.dao.crm;

import com.google.common.collect.ImmutableMap;
import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.crm.ClientCompany;
import com.workmarket.domains.model.crm.ClientContact;
import com.workmarket.domains.model.crm.ClientContactPagination;
import com.workmarket.domains.model.directory.ContactContextType;
import com.workmarket.domains.model.directory.Email;
import com.workmarket.domains.model.directory.Phone;
import com.workmarket.domains.model.directory.Website;
import com.workmarket.utility.sql.SQLBuilder;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Hibernate;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Repository
public class ClientContactDAOImpl extends AbstractDAO<ClientContact> implements ClientContactDAO {

	@Autowired @Qualifier("readOnlyJdbcTemplate") private NamedParameterJdbcTemplate jdbcTemplate;

	protected Class<ClientContact> getEntityClass() {
		return ClientContact.class;
	}

	@Override
	public ClientContact findContactById(Long id) {
		Assert.notNull(id);
		return (ClientContact) getFactory().getCurrentSession().createCriteria(getEntityClass())
				.setFetchMode("clientLocation", FetchMode.JOIN)
				.setFetchMode("locationAssociations", FetchMode.JOIN)
				.setFetchMode("emailAssociations", FetchMode.JOIN)
				.setFetchMode("phoneAssociations", FetchMode.JOIN)
				.setFetchMode("websiteAssociations", FetchMode.JOIN)
				.add(Restrictions.eq("id", id)).uniqueResult();
	}

	@Override
	public ClientContact findClientCompanyByIdAndCompany(Long companyId, Long contactId) {
		ClientContact contact = (ClientContact) getFactory().getCurrentSession().createCriteria(getEntityClass())
				.setFetchMode("clientLocation", FetchMode.JOIN)
				.add(Restrictions.eq("id", contactId))
				.add(Restrictions.eq("company.id", companyId))
				.uniqueResult();

		if (contact != null) {
			Hibernate.initialize(contact.getEmailAssociations());
			Hibernate.initialize(contact.getPhoneAssociations());
			Hibernate.initialize(contact.getWebsiteAssociations());
		}

		return contact;
	}

	@Override
	public ClientContact findClientContactByClientLocationAndName(Long clientLocationId, String firstName, String lastName) {
		Assert.notNull(clientLocationId);
		Assert.hasText(firstName);
		Assert.hasText(lastName);
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.createAlias("locationAssociations", "locationAssociation")
				.createAlias("locationAssociation.clientLocation", "clientLocationAssoc")
				.add(Restrictions.eq("clientLocationAssoc.id", clientLocationId))
				.add(Restrictions.ilike("firstName", firstName, MatchMode.EXACT))
				.add(Restrictions.ilike("lastName", lastName, MatchMode.EXACT))
				.add(Restrictions.eq("deleted", false))
				.setMaxResults(1);

		return (ClientContact) criteria.uniqueResult();
	}

	@Override
	public ClientContact findClientContactByClientLocationNamePhone(Long clientLocationId, String firstName, String lastName, String phone, String extension) {
		Assert.notNull(clientLocationId);
		Assert.hasText(firstName);
		Assert.hasText(lastName);
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
			.createAlias("locationAssociations", "locationAssociation")
			.createAlias("locationAssociation.clientLocation", "clientLocationAssoc")
			.createAlias("phoneAssociations", "phoneAssociation")
			.createAlias("phoneAssociation.phone", "phone")
			.add(Restrictions.eq("clientLocationAssoc.id", clientLocationId))
			.add(Restrictions.ilike("firstName", firstName, MatchMode.EXACT))
			.add(Restrictions.ilike("lastName", lastName, MatchMode.EXACT))
			.add(Restrictions.eq("deleted", false))
			.add(Restrictions.eq("phone.phone", phone))
			.setMaxResults(1);
		if (StringUtils.isNotBlank(extension)) {
			criteria.add(Restrictions.eq("phone.extension", extension));
		}
		return (ClientContact) criteria.uniqueResult();
	}

	@Override
	public ClientContact findClientContactByCompanyIdAndName(Long companyId, String firstName, String lastName) {
		Assert.notNull(companyId);
		Assert.hasText(firstName);
		Assert.hasText(lastName);
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass());
		criteria.add(Restrictions.eq("company.id", companyId))
				.add(Restrictions.isNull("clientLocation.id"))
				.add(Restrictions.ilike("firstName", firstName, MatchMode.EXACT))
				.add(Restrictions.ilike("lastName", lastName, MatchMode.EXACT))
				.add(Restrictions.eq("deleted", false))
				.setMaxResults(1);

		return (ClientContact) criteria.uniqueResult();
	}

	@Override
	public ClientContact findClientContactByCompanyIdNamePhone(Long companyId, String firstName, String lastName, String phone, String extension) {
		Assert.notNull(companyId);
		Assert.hasText(firstName);
		Assert.hasText(lastName);
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
			.createAlias("phoneAssociations", "phoneAssociation")
			.createAlias("phoneAssociation.phone", "phone")
			.add(Restrictions.eq("company.id", companyId))
			.add(Restrictions.isNull("clientLocation.id"))
			.add(Restrictions.ilike("firstName", firstName, MatchMode.EXACT))
			.add(Restrictions.ilike("lastName", lastName, MatchMode.EXACT))
			.add(Restrictions.eq("deleted", false))
			.add(Restrictions.eq("phone.phone", phone))
			.setMaxResults(1);
		if (StringUtils.isNotBlank(extension)) {
			criteria.add(Restrictions.eq("phone.extension", extension));
		}

		return (ClientContact) criteria.uniqueResult();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<ClientContact> findClientContactsByCompany(Long companyId) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass());
		return criteria.add(Restrictions.eq("company.id", companyId))
				.add(Restrictions.eq("deleted", false)).list();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<ClientContact> findIndividualClientContactsByClientCompanyId(Long clientCompanyId) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.eq("clientCompany.id", clientCompanyId))
				.add(Restrictions.isNull("clientLocation.id"))
				.add(Restrictions.eq("deleted", false))
				.createAlias("emailAssociations", "emailAssociations", Criteria.LEFT_JOIN)
				.createAlias("phoneAssociations", "phoneAssociations", Criteria.LEFT_JOIN)
				.createAlias("websiteAssociations", "websiteAssociations", Criteria.LEFT_JOIN)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		return criteria.list();
	}

	@Override
	public ClientContactPagination findClientContacts(ClientContactPagination pagination) {
		Assert.notNull(pagination);

		// Build base sql
		SQLBuilder count =
			sqlBuilderForFindClientContacts(pagination)
				.addColumn("COUNT(*) count")
				.addGroupColumns("clientDeleted");
		SQLBuilder results =
			sqlBuilderForFindClientContacts(pagination)
				.addLimitClause(pagination.getStartRow(), pagination.getResultsLimit(), pagination.isLimitMaxRows())
				.addColumn("contact.*")
				.addColumn("cc.name client_company_name")
				.addColumn("cc.id client_company_id");

		// Add sorts
		String orderByColumnName = "first_name";
		if (pagination.hasSortColumn()) {
			if (pagination.getSortColumn().equals(ClientContactPagination.SORTS.CLIENT_COMPANY_NAME.toString())) {
				orderByColumnName = "client_company_name";
			}
		}

		// Add order
		String orderDirection;
		if (pagination.getSortDirection().equals(Pagination.SORT_DIRECTION.DESC)) {
			orderDirection = "DESC";
		} else {
			orderDirection = "ASC";
		}
		results.addOrderBy(orderByColumnName, orderDirection);

		// Add associations
		if (pagination.isWithAssociations()) {
			addAssociations(results);
		}

		List<ClientContact> clientContacts = jdbcTemplate.query(results.build(), results.getParams(), new ClientContactRowMapper());
		List<Long> totalCount = jdbcTemplate.query(count.build(), results.getParams(), new RowMapper<Long>() {
			@Override
			public Long mapRow(ResultSet resultSet, int i) throws SQLException {
				return resultSet.getLong("count");
			}
		});

		Long totalRowCount = 0L;
		if (totalCount.size() > 0) {
			totalRowCount = totalCount.get(0);
		}

		pagination.setResults(clientContacts);
		pagination.setRowCount(totalRowCount);
		return pagination;
	}

//	SELECT COALESCE(cc.deleted, false) clientDeleted, contact.*, cc.name client_company_name, cc.id client_company_id,
//
//	(SELECT email.email
//	FROM client_contact_email_association a
//	INNER JOIN email ON email.id = a.email_id
//	WHERE a.deleted = 0 AND a.client_contact_id  = contact.id
//	ORDER BY a.modified_on DESC LIMIT 1
//	) AS email,
//
//	(SELECT website.website
//	FROM client_contact_website_association a
//	INNER JOIN website ON website.id = a.website_id
//	WHERE a.deleted = 0 AND a.client_contact_id  = contact.id
//	ORDER BY a.modified_on DESC LIMIT 1
//	) AS website,

//	(SELECT phone.phone
//	FROM client_contact_phone_association a
//	INNER JOIN phone ON phone.id = a.phone_id
//	WHERE a.deleted = 0 AND a.client_contact_id  = contact.id AND phone.contact_context_type = 'HOME'
//	ORDER BY a.modified_on DESC LIMIT 1
//	) AS home_phone,
//
//	(SELECT phone.phone
//	FROM client_contact_phone_association a
//	INNER JOIN phone ON phone.id = a.phone_id
//	WHERE a.deleted = 0 AND a.client_contact_id  = contact.id AND phone.contact_context_type = 'WORK'
//	ORDER BY a.modified_on DESC LIMIT 1
//	) AS work_phone,
//	(SELECT phone.extension
//	FROM client_contact_phone_association a
//	INNER JOIN phone ON phone.id = a.phone_id
//	WHERE a.deleted = 0 AND a.client_contact_id  = contact.id AND phone.contact_context_type = 'HOME'
//	ORDER BY a.modified_on DESC LIMIT 1
//	) AS work_phone_extension
//
//	FROM client_contact contact
//	LEFT JOIN client_company cc ON contact.client_company_id = cc.id and contact.company_id = cc.company_id
//
//	WHERE 1=1 AND contact.deleted = false AND contact.company_id = 1
//	HAVING clientDeleted = false ORDER BY first_name ASC LIMIT 0, 50;

	private SQLBuilder sqlBuilderForFindClientContacts(ClientContactPagination pagination) {
		SQLBuilder defaultSql =
			new SQLBuilder()
				.addColumn("COALESCE(cc.deleted, false) clientDeleted")
				.addTable("client_contact contact")
				.addJoin("LEFT JOIN client_company cc ON contact.client_company_id = cc.id and contact.company_id = cc.company_id")
				.addWhereClause("contact.deleted = false")
				.addHavingClause("clientDeleted = false");

		// Add filters
		if (pagination.hasFilters()) {
			if (pagination.hasFilter(ClientContactPagination.FILTER_KEYS.CLIENT_COMPANY_ID)) {
				Long clientCompanyId = Long.parseLong(pagination.getFilter(ClientContactPagination.FILTER_KEYS.CLIENT_COMPANY_ID));
				defaultSql
					.addWhereClause("contact.client_company_id = :clientCompanyId")
					.addParam("clientCompanyId", clientCompanyId);
			}
			if (pagination.hasFilter(ClientContactPagination.FILTER_KEYS.COMPANY_ID)) {
				Long companyId = Long.parseLong(pagination.getFilter(ClientContactPagination.FILTER_KEYS.COMPANY_ID));
				defaultSql
					.addWhereClause("contact.company_id = :companyId")
					.addParam("companyId", companyId);
			}
		}
		return defaultSql;
	}

	private SQLBuilder addAssociations(SQLBuilder sqlBuilder) {
		// Email
		buildAssociationQuery(sqlBuilder, ASSOCIATION_SQL, ImmutableMap.of("contactTypeTable", "email"));
		// Website
		buildAssociationQuery(sqlBuilder, ASSOCIATION_SQL, ImmutableMap.of("contactTypeTable", "website"));
		// Home phone (mobile)
		buildAssociationQuery(
				sqlBuilder,
				ASSOCIATION_SQL_PHONE,
				ImmutableMap.of("alias", "home_phone", "phoneType", ContactContextType.HOME.name(), "attribute", "phone"));
		// Home phone (mobile) extension
		buildAssociationQuery(
				sqlBuilder,
				ASSOCIATION_SQL_PHONE,
				ImmutableMap.of("alias", "home_phone_extension", "phoneType", ContactContextType.HOME.name(), "attribute", "extension"));
		// Work phone
		buildAssociationQuery(
				sqlBuilder,
				ASSOCIATION_SQL_PHONE,
				ImmutableMap.of("alias", "work_phone", "phoneType", ContactContextType.WORK.name(), "attribute", "phone"));
		// Work phone extension
		buildAssociationQuery(
				sqlBuilder,
				ASSOCIATION_SQL_PHONE,
				ImmutableMap.of("alias", "work_phone_extension", "phoneType", ContactContextType.WORK.name(), "attribute", "extension"));

		return sqlBuilder;
	}

	// Retrieves the most recently modified contact association
	private void buildAssociationQuery(SQLBuilder sqlBuilder, String associationSQL, Map<String, String> queryParams) {
		StrSubstitutor substitutor = new StrSubstitutor(queryParams);
		String query = substitutor.replace(associationSQL);
		sqlBuilder.addColumn(query);
	}

	private static final String ASSOCIATION_SQL =
		"(SELECT ${contactTypeTable}.${contactTypeTable} \n" +
		"FROM client_contact_${contactTypeTable}_association a \n" +
		"INNER JOIN ${contactTypeTable} ON ${contactTypeTable}.id = a.${contactTypeTable}_id \n" +
		"WHERE a.deleted = 0 AND a.client_contact_id  = contact.id \n" +
		"ORDER BY a.modified_on DESC LIMIT 1 \n" +
		") AS ${contactTypeTable} \n";

	private static final String ASSOCIATION_SQL_PHONE =
		"(SELECT phone.${attribute} \n" +
		"FROM client_contact_phone_association a \n" +
		"INNER JOIN phone ON phone.id = a.phone_id \n" +
		"WHERE a.deleted = 0 AND a.client_contact_id  = contact.id AND phone.contact_context_type = '${phoneType}' \n" +
		"ORDER BY a.modified_on DESC LIMIT 1 \n" +
		") AS ${alias} \n";

	private class ClientContactRowMapper implements RowMapper<ClientContact> {
		public ClientContact mapRow(ResultSet rs, int rowNum) throws SQLException {
			ClientContact contact = new ClientContact();

			// Basic info
			contact.setId(rs.getLong("id"));
			contact.setFirstName(rs.getString("first_name"));
			contact.setLastName(rs.getString("last_name"));
			contact.setJobTitle(rs.getString("job_title"));
			contact.setPrimary(rs.getBoolean("primary_flag"));
			contact.setManager(rs.getBoolean("manager_flag"));

			// Contact info
			String mobilePhone = rs.getString("home_phone");
			if (mobilePhone != null) {
				contact.setMostRecentMobilePhone(
					new Phone(mobilePhone, rs.getString("home_phone_extension"), null));
			}

			String workPhone = rs.getString("work_phone");
			if (workPhone != null) {
				contact.setMostRecentWorkPhone(
					new Phone(workPhone, rs.getString("work_phone_extension"), null));
			}

			String email = rs.getString("email");
			if (email != null) {
				contact.setMostRecentEmail(new Email(email, null));
			}

			String website = rs.getString("website");
			if (website != null) {
				 contact.setMostRecentWebsite(new Website(website, null));
			}

			// Client company
			long clientCompanyId = rs.getLong("client_company_id");
			if (clientCompanyId > 0) {
				ClientCompany clientCompany = new ClientCompany();
				clientCompany.setId(clientCompanyId);
				clientCompany.setName(rs.getString("client_company_name"));
				contact.setClientCompany(clientCompany);
			}

			return contact;
		}
	}

}

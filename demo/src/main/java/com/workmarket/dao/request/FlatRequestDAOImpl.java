package com.workmarket.dao.request;

import com.google.common.collect.ImmutableList;
import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.groups.model.UserUserGroupAssociation;
import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.VerificationStatus;
import com.workmarket.domains.model.request.*;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.List;

@SuppressWarnings("unchecked")
@Repository
public class FlatRequestDAOImpl extends AbstractDAO<FlatRequest> implements FlatRequestDAO {
	protected Class<FlatRequest> getEntityClass() {
		return FlatRequest.class;
	}

}

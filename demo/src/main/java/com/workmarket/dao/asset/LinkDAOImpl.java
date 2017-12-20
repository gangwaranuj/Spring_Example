package com.workmarket.dao.asset;

import com.workmarket.dao.DeletableAbstractDAO;
import com.workmarket.domains.model.asset.Link;
import org.springframework.stereotype.Repository;

@Repository
public class LinkDAOImpl extends DeletableAbstractDAO<Link> implements LinkDAO {

	@Override
	protected Class<Link> getEntityClass() {
		return Link.class;
	}
}

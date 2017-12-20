package com.workmarket.dao;

import com.workmarket.domains.model.LoginInfo;
import org.springframework.stereotype.Repository;

@Repository
@SuppressWarnings("unchecked")
public class LoginInfoDAOImpl extends AbstractDAO<LoginInfo> implements LoginInfoDAO {
	
	protected Class<LoginInfo> getEntityClass() {
		return LoginInfo.class;
	}
}

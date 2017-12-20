package com.workmarket.service.business;

import com.workmarket.dao.LoginInfoDAO;
import com.workmarket.dao.acl.AclRoleDAO;
import com.workmarket.domains.model.LoginInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;

@Service
public class WebActivityAuditServiceImpl implements WebActivityAuditService {

	@Autowired private LoginInfoDAO loginInfoDAO;
	@Autowired private AclRoleDAO aclRoleDAO;

	@Override
	public void saveLoginInfo(Long userId, Calendar date, Long companyId, String inetAddress, boolean successful) {
		LoginInfo loginInfoEntry = new LoginInfo(userId, date, inetAddress);
		loginInfoEntry.setRoleString(aclRoleDAO.getRolesStringbyUser(userId));
		loginInfoEntry.setCompanyId(companyId);
		loginInfoEntry.setSuccessful(successful);
		loginInfoDAO.saveOrUpdate(loginInfoEntry);
	}
}

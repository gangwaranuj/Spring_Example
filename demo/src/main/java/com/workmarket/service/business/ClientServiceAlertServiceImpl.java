package com.workmarket.service.business;

import com.workmarket.dao.clientservice.ClientServiceAlertDAO;
import com.workmarket.domains.model.clientservice.ClientServiceAlert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class ClientServiceAlertServiceImpl implements ClientServiceAlertService {

	@Autowired
	private UserService userService;
	@Autowired
	private ClientServiceAlertDAO clientServiceAlertDAO;

	@Override
	@Transactional(readOnly = false)
	public void saveOrUpdate(ClientServiceAlert alert) {
		clientServiceAlertDAO.saveOrUpdate(alert);
	}

	@Override
	public ClientServiceAlert findById(Long alertId) {
		return clientServiceAlertDAO.findById(alertId);
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}
}

package com.workmarket.web.editors;

import com.workmarket.domains.model.User;
import com.workmarket.service.business.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserEditor extends AbstractEntityEditor<User> {
	@Autowired private UserService userService;

	@Override
	public User getEntity(Long id) {
		return userService.findUserById(id);
	}
}

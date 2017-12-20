package com.workmarket.domains.authentication.services;

import java.util.List;
import java.util.Vector;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.workmarket.dao.UserDAO;

public class InMemoryUserDetailsService implements UserDetailsService, InitializingBean {

	@Autowired private UserDAO userDAO;

	public void afterPropertiesSet() throws Exception {}

	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {

		com.workmarket.domains.model.User workmarketUser = userDAO.findUserByEmail(username);
		List<GrantedAuthority> authorities = new Vector<>();
		GrantedAuthorityImpl grantedAuthority = new GrantedAuthorityImpl("ROLE_USER");
		authorities.add(grantedAuthority);
		UserDetails user = new User(workmarketUser.getEmail(), "", true, true, true, true, authorities);

		return user;
	}
}

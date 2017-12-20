package com.workmarket.test.mock.answer.defaults;

import java.util.ArrayList;
import java.util.List;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.workmarket.domains.authentication.model.ExtendedUserDetails;

public class DefaultExtendedUserDetailsAnswer implements Answer<ExtendedUserDetails> {
	private final List<GrantedAuthority> authorities;
	
	public DefaultExtendedUserDetailsAnswer(List<String> grantedAuthorities) {
		this.authorities = new ArrayList<>();
		for(String ga : grantedAuthorities) {
			authorities.add(new SimpleGrantedAuthority(ga));
		}
	}
	
	@Override
	public ExtendedUserDetails answer(InvocationOnMock invocation) throws Throwable {
		ExtendedUserDetails elmo = new ExtendedUserDetails("elmo", "bigbird", authorities);
		elmo.setCompanyId(1L);
		elmo.setEmail("elmo@sesame-street.com");
		elmo.setCompanyIsLocked(false);
		return elmo;
	}

}

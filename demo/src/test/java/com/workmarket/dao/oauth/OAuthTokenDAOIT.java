package com.workmarket.dao.oauth;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.oauth.OAuthToken;
import com.workmarket.domains.model.oauth.OAuthTokenProviderType;
import com.workmarket.service.business.BaseServiceIT;

import static org.junit.Assert.*;

import com.workmarket.test.IntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * User: micah
 * Date: 7/8/13
 * Time: 10:14 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class OAuthTokenDAOIT extends BaseServiceIT {
	@Autowired OAuthTokenDAO oAuthTokenDAO;

	User u;

	@Before
	public void setUp() throws Exception {
		u = newInternalUser();
	}

	@Test
	@Transactional
	public void findByUserAndProvider_NoResults_Null() throws Exception {
		OAuthToken oAuthToken = oAuthTokenDAO.findByUserAndProvider(u.getId(), OAuthTokenProviderType.LINKEDIN);

		assertEquals(null, oAuthToken);
	}

	@Test
	@Transactional
	public void findByUserAndProvider_OneResult_ConfirmResult() throws Exception {
		setupOAuthToken(u);

		OAuthToken oAuthTokenResult = oAuthTokenDAO.findByUserAndProvider(u.getId(), OAuthTokenProviderType.LINKEDIN);

		assertEquals(u.getId(), oAuthTokenResult.getUser().getId());
	}

	@Test
	@Transactional
	public void findByUserAndProvider_MultipleResults_ConfirmLastResult() throws Exception {
		for (int i = 0; i < 5; i++) setupOAuthToken(u);

		List<OAuthToken> oAuthTokens = oAuthTokenDAO.findAllBy("user", u);

		Long oAuthTokenId = -1L;
		for (OAuthToken token : oAuthTokens) {
			if (token.getId() > oAuthTokenId) oAuthTokenId = token.getId();
		}

		OAuthToken oAuthTokenResult = oAuthTokenDAO.findByUserAndProvider(u.getId(), OAuthTokenProviderType.LINKEDIN);

		assertEquals(oAuthTokenId, oAuthTokenResult.getId());
	}

	private void setupOAuthToken(User u) {
		OAuthToken oAuthToken = new OAuthToken();
		oAuthToken.setUser(u);
		oAuthToken.setProviderType(OAuthTokenProviderType.newInstance(OAuthTokenProviderType.LINKEDIN));
		oAuthTokenDAO.saveOrUpdate(oAuthToken);
	}
}
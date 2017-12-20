package com.workmarket.service.business;

import com.codahale.metrics.MetricRegistry;
import com.workmarket.common.metric.MetricRegistryFacade;
import com.workmarket.common.metric.WMMetricRegistryFacade;
import com.workmarket.dao.SocialUserConnectionDAO;
import com.workmarket.domains.model.SocialUserConnection;
import com.workmarket.domains.model.User;
import com.workmarket.service.exception.authentication.InternalAuthenticationException;
import com.workmarket.service.exception.authentication.SocialUserNotFoundException;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.StringUtilities;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

/**
 * User: micah
 * Date: 3/16/13
 * Time: 11:59 PM
 */
@Service
public class SocialServiceImpl implements SocialService, MessageSourceAware {
	// TODO: switch SIGNUP_BASE to use new public sight
	private static final String SIGNUP_BASE = "redirect:/findwork";
//	private static final String SIGNUP_BASE = "redirect:/find-work";
	private static final String LOGIN_ERROR = "redirect:/login?error";
	private static final String SPRING_SECURITY_LAST_EXCEPTION ="SPRING_SECURITY_LAST_EXCEPTION";

	@Autowired private MetricRegistry registry;
	private MetricRegistryFacade facade;

	@Autowired private SocialUserConnectionDAO socialUserConnectionDAO;

	protected MessageSourceAccessor messages =
		SpringSecurityMessageSource.getAccessor();

	@PostConstruct
	public void init() {
		facade = new WMMetricRegistryFacade(registry, "auth.social");
	}

	@Override
	public String processSocialSignup(HttpServletRequest request) {
		facade.meter("processsignup").mark();
		RequestAttributes requestAttributes =
			RequestContextHolder.getRequestAttributes();
		Connection<?> connection =
			ProviderSignInUtils.getConnection(requestAttributes);

		UserProfile profile = connection.fetchUserProfile();

		return SIGNUP_BASE + "?" +
			"firstName="   + StringUtilities.urlEncode(profile.getFirstName())   +
			"&lastName="   + StringUtilities.urlEncode(profile.getLastName())    +
			"&pictureUrl=" + StringUtilities.urlEncode(connection.getImageUrl()) +
			"&userEmail="  + StringUtilities.urlEncode(profile.getEmail());
	}

	@Override
	public String processSocialNoLink(HttpServletRequest request) {
		facade.meter("processnolink").mark();
		RequestAttributes requestAttributes =
			RequestContextHolder.getRequestAttributes();
		Connection<?> connection =
			ProviderSignInUtils.getConnection(requestAttributes);

		if (connection == null) {
			AuthenticationException ae = new InternalAuthenticationException(
				messages.getMessage("auth.internalError")
			);
			request.getSession().setAttribute(SPRING_SECURITY_LAST_EXCEPTION, ae);
			return LOGIN_ERROR;
		}

		String email = connection.fetchUserProfile().getEmail();
		String socialId = connection.getKey().getProviderUserId();
		String provider =
			StringUtils.capitalise(connection.getKey().getProviderId());
		AuthenticationException ae = new SocialUserNotFoundException(
			messages.getMessage(
				"auth.social.notLinked",
				CollectionUtilities.newArray(email, provider)
			)
		).setSocialId(socialId);
		request.getSession().setAttribute(SPRING_SECURITY_LAST_EXCEPTION, ae);
		return LOGIN_ERROR;
	}

	@Override
	public void processSocialException(
		HttpServletRequest request, AuthenticationException ae
	) {
		facade.meter("processexc").mark();
		request.getSession().setAttribute(SPRING_SECURITY_LAST_EXCEPTION, ae);
	}

	@Override
	public User findUserBySocialKey(ConnectionKey key) {
		facade.meter("finduserbykey").mark();
		SocialUserConnection socialUserConnection =
			socialUserConnectionDAO.findBySocialKey(key);
		if (socialUserConnection == null) return null;
		return socialUserConnection.getUser();
	}

	@Override
	public void setMessageSource(MessageSource messageSource) {
		this.messages = new MessageSourceAccessor(messageSource);
	}
}

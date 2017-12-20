package com.workmarket.service.configuration;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.User;
import com.workmarket.service.business.UserService;
import com.workmarket.domains.authentication.services.SocialSignInAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.social.connect.*;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.social.connect.web.ProviderSignInController;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.google.api.Google;
import org.springframework.social.google.connect.GoogleConnectionFactory;

import javax.sql.DataSource;
import java.util.List;

/**
 * User: micah
 * Date: 3/7/13
 * Time: 4:41 PM
 */
@Configuration
public class SocialConfig {
	@Autowired @Qualifier("dataSourceWorkmarket") private DataSource dataSource;
	@Autowired private UserService userService;
	@Autowired  @Qualifier("socialSignInAdapter") private SocialSignInAdapter socialSignInAdapter;

	@Value("${facebook.key}")
	private String facebookKey;

	@Value("${facebook.secret}")
	private String facebookSecret;

	@Value("${google.key}")
	private String googleKey;

	@Value("${google.secret}")
	private String googleSecret;

	public SocialConfig() {}

	/**
	 * When a new provider is added to the app, register its
	 * ConnectionFactory here.
	 */
	@Bean
	public ConnectionFactoryLocator connectionFactoryLocator() {
		ConnectionFactoryRegistry registry = new ConnectionFactoryRegistry();
		List<ConnectionFactory<?>> connectionFactories = Lists.newArrayList();
		connectionFactories.add(new FacebookConnectionFactory(facebookKey, facebookSecret));

		GoogleConnectionFactory googleConnectionFactory = new GoogleConnectionFactory(googleKey, googleSecret);
		connectionFactories.add(googleConnectionFactory);

		registry.setConnectionFactories(connectionFactories);

		return registry;
	}

	private class SocialConnectionSignup implements ConnectionSignUp {
		@Override
		public String execute(Connection<?> connection) {
			// only Facebook right now
			if (connection.getApi() instanceof Facebook) {
				User user = userService.findUserByEmail(
					connection.fetchUserProfile().getEmail()
				);
				if (user == null) return null;
				return String.valueOf(user.getId());
			} else if (connection.getApi() instanceof Google) {
				User user = userService.findUserByEmail(
						connection.fetchUserProfile().getEmail()
				);

				if(user == null) return null;
				return String.valueOf(user.getId());
			}
			return null;
		}
	}

	@Bean
	public UsersConnectionRepository usersConnectionRepository() {
		JdbcUsersConnectionRepository repository =
			new JdbcUsersConnectionRepository(
				dataSource, connectionFactoryLocator(), Encryptors.noOpText()
			);
		repository.setConnectionSignUp(new SocialConnectionSignup());
		return repository;
	}

	@Bean
	public ProviderSignInController providerSignInController() {
		ProviderSignInController providerSignInController =
			new ProviderSignInController(
				connectionFactoryLocator(),
				usersConnectionRepository(),
				socialSignInAdapter
		);

		providerSignInController.setSignUpUrl("/social/login/social_no_link");
		providerSignInController.setSignInUrl("/login");

		return providerSignInController;
	}
}
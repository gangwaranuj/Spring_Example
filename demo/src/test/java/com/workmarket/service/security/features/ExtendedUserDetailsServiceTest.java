package com.workmarket.service.security.features;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.workmarket.service.infra.business.UserRoleService;
import com.workmarket.domains.authentication.services.ExtendedUserDetailsOptionsService;
import com.workmarket.domains.authentication.services.ExtendedUserDetailsService;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.Profile;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.account.payment.PaymentConfiguration;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.domains.model.acl.Permission;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.asset.UserAssetAssociation;
import com.workmarket.domains.model.datetime.TimeZone;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.postalcode.PostalCode;
import com.workmarket.domains.model.tax.AbstractTaxEntity;
import com.workmarket.domains.model.user.PersonaPreference;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.account.SubscriptionService;
import com.workmarket.service.business.tax.TaxService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.web.WebRequestContextProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.verification.VerificationMode;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static com.workmarket.domains.authentication.services.ExtendedUserDetailsOptionsService.OPTION.AUTH;
import static com.workmarket.domains.authentication.services.ExtendedUserDetailsOptionsService.OPTION.AVATARS;
import static com.workmarket.domains.authentication.services.ExtendedUserDetailsOptionsService.OPTION.BACKGROUND_IMAGE;
import static com.workmarket.domains.authentication.services.ExtendedUserDetailsOptionsService.OPTION.COMPANY;
import static com.workmarket.domains.authentication.services.ExtendedUserDetailsOptionsService.OPTION
		.PERSONA_PREFERENCE;
import static com.workmarket.domains.authentication.services.ExtendedUserDetailsOptionsService.OPTION.POSTAL_CODE;
import static com.workmarket.domains.authentication.services.ExtendedUserDetailsOptionsService.OPTION.PROFILE;
import static com.workmarket.domains.authentication.services.ExtendedUserDetailsOptionsService.OPTION.TAX_INFO;
import static com.workmarket.domains.authentication.services.ExtendedUserDetailsOptionsService.OPTION.TIME_ZONE;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * User: micah
 * Date: 1/26/14
 * Time: 11:23 AM
 */
@RunWith(MockitoJUnitRunner.class)
public class ExtendedUserDetailsServiceTest {
	@Mock private UserService userService;
	@Mock private ProfileService profileService;
	@Mock private CompanyService companyService;
	@Mock private AuthenticationService authenticationService;
	@Mock private SubscriptionService subscriptionService;
	@Mock private TaxService taxService;
	@Mock private WebRequestContextProvider webRequestContextProvider;
	@Mock private UserRoleService userRoleService;

	@InjectMocks ExtendedUserDetailsService extendedUserDetailsService;

	Company company = mock(Company.class);
	Profile profile = mock(Profile.class);
	String email = "micah@workmarket.com";
	User user = mock(User.class);
	PersonaPreference personaPreference = mock(PersonaPreference.class);
	Optional<PersonaPreference> personaPreferenceOptional = Optional.fromNullable(personaPreference);
	Country country = mock(Country.class);
	PostalCode postalCode = mock(PostalCode.class);
	Optional<PostalCode> postalCodeOptional = Optional.fromNullable(postalCode);
	List<AclRole> aclRoles = Lists.newArrayList();
	List<Permission> permissions = Lists.newArrayList();
	String[] roleNames = new String[0];
	PaymentConfiguration paymentConfiguration = mock(PaymentConfiguration.class);
	TimeZone timeZone = mock(TimeZone.class);
	UserAssetAssociation userAssetAssociation = mock(UserAssetAssociation.class);
	Asset asset = mock(Asset.class);
	AbstractTaxEntity taxEntity = mock(AbstractTaxEntity.class);

	final VerificationMode NEVER = never();
	final VerificationMode ONCE = times(1);

	Map<ExtendedUserDetailsOptionsService.OPTION, VerificationMode> verifiers;

	@Before
	public void setup() {
		when(userService.findUserByEmail(any(String.class))).thenReturn(user);

		when(user.getId()).thenReturn(1L);
		when(user.getProfile()).thenReturn(profile);
		when(user.getCompany()).thenReturn(company);
		when(user.getEmail()).thenReturn(email);
		when(company.getId()).thenReturn(2L);
		when(authenticationService.getRoles(1L)).thenReturn(roleNames);

		when(companyService.findCompanyById(2L)).thenReturn(company);
		when(company.getPaymentConfiguration()).thenReturn(paymentConfiguration);
		when(paymentConfiguration.isSubscriptionPricing()).thenReturn(Boolean.TRUE);

		when(profileService.findById(3L)).thenReturn(profile);
		when(profile.getId()).thenReturn(3L);
		when(profile.getMaxTravelDistance()).thenReturn(new BigDecimal(10));

		when(profileService.findUserProfileTimeZone(1L)).thenReturn(timeZone);
		when(timeZone.getId()).thenReturn(4L);
		when(timeZone.getTimeZoneId()).thenReturn("NY");

		when(profileService.findPostalCodeForUser(1L)).thenReturn(postalCodeOptional);
		when(postalCode.getPostalCode()).thenReturn("11111");
		when(postalCode.getCountry()).thenReturn(country);

		when(userService.getPersonaPreference(1L)).thenReturn(personaPreferenceOptional);
		when(userService.findUserByEmail(anyString())).thenReturn(user);
		when(userService.findUserAvatars(1L)).thenReturn(userAssetAssociation);
		when(userAssetAssociation.getTransformedSmallAsset()).thenReturn(asset);
		when(userAssetAssociation.getTransformedLargeAsset()).thenReturn(asset);
		when(asset.getUri()).thenReturn("MyURI");

		when(userService.findUserBackgroundImage(1L)).thenReturn(asset);

		when(authenticationService.hasPaymentCenterAndEmailsAccess(1L, Boolean.FALSE)).thenReturn(Boolean.FALSE);
		when(authenticationService.hasManageBankAndFundsAccess(1L, Boolean.FALSE)).thenReturn(Boolean.FALSE);
		when(authenticationService.hasCustomAccessSettingsSet(1L)).thenReturn(Boolean.FALSE);
		when(userRoleService.hasPermissionsForCustomAuth(1L, Permission.EDIT_PRICING_AUTH)).thenReturn(Boolean.FALSE);
		when(userRoleService.hasPermissionsForCustomAuth(1L, Permission.APPROVE_WORK_AUTH)).thenReturn(Boolean.FALSE);
		when(userRoleService.hasPermissionsForCustomAuth(1L, Permission.COUNTEROFFER_AUTH)).thenReturn(Boolean.FALSE);

		when(subscriptionService.hasMboServiceType(company.getId())).thenReturn(Boolean.TRUE);

		when(taxService.findActiveTaxEntity(1l)).thenReturn(taxEntity);

		verifiers = Maps.newHashMap();
		verifiers.put(COMPANY, NEVER);
		verifiers.put(PROFILE, NEVER);
		verifiers.put(TIME_ZONE, NEVER);
		verifiers.put(POSTAL_CODE, NEVER);
		verifiers.put(PERSONA_PREFERENCE, NEVER);
		verifiers.put(AVATARS, NEVER);
		verifiers.put(BACKGROUND_IMAGE, NEVER);
		verifiers.put(AUTH, NEVER);
		verifiers.put(TAX_INFO, NEVER);
	}

	private void verifier(Map<ExtendedUserDetailsOptionsService.OPTION, VerificationMode> verifiers) {
		verify(companyService, verifiers.get(COMPANY)).findCompanyById(any(Long.class));
		verify(profileService, verifiers.get(PROFILE)).findById(any(Long.class));
		verify(profileService, verifiers.get(TIME_ZONE)).findUserProfileTimeZone(any(Long.class));
		verify(profileService, verifiers.get(POSTAL_CODE)).findPostalCodeForUser(any(Long.class));
		verify(userService, verifiers.get(PERSONA_PREFERENCE)).getPersonaPreference(any(Long.class));
		verify(userService, verifiers.get(AVATARS)).findUserAvatars(any(Long.class));
		verify(userService, verifiers.get(BACKGROUND_IMAGE)).findUserBackgroundImage(any(Long.class));
		verify(authenticationService, verifiers.get(AUTH)).hasPaymentCenterAndEmailsAccess(any(Long.class), any(Boolean.class));
		verify(taxService, verifiers.get(TAX_INFO)).findActiveTaxEntity(any(Long.class));
	}

	@Test
	public void loadUserByUsername_Success() {
		extendedUserDetailsService.loadUserByUsername(email);

		verifiers.put(COMPANY, ONCE);
		verifiers.put(PROFILE, ONCE);
		verifiers.put(TIME_ZONE, ONCE);
		verifiers.put(POSTAL_CODE, ONCE);
		verifiers.put(PERSONA_PREFERENCE, ONCE);
		verifiers.put(AVATARS, ONCE);
		verifiers.put(BACKGROUND_IMAGE, ONCE);
		verifiers.put(AUTH, ONCE);
		verifiers.put(TAX_INFO, ONCE);

		verifier(verifiers);
	}

	@Test
	public void loadUser(){
		extendedUserDetailsService.loadUser(user);

		verifiers.put(COMPANY, ONCE);
		verifiers.put(PROFILE, ONCE);
		verifiers.put(TIME_ZONE, ONCE);
		verifiers.put(POSTAL_CODE, ONCE);
		verifiers.put(PERSONA_PREFERENCE, ONCE);
		verifiers.put(AVATARS, ONCE);
		verifiers.put(BACKGROUND_IMAGE, ONCE);
		verifiers.put(AUTH, ONCE);
		verifiers.put(TAX_INFO, ONCE);

		verifier(verifiers);
	}

	@Test
	public void loadUserByEmail_NullOptions() {
		extendedUserDetailsService.loadUserByEmail(email, null);

		verifier(verifiers);
	}

	@Test
	public void loadUserByEmail_NoOptions() {
		ExtendedUserDetailsOptionsService.OPTION[] options = {};

		extendedUserDetailsService.loadUserByEmail(email, options);

		verifier(verifiers);
	}

	@Test
	public void loadUserByEmail_CompanyOptionOnly() {
		ExtendedUserDetailsOptionsService.OPTION[] options = { ExtendedUserDetailsOptionsService.OPTION.COMPANY };

		extendedUserDetailsService.loadUserByEmail(email, options);

		verifiers.put(COMPANY, ONCE);
		verifier(verifiers);
	}

	@Test
	public void loadUserByEmail_ProfileOptionOnly() {
		ExtendedUserDetailsOptionsService.OPTION[] options = { ExtendedUserDetailsOptionsService.OPTION.PROFILE };

		extendedUserDetailsService.loadUserByEmail(email, options);

		verifiers.put(PROFILE, ONCE);
		verifier(verifiers);
	}

	@Test
	public void loadUserByEmail_TimeZoneOptionOnly() {
		ExtendedUserDetailsOptionsService.OPTION[] options = { ExtendedUserDetailsOptionsService.OPTION.TIME_ZONE };

		extendedUserDetailsService.loadUserByEmail(email, options);

		verifiers.put(TIME_ZONE, ONCE);
		verifier(verifiers);
	}

	@Test
	public void loadUserByEmail_PostalCodeOnly() {
		ExtendedUserDetailsOptionsService.OPTION[] options = { ExtendedUserDetailsOptionsService.OPTION.POSTAL_CODE };

		extendedUserDetailsService.loadUserByEmail(email, options);

		verifiers.put(POSTAL_CODE, ONCE);
		verifier(verifiers);
	}

	@Test
	public void loadUserByEmail_PersonaPreferenceOnly() {
		ExtendedUserDetailsOptionsService.OPTION[] options = { ExtendedUserDetailsOptionsService.OPTION.PERSONA_PREFERENCE };

		extendedUserDetailsService.loadUserByEmail(email, options);

		verifiers.put(PERSONA_PREFERENCE, ONCE);
		verifier(verifiers);
	}

	@Test
	public void loadUserByEmail_AvatarsOnly() {
		ExtendedUserDetailsOptionsService.OPTION[] options = { ExtendedUserDetailsOptionsService.OPTION.AVATARS };

		extendedUserDetailsService.loadUserByEmail(email, options);

		verifiers.put(AVATARS, ONCE);
		verifier(verifiers);
	}

	@Test
	public void loadUserByEmail_BackgroundImageOnly() {
		ExtendedUserDetailsOptionsService.OPTION[] options = { ExtendedUserDetailsOptionsService.OPTION.BACKGROUND_IMAGE };

		extendedUserDetailsService.loadUserByEmail(email, options);

		verifiers.put(BACKGROUND_IMAGE, ONCE);
		verifier(verifiers);
	}

	@Test
	public void loadUserByEmail_AuthOnly() {
		ExtendedUserDetailsOptionsService.OPTION[] options = { ExtendedUserDetailsOptionsService.OPTION.AUTH };

		extendedUserDetailsService.loadUserByEmail(email, options);

		verifiers.put(AUTH, ONCE);
		verifier(verifiers);
	}

	@Test
	public void loadUserByEmail_TaxInfoOnly() {
		ExtendedUserDetailsOptionsService.OPTION[] options = { ExtendedUserDetailsOptionsService.OPTION.TAX_INFO };

		extendedUserDetailsService.loadUserByEmail(email, options);

		verifiers.put(TAX_INFO, ONCE);
		verifier(verifiers);
	}
}

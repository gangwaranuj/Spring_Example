package com.workmarket.service.business.integration.sso;

import com.google.common.base.Optional;

import com.workmarket.common.core.RequestContext;
import com.workmarket.service.infra.business.UserRoleService;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.Profile;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.account.payment.PaymentConfiguration;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.domains.model.asset.DefaultBackgroundImage;
import com.workmarket.domains.model.datetime.TimeZone;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.postalcode.PostalCode;
import com.workmarket.domains.model.user.PersonaPreference;
import com.workmarket.service.admin.DefaultBackgroundImageService;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.RegistrationService;
import com.workmarket.service.business.SSOConfigurationService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.account.SubscriptionService;
import com.workmarket.service.business.dto.UserDTO;
import com.workmarket.service.business.tax.TaxService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.web.WebRequestContextProvider;
import com.workmarket.sso.SSOServiceClient;
import com.workmarket.sso.dto.SSOMetadataDTO;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.saml2.core.NameID;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.schema.impl.XSStringImpl;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.saml.SAMLCredential;

import java.util.Arrays;
import java.util.UUID;

import rx.Observable;
import rx.observables.BlockingObservable;
import rx.observers.TestObserver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SSOUserDetailsServiceTest {

	@Mock AuthenticationService authenticationService;
	@Mock UserService userService;
	@Mock ProfileService profileService;
	@Mock CompanyService companyService;
	@Mock SubscriptionService subscriptionService;
	@Mock RegistrationService registrationService;
	@Mock TaxService taxService;
	@Mock DefaultBackgroundImageService defaultBackgroundImageService;
	@Mock SSOServiceClient ssoServiceClient;
	@Mock SSOConfigurationService ssoConfigurationService;
	@Mock UserRoleService userRoleService;
	@InjectMocks SSOUserDetailsService userDetailsService;
	@Mock WebRequestContextProvider webRequestContextProvider;

	@Mock SAMLCredential samlCredential;
	@Mock User user;
	@Mock Company company;

	private String firstName = "Timothy";
	private String lastName = "Imhof";
	private String email = "timothy@workmarket.com";
	private RequestContext requestContext;

	@Before
	public void init() throws Exception {
		Profile profile = mock(Profile.class);
		TimeZone timeZone = mock(TimeZone.class);
		PostalCode postalCode = mock(PostalCode.class);
		Country country = mock(Country.class);
		PersonaPreference personaPreference = mock(PersonaPreference.class);
		SSOMetadataDTO ssoMetadataDTO = mock(SSOMetadataDTO.class);

		requestContext = new RequestContext(UUID.randomUUID().toString(), "DUMMY_TENANT_ID");
		requestContext.setUserId("workmarket");
		when(webRequestContextProvider.getRequestContext()).thenReturn(requestContext);

		when(postalCode.getCountry()).thenReturn(country);

		when(profileService.findUserProfileTimeZone(anyLong())).thenReturn(timeZone);
		when(profileService.findById(anyLong())).thenReturn(profile);
		when(profileService.findPostalCodeForUser(anyLong())).thenReturn(Optional.of(postalCode));

		when(user.getEmail()).thenReturn(email);
		when(user.getFirstName()).thenReturn(firstName);
		when(user.getLastName()).thenReturn(lastName);
		when(user.getCompany()).thenReturn(company);
		when(user.getProfile()).thenReturn(profile);

		when(companyService.findCompanyById(anyLong())).thenReturn(company);
		when(companyService.findByUUID(anyString())).thenReturn(company);
		when(companyService.findById(anyLong())).thenReturn(company);

		PaymentConfiguration paymentConfiguration = mock(PaymentConfiguration.class);
		when(company.getPaymentConfiguration()).thenReturn(paymentConfiguration);

		AclRole viewOnlyRole = mock(AclRole.class);
		when(viewOnlyRole.getId()).thenReturn(AclRole.ACL_VIEW_ONLY);
		when(viewOnlyRole.getConstantName()).thenReturn("VIEW_ONLY");
		when(authenticationService.findAllAssignedAclRolesByUser(anyLong())).thenReturn(Arrays.asList(viewOnlyRole));
		when(authenticationService.getRoles(anyLong())).thenReturn(new String[]{"VIEW_ONLY"});

		Attribute firstName = mockAttribute("firstName", this.firstName);
		Attribute lastName = mockAttribute("lastName", this.lastName);
		Attribute email = mockAttribute("email", this.email);
		when(samlCredential.getAttributes()).thenReturn(Arrays.asList(new Attribute[]{firstName, lastName, email}));

		when(subscriptionService.hasMboServiceType(anyLong())).thenReturn(false);

		when(userService.getPersonaPreference(anyLong())).thenReturn(Optional.of(personaPreference));

		when(defaultBackgroundImageService.getCurrentDefaultBackgroundImage()).
			thenReturn(Optional.<DefaultBackgroundImage>absent());

		when(taxService.findActiveTaxEntity(anyLong())).thenReturn(null);

		when(ssoServiceClient.getIDPMetadata(anyString(), any(RequestContext.class)))
			.thenReturn(Observable.just(ssoMetadataDTO));

		when(registrationService.registerNewForCompany(any(UserDTO.class), anyLong(), any(Long[].class), anyBoolean()))
			.thenReturn(user);
	}

	@Test
	public void loadUserBySAML_userExists() {
		when(userService.findUserByEmail(anyString())).thenReturn(user);

		final ExtendedUserDetails userDetails =
				(ExtendedUserDetails) userDetailsService.loadUserBySAML(samlCredential);

		assertEquals(firstName, userDetails.getFirstName());
		assertEquals(lastName, userDetails.getLastName());
		assertEquals(email, userDetails.getEmail());
	}

	@Test
	public void loadUserBySAML_userDoesNotExists() {
		when(userService.findUserByEmail(anyString())).thenReturn(null);

		final ExtendedUserDetails userDetails =
				(ExtendedUserDetails) userDetailsService.loadUserBySAML(samlCredential);

		assertEquals(firstName, userDetails.getFirstName());
		assertEquals(lastName, userDetails.getLastName());
		assertEquals(email, userDetails.getEmail());
	}

	@Test
	public void loadUserBySAML_useNameIdWhenEmail() {
		final NameID mockNameId = mock(NameID.class);
		when(mockNameId.getValue()).thenReturn("foo@bar.com");
		when(samlCredential.getNameID()).thenReturn(mockNameId);
		when(userService.findUserByEmail(anyString())).thenReturn(null);
		when(user.getEmail()).thenReturn("foo@bar.com");

		final ExtendedUserDetails userDetails =
				(ExtendedUserDetails) userDetailsService.loadUserBySAML(samlCredential);

		assertEquals(firstName, userDetails.getFirstName());
		assertEquals(lastName, userDetails.getLastName());
		assertEquals("foo@bar.com", userDetails.getEmail());
	}

	@Test(expected = UsernameNotFoundException.class)
	public void exceptionWithNoCompany() {
		final NameID mockNameId = mock(NameID.class);
		when(mockNameId.getValue()).thenReturn("foo@bar.com");
		when(samlCredential.getNameID()).thenReturn(mockNameId);
		when(userService.findUserByEmail(anyString())).thenReturn(null);

		final SSOMetadataDTO mockMetadata = mock(SSOMetadataDTO.class);
		when(mockMetadata.getCompanyUuid()).thenReturn("1234567890");

		when(company.getId()).thenReturn(1234L);

		when(companyService.findByUUID(anyString())).thenReturn(null);

		when(ssoServiceClient.getIDPMetadata(anyString(), any(RequestContext.class)))
				.thenReturn(Observable.just(mockMetadata));
		when(user.getEmail()).thenReturn("foo@bar.com");

		final ExtendedUserDetails userDetails =
				(ExtendedUserDetails) userDetailsService.loadUserBySAML(samlCredential);
	}

	private Attribute mockAttribute(String name, String value) {
		XSStringImpl attributeValue = mock(XSStringImpl.class);
		when(attributeValue.getValue()).thenReturn(value);
		Attribute attribute = mock(Attribute.class);
		when(attribute.getAttributeValues()).thenReturn(Arrays.asList((XMLObject) attributeValue));
		when(attribute.getName()).thenReturn(name);

		return attribute;
	}
}

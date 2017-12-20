package com.workmarket.service.business.integration.sso;

import com.google.common.base.MoreObjects;

import com.Ostermiller.util.RandPass;
import com.workmarket.api.internal.user.gen.User.InternalUserCreateOrUpdateRequest;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.authentication.services.ExtendedUserDetailsService;
import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.company.SSOConfiguration;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.RegistrationService;
import com.workmarket.service.business.SSOConfigurationService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.UserDTO;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.web.WebRequestContextProvider;
import com.workmarket.sso.SSOServiceClient;
import com.workmarket.sso.dto.SSOMetadataDTO;
import com.workmarket.utility.EmailUtilities;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.saml2.core.NameID;
import org.opensaml.saml2.core.NameIDType;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.schema.XSAny;
import org.opensaml.xml.schema.XSString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.util.List;

@Service
public class SSOUserDetailsService extends ExtendedUserDetailsService implements SAMLUserDetailsService {

	private static final Logger logger = LoggerFactory.getLogger(SSOUserDetailsService.class);

	@Autowired private UserService userService;
	@Autowired private RegistrationService registrationService;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private CompanyService companyService;
	@Autowired private SSOConfigurationService ssoConfigurationService;
	@Autowired private SSOServiceClient ssoServiceClient;
	@Autowired private WebRequestContextProvider webRequestContextProvider;

	@Override
	public Object loadUserBySAML(SAMLCredential samlCredential) throws UsernameNotFoundException, DataAccessException {
		if (samlCredential == null) {
			throw new UsernameNotFoundException("Assertion validation failed");
		}
		final Saml saml = new Saml(samlCredential);
		logger.info("attempting authentication for " + saml);

		authenticationService.setCurrentUser(Constants.WORKMARKET_SYSTEM_USER_ID);
		final ExtendedUserDetails details = loadSSOUserBySAML(saml);

		return details;
	}

	public ExtendedUserDetails createSSOUser(InternalUserCreateOrUpdateRequest createUserRequest) throws
		InvalidParameterException {
		logger.debug("createSSOUser(): creating user {}", createUserRequest);
		checkParameters(createUserRequest);
		final Saml theSaml = new Saml();
		theSaml.email = createUserRequest.getProfile().getUserEmail();
		theSaml.firstName = createUserRequest.getProfile().getFirstName();
		theSaml.lastName = createUserRequest.getProfile().getLastName();
    		theSaml.idpEntityId = createUserRequest.getUser().getIdpEntityId();
		ExtendedUserDetails createdUser = loadSSOUserBySAML(theSaml);
		logger.debug("createSSOUser(): {} created", theSaml.email);
		return createdUser;
	}

	private void checkParameters(InternalUserCreateOrUpdateRequest createUserRequest) throws InvalidParameterException {
		if (StringUtils.isBlank(createUserRequest.getProfile().getUserEmail()) ||
			StringUtils.isBlank(createUserRequest.getProfile().getFirstName()) ||
			StringUtils.isBlank(createUserRequest.getProfile().getLastName()) ||
			StringUtils.isBlank(createUserRequest.getUser().getIdpEntityId())) {
		throw new InvalidParameterException("createSSOUser(): provided parameters '"
		+ createUserRequest.getProfile().getUserEmail()
		+ "', '" + createUserRequest.getProfile().getFirstName()
		+ "', '" + createUserRequest.getProfile().getLastName()
		+ "', '" + createUserRequest.getUser().getIdpEntityId()
		+ "' are insufficient to create a profile.");
		}
	}


	private ExtendedUserDetails loadSSOUserBySAML(Saml saml) throws UsernameNotFoundException, DataAccessException {
		try {
			// let's also double check to see if user with this email exists, if so, it is the same user
			User user = userService.findUserByEmail(saml.email);

			if (user == null) {
				final SSOMetadataDTO ssoMetadata =
					ssoServiceClient.getIDPMetadata(saml.idpEntityId, webRequestContextProvider.getRequestContext())
						.toBlocking().first();
				logger.info("loadSSOUserBySAML(): {} metadata {}", saml.email, MoreObjects.toStringHelper(ssoMetadata));
				if (ssoMetadata != null) {
					Company company = companyService.findByUUID(ssoMetadata.getCompanyUuid());
					logger.info("loadSSOUserBySAML(): loadSSOUserBySAML(): {} user not existing for company {}", saml.email,
						ssoMetadata.getCompanyUuid());
					if (company != null) {
						user = createUserWithRoles(saml, company.getId());
						logger.info("loadSSOUserBySAML(): loadSSOUserBySAML(): user {} created in webapp.", saml.email);
					} else {
						logger.error("loadSSOUserBySAML():"
							+ " this should not happen,"
							+ " an non existing company tried to allow '{}',"
							+ " an non existing worker to SSO login...", saml.email);
						throw new UsernameNotFoundException("loadSSOUserBySAML(): Non existing user "
								+ saml.email + " and company.");
					}
				} else {
					logger.error("loadSSOUserBySAML():"
							+ " no company was found for user {},"
							+ " has the company been configured for SSO?", saml.email);
					throw new UsernameNotFoundException("loadSSOUserBySAML(): Non existing company for user "
							+ saml.email + ". This points to SSO not being properly configured for the company.");
				}
			}

			final ExtendedUserDetails details = (ExtendedUserDetails) loadUser(user);
			details.setSsoUser(true);
			authenticationService.setCurrentUser(user.getId());

			if (logger.isDebugEnabled()) {
				logger.info("loadSSOUserBySAML(): \n {}", details.toString());
			}

			return details;
		} catch (final Exception e) {
			if (e instanceof DataAccessException) {
				throw (DataAccessException) e;
			} else if (e instanceof UsernameNotFoundException) {
				throw (UsernameNotFoundException) e;
			} else {
				throw new RuntimeException("loadSSOUserBySAML(): unable to process authentication request for " + saml, e);
			}
		}
	}

	private User createUserWithRoles(Saml saml, Long companyId) throws Exception {
		final UserDTO userDTO = getUser(saml);
		final Company company = companyService.findById(companyId);
		final Long[] aclRoleIds = getSAMLAclRoleIds(companyId);
		final User user = registrationService.registerNewForCompany(userDTO, company.getId(), aclRoleIds, false);

		logger.info("createUserWithRoles(): registered user {} for company {}", saml.email, companyId);
		// auto-confirm & approve new resources that come in via SSO, but do NOT put them into the marketplace at large.
		registrationService.confirmAccount(user.getId(), false);
		logger.info("createUserWithRoles(): account confirmed for user {} for company {}", saml.email, companyId);
		user.setLane3ApprovalStatus(ApprovalStatus.OPT_OUT);

		authenticationService.approveUser(user.getId());
		logger.info("createUserWithRoles(): approved user {} for company {}", saml.email, companyId);
		return user;
	}

	private Long[] getSAMLAclRoleIds(Long companyId) {
		final SSOConfiguration ssoConfig = ssoConfigurationService.findByCompanyId(companyId);
		if (ssoConfig != null && ssoConfig.getDefaultRole() != null) {
			return new Long[]{ssoConfig.getDefaultRole().getId()};
		} else {
			return new Long[]{};
		}
	}

	private UserDTO getUser(Saml saml) {
		logger.info(" getUser(): fields coming in SAML Response '{}'", saml.toString());
		final UserDTO dto = new UserDTO();
		dto.setEmail(saml.email);
		dto.setFirstName(saml.firstName);
		dto.setLastName(saml.lastName);
		dto.setPassword(new RandPass(RandPass.LOWERCASE_LETTERS_AND_NUMBERS_ALPHABET).getPass(8));
		return dto;
	}

	class Saml {
		private String email = null;
		private String lastName = null;
		private String firstName = null;
		private String objectGUID = null;
		private String idpEntityId;
		private String context;

		public Saml() { }

		public Saml(final SAMLCredential samlCredential) {
			this.idpEntityId = samlCredential.getRemoteEntityID();
			final List<Attribute> attributes = samlCredential.getAttributes();
			final NameID nameID = samlCredential.getNameID();

			if (nameID != null
				&& NameIDType.EMAIL.equals(nameID.getFormat())
				&& EmailUtilities.isValidEmailAddress(nameID.getValue())) {
				email = nameID.getValue();
			}

			if (attributes != null) {
				for (Attribute attribute : attributes) {
					final String value = CollectionUtils.isNotEmpty(attribute.getAttributeValues())
						? getAttributeValue(attribute)
						: null;
					final String name = attribute.getName();

					// TODO: refactor this code to allow custom mapping dynamically
					// catbusinessmail, sn and givenname are added to CAT support
					if (value != null) {
						if ("email".equalsIgnoreCase(name)
							|| "emailAddress".equalsIgnoreCase(name)
							|| "catbusinessmail".equalsIgnoreCase(name) ) {
							if (email == null) {
								email = value;
							}
						} else if ("lastname".equalsIgnoreCase(name) || "sn".equalsIgnoreCase(name) ) {
							lastName = value;
						} else if ("firstname".equalsIgnoreCase(name) || "givenname".equalsIgnoreCase(name)) {
							firstName = value;
						} else {
							logger.warn(String.format("unused saml field %s=%s\n", name, value));
						}
					}
				}
			}
			context = String.format("objectGUID[%s] idpEntityId[%s] firstName[%s] lastName[%s] email[%s]", objectGUID, idpEntityId, firstName, lastName, email);
		}

		private String getAttributeValue(final Attribute attribute) {
			final XMLObject xmlObject = attribute.getAttributeValues().get(0);
			if (xmlObject instanceof XSString) {
				return ((XSString) xmlObject).getValue();
			}
			if (xmlObject instanceof XSAny) {
				return ((XSAny) xmlObject).getTextContent();
			}
			throw new RuntimeException("attribute is of type " + xmlObject.getClass() + " which we do not yet handle");
		}

		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("firstname: ").append(firstName).append("\n")
					.append("lastname: ").append(lastName).append("\n")
					.append("email: ").append(email).append("\n")
					.append("objectGUID: ").append(objectGUID).append("\n")
					.append("idpEntityId: ").append(idpEntityId).append("\n")
					.append("context: ").append(context).append("\n");
			return sb.toString();
		}
	}
}

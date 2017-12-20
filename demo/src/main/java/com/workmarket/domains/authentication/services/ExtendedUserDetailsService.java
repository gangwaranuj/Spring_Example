package com.workmarket.domains.authentication.services;

import com.google.common.base.Optional;

import com.workmarket.service.infra.business.UserRoleService;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.Profile;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.UserStatusType;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.domains.model.acl.Permission;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.asset.DefaultBackgroundImage;
import com.workmarket.domains.model.asset.UserAssetAssociation;
import com.workmarket.domains.model.datetime.TimeZone;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.postalcode.PostalCode;
import com.workmarket.domains.model.tax.AbstractTaxEntity;
import com.workmarket.domains.model.user.PersonaPreference;
import com.workmarket.service.admin.DefaultBackgroundImageService;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.account.SubscriptionService;
import com.workmarket.service.business.tax.TaxService;
import com.workmarket.service.infra.business.AuthenticationService;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;

@Primary
@Service
public class ExtendedUserDetailsService implements UserDetailsService, ExtendedUserDetailsOptionsService {

	@Autowired private UserService userService;
	@Autowired private ProfileService profileService;
	@Autowired private CompanyService companyService;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private SubscriptionService subscriptionService;
	@Autowired private DefaultBackgroundImageService defaultBackgroundImageService;
	@Autowired private TaxService taxService;
	@Autowired private UserRoleService userRoleService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
		return loadUserByEmail(username, ALL_OPTIONS);
	}

	@Override
	public UserDetails loadUser(User user) throws UsernameNotFoundException, DataAccessException {
		return loadUser(user, ALL_OPTIONS);
	}

	@Override
	public UserDetails loadUserByEmail(String email, OPTION[] options) {
		User user = userService.findUserByEmail(email);

		if (user == null) {
			throw new UsernameNotFoundException("Username not found.");
		}

		return loadUser(user, options);
	}

	private UserDetails loadUser(User user, OPTION[] options) {

		List<AclRole> aclRoles = authenticationService.findAllAssignedAclRolesByUser(user.getId());
		List<String> aclRoleNames = extract(aclRoles, on(AclRole.class).getConstantName());

		List<Permission> permissions = authenticationService.findAllAssignedPermissionsByUser(user.getId());
		List<String> permissionNames = extract(permissions, on(Permission.class).getConstantName());

		String[] roleNames = authenticationService.getRoles(user.getId());

		List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(
			aclRoleNames.toArray(new String[aclRoleNames.size()])
		);
		authorities.addAll(AuthorityUtils.createAuthorityList(formatRoleNames(roleNames)));
		authorities.addAll(AuthorityUtils.createAuthorityList(permissionNames.toArray(new String[permissionNames.size()])));

		ExtendedUserDetails userDetails = new ExtendedUserDetails(user.getEmail(), "", true, true, true, true, authorities);

		userDetails.setId(user.getId());
		userDetails.setUuid(user.getUuid());
		userDetails.setUserNumber(user.getUserNumber());
		userDetails.setFirstName(user.getFirstName());
		userDetails.setLastName(user.getLastName());
		userDetails.setFullName(user.getFullName());
		userDetails.setEmail(user.getEmail());
		userDetails.setEmailConfirmed(authenticationService.getEmailConfirmed(user));
		final UserStatusType userStatusType = authenticationService.getUserStatus(user);
		userDetails.setUserStatusType((userStatusType == null) ? "" : userStatusType.getCode());

		if (options == null) { options = new OPTION[0]; }

		for (OPTION option : options) {
			switch (option) {
				case COMPANY:
					handleCompany(user, userDetails);
					break;
				case PROFILE:
					handleProfile(user, userDetails);
					break;
				case TIME_ZONE:
					handleTimeZone(user, userDetails);
					break;
				case POSTAL_CODE:
					handlePostalCode(user, userDetails);
					break;
				case PERSONA_PREFERENCE:
					handlePersonaPreference(user, userDetails);
					break;
				case AVATARS:
					handleAvatars(user, userDetails);
					break;
				case BACKGROUND_IMAGE:
					handleBackgroundImage(user, userDetails);
					break;
				case AUTH:
					handleAuth(user, userDetails);
					break;
				case TAX_INFO:
					handleTaxInfo(user, userDetails);
					break;
			}
		}

		return userDetails;
	}

	private void handleCompany(User user, ExtendedUserDetails userDetails) {
		Company company = companyService.findCompanyById(user.getCompany().getId());

		userDetails.setCompanyId(company.getId());
		userDetails.setCompanyNumber(company.getCompanyNumber());
		userDetails.setCompanyUuid(company.getUuid());
		userDetails.setCompanyName(company.getName());
		userDetails.setCompanyEffectiveName(company.getEffectiveName());
		userDetails.setCompanyIsIndividual(company.getOperatingAsIndividualFlag());
		userDetails.setCompanyIsLocked(company.isLocked());
		userDetails.setLockWarningSentOn(company.getLockAccountWarningSentOn());
		userDetails.setOverdueWarningSentOn(company.getOverdueAccountWarningSentOn());
		userDetails.setCompanyIsSuspended(company.isSuspended());
		userDetails.setStatementsEnabled(company.hasStatementsEnabled());
		userDetails.setSubscriptionEnabled(company.getPaymentConfiguration().isSubscriptionPricing());
		userDetails.setMboServiceType(hasMboServiceType(company));
		userDetails.setCompanyHidesPricing(company.isHidePricing());
	}

	private void handleProfile(User user, ExtendedUserDetails userDetails) {
		Profile profile = profileService.findById(user.getProfile().getId());

		userDetails.setMaxTravelDistance(profile.getMaxTravelDistance());
		userDetails.setFindWork(BooleanUtils.isTrue(profile.getFindWork()));
		userDetails.setManageWork(BooleanUtils.isTrue(profile.getManageWork()));
		userDetails.setMboProfile(profileService.findMboProfile(user.getId()));
		userDetails.setInSearch(user.getLane3ApprovalStatus() == ApprovalStatus.APPROVED);
	}

	private void handleTimeZone(User user, ExtendedUserDetails userDetails) {
		TimeZone timeZone = profileService.findUserProfileTimeZone(user.getId());

		userDetails.setTimeZoneId(timeZone.getTimeZoneId());
	}

	private void handlePostalCode(User user, ExtendedUserDetails userDetails) {
		Optional<PostalCode> postalCode = profileService.findPostalCodeForUser(user.getId());

		if (postalCode.isPresent()) {
			userDetails.setPostalCode(postalCode.get().getPostalCode());
			userDetails.setCountry(postalCode.get().getCountry().getId());
		}
	}

	private void handlePersonaPreference(User user, ExtendedUserDetails userDetails) {
		if (user != null) {
			Optional<PersonaPreference> personaPreference = userService.getPersonaPreference(user.getId());
			if (personaPreference.isPresent()) {
				userDetails.setPersonaPreference(personaPreference.get());
			}
		}
	}

	private void handleAvatars(User user, ExtendedUserDetails userDetails) {
		UserAssetAssociation avatars = userService.findUserAvatars(user.getId());

		if (avatars != null) {
			userDetails.setSmallAvatarUri(avatars.getTransformedSmallAsset().getUri());
			userDetails.setLargeAvatarUri(avatars.getTransformedLargeAsset().getUri());
		}
	}

	private void handleBackgroundImage(User user, ExtendedUserDetails userDetails) {
		Asset backgroundImage = userService.findUserBackgroundImage(user.getId());

		if (backgroundImage != null) {
			userDetails.setBackgroundImageUri(backgroundImage.getUri());
		} else {
			Optional<DefaultBackgroundImage> image = defaultBackgroundImageService.getCurrentDefaultBackgroundImage();
			if (image.isPresent()) {
				userDetails.setBackgroundImageUri(image.get().getAsset().getUri());
			}
		}
	}

	private void handleAuth(User user, ExtendedUserDetails userDetails) {
		userDetails.setUserPaymentAccessBlocked(!authenticationService.hasPaymentCenterAndEmailsAccess(user.getId(), Boolean.FALSE));
		userDetails.setUserFundsAccessBlocked(!authenticationService.hasManageBankAndFundsAccess(user.getId(), Boolean.FALSE));
		userDetails.setHasCustomAccessSettingsSet(authenticationService.hasCustomAccessSettingsSet(user.getId()));
		userDetails.setEditPricingCustomAuth(userRoleService.hasPermissionsForCustomAuth(user.getId(), Permission.EDIT_PRICING_AUTH));
		userDetails.setApproveWorkCustomAuth(userRoleService.hasPermissionsForCustomAuth(user.getId(), Permission.APPROVE_WORK_AUTH));
		userDetails.setCounterOfferCustomAuth(userRoleService.hasPermissionsForCustomAuth(user.getId(), Permission.COUNTEROFFER_AUTH));
	}

	private void handleTaxInfo(User user, ExtendedUserDetails userDetails) {
		AbstractTaxEntity taxEntity = taxService.findActiveTaxEntity(user.getId());
		if (taxEntity != null) {
			if (StringUtils.equals(taxEntity.getCountry(), AbstractTaxEntity.COUNTRY_USA)) {
				userDetails.setTaxCountry(Country.USA_COUNTRY);
			}
			else if (StringUtils.equals(taxEntity.getCountry(), AbstractTaxEntity.COUNTRY_CANADA)) {
				userDetails.setTaxCountry(Country.CANADA_COUNTRY);
			} else {
				// if it is something other than Canada or the US then we aren't going to set it - this will
				// need to change in the future once we go international but this is a first step
				;
			}
		}
	}

	private String[] formatRoleNames(String[] roleNames) {
		String[] ucRoleNames = new String[roleNames.length];
		for (int i = 0; i < roleNames.length; i++)
			ucRoleNames[i] = String.format("ROLE_%s", StringUtils.upperCase(roleNames[i]));
		return ucRoleNames;
	}

	private boolean hasMboServiceType(Company company) {
		return subscriptionService.hasMboServiceType(company.getId());
	}
}

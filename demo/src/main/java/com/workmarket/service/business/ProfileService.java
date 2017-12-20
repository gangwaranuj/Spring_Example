package com.workmarket.service.business;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.workmarket.domains.model.Address;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.ImageDTO;
import com.workmarket.domains.model.LocationType;
import com.workmarket.domains.model.MboProfile;
import com.workmarket.domains.model.Profile;
import com.workmarket.domains.model.ProfileLanguage;
import com.workmarket.domains.model.ProfileModificationPagination;
import com.workmarket.domains.model.ProfileModificationType;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.UserProfileModification;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.asset.UserAssetAssociation;
import com.workmarket.domains.model.asset.UserLinkAssociation;
import com.workmarket.domains.model.datetime.TimeZone;
import com.workmarket.domains.model.directory.Phone;
import com.workmarket.domains.model.geocoding.Coordinate;
import com.workmarket.domains.model.postalcode.PostalCode;
import com.workmarket.domains.onboarding.model.WorkerOnboardingDTO;
import com.workmarket.service.business.LinkedInServiceImpl.LinkedInImportFailed;
import com.workmarket.dto.AddressDTO;
import com.workmarket.service.business.dto.CompanyDTO;
import com.workmarket.service.business.dto.LinkedInProfileDTO;
import com.workmarket.service.business.dto.ManageMyWorkMarketDTO;
import com.workmarket.service.business.dto.PhoneNumberDTO;
import com.workmarket.service.business.dto.ProfileDTO;
import com.workmarket.service.business.dto.ProfileLanguageDTO;
import com.workmarket.service.business.dto.UserDTO;
import com.workmarket.service.business.dto.UserProfileCompletenessDTO;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

public interface ProfileService {

	Address findCompanyAddress(Long userId);

	Profile findProfile(Long userId);

	Long findProfileId(Long userId);

	ProfileDTO findProfileDTO(Long userId);

	/**
	 * Checks for a user's profile address first.
	 * If null, checks for a user's company address
	 * @param userId
	 * @return
	 */
	Address findAddress(Long userId);

	Company findCompany(Long userId);

	PostalCode updateProfilePostalCode(Profile profile, String profilePostalCode, AddressDTO addressDto);

	Company saveOrUpdateCompany(Long userId, CompanyDTO companyDTO);

	Company saveOrUpdateCompany(CompanyDTO companyDTO);

	Company updateManageMyWorkMarket(Long companyId, ManageMyWorkMarketDTO manageMyWorkMarketDTO);

	Address saveOrUpdateCompanyAddress(Long userId, AddressDTO addressDTO);

	User updateUser(Long userId, String firstName, String lastName, String email) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException;

	User updateUser(Long userId, UserDTO userDTO) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException;

	List<ProfileLanguage> findProfileLanguages(Long userId);

	ProfileLanguage saveOrUpdateProfileLanguage(Long userId, ProfileLanguageDTO profileLanguageDTO);

	void deleteProfileLanguage(Long profileLanguageId);

	//Profile Modifications

	void registerUserProfileModification(Long userId, ProfileModificationType modificationType);

	ProfileModificationPagination findAllProfileModificationsByUserId(Long userId, ProfileModificationPagination pagination);

	ProfileModificationPagination findAllPendingProfileModifications(ProfileModificationPagination pagination);

	List<UserProfileModification> findAllPendingModificationsByUserId(Long userId);

	void approveUserProfileModifications(Long userId, Long[] profileModificationIds);

	/**
	 * Declines an individual profile modification.
	 *
	 * @param profileModificationId
	 * @
	 */
	void declineProfileModification(Long profileModificationId);

	/**
	 * Approves all the pending profile modifications for a particular user at once.
	 * Sends an email to the user and approves the user for lane 3 if it was pending.
	 *
	 * @param userId
	 * @
	 */
	void approveUserProfileModifications(Long userId);

	/**
	 * Declines all the pending profile modifications for a particular user at once.
	 *
	 * @param userId
	 * @
	 */
	void declineUserProfileModifications(Long userId);

	Map<Long, String> findAllUsersByCompanyId(Long companyId);

	Map<Long, String> findAllActiveUsersByCompanyId(Long companyId);

	List<User> findAllUsersByCompanyId(Long companyId, List<String> userStatusTypeCodes);

	Company saveOrUpdateCompany(Company company);

	Company findCompanyById(Long companyId);

	void saveOrUpdateProfile(Profile profile);

	void updateProfileIndustries(Long profileId, Long[] industryIds);

	//Location Type Preferences
	void saveOrUpdateLocationTypePreferences(Long userId, List<LocationType> locationTypes);

	void updateLocationTypePreferences(Long userId, Long[] locationTypeIds);

	List<LocationType> findLocationTypesPreferenceByUserId(Long userId);

	// LinkedIn

	List<User> findApprovedLane3UsersByCompanyId(Long companyId);

	String getLinkedInAuthorizationUrl(Long userId, String callbackUrl);

	Boolean authorizeLinkedIn(Long userId, String verifier);

	LinkedInProfileDTO getLinkedInProfile(Long userId);

	void suspendCompany(Long companyId, String comment);

	void unsuspendCompany(Long companyId);

	List<String> findBlacklistedZipcodesForUser(Long userId);

	void setBlacklistedZipcodesForUser(Long userId, String[] zipcodes);

	void setBlacklistedZipcodesForUser(Long userId, List<String> zipcodes);

	Profile findById(Long profileId);

	List<Asset> findAllUserResumes(Long userId);
	List<UserAssetAssociation> findAllUserProfileImageAssociations(Long userId);
	List<UserAssetAssociation> findAllUserProfileVideoAssociations(Long userId);

	List<UserAssetAssociation> findAllUserProfileOrderedAssetAssociations(Long userId);

	List<UserAssetAssociation> findAllUserProfileVideoAssociations(String userNumber);
	List<UserLinkAssociation> findAllUserProfileEmbedVideoAssociations(Long userId);
	List<UserLinkAssociation> findAllUserProfileEmbedVideoAssociations(String userNumber);

	List<UserAssetAssociation> findAllUserProfileAndAvatarImageAssociations(Long userId);

	UserAssetAssociation findUserAssetAssociation(Long userId, Long AssetId);

	//CRITS Queue
	void closeCsrProfileLead(Long userId);

	void openCsrProfileLead(Long userId);

	//User Profile completeness
	UserProfileCompletenessDTO getUserProfileCompleteness(Long userId) throws LinkedInImportFailed;

	/**
	 * Updates profile, but it will only change the properties specified in the map.
	 *
	 * @param userId
	 * @param properties - map of  { property name => property value } property names have to be camel cased
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 */
	void updateProfileProperties(Long userId, Map<String, String> properties) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException;

	void updateProfileProperties(Long userId, Map<String, String> properties, boolean shouldSendEmail) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException;

	Profile updateProfileAddressProperties(Long userId, Map<String, String> properties);

	void updateProfileAddress(Long profileId, Long addressId);

	UserAssetAssociation undeleteAssetAssociation(UserAssetAssociation assetAssoc);

	void updateAddress(Long profileId, Address address);

	void addPhoneToProfile(Long profileId, PhoneNumberDTO phoneDTO);

	Coordinate findLatLongForUser(Long userId);

	Optional<PostalCode> findPostalCodeForUser(Long userId);

	TimeZone findUserProfileTimeZone(Long userId);

	MboProfile findMboProfile(Long userId);

	void saveMboProfile(MboProfile profile);

	MboProfile findMboProfileByGUID(String objectGUID);

	List<Phone> findPhonesByProfileId(long profileId);

	void saveOnboardProfile(Long userId, Long profile, Company company, WorkerOnboardingDTO dto, boolean overwriteMissingPhoneNumbers) throws Exception;

	TimeZone getTimeZoneByUserId(Long userId);

	void saveProfileAvatar(Long userId, ImageDTO image) throws Exception;

	void saveOnboardPhoneCodes(Long profileId, WorkerOnboardingDTO dto, boolean overwriteMissingPhoneNumbers) throws Exception;

	Map<String, Object> getProjectionMapByUserNumber(String userNumber, String... fields);

	ImmutableList<Map> getProjectedFollowers(String[] fields) throws Exception;

	boolean isUserTelaidPrivate(Long userId);

	void sendProfileUpdateEmail(Long userId, String fieldName);
}

package com.workmarket.api.internal.endpoints;

import com.google.common.collect.ImmutableList;

import com.workmarket.api.internal.user.gen.User.InternalCompanyEmployeesRequest;
import com.workmarket.api.internal.user.gen.User.InternalCompanyEmployeesResponse;
import com.workmarket.api.internal.user.gen.User.InternalUserFindByRequest;
import com.workmarket.api.internal.user.gen.User.InternalUserAtCompanyDetails;
import com.workmarket.api.internal.user.gen.User.InternalUserProfileDetails;
import com.workmarket.api.internal.user.gen.User.QueryBy;
import com.workmarket.common.api.vo.Response;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.Profile;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.asset.UserAssetAssociation;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.UserService;

import com.workmarket.service.business.dto.UserDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Mockito.when;

/**
 * Test the UserAndCompanyInfoController.
 */
@RunWith(MockitoJUnitRunner.class)
public class UserAndCompanyInfoControllerTest {
  private static final String USER_FIRST_NAME = "JOE";
  private static final String USER_LAST_NAME = "RANDOM";
  private static final String USER_UUID = "user uuid";
  private static final String USER_NUMBER = "12345";
  private static final String USER_EMAIL = "joe@example.com";
  private static final String COMPANY_UUID = "company uuid";
  private static final long USER_ID = 42L;
  private static final long COMPANY_ID = 43L;
  @Mock
  private User user;
  @Mock
  private Profile profile;
  @Mock
  private Company company;
  @Mock
  private Company yayCompany;
  @Mock
  private UserAssetAssociation userAssetAssociation;
  @Mock
  private Asset asset;

  @Mock
  private CompanyService companyService;
  @Mock
  private UserService userService;

  @InjectMocks
  private UserAndCompanyInfoController controller = new UserAndCompanyInfoController();

  @Before
  public void setUp() {
    final UserDTO userDTO = new UserDTO();
    userDTO.setFirstName(USER_FIRST_NAME);
    userDTO.setLastName(USER_LAST_NAME);
    userDTO.setUuid(USER_UUID);
    userDTO.setCompanyId(COMPANY_ID);

    when(user.getUuid()).thenReturn(USER_UUID);
    when(user.getUserNumber()).thenReturn(USER_NUMBER);
    when(user.getCompany()).thenReturn(company);
    when(user.getProfile()).thenReturn(profile);
    when(company.getId()).thenReturn(37L);
    when(companyService.findById(37L)).thenReturn(yayCompany);
    when(yayCompany.getUuid()).thenReturn(COMPANY_UUID);
    when(user.getFirstName()).thenReturn(USER_FIRST_NAME);
    when(user.getLastName()).thenReturn(USER_LAST_NAME);
    when(user.getEmail()).thenReturn(USER_EMAIL);
    when(user.getId()).thenReturn(42L);
    when(profile.getJobTitle()).thenReturn("EMPLOYEE");
    when(userService.findUserDTOsOfAllActiveEmployees(COMPANY_ID, false)).thenReturn(ImmutableList.of(userDTO));
    when(userService.findUserAvatars(anyCollectionOf(Long.class))).thenReturn(ImmutableList.of(userAssetAssociation));
    when(userService.findUserAvatars(USER_ID)).thenReturn(userAssetAssociation);
    when(userAssetAssociation.getAsset()).thenReturn(asset);
    when(userAssetAssociation.getEntity()).thenReturn(user);
    when(asset.getDownloadableUri()).thenReturn("AVATAR_URL");
  }

  @Test
  public void getUserProfileByUuid() {
    when(userService.findUserIdByUuid(USER_UUID)).thenReturn(42L);
    when(userService.getUser(42L)).thenReturn(user);
    final Response<InternalUserAtCompanyDetails> response = controller.getUserProfile(InternalUserFindByRequest.newBuilder()
        .setQueryBy(QueryBy.USER_BY_UUID).setQueryParameter(USER_UUID).build());
    final InternalUserAtCompanyDetails res = response.getResults().get(0);
    assertTrue(res.getUserProfileDetails().getFound());
    assertEquals(USER_UUID, res.getUuid());
    assertEquals(USER_ID, res.getUserProfileDetails().getDeprecatedNumericId());
    assertEquals(USER_EMAIL, res.getUserAtCompanyEmail());
    assertEquals("JOE", res.getFirstName());
    assertEquals("RANDOM", res.getLastName());
    assertEquals(COMPANY_UUID, res.getCompanyUuid());
  }

  @Test
  public void getUserProfileByEmail() {
    when(userService.findUserIdByEmail(USER_EMAIL)).thenReturn(42L);
    when(userService.getUser(42L)).thenReturn(user);
    final Response<InternalUserAtCompanyDetails> response = controller.getUserProfile(InternalUserFindByRequest.newBuilder()
        .setQueryBy(QueryBy.USER_BY_EMAIL).setQueryParameter(USER_EMAIL).build());
    final InternalUserAtCompanyDetails res = response.getResults().get(0);
    assertTrue(res.getUserProfileDetails().getFound());
    assertEquals(USER_UUID, res.getUuid());
    assertEquals(USER_ID, res.getUserProfileDetails().getDeprecatedNumericId());
    assertEquals(USER_EMAIL, res.getUserAtCompanyEmail());
    assertEquals("JOE", res.getFirstName());
    assertEquals("RANDOM", res.getLastName());
    assertEquals(COMPANY_UUID, res.getCompanyUuid());
  }

  @Test
  public void getCompanyEmployees() {
    when(companyService.findCompanyIdByUuid(COMPANY_UUID)).thenReturn(COMPANY_ID);
    final Response<InternalCompanyEmployeesResponse> response = controller.getCompanyEmployees(
        InternalCompanyEmployeesRequest.newBuilder()
            .setCompanyUuid(COMPANY_UUID)
            .build());
    final InternalCompanyEmployeesResponse res = response.getResults().get(0);
    final InternalUserAtCompanyDetails profileResponse = res.getUsers(0);
    assertEquals(COMPANY_UUID, res.getCompanyUuid());
    assertEquals(USER_UUID, profileResponse.getUuid());
    assertEquals(USER_FIRST_NAME, profileResponse.getFirstName());
    assertEquals(USER_LAST_NAME, profileResponse.getLastName());
  }
}
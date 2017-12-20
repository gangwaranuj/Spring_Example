package com.workmarket.service.business;

import com.workmarket.domains.model.CallingCode;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.redis.RedisAdapter;
import com.workmarket.service.business.dto.UserDTO;
import com.workmarket.service.business.dto.UserImportDTO;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.business.InvariantDataService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserBulkUploadServiceTest {

  @Mock
  User user;
  @Mock
  Company company;
  @Mock
  UserServiceImpl userService;
  @Mock
  RedisAdapter redisAdapter;
  @Mock
  AuthenticationService authenticationService;
  @Mock
  RegistrationService registrationService;
  @Mock
  InvariantDataService invariantDataService;
  @Mock
  ProfileService profileService;
  @InjectMocks
  final UserBulkUploadService userBulkUploadService = new UserBulkUploadServiceImpl();

  private static final String UUID = "07b577d4-c2be-41e6-ae28-cf9a2e52e90e";
  private static final Long userId = 999999L;

  @Before
  public void setup() throws Exception {
    final User newUser = new User();
    newUser.setId(1L);
    final AclRole aclRole = new AclRole();
    aclRole.setId(1L);
    final CallingCode callingCode = new CallingCode();
    callingCode.setId(1L);

    when(userService.findUserById(userId)).thenReturn(user);
    when(authenticationService.findSystemRoleByName(anyString())).thenReturn(aclRole);
    when(registrationService.registerNewForCompany(
        any(UserDTO.class),
        anyLong(),
        any(Long[].class)))
    .thenReturn(newUser);

    when(user.getCompany()).thenReturn(company);
    when(company.getId()).thenReturn(1L);
    when(invariantDataService.findCallingCodeFromCallingCodeId(anyString())).thenReturn(callingCode);
  }

  @Test
  public void testUploadFailure_MakesCallToRegistrationServiceToMarkUserAsDeleted() {
    when(invariantDataService.findCallingCodeFromCallingCodeId(anyString())).thenReturn(null);
    final UserImportDTO userImportDTO = new UserImportDTO();
    userImportDTO.setFirstName("first_name");
    userImportDTO.setLastName("last_name");
    userImportDTO.setEmail("test@email.com");

    userBulkUploadService.upload(
        userId,
        UUID,
        userImportDTO,
        null);

    verify(registrationService, times(1)).updateUserStatusToDeleted(userImportDTO.getEmail());
  }
}

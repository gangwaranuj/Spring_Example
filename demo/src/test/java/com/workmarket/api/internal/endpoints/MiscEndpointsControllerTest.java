package com.workmarket.api.internal.endpoints;

import com.fasterxml.jackson.core.type.TypeReference;
import com.workmarket.api.BaseApiControllerTest;
import com.workmarket.api.helpers.AccountServices;
import com.workmarket.api.internal.model.UserRegistration;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.v2.worker.controllers.ProfileController;
import com.workmarket.domains.model.User;
import com.workmarket.dto.AddressDTO;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.dto.CreateNewWorkerRequest;
import com.workmarket.service.business.dto.CreateNewWorkerResponse;
import com.workmarket.service.business.dto.UserDTO;
import com.workmarket.utility.RandomUtilities;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by joshlevine on 4/28/17.
 */
@org.junit.runner.RunWith(org.mockito.runners.MockitoJUnitRunner.class)
public class MiscEndpointsControllerTest  extends BaseApiControllerTest {
  private static final TypeReference<ApiV2Response<UserRegistration>> apiV2ResponseType = new TypeReference<ApiV2Response<UserRegistration>>() {};

  public static final String ENDPOINT_V2_WARP_REGISTER = "/api/v2/register.api";

  @Mock private AccountServices accountServices;
  @InjectMocks private MiscEndpointsController controller = new MiscEndpointsController();

  @Before
  public void setup() throws Exception {
    super.setup(controller);
  }

  @Test
  public void registerIsAvailable() throws Exception {
    String userName = "worker" + RandomUtilities.generateNumericString(10);
    UserRegistration newWorker = new UserRegistration();
    newWorker.setFirstName("workerFirstName" + RandomUtilities.generateNumericString(10));
    newWorker.setLastName("workerLastName" + RandomUtilities.generateNumericString(10));
    newWorker.setEmail(userName + "@workmarket.com");
    newWorker.setOnboardCompleted(false);
    newWorker.setAutoConfirmEmail(true);
    newWorker.setSendConfirmEmail(true);

    User user = new User();
    user.setId(1978L);
    user.setEmail(newWorker.getEmail());

    when(accountServices.registerNewUser(
      any(UserDTO.class),
      any(AddressDTO.class),
      eq(newWorker.getSendConfirmEmail()),
      eq(newWorker.isOnboardCompleted()),
      eq(newWorker.getAutoConfirmEmail()))).thenReturn(user);

    MvcResult result = mockMvc.perform(MockMvcRequestBuilders
      .get(ENDPOINT_V2_WARP_REGISTER)
      .header("accept", MediaType.APPLICATION_JSON)
      .contentType(MediaType.APPLICATION_JSON)
      .param("email", newWorker.getEmail())
      .param("firstName", newWorker.getFirstName())
      .param("onboardCompleted", "" + newWorker.isOnboardCompleted())
      .param("autoConfirmEmail", "" + newWorker.getAutoConfirmEmail())
      .param("sendConfirmEmail", "" + newWorker.getSendConfirmEmail())
      .content(jackson.writeValueAsString(newWorker))).andExpect(status().isOk()).andReturn();

    final ApiV2Response<UserRegistration> response = expectApiV2Response(result, apiV2ResponseType);
    expectStatusCode(HttpStatus.OK.value(), response.getMeta());
    assertEquals("Expect email of new user matches", response.getResults().get(0).getEmail(), user.getEmail());
  }
}
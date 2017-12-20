package com.workmarket.api.v2.employer.assignments.services;

import com.workmarket.api.v2.employer.assignments.models.AssignmentDTO;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.integration.hooks.webhook.WebHookEventService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.thrift.core.ConstraintViolation;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.thrift.work.Work;
import com.workmarket.thrift.work.WorkAuthorizationException;
import com.workmarket.thrift.work.WorkResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AssignmentServiceImplTest {
  final AssignmentDTO dto = new AssignmentDTO.Builder().build();

  @Mock
  UseCaseFactory useCaseFactory;
  @InjectMocks
  AssignmentServiceImpl assignmentService;

  @Mock
  private WebHookEventService webHookEventService;
  @Mock
  private AuthenticationService authenticationService;
  @InjectMocks
  CreateAssignmentUseCase createAssignmentUseCase = spy(buildUseCase());

  @Before
  public void setup() {
    when(authenticationService.getCurrentUserCompanyId()).thenReturn(1L);
  }

  @Test
  public void verifyOnCreateWebhook() throws ValidationException, WorkAuthorizationException {
    when(useCaseFactory.getUseCase(CreateAssignmentUseCase.class, dto, false)).thenReturn(createAssignmentUseCase);

    assignmentService.create(dto, false);

    verify(webHookEventService).onWorkCreated(any(Long.class), any(Long.class), any(Long.class));
  }

  @Test
  public void verifyOnCreateWebhookNotCalledOnError() throws ValidationException, WorkAuthorizationException {
    when(useCaseFactory.getUseCase(CreateAssignmentUseCase.class, dto, false)).thenReturn(createAssignmentUseCase);
    doThrow(new ValidationException("uh-oh", new ArrayList<ConstraintViolation>()))
        .when(createAssignmentUseCase).saveWork();

    try {
      assignmentService.create(dto, false);
      fail();
    } catch (final ValidationException e) {
      assertEquals("uh-oh", e.getWhy());
    }

    verify(webHookEventService, times(0)).onWorkCreated(any(Long.class), any(Long.class), any(Long.class));
  }

  @Test
  public void validNumberOfCopies_createMultiple_multipleCopiesCreated() throws ValidationException, WorkAuthorizationException {
    when(useCaseFactory.getUseCase(CreateAssignmentUseCase.class, dto, false)).thenReturn(createAssignmentUseCase);

    final int numberOfCopies = assignmentService.getAssignmentCopyQuantities().get(0);

    List<AssignmentDTO> results = assignmentService.createMultiple(dto, numberOfCopies, false);

    assertEquals(numberOfCopies, results.size());
  }

  @Test
  public void zeroCopiesSpecified_createMultiple_exceptionThrown() throws ValidationException, WorkAuthorizationException {
    when(useCaseFactory.getUseCase(CreateAssignmentUseCase.class, dto, false)).thenReturn(createAssignmentUseCase);

    try {
      assignmentService.createMultiple(dto, 0, false);
      fail();
    } catch (final ValidationException e) {
      assertThat(e.getWhy(), containsString("Invalid number of copies"));
    }
  }

  @Test
  public void invalidNumberOfCopies_createMultiple_exceptionThrown() throws ValidationException, WorkAuthorizationException {
    when(useCaseFactory.getUseCase(CreateAssignmentUseCase.class, dto, false)).thenReturn(createAssignmentUseCase);

    final int invalidNumberOfCopies = 1000;

    assertFalse("Number of copies is invalid. ", assignmentService.getAssignmentCopyQuantities().contains(invalidNumberOfCopies));

    try {
      assignmentService.createMultiple(dto, invalidNumberOfCopies, false);
      fail();
    } catch (final ValidationException e) {
      assertThat(e.getWhy(), containsString("Invalid number of copies"));
    }
  }

  private CreateAssignmentUseCase buildUseCase() {
    return new CreateAssignmentUseCase() {
      @Override
      protected void saveWork() throws ValidationException, WorkAuthorizationException {
        this.workResponse = mock(WorkResponse.class);
        when(workResponse.getWork()).thenReturn(mock(Work.class));
      }
      @Override
      protected void sendWork() throws WorkAuthorizationException { }
      @Override
      protected void failFast() { }
      @Override
      protected void getUser() { }
      @Override
      protected void getScheduleDTO() { }
      @Override
      protected void getLocationDTO() { }
      @Override
      protected void getLocationContactDTO() { }
      @Override
      protected void getAssignmentDTO() { }
      @Override
      protected void getConfigurationDTO() { }
      @Override
      protected void getPricingDTO() { }
      @Override
      protected void getRoutingDTO() { }
      @Override
      protected void getDocumentDTOs() { }
      @Override
      protected void getShipmentDTOs() { }
      @Override
      protected void getSurveyDTOs() { }
      @Override
      protected void getDeliverablesGroupDTO() { }
      @Override
      protected void getSecondaryLocationContactDTO() { }
      @Override
      protected void getCustomFieldGroupDTOs() { }
      @Override
      protected void copyAssignmentDTO() { }
      @Override
      protected void copyConfigurationDTO() { }
      @Override
      protected void copyCustomFieldGroupDTOs() { }
      @Override
      protected void copyDeliverablesGroupDTO() { }
      @Override
      protected void copyDocumentDTOs() { }
      @Override
      protected void copyLocationContactDTO() { }
      @Override
      protected void copyLocationDTO() { }
      @Override
      protected void copyPricingDTO() { }
      @Override
      protected void copyRoutingDTO() { }
      @Override
      protected void copyScheduleDTO() { }
      @Override
      protected void copySecondaryLocationContactDTO() { }
      @Override
      protected void copyShipmentGroupDTO() { }
      @Override
      protected void copySurveyDTOs() { }
      @Override
      protected void copyTemplateDTO() { }
      @Override
      protected void loadSchedule() { }
      @Override
      protected void loadAddress() { }
      @Override
      protected void loadBuyer() { }
      @Override
      protected void loadSupportContact() { }
      @Override
      protected void loadLocation() { }
      @Override
      protected void loadPricing() { }
      @Override
      protected void loadDocuments() { }
      @Override
      protected void loadRouting() { }
      @Override
      protected void loadDeliverablesGroup() { }
      @Override
      protected void loadConfiguration() { }
      @Override
      protected void loadCustomFieldGroups() { }
      @Override
      protected void loadLocationContact() { }
      @Override
      protected void loadSecondaryLocationContact() { }
      @Override
      protected void createDocumentUploads() { }
      @Override
      protected void loadWork() { }
      @Override
      protected void loadTemplate() { }
      @Override
      protected void loadShipments() { }
      @Override
      protected void generateWorkRequest() { }
      @Override
      protected void generateWorkSaveRequest() { }
      @Override
      protected void saveRecurrence() { }
    };
  }

}
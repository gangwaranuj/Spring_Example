package com.workmarket.service.business.integration.event;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.workmarket.domains.model.MboProfile;
import com.workmarket.domains.model.Profile;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.integration.IntegrationEventType;
import com.workmarket.domains.model.integration.webhook.AbstractWebHookClient;
import com.workmarket.domains.model.integration.webhook.WebHook;
import com.workmarket.domains.model.note.Note;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.negotiation.AbstractWorkNegotiation;
import com.workmarket.domains.work.model.state.WorkSubStatusTypeAssociation;
import com.workmarket.domains.work.service.WorkNegotiationService;
import com.workmarket.domains.work.service.WorkNoteService;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.domains.work.service.state.WorkSubStatusService;
import com.workmarket.feature.gen.Messages.FeatureToggle;
import com.workmarket.feature.gen.Messages.Status;
import com.workmarket.feature.vo.FeatureToggleAndStatus;
import com.workmarket.integration.webhook.WebHookDispatchField;
import com.workmarket.service.business.AssetManagementService;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.integration.ParsedWebHookDTO;
import com.workmarket.service.business.integration.hooks.webhook.WebHookHTTPPoolingFactory;
import com.workmarket.service.business.integration.hooks.webhook.WebHookIntegrationService;
import com.workmarket.service.business.integration.mbo.MboProfileDAO;
import com.workmarket.service.business.integration.mbo.SalesForceClient;
import com.workmarket.service.featuretoggle.FeatureEntitlementService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.file.AWSConfigData;
import com.workmarket.service.infra.security.SecurityContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.util.Map;

import rx.Observable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class IntegrationListenerServiceImplTest {

  @Mock private SalesForceClient salesForceClient;
  @Mock private ProfileService profileService;
  @Mock private MboProfileDAO mboProfileDAO;
  @Mock private WorkService workService;
  @Mock FeatureEntitlementService featureEntitlementService;
  @Mock SecurityContext securityContext;
  @Mock MetricRegistry metricRegistry;
  @Mock WebHookIntegrationService webHookIntegrationService;
  @Mock UserService userService;
  @Mock AssetManagementService assetService;
  @Mock WorkNoteService workNoteService;
  @Mock WorkSubStatusService workSubStatusService;
  @Mock WorkNegotiationService workNegotiationService;
  @Mock WebHookHTTPPoolingFactory webHookHTTPPoolingFactory;
  @Mock AWSConfigData awsConfigData;
  @Mock AuthenticationService authenticationService;

  @InjectMocks
  private IntegrationListenerServiceImpl integrationListenerService = spy(new IntegrationListenerServiceImpl());

  private User resource = mock(User.class);
  private WorkResource workResource = mock(WorkResource.class);
  private Work work = mock(Work.class);
  private User buyer = mock(User.class);
  private Profile buyerProfile = mock(Profile.class);
  private MboProfile mboProfile = mock(MboProfile.class);

  @Before
  public void setup() {
    when(work.getId()).thenReturn(1L);
    when(resource.getId()).thenReturn(1L);
    when(work.getBuyer()).thenReturn(buyer);
    when(buyer.getProfile()).thenReturn(buyerProfile);
    when(profileService.findMboProfile(anyLong())).thenReturn(null);
    when(workResource.getUser()).thenReturn(resource);

    when(workService.findWork(work.getId())).thenReturn(work);
    when(workService.findActiveWorkResource(work.getId())).thenReturn(workResource);
    when(metricRegistry.meter(anyString())).thenReturn(new Meter());
    when(authenticationService.getCurrentUserWithFallback()).thenReturn(new User());
    integrationListenerService.init();
  }

  @Test
  public void processWorkAcceptedEvent_doNotNotifyMbo_doesNotCallCreateOpportunity() throws Exception {
    Map<String, Object> eventArguments = Maps.newHashMap();
    eventArguments.put(IntegrationEvent.IS_AUTOTASK, false);
    eventArguments.put(IntegrationEvent.WEBHOOK_ID, null);
    eventArguments.put(IntegrationEvent.RESOURCE_ID, resource.getId());
    eventArguments.put(IntegrationEvent.NOTIFY_MBO, false);

    integrationListenerService.onWorkAccepted(work.getId(), eventArguments);

    verify(salesForceClient, never()).createOpportunity(any(AbstractWork.class), any(MboProfile.class));
  }

  @Test
  public void processWorkAcceptedEvent_notifyMbo_callsCreateOpportunity() throws Exception {
    Map<String, Object> eventArguments = Maps.newHashMap();
    eventArguments.put(IntegrationEvent.IS_AUTOTASK, false);
    eventArguments.put(IntegrationEvent.WEBHOOK_ID, null);
    eventArguments.put(IntegrationEvent.RESOURCE_ID, resource.getId());
    eventArguments.put(IntegrationEvent.NOTIFY_MBO, true);
    when(profileService.findMboProfile(anyLong())).thenReturn(mboProfile);
    integrationListenerService.onWorkAccepted(work.getId(), eventArguments);

    verify(salesForceClient, times(1)).createOpportunity(any(AbstractWork.class), any(MboProfile.class));
  }

  @Test
  public void shouldNotSuppressApiEventsWhenUserIsNotApiEnabled() {
    final WebHook webhook = mock(WebHook.class);
    final AbstractWebHookClient client = mock(AbstractWebHookClient.class);
    final User user = mock(User.class);
    final IntegrationEventType integrationEventType = mock(IntegrationEventType.class);
    when(webhook.getIntegrationEventType()).thenReturn(integrationEventType);
    when(webhook.getIntegrationEventType()).thenReturn(IntegrationEventType.newInstance("some-event"));
    when(integrationEventType.getCode()).thenReturn("some-code");
    when(client.isSuppressApiEvents()).thenReturn(true);
    when(user.isApiEnabled()).thenReturn(false);

    assertFalse(integrationListenerService.shouldSuppressWebhook(user, webhook));
  }

  @Test
  public void shouldSuppressApiEventsWhenUserIsApiEnabled() {
    final WebHook webhook = mock(WebHook.class);
    final AbstractWebHookClient client = mock(AbstractWebHookClient.class);
    final User user = mock(User.class);
    when(webhook.getWebHookClient()).thenReturn(client);
    when(webhook.getIntegrationEventType()).thenReturn(IntegrationEventType.newInstance("some-event"));
    when(client.isSuppressApiEvents()).thenReturn(true);
    when(user.isApiEnabled()).thenReturn(true);

    assertTrue(integrationListenerService.shouldSuppressWebhook(user, webhook));
  }

  @Test
  public void shouldNotSuppressApiEventsIfUserIsApiEnabledAndClientIsFalse() {
    final WebHook webhook = mock(WebHook.class);
    final AbstractWebHookClient client = mock(AbstractWebHookClient.class);
    final User user = mock(User.class);
    final IntegrationEventType integrationEventType = mock(IntegrationEventType.class);
    when(webhook.getIntegrationEventType()).thenReturn(integrationEventType);
    when(integrationEventType.getCode()).thenReturn("some-code");
    when(webhook.getWebHookClient()).thenReturn(client);
    when(webhook.getIntegrationEventType()).thenReturn(IntegrationEventType.newInstance("some-event"));
    when(client.isSuppressApiEvents()).thenReturn(false);
    when(user.isApiEnabled()).thenReturn(true);

    assertFalse(integrationListenerService.shouldSuppressWebhook(user, webhook));
  }

  @Test
  public void shouldSuppressApiEventsIfUserIsApiEnabledAndFeatureToggleTrueAndWebhookSuppressionEnabled() {
    final WebHook webhook = mock(WebHook.class);
    final AbstractWebHookClient client = mock(AbstractWebHookClient.class);
    final User user = mock(User.class);
    final IntegrationEventType integrationEventType = mock(IntegrationEventType.class);
    when(webhook.getIntegrationEventType()).thenReturn(integrationEventType);
    when(integrationEventType.getCode()).thenReturn("some-code");
    when(webhook.getWebHookClient()).thenReturn(client);
    when(webhook.suppressApiEvents()).thenReturn(true);
    when(webhook.getIntegrationEventType()).thenReturn(IntegrationEventType.newInstance("some-event"));
    when(user.isApiEnabled()).thenReturn(true);
    when(featureEntitlementService.hasFeatureToggle(any(Long.class), any(String.class))).thenReturn(true);

    assertTrue(integrationListenerService.shouldSuppressWebhook(user, webhook));
  }

  @Test
  public void shouldNotSuppressApiEventsWhenEventIsNotFromApi() {
    final WebHook webhook = mock(WebHook.class);
    final AbstractWebHookClient client = mock(AbstractWebHookClient.class);
    final IntegrationEventType integrationEventType = mock(IntegrationEventType.class);
    when(integrationEventType.getCode()).thenReturn("some-code");
    when(webhook.getIntegrationEventType()).thenReturn(integrationEventType);
    when(webhook.getWebHookClient()).thenReturn(client);
    when(webhook.getIntegrationEventType()).thenReturn(IntegrationEventType.newInstance("some-event"));
    when(client.isSuppressApiEvents()).thenReturn(true);

    assertFalse(integrationListenerService.shouldSuppressWebhook(
        ImmutableMap.<String, Object>of(IntegrationEvent.IS_API_TRIGGERED, Boolean.FALSE), webhook));
  }

  @Test
  public void shouldSuppressApiEventsWhenEventIsFromApi() {
    final WebHook webhook = mock(WebHook.class);
    final AbstractWebHookClient client = mock(AbstractWebHookClient.class);
    when(webhook.getWebHookClient()).thenReturn(client);
    when(webhook.getIntegrationEventType()).thenReturn(IntegrationEventType.newInstance("some-event"));
    when(client.isSuppressApiEvents()).thenReturn(true);

    assertTrue(integrationListenerService.shouldSuppressWebhook(
        ImmutableMap.<String, Object>of(IntegrationEvent.IS_API_TRIGGERED, Boolean.TRUE), webhook));
  }

  @Test
  public void shouldSuppressApiEventsWhenEventIsFromApiAndFeatureToggleOnAndWebhookSupressionTrue() {
    final WebHook webhook = mock(WebHook.class);
    final AbstractWebHookClient client = mock(AbstractWebHookClient.class);
    when(webhook.getWebHookClient()).thenReturn(client);
    when(webhook.suppressApiEvents()).thenReturn(true);
    when(webhook.getIntegrationEventType()).thenReturn(IntegrationEventType.newInstance("some-event"));
    when(client.isSuppressApiEvents()).thenReturn(true);
    when(featureEntitlementService.hasFeatureToggle(any(Long.class), any(String.class))).thenReturn(true);

    assertTrue(integrationListenerService.shouldSuppressWebhook(
        ImmutableMap.<String, Object>of(IntegrationEvent.IS_API_TRIGGERED, Boolean.TRUE), webhook));
  }

  @Test
  public void shouldNotSuppressApiEventsIfApiTriggeredAndClientFalse() {
    final WebHook webhook = mock(WebHook.class);
    final AbstractWebHookClient client = mock(AbstractWebHookClient.class);
    final IntegrationEventType integrationEventType = mock(IntegrationEventType.class);
    when(webhook.getIntegrationEventType()).thenReturn(integrationEventType);
    when(webhook.getIntegrationEventType()).thenReturn(IntegrationEventType.newInstance("some-event"));
    when(integrationEventType.getCode()).thenReturn("some-code");
    when(webhook.getWebHookClient()).thenReturn(client);
    when(client.isSuppressApiEvents()).thenReturn(false);

    assertFalse(integrationListenerService.shouldSuppressWebhook(
        ImmutableMap.<String, Object>of(IntegrationEvent.IS_API_TRIGGERED, Boolean.TRUE), webhook));
  }

  @Test
  public void suppressWebhookOnWorkCreated() {
    final AbstractWork work = mock(AbstractWork.class);
    final WebHook webhook = mock(WebHook.class);
    final AbstractWebHookClient client = mock(AbstractWebHookClient.class);
    final User user = mock(User.class);
    when(client.isSuppressApiEvents()).thenReturn(true);
    when(webhook.getWebHookClient()).thenReturn(client);
    when(webhook.getIntegrationEventType()).thenReturn(IntegrationEventType.newInstance("some-event"));
    when(user.isApiEnabled()).thenReturn(true);
    when(workService.findWork(any(Long.class))).thenReturn(work);
    when(webHookIntegrationService.getWebHook(any(Long.class))).thenReturn(Optional.of(webhook));
    when(userService.findUserById(any(Long.class))).thenReturn(user);

    assertTrue(integrationListenerService.onWorkCreated(1L, 1L));
    verify(integrationListenerService, times(0))
        .runWebHook(any(AbstractWork.class), any(WebHook.class), any(Map.class));
  }

  @Test
  public void suppressWebhookOnAttachmentAdded() {
    final AbstractWork work = mock(AbstractWork.class);
    final WebHook webhook = mock(WebHook.class);
    final AbstractWebHookClient client = mock(AbstractWebHookClient.class);
    final User user = mock(User.class);
    when(client.isSuppressApiEvents()).thenReturn(true);
    when(webhook.getWebHookClient()).thenReturn(client);
    when(webhook.getIntegrationEventType()).thenReturn(IntegrationEventType.newInstance("some-event"));
    when(user.isApiEnabled()).thenReturn(true);
    when(workService.findWork(any(Long.class))).thenReturn(work);
    when(webHookIntegrationService.getWebHook(any(Long.class))).thenReturn(Optional.of(webhook));
    when(userService.findCreatorByAssetId(any(Long.class))).thenReturn(user);
    when(assetService.findAssetById(any(Long.class))).thenReturn(mock(Asset.class));

    assertTrue(integrationListenerService.onAttachmentAdded(1L, ImmutableMap.<String, Object>of(
        IntegrationEvent.IS_AUTOTASK, Boolean.FALSE,
        IntegrationEvent.ASSET_ID, 1L,
        IntegrationEvent.WEBHOOK_ID, 1L)));
    verify(integrationListenerService, times(0))
        .runWebHook(any(AbstractWork.class), any(WebHook.class), any(Map.class));
  }

  @Test
  public void runWebhookOnAttachmentAdded() {
    // Webhook relay service disabled
    final AbstractWork work = mock(AbstractWork.class);
    final WebHook webhook = mock(WebHook.class);
    final AbstractWebHookClient client = mock(AbstractWebHookClient.class);
    final User user = mock(User.class);
    when(client.isSuppressApiEvents()).thenReturn(false);
    when(webhook.getWebHookClient()).thenReturn(client);
    when(webhook.getIntegrationEventType()).thenReturn(IntegrationEventType.newInstance("some-event"));
    when(user.isApiEnabled()).thenReturn(true);
    when(workService.findWork(any(Long.class))).thenReturn(work);
    when(webHookIntegrationService.getWebHook(any(Long.class))).thenReturn(Optional.of(webhook));
    when(userService.findCreatorByAssetId(any(Long.class))).thenReturn(user);
    final Asset asset = new Asset("asset-name", "asset-description", "asset-uuid", "asset-type");
    when(assetService.findAssetById(any(Long.class))).thenReturn(asset);
    when(awsConfigData.getRemoteFileEnvironment()).thenReturn("");
    final FeatureToggleAndStatus featureToggleAndStatus = mock(FeatureToggleAndStatus.class);
    when(featureToggleAndStatus.getStatus()).thenReturn(Status.newBuilder().setSuccess(false).build());
    when(featureEntitlementService.getFeatureToggleForCurrentUser(any(String.class))).thenReturn(Observable.just(featureToggleAndStatus));
    when(webHookHTTPPoolingFactory.buildHook(any(Work.class), any(WebHook.class), any(Map.class), any(Map.class))).thenReturn(Optional.of(new ParsedWebHookDTO()));
    when(webHookHTTPPoolingFactory.launchHook(any(WebHook.class), any(ParsedWebHookDTO.class))).thenReturn(HttpStatus.OK);

    assertTrue(integrationListenerService.onAttachmentAdded(1L, ImmutableMap.<String, Object>of(
        IntegrationEvent.IS_AUTOTASK, Boolean.FALSE,
        IntegrationEvent.ASSET_ID, 1L,
        IntegrationEvent.WEBHOOK_ID, 1L)));

    verify(integrationListenerService, times(1))
        .runWebHook(any(AbstractWork.class), any(WebHook.class), any(Map.class));

    ArgumentCaptor<Map> contextVariablesNoRelayService = ArgumentCaptor.forClass(Map.class);
    verify(webHookHTTPPoolingFactory).buildHook(any(AbstractWork.class), any(WebHook.class), contextVariablesNoRelayService.capture(), any(Map.class));
    assertEquals("asset-uuid", contextVariablesNoRelayService.getValue().get(WebHookDispatchField.FILE_UUID.getFieldName()));
    assertEquals("asset-name", contextVariablesNoRelayService.getValue().get(WebHookDispatchField.FILE_NAME.getFieldName()));
    assertEquals("asset-description", contextVariablesNoRelayService.getValue().get(WebHookDispatchField.FILE_DESCRIPTION.getFieldName()));
    assertEquals(null, contextVariablesNoRelayService.getValue().get(WebHookDispatchField.FILE_DATA_BASE_64.getFieldName()));
    assertEquals(null, contextVariablesNoRelayService.getValue().get(WebHookDispatchField.FILE_DATA_RAW.getFieldName()));
    assertEquals(null, contextVariablesNoRelayService.getValue().get(WebHookDispatchField.FILE_DATA.getFieldName()));
    assertEquals(null, contextVariablesNoRelayService.getValue().get(WebHookDispatchField.FILE_DATA_BYTE_LENGTH.getFieldName()));

    // Webhook relay service enabled

    when(featureToggleAndStatus.getStatus()).thenReturn(Status.newBuilder().setSuccess(true).build());
    when(featureToggleAndStatus.getFeatureToggle()).thenReturn(FeatureToggle.newBuilder().setValue("true").build());

    assertTrue(integrationListenerService.onAttachmentAdded(1L, ImmutableMap.<String, Object>of(
        IntegrationEvent.IS_AUTOTASK, Boolean.FALSE,
        IntegrationEvent.ASSET_ID, 1L,
        IntegrationEvent.WEBHOOK_ID, 1L)));

    verify(integrationListenerService, times(2))
        .runWebHook(any(AbstractWork.class), any(WebHook.class), any(Map.class));

    ArgumentCaptor<Map> contextVariablesWithRelayService = ArgumentCaptor.forClass(Map.class);
    verify(webHookHTTPPoolingFactory, times(2))
        .buildHook(any(AbstractWork.class), any(WebHook.class), contextVariablesWithRelayService.capture(), any(Map.class));
    assertEquals("asset-uuid", contextVariablesWithRelayService.getValue().get(WebHookDispatchField.FILE_UUID.getFieldName()));
    assertEquals("asset-name", contextVariablesWithRelayService.getValue().get(WebHookDispatchField.FILE_NAME.getFieldName()));
    assertEquals("asset-description", contextVariablesWithRelayService.getValue().get(WebHookDispatchField.FILE_DESCRIPTION.getFieldName()));
    assertEquals(String.format("${%s}", WebHookDispatchField.FILE_DATA_BASE_64.getFieldName()), contextVariablesWithRelayService.getValue().get(WebHookDispatchField.FILE_DATA_BASE_64.getFieldName()));
    assertEquals(String.format("${%s}", WebHookDispatchField.FILE_DATA_RAW.getFieldName()), contextVariablesWithRelayService.getValue().get(WebHookDispatchField.FILE_DATA_RAW.getFieldName()));
    assertEquals(String.format("${%s}", WebHookDispatchField.FILE_DATA.getFieldName()), contextVariablesWithRelayService.getValue().get(WebHookDispatchField.FILE_DATA.getFieldName()));
    assertEquals(String.format("${%s}", WebHookDispatchField.FILE_DATA_BYTE_LENGTH.getFieldName()), contextVariablesWithRelayService.getValue().get(WebHookDispatchField.FILE_DATA_BYTE_LENGTH.getFieldName()));
  }

  @Test
  public void suppressWebhookOnAttachmentRemoved() {
    final AbstractWork work = mock(AbstractWork.class);
    final WebHook webhook = mock(WebHook.class);
    final AbstractWebHookClient client = mock(AbstractWebHookClient.class);
    final User user = mock(User.class);
    when(client.isSuppressApiEvents()).thenReturn(true);
    when(webhook.getWebHookClient()).thenReturn(client);
    when(webhook.getIntegrationEventType()).thenReturn(IntegrationEventType.newInstance("some-event"));
    when(user.isApiEnabled()).thenReturn(true);
    when(workService.findWork(any(Long.class))).thenReturn(work);
    when(webHookIntegrationService.getWebHook(any(Long.class))).thenReturn(Optional.of(webhook));
    when(userService.findModifierByAssetId(any(Long.class))).thenReturn(user);
    when(assetService.findAssetById(any(Long.class))).thenReturn(mock(Asset.class));

    assertTrue(integrationListenerService.onAttachmentRemoved(1L, ImmutableMap.<String, Object>of(
        IntegrationEvent.ASSET_ID, 1L,
        IntegrationEvent.WEBHOOK_ID, 1L)));
    verify(integrationListenerService, times(0))
        .runWebHook(any(AbstractWork.class), any(WebHook.class), any(Map.class));
  }

  @Test
  public void suppressWebhookOnNoteAdded() {
    final AbstractWork work = mock(AbstractWork.class);
    final WebHook webhook = mock(WebHook.class);
    final AbstractWebHookClient client = mock(AbstractWebHookClient.class);
    final User user = mock(User.class);
    final Note note = mock(Note.class);
    when(client.isSuppressApiEvents()).thenReturn(true);
    when(webhook.getWebHookClient()).thenReturn(client);
    when(webhook.getIntegrationEventType()).thenReturn(IntegrationEventType.newInstance("some-event"));
    when(user.isApiEnabled()).thenReturn(true);
    when(workService.findWork(any(Long.class))).thenReturn(work);
    when(webHookIntegrationService.getWebHook(any(Long.class))).thenReturn(Optional.of(webhook));
    when(userService.findUserById(any(Long.class))).thenReturn(user);
    when(workNoteService.findNoteById(any(Long.class))).thenReturn(note);

    assertTrue(integrationListenerService.onNoteAdded(1L, ImmutableMap.<String, Object>of(
        IntegrationEvent.IS_AUTOTASK, Boolean.FALSE,
        IntegrationEvent.NOTE_ID, 1L,
        IntegrationEvent.WEBHOOK_ID, 1L)));
    verify(integrationListenerService, times(0))
        .runWebHook(any(AbstractWork.class), any(WebHook.class), any(Map.class));
  }

  @Test
  public void suppressWebhookOnLabelAdded() {
    final AbstractWork work = mock(AbstractWork.class);
    final WebHook webhook = mock(WebHook.class);
    final AbstractWebHookClient client = mock(AbstractWebHookClient.class);
    final User user = mock(User.class);
    final WorkSubStatusTypeAssociation workSubStatusTypeAssociation = mock(WorkSubStatusTypeAssociation.class);
    when(client.isSuppressApiEvents()).thenReturn(true);
    when(webhook.getWebHookClient()).thenReturn(client);
    when(webhook.getIntegrationEventType()).thenReturn(IntegrationEventType.newInstance("some-event"));
    when(user.isApiEnabled()).thenReturn(true);
    when(workService.findWork(any(Long.class))).thenReturn(work);
    when(webHookIntegrationService.getWebHook(any(Long.class))).thenReturn(Optional.of(webhook));
    when(userService.findCreatorByWorkLabelAssociationId(any(Long.class))).thenReturn(user);
    when(workSubStatusService.getAssociation(any(Long.class))).thenReturn(workSubStatusTypeAssociation);

    assertTrue(integrationListenerService.onLabelAdded(1L, ImmutableMap.<String, Object>of(
        IntegrationEvent.WORK_SUBSTATUS_TYPE_ASSOCIATION_ID, 1L,
        IntegrationEvent.WEBHOOK_ID, 1L)));
    verify(integrationListenerService, times(0))
        .runWebHook(any(AbstractWork.class), any(WebHook.class), any(Map.class));
  }

  @Test
  public void suppressWebhookOnLabelRemoved() {
    final AbstractWork work = mock(AbstractWork.class);
    final WebHook webhook = mock(WebHook.class);
    final AbstractWebHookClient client = mock(AbstractWebHookClient.class);
    final User user = mock(User.class);
    final WorkSubStatusTypeAssociation workSubStatusTypeAssociation = mock(WorkSubStatusTypeAssociation.class);
    when(client.isSuppressApiEvents()).thenReturn(true);
    when(webhook.getWebHookClient()).thenReturn(client);
    when(webhook.getIntegrationEventType()).thenReturn(IntegrationEventType.newInstance("some-event"));
    when(user.isApiEnabled()).thenReturn(true);
    when(workService.findWork(any(Long.class))).thenReturn(work);
    when(webHookIntegrationService.getWebHook(any(Long.class))).thenReturn(Optional.of(webhook));
    when(userService.findModifierByWorkLabelAssociationId(any(Long.class))).thenReturn(user);
    when(workSubStatusService.getAssociation(any(Long.class))).thenReturn(workSubStatusTypeAssociation);

    assertTrue(integrationListenerService.onLabelRemoved(1L, ImmutableMap.<String, Object>of(
        IntegrationEvent.WORK_SUBSTATUS_TYPE_ASSOCIATION_ID, 1L,
        IntegrationEvent.WEBHOOK_ID, 1L)));
    verify(integrationListenerService, times(0))
        .runWebHook(any(AbstractWork.class), any(WebHook.class), any(Map.class));
  }

  @Test
  public void suppressWebhookOnWorkRescheduleRequested() {
    final AbstractWork work = mock(AbstractWork.class);
    final WebHook webhook = mock(WebHook.class);
    final AbstractWebHookClient client = mock(AbstractWebHookClient.class);
    final User user = mock(User.class);
    final AbstractWorkNegotiation abstractWorkNegotiation = mock(AbstractWorkNegotiation.class);
    when(client.isSuppressApiEvents()).thenReturn(true);
    when(webhook.getWebHookClient()).thenReturn(client);
    when(webhook.getIntegrationEventType()).thenReturn(IntegrationEventType.newInstance("some-event"));
    when(user.isApiEnabled()).thenReturn(true);
    when(workService.findWork(any(Long.class))).thenReturn(work);
    when(webHookIntegrationService.getWebHook(any(Long.class))).thenReturn(Optional.of(webhook));
    when(userService.findCreatorByWorkNegotiationId(any(Long.class))).thenReturn(user);
    when(workNegotiationService.findById(any(Long.class))).thenReturn(abstractWorkNegotiation);

    assertTrue(integrationListenerService.onWorkRescheduleRequested(1L, ImmutableMap.<String, Object>of(
        IntegrationEvent.NEGOTIATION_ID, 1L,
        IntegrationEvent.WEBHOOK_ID, 1L)));
    verify(integrationListenerService, times(0))
        .runWebHook(any(AbstractWork.class), any(WebHook.class), any(Map.class));
  }
}

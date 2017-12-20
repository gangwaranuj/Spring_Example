package com.workmarket.service.business;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import com.codahale.metrics.MetricRegistry;
import com.workmarket.common.core.RequestContext;
import com.workmarket.core.notification.gen.Messages.Dimension;
import com.workmarket.core.notification.gen.Messages.DimensionValuePair;
import com.workmarket.core.notification.gen.Messages.GetPrefs;
import com.workmarket.core.notification.gen.Messages.GetResp;
import com.workmarket.core.notification.gen.Messages.ModPref;
import com.workmarket.core.notification.gen.Messages.SetPrefs;
import com.workmarket.core.notification.gen.Messages.Status;
import com.workmarket.core.notification.gen.Messages.Type;
import com.workmarket.core.notification.gen.Messages.TypeToValue;
import com.workmarket.core.notification.gen.Messages.TypeValue;
import com.workmarket.dao.UserDAO;
import com.workmarket.dao.notification.UserNotificationPreferenceDAO;
import com.workmarket.dao.user.PersonaPreferenceDAO;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.work.service.part.HibernateTrialWrapper;
import com.workmarket.notification.NotificationClient;
import com.workmarket.service.business.dto.NotificationPreferenceDTO;
import com.workmarket.service.web.WebRequestContextProvider;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.Map;

import rx.Observable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * Test the user notification prefs service.
 */
@RunWith(MockitoJUnitRunner.class)
public class UserNotificationPrefsServiceTest {
  private static final ImmutableMap<String, TypeValue> PREFS_MAP = ImmutableMap.<String, TypeValue>builder()
      .put("type", TypeValue.newBuilder()
          .addTypeValue(TypeToValue.newBuilder()
              .setType(Type.PUSH)
              .setValue(false))
          .addTypeValue(TypeToValue.newBuilder()
              .setType(Type.EMAIL)
              .setValue(true))
          .build())
      .put("type.dispatcher", TypeValue.newBuilder()
          .addTypeValue(TypeToValue.newBuilder()
              .setType(Type.PUSH)
              .setValue(true))
          .build())
      .put("otherType", TypeValue.newBuilder()
          .addTypeValue(TypeToValue.newBuilder()
              .setType(Type.VOICE)
              .setValue(true))
          .build())
      .build();
  private static final Long USER_ID = 1L;
  private static final String USER_UUID = "USER_UUID";
  private static final String COMPANY_UUID = "COMPANY_UUID";

  @Mock private WebRequestContextProvider webRequestContextProvider;
  @Mock private HibernateTrialWrapper hibernateWrapper;
  @Mock private MetricRegistry metricRegistry;
  @Mock private UserNotificationPreferenceDAO userNotificationPreferenceDAO;
  @Mock private PersonaPreferenceDAO personaPreferenceDAO;
  @Mock private NotificationClient notificationsClient;

  @Mock User user;
  @Mock UserDAO userDAO;
  @Mock Company company;

  @InjectMocks private UserNotificationPrefsService service = spy(new UserNotificationPrefsService());
  private static final DimensionValuePair.Builder USER_DVP = DimensionValuePair.newBuilder()
      .setDimension(Dimension.USER)
      .setObjectId(USER_UUID);

  @Before
  public void setUp() throws Exception {
//    when(user.getCompany()).thenReturn(company);
//    when(userDAO.getUser(USER_ID)).thenReturn(user);
//    when(user.getUuid()).thenReturn(USER_UUID);
//    when(company.getUuid()).thenReturn(COMPANY_UUID);
  }

  @Test
  public void loadUserPrefs() {
    when(notificationsClient.getPreferences((GetPrefs) anyObject(), (RequestContext) anyObject())).
        thenReturn(
            Observable.just(GetResp.newBuilder()
                .setStatus(Status.newBuilder().setSuccess(true))
                .putAllPref(PREFS_MAP)
                .build()));

    final Map<String, UserNotificationPreferencePojo> prefs = service
        .loadNotificationPrefs("useruuid", "companyuuid", true)
        .toBlocking().single();

    assertFalse(prefs.containsKey("type.dispatcher"));
    assertTrue(prefs.containsKey("type"));
    final UserNotificationPreferencePojo v = prefs.get("type");
    assertTrue(v.getDispatchPushFlag());
    assertFalse(v.getPushFlag());
    assertTrue(v.getEmailFlag());
    assertFalse(v.getVoiceFlag());
    assertTrue(prefs.containsKey("otherType"));
    assertTrue(prefs.get("otherType").getVoiceFlag());
  }

  @Test
  public void newFindByUserWithDefault() {
    when(notificationsClient.getPreferences((GetPrefs) anyObject(), (RequestContext) anyObject())).
        thenReturn(
            Observable.just(GetResp.newBuilder()
                .setStatus(Status.newBuilder().setSuccess(true))
                .putAllPref(PREFS_MAP)
                .build()));

    final List<UserNotificationPreferencePojo> prefs = service.newFindByUserWithDefault(USER_UUID, COMPANY_UUID)
        .toBlocking().single();
    assertEquals(2, prefs.size());
    final int typeInd;
    final int otherInd;
    if (prefs.get(0).getNotificationType().equals("type")) {
      typeInd = 0;
      otherInd = 1;
    } else {
      typeInd = 1;
      otherInd = 0;
    }
    assertFalse(prefs.get(typeInd).getVoiceFlag());
    assertTrue(prefs.get(typeInd).getEmailFlag());
    assertTrue(prefs.get(otherInd).getVoiceFlag());
  }

  @Test
  public void findByUserAndNotificationType() {
    when(notificationsClient.getPreferences((GetPrefs) anyObject(), (RequestContext) anyObject())).
        thenReturn(
            Observable.just(GetResp.newBuilder()
                .setStatus(Status.newBuilder().setSuccess(true))
                .putAllPref(PREFS_MAP)
                .build()));

    final UserNotificationPreferencePojo result = service.newFindByUserAndNotificationType(
        USER_UUID, COMPANY_UUID, "type").toBlocking().single();
    assertTrue(result.getDispatchPushFlag());
    assertFalse(result.getPushFlag());
    assertTrue(result.getEmailFlag());
    assertFalse(result.getVoiceFlag());
  }

  @Test
  public void setPaymentCenterAndEmailsNotificationPrefsOn() {
    final ImmutableMap<String, TypeValue> map = ImmutableMap.<String, TypeValue>builder()
        .put("pay.statement", TypeValue.newBuilder()
            .addTypeValue(TypeToValue.newBuilder()
                .setType(Type.BULLHORN)
                .setValue(true))
            .addTypeValue(TypeToValue.newBuilder()
                .setType(Type.EMAIL)
                .setValue(false))
            .build())
        .put("pay.statement.dispatcher", TypeValue.newBuilder()
            .addTypeValue(TypeToValue.newBuilder()
                .setType(Type.PUSH)
                .setValue(true))
            .build())
        .build();

    when(notificationsClient.getPreferences((GetPrefs) anyObject(), (RequestContext) anyObject())).
        thenReturn(Observable.just(GetResp.newBuilder()
            .setStatus(Status.newBuilder().setSuccess(true))
            .putAllPref(map)
            .build()));

    final ArgumentCaptor<SetPrefs> prefCaptor = ArgumentCaptor.forClass(SetPrefs.class);
    when(notificationsClient.setPreferences(prefCaptor.capture(), (RequestContext) anyObject()))
        .thenReturn(Observable.just(Status.newBuilder().setSuccess(true).build()));

    service.newSetPaymentCenterAndEmailsNotificationPrefs(USER_UUID, COMPANY_UUID, true).toBlocking().single();

    final SetPrefs value = prefCaptor.getValue();
    final List<ModPref> modPrefs = value.getModPrefList();
    assertEquals(1L, modPrefs.size());
    final ModPref modPref = modPrefs.get(0);
    // should turn on email
    assertEquals(Type.EMAIL, modPref.getType());
    assertTrue(modPref.getState());
  }

  @Test
  public void setPaymentCenterAndEmailsNotificationPrefsOff() {
    final ImmutableMap<String, TypeValue> map = ImmutableMap.<String, TypeValue>builder()
        .put("pay.statement", TypeValue.newBuilder()
            .addTypeValue(TypeToValue.newBuilder()
                .setType(Type.BULLHORN)
                .setValue(true))
            .addTypeValue(TypeToValue.newBuilder()
                .setType(Type.EMAIL)
                .setValue(true))
            .build())
        .put("pay.statement.dispatcher", TypeValue.newBuilder()
            .addTypeValue(TypeToValue.newBuilder()
                .setType(Type.PUSH)
                .setValue(true))
            .build())
        .build();

    when(notificationsClient.getPreferences((GetPrefs) anyObject(), (RequestContext) anyObject())).
        thenReturn(Observable.just(GetResp.newBuilder()
            .setStatus(Status.newBuilder().setSuccess(true))
            .putAllPref(map)
            .build()));

    final ArgumentCaptor<SetPrefs> prefCaptor = ArgumentCaptor.forClass(SetPrefs.class);
    when(notificationsClient.setPreferences(prefCaptor.capture(), (RequestContext) anyObject()))
        .thenReturn(Observable.just(Status.newBuilder().setSuccess(true).build()));

    service.newSetPaymentCenterAndEmailsNotificationPrefs(USER_UUID, COMPANY_UUID, false).toBlocking().single();
    final SetPrefs value = prefCaptor.getValue();
    final List<ModPref> modPrefs = value.getModPrefList();
    assertEquals(9L, modPrefs.size());

    // build a list of expected things
    final List<ModPref> expected = Lists.newArrayList();
    for (final String code : UserNotificationPrefsService.PAYMENT_ACCESS_NOTIFICATIONS) {
      final ModPref.Builder build = ModPref.newBuilder()
          .setCode(code)
          .setState(false)
          .setDimensionValuePair(USER_DVP);
      // should turn off bullhorn
      expected.add(build.setType(Type.BULLHORN).build());
      // should turn off push
      expected.add(build.setType(Type.PUSH).build());

      if ("pay.statement".equals(code)) {
        // should turn off email
        expected.add(build.setType(Type.EMAIL).build());
      }
    }
    // now check expectations
    assertThat(modPrefs, Matchers.hasItems(expected.toArray(new ModPref[0])));
  }

  @Test
  public void removeSMSNotifications() {
    final ImmutableMap<String, TypeValue> map = ImmutableMap.<String, TypeValue>builder()
        .put("type", TypeValue.newBuilder()
            .addTypeValue(TypeToValue.newBuilder()
                .setType(Type.PUSH)
                .setValue(false))
            .addTypeValue(TypeToValue.newBuilder()
                .setType(Type.SMS)
                .setValue(true))
            .build())
        .put("type.dispatcher", TypeValue.newBuilder()
            .addTypeValue(TypeToValue.newBuilder()
                .setType(Type.SMS)
                .setValue(false))
            .build())
        .put("otherType", TypeValue.newBuilder()
            .addTypeValue(TypeToValue.newBuilder()
                .setType(Type.SMS)
                .setValue(true))
            .build())
        .build();

    when(notificationsClient.getPreferences((GetPrefs) anyObject(), (RequestContext) anyObject())).
        thenReturn(Observable.just(GetResp.newBuilder()
            .setStatus(Status.newBuilder().setSuccess(true))
            .putAllPref(map)
            .build()));

    final ArgumentCaptor<SetPrefs> prefCaptor = ArgumentCaptor.forClass(SetPrefs.class);
    when(notificationsClient.setPreferences(prefCaptor.capture(), (RequestContext) anyObject()))
        .thenReturn(Observable.just(Status.newBuilder().setSuccess(true).build()));

    service.newRemoveSMSNotifications(USER_UUID, COMPANY_UUID).toBlocking().single();

    final SetPrefs captured = prefCaptor.getValue();
    final List<ModPref> modPrefs = captured.getModPrefList();
    final List<ModPref> expected = ImmutableList.of(
        ModPref.newBuilder()
            .setCode("type")
            .setState(false)
            .setDimensionValuePair(USER_DVP)
            .setType(Type.SMS)
            .build(),
        ModPref.newBuilder()
            .setCode("otherType")
            .setState(false)
            .setDimensionValuePair(USER_DVP)
            .setType(Type.SMS)
            .build());
    assertThat(modPrefs, Matchers.hasItems(expected.toArray(new ModPref[0])));
  }

  @Test
  public void setPrefs() {
    final NotificationPreferenceDTO p = new NotificationPreferenceDTO("foo",
    true, false, true, false, false, false); // email and bullhorn
    final NotificationPreferenceDTO[] prefs = new NotificationPreferenceDTO[]{p};

    final ModPref.Builder basePref = ModPref.newBuilder()
        .setCode("foo")
        .setDimensionValuePair(USER_DVP);

    final List<ModPref> expected = ImmutableList.of(
        basePref.setState(false).setType(Type.SMS).build(),
        basePref.setState(false).setType(Type.VOICE).build(),
        basePref.setState(false).setType(Type.PUSH).build(),
        basePref.setState(true).setType(Type.BULLHORN).build(),
        basePref.setState(true).setType(Type.EMAIL).build());

    final ArgumentCaptor<SetPrefs> prefCaptor = ArgumentCaptor.forClass(SetPrefs.class);
    when(notificationsClient.setPreferences(prefCaptor.capture(), (RequestContext) anyObject()))
        .thenReturn(Observable.just(Status.newBuilder().setSuccess(true).build()));

    service.newSetPrefs(USER_UUID, prefs, false);
    final List<ModPref> captured = prefCaptor.getValue().getModPrefList();
    assertThat(captured, Matchers.hasItems(expected.toArray(new ModPref[0])));
  }

  @Test
  public void setManageBankAndFundsNotificationPrefsOn() { //TODO
    final ImmutableMap<String, TypeValue> map = ImmutableMap.<String, TypeValue>builder()
        .put("money.withdrawn", TypeValue.newBuilder()
            .addTypeValue(TypeToValue.newBuilder()
                .setType(Type.BULLHORN)
                .setValue(true))
            .addTypeValue(TypeToValue.newBuilder()
                .setType(Type.EMAIL)
                .setValue(false))
            .build())
        .put("money.withdrawn.dispatcher", TypeValue.newBuilder()
            .addTypeValue(TypeToValue.newBuilder()
                .setType(Type.PUSH)
                .setValue(true))
            .build())
        .build();

    when(notificationsClient.getPreferences((GetPrefs) anyObject(), (RequestContext) anyObject())).
        thenReturn(Observable.just(GetResp.newBuilder()
            .setStatus(Status.newBuilder().setSuccess(true))
            .putAllPref(map)
            .build()));

    final ArgumentCaptor<SetPrefs> prefCaptor = ArgumentCaptor.forClass(SetPrefs.class);
    when(notificationsClient.setPreferences(prefCaptor.capture(), (RequestContext) anyObject()))
        .thenReturn(Observable.just(Status.newBuilder().setSuccess(true).build()));

    service.newSetManageBankAndFundsNotificationPrefs(USER_UUID, COMPANY_UUID, true).toBlocking().single();

    final SetPrefs value = prefCaptor.getValue();
    final List<ModPref> modPrefs = value.getModPrefList();
    assertEquals(1L, modPrefs.size());
    final ModPref modPref = modPrefs.get(0);
    // should turn on email
    assertEquals(Type.EMAIL, modPref.getType());
    assertTrue(modPref.getState());
  }


  @Test
  public void findUsersByCompanyAndNotificationType() {
    //FIXME
  }
}

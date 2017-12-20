package com.workmarket.service.business;


import com.codahale.metrics.Meter;
import com.workmarket.common.metric.WMMetricRegistryFacade;
import com.workmarket.common.template.push.PushTemplateParser;
import com.workmarket.domains.model.notification.DeviceType;
import com.workmarket.service.business.dto.PushDTO;
import com.workmarket.service.business.wrapper.PushResponse;
import com.workmarket.service.infra.PushServiceImpl;
import com.workmarket.service.infra.communication.PushAdapter;
import com.workmarket.utility.RandomUtilities;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * User: andrew
 * Date: 12/9/13
 */
@RunWith(MockitoJUnitRunner.class)
public class PushServiceUnitTest {

	@Mock PushAdapter pushAdapter;
	@Mock UserService userService;
	@Mock PushTemplateParser pushTemplateParser;
	@Mock WMMetricRegistryFacade wmMetricRegistryFacade;
	@Mock Meter meter;
	@Mock Meter iosSendSuccessMeter;
	@Mock Meter iosSendFailureMeter;
	@Mock Meter androidSendSuccessMeter;
	@Mock Meter androidSendFailureMeter;
	@InjectMocks PushServiceImpl pushService;

	private PushDTO pushDTO;

	@Before
	public void setUp() {
		pushDTO = mock(PushDTO.class);

		when(wmMetricRegistryFacade.meter(any(String.class))).thenReturn(meter);
		when(pushTemplateParser.parseAction(any(String.class))).thenReturn("http://workmarket.com/action");
		when(pushTemplateParser.parseMessage(any(String.class))).thenReturn("this is the notification");

		when(pushDTO.getMessage()).thenReturn("{}");
		when(pushDTO.getType()).thenReturn(DeviceType.ANDROID.getCode());
		when(pushDTO.getRegid()).thenReturn(RandomUtilities.generateAlphaString(10));
		when(pushDTO.getToUserId()).thenReturn(RandomUtilities.nextLong());

		when(pushAdapter.sendAndroidPush(anyString(), anyString(), anyString())).thenReturn(PushResponse.success());
		when(pushAdapter.sendIosPush(anyString(), anyString(), anyString())).thenReturn(PushResponse.success());
	}

	@Test
	public void sendPush_android_success() {
		pushService.sendPush(pushDTO);
		verify(pushAdapter, times(1)).sendAndroidPush(anyString(), anyString(), anyString());
		verify(userService, times(0)).removeDevice(anyLong(), anyString());
	}

	@Test
	public void sendPush_android_invalidDevice() {
		when(pushAdapter.sendAndroidPush(anyString(), anyString(), anyString())).thenReturn(PushResponse.invalidDevice());
		pushService.sendPush(pushDTO);
		verify(pushAdapter, times(1)).sendAndroidPush(anyString(), anyString(), anyString());
		verify(userService, times(1)).removeDevice(anyLong(), anyString());
	}

	@Test
	public void sendPush_ios_success() {
		when(pushDTO.getType()).thenReturn(DeviceType.IOS.getCode());
		pushService.sendPush(pushDTO);
		verify(pushAdapter, times(1)).sendIosPush(anyString(), anyString(), anyString());
		verify(userService, times(0)).removeDevice(anyLong(), anyString());
	}

	@Test
	public void sendPush_iosMessage_failure() {
		when(pushDTO.getType()).thenReturn(DeviceType.IOS.getCode());
		when(pushTemplateParser.parseMessage(any(String.class))).thenReturn("");
		when(pushAdapter.sendIosPush(anyString(), anyString(), anyString())).thenReturn(PushResponse.fail());
		pushService.sendPush(pushDTO);
		verify(pushAdapter, times(0)).sendIosPush(anyString(), anyString(), anyString());
	}

	@Test
	public void sendPush_parsesNewLines_success() throws Exception {
		when(pushDTO.getMessage()).thenReturn("{\n" +
				"\"message\": \"test\n\",\n" +
				"\"action\": \"test\"\n" +
				"}");
		PushResponse actualResult = pushService.sendPush(pushDTO);
		assertEquals(PushResponse.success().getStatus(), actualResult.getStatus());
	}
}

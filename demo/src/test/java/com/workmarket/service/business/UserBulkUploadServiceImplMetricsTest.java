package com.workmarket.service.business;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.UnmodifiableIterator;
import com.workmarket.common.template.NotificationTemplateFactory;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.asset.Upload;
import com.workmarket.redis.RedisAdapter;
import com.workmarket.service.business.dto.UserImportDTO;
import com.workmarket.service.business.event.BulkUserUploadDispatchEvent;
import com.workmarket.service.business.event.EventFactoryImpl;
import com.workmarket.service.business.upload.users.model.BulkUserUploadCompletionStatus;
import com.workmarket.service.business.upload.users.model.BulkUserUploadRequest;
import com.workmarket.service.business.upload.users.model.BulkUserUploadResponse;
import com.workmarket.service.infra.business.UploadServiceImpl;
import com.workmarket.service.infra.event.EventRouterImpl;
import com.workmarket.service.infra.notification.NotificationDispatcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserBulkUploadServiceImplMetricsTest {

	@Mock User user;
	@Mock UserServiceImpl userService;
	@Mock UploadServiceImpl uploadService;
	@Mock Upload uploadedFile;
	@Mock Meter meter;
	@Mock Histogram histogram;
	@Mock RedisAdapter redisAdapter;
	@Mock List<String> errors;
	@Mock ImmutableList<UserImportDTO> uploads;
	@Mock UserImportDTO dto;
	@Mock EventFactoryImpl eventFactory;
	@Mock EventRouterImpl eventRouter;
	@Mock private NotificationTemplateFactory notificationTemplateFactory;
	@Mock private NotificationDispatcher notificationDispatcher;
	@Mock BulkUserUploadDispatchEvent bulkUserUploadDispatchEvent;
	@Mock Optional<Object> result;
	@InjectMocks UserBulkUploadServiceImpl service = spy(new UserBulkUploadServiceImpl());

	private UnmodifiableIterator<UserImportDTO> uploadItr;
	private static String UUID = "07b577d4-c2be-41e6-ae28-cf9a2e52e90e";
	private static Long userId = 999999L;
	private final static Integer totalErrors = 2;
	private final static Integer totalUploadSize = 3;
	private final static Integer totalSuccess = 3;

	private BulkUserUploadRequest request;
	private BulkUserUploadResponse response;

	@Before
	public void setup() {

		request = spy(new BulkUserUploadRequest(UUID, userId));
		response = spy(new BulkUserUploadResponse());

		when(request.getUUID()).thenReturn(UUID);
		when(request.getUserId()).thenReturn(userId);
		when(userService.findUserById(userId)).thenReturn(user);

		uploadItr = mock(UnmodifiableIterator.class);
		when(uploads.iterator()).thenReturn(uploadItr);
		when(uploadItr.hasNext()).thenReturn(true, true, true, false);
		when(uploadItr.next()).thenReturn(dto)
			.thenReturn(dto).thenReturn(dto);

	}

	@Test
	public void testUploadMeter() throws Exception {
		service.start(request, response, false);
		ArgumentCaptor<Meter> meterMetricCaptor = ArgumentCaptor.forClass(Meter.class);
		ArgumentCaptor<Long> valueCaptor = ArgumentCaptor.forClass(Long.class);
		verify(service).sendMeterMetric(meterMetricCaptor.capture(), valueCaptor.capture());
		assertTrue(valueCaptor.getValue() == 1);
	}

	@Test
	public void testUploadSizeMetric() throws Exception {
		when(response.getErrors()).thenReturn(errors);
		when(errors.isEmpty()).thenReturn(true);
		when(response.getUserUploads()).thenReturn(uploads);
		when(uploads.size()).thenReturn(totalUploadSize);

		service.start(request, response, false);
		verify(service).bulkUploadFileReadAndValidate(request, response, false);
		service.dispatchUpload(response, false, Optional.<Map<UserImportDTO,List<String>>>absent());
		verify(service).dispatchUpload(response, false, Optional.<Map<UserImportDTO,List<String>>>absent());
		ArgumentCaptor<Histogram> histogramMetricCaptor = ArgumentCaptor.forClass(Histogram.class);
		ArgumentCaptor<Integer> valueCaptor = ArgumentCaptor.forClass(Integer.class);
		verify(service).sendHistogramMetric(histogramMetricCaptor.capture(), valueCaptor.capture());
		assertTrue(valueCaptor.getValue() == totalUploadSize);
	}

	@Test
	public void testValidationErrorMetric() throws Exception {
		when(response.getErrors()).thenReturn(errors);
		when(errors.isEmpty()).thenReturn(false);
		when(response.getNumOfRowsWithValidationError()).thenReturn(totalErrors);
		when(response.getStatus()).thenReturn(BulkUserUploadCompletionStatus.COMPLETED_WITH_VALIDATION_ERROR);
		when(response.getUserUploads()).thenReturn(null);

		service.start(request, response, false);
		verify(service).bulkUploadFileReadAndValidate(request, response, false);
		service.finish(response);
		ArgumentCaptor<Meter> meterMetricCaptor = ArgumentCaptor.forClass(Meter.class);
		ArgumentCaptor<Long> valueCaptor = ArgumentCaptor.forClass(Long.class);
		verify(service, atLeast(2)).sendMeterMetric(meterMetricCaptor.capture(), valueCaptor.capture());
		assertTrue(valueCaptor.getValue() == totalErrors.longValue());
	}

	@Test
	public void testSystemErrorMetric() throws Exception {
		when(redisAdapter.getSet(anyString())).thenReturn(ImmutableSet.of("user1", "user2"));
		when(response.getFileUUID()).thenReturn(UUID);
		when(response.getStatus()).thenReturn(BulkUserUploadCompletionStatus.COMPLETED_WITH_SYSTEM_ERROR);
		when(uploadService.findUploadByUUID(UUID)).thenReturn(uploadedFile);
		when(uploadService.storeUpload(any(InputStream.class), anyString(), anyString(), anyLong())).thenReturn(uploadedFile);

		service.start(request, response, false);
		verify(service).bulkUploadFileReadAndValidate(request, response, false);
		service.finish(response);
		ArgumentCaptor<Meter> meterMetricCaptor = ArgumentCaptor.forClass(Meter.class);
		ArgumentCaptor<Long> valueCaptor = ArgumentCaptor.forClass(Long.class);
		verify(service, atLeast(2)).sendMeterMetric(meterMetricCaptor.capture(), valueCaptor.capture());
		assertTrue(valueCaptor.getValue() == totalErrors.longValue());
	}

	@Test
	public void testSuccessMetric() throws Exception {
		when(response.getStatus()).thenReturn(BulkUserUploadCompletionStatus.COMPLETED_WITH_NO_ERROR);
		when(redisAdapter.get(anyString())).thenReturn(result);
		when(result.get()).thenReturn(String.valueOf(totalSuccess));

		service.start(request, response, false);
		verify(service).bulkUploadFileReadAndValidate(request, response, false);
		service.finish(response);
		ArgumentCaptor<Meter> meterMetricCaptor = ArgumentCaptor.forClass(Meter.class);
		ArgumentCaptor<Long> valueCaptor = ArgumentCaptor.forClass(Long.class);
		verify(service, atLeast(2)).sendMeterMetric(meterMetricCaptor.capture(), valueCaptor.capture());
		assertTrue(valueCaptor.getValue() == totalSuccess.longValue());
	}
}

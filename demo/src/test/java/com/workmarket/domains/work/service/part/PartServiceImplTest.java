package com.workmarket.domains.work.service.part;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.workmarket.BaseUnitTest;
import com.workmarket.common.core.RequestContext;
import com.workmarket.common.kafka.KafkaClient;
import com.workmarket.domains.authentication.features.FeatureEvaluator;
import com.workmarket.domains.model.Location;
import com.workmarket.domains.work.cache.PartCache;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.part.ShippingDestinationType;
import com.workmarket.domains.work.model.part.ShippingProvider;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.id.IdGenerator;
import com.workmarket.service.business.DirectoryService;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.dto.LocationDTO;
import com.workmarket.service.business.dto.PartDTO;
import com.workmarket.service.business.dto.PartGroupDTO;
import com.workmarket.service.external.AfterShipError;
import com.workmarket.service.external.TrackingNumberAdapter;
import com.workmarket.service.external.TrackingNumberResponse;
import com.workmarket.service.external.TrackingStatus;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.web.WebRequestContextProvider;
import com.workmarket.shipment.client.ShipmentClient;
import com.workmarket.shipment.vo.DestinationType;
import com.workmarket.shipment.vo.Shipment;
import com.workmarket.shipment.vo.ShipmentGroup;
import com.workmarket.web.converters.LocationToLocationDTOConverter;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import rx.Observable;
import rx.functions.Func1;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.Callable;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PartServiceImplTest extends BaseUnitTest {
	private static final String TRACKING_NUMBER = "TRACKING_NUMBER";
	private static final String PART_UUID = "PART_UUID";
	private static final String GROUP_UUID = "GROUP_UUID";
	private static final String CREATOR = "CREATOR";
	private static final String MODIFIER = "MODIFIER";
	private static final DateTime CREATED_ON = new DateTime();
	private static final DateTime MODIFIED_ON = new DateTime();
	private static final Shipment SHIPMENT = new Shipment(PART_UUID, GROUP_UUID, "name", TRACKING_NUMBER,
			com.workmarket.shipment.vo.ShippingProvider.ups,
			new BigDecimal(100.0), false, CREATOR, MODIFIER, CREATED_ON, MODIFIED_ON, false);
	private static final String WORK_ID = "8675309";
	private static final String LOCATION_ID = "5551212";
	private static final Long WORK_ID_LONG = 8675309L;
	private static final Long LOCATION_ID_LONG = 5551212L;
	private static final ShipmentGroup SHIPMENT_GROUP = new ShipmentGroup(GROUP_UUID, LOCATION_ID, null,
			ImmutableList.of(SHIPMENT), WORK_ID, DestinationType.ONSITE, CREATOR,
			MODIFIER, CREATED_ON, MODIFIED_ON, false);

	@Mock private PartCache partCache;
	@Mock private TrackingNumberAdapter trackingNumberAdapter;
	@Mock private AuthenticationService authenticationService;
	@Mock private LocationToLocationDTOConverter locationToLocationDTOConverter;
	@Mock private WorkService workService;
	@Mock private ProfileService profileService;
	@Mock private DirectoryService directoryService;
	@Mock private Location location;
	@Mock private ShipmentClient uServiceClient;
	@Mock private WebRequestContextProvider webRequestContextProvider;
	@Mock private KafkaClient kafkaClient;
	@Mock private FeatureEvaluator featureEvaluator;
	@Mock private IdGenerator idGenerator;
	@Mock private ShipmentConverter shipmentConverter;
	@Mock private Work work;
	private HibernateTrialWrapper hibernateTrialWrapper;

	private PartServiceImpl partService;
	private RequestContext requestContext;
	private PartDTO partDTO;
	private PartGroupDTO partGroupDTO;

	@Before
	public void setUp() {

		final MetricRegistry metricRegistry = new MetricRegistry();
		requestContext = new RequestContext("DUMMY_REQUEST_ID", "DUMMY_TENANT_ID");
		requestContext.setUserId("USER_ID");

		when(webRequestContextProvider.getRequestContext()).thenReturn(requestContext);
		partService = new PartServiceImpl(partCache, trackingNumberAdapter, authenticationService, directoryService,
				workService, profileService, locationToLocationDTOConverter, webRequestContextProvider, metricRegistry,
				hibernateTrialWrapper, uServiceClient, idGenerator, shipmentConverter, featureEvaluator);

		hibernateTrialWrapper = new HibernateTrialWrapper(null, null, null, null) {
			@Override
			public <T> Callable<Observable<T>> wrap(final Callable<Observable<T>> callableObservable) {
				return callableObservable;
			}
		};

		this.partDTO = new PartDTO();
		partDTO.setUuid(PART_UUID);
		partDTO.setTrackingNumber(TRACKING_NUMBER);
		partDTO.setShippingProvider(ShippingProvider.UPS);
		partDTO.setTrackingStatus(TrackingStatus.IN_TRANSIT);

		this.partGroupDTO = new PartGroupDTO();
		partGroupDTO.setUuid(GROUP_UUID);
		partGroupDTO.setParts(ImmutableList.of(partDTO));
		partGroupDTO.setShippingDestinationType(ShippingDestinationType.ONSITE);
		partGroupDTO.setWorkId(WORK_ID_LONG);
	}


	@Test
	public void updateTrackingStatus() {
		when(uServiceClient.getShipmentsByShippingDetails(com.workmarket.shipment.vo.ShippingProvider.ups,
				TRACKING_NUMBER, requestContext)).thenReturn(
				Observable.just(SHIPMENT)
		);

		partService.updateTrackingStatus(partDTO);
		verify(partCache).updateTrackingStatus(PART_UUID, "IN_TRANSIT");
	}

	@Test
	public void getPartsByGroupUuidCached() {
		when(uServiceClient.getShipments(GROUP_UUID, requestContext))
				.thenReturn(Observable.just(SHIPMENT));
		when(partCache.getPart(PART_UUID))
				.thenReturn(Optional.of(partDTO));

		final List<PartDTO> result = partService.getPartsByGroupUuid(GROUP_UUID);
		final PartDTO single = result.get(0);
		assertEquals(ShippingProvider.UPS, single.getShippingProvider());
		assertEquals(TrackingStatus.IN_TRANSIT, single.getTrackingStatus());
	}

	@Test
	public void getPartsByGroupUuidNotCachedTrackingCreated() {
		when(uServiceClient.getShipments(GROUP_UUID, requestContext))
				.thenReturn(Observable.just(SHIPMENT));
		when(partCache.getPart(PART_UUID))
				.thenReturn(Optional.<PartDTO>absent());

		mockBasicTracking();

		final List<PartDTO> result = partService.getPartsByGroupUuid(GROUP_UUID);
		final PartDTO single = result.get(0);
		assertEquals(ShippingProvider.UPS, single.getShippingProvider());
		assertEquals(TrackingStatus.IN_TRANSIT, single.getTrackingStatus());
	}

	private void mockBasicTracking() {
		final TrackingNumberResponse t = new TrackingNumberResponse();
		t.setTrackingStatus(TrackingStatus.IN_TRANSIT);
		t.setShippingProvider(ShippingProvider.UPS);
		t.setSuccessful(true);
		when(trackingNumberAdapter.track(TRACKING_NUMBER))
				.thenReturn(t);
	}

	@Test
	public void getPartsByGroupUuidNotCachedTrackingAlreadyExists() {
		when(uServiceClient.getShipments(GROUP_UUID, requestContext))
				.thenReturn(Observable.just(SHIPMENT));
		when(partCache.getPart(PART_UUID))
				.thenReturn(Optional.<PartDTO>absent());

		final TrackingNumberResponse createTrackingResponse = new TrackingNumberResponse();
		createTrackingResponse.setSuccessful(false);
		createTrackingResponse.setMetaCode(AfterShipError.TRACKING_EXISTS);
		when(trackingNumberAdapter.track(TRACKING_NUMBER))
				.thenReturn(createTrackingResponse);

		final TrackingNumberResponse getTrackingResponse = new TrackingNumberResponse();
		getTrackingResponse.setTrackingStatus(TrackingStatus.IN_TRANSIT);
		getTrackingResponse.setShippingProvider(ShippingProvider.UPS);
		getTrackingResponse.setSuccessful(true);
		when(trackingNumberAdapter.get(TRACKING_NUMBER, "ups"))
				.thenReturn(getTrackingResponse);

		final List<PartDTO> result = partService.getPartsByGroupUuid(GROUP_UUID);
		final PartDTO single = result.get(0);
		assertEquals(ShippingProvider.UPS, single.getShippingProvider());
		assertEquals(TrackingStatus.IN_TRANSIT, single.getTrackingStatus());
	}

	@Test
	public void deletePart() {
		when(uServiceClient.deleteShipment(PART_UUID, requestContext))
				.thenReturn(Observable.just(SHIPMENT));

		partService.deletePart(PART_UUID);
		verify(uServiceClient).deleteShipment(PART_UUID, requestContext);
	}

	@Test
	public void deletePartGroup() {
		final Observable<ShipmentGroup> shipmentGroupObservable = Observable.just(SHIPMENT_GROUP);
		when(uServiceClient.getShipmentGroupByWorkId(WORK_ID, requestContext))
				.thenReturn(shipmentGroupObservable);

		when(uServiceClient.deleteShipmentGroup(GROUP_UUID, requestContext))
				.thenReturn(shipmentGroupObservable);

		partService.deletePartGroup(WORK_ID_LONG);
	}

	@Test
	public void getPartGroupByWorkId() {
		final LocationDTO locationDTO = new LocationDTO();
		locationDTO.setId(LOCATION_ID_LONG);


		when(uServiceClient.getShipmentGroupByWorkId(WORK_ID, requestContext))
				.thenReturn(Observable.just(SHIPMENT_GROUP));
		when(shipmentConverter.convertShipmentGroupPartGroupDTO())
				.thenReturn(new Func1<ShipmentGroup, PartGroupDTO>() {
					@Override
					public PartGroupDTO call(ShipmentGroup shipmentGroup) {
						return partGroupDTO;
					}
				});
		// because the destination type on the group is ONSITE
		when(workService.findWork(WORK_ID_LONG))
				.thenReturn(work);
		when(work.getLocation())
				.thenReturn(location);
		when(locationToLocationDTOConverter.convert(location))
				.thenReturn(locationDTO);

		final PartGroupDTO resultGroup = partService.getPartGroupByWorkId(WORK_ID_LONG);
		assertEquals(GROUP_UUID, resultGroup.getUuid());
		assertEquals(LOCATION_ID_LONG, resultGroup.getShipToLocation().getId());
		final List<PartDTO> parts = resultGroup.getParts();
		assertEquals(1, parts.size());
		final PartDTO part = parts.get(0);
		assertEquals(PART_UUID, part.getUuid());
		verify(workService).findWork(WORK_ID_LONG);
	}

	@Test
	public void saveOrUpdatePartDoUpdate() {
		when(uServiceClient.updateShipment((Shipment) anyObject(), eq(requestContext)))
				.thenReturn(Observable.just(SHIPMENT));
		mockBasicTracking();
		when(partCache.getPart(PART_UUID))
				.thenReturn(Optional.of(partDTO));

		final PartDTO dto = partService.saveOrUpdatePart(partDTO, GROUP_UUID);
		assertEquals(PART_UUID, dto.getUuid());
	}

	@Test
	public void saveOrUpdatePartDoCreate() {
		partDTO.setUuid(null);
		when(uServiceClient.createShipment((Shipment) anyObject(), eq(requestContext)))
				.thenReturn(Observable.just(SHIPMENT));
		mockBasicTracking();
		when(idGenerator.next())
				.thenReturn(Observable.just(PART_UUID));
		when(partCache.getPart(PART_UUID))
				.thenReturn(Optional.of(partDTO));

		final PartDTO dto = partService.saveOrUpdatePart(partDTO, GROUP_UUID);
		assertEquals(PART_UUID, dto.getUuid());
	}

	@Test
	public void saveOrUpdatePartGroupDoCreate() {
		partGroupDTO.setUuid(null); // so it's a create
		final LocationDTO locationDTO = new LocationDTO();
		when(directoryService.saveOrUpdateLocation((LocationDTO) anyObject()))
				.thenReturn(location);
		when(locationToLocationDTOConverter.convert(location))
				.thenReturn(locationDTO);
		when(idGenerator.next())
				.thenReturn(Observable.just(GROUP_UUID));
		when(uServiceClient.createShipmentGroup((ShipmentGroup) anyObject(), eq(requestContext)))
				.thenReturn(Observable.just(SHIPMENT_GROUP));
		when(shipmentConverter.convertShipmentGroupPartGroupDTO())
				.thenReturn(new Func1<ShipmentGroup, PartGroupDTO>() {
					@Override
					public PartGroupDTO call(final ShipmentGroup shipmentGroup) {
						partGroupDTO.setUuid(GROUP_UUID);
						return partGroupDTO;
					}
				});
		when(uServiceClient.getShipmentGroup(GROUP_UUID, requestContext))
				.thenReturn(Observable.just(SHIPMENT_GROUP));
		mockBasicTracking();
		when(uServiceClient.createShipment(SHIPMENT, requestContext))
				.thenReturn(Observable.just(SHIPMENT));

		partService.saveOrUpdatePartGroup(partGroupDTO);
	}

	@Test
	public void saveOrUpdatePartGroupDoUpdateAndDeletePart() {
		partGroupDTO.setParts(ImmutableList.<PartDTO>of()); // delete it's parts
		final LocationDTO locationDTO = new LocationDTO();
		when(directoryService.saveOrUpdateLocation((LocationDTO) anyObject()))
				.thenReturn(location);
		when(locationToLocationDTOConverter.convert(location))
				.thenReturn(locationDTO);
		when(idGenerator.next())
				.thenReturn(Observable.just(GROUP_UUID));
		when(uServiceClient.updateShipmentGroup((ShipmentGroup) anyObject(), eq(requestContext)))
				.thenReturn(Observable.just(SHIPMENT_GROUP));
		when(shipmentConverter.convertShipmentGroupPartGroupDTO())
				.thenReturn(new Func1<ShipmentGroup, PartGroupDTO>() {
					@Override
					public PartGroupDTO call(final ShipmentGroup shipmentGroup) {
						partGroupDTO.setUuid(GROUP_UUID);
						return partGroupDTO;
					}
				});
		when(uServiceClient.getShipmentGroup(GROUP_UUID, requestContext))
				.thenReturn(Observable.just(SHIPMENT_GROUP));
		mockBasicTracking();
		when(uServiceClient.deleteShipment(PART_UUID, requestContext))
				.thenReturn(Observable.just(SHIPMENT));

		partService.saveOrUpdatePartGroup(partGroupDTO);
		verify(uServiceClient).deleteShipment(PART_UUID, requestContext);
		verify(uServiceClient).updateShipmentGroup((ShipmentGroup) anyObject(), eq(requestContext));
	}
}


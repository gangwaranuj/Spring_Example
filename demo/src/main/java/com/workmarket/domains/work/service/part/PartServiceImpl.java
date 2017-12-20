package com.workmarket.domains.work.service.part;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.workmarket.common.core.RequestContext;
import com.workmarket.common.metric.WMMetricRegistryFacade;
import com.workmarket.domains.authentication.features.FeatureEvaluator;
import com.workmarket.domains.model.Address;
import com.workmarket.domains.model.AddressType;
import com.workmarket.domains.model.Location;
import com.workmarket.domains.work.cache.PartCache;
import com.workmarket.domains.work.model.part.ShippingDestinationType;
import com.workmarket.domains.work.model.part.ShippingProvider;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.id.IdGenerator;
import com.workmarket.integration.autotask.util.StringUtil;
import com.workmarket.service.business.DirectoryService;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.dto.LocationDTO;
import com.workmarket.service.business.dto.PartDTO;
import com.workmarket.service.business.dto.PartGroupDTO;
import com.workmarket.service.external.AfterShipError;
import com.workmarket.service.external.ShippingProviderDetectResponse;
import com.workmarket.service.external.TrackingNumberAdapter;
import com.workmarket.service.external.TrackingNumberResponse;
import com.workmarket.service.external.TrackingStatus;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.web.WebRequestContextProvider;
import com.workmarket.shipment.client.ShipmentClient;
import com.workmarket.shipment.vo.DestinationType;
import com.workmarket.shipment.vo.Shipment;
import com.workmarket.shipment.vo.ShipmentGroup;
import com.workmarket.shipment.vo.ShipmentGroupBuilder;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.web.converters.LocationToLocationDTOConverter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;

import static com.google.common.collect.Iterables.getOnlyElement;
import static com.workmarket.domains.work.service.part.ShipmentConverter.CONVERT_PART_DTO_TO_SHIPMENT;
import static com.workmarket.domains.work.service.part.ShipmentConverter.CONVERT_SHIPMENT_TO_PART_DTO;

@Service
public class PartServiceImpl implements PartService {
	private static final Logger logger = LoggerFactory.getLogger(PartServiceImpl.class);

	private final PartCache partCache;
	private final TrackingNumberAdapter trackingNumberAdapter;
	private final AuthenticationService authenticationService;
	private final DirectoryService directoryService;
	private final WorkService workService;
	private final ProfileService profileService;
	private final LocationToLocationDTOConverter locationToLocationDTOConverter;
	private final ShipmentClient uServiceClient;
	private final WebRequestContextProvider webRequestContextProvider;
	private final IdGenerator idGenerator;
	private final ShipmentConverter shipmentConverter;

	private final Histogram groupSizeMetric;

	@Autowired
	public PartServiceImpl(final PartCache partCache,
	                       final TrackingNumberAdapter trackingNumberAdapter,
	                       final AuthenticationService authenticationService,
	                       final DirectoryService directoryService,
	                       final WorkService workService,
	                       final ProfileService profileService,
	                       final LocationToLocationDTOConverter locationToLocationDTOConverter,
	                       final WebRequestContextProvider webRequestContextProvider,
	                       final MetricRegistry metricRegistry,
	                       final HibernateTrialWrapper hibernateTrialWrapper,
	                       final ShipmentClient uServiceClient,
	                       final IdGenerator idGenerator,
	                       final ShipmentConverter shipmentConverter,
	                       final FeatureEvaluator featureEvaluator) {
		this.partCache = partCache;
		this.trackingNumberAdapter = trackingNumberAdapter;
		this.authenticationService = authenticationService;
		this.directoryService = directoryService;
		this.workService = workService;
		this.profileService = profileService;
		this.locationToLocationDTOConverter = locationToLocationDTOConverter;
		this.webRequestContextProvider = webRequestContextProvider;
		this.uServiceClient = uServiceClient;
		this.idGenerator = idGenerator;
		this.shipmentConverter = shipmentConverter;
		this.groupSizeMetric = new WMMetricRegistryFacade(metricRegistry, "shipment").histogram("groupsize");
	}

	@Override
	public void updateTrackingStatus(final PartDTO partDTO) {
	    logger.debug("UPDATE TRACKING STATUS");
		Assert.notNull(partDTO);
		Assert.notNull(partDTO.getShippingProvider());
		Assert.notNull(partDTO.getTrackingStatus());

		final String trackingNumber = partDTO.getTrackingNumber();
		final String shippingProvider = partDTO.getShippingProvider().getCode();
		final String deliveryStatus = partDTO.getTrackingStatus().getCode().toUpperCase();

		Assert.hasText(trackingNumber);
		final RequestContext requestContext = webRequestContextProvider.getRequestContext();

		final List<String> partIds = uServiceClient.getShipmentsByShippingDetails(
				ShipmentConverter.convertShippingProvider(partDTO), trackingNumber, requestContext)
				.map(extractShipmentUuid())
				.toList().toBlocking().single();


		if (CollectionUtils.isEmpty(partIds)) {
			logger.error(String.format("[partService] No parts with tracking number: %s and provider: %s",
				trackingNumber, shippingProvider));
			return;
		}

		for (final String partId : partIds) {
			partCache.updateTrackingStatus(partId, deliveryStatus);
		}
	}

	private Func1<PartDTO, PartDTO> addInTrackingInfo() {
		return new Func1<PartDTO, PartDTO>() {
			@Override
			public PartDTO call(final PartDTO part) {
				final Optional<PartDTO> partDTOOptional = partCache.getPart(part.getUuid());
				if (partDTOOptional.isPresent()) {
					part.setTrackingStatus(partDTOOptional.get().getTrackingStatus());
					return part;
				}
				return trackPart(part);
			}
		};
	}

	private PartDTO setTrackingStatus(final PartDTO partDTO) {
		Preconditions.checkNotNull(partDTO, "partDTO");
		Preconditions.checkNotNull(partDTO.getTrackingNumber(), "partDTO tracking number");

		if (ShippingProvider.OTHER.equals(partDTO.getShippingProvider())) {
			final ShippingProviderDetectResponse response = trackingNumberAdapter
				.detectShippingProvider(partDTO.getTrackingNumber());
			if (!response.isSuccessful()) {
				partDTO.setShippingProvider(ShippingProvider.OTHER);
				partDTO.setTrackingStatus(TrackingStatus.NOT_AVAILABLE);
				return partDTO;
			}
			partDTO.setShippingProvider(response.getShippingProvider());
		}

		final TrackingNumberResponse trackingNumberResponse = trackingNumberAdapter.get(partDTO.getTrackingNumber(),
			partDTO.getShippingProvider().getCode());

		final TrackingStatus trackingStatus;

		if (trackingNumberResponse.isSuccessful()) {
			trackingStatus = trackingNumberResponse.getTrackingStatus();
		} else {
			trackingStatus = TrackingStatus.NOT_AVAILABLE;
		}

		partDTO.setTrackingStatus(trackingStatus);
		return partDTO;
	}

	@Override
	public PartGroupDTO getPartGroupByWorkId(final Long workId) {
	    logger.debug("GPGBWorkID");
		Assert.notNull(workId);
		final RequestContext requestContext = webRequestContextProvider.getRequestContext();

		final List<PartGroupDTO> partGroupDTOList = uServiceClient.getShipmentGroupByWorkId(
				String.valueOf(workId), requestContext)
			.map(shipmentConverter.convertShipmentGroupPartGroupDTO())
			.toList().toBlocking().single();

		if (partGroupDTOList.isEmpty()) {
			logger.debug("Returning null group by work id");
			return null;
		}
		final PartGroupDTO partGroupDTO = partGroupDTOList.get(0);

		final Location shipToLocation = computeShipToLocation(partGroupDTO);

		if (shipToLocation != null) {
			partGroupDTO.setShipToLocation(locationToLocationDTOConverter.convert(shipToLocation));
		}

		logger.debug("Returning non-null group by work id");
		return partGroupDTO;
	}

	private Location computeShipToLocation(final PartGroupDTO partGroupDTO) {
		if (ShippingDestinationType.ONSITE.equals(partGroupDTO.getShippingDestinationType())) {
			return workService.findWork(partGroupDTO.getWorkId()).getLocation();
		}

		if (!ShippingDestinationType.WORKER.equals(partGroupDTO.getShippingDestinationType())) {
			return null;
		}

		final Long activeWorkerId = workService.findActiveWorkerId(partGroupDTO.getWorkId());
		if (activeWorkerId == null) {
			return null;
		}

		final Address workerAddress = profileService.findAddress(activeWorkerId);
		if (workerAddress == null) {
			return null;
		}

		final Location shipToLocation = new Location();
		shipToLocation.setAddress(workerAddress);
		return shipToLocation;
	}

	@Override
	public void saveOrUpdatePartGroup(final PartGroupDTO partGroupDTOIn) {
	    Assert.notNull(partGroupDTOIn);
		Assert.notNull(partGroupDTOIn.getWorkId());
		final PartGroupDTO partGroupDTO = partGroupDTOIn.copy();
		groupSizeMetric.update(partGroupDTO.getParts().size());
		logger.debug("SOUPG part group has {} parts", partGroupDTO.getParts().size());

		final Long currentUserCompanyId = authenticationService.getCurrentUserCompanyId();
		final RequestContext requestContext = webRequestContextProvider.getRequestContext();
		final String partGroupUuid = partGroupDTO.getUuid();

		final Observable<String> newGroupUuid = idGenerator.next().cache();

		final Location shipToLocation;
		final LocationDTO shipToLocationDTO;
		if (!partGroupDTO.hasShipToLocation()) {
			shipToLocation = null;
			shipToLocationDTO = null;
		} else {
			final LocationDTO newShipToLocationDTO = partGroupDTO.getShipToLocation();
			newShipToLocationDTO.setAddressTypeCode(AddressType.PARTS_LOGISTICS);
			newShipToLocationDTO.setCompanyId(currentUserCompanyId);
			shipToLocation = directoryService.saveOrUpdateLocation(newShipToLocationDTO);
			shipToLocationDTO = locationToLocationDTOConverter.convert(shipToLocation);
		}

		final Location returnToLocation;
		final LocationDTO returnToLocationDTO;
		if (!partGroupDTO.hasReturnToLocation()) {
			returnToLocation = null;
			returnToLocationDTO = null;
		} else {
			final LocationDTO newReturnToLocationDTO = partGroupDTO.getReturnToLocation();
			newReturnToLocationDTO.setAddressTypeCode(AddressType.PARTS_LOGISTICS);
			newReturnToLocationDTO.setCompanyId(currentUserCompanyId);
			returnToLocation = directoryService.saveOrUpdateLocation(newReturnToLocationDTO);
			returnToLocationDTO = locationToLocationDTOConverter.convert(returnToLocation);
		}

		final Observable<ShipmentGroup> initial;
		if (StringUtil.isNullOrEmpty(partGroupUuid)) {
			logger.debug("SOUPG Just null");
			initial = Observable.empty();
		} else {
			logger.debug("SOUPG getting shipment group by partGroupUuid {}", partGroupUuid);
			initial = uServiceClient.getShipmentGroup(partGroupUuid, requestContext);
		}

		try {
			initial
				.toList()
				.flatMap(returnFoundGroupOrQueryByWorkId(partGroupDTO, requestContext))
				.toList()
				.map(groupsBuilderIfFoundOrNewBuilder())
				// joining with newUuid if we had to create something to get a uuid common to both
				// control and experiment
				.zipWith(newGroupUuid,
				    newSavePartGroup(partGroupDTO, requestContext, shipToLocation, returnToLocation))
				.flatMap(PartServiceImpl.<ShipmentGroup>identity())
				.map(new Func1<ShipmentGroup, PartGroupDTO>() {
					@Override
					public PartGroupDTO call(final ShipmentGroup saved) {
						logger.debug("Save of shipment group returned");

						final PartGroupDTO converted;
						try {
							converted = shipmentConverter.convertShipmentGroupPartGroupDTO().call(saved);
						} catch (final RuntimeException e) {
							logger.error("Failed converting", e);
							throw e;
						}

						logger.debug("Got past conversion, now off to save or update parts");
						final List<PartDTO> parts;
						try {
							logger.debug("NSOUPG");
							final ImmutablePair<Iterable<PartDTO>, Iterable<PartDTO>> splitParts = splitCreateFromUpdate(
									partGroupDTO.getParts());
							parts = newSaveOrUpdateParts(splitParts.getLeft(), splitParts.getRight(), saved.getUuid())
								.toList().toBlocking().single();
						} catch (final Exception e) {
							logger.error("exception kaboom", e);
							throw Throwables.propagate(e);
						}

						converted.setParts(parts);
						converted.setShipToLocation(shipToLocationDTO);
						converted.setReturnToLocation(returnToLocationDTO);
						return converted;
					}
				})
				.map(newDeleteDeletedGroupParts(requestContext))
				.map(new Func1<PartGroupDTO, PartGroupDTO>() {
					@Override
					public PartGroupDTO call(final PartGroupDTO partGroupDTO) {
						final PartGroupDTO partGroupCopy = partGroupDTO.copy();
						final List<PartDTO> newParts = Lists.newArrayList();
						for (final PartDTO part : partGroupDTO.getParts()) {
						    final PartDTO copy = part.asForm().asDTO();
						    newParts.add(copy);
						    updatePartCache().call(copy);
						}
						partGroupCopy.setParts(newParts);
						return partGroupCopy;
					}
				})
				.toBlocking().single();
		} catch (final Exception e) {
			throw Throwables.propagate(e);
		}
	}

	private Func1<PartGroupDTO, PartGroupDTO> newDeleteDeletedGroupParts(final RequestContext requestContext) {
		return new Func1<PartGroupDTO, PartGroupDTO>() {
			@Override
			public PartGroupDTO call(final PartGroupDTO saved) {
				final HashSet<String> savedPartUuids = new HashSet<>();
				for (final PartDTO partDTO : saved.getParts()) {
					savedPartUuids.add(partDTO.getUuid());
				}

				uServiceClient.getShipmentGroup(saved.getUuid(), requestContext)
					.map(extractShipmentsFromGroup())
					.flatMap(PartServiceImpl.<Shipment>fromList())
					.map(extractShipmentUuid())
					.filter(new Func1<String, Boolean>() {
						@Override
						public Boolean call(final String uuid) {
							return !savedPartUuids.contains(uuid);
						}
					})
					.flatMap(new Func1<String, Observable<Shipment>>() {
						@Override
						public Observable<Shipment> call(final String uuid) {
							return uServiceClient.deleteShipment(uuid, requestContext);
						}
					})
					.toList().toBlocking().single(); // force execution
				return saved;
			}
		};
	}

	private Func2<ShipmentGroupBuilder, String, Observable<ShipmentGroup>> newSavePartGroup(
			final PartGroupDTO partGroupDTO,
			final RequestContext requestContext,
			final Location shipToLocation,
			final Location returnToLocation) {
		return new Func2<ShipmentGroupBuilder, String, Observable<ShipmentGroup>>() {
			@Override public Observable<ShipmentGroup> call(final ShipmentGroupBuilder builder, final String uuid) {
				logger.debug("filling in builder");

				final String shipToLocationId = shipToLocation == null ? null : shipToLocation.getId().toString();
				if (builder.getShipToLocationId() != null && shipToLocationId == null) {
					directoryService.deleteLocation(
						directoryService.findLocationById(Long.valueOf(builder.getShipToLocationId())));
				}
				builder.setShipToLocationId(shipToLocationId);

				final String returnToLocationId = returnToLocation == null ? null : returnToLocation.getId().toString();
				// if it had one before, and doesn't now, nuke it.
				if (builder.getReturnToLocationId() != null && returnToLocation == null) {
					directoryService.deleteLocation(
						directoryService.findLocationById(Long.valueOf(builder.getReturnToLocationId())));
				}
				builder.setReturnToLocationId(returnToLocationId);

				builder.setWorkId(partGroupDTO.getWorkId().toString());
				if (partGroupDTO.isSuppliedByWorker()) {
					builder.setDestinationType(DestinationType.NONE);
				} else {
					Assert.notNull(partGroupDTO.getShippingDestinationType());
					builder.setDestinationType(
						ShipmentConverter.convertDestination(partGroupDTO.getShippingDestinationType()));
				}
				if (StringUtil.isNullOrEmpty(builder.getUuid())) {
					builder.setUuid(uuid);
					logger.debug("creating shipment group");
					return uServiceClient.createShipmentGroup(builder.build(), requestContext);
				} else {
					logger.debug("updating shipment group");
					return uServiceClient.updateShipmentGroup(builder.build(), requestContext);
				}
			}
		};
	}

	private Func1<List<ShipmentGroup>, Observable<ShipmentGroup>> returnFoundGroupOrQueryByWorkId(
		final PartGroupDTO partGroupDTO, final RequestContext requestContext) {
		return new Func1<List<ShipmentGroup>, Observable<ShipmentGroup>>() {
			@Override
			public Observable<ShipmentGroup> call(final List<ShipmentGroup> group) {
				if (!group.isEmpty()) {
					logger.debug("found a group");
					return Observable.just(group.get(0));
				}
				if (partGroupDTO.getWorkId() == null) {
					logger.debug("no workid, just returning null");
					return Observable.empty();
				}
				logger.debug("searching for group by work id");
				return uServiceClient.getShipmentGroupByWorkId(String.valueOf(partGroupDTO.getWorkId()),
					requestContext);
			}
		};
	}

	private Func1<List<ShipmentGroup>, ShipmentGroupBuilder> groupsBuilderIfFoundOrNewBuilder() {
		return new Func1<List<ShipmentGroup>, ShipmentGroupBuilder>() {
			@Override
			public ShipmentGroupBuilder call(final List<ShipmentGroup> group) {
				if (group.isEmpty()) {
					logger.debug("group list is empty");
					return ShipmentGroup.builder();
				}
				logger.debug("got a group!");
				return group.get(0).toBuilder();
			}
		};
	}

	@Override
	public PartDTO saveOrUpdatePart(final PartDTO partDTO, final String partGroupUuid) {
		Assert.notNull(partGroupUuid);
		Assert.notNull(partDTO);

		logger.debug("save or update parts");

		final Observable<PartDTO> savedPartsObservable;
		if (StringUtil.isNullOrEmpty(partDTO.getUuid())) {
			logger.debug("Creating a part");
			savedPartsObservable = newSaveOrUpdateParts(
					ImmutableList.of(sanitizePartDTO(partDTO)), ImmutableList.<PartDTO>of(), partGroupUuid);
		} else {
			logger.debug("updating a part");
			savedPartsObservable = newSaveOrUpdateParts(
					ImmutableList.<PartDTO>of(), ImmutableList.of(partDTO), partGroupUuid);
		}

		final List<PartDTO> savedParts = savedPartsObservable
			.map(updatePartCache())
			.toList().toBlocking().single();

		if (CollectionUtilities.isEmpty(savedParts)) {
			return null;
		}
		return getOnlyElement(savedParts);
	}

	@Override
	public void deletePartGroup(final Long workId) {
		logger.debug("DELETE PART GROUP");
		Assert.notNull(workId);
		//TODO make endpoint to delete by workid
		final RequestContext requestContext = webRequestContextProvider.getRequestContext();
		uServiceClient.getShipmentGroupByWorkId(String.valueOf(workId), requestContext)
			.toList()
			.flatMap(new Func1<List<ShipmentGroup>, Observable<List<ShipmentGroup>>>() {
				@Override
				public Observable<List<ShipmentGroup>> call(final List<ShipmentGroup> groups) {
					if (groups.size() != 1) {
						return Observable.just(null);
					}
					final String uuid = groups.get(0).getUuid();
					return uServiceClient.deleteShipmentGroup(uuid, requestContext)
						.toList().single();
				}
			}).toList().toBlocking().single();
	}

	@Override
	public void deletePart(final String uuid) {
	    logger.debug("DELETE PART!");
		final RequestContext requestContext = webRequestContextProvider.getRequestContext();

		uServiceClient.deleteShipment(uuid, requestContext)
			.toBlocking().single();
	}

	// appears to be a functional (in the mathematical sense) but isn't
	private PartDTO sanitizePartDTO(final PartDTO partDTO) {
		partDTO.setName(StringUtilities.stripXSSAndEscapeHtml(partDTO.getName()));
		partDTO.setTrackingNumber(StringUtilities.stripXSSAndEscapeHtml(partDTO.getTrackingNumber()));
		partDTO.setPartValue(partDTO.isSetPartValue() ? partDTO.getPartValue() : new BigDecimal(0));
		return partDTO;
	}

	private ImmutablePair<Iterable<PartDTO>, Iterable<PartDTO>> splitCreateFromUpdate(
			final Iterable<PartDTO> partDTOs) {
		final Predicate<PartDTO> hasUuid = new Predicate<PartDTO>() {
			@Override
			public boolean apply(final PartDTO partDTO) {
				return !StringUtil.isNullOrEmpty(partDTO.getUuid());
			}
		};

		final Iterable<PartDTO> newDTOs = ImmutableList.copyOf(Iterables.filter(partDTOs, Predicates.not(hasUuid)));
		final Iterable<PartDTO> updateDTOs = ImmutableList.copyOf(Iterables.filter(partDTOs, hasUuid));

		for (final PartDTO partDTO : newDTOs) {
			sanitizePartDTO(partDTO);
		}
		return ImmutablePair.of(newDTOs, updateDTOs);
	}

	private Func1<PartDTO, PartDTO> updatePartCache() {
		return new Func1<PartDTO, PartDTO>() {
			@Override
			public PartDTO call(final PartDTO partDTO) {
				final String uuid = partDTO.getUuid();
				final Optional<PartDTO> partDTOOptional = partCache.getPart(uuid);
				if (partDTOOptional.isPresent()) {
					partDTO.setTrackingStatus(partDTOOptional.get().getTrackingStatus());
					if (!partDTOOptional.get().equals(partDTO)) {
						partCache.putPart(partDTO);
					}
				}
				return partDTO;
			}
		};
	}

  private Observable<PartDTO> newSaveOrUpdateParts(
        final Iterable<PartDTO> createDTOs,
        final Iterable<PartDTO> updateDTOs,
        final String partGroupUuid) {

		final RequestContext requestContext = webRequestContextProvider.getRequestContext();
		logger.debug("new save or update parts");
		logger.debug("new Creating {} parts", Iterables.size(createDTOs));
		logger.debug("new Updating {} parts", Iterables.size(updateDTOs));

		final Func1<PartDTO, PartDTO> partTrack = new Func1<PartDTO, PartDTO>() {
			@Override
			public PartDTO call(final PartDTO part) {
				return trackPart(part);
			}
		};

	  final Observable<String> newIds = Observable.from(createDTOs).flatMap(listItemToGeneratedId());
	  final Observable<Observable<Shipment>> created = Observable.from(createDTOs)
		  .zipWith(newIds, new Func2<PartDTO, String, PartDTO>() { // give it a uuid
			  @Override
			  public PartDTO call(final PartDTO part, final String uuid) {
				  part.setUuid(uuid);
				  return part;
			  }
		  })
	    .map(partTrack)
	    .map(CONVERT_PART_DTO_TO_SHIPMENT)
	    .map(new Func1<Shipment, Observable<Shipment>>() {
		    @Override
		    public Observable<Shipment> call(final Shipment shipment) {
			    logger.debug("inside creating!");
			    // service should update the shipping provider if empty and has tracking info
			    return uServiceClient.createShipment(
					    shipment.toBuilder().setShipmentGroupUuid(partGroupUuid).build(),
					    requestContext);
		    }
	    });

	  final Observable<Observable<Shipment>> updated = Observable.from(updateDTOs)
		  .map(CONVERT_PART_DTO_TO_SHIPMENT)
		  .map(new Func1<Shipment, Observable<Shipment>>() {
			  @Override
			  public Observable<Shipment> call(final Shipment shipment) {
				  return uServiceClient.updateShipment(shipment, requestContext);
			  }
		  });

	  return created.concatWith(updated)
		  .flatMap(PartServiceImpl.<Shipment>identity())
		  .map(ShipmentConverter.CONVERT_SHIPMENT_TO_PART_DTO)
		  // yes, I know this is done twice, but since we don't store tracking
		  // status in the service, for now, this is what we have to do
		  .map(partTrack);
  }

	private PartDTO trackPart(final PartDTO partDTO) {
		Assert.notNull(partDTO);
		Assert.notNull(partDTO.getUuid());

		final TrackingNumberResponse response = trackingNumberAdapter.track(partDTO.getTrackingNumber());
		if (response.isSuccessful()) {
			partDTO.setShippingProvider(response.getShippingProvider());
			partDTO.setTrackingStatus(response.getTrackingStatus());
		} else if (AfterShipError.TRACKING_EXISTS.equals(response.getMetaCode())) {
			setTrackingStatus(partDTO);
		} else {
			partDTO.setShippingProvider(ShippingProvider.OTHER);
			partDTO.setTrackingStatus(TrackingStatus.NOT_AVAILABLE);
		}

		if (!TrackingStatus.PENDING.equals(response.getTrackingStatus())) {
			partCache.putPart(partDTO);
		}

		return partDTO;
	}

	@Override
	public List<PartDTO> getPartsByGroupUuid(final String uuid) {
	    logger.debug("GET PARTS BY GROUP UUID");
		final RequestContext requestContext = webRequestContextProvider.getRequestContext();
		Assert.notNull(uuid);

		if (StringUtils.isBlank(uuid)) { // not sure why this happens, but whatever....
			return ImmutableList.of();
		}
		final Observable<Shipment> shipments = uServiceClient.getShipments(uuid, requestContext);
		return shipments
			.map(CONVERT_SHIPMENT_TO_PART_DTO)
			.map(addInTrackingInfo())
			.toList().toBlocking().single();
	}

	///////// function stuff to make observables easier

	// x -> idGenerator.next()
	private Func1<Object, Observable<String>> listItemToGeneratedId() {
		return new Func1<Object, Observable<String>>() {
			@Override
			public Observable<String> call(final Object arg0) {
				return idGenerator.next();
			}
		};
	}

	// ShipmentGroup::getShipments
	private Func1<ShipmentGroup, List<Shipment>> extractShipmentsFromGroup() {
		return new Func1<ShipmentGroup, List<Shipment>>() {
			@Override
			public List<Shipment> call(final ShipmentGroup group) {
				return group.getShipments();
			}
		};
	}

	// Shipment::getUuid
	private Func1<Shipment, String> extractShipmentUuid() {
		return new Func1<Shipment, String>() {
			@Override
			public String call(final Shipment shipment) {
				return shipment.getUuid();
			}
		};
	}

	// x -> x
	private static <T> Func1<Observable<T>, Observable<T>> identity() {
		return new Func1<Observable<T>, Observable<T>>() {
			@Override
			public Observable<T> call(final Observable<T> x) {
				return x;
			}
		};
	}

	// Observable::from
	private static <T> Func1<List<T>, Observable<T>> fromList() {
		return new Func1<List<T>, Observable<T>>() {
			@Override
			public Observable<T> call(final List<T> list) {
				return Observable.from(list);
			}
		};
	}
}

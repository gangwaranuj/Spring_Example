package com.workmarket.api.v2.employer.assignments.services;

import com.google.common.collect.Lists;
import com.workmarket.api.v2.employer.assignments.controllers.support.AssignmentMaker;
import com.workmarket.api.v2.employer.assignments.controllers.support.ConfigurationMaker;
import com.workmarket.api.v2.employer.assignments.controllers.support.ShipmentGroupMaker;
import com.workmarket.api.v2.employer.assignments.models.AssignmentDTO;
import com.workmarket.api.v2.employer.assignments.models.ConfigurationDTO;
import com.workmarket.api.v2.employer.assignments.models.ShipmentDTO;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.work.model.part.ShippingDestinationType;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.dto.PartGroupDTO;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.option.CompanyOptionsService;
import com.workmarket.service.thrift.TWorkFacadeService;
import com.workmarket.thrift.work.WorkActionException;
import com.workmarket.thrift.work.WorkRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import com.workmarket.api.v2.employer.assignments.models.ShipmentGroupDTO;
import static com.natpryce.makeiteasy.MakeItEasy.*;
import static com.natpryce.makeiteasy.MakeItEasy.with;
import static com.workmarket.api.v2.employer.assignments.controllers.support.AssignmentMaker.configuration;
import static com.workmarket.api.v2.employer.assignments.controllers.support.AssignmentMaker.shipmentGroup;
import static com.workmarket.api.v2.employer.assignments.controllers.support.ConfigurationMaker.shipmentsEnabled;
import static com.workmarket.api.v2.employer.assignments.controllers.support.ShipmentGroupMaker.shipments;
import static com.workmarket.api.v2.employer.assignments.controllers.support.ShipmentGroupMaker.shippingAddress;
import static com.workmarket.api.v2.employer.assignments.controllers.support.ShipmentGroupMaker.shippingDestinationType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;
import com.workmarket.thrift.work.WorkResponse;
import com.workmarket.thrift.work.Work;
import com.workmarket.thrift.work.ManageMyWorkMarket;

@RunWith(MockitoJUnitRunner.class)
public class UpdateShipmentsUseCaseTest {
	private final long expectedUserId = 123L;
	private final long expectedCompanyId = 456L;
	private final long expectedWorkId = 789L;
	private final String expectedFullName = "test";

	private User mockUser;
	private Company mockCompany;
	private com.workmarket.thrift.core.User mockCoreUser;
	private WorkResponse mockWorkResponse;
	private Work mockWork;
	private PartGroupDTO mockPartGroupDTO;
	private ManageMyWorkMarket mockConfiguration;

	@InjectMocks private UpdateShipmentsUseCase updateShipmentsUseCase = new UpdateShipmentsUseCase();

	@Mock private AuthenticationService mockAuthenticationService;
	@Mock private ProfileService mockProfileService;
	@Mock private TWorkFacadeService mockTWorkFacadeService;
	@Mock private CompanyOptionsService companyOptionsService;

	@Before
	public void setUp() throws WorkActionException {
		mockUser = new User();
		mockUser.setId(expectedUserId);

		mockCoreUser = new com.workmarket.thrift.core.User();
		mockCoreUser.setId(expectedUserId);

		mockCompany = new Company();
		mockCompany.setId(expectedCompanyId);

		mockPartGroupDTO = new PartGroupDTO();

		mockConfiguration = new ManageMyWorkMarket();
		mockConfiguration.setPaymentTermsDays(5);

		mockWork = new Work();
		mockWork.setId(expectedWorkId);
		mockWork.setPartGroup(mockPartGroupDTO);
		mockWork.setConfiguration(mockConfiguration);

		mockWorkResponse = new WorkResponse();
		mockWorkResponse.setLastRatingBuyerFullName(expectedFullName);
		mockWorkResponse.setWork(mockWork);

		ConfigurationDTO.Builder configurationDTOBuilder = new ConfigurationDTO.Builder(make(an(ConfigurationMaker.ConfigurationDTO,
			with(shipmentsEnabled, false))));
		ShipmentGroupDTO.Builder shipmentGroupBuilder = new ShipmentGroupDTO.Builder(make(a(ShipmentGroupMaker.ShipmentGroupDTO,
			with(shippingDestinationType, ShippingDestinationType.WORKER),
			withNull(shippingAddress),
			with(shipments, Lists.<ShipmentDTO.Builder>newArrayList()))));
		AssignmentDTO assignmentDTO = make(an(AssignmentMaker.AssignmentDTO, with(shipmentGroup, shipmentGroupBuilder),
			with(configuration, configurationDTOBuilder)));
		ShipmentGroupDTO shipmentGroupDTO = assignmentDTO.getShipmentGroup();
		ConfigurationDTO configurationDTO = assignmentDTO.getConfiguration();


		updateShipmentsUseCase.id = "123";
		updateShipmentsUseCase.shipmentGroupDTO = shipmentGroupDTO;
		updateShipmentsUseCase.readyToSend = true;
		updateShipmentsUseCase.workResponse = mockWorkResponse;
		updateShipmentsUseCase.user = mockCoreUser;
		updateShipmentsUseCase.configurationDTOBuilder = configurationDTOBuilder;
		updateShipmentsUseCase.shipmentGroupDTOBuilder = shipmentGroupBuilder;
		updateShipmentsUseCase.configurationDTO = configurationDTO;

		when(mockAuthenticationService.getCurrentUser()).thenReturn(mockUser);
		when(mockProfileService.findCompany(anyLong())).thenReturn(mockCompany);
		when(mockTWorkFacadeService.findWork(any(WorkRequest.class))).thenReturn(mockWorkResponse);
	}

	@Test
	public void when_init_then_get_user_generate_workrequest_get_workresponse() throws WorkActionException {
		updateShipmentsUseCase.init();

		final long expectedId = updateShipmentsUseCase.user.getId();
		final long actualId = mockUser.getId();

		final long workRequestUserId = updateShipmentsUseCase.workRequest.getUserId();

		final String actualFullName = updateShipmentsUseCase.workResponse.getLastRatingBuyerFullName();

		assertEquals(expectedId, actualId);
		assertEquals(workRequestUserId, actualId);
		assertEquals(expectedFullName, actualFullName);
	}

	@Test
	public void prepare_test() {
		updateShipmentsUseCase.prepare();

		final Work actualWork = updateShipmentsUseCase.work;
		final PartGroupDTO actualShipments = updateShipmentsUseCase.shipments;
		final ManageMyWorkMarket actualConfiguration = updateShipmentsUseCase.configuration;

		assertEquals(mockWork, actualWork);
		assertEquals(mockPartGroupDTO, actualShipments);
		assertEquals(mockConfiguration, actualConfiguration);
	}

	@Test
	public void shipments_should_be_null_if_shipmentsEnabled_is_false() {
		updateShipmentsUseCase.loadShipments();

		final PartGroupDTO actualShipments = updateShipmentsUseCase.shipments;

		assertNull(actualShipments);
	}

	@Test
	public void shipments_should_not_be_null_if_shipmentsEnabled_is_true() {
		ConfigurationDTO.Builder configurationDTOBuilder = new ConfigurationDTO.Builder(updateShipmentsUseCase.configurationDTO).setShipmentsEnabled(true);
		updateShipmentsUseCase.configurationDTO = configurationDTOBuilder.build();
		updateShipmentsUseCase.loadShipments();

		final PartGroupDTO actualShipments = updateShipmentsUseCase.shipments;

		assertNotNull(actualShipments);
		assertEquals(ShippingDestinationType.WORKER.name(), actualShipments.getShippingDestinationType().name());
	}

}

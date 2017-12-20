package com.workmarket.service.network;

import com.google.common.collect.Lists;
import com.workmarket.dao.company.CompanyDAO;
import com.workmarket.domains.groups.dao.UserGroupDAO;
import com.workmarket.dao.network.CompanyNetworkAssociationDAO;
import com.workmarket.dao.network.NetworkDAO;
import com.workmarket.dao.network.UserGroupNetworkAssociationDAO;
import com.workmarket.dao.network.UserNetworkAssociationDAO;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.model.network.Network;
import com.workmarket.domains.model.network.UserGroupNetworkAssociation;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.UserGroupService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.search.group.GroupSearchService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class NetworkServiceImplTest {

	@Mock NetworkDAO networkDAO;
	@Mock UserGroupNetworkAssociationDAO userGroupNetworkAssociationDAO;
	@Mock UserNetworkAssociationDAO userNetworkAssociationDAO;
	@Mock CompanyNetworkAssociationDAO companyNetworkAssociationDAO;
	@Mock UserService userService;
	@Mock CompanyService companyService;
	@Mock CompanyDAO companyDAO;
	@Mock UserGroupService groupService;
	@Mock GroupSearchService groupSearchService;
	@Mock UserGroupDAO userGroupDAO;

	@InjectMocks NetworkService service = spy(new NetworkServiceImpl());

	List<Long> networkIds;
	List<Long> emptyNetworkIds;
	UserGroup userGroup;
	Network network;
	List<Network> networks;
	UserGroupNetworkAssociation ugna;
	Company company;

	@Before
	public void setUp() throws Exception {
		networkIds = new ArrayList<>();
		networkIds.add(1L);

		emptyNetworkIds = new ArrayList<>();

		company = mock(Company.class);
		when(company.getId()).thenReturn(1L);

		userGroup = mock(UserGroup.class);
		when(userGroup.getId()).thenReturn(1L);

		network = mock(Network.class);
		when(network.getId()).thenReturn(1L);

		networks = mock(List.class);
		networks.add(network);

		ugna = mock(UserGroupNetworkAssociation.class);

		when(service.findNetworkById(network.getId())).thenReturn(network);
		when(groupService.findGroupByIdNoAssociations(anyLong())).thenReturn(userGroup);
	}

	@Test
	public void findAllCompanyNetworks_callFindAllActiveNetworks_success() throws Exception {
		service.findAllCompanyNetworkIds(anyLong());
		verify(networkDAO).findAllActiveNetworks(anyLong());
	}

	@Test
	public void findAllCompanyNetworks_whenCalledWithNull_throwException() throws Exception {
		try {
			service.findAllCompanyNetworkIds(null);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {}
	}

	@Test
	public void addGroupToNetwork_callDAO_success() throws Exception {
		service.addGroupToNetwork(userGroup.getId(), network.getId());
		verify(userGroupNetworkAssociationDAO).addGroupToNetwork(userGroup, network);
	}

	@Test
	public void addGroupToNetwork_callFindAssociationByNetworkAndGroup_success() throws Exception {
		service.isGroupInNetwork(userGroup.getId(), network.getId());
		verify(userGroupNetworkAssociationDAO).findAssociationByGroupIdAndNetworkId(userGroup.getId(), network.getId());
	}

	@Test
	public void findAllCompanyNetworkIds_callFindAllActiveNetworks_success() throws Exception {
		service.findAllCompanyNetworkIds(anyLong());
		verify(networkDAO).findAllActiveNetworks(anyLong());
	}

	@Test
	public void findNetworksWhereGroupIsShared_withNetworkId_findsNetworks() {
		service.findNetworksWhereGroupIsShared(anyLong());
		verify(userGroupNetworkAssociationDAO).findNetworksWhereGroupIsShared(anyLong());
	}

	@Test
	public void addGroupToCompanyNetworks_withNetworkIdAndGroupId_addsGroupToNetwork() {
		service.addGroupToCompanyNetworks(anyLong(), anyLong());
		verify(groupService).findGroupByIdNoAssociations(anyLong());
		verify(service).findAllCompanyNetworkIds(anyLong());

	}

	@Test
	public void addGroupToCompanyNetworks_withNetworkIdAndGroupId_findsNetworks() {
		Network network1 = new Network();
		network1.setId(1L);
		when(networkDAO.findAllActiveNetworks(eq(company.getId()))).thenReturn(Lists.newArrayList(network));
		service.addGroupToCompanyNetworks(userGroup.getId(), company.getId());
		verify(userGroupNetworkAssociationDAO).addGroupToNetwork(userGroup, network);
	}

	@Test
	public void addGroupToCompanyNetworks_withNetworkIdAndGroupId_findsNoNetworks() {
		when(service.findAllCompanyNetworkIds(company.getId())).thenReturn(emptyNetworkIds);
		service.addGroupToCompanyNetworks(userGroup.getId(), company.getId());
		verify(userGroupNetworkAssociationDAO, never()).addGroupToNetwork(userGroup, network);
	}
}

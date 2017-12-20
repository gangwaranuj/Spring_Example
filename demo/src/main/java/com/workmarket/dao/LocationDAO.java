package com.workmarket.dao;

import com.workmarket.domains.model.Location;
import com.workmarket.domains.model.crm.ClientLocation;
import com.workmarket.domains.model.crm.ClientLocationPagination;

import java.util.List;

public interface LocationDAO extends DAOInterface<Location> {

	Location findLocationById(Long id);

	<T extends Location> T findLocationById(Class<? extends Location> clazz, Long locationId);

	<T extends Location> T findLocationByIdAndCompany(Class<? extends Location> clazz, Long locationId, Long companyId);

	ClientLocation findPrimaryLocationByClient(Long clientCompanyId);

	List<ClientLocation> findLocationByNumberAndCompany(String locationNumber, Long companyId);

	List<ClientLocation> findLocationsByNumberAndClientCompany(String locationNumber, Long clientCompanyId);

	List<ClientLocation> findLocationsByClientCompanyAndName(Long clientCompanyId, String name);

	List<ClientLocation> findLocationsByCompanyAndName(Long clientCompanyId, String name);

	List<ClientLocation> findAllLocationsByClientCompany(Long companyId, Long clientCompanyId);

	List<ClientLocation> findAllLocationsByCompany(Long companyId);

	ClientLocationPagination findAllLocationsByClientCompany(Long companyId, Long clientCompanyId, ClientLocationPagination pagination);

	ClientLocationPagination findAllLocations(Long companyId, ClientLocationPagination pagination);

	ClientLocation findLocationByClientCompanyAndName(
			Long clientCompanyId, String name, String address1,
			String city, String state, String postalCode);

	ClientLocation findLocationByClientCompanyAndLocationNumber(
			Long clientCompanyId, String number, String address1, String city,
			String state, String postalCode);

	ClientLocation findLocationByClientCompanyAndLocationNumber(
		Long companyId, Long clientCompanyId, String locationNumber,
		String locationAddressLine1, String locationCity, String locationState,
		String locationPostalCode);

	ClientLocation findLocationByClientCompanyAndName(
		Long companyId, Long clientCompanyId, String locationName,
		String locationAddressLine1, String locationCity, String locationState,
		String locationPostalCode);
}

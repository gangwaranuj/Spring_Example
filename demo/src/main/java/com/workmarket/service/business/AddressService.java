package com.workmarket.service.business;

import com.workmarket.domains.model.Address;
import com.workmarket.domains.model.geocoding.Coordinate;
import com.workmarket.domains.model.postalcode.State;
import com.workmarket.dto.AddressDTO;
import com.workmarket.service.business.dto.AddressVerificationDTO;
import com.workmarket.web.models.MessageBundle;

import java.util.List;

public interface AddressService {

	Address findById(Long addressId);

	List<Address> findByIds(List<Long> addressIds);

	void saveOrUpdate(Address address);

	void saveOrUpdate(State state);

	Address saveOrUpdate(AddressDTO addressDTO);

	void addNewStateToAddress(Address address, String country, String state);

	AddressVerificationDTO verify(String address) throws Exception;

	Address verifyAndSave(Address address, MessageBundle messages) throws Exception;

	Coordinate getCoordinatesForUser(Long userId);

	Coordinate getCoordinatesByAddressId(Long addressId);
}

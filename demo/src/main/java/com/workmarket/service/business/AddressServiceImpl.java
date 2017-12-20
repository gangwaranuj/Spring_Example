package com.workmarket.service.business;

import com.workmarket.configuration.Constants;
import com.workmarket.dao.AddressDAO;
import com.workmarket.dao.DressCodeDAO;
import com.workmarket.dao.LocationTypeDAO;
import com.workmarket.dao.postalcode.StateDAO;
import com.workmarket.domains.model.Address;
import com.workmarket.domains.model.AddressType;
import com.workmarket.domains.model.DressCode;
import com.workmarket.domains.model.LocationType;
import com.workmarket.domains.model.geocoding.Coordinate;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.postalcode.State;
import com.workmarket.dto.AddressDTO;
import com.workmarket.service.business.dto.AddressVerificationDTO;
import com.workmarket.service.exception.geo.GeocodingException;
import com.workmarket.service.infra.business.GeocodingService;
import com.workmarket.service.infra.business.InvariantDataService;
import com.workmarket.service.infra.geo.GeocodingErrorType;
import com.workmarket.web.models.MessageBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

	@Autowired private GeocodingService geocodingService;
	@Autowired private AddressDAO addressDAO;
	@Autowired private StateDAO stateDAO;
	@Autowired private DressCodeDAO dressCodeDAO;
	@Autowired private LocationTypeDAO locationTypeDAO;
	@Autowired private InvariantDataService invariantDataService;
	@Autowired private ProfileService profileService;

	@Override
	public Address findById(Long addressId)  {
		return addressDAO.get(addressId);
	}

	@Override
	public List<Address> findByIds(List<Long> addressIds) {
		return addressDAO.get(addressIds);
	}


	@Override
	public void saveOrUpdate(Address address)  {
		addressDAO.saveOrUpdate(address);
		BigDecimal latitude = address.getLatitude();
		BigDecimal longitude = address.getLongitude();
		if (latitude == null || longitude == null || (latitude.equals(BigDecimal.ZERO) && longitude.equals(BigDecimal.ZERO))) {
			geocodingService.geocode(address.getId());
		}
	}

	@Override
	public void saveOrUpdate(State state)  {
		stateDAO.saveOrUpdate(state);

		// When a new state is saved, we want to test the lookup immediately.
		//   The goal here is to prevent duplicates from being inserted.
		//   If a duplicate was inserted, this lookup will cause a
		//   NonUniqueResultException and rollback the entire transaction that
		//   caused the insertion. Because Legacy.
		invariantDataService.findStateWithCountryAndState(state.getCountry().getName(), state.getName());
	}

	@Override
	public Address saveOrUpdate(AddressDTO addressDTO)  {
		Assert.notNull(addressDTO);
		Assert.hasText(addressDTO.getCountry());

		Address address;
		if (addressDTO.getAddressId() == null) {
			address = addressDTO.toAddress();
		} else {
			address = findById(addressDTO.getAddressId());
			Assert.notNull(address);

			address.setAddress1(addressDTO.getAddress1());
			address.setAddress2(addressDTO.getAddress2());
			address.setCity(addressDTO.getCity());
			address.setPostalCode(addressDTO.getPostalCode());
			address.setLatitude(addressDTO.getLatitude());
			address.setLongitude(addressDTO.getLongitude());
			address.setCountry(Country.valueOf(addressDTO.getCountry()));
			address.setAddressType(AddressType.newAddressType(addressDTO.getAddressTypeCode()));
		}

		State state = invariantDataService.findStateWithCountryAndState(
			Country.valueOf(addressDTO.getCountry()).getId(), StringUtils.isEmpty(addressDTO.getState()) ? Constants.NO_STATE : addressDTO.getState()
		);
		if (state != null) {
			address.setState(state);
			address.setCountry(state.getCountry());
		} else {
			addNewStateToAddress(address, addressDTO.getCountry(), addressDTO.getState());
		}

		DressCode dressCode = dressCodeDAO.findDressCodeById(addressDTO.getDressCodeId() != null ?
				addressDTO.getDressCodeId() : DressCode.BUSINESS_CASUAL);
		address.setDressCode(dressCode);

		LocationType locationType = locationTypeDAO.findLocationTypeById(addressDTO.getLocationTypeId() != null ?
				addressDTO.getLocationTypeId() : LocationType.COMMERCIAL_CODE);
		address.setLocationType(locationType);

		saveOrUpdate(address);

		return address;
	}

	@Override
	public void addNewStateToAddress(Address address, String country, String state) {
		Assert.hasText(country);
		State newStateOrProvince = new State();
		newStateOrProvince.setShortName(state);
		newStateOrProvince.setName(StringUtils.isEmpty(state) ? Constants.NO_STATE : state);
		newStateOrProvince.setCountry(invariantDataService.findCountryById(country));
		saveOrUpdate(newStateOrProvince);
		address.setState(newStateOrProvince);
	}

	@Override
	public AddressVerificationDTO verify(String address) throws Exception  {
		return geocodingService.verify(address);
	}

	@Override
	public Address verifyAndSave(Address address, MessageBundle messages) throws Exception {
		Assert.notNull(address);

		Address verifiedAddress = null;
		try {
			verifiedAddress = geocodingService.geocodeReturnAddress(address);
		} catch (GeocodingException e) {
			if (e.getErrorType().equals(GeocodingErrorType.POSTAL_CODE_MISMATCH)) {
				messages.addError(e.getMessage());
				return null;
			}
		}
		addressDAO.saveOrUpdate(verifiedAddress);
		return address;
	}

	@Override
	public Coordinate getCoordinatesForUser(Long userId) {
		Assert.notNull(userId);

		Long addressId = profileService.findProfileDTO(userId).getAddressId();
		if (addressId != null) {
			Address address = addressDAO.get(addressId);
			if (address.getLongitude() == null || address.getLatitude() == null) {
				return null;
			}

			return new Coordinate(address.getLongitude().doubleValue(), address.getLatitude().doubleValue());
		}
		return null;
	}

	@Override
	public Coordinate getCoordinatesByAddressId(Long addressId) {
		Assert.notNull(addressId);

		Address address = addressDAO.get(addressId);
		if (address.getLongitude() == null || address.getLatitude() == null) {
			return null;
		}

		return new Coordinate(address.getLongitude().doubleValue(), address.getLatitude().doubleValue());
	}
}

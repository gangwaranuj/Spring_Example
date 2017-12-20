package com.workmarket.service.business;

import com.workmarket.dao.datetime.TimeZoneDAO;
import com.workmarket.dao.profile.ProfileDAO;
import com.workmarket.domains.model.Address;
import com.workmarket.domains.model.Profile;
import com.workmarket.domains.model.datetime.TimeZone;
import com.workmarket.domains.model.postalcode.PostalCode;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.business.InvariantDataService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

@Service
public class DateTimeServiceImpl implements DateTimeService {
	private static final Log logger = LogFactory.getLog(DateTimeServiceImpl.class);
	@Autowired private TimeZoneDAO timeZoneDAO;
	@Autowired private ProfileDAO profileDAO;
	@Autowired private InvariantDataService invariantDataService;
	@Autowired private AddressService addressService;

	@Override
	public List<TimeZone> findAllActiveTimeZones() {
		return timeZoneDAO.findAllActiveTimeZones();
	}

	@Override
	public TimeZone findTimeZonesById(Long timeZoneId) {
		return timeZoneDAO.findTimeZonesById(timeZoneId);
	}

	@Override
	public TimeZone findTimeZonesByTimeZoneId(String timeZoneId) {
		return timeZoneDAO.findTimeZonesByTimeZoneId(timeZoneId);
	}

	@Override
	@Transactional(readOnly = false)
	public TimeZone matchTimeZoneForUser(Long userId) {
		Assert.notNull(userId);

		Profile profile = profileDAO.findByUser(userId);

		if (profile != null && profile.getAddressId() != null) {
			Address address = addressService.findById(profile.getAddressId());

			if (address.getPostalCode() != null) {
				PostalCode postal = invariantDataService.getPostalCodeByCodeCountryStateCity(address.getPostalCode(), address.getCountry().getId(), address.getState().getShortName(), address.getCity());
				if (postal != null) {
					if (!postal.getTimeZone().getDeleted())
						return postal.getTimeZone();
				}
			}
		}
		return invariantDataService.findTimeZonesByTimeZoneId(Constants.WM_TIME_ZONE);
	}

	@Override
	@Transactional(readOnly = false)
	public TimeZone matchTimeZoneForPostalCode(String postalCode, String country, String state, String city) {
		PostalCode postal = invariantDataService.getPostalCodeByCodeCountryStateCity(postalCode, country, state, city);

		if (postal != null) {
			if (!postal.getTimeZone().getDeleted())
				return postal.getTimeZone();
		}

		return invariantDataService.findTimeZonesByTimeZoneId(Constants.WM_TIME_ZONE);
	}
}

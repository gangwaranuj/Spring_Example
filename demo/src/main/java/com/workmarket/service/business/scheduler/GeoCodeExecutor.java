package com.workmarket.service.business.scheduler;

import com.workmarket.service.business.AddressService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.business.GeocodingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class GeoCodeExecutor implements ScheduledExecutor {

	@Qualifier("jdbcTemplate") @Autowired private NamedParameterJdbcTemplate jdbcTemplate;
	@Autowired AddressService addressService;
	@Autowired GeocodingService geocodingService;
	@Autowired AuthenticationService authenticationService;

	public void execute() {
		String sql = "select a.id from address a where latitude is null and created_on >= '2014-01-01'";

		authenticationService.setCurrentUser(1L);
		List<Long> addressess = jdbcTemplate.queryForList(sql, Collections.EMPTY_MAP, Long.class);
		for (Long address : addressess) {
			geocodingService.geocode(address);
		}
	}


}
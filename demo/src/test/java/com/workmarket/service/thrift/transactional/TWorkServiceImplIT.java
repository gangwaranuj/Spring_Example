package com.workmarket.service.thrift.transactional;

import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.dto.WorkDTO;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.test.IntegrationTest;
import com.workmarket.thrift.core.Address;
import com.workmarket.thrift.core.Location;
import com.workmarket.thrift.core.User;
import com.workmarket.service.thrift.TWorkUploadService;
import com.workmarket.thrift.work.Work;
import com.workmarket.thrift.work.WorkSaveRequest;
import com.workmarket.thrift.work.uploader.WorkUploadRequest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class TWorkServiceImplIT extends BaseServiceIT {

	@Autowired TWorkService tWorkService;
	@Autowired AuthenticationService authenticationService;
	@Autowired TWorkUploadService uploader;

	@Test
	@Transactional
	public void startUploadEventHelper_setsCurrentUser() {
		WorkUploadRequest uploadRequest = new WorkUploadRequest("12345", "12345", true);
		tWorkService.startUploadEventHelper(uploadRequest, (long) 1003);
		assertEquals((long) authenticationService.getCurrentUserId(), (long) 1003);
	}

	// tests the location override for work.
	@Test
	public void saveWorkCore__testTimeZoneLocationAssignment_setsTimeZoneToPacific() {

		// test data setup
		Address address = new Address();
		address.setZip("90210");

		Location location = new Location();
		location.setAddress(address);

		User user = new User();
		user.setId(Constants.JEFF_WALD_USER_ID);

		Work work = new Work();
		work.setLocation(location);
		work.setBuyer(user);

		WorkSaveRequest uploadRequest = new WorkSaveRequest(Constants.JEFF_WALD_USER_ID, work);

		WorkDTO dto = new WorkDTO();
		tWorkService.saveWorkCore(uploadRequest, dto);

		// 148 is the time_zone.id for pacific.
		assertEquals(dto.getTimeZoneId().longValue(), 148L);
	}

	@Test
	public void saveWorkCore__testCanadianTimeZoneLocationAssignment_setsTimeZoneToPacific() {

		// test data setup
		Address address = new Address();
		address.setZip("M4B 1B3");

		Location location = new Location();
		location.setAddress(address);

		User user = new User();
		user.setId(Constants.JEFF_WALD_USER_ID);

		Work work = new Work();
		work.setLocation(location);
		work.setBuyer(user);

		WorkSaveRequest uploadRequest = new WorkSaveRequest(Constants.JEFF_WALD_USER_ID, work);

		WorkDTO dto = new WorkDTO();
		tWorkService.saveWorkCore(uploadRequest, dto);

		// 370 is the time_zone.id for pacific.
		assertEquals(dto.getTimeZoneId().longValue(), 370L);
	}

}

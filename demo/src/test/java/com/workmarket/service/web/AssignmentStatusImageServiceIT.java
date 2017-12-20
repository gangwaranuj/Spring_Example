package com.workmarket.service.web;

import com.workmarket.domains.model.User;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.WorkBundle;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.dto.WorkDTO;
import com.workmarket.test.IntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class AssignmentStatusImageServiceIT extends BaseServiceIT {

	@Autowired AssignmentStatusImageService assignmentStatusImageService;

	User user, resource, resource2;
	Work assignment;
	WorkBundle bundle;

	@Before
	public void setup() throws Exception {
		user = newFirstEmployeeWithCashBalance();
		resource = newContractor();
		resource2 = newContractor();

		WorkDTO workDTO = new WorkDTO();
		workDTO.setTitle("Do Work!");
		workDTO.setDescription("Description of assignment.");
		workDTO.setIsOnsiteAddress(false);
		workDTO.setPricingStrategyId(FLAT_PRICING_STRATEGY);
		workDTO.setFlatPrice(100.00);
		workDTO.setIsScheduleRange(false);
		workDTO.setScheduleFromString("2010-09-02T09:00:00Z");

		assignment = workFacadeService.saveOrUpdateWork(user.getId(), workDTO);

		bundle = newWorkBundle(user.getId());
		Work bundleMemberOne = newWorkOnSiteWithLocation(user.getId());
		Work bundleMemberTwo = newWorkOnSiteWithLocation(user.getId());
		workBundleService.addToBundle(bundle, bundleMemberOne);
		workBundleService.addToBundle(bundle, bundleMemberTwo);
	}

	@Test
	public void getImageAsset_NotAvailable() throws Exception {
		laneService.addUserToCompanyLane2(resource.getId(), user.getCompany().getId());
		laneService.addUserToCompanyLane2(resource2.getId(), user.getCompany().getId());

		workRoutingService.addToWorkResources(assignment.getId(), resource.getId());
		workRoutingService.addToWorkResources(assignment.getId(), resource2.getId());
		workService.acceptWork(resource.getId(), assignment.getId());

		ImageAsset expected = ImageAsset.NOT_AVAILABLE;
		ImageAsset actual = assignmentStatusImageService.getImageAsset(resource2, assignment.getWorkNumber());

		assertEquals(expected, actual);
	}

	@Test
	public void getImageAsset_Available() throws Exception {
		laneService.addUserToCompanyLane2(resource.getId(), user.getCompany().getId());

		workRoutingService.addToWorkResources(assignment.getId(), resource.getId());

		ImageAsset expected = ImageAsset.AVAILABLE;
		ImageAsset actual = assignmentStatusImageService.getImageAsset(resource, assignment.getWorkNumber());

		assertEquals(expected, actual);
	}

	@Test
	public void getImageAsset_NotAllowed() throws Exception {
		ImageAsset expected = ImageAsset.NOT_ALLOWED;
		ImageAsset actual = assignmentStatusImageService.getImageAsset(resource, assignment.getWorkNumber());

		assertEquals(expected, actual);
	}

	@Test
	public void getImageAsset_BundleNotAllowed() throws Exception {
		ImageAsset expected = ImageAsset.BUNDLE_NOT_ALLOWED;
		ImageAsset actual = assignmentStatusImageService.getImageAsset(resource, bundle.getWorkNumber());

		assertEquals(expected, actual);
	}
}

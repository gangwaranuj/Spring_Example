package com.workmarket.service.business;

import com.workmarket.service.business.dto.EducationHistoryDTO;
import com.workmarket.service.business.dto.EmploymentHistoryDTO;
import com.workmarket.service.business.dto.LinkedInProfileDTO;
import com.workmarket.test.BrokenTest;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(BrokenTest.class)
@Ignore
public class LinkedInProfileIT extends BaseServiceIT {

	@Autowired private ProfileService profileService;

	@Test
	// @Transactional
	public void test_getLinkedInAuthorizationUrl() throws Exception {
		/*String url = profileService.getLinkedInAuthorizationUrl(ANONYMOUS_USER_ID, null);
		Assert.assertNotNull(url);

		System.out.println(String.format("[in] url: %s", url));

		//https://www.linkedin.com/uas/oauth/authorize?oauth_token=abc72a3d-54a2-4f12-9e3c-99b7a976f8cb
		// 17107
		*/

		Boolean status = profileService.authorizeLinkedIn(ANONYMOUS_USER_ID, "49768");
		Assert.assertTrue(status);


		LinkedInProfileDTO profile = profileService.getLinkedInProfile(ANONYMOUS_USER_ID);

		System.out.println(String.format("[in] name:     %s %s", profile.getFirstName(), profile.getLastName()));
		System.out.println(String.format("[in] title:    %s", profile.getJobTitle()));
		System.out.println(String.format("[in] company:  %s", profile.getCompanyName()));
		System.out.println(String.format("[in] industry:  %s", profile.getIndustry()));
		System.out.println(String.format("[in] work #:   %s", profile.getWorkPhone()));
		System.out.println(String.format("[in] cell #:   %s", profile.getMobilePhone()));
		System.out.println(String.format("[in] photo:    %s", profile.getAvatarAbsoluteURI()));
		System.out.println(String.format("[in] address1: %s", profile.getAddress1()));
		System.out.println(String.format("[in] address2: %s", profile.getAddress2()));
		System.out.println(String.format("[in] zip code: %s", profile.getPostalCode()));
		System.out.println(String.format("[in] city:     %s", profile.getCity()));
		System.out.println(String.format("[in] state:    %s", profile.getState()));

		for (EmploymentHistoryDTO dto : profile.getEmploymentHistory()) {
			System.out.println(String.format("[in] Company Name:  %s ", dto.getCompanyName()));
			System.out.println(String.format("[in] Description:  %s ", dto.getDescription()));
			System.out.println(String.format("[in] Title:  %s ", dto.getTitle()));
		}

		for (EducationHistoryDTO dto : profile.getEducationHistory()) {
			System.out.println(String.format("[in] Activities:  %s ", dto.getActivities()));
			System.out.println(String.format("[in] Degree:  %s ", dto.getDegree()));
			System.out.println(String.format("[in] Field:  %s ", dto.getFieldOfStudy()));
			System.out.println(String.format("[in] School:  %s ", dto.getSchoolName()));
		}

	}

	public ProfileService getProfileService() {
		return profileService;
	}

	public void setProfileService(ProfileService profileService) {
		this.profileService = profileService;
	}
}


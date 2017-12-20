package com.workmarket.web.controllers.admin.manage;

import com.google.common.collect.ImmutableList;
import com.workmarket.domains.velvetrope.model.Admission;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.velvetrope.service.AdmissionService;
import com.workmarket.service.web.cachebusting.CacheBusterServiceImpl;
import com.workmarket.velvetrope.Venue;
import com.workmarket.web.controllers.BaseControllerUnitTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.View;

import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class AdminManageCompanyControllerTest extends BaseControllerUnitTest {

	public static final long BAD_COMPANY = 99999L;
	// BaseController Mocks
	@Mock private View mockView;
	@Mock private CacheBusterServiceImpl cacheBusterService;

	// AdminManageCompanyController Mocks
	@Mock private AdmissionService admissionService;

	@InjectMocks AdminManageCompanyController controller;

	private static final Long COMPANY = 8675309L;
	private MockMvc mockMvc;
	private Company company;
	private List<Admission> admissions;
	private Admission admission;

	protected static class MockAdminManageCompanyControllerRequest {
		public static MockHttpServletRequestBuilder features(Long companyId) {
			return MockMvcRequestBuilders.get("/admin/manage/company/features/" + companyId);
		}

		public static MockHttpServletRequestBuilder addAdmission(Long companyId, String venue) {
			return MockMvcRequestBuilders.post("/admin/manage/company/admissions/" + companyId + "/add")
				.param("venue", venue);
		}

		public static MockHttpServletRequestBuilder removeAdmission(Long companyId, String venue) {
			return MockMvcRequestBuilders.post("/admin/manage/company/admissions/" + companyId + "/remove")
				.param("venue", venue);
		}
	}

	@Before
	public void setUp() throws Exception {

		// CompanyService
		company = mock(Company.class);
		when(companyService.findCompanyById(COMPANY)).thenReturn(company);

		// AdmissionService
		admission = mock(Admission.class);
		when(admission.getVenue()).thenReturn(Venue.LOBBY);
		admissions = ImmutableList.of(admission);

		when(admissionService.findAllAdmissionsByCompanyIdForVenue(COMPANY, Venue.values())).thenReturn(admissions);

		mockMvc = standaloneSetup(controller)
			.setSingleView(mockView)
			.build();
	}

	@Test
	public void features_withValidCompanyId_IsOK() throws Exception {
		mockMvc.perform(MockAdminManageCompanyControllerRequest.features(COMPANY))
			.andExpect(status().isOk());
	}

	@Test
	public void features_withValidCompanyId_FindsCompany() throws Exception {
		mockMvc.perform(MockAdminManageCompanyControllerRequest.features(COMPANY));
		verify(companyService).findCompanyById(COMPANY);
	}

	@Test
	public void features_withInvalidCompanyId_IsNotFound404() throws Exception {
		mockMvc.perform(MockAdminManageCompanyControllerRequest.features(BAD_COMPANY))
			.andExpect(status().isNotFound());
	}

	@Test
	public void features_withValidCompanyId_AddsCompanyToModelAttributes() throws Exception {
		mockMvc.perform(MockAdminManageCompanyControllerRequest.features(COMPANY))
			.andExpect(model().attribute("company", company));
	}

	@Test
	public void features_withValidCompanyId_AddsCompanyViewToModelAttributes() throws Exception {
		mockMvc.perform(MockAdminManageCompanyControllerRequest.features(COMPANY))
			.andExpect(model().attribute("companyView", "features"));
	}

	@Test
	public void features_withValidCompanyId_AddsVenuesToModelAttributes() throws Exception {
		mockMvc.perform(MockAdminManageCompanyControllerRequest.features(COMPANY))
			.andExpect(model().attribute("venues", Venue.values()));
	}

	@Test
	public void features_withValidCompanyId_ReturnsTheView() throws Exception {
		mockMvc.perform(MockAdminManageCompanyControllerRequest.features(COMPANY))
			.andExpect(view().name("web/pages/admin/manage/company/features"));
	}

	@Test
	public void addAdmission_withValidCompanyIdAndVenue_IsOK() throws Exception {
		mockMvc.perform(MockAdminManageCompanyControllerRequest.addAdmission(COMPANY, String.valueOf(Venue.LOBBY)))
			.andExpect(status().isOk());
	}

	@Test
	public void addAdmission_withValidCompanyIdAndVenue_SavesAnAdmissionForTheCompanyAndVenue() throws Exception {
		mockMvc.perform(MockAdminManageCompanyControllerRequest.addAdmission(COMPANY, String.valueOf(Venue.LOBBY)));
		verify(admissionService).saveAdmissionForCompanyIdAndVenue(COMPANY, Venue.LOBBY);
	}

	@Test
	public void features_withValidCompanyId_AddsAdmittedVenuesToModelAttributes() throws Exception {
		mockMvc.perform(MockAdminManageCompanyControllerRequest.features(COMPANY))
			.andExpect(model().attribute("admittedVenues", hasItem(Venue.LOBBY)));
	}

	@Test
	public void removeAdmission_withValidCompanyIdAndVenue_IsOK() throws Exception {
		mockMvc.perform(MockAdminManageCompanyControllerRequest.removeAdmission(COMPANY, String.valueOf(Venue.LOBBY)))
			.andExpect(status().isOk());
	}

	@Test
	public void removeAdmission_withValidCompanyIdAndVenue_DestroysTheAdmissionForTheCompanyAndVenue() throws Exception {
		mockMvc.perform(MockAdminManageCompanyControllerRequest.removeAdmission(COMPANY, String.valueOf(Venue.LOBBY)));
		verify(admissionService).destroyAdmissionForCompanyIdAndVenue(COMPANY, Venue.LOBBY);
	}

}

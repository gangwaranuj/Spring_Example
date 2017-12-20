package com.workmarket.service.business.requirementsets;

import com.workmarket.data.solr.repository.UserSearchableFields;
import com.workmarket.domains.model.requirementset.abandon.AbandonRequirement;
import com.workmarket.domains.model.requirementset.agreement.AgreementRequirable;
import com.workmarket.domains.model.requirementset.agreement.AgreementRequirement;
import com.workmarket.domains.model.requirementset.backgroundcheck.BackgroundCheckRequirement;
import com.workmarket.domains.model.requirementset.cancelled.CancelledRequirement;
import com.workmarket.domains.model.requirementset.certification.CertificationRequirable;
import com.workmarket.domains.model.requirementset.certification.CertificationRequirement;
import com.workmarket.domains.model.requirementset.companytype.CompanyTypeRequirable;
import com.workmarket.domains.model.requirementset.companytype.CompanyTypeRequirement;
import com.workmarket.domains.model.requirementset.companywork.CompanyWorkRequirable;
import com.workmarket.domains.model.requirementset.companywork.CompanyWorkRequirement;
import com.workmarket.domains.model.requirementset.country.CountryRequirable;
import com.workmarket.domains.model.requirementset.country.CountryRequirement;
import com.workmarket.domains.model.requirementset.deliverableontime.DeliverableOnTimeRequirement;
import com.workmarket.domains.model.requirementset.drugtest.DrugTestRequirement;
import com.workmarket.domains.model.requirementset.groupmembership.GroupMembershipRequirable;
import com.workmarket.domains.model.requirementset.groupmembership.GroupMembershipRequirement;
import com.workmarket.domains.model.requirementset.industry.IndustryRequirable;
import com.workmarket.domains.model.requirementset.industry.IndustryRequirement;
import com.workmarket.domains.model.requirementset.insurance.InsuranceRequirable;
import com.workmarket.domains.model.requirementset.insurance.InsuranceRequirement;
import com.workmarket.domains.model.requirementset.license.LicenseRequirable;
import com.workmarket.domains.model.requirementset.license.LicenseRequirement;
import com.workmarket.domains.model.requirementset.ontime.OntimeRequirement;
import com.workmarket.domains.model.requirementset.paid.PaidRequirement;
import com.workmarket.domains.model.requirementset.profilepicture.ProfilePictureRequirement;
import com.workmarket.domains.model.requirementset.profilevideo.ProfileVideoRequirement;
import com.workmarket.domains.model.requirementset.rating.RatingRequirement;
import com.workmarket.domains.model.requirementset.resourcetype.ResourceType;
import com.workmarket.domains.model.requirementset.resourcetype.ResourceTypeRequirable;
import com.workmarket.domains.model.requirementset.resourcetype.ResourceTypeRequirement;
import com.workmarket.domains.model.requirementset.test.TestRequirement;
import com.workmarket.domains.model.requirementset.traveldistance.TravelDistanceRequirement;
import org.apache.solr.client.solrj.SolrQuery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class SolrQueryVisitorImplTest {
	private static final Long ID = 1L;
	private static final String COUNTRY_ID = "USA";
	private static final Integer RATING_VALUE = 1;
	private static final Long TRAVEL_DISTANCE = 5L;
	private static final int MAX_ABANDONED = 1;
	private static final int MAX_CANCELLED = 1;
	private static final int MIN_WORK_COUNT = 1;
	private static final int MIN_PERCENTAGE = 50;
	private static final int DELIVERABLE_MIN_PERCENTAGE = 50;

	SolrQueryVisitorImpl visitor = new SolrQueryVisitorImpl();

	SolrQuery query;

	AgreementRequirement agreementRequirement;
	AgreementRequirable agreementRequirable;

	BackgroundCheckRequirement backgroundCheckRequirement;

	CertificationRequirement certificationRequirement;
	CertificationRequirable certificationRequirable;

	CompanyTypeRequirement companyTypeRequirement;
	CompanyTypeRequirable companyTypeRequirable;

	CountryRequirement countryRequirement;
	CountryRequirable countryRequirable;

	DrugTestRequirement drugTestRequirement;

	IndustryRequirement industryRequirement;
	IndustryRequirable industryRequirable;

	InsuranceRequirable insuranceRequirable;
	InsuranceRequirement insuranceRequirement;

	LicenseRequirable licenseRequirable;
	LicenseRequirement licenseRequirement;

	RatingRequirement ratingRequirement;

	ResourceTypeRequirable resourceTypeRequirable;
	ResourceTypeRequirement resourceTypeRequirement;

	TestRequirement testRequirement;

	TravelDistanceRequirement travelDistanceRequirement;

	ProfileVideoRequirement profileVideoRequirement;

	AbandonRequirement abandonRequirement;

	CancelledRequirement cancelledRequirement;

	CompanyWorkRequirement companyWorkRequirement;
	CompanyWorkRequirable companyWorkRequirable;

	GroupMembershipRequirement groupMembershipRequirement;
	GroupMembershipRequirable groupMembershipRequirable;

	OntimeRequirement ontimeRequirement;

	DeliverableOnTimeRequirement deliverableOnTimeRequirement;

	PaidRequirement paidRequirement;

	ProfilePictureRequirement profilePictureRequirement;

	@Before
	public void setup() {
		query = mock(SolrQuery.class);

		agreementRequirement = mock(AgreementRequirement.class);
		agreementRequirable = mock(AgreementRequirable.class);
		when(agreementRequirable.getId()).thenReturn(ID);
		when(agreementRequirement.getAgreementRequirable()).thenReturn(agreementRequirable);

		backgroundCheckRequirement = mock(BackgroundCheckRequirement.class);

		certificationRequirement = mock(CertificationRequirement.class);
		certificationRequirable = mock(CertificationRequirable.class);
		when(certificationRequirable.getId()).thenReturn(ID);
		when(certificationRequirement.getCertificationRequirable()).thenReturn(certificationRequirable);

		companyTypeRequirement = mock(CompanyTypeRequirement.class);
		companyTypeRequirable = mock(CompanyTypeRequirable.class);
		when(companyTypeRequirable.getId()).thenReturn(ID);
		when(companyTypeRequirement.getCompanyTypeRequirable()).thenReturn(companyTypeRequirable);

		countryRequirement = mock(CountryRequirement.class);
		countryRequirable = mock(CountryRequirable.class);
		when(countryRequirable.getId()).thenReturn(COUNTRY_ID);
		when(countryRequirement.getCountryRequirable()).thenReturn(countryRequirable);

		drugTestRequirement = mock(DrugTestRequirement.class);

		industryRequirable = mock(IndustryRequirable.class);
		industryRequirement = mock(IndustryRequirement.class);
		when(industryRequirable.getId()).thenReturn(ID);
		when(industryRequirement.getIndustryRequirable()).thenReturn(industryRequirable);

		insuranceRequirable = mock(InsuranceRequirable.class);
		insuranceRequirement = mock(InsuranceRequirement.class);
		when(insuranceRequirable.getId()).thenReturn(ID);
		when(insuranceRequirement.getInsuranceRequirable()).thenReturn(insuranceRequirable);

		licenseRequirement = mock(LicenseRequirement.class);
		licenseRequirable = mock(LicenseRequirable.class);
		when(licenseRequirable.getId()).thenReturn(ID);
		when(licenseRequirement.getLicenseRequirable()).thenReturn(licenseRequirable);

		ratingRequirement = mock(RatingRequirement.class);
		when(ratingRequirement.getValue()).thenReturn(RATING_VALUE);

		resourceTypeRequirable = mock(ResourceTypeRequirable.class);
		resourceTypeRequirement = mock(ResourceTypeRequirement.class);
		when(resourceTypeRequirement.getResourceTypeRequirable()).thenReturn(resourceTypeRequirable);

		testRequirement = mock(TestRequirement.class);

		travelDistanceRequirement = mock(TravelDistanceRequirement.class);
		when(travelDistanceRequirement.getDistance()).thenReturn(TRAVEL_DISTANCE);

		profileVideoRequirement = mock(ProfileVideoRequirement.class);

		abandonRequirement = mock(AbandonRequirement.class);
		when(abandonRequirement.getMaximumAllowed()).thenReturn(MAX_ABANDONED);

		cancelledRequirement = mock(CancelledRequirement.class);
		when(cancelledRequirement.getMaximumAllowed()).thenReturn(MAX_CANCELLED);

		companyWorkRequirement = mock(CompanyWorkRequirement.class);
		companyWorkRequirable = mock(CompanyWorkRequirable.class);
		when(companyWorkRequirable.getId()).thenReturn(ID);
		when(companyWorkRequirement.getMinimumWorkCount()).thenReturn(MIN_WORK_COUNT);
		when(companyWorkRequirement.getCompanyWorkRequirable()).thenReturn(companyWorkRequirable);

		groupMembershipRequirement = mock(GroupMembershipRequirement.class);
		groupMembershipRequirable = mock(GroupMembershipRequirable.class);
		when(groupMembershipRequirable.getId()).thenReturn(ID);
		when(groupMembershipRequirement.getGroupMembershipRequirable()).thenReturn(groupMembershipRequirable);

		ontimeRequirement = mock(OntimeRequirement.class);
		when(ontimeRequirement.getMinimumPercentage()).thenReturn(MIN_PERCENTAGE);

		deliverableOnTimeRequirement = mock(DeliverableOnTimeRequirement.class);
		when(deliverableOnTimeRequirement.getMinimumPercentage()).thenReturn(DELIVERABLE_MIN_PERCENTAGE);

		paidRequirement = mock(PaidRequirement.class);
		when(paidRequirement.getMinimumAssignments()).thenReturn(MIN_WORK_COUNT);

		profilePictureRequirement = mock(ProfilePictureRequirement.class);
	}

	@Test
	public void visit_withAgreementRequirement_filterQuery() {
		visitor.visit(query, agreementRequirement);
		verify(query).addFilterQuery(UserSearchableFields.CONTRACT_IDS.getName() + ":" + agreementRequirable.getId());
	}

	@Test
	public void visit_withBackgroundRequirement_filterQuery() {
		visitor.visit(query, backgroundCheckRequirement);
		verify(query).addFilterQuery(UserSearchableFields.LAST_BACKGROUND_CHECK_DATE.getName() + ":[* TO *]");
	}

	@Test
	public void visit_withCertificationRequirement_filterQuery() {
		visitor.visit(query, certificationRequirement);
		verify(query).addFilterQuery(UserSearchableFields.CERTIFICATION_IDS.getName() + ":" + certificationRequirable.getId());
	}

	@Test
	public void visit_withCompanyTypeRequirement_filterQuery() {
		visitor.visit(query, companyTypeRequirement);
		verify(query).addFilterQuery(UserSearchableFields.COMPANY_TYPE.getName() + ":" + companyTypeRequirable.getId());
	}

	@Test
	public void visit_withCountryRequirement_filterQuery() {
		visitor.visit(query, countryRequirement);
		verify(query).addFilterQuery(UserSearchableFields.COUNTRY.getName() + ":" + countryRequirable.getId());
	}

	@Test
	public void visit_withDrugTestRequirement_filterQuery() {
		visitor.visit(query, drugTestRequirement);
		verify(query).addFilterQuery(UserSearchableFields.LAST_DRUG_TEST_DATE.getName() + ":[* TO *]");
	}

	@Test
	public void visit_withIndustryRequirement_filterQuery() {
		visitor.visit(query, industryRequirement);
		verify(query).addFilterQuery(UserSearchableFields.INDUSTRIES_ID.getName() + ":" + industryRequirable.getId());
	}

	@Test
	public void visit_withInsuranceRequirement_filterQuery() {
		visitor.visit(query, insuranceRequirement);
		verify(query).addFilterQuery(UserSearchableFields.INSURANCE_IDS.getName() + ":" + insuranceRequirable.getId());
	}

	@Test
	public void visit_withLicenseRequirement_filterQuery() {
		visitor.visit(query, licenseRequirement);
		verify(query).addFilterQuery(UserSearchableFields.LICENSE_IDS.getName() + ":" + licenseRequirable.getId());
	}

	@Test
	public void visit_withRatingRequirement_filterQuery() {
		visitor.visit(query, ratingRequirement);
		verify(query).addFilterQuery(UserSearchableFields.RATING.getName() + ":[" + ratingRequirement.getValue() + " TO *]");
	}

	@Test
	public void visit_withResourceTypeRequirement_filterQueryEmployee() {
		when(resourceTypeRequirable.getId()).thenReturn(ResourceType.EMPLOYEE.getId());
		visitor.visit(query, resourceTypeRequirement);
		verify(query).addFilterQuery(UserSearchableFields.LANE0_COMPANY_IDS.getName() + ":[* TO *] OR " +
				UserSearchableFields.LANE1_COMPANY_IDS.getName() + ":[* TO *]");
	}

	@Test
	public void visit_withResourceTypeRequirement_filterQueryContractro() {
		when(resourceTypeRequirable.getId()).thenReturn(ResourceType.CONTRACTOR.getId());
		visitor.visit(query, resourceTypeRequirement);
		verify(query).addFilterQuery(UserSearchableFields.LANE2_COMPANY_IDS.getName() + ":[* TO *] OR " +
				UserSearchableFields.LANE3_COMPANY_IDS.getName() + ":[* TO *]");
	}

	@Test
	public void visit_withTestRequirement_filterQuery() {
		visitor.visit(query, testRequirement);
		verify(query).addFilterQuery(UserSearchableFields.LAST_DRUG_TEST_DATE.getName() + ":[* TO *]");
	}

	@Test
	public void visit_withProfileVideoRequirement_filterQuery() {
		visitor.visit(query, profileVideoRequirement);
		verify(query).addFilterQuery(UserSearchableFields.HAS_VIDEO.getName() + ":true");
	}

	@Test
	public void visit_withTravelDistanceRequirement_filterQuery() {
		visitor.visit(query, travelDistanceRequirement);
		verify(query).addFilterQuery(UserSearchableFields.MAX_TRAVEL_DISTANCE.getName() + ":[* TO " + travelDistanceRequirement.getDistance() + "]");
	}

	@Test
	public void visit_withAbandonRequirement_filterQuery() {
		visitor.visit(query, abandonRequirement);
		verify(query).addFilterQuery(UserSearchableFields.ABANDONED_COUNT.getName() + ":[* TO " + abandonRequirement.getMaximumAllowed() + "]");
	}

	@Test
	public void visit_withCancelledRequirement_filterQuery() {
		visitor.visit(query, cancelledRequirement);
		verify(query).addFilterQuery(UserSearchableFields.WORK_CANCELLED_COUNT.getName() + ":[* TO " + cancelledRequirement.getMaximumAllowed() + "]");
	}

	@Test
	public void visit_withCompanyWorkRequirement_filterQuery() {
		visitor.visit(query, companyWorkRequirement);
		verify(query).addFilterQuery(UserSearchableFields.PAID_COMPANY_COUNT.getName() + companyWorkRequirement.getCompanyWorkRequirable().getId()
				+ ":[" + companyWorkRequirement.getMinimumWorkCount() + " TO *]");
	}

	@Test
	public void visit_withGroupMembershipRequirement_filterQuery() {
		visitor.visit(query, groupMembershipRequirement);
		verify(query).addFilterQuery(UserSearchableFields.GROUP_IDS + ":" + groupMembershipRequirement.getGroupMembershipRequirable().getId());
	}

	@Test
	public void visit_withOntimeRequirement_filterQuery() {
		visitor.visit(query, ontimeRequirement);
		verify(query).addFilterQuery(UserSearchableFields.ONTIME_PERCENTAGE + ":[" + ontimeRequirement.getMinimumPercentage() + " TO *]");
	}

	@Test
	public void visit_withDeliverableOntimeRequirement_filterQuery() {
		visitor.visit(query, deliverableOnTimeRequirement);
		verify(query).addFilterQuery(UserSearchableFields.DELIVERABLE_ON_TIME_PERCENTAGE + ":[" + deliverableOnTimeRequirement.getMinimumPercentage() + " TO *]");
	}

	@Test
	public void visit_withPaidRequirement_filterQuery() {
		visitor.visit(query, paidRequirement);
		verify(query).addFilterQuery(UserSearchableFields.PAID_ASSIGNMENTS_COUNT + ":[" + paidRequirement.getMinimumAssignments() + " TO *]");
	}

	@Test
	public void visit_withProfilePictureRequirement_filterQuery() {
		visitor.visit(query, profilePictureRequirement);
		query.addFilterQuery(UserSearchableFields.HAS_AVATAR + ":true");
	}
}

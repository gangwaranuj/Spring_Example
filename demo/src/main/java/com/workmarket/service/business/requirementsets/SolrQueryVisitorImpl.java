package com.workmarket.service.business.requirementsets;

import com.workmarket.data.solr.repository.UserSearchableFields;
import com.workmarket.domains.model.requirementset.SolrQueryVisitor;
import com.workmarket.domains.model.requirementset.abandon.AbandonRequirement;
import com.workmarket.domains.model.requirementset.agreement.AgreementRequirable;
import com.workmarket.domains.model.requirementset.agreement.AgreementRequirement;
import com.workmarket.domains.model.requirementset.backgroundcheck.BackgroundCheckRequirement;
import com.workmarket.domains.model.requirementset.cancelled.CancelledRequirement;
import com.workmarket.domains.model.requirementset.certification.CertificationRequirable;
import com.workmarket.domains.model.requirementset.certification.CertificationRequirement;
import com.workmarket.domains.model.requirementset.companytype.CompanyTypeRequirable;
import com.workmarket.domains.model.requirementset.companytype.CompanyTypeRequirement;
import com.workmarket.domains.model.requirementset.companywork.CompanyWorkRequirement;
import com.workmarket.domains.model.requirementset.country.CountryRequirable;
import com.workmarket.domains.model.requirementset.country.CountryRequirement;
import com.workmarket.domains.model.requirementset.deliverableontime.DeliverableOnTimeRequirement;
import com.workmarket.domains.model.requirementset.document.DocumentRequirement;
import com.workmarket.domains.model.requirementset.drugtest.DrugTestRequirement;
import com.workmarket.domains.model.requirementset.esignature.EsignatureRequirement;
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
import org.springframework.stereotype.Component;

@Component
public class SolrQueryVisitorImpl implements SolrQueryVisitor {
	@Override
	public void visit(SolrQuery query, AgreementRequirement requirement) {
		AgreementRequirable requirable = requirement.getAgreementRequirable();

		query.addFilterQuery(UserSearchableFields.CONTRACT_IDS.getName() + ":" + requirable.getId());
	}

	@Override
	public void visit(SolrQuery query, BackgroundCheckRequirement requirement) {
		query.addFilterQuery(UserSearchableFields.LAST_BACKGROUND_CHECK_DATE.getName() + ":[* TO *]");
	}

	@Override
	public void visit(SolrQuery query, CertificationRequirement requirement) {
		CertificationRequirable requirable = requirement.getCertificationRequirable();

		query.addFilterQuery(UserSearchableFields.CERTIFICATION_IDS.getName() + ":" + requirable.getId());
	}

	@Override
	public void visit(SolrQuery query, CompanyTypeRequirement requirement) {
		CompanyTypeRequirable requirable = requirement.getCompanyTypeRequirable();

		query.addFilterQuery(UserSearchableFields.COMPANY_TYPE.getName() + ":" + requirable.getId());
	}

	@Override
	public void visit(SolrQuery query, CountryRequirement requirement) {
		CountryRequirable requirable = requirement.getCountryRequirable();

		query.addFilterQuery(UserSearchableFields.COUNTRY.getName() + ":" + requirable.getId());
	}

	@Override
	public void visit(SolrQuery query, DrugTestRequirement requirement) {
		query.addFilterQuery(UserSearchableFields.LAST_DRUG_TEST_DATE.getName() + ":[* TO *]");
	}

	@Override
	public void visit(SolrQuery query, IndustryRequirement requirement) {
		IndustryRequirable requirable = requirement.getIndustryRequirable();

		query.addFilterQuery(UserSearchableFields.INDUSTRIES_ID.getName() + ":" + requirable.getId());
	}

	@Override
	public void visit(SolrQuery query, InsuranceRequirement requirement) {
		InsuranceRequirable requirable = requirement.getInsuranceRequirable();

		query.addFilterQuery(UserSearchableFields.INSURANCE_IDS.getName() + ":" + requirable.getId());
	}

	@Override
	public void visit(SolrQuery query, LicenseRequirement requirement) {
		LicenseRequirable requirable = requirement.getLicenseRequirable();

		query.addFilterQuery(UserSearchableFields.LICENSE_IDS.getName() + ":" + requirable.getId());
	}

	@Override
	public void visit(SolrQuery query, RatingRequirement requirement) {
		query.addFilterQuery(UserSearchableFields.RATING.getName() + ":[" + requirement.getValue() + " TO *]");
	}

	@Override
	public void visit(SolrQuery query, ResourceTypeRequirement requirement) {
		ResourceTypeRequirable requirable = requirement.getResourceTypeRequirable();
		ResourceType requiredResourceType = ResourceType.getById(requirable.getId());

		switch (requiredResourceType) {
			case EMPLOYEE:
				query.addFilterQuery(UserSearchableFields.LANE0_COMPANY_IDS.getName() + ":[* TO *] OR " +
						UserSearchableFields.LANE1_COMPANY_IDS.getName() + ":[* TO *]");
				break;
			case CONTRACTOR:
				query.addFilterQuery(UserSearchableFields.LANE2_COMPANY_IDS.getName() + ":[* TO *] OR " +
						UserSearchableFields.LANE3_COMPANY_IDS.getName() + ":[* TO *]");
				break;
		}
	}

	@Override
	public void visit(SolrQuery query, TestRequirement requirement) {
		query.addFilterQuery(UserSearchableFields.LAST_DRUG_TEST_DATE.getName() + ":[* TO *]");
	}

	@Override
	public void visit(SolrQuery query, TravelDistanceRequirement requirement) {
		query.addFilterQuery(UserSearchableFields.MAX_TRAVEL_DISTANCE.getName() + ":[* TO " + requirement.getDistance() + "]");
	}

	@Override
	public void visit(SolrQuery query, ProfileVideoRequirement requirement) {
		query.addFilterQuery(UserSearchableFields.HAS_VIDEO.getName() + ":true");
	}

	@Override
	public void visit(SolrQuery query, DocumentRequirement documentRequirement) {
		// TODO[Jim]: Implementation pending - noop is ok for now
	}

	@Override
	public void visit(final SolrQuery query, final EsignatureRequirement esignatureRequirement) {

	}

	@Override
	public void visit(SolrQuery query, AbandonRequirement requirement) {
		query.addFilterQuery(UserSearchableFields.ABANDONED_COUNT.getName() + ":[* TO " + requirement.getMaximumAllowed() + "]");
	}

	@Override
	public void visit(SolrQuery query, CancelledRequirement requirement) {
		query.addFilterQuery(UserSearchableFields.WORK_CANCELLED_COUNT.getName() + ":[* TO " + requirement.getMaximumAllowed() + "]");
	}

	@Override
	public void visit(SolrQuery query, CompanyWorkRequirement requirement) {
		// This field search uses dynamic fields
		query.addFilterQuery(UserSearchableFields.PAID_COMPANY_COUNT.getName() + requirement.getCompanyWorkRequirable().getId()
				+ ":[" + requirement.getMinimumWorkCount() + " TO *]");
	}

	@Override
	public void visit(SolrQuery query, GroupMembershipRequirement requirement) {
		query.addFilterQuery(UserSearchableFields.GROUP_IDS + ":" + requirement.getGroupMembershipRequirable().getId());
	}

	@Override
	public void visit(SolrQuery query, OntimeRequirement requirement) {
		query.addFilterQuery(UserSearchableFields.ONTIME_PERCENTAGE + ":[" + requirement.getMinimumPercentage() + " TO *]");
	}

	@Override
	public void visit(SolrQuery query, DeliverableOnTimeRequirement requirement) {
		query.addFilterQuery(UserSearchableFields.DELIVERABLE_ON_TIME_PERCENTAGE + ":[" + requirement.getMinimumPercentage() + " TO *]");
	}

	@Override
	public void visit(SolrQuery query, PaidRequirement requirement) {
		query.addFilterQuery(UserSearchableFields.PAID_ASSIGNMENTS_COUNT + ":[" + requirement.getMinimumAssignments() + " TO *]");
	}

	@Override
	public void visit(SolrQuery query, ProfilePictureRequirement requirement) {
		query.addFilterQuery(UserSearchableFields.HAS_AVATAR + ":true");
	}
}

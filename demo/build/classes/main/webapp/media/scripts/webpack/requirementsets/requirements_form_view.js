'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import GooglePlaces from '../funcs/googlePlaces';
import AbandonTypeView from './abandon_requirement_form_view';
import AvailabilityTypeView from './availability_requirement_form_view';
import WeekdaysCollection from './weekdays_collection';
import BackgroundCheckTypeView from './background_check_requirement_form_view';
import CancelledRequirementTypeView from './cancelled_requirement_form_view';
import CertificationsCollection from './certifications_collection';
import CertificationTypeView from './certification_requirement_form_view';
import DeliverableTypeView from './deliverable_ontime_requirement_form_view';
import DocumentsCollection from './documents_collection'
import DocumentFormView from './document_requirement_form_view';
import DrugTestFormView from './drug_test_requirement_form_view';
import InsurancesCollection from './insurances_collection';
import InsuranceFormView from './insurance_requirement_form_view';
import LicensesCollection from './licenses_collection';
import LicenseFormView from './license_requirement_form_view';
import OntimeFormView from './ontime_requirement_form_view';
import ProfilePictureFormView from './profile_picture_requirement_form_view';
import ProfileVideoFormView from './profile_video_requirement_form_view';
import PaidFormView from './paid_requirement_form_view';
import RatingFormView from './rating_requirement_form_view';
import ResourceTypeView from './resource_type_requirement_form_view';
import ResourceTypesCollection from './resourcetypes_collection';
import AgreementsTypeView from './agreement_requirement_form_view';
import AgreementsCollection from './agreements_collection';
import CompanyTypesCollection from './companytypes_collection';
import CompanyTypeView from './company_type_requirement_form_view';
import CountriesCollection from './countries_collection';
import CountriesTypeView from './country_requirement_form_view';
import IndustriesCollection from './industries_collection';
import IndustryFormView from './industry_requirement_form_view';
import StatesCollection from './states_collection';
import TestsCollection from './tests_collection';
import TestFormView from './test_requirement_form_view';
import TravelDistanceFormView from './travel_distance_requirement_form_view';
import GroupMembershipsCollection from './groupmemberships_collection';
import GroupMembershipTypeView from './group_membership_requirement_form_view';
import CompanyWorksCollection from './companyworks_collection';
import CompanyWorkFormView from './company_work_requirement_form_view';
import Template from './templates/requirements-form.hbs';

export default Backbone.View.extend({
	className: 'well-b2',
	template: Template,

	events: {
		'change [data-toggle="form"]' : 'changeRequirementTypeForm'
	},

	initialize: function (options) {
		this.requirementSet = options.requirementSet;
		this.requirementTypes = options.requirementTypes;
		this.isMandatoryRequirement = options.isMandatoryRequirement;
		this.filter = options.filter;
		this.agreementsCollection = new AgreementsCollection();
		this.weekdaysCollection = new WeekdaysCollection();
		this.companyTypesCollection = new CompanyTypesCollection();
		this.countriesCollection = new CountriesCollection();
		this.certificationsCollection = new CertificationsCollection();
		this.documentsCollection = new DocumentsCollection();
		this.industriesCollection = new IndustriesCollection();
		this.insurancesCollection = new InsurancesCollection();
		this.licensesCollection = new LicensesCollection();
		this.resourceTypesCollection = new ResourceTypesCollection();
		this.statesCollection = new StatesCollection();
		this.testsCollection = new TestsCollection();
		this.groupMembershipsCollection = new GroupMembershipsCollection();
		this.companyWorksCollection = new CompanyWorksCollection();
		this.weekdaysCollection = new WeekdaysCollection();
		this.render();
	},

	toggleAddButton: function (obj) {
		$('[data-action="add"]').prop('disabled', $('#distance').val() === '');
	},

	render: function () {
		this.$el.html(this.template({
			requirementTypes: this.requirementTypes.filteredFor(this.filter)
		}));
		return this;
	},

	changeRequirementTypeForm: function (e) {
		var changer = this.$(e.currentTarget);
		var placeholder = this.$el.find('[data-placeholder="form"]');
		placeholder.empty();
		var formType = changer.val();
		var formLabel = changer.find('option:selected').text();
		var formCollection,
			formView;

		switch (formType) {
			case 'AbandonRequirement':
				formView = AbandonTypeView;
				break;
			case 'AgreementRequirement':
				formCollection = this.agreementsCollection;
				formView = AgreementsTypeView;
				break;
			case 'AvailabilityRequirement':
				formCollection = this.weekdaysCollection;
				formView = AvailabilityTypeView;
				break;
			case 'BackgroundCheckRequirement':
				formView = BackgroundCheckTypeView;
				break;
			case 'CancelledRequirement':
				formView = CancelledRequirementTypeView;
				break;
			case 'CertificationRequirement':
				formCollection = this.certificationsCollection;
				formView = CertificationTypeView;
				break;
			case 'CompanyTypeRequirement':
				formCollection = this.companyTypesCollection;
				formView = CompanyTypeView;
				break;
			case 'CompanyWorkRequirement':
				formCollection = this.companyWorksCollection;
				formView = CompanyWorkFormView;
				break;
			case 'CountryRequirement':
				formCollection = this.countriesCollection;
				formView = CountriesTypeView;
				break;
			case 'DeliverableOnTimeRequirement':
				formView = DeliverableTypeView;
				break;
			case 'DocumentRequirement':
				formCollection = this.documentsCollection;
				formView = DocumentFormView;
				break;
			case 'DrugTestRequirement':
				formView = DrugTestFormView;
				break;
			case 'GroupMembershipRequirement':
				formCollection = this.groupMembershipsCollection;
				formView = GroupMembershipTypeView;
				break;
			case 'IndustryRequirement':
				formCollection = this.industriesCollection;
				formView = IndustryFormView;
				break;
			case 'InsuranceRequirement':
				formCollection = this.insurancesCollection;
				formView = InsuranceFormView;
				break;
			case 'LicenseRequirement':
				formCollection = this.licensesCollection;
				formView = LicenseFormView;
				break;
			case 'OntimeRequirement':
				formView = OntimeFormView;
				break;
			case 'PaidRequirement':
				formView = PaidFormView;
				break;
			case 'ProfilePictureRequirement':
				formView = ProfilePictureFormView;
				break;
			case 'ProfileVideoRequirement':
				formView = ProfileVideoFormView;
				break;
			case 'RatingRequirement':
				formView = RatingFormView;
				break;
			case 'TestRequirement':
				formCollection = this.testsCollection;
				formView = TestFormView;
				break;
			case 'TravelDistanceRequirement':
				formView = TravelDistanceFormView;
				break;
			case 'ResourceTypeRequirement':
				formCollection = this.resourceTypesCollection;
				formView = ResourceTypeView;
				break;
		}

		if (typeof formCollection != 'undefined') {
			formCollection.fetch().then(_.bind(function() {
				var form = new formView({
					formType: formType,
					formLabel: formLabel,
					requirementTypes: this.requirementTypes,
					requirementSet: this.requirementSet,
					collection: formCollection,
					isMandatoryRequirement: this.isMandatoryRequirement
				});
				placeholder.html(form.el);
				changer.find('option.prompt').remove();
			}, this));
		} else {
			var form = new formView({
				formType: formType,
				formLabel: formLabel,
				requirementTypes: this.requirementTypes,
				requirementSet: this.requirementSet,
				isMandatoryRequirement: this.isMandatoryRequirement
			});
			placeholder.html(form.el);
			if (formType === 'TravelDistanceRequirement') {
				GooglePlaces('travel_address', this.toggleAddButton);
			}
			changer.find('option.prompt').remove();
		}
	}

});


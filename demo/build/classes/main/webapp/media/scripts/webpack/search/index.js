/* eslint-disable no-new */
import Application from '../core';
import CartView from './cart_view';
import FacetsView from './facets_view';
import GroupsView from '../groups/details_view';
import Helpers from '../groups/helpers';
import AssignmentSearchPage from './assignments_search';
import ajaxSendInit from '../funcs/ajaxSendInit';

Application.init({ name: 'search', features: config }, () => {});

ajaxSendInit();

const loadAssignmentCreationModal = async() => {
	const module = await import(/* webpackChunkName: "newAssignmentCreation" */ '../assignments/creation_modal');
	return module.default;
};

if (config.mode === 'assignments') {
	new AssignmentSearchPage(config.defaultSearch);
} else {
	const editLink = document.getElementById('contact-edit-assignment');
	if (editLink) {
		const assignmentId = editLink.getAttribute('data-assignmentId');
		editLink.addEventListener('click', (e) => {
			e.preventDefault();
			loadAssignmentCreationModal().then(CreationModal => new CreationModal({ assignmentId, title: 'Edit Assignment' }));
		});
	}

	if (config.mode === 'group-detail') {
		new GroupsView({
			model: {
				id: config.group_id,
				is_admin: config.isGroupAdmin,
				requires_agreements: config.missingContractVersions
			},

			clientIdToBlock: config.clientIdToBlock,
			companyBlocked: config.companyBlocked,
			isGroupCompanyViewer: config.isGroupCompanyViewer,
			blockedClientTooltip: 'You have blocked this company. Unblock to apply to this group.'
		});
		Helpers();
	}

	if (config.mode !== 'group-detail') {
		new FacetsView({
			existingWorkers: config.existingWorkers,
			declinedWorkers: config.declinedWorkers,
			appliedWorkers: config.appliedWorkers,
			existingVendorNumbers: config.existingVendorNumbers,
			declinedVendorNumbers: config.declinedVendorNumbers,
			mode: config.mode,
			pricing_type: config.pricing_type,
			work_number: config.work_number,
			addressFilter: config.addressFilter,
			groupId: config.group_id,
			isSurvey: config.isSurvey,
			isGroupAdmin: config.isGroupAdmin,
			filterOrder: config.filterOrder,
			searchKeywords: config.searchKeywords,
			isInstantWorkerPool: config.isInstantWorkerPool,
			assignToFirstWorker: config.assignToFirstWorker,
			isDispatch: config.isDispatch,
			industries: {
				id: config.mode === 'assignment' ? config.industries.id : {},
				filter_on: config.mode === 'assignment' ? config.industries.filter_on : {}
			},
			labels: config.mode === 'assignment' ? {
				industries: {
					id: config.labels.industries.id,
					name: config.labels.industries.name
				}
			} : {},
			searchType: config.searchType
		});

		new CartView({
			model: '',
			isSurvey: config.isSurvey,
			workNumber: config.work_number,
			mode: config.mode,
			testId: config.assessment_id,
			groupId: config.group_id,
			companyName: config.companyName,
			currentUserCompanyName: config.currentUserCompanyName,
			paymentTime: config.paymentTime,
			pricingType: config.pricingType,
			disablePriceNegotiation: config.disablePriceNegotiation,
			work: config.work,
			isBundle: config.isBundle,
			isDispatch: config.isDispatch,
			searchType: config.searchType,
			hasVendorPoolsFeature: config.hasVendorPoolsFeature,
			hasMarketplace: config.hasMarketplace
		});
	} else if (config.isGroupAdmin) {
		new FacetsView({
			existingWorkers: config.existingWorkers,
			declinedWorkers: config.declinedWorkers,
			appliedWorkers: config.appliedWorkers,
			mode: config.mode,
			pricing_type: config.pricing_type,
			work_number: config.work_number,
			groupId: config.group_id,
			isSurvey: config.isSurvey,
			auto_generated: config.auto_generated,
			isGroupAdmin: config.isGroupAdmin,
			filterOrder: config.filterOrder,
			industries: {
				id: config.mode === 'assignment' ? config.industries.id : {},
				filter_on: config.mode === 'assignment' ? config.industries.filter_on : {}
			},
			labels:
				config.mode === 'assignment' ? {
					industries: {
						id: config.labels.industries.id,
						name: config.labels.industries.name
					}
				} : {}
		});

		new CartView({
			model: '',
			isSurvey: config.isSurvey,
			auto_generated: config.auto_generated,
			export_csv: config.export_csv,
			workNumber: config.work_number,
			mode: config.mode,
			testId: config.assessment_id,
			groupId: config.group_id,
			searchType: config.searchType,
			hasVendorPoolsFeature: config.hasVendorPoolsFeature,
			hasMarketplace: config.hasMarketplace
		});
	}
}

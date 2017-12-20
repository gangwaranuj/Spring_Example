import Handlebars from 'handlebars';
import React from 'react';
import { mount } from 'enzyme';
import { WMTable, WMTableRow, WMRaisedButton } from '@workmarket/front-end-components';
import TalentPoolMembers from '../template';
import { initialState } from '../../../reducers/index';
import buttonTemplate from '../../../../funcs/templates/button.hbs';

const mergedState = initialState.mergeDeep({
	rootData: {
		inviting: false,
		openMembership: 'true',
		isActive: true,
		allInvitedOrApplied: [{
			email: 'qa+r@workmarket.com',
			first_name: 'Testing',
			last_name: 'Worker'
		}],
		members: [
			{
				position: 1,
				score: 395.8912,
				id: 20866,
				userNumber: '6800441',
				first_name: 'Zia',
				last_name: 'Khan',
				email: 'qa+6800441@workmarket.com',
				avatar_asset_uri: null,
				job_title: null,
				company_name: 'CoName_17629',
				city: 'Lawrenceville',
				state: 'GA',
				postal_code: '30046',
				country: 'United States',
				latitude: 40.740256,
				longitude: -73.992391,
				lane: 3,
				laneString: 'Third Party',
				lane2_approval_status: null,
				recruiting_campaign_id: null,
				recruiting_campaign_name: null,
				rating: 0,
				rating_text: 'zero',
				rating_count: 0,
				company_rating: 0,
				company_rating_text: 'zero',
				company_rating_count: 0,
				background_check: true,
				background_check_failed: false,
				background_check_date: '02/14/2012',
				last_assigned_work_date: '',
				drug_test: true,
				drug_test_failed: false,
				drug_test_date: '02/14/2012',
				ontime_reliability: 0,
				deliverable_on_time_reliability: 0,
				blocked: false,
				certifications: null,
				insurances: null,
				licenses: null,
				company_assessments: null,
				company_tags: null,
				groups: [
					'1807',
					'1630',
					'3434',
					'Background Checked Resources'
				],
				instant_worker_pool: true,
				distance: '',
				work_completed_count: 0,
				abandoned_count: 0,
				work_completed_company_count: null,
				work_cancelled_count: 0,
				created_on: '02/12/2012',
				derivedStatus: 'MEMBER',
				assessmentStatus: null,
				internal: false,
				mbo: false,
				mbo_status: null
			}
		]
	}
});

describe('<TalentPoolMembers />', () => {
	const renderComponent = (
		state = mergedState,
		getMembers = () => {},
		switchToInvite = () => {},
		handleInvite = () => {},
		handleInviteParticipants = () => {},
		handleSelect = () => {},
		handleSelectAll = () => {},
		handleOpenProfileModal = () => {},
		handleOpenParticipantProfileModal = () => {},
		handleEditDetails = () => {},
		handleMemberActionMenuOpen = () => {},
		handleRemoveDecline = () => {},
		handleRemoveDeclineParticipants = () => {},
		handleUninvite = () => {},
		handleUninviteParticipants = () => {},
		handleRefresh = () => {},
		handleSearchFilterUpdate = () => {},
		handleApprove = () => {},
		handleApproveParticipants = () => {},
		handleDownloadDocumentation = () => {},
		handleBulkMenuOpen = () => {},
		handleMemberPagination = () => {}
	) => mount(
		<TalentPoolMembers
			talentPoolData={ state.get('rootData') }
			getMembers={ getMembers }
			switchToInvite={ switchToInvite }
			handleInvite={ handleInvite }
			handleInviteParticipants={ handleInviteParticipants }
			handleSelect={ handleSelect }
			handleSelectAll={ handleSelectAll }
			handleOpenProfileModal={ handleOpenProfileModal }
			handleOpenParticipantProfileModal={ handleOpenParticipantProfileModal }
			handleEditDetails={ handleEditDetails }
			handleMemberActionMenuOpen={ handleMemberActionMenuOpen }
			handleRemoveDecline={ handleRemoveDecline }
			handleRemoveDeclineParticipants={ handleRemoveDeclineParticipants }
			handleUninvite={ handleUninvite }
			handleUninviteParticipants={ handleUninviteParticipants }
			handleRefresh={ handleRefresh }
			handleSearchFilterUpdate={ handleSearchFilterUpdate }
			handleApprove={ handleApprove }
			handleApproveParticipants={ handleApproveParticipants }
			handleDownloadDocumentation={ handleDownloadDocumentation }
			handleBulkMenuOpen={ handleBulkMenuOpen }
			handleMemberPagination={ handleMemberPagination }
  />, { attachTo: document.body.appendChild(document.createElement('div')) }
	);

	let wrapper;

	beforeEach(() => {
		Handlebars.registerPartial('button', buttonTemplate);
		wrapper = renderComponent();
	});

	afterEach(() => {
		document.body.innerHTML = '';
	});

	describe('Rendering', () => {

		xit('should have a table', () => {
			const component = wrapper.find(WMTable);
			expect(component).toHaveLength(1);
		});
		xit('should have a filter UI bucket', () => {
			const component = wrapper.find('.search-filter-bucket');
			expect(component).toHaveLength(1);
		});
		xit('should have a filter UI instance', () => {
			expect(document.querySelectorAll('.wm-search-filter')).toHaveLength(1);
		});
		xit('should have a TableRow for each member', () => {
			const component = wrapper.find(WMTableRow);
			expect(component).toHaveLength(1);
			const emptyState = wrapper.find('#emptyMembers');
			expect(emptyState).toHaveLength(0);
		});
		xit('should show an empty state', () => {
			wrapper = renderComponent(initialState);
			const component = wrapper.find('#emptyMembers');
			expect(component).toHaveLength(1);
		});
		xit('should show activate flow', () => {
			wrapper = renderComponent(initialState.setIn(['rootData', 'isActive'], false));
			const component = wrapper.find(WMRaisedButton);
			expect(component.get(0).props.label).toEqual('Edit Details');
		});
		xit('should show invite flow', () => {
			wrapper = renderComponent(mergedState.setIn(['rootData', 'inviting'], true));
			const component = wrapper.find(WMRaisedButton);
			expect(component.get(0).props.label).toEqual('Send Invitations');
		});
		xit('should show add flow', () => {
			wrapper = renderComponent(mergedState.setIn(['rootData', 'inviting'], true).setIn(['rootData', 'openMembership'], 'false'));
			const component = wrapper.find(WMRaisedButton);
			expect(component.get(0).props.label).toEqual('Add Members');
		});
	});
});

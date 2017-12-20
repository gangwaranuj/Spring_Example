import * as types from '../constants/actionTypes';
import * as requirementTypes from '../constants/requirementTypes';
import * as actions from '../actions';
import Application from '../../core';

const serverEvents = (store) => {
	return next => (action) => {
		const rootData = store.getState().rootData;
		const inviting = rootData.get('inviting');
		switch (action.type) {
		case types.MANAGE_GROUP:
			if (rootData.get('activeTab') === 'members') {
				Application.Events.trigger('talentpools:resetSearchFilter');
				const page = rootData.get('memberResultPage');
				const size = rootData.get('memberResultPageSize');
				store.dispatch(actions.getMembers(action.group.id, [], inviting, page, size));
			} else if (rootData.get('activeTab') === 'messages') {
				store.dispatch(actions.fetchMessages(action.group.id));
			}
			return next(action);
		case types.CHANGE_TAB:
			if (action.tab === 'members') {
				if (rootData.get('activeTab') !== 'members') {
					const page = rootData.get('memberResultPage');
					const size = rootData.get('memberResultPageSize');
					store.dispatch(actions.getMembers(action.groupId, [], inviting, page, size));
				}
			} else if (action.tab === 'messages') {
				if (rootData.get('activeTab') !== 'messages') {
					store.dispatch(actions.fetchMessages(action.groupId));
				}
			}
			return next(action);
		case types.CHANGE_FIELD:
			if (action.name === 'orgUnitUuids') {
				store.dispatch(actions.fetchOrgUnitMembers(action.value));
			}
			return next(action);
		case types.REMOVE_ORG_UNIT:
			const orgUnitUuids = store.getState().formData.get('orgUnitUuids');
			const filtered = orgUnitUuids.filter(uuid => uuid !== action.value);
			store.dispatch(actions.fetchOrgUnitMembers(filtered));
			return next(action);
		case types.SET_ACTIVE_REQUIREMENT_TYPE: {
			if (action.requirementType === requirementTypes.COUNTRY.componentName) {
				store.dispatch(actions.fetchCountries());
				return next(action);
			} else if (action.requirementType === requirementTypes.COMPANY_TYPE.componentName) {
				store.dispatch(actions.fetchCompanyTypes());
				return next(action);
			} else if (action.requirementType === requirementTypes.DOCUMENT.componentName) {
				store.dispatch(actions.fetchDocuments());
				return next(action);
			} else if (action.requirementType === requirementTypes.ESIGNATURES.componentName) {
				store.dispatch(actions.fetchSignatures());
				return next(action);
			} else if (action.requirementType === requirementTypes.AGREEMENT.componentName) {
				store.dispatch(actions.fetchAgreements());
				return next(action);
			} else if (action.requirementType === requirementTypes.TALENT_POOL_MEMBERSHIPS.componentName) { // eslint-disable-line max-len
				store.dispatch(actions.fetchTalentPoolMemberships());
				return next(action);
			}
			return next(action);
		}
		case types.SUBMIT_MESSAGE_FORM_SUCCESS:
			store.dispatch(actions.fetchMessages(action.groupId));
			return next(action);
		case types.SUBMIT_TALENT_POOL_FORM_SUCCESS:
			store.dispatch(actions.fetchTalentPools());
			return next(action);
		case types.NEW_TALENT_POOL:
			store.dispatch(actions.fetchSuggestedSkills(''));
			if (Application.Features.hasOrgStructures) {
				const formData = store.getState().formData;
				const orgMode = formData.get('orgMode');
				if (orgMode) {
					store.dispatch(actions.fetchOrgUnits(orgMode.get('uuid')));
					store.dispatch(actions.fetchOrgUnitMembers([orgMode.get('uuid')]));
				}
			}
			return next(action);
		case types.LOAD_EXISTING_GROUP:
			if (Application.Features.hasOrgStructures) {
				const formData = store.getState().formData;
				const orgMode = formData.get('orgMode');
				if (orgMode) {
					store.dispatch(actions.fetchOrgUnits(orgMode.get('uuid')));
				}

				const orgUnits = action.group.org_units;
				if (orgUnits) {
					const orgUnitsUuids = orgUnits.map(orgUnit => orgUnit.uuid);
					store.dispatch(actions.fetchOrgUnitMembers(orgUnitsUuids));
				}
			}
			return next(action);
		case types.SUBMIT_TALENT_POOL_FORM_CREATE_SUCCESS:
			store.dispatch(actions.fetchTalentPools());
			store.dispatch(actions.changeTab('details', action.groupId));
			return next(action);
		case types.DELETE_TALENT_POOL_SUCCESS:
			store.dispatch(actions.fetchTalentPools());
			return next(action);
		case types.ACTIVATE_AUTO_ENFORCEMENT_SUCCESS:
			store.dispatch(actions.fetchTalentPools());
			return next(action);
		case types.SHOW_INVITE_FLOW: {
			const size = store.getState().rootData.get('memberResultPageSize');
			store.dispatch(actions.getMembers(action.groupId, [], action.inviting, 1, size));
			return next(action);
		}
		case types.INVITE_MEMBERS_SUCCESS: {
			Application.Events.trigger('talentpools:resetSearchFilter');
			const size = store.getState().rootData.get('memberResultPageSize');
			store.dispatch(actions.getMembers(action.id, [], false, 1, size));
			return next(action);
		}
		case types.SEARCH_FILTER_UPDATE: {
			const id = store.getState().rootData.get('id');
			const page = store.getState().rootData.get('memberResultPage');
			const size = store.getState().rootData.get('memberResultPageSize');
			store.dispatch(actions.getMembers(id, action.filterObject, inviting, page, size));
			return next(action);
		}
		case types.MEMBER_PAGINATION_CHANGED: {
			const id = store.getState().rootData.get('id');
			const filters = store.getState().rootData.get('activeFilters').toJS();
			const page = action.page;
			const size = store.getState().rootData.get('memberResultPageSize');
			store.dispatch(actions.getMembers(id, filters, inviting, page, size));
			return next(action);
		}
		case types.REFRESH_MEMBERS: {
			const id = store.getState().rootData.get('id');
			const filters = store.getState().rootData.get('activeFilters').toJS();
			const page = store.getState().rootData.get('memberResultPage');
			const size = store.getState().rootData.get('memberResultPageSize');
			store.dispatch(actions.getMembers(id, filters, inviting, page, size));
			return next(action);
		}
		case types.APPROVE_SUCCESS: {
			const id = store.getState().rootData.get('id');
			const filters = store.getState().rootData.get('activeFilters').toJS();
			const page = store.getState().rootData.get('memberResultPage');
			const size = store.getState().rootData.get('memberResultPageSize');
			store.dispatch(actions.getMembers(id, filters, false, page, size));
			return next(action);
		}
		default:
			return next(action);
		}
	};
};

export default serverEvents;

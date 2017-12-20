import { browserHistory } from 'react-router';
import fetch from 'isomorphic-fetch';
import React from 'react';
import ReactDOM, { render } from 'react-dom';
import * as interactionModes from '../constants/interactionModes';
import wmAlert from '../../funcs/wmAlert';
import * as types from '../constants/actionTypes';
import getCSRFToken from '../../funcs/getCSRFToken';
import wmModal from '../../funcs/wmModal';
import Template from '../../search/templates/user_profile_modal_container.hbs';

const loadWMCompanyProfileSPA = async() => {
	const module = await import(/* webpackChunkName: "WMCompanyProfileSPA" */ 'wm-company-profile-spa');
	return module.default;
};
const loadUserProfile = async() => {
	const UserProfile = await import(/* webpackChunkName: "UserProfile" */ '../../profile/profile_model');
	const UserProfileModal = await import(/* webpackChunkName: "UserProfile" */ '../../search/user_profile_modal_view');
	return {
		UserProfile: UserProfile.default,
		UserProfileModal: UserProfileModal.default
	};
};

const makeActionCreator = (type, ...argNames) => (...args) => {
	const action = { type };

	argNames.forEach((arg, index) => {
		action[argNames[index]] = args[index];
	});

	return action;
};

const buildQueryString = (queryStringParamData) => {
	return queryStringParamData.map((paramData) => {
		const name = Object.keys(paramData)[0];
		const value = paramData[name];
		return `${encodeURIComponent(name)}=${encodeURIComponent(value)}`;
	}).join('&');
};

const prepareTalentPoolData = (talentPoolData) => {
	const formData = new FormData();
	Object.keys(talentPoolData).forEach((key) => {
		if (key === 'skills') {
			const skillsInFormData = talentPoolData[key].map(skill => skill.id);
			formData.append('skillIds', skillsInFormData.toJS());
		} else if (key === 'orgUnitUuids') {
			formData.append('orgUnitUuids', talentPoolData[key].toJS());
		} else {
			formData.append(key, talentPoolData[key]);
		}
	});
	return formData;
};

export const changeField = makeActionCreator(types.CHANGE_FIELD, 'name', 'value');
export const onChangeSkillsField = makeActionCreator(types.ADD_SKILLS, 'value');
export const onRemoveSkillsField = makeActionCreator(types.REMOVE_SKILLS, 'value');
export const changeMessageField = makeActionCreator(types.CHANGE_MESSAGE_FIELD, 'name', 'value');
export const changeRequirementField = makeActionCreator(types.CHANGE_REQUIREMENT_FIELD, 'name', 'value');
export const changeDeepRequirementField = makeActionCreator(types.CHANGE_DEEP_REQUIREMENT_FIELD, 'name', 'fieldName', 'value');
export const clearToggledRequirements = makeActionCreator(types.CLEAR_TOGGLED_REQUIREMENTS);
export const changeLocationField = makeActionCreator(types.CHANGE_LOCATION_FIELD, 'name', 'fieldName', 'value');
export const closeDrawer = makeActionCreator(types.CLOSE_DRAWER);
export const fetchMessagesRequest = makeActionCreator(types.FETCH_MESSAGES, 'isRequesting');
export const fetchMessagesSuccess = makeActionCreator(types.FETCH_MESSAGES_SUCCESS, 'groupId', 'response');
export const clearMessages = makeActionCreator(types.CLEAR_MESSAGES);
export const fetchTalentPoolsRequest = makeActionCreator(types.FETCH_TALENT_POOLS, 'isRequesting');
export const fetchTalentPoolsSuccess = makeActionCreator(types.FETCH_TALENT_POOLS_SUCCESS, 'response', 'readOnly');
export const fetchTalentPoolsFail = makeActionCreator(types.FETCH_TALENT_POOLS_FAIL, 'error');
export const fetchSuggestedSkillsRequest = makeActionCreator(types.FETCH_SUGGESTED_SKILLS, 'isRequesting');
export const fetchSuggestedSkillsSuccess = makeActionCreator(types.FETCH_SUGGESTED_SKILLS_SUCCESS, 'response');
export const fetchSignaturesRequest = makeActionCreator(types.FETCH_ESIGNATURES, 'isRequesting');
export const fetchSignaturesSuccess = makeActionCreator(types.FETCH_ESIGNATURES_SUCCESS, 'response');
export const fetchAgreementsRequest = makeActionCreator(types.FETCH_AGREEMENTS, 'isRequesting');
export const fetchAgreementsSuccess = makeActionCreator(types.FETCH_AGREEMENTS_SUCCESS, 'response');
export const fetchCompanyTypesRequest = makeActionCreator(types.FETCH_COMPANY_TYPES, 'isRequesting');
export const fetchCompanyTypesSuccess = makeActionCreator(types.FETCH_COMPANY_TYPES_SUCCESS, 'response');
export const fetchCountriesRequest = makeActionCreator(types.FETCH_COUNTRIES, 'isRequesting');
export const fetchCountriesSuccess = makeActionCreator(types.FETCH_COUNTRIES_SUCCESS, 'response');
export const fetchDocumentsRequest = makeActionCreator(types.FETCH_DOCUMENTS, 'isRequesting');
export const fetchDocumentsSuccess = makeActionCreator(types.FETCH_DOCUMENTS_SUCCESS, 'response');
export const fetchTalentPoolMembershipsRequest = makeActionCreator(types.FETCH_TALENT_POOL_MEMBERSHIPS, 'isRequesting');
export const fetchTalentPoolMembershipsSuccess = makeActionCreator(types.FETCH_TALENT_POOL_MEMBERSHIPS_SUCCESS, 'response');
export const fetchOrgUnitsRequest = makeActionCreator(types.FETCH_ORG_UNITS, 'isRequesting');
export const fetchOrgUnitsSuccess = makeActionCreator(types.FETCH_ORG_UNITS_SUCCESS, 'response');
export const fetchOrgUnitMembersRequest = makeActionCreator(types.FETCH_ORG_UNIT_MEMBERS, 'isRequesting');
export const fetchOrgUnitMembersSuccess = makeActionCreator(types.FETCH_ORG_UNIT_MEMBERS_SUCCESS, 'response');
export const toggleTalentPoolActive = makeActionCreator(types.TOGGLE_TALENT_POOL_ACTIVE, 'groupId', 'isActive');
export const changeTab = makeActionCreator(types.CHANGE_TAB, 'tab', 'groupId');
export const submitTalentPoolFormRequest = makeActionCreator(types.SUBMIT_TALENT_POOL_FORM, 'isRequesting');
export const submitTalentPoolFormSuccess = makeActionCreator(types.SUBMIT_TALENT_POOL_FORM_SUCCESS, 'response', 'updatedTalentPool');
export const submitTalentPoolFormFail = makeActionCreator(types.SUBMIT_TALENT_POOL_FORM_FAIL, 'error');
export const submitTalentPoolFormCreateRequest = makeActionCreator(types.SUBMIT_TALENT_POOL_FORM_CREATE, 'isRequesting');
export const submitTalentPoolFormCreateSuccess = makeActionCreator(types.SUBMIT_TALENT_POOL_FORM_CREATE_SUCCESS, 'response', 'createdTalentPool');
export const submitTalentPoolFormCreateFail = makeActionCreator(types.SUBMIT_TALENT_POOL_FORM_CREATE_FAIL, 'error');
export const submitMessageFormRequest = makeActionCreator(types.SUBMIT_MESSAGE_FORM, 'isRequesting');
export const submitMessageFormSuccess = makeActionCreator(types.SUBMIT_MESSAGE_FORM_SUCCESS, 'groupId');
export const submitMessageFormFail = makeActionCreator(types.SUBMIT_MESSAGE_FORM_FAIL, 'error');
export const getInvitedOrAppliedSuccess = makeActionCreator(types.GET_INVITED_OR_APPLIED_SUCCESS, 'response');
export const getMembersRequest = makeActionCreator(types.GET_MEMBERS, 'isRequesting');
export const getMembersSuccess = makeActionCreator(types.GET_MEMBERS_SUCCESS, 'response', 'count');
export const getMembersFail = makeActionCreator(types.GET_MEMBERS_FAIL, 'error');
export const updateFilters = makeActionCreator(types.UPDATE_FILTERS, 'filters');
export const newTalentPool = makeActionCreator(types.NEW_TALENT_POOL);
export const deleteTalentPool = makeActionCreator(types.DELETE_TALENT_POOL);
export const deleteTalentPoolCancel = makeActionCreator(types.DELETE_TALENT_POOL_CANCEL);
export const deleteTalentPoolSuccess = makeActionCreator(types.DELETE_TALENT_POOL_SUCCESS, 'groupId');
export const deleteTalentPoolFail = makeActionCreator(types.DELETE_TALENT_POOL_FAIL, 'error');
export const removeRequirement = makeActionCreator(types.REMOVE_REQUIREMENT, 'requirementKey', 'requirementValue');
export const routingTalentPools = makeActionCreator(types.ROUTING_TALENT_POOLS, 'path');
export const toggleRequirementNotifyOnExpiry = makeActionCreator(types.TOGGLE_NOTIFY_ON_EXPIRY_REQUIREMENT, 'requirementKey', 'requirementValue');
export const toggleRequirementRemoveOnExpiry = makeActionCreator(types.TOGGLE_REMOVE_ON_EXPIRY_REQUIREMENT, 'requirementKey', 'requirementValue');
export const loadExistingGroupForEdit = makeActionCreator(types.LOAD_EXISTING_GROUP, 'group');
export const applyRequirement = makeActionCreator(types.APPLY_REQUIREMENT, 'requirement');
export const cancelRequirement = makeActionCreator(types.CANCEL_REQUIREMENT);
export const setActiveRequirementType = makeActionCreator(types.SET_ACTIVE_REQUIREMENT_TYPE, 'requirementType');
export const activateAutoEnforcementSuccess = makeActionCreator(types.ACTIVATE_AUTO_ENFORCEMENT_SUCCESS, 'groupId', 'result');
export const activateAutoEnforcementFail = makeActionCreator(types.ACTIVATE_AUTO_ENFORCEMENT_FAIL, 'groupId');
export const initRequirements = makeActionCreator(types.INIT_REQUIREMENTS, 'id', 'autoEnforce');
export const saveRequirementSetStarted = makeActionCreator(types.SAVE_REQUIREMENT_STARTED);
export const saveRequirementSetSuccess = makeActionCreator(types.SAVE_REQUIREMENT_SUCCESS, 'requirement');
export const saveRequirementSetFail = makeActionCreator(types.SAVE_REQUIREMENT_FAIL, 'error');
export const setRequirementsGroupId = makeActionCreator(types.SET_REQUIREMENTS_GROUP_ID, 'groupId', 'groupName');
export const getRequirementSetSuccess = makeActionCreator(types.GET_REQUIREMENT_SET_SUCCESS, 'response');
export const getRequirementSetFail = makeActionCreator(types.GET_REQUIREMENT_SET_FAIL, 'error');
export const getRequirementsSuccess = makeActionCreator(types.GET_REQUIREMENTS_SUCCESS, 'response');
export const getRequirementsFail = makeActionCreator(types.GET_REQUIREMENTS_FAIL, 'error');
export const showInviteFlow = makeActionCreator(types.SHOW_INVITE_FLOW, 'inviting', 'groupId');
export const addToSelection = makeActionCreator(types.ADD_TO_SELECTION, 'user');
export const removeFromSelection = makeActionCreator(types.REMOVE_FROM_SELECTION, 'user');
export const inviteMembersSuccess = makeActionCreator(types.INVITE_MEMBERS_SUCCESS, 'response', 'id');
export const inviteMembersFail = makeActionCreator(types.INVITE_MEMBERS_FAIL, 'error');
export const inviteParticipantsSuccess = makeActionCreator(types.INVITE_PARTICIPANTS_SUCCESS, 'response', 'id');
export const toggleSelectAll = makeActionCreator(types.TOGGLE_SELECT_ALL, 'selected');
export const talentPoolSortChanged = makeActionCreator(types.TALENT_POOL_SORT_CHANGED, 'sortedTalentPools', 'fieldName', 'sortDirection');
export const googleAPILoaded = makeActionCreator(types.GOOGLE_INITIALIZED);
export const openMemberActionMenu = makeActionCreator(types.OPEN_MEMBER_ACTION_MENU, 'isOpen', 'userNumber');
export const openBulkActionMenu = makeActionCreator(types.OPEN_BULK_ACTION_MENU, 'isOpen');
export const removeDeclineSuccess = makeActionCreator(types.REMOVE_DECLINE_SUCCESS, 'users');
export const removeDeclineFail = makeActionCreator(types.REMOVE_DECLINE_FAIL);
export const removeDeclineParticipantsSuccess = makeActionCreator(types.REMOVE_DECLINE_PARTICIPANTS_SUCCESS, 'participants');
export const removeDeclineParticipantsFail =
	makeActionCreator(types.REMOVE_DECLINE_PARTICIPANTS_FAIL);
export const uninviteSuccess = makeActionCreator(types.UNINVITE_SUCCESS, 'users');
export const uninviteFail = makeActionCreator(types.UNINVITE_FAIL);
export const approveSuccess = makeActionCreator(types.APPROVE_SUCCESS);
export const approveFail = makeActionCreator(types.APPROVE_FAIL);
export const approveParticipantsSuccess = makeActionCreator(types.APPROVE_PARTICIPANTS_SUCCESS);
export const approveParticipantsFail = makeActionCreator(types.APPROVE_PARTICIPANTS_FAIL);
export const downloadDocumentationSuccess = makeActionCreator(types.DOWNLOAD_DOCUMENTATION_SUCCESS);
export const downloadDocumentationFail = makeActionCreator(types.DOWNLOAD_DOCUMENTATION_FAIL);
export const searchFilterUpdate = makeActionCreator(types.SEARCH_FILTER_UPDATE, 'filterObject');
export const memberPaginationChanged = makeActionCreator(types.MEMBER_PAGINATION_CHANGED, 'page');
export const refreshMembers = makeActionCreator(types.REFRESH_MEMBERS);
export const removeOrgUnit = makeActionCreator(types.REMOVE_ORG_UNIT, 'value');
export const changeOrgMode = makeActionCreator(types.CHANGE_ORG_MODE, 'value');

export const toggleActivateAutomaticEnforcement = (id, enabled) => {
	return (dispatch) => {
		const urlString = `/groups/${id}/automatic_evaluation/${((enabled === 'true') ? 'on' : 'off')}`;

		return fetch(urlString, { headers: { 'X-CSRF-Token': getCSRFToken() }, credentials: 'same-origin', method: 'post' })
			.then(res => res.json())
			.then((response) => {
				if (response.successful) {
					dispatch(activateAutoEnforcementSuccess(id, enabled));
				}
				wmAlert({
					type: response.successful ? 'success' : 'danger',
					message: response.messages[0]
				});
			},
			() => {
				dispatch(activateAutoEnforcementFail(id));
				wmAlert({
					type: 'danger',
					message: 'There was an error activating automatic enforcement of requirement sets'
				});
			});
	};
};

export const addRequirement = (requirement, multiSelect = false, requirementType = null) => {
	return (dispatch) => {
		dispatch({
			type: types.ADD_REQUIREMENT,
			requirement,
			multiSelect,
			requirementType
		});
		dispatch(clearToggledRequirements());
	};
};

export const routeTalentPools = (path) => {
	return (dispatch) => {
		browserHistory.push(path);
		dispatch(routingTalentPools(path));
	};
};

export const openProfileModal = (userNumber) => {
	return () => {
		loadUserProfile()
			.then(({
				UserProfile,
				UserProfileModal
			}) => {
				const url = `/profile/${userNumber}`;
				const popup = wmModal({
					title: url,
					root: 'body',
					destroyOnClose: true,
					template: Template
				});
				popup.show();
				const profile = new UserProfile({
					hideActions: true,
					userNumber
				});
				new UserProfileModal({ // eslint-disable-line no-new
					model: profile,
					root: '.wm-modal .wm-modal--content'
				});
			});
	};
};

export const openCompanyProfileModal = (companyNumber) => {
	return () => {
		loadWMCompanyProfileSPA()
			.then((WMCompanyProfileSPA) => {
				const csrfToken = getCSRFToken();
				const vendorProfileDiv = document.createElement('div');
				vendorProfileDiv.setAttribute('id', 'app');
				document.body.appendChild(vendorProfileDiv);
				const unmountModal = () => {
					ReactDOM.unmountComponentAtNode(vendorProfileDiv);
				};

				render(
					<WMCompanyProfileSPA
						companyNumber={ companyNumber }
						isModal
						csrf={ csrfToken }
						onClose={ unmountModal }
					/>,
					vendorProfileDiv
				);
			});
	};
};

export const toggleActive = (id, name, isActive) => {
	return (dispatch) => {
		const urlString = `/groups/v2/${id}/${(isActive ? '/deactivate' : '/activate')}`;

		return fetch(urlString, { headers: { 'X-CSRF-Token': getCSRFToken() }, credentials: 'same-origin', method: 'post' })
			.then(res => res.json())
			.then((response) => {
				if (response.successful) {
					dispatch(toggleTalentPoolActive(id, response.data.isActive));
				}
				wmAlert({
					type: response.successful ? 'success' : 'danger',
					message: response.messages[0]
				});
			},
			() => {
				wmAlert({
					type: 'danger',
					message: `There was an error ${(isActive ? 'deactivating' : 'activating')} group ${name}`
				});
			});
	};
};

export const fetchTalentPools = (url = '/groups/view/list_groups') => {
	return (dispatch) => {
		const cacheBreaker = new Date().getTime();
		dispatch(fetchTalentPoolsRequest(true));
		return fetch(`${url}?${cacheBreaker}`, { credentials: 'same-origin' })
			.then(res => res.json())
			.then(res => dispatch(fetchTalentPoolsSuccess(res.data.groups, res.data.readOnly)));
	};
};

export const fetchSuggestedSkills = (query) => {
	return (dispatch) => {
		dispatch(fetchSuggestedSkillsRequest());
		return fetch(`/v2/suggest/skill?q=${query}`, { credentials: 'same-origin' })
			.then(res => res.json())
			.then(res => dispatch(fetchSuggestedSkillsSuccess(res.results)));
	};
};

export const fetchOrgUnits = (orgUnit, baseUrl = '/v2/orgStructure') => {
	return (dispatch) => {
		dispatch(fetchOrgUnitsRequest());
		return fetch(`${baseUrl}/${orgUnit}/subTreePaths`, { credentials: 'same-origin' })
			.then(res => res.json())
			.then(res => dispatch(fetchOrgUnitsSuccess(res.results)),
				() => wmAlert({
					type: 'danger',
					message: 'There was an error fetching org units.'
				}));
	};
};

export const fetchOrgUnitMembers = (orgUnits, baseUrl = '/v2/orgStructure') => {
	return (dispatch) => {
		dispatch(fetchOrgUnitMembersRequest());
		return fetch(`${baseUrl}/orgUnitMembers`,
			{
                headers: { 'X-CSRF-Token': getCSRFToken() },
				credentials: 'same-origin',
                method: 'post',
                body: JSON.stringify(orgUnits)
			})
			.then(res => res.json())
			.then(res => dispatch(fetchOrgUnitMembersSuccess(res.results)),
				() => wmAlert({
					type: 'danger',
					message: 'There was an error fetching org unit members.'
				}));
	}
};

export const fetchMessages = (groupId) => {
	const paginationData = [
		{ iDisplayStart: 0 },
		{ iDisplayLength: 25 },
		{ sSortDir_0: 'desc' }
	];

	const url = `/groups/${groupId}/messages`;
	const paginationParams = buildQueryString(paginationData);

	return (dispatch) => {
		dispatch(fetchMessagesRequest());
		const cacheBreaker = new Date().getTime();
		return fetch(`${url}?${paginationParams}&${cacheBreaker}`, { credentials: 'same-origin' })
			.then(res => res.json())
			.then(res => dispatch(fetchMessagesSuccess(groupId, res.data)));
	};
};

export const fetchAgreements = () => {
	return (dispatch) => {
		dispatch(fetchAgreementsRequest());
		const cacheBreaker = new Date().getTime();
		return fetch(`/agreements/fetch?${cacheBreaker}`, { credentials: 'same-origin' })
			.then(res => res.json())
			.then(res => dispatch(fetchAgreementsSuccess(res)));
	};
};

export const fetchCompanyTypes = () => {
	return (dispatch) => {
		dispatch(fetchCompanyTypesRequest());
		const cacheBreaker = new Date().getTime();
		return fetch(`/company_types?${cacheBreaker}`, { credentials: 'same-origin' })
			.then(res => res.json())
			.then(res => dispatch(fetchCompanyTypesSuccess(res)));
	};
};

export const fetchCountries = () => {
	return (dispatch) => {
		dispatch(fetchCountriesRequest());
		return fetch('/countries', { credentials: 'same-origin' })
			.then(res => res.json())
			.then(res => dispatch(fetchCountriesSuccess(res)));
	};
};

export const fetchDocuments = () => {
	return (dispatch) => {
		dispatch(fetchDocumentsRequest());
		const cacheBreaker = new Date().getTime();
		return fetch(`/documents?${cacheBreaker}`, { credentials: 'same-origin' })
			.then(res => res.json())
			.then(res => dispatch(fetchDocumentsSuccess(res)));
	};
};

export const fetchSignatures = () => {
	return (dispatch) => {
		dispatch(fetchSignaturesRequest());
		const cacheBreaker = new Date().getTime();
		return fetch(`/v2/esignature/template/list?${cacheBreaker}`, { credentials: 'same-origin' })
			.then(res => res.json())
			.then(res => dispatch(fetchSignaturesSuccess(res)));
	};
};

export const fetchTalentPoolMemberships = () => {
	return (dispatch) => {
		dispatch(fetchTalentPoolMembershipsRequest());
		return fetch('/group_memberships', { credentials: 'same-origin' })
			.then(res => res.json())
			.then(res => dispatch(fetchTalentPoolMembershipsSuccess(res)));
	};
};

export const submitTalentPoolForm = (talentPoolData, baseUrl = '/groups/v2') => {
	return (dispatch) => {
		dispatch(submitTalentPoolFormRequest(true));
		const urlString = `${baseUrl}/update/${talentPoolData.id}`;
		const formData = prepareTalentPoolData(talentPoolData);

		return fetch(urlString, {
			headers: { 'X-CSRF-Token': getCSRFToken() },
			credentials: 'same-origin',
			method: 'post',
			body: formData
		})
			.then(res => res.json())
			.then((response) => {
				if (response.data.result.success) {
					wmAlert({
						type: 'success',
						message: 'Your Talent Pool has been updated.'
					});

					const updatedTalentPool = talentPoolData;
					updatedTalentPool.id = response.data.result.id;
					updatedTalentPool.isSaving = false;
					dispatch(submitTalentPoolFormSuccess(response.data.result, updatedTalentPool));
				} else if (response.data.result.error) {
					dispatch(submitTalentPoolFormFail(response.data.result.error));
					wmAlert({
						type: 'danger',
						message: `Error: ${response.data.result.error}`
					});
				}
			},
				(error) => {
					dispatch(submitTalentPoolFormFail(error));
				});
	};
};

export const getRequirements = (requirementSetId, url = '/requirement_sets') => {
	return (dispatch) => {
		const cacheBreaker = new Date().getTime();
		const urlString = `${url}/${requirementSetId}/requirements?${cacheBreaker}`;
		return fetch(urlString, {
			headers: {
				Accept: 'application/json, text/javascript, */*; q=0.01',
				'X-Requested-With': 'XMLHttpRequest'
			},
			credentials: 'same-origin',
			method: 'get',
			cache: 'no-cache'
		})
			.then(res => res.json())
			.then((response) => {
				dispatch(getRequirementsSuccess(response));
			},
			(error) => {
				dispatch(getRequirementsFail(error));
			});
	};
};

export const getRequirementSet = (groupId, url = '/groups') => {
	return (dispatch) => {
		const urlString = `${url}/${groupId}/requirements`;
		return fetch(urlString, {
			headers: {
				Accept: 'application/json, text/javascript, */*; q=0.01',
				'X-Requested-With': 'XMLHttpRequest'
			},
			credentials: 'same-origin',
			method: 'get'
		})
			.then(res => res.json())
			.then((response) => {
				dispatch(getRequirements(response.id));
				dispatch(getRequirementSetSuccess(response));
			},
			(error) => {
				dispatch(getRequirementSetFail(error));
			});
	};
};

export const submitTalentPoolCreateForm = (talentPoolData, baseUrl = '/groups/v2') => {
	return (dispatch) => {
		dispatch(submitTalentPoolFormCreateRequest(true));
		const urlString = `${baseUrl}/create`;
		const formData = prepareTalentPoolData(talentPoolData);

		return fetch(urlString, {
			headers: { 'X-CSRF-Token': getCSRFToken() },
			credentials: 'same-origin',
			method: 'post',
			body: formData
		})
			.then(res => res.json())
			.then((response) => {
				if (response.data.result.success) {
					wmAlert({
						type: 'success',
						message: 'Your Talent Pool has been created.'
					});

					const createdTalentPool = talentPoolData;
					createdTalentPool.id = response.data.result.id;
					createdTalentPool.interactionMode = interactionModes.MANAGE;
					if (createdTalentPool.openMembership === 'true') {
						dispatch(initRequirements(createdTalentPool.id, false));
						dispatch(getRequirementSet(createdTalentPool.id));
					}
					dispatch(submitTalentPoolFormCreateSuccess(response.data.result, createdTalentPool));
				} else if (response.data.result.error) {
					dispatch(submitTalentPoolFormCreateFail(response.data.result.error));
					wmAlert({
						type: 'danger',
						message: `Error: ${response.data.result.error}`
					});
				}
			},
		(error) => {
			dispatch(submitTalentPoolFormCreateFail(error));
		});
	};
};

export const submitMessageForm = (groupId, messageData) => {
	return (dispatch) => {
		dispatch(submitMessageFormRequest());

		const url = `/groups/${groupId}/messages`;
		const formData = new FormData();
		Object.keys(messageData).forEach(key => formData.append(key, messageData[key]));

		return fetch(url, {
			headers: { 'X-CSRF-Token': getCSRFToken() },
			credentials: 'same-origin',
			method: 'post',
			body: formData
		})
			.then(res => res.json())
			.then((response) => {
				if (response.successful) {
					dispatch(submitMessageFormSuccess(groupId));
				}
				wmAlert({
					type: response.successful ? 'success' : 'danger',
					message: response.messages[0]
				});
			},
				(error) => {
					dispatch(submitMessageFormFail(error));
				});
	};
};

export const deleteTalentPoolConfirm = (groupId) => {
	return (dispatch) => {
		const url = `/groups/${groupId}/delete`;

		return fetch(url, {
			headers: { 'X-CSRF-Token': getCSRFToken() },
			credentials: 'same-origin',
			method: 'post'
		})
			.then(res => res.json())
			.then((response) => {
				if (response.successful) {
					dispatch(deleteTalentPoolSuccess(groupId));
				}
				wmAlert({
					type: response.successful ? 'success' : 'danger',
					message: response.messages[0]
				});
			},
			(error) => {
				dispatch(deleteTalentPoolFail(error));
			});
	};
};

const doSearchRequest = (data, url) => {
	const queryString = buildQueryString(data);
	const cacheBreaker = new Date().getTime();
	return fetch(`${url}?${queryString}&${cacheBreaker}`, {
		headers: {
			Accept: 'application/json, text/javascript, */*; q=0.01',
			'X-Requested-With': 'XMLHttpRequest'
		},
		credentials: 'same-origin',
		method: 'GET'
	})
	.then(res => res.json());
};

export const getMembers = (id, memberFilters, inviting, page, pageSize, url = '/search/retrieve') => {
	// todo - improve wmSearchFilter for use here
	return (dispatch) => {
		dispatch(getMembersRequest(true));

		const activeFilters = memberFilters;
		const searchMode = (inviting ? 'PEOPLE_SEARCH_TALENT_POOL_INVITE' : 'PEOPLE_SEARCH_GROUP_MEMBER');
		const pageIndex = page - 1;
		const filterData = [
			{
				sortby: ''
			},
			{
				start: pageIndex * pageSize
			},
			{
				limit: pageSize
			},
			{
				search_type: searchMode
			},
			{
				group_id: id
			},
			{
				member: !inviting
			},
			{
				memberoverride: !inviting
			},
			{
				pending: !inviting
			},
			{
				pendingoverride: !inviting
			},
			{
				invited: !inviting
			},
			{
				declined: !inviting
			}
		];

		// Reset membership filters
		activeFilters.forEach((filter) => {
			if (filter.name === 'groupstatus') {
				filterData[5].member = false;
				filterData[6].memberoverride = false;
				filterData[7].pending = false;
				filterData[8].pendingoverride = false;
				filterData[9].invited = false;
				filterData[10].declined = false;
			}
		});

		activeFilters.forEach((filter) => {
			const objectToPush = {};
			if (filter.name === 'location') {
				filterData.push({ address: filter.value.address });
				filterData.push({ radius: filter.value.radius });
				if (filter.value.countries !== undefined) {
					filterData.push({ countries: filter.value.countries });
				}
			} else if (filter.value === 'avatar') {
				objectToPush.avatar = true;
				filterData.push(objectToPush);
			} else if (filter.value === 'WORKER' || filter.value === 'VENDOR') {
				objectToPush.userTypes = filter.value;
				filterData.push(objectToPush);
			} else if (filter.value.indexOf('companyType:') === 0) {
				objectToPush.companytypes = filter.value.substring(filter.value.indexOf(':') + 1);
				filterData.push(objectToPush);
			} else if (filter.name === 'groupstatus' && filter.value === 'member') {
				filterData[5].member = true;
			} else if (filter.name === 'groupstatus' && filter.value === 'memberoverride') {
				filterData[6].memberoverride = true;
			} else if (filter.name === 'groupstatus' && filter.value === 'pending') {
				filterData[7].pending = true;
			} else if (filter.name === 'groupstatus' && filter.value === 'pendingoverride') {
				filterData[8].pendingoverride = true;
			} else if (filter.name === 'groupstatus' && filter.value === 'invited') {
				filterData[9].invited = true;
			} else if (filter.name === 'groupstatus' && filter.value === 'declined') {
				filterData[10].declined = true;
			} else {
				objectToPush[filter.name] = filter.value;
				filterData.push(objectToPush);
			}
		});

		return doSearchRequest(filterData, url)
			.then((res) => {
				dispatch(getMembersSuccess(res.results, res.results_count));
				if (memberFilters.length === 0) {
					dispatch(getInvitedOrAppliedSuccess(res.results));
				}
				dispatch(updateFilters(res.filters));
			});
	};
};

export const saveRequirementSet = (requirementSet, baseRequirementsModel, groupId, url = '/groups') => {
	return (dispatch) => {
		dispatch(saveRequirementSetStarted());
		const urlString = `${url}/${groupId}/requirements/${baseRequirementsModel.id}`;
		const requirementsModel = baseRequirementsModel;
		requirementsModel.requirements = requirementSet;
		const queryString = `model=${encodeURIComponent(JSON.stringify(requirementsModel))}`;
		return fetch(urlString, {
			headers: {
				'Content-Type': 'application/x-www-form-urlencoded',
				'X-CSRF-Token': getCSRFToken()
			},
			credentials: 'same-origin',
			method: 'POST',
			body: queryString
		})
			.then(res => res.json())
			.then((response) => {
				dispatch(saveRequirementSetSuccess(response));
				dispatch(getRequirementSet(groupId));
				wmAlert({
					type: 'success',
					message: 'Requirements have been saved.'
				});
			},
			() => {
				wmAlert({
					type: 'danger',
					message: `There was a problem updating the requirement set ${requirementsModel.name}`
				});
			});
	};
};

export const inviteMembers = (id, userNumbers, url = '/groups/invite_workers') => {
	return (dispatch) => {
		const urlString = `${url}/${id}`;
		const queryString = userNumbers
			.map(num => `selected_workers[]=${num}&`)
			.join('')
			.slice(0, -1);

		return fetch(urlString, {
			headers: {
				'Content-Type': 'application/x-www-form-urlencoded',
				'X-CSRF-Token': getCSRFToken()
			},
			credentials: 'same-origin',
			method: 'POST',
			body: queryString
		})
			.then(res => res.json())
			.then((response) => {
				dispatch(inviteMembersSuccess(response, id));
				response.messages.forEach((message) => {
					wmAlert({
						type: 'success',
						message
					});
				});
			},
			() => {
				wmAlert({
					type: 'danger',
					message: 'There was a problem inviting workers to the talent pool.'
				});
			});
	};
};

export const inviteParticipants = (id, participants, url = '/groups/invite_participants') => {
	return (dispatch) => {
		const urlString = `${url}/${id}`;

		return fetch(urlString, {
			headers: {
				'Content-Type': 'application/json',
				'X-CSRF-Token': getCSRFToken()
			},
			credentials: 'same-origin',
			method: 'POST',
			body: JSON.stringify(participants)
		})
			.then(res => res.json())
			.then((response) => {
				dispatch(inviteParticipantsSuccess(response, id));
				response.messages.forEach((message) => {
					wmAlert({
						type: 'success',
						message
					});
				});
			},
				() => {
					wmAlert({
						type: 'danger',
						message: 'There was a problem inviting participants to the talent pool.'
					});
				});
	};
};

export const manageGroup = (group, memberTabMode) => {
	return (dispatch) => {
		dispatch(loadExistingGroupForEdit(group));
		if (group.open_membership === true) {
			dispatch(initRequirements(group.id, group.autoEnforce));
			dispatch(getRequirementSet(group.id));
		}
		const action = {
			type: types.MANAGE_GROUP,
			group,
			memberTabMode
		};
		dispatch(action);
	};
};

export const manageTalentPoolByGroupId = (groupId) => {
	return (dispatch, getState) => {
		return dispatch(fetchTalentPools())
		.then(() => {
			const rootData = getState().rootData;
			if (rootData.get('id') !== groupId) {
				const talentPools = rootData.get('talentPools');
				const activeGroup = talentPools.find(talentpool => talentpool.get('id') === groupId);
				dispatch(manageGroup(activeGroup.toJS(), rootData.get('memberTabMode') || 'all'));
			}
		});
	};
};

export const sortTalentPools = (
	talentPools,
	sortField,
	sortDirection,
	isNumeric = false,
	toggleDirection = false
) => {
	return (dispatch) => {
		let direction = sortDirection;
		if (toggleDirection) {
			direction = (sortDirection === 'asc') ? 'desc' : 'asc';
		}
		let sortedTalentPools = [];
		sortedTalentPools = talentPools.sort((a, b) => {
			const aValue = isNumeric ? parseInt(a[sortField], 10) : a[sortField].toString().toUpperCase();
			const bValue = isNumeric ? parseInt(b[sortField], 10) : b[sortField].toString().toUpperCase();

			if (direction === 'desc') {
				if (aValue < bValue) {
					return 1;
				} else if (aValue > bValue) {
					return -1;
				}
			} else if (direction === 'asc') {
				if (aValue > bValue) {
					return 1;
				} else if (aValue < bValue) {
					return -1;
				}
			}
			return 0;
		});

		dispatch(talentPoolSortChanged(sortedTalentPools, sortField, direction));
	};
};

export const removeOrDecline = (id, userNumbers, baseUrl = '') => {
	return (dispatch) => {
		const urlString = `${baseUrl}/groups/${id}/remove_users`;
		const formData = new FormData();
		userNumbers.forEach((userNumber) => {
			formData.append('userNumbers[]', userNumber);
		});

		return fetch(urlString, {
			headers: {
				Accept: 'application/json, text/javascript, */*; q=0.01',
				'X-CSRF-Token': getCSRFToken(),
				'X-Requested-With': 'XMLHttpRequest'
			},
			credentials: 'same-origin',
			method: 'POST',
			body: formData
		})
			.then(res => res.json())
			.then(() => {
				dispatch(removeDeclineSuccess(userNumbers));
				dispatch(openBulkActionMenu(false));
				setTimeout(() => { dispatch(refreshMembers()); }, 2000);
				wmAlert({
					type: 'success',
					message: 'The selected users have been removed from the talent pool.'
				});
			},
			() => {
				dispatch(removeDeclineFail());
				dispatch(openBulkActionMenu(false));
				wmAlert({
					type: 'danger',
					message: 'There was a problem removing the selected users.'
				});
			});
	};
};

export const removeOrDeclineParticipants = (id, participants, baseUrl = '') => {
	return (dispatch) => {
		const urlString = `${baseUrl}/groups/${id}/remove_participants`;

		return fetch(urlString, {
			headers: {
				Accept: 'application/json, text/javascript, */*; q=0.01',
				'Content-Type': 'application/json',
				'X-CSRF-Token': getCSRFToken(),
				'X-Requested-With': 'XMLHttpRequest'
			},
			credentials: 'same-origin',
			method: 'POST',
			body: JSON.stringify(participants)
		})
			.then(res => res.json())
			.then(() => {
				dispatch(removeDeclineParticipantsSuccess(participants));
				dispatch(openBulkActionMenu(false));
				setTimeout(() => { dispatch(refreshMembers()); }, 2000);
				wmAlert({
					type: 'success',
					message: 'The selected participants have been removed from the talent pool.'
				});
			},
			() => {
				dispatch(removeDeclineParticipantsFail());
				dispatch(openBulkActionMenu(false));
				wmAlert({
					type: 'danger',
					message: 'There was a problem removing the selected participants.'
				});
			});
	};
};

export const uninvite = (id, userNumbers, baseUrl = '') => {
	return (dispatch) => {
		const urlString = `${baseUrl}/groups/${id}/cancel_invitations`;
		const formData = new FormData();
		userNumbers.forEach((userNumber) => {
			formData.append('userNumbers[]', userNumber);
		});

		return fetch(urlString, {
			headers: {
				Accept: 'application/json, text/javascript, */*; q=0.01',
				'X-CSRF-Token': getCSRFToken(),
				'X-Requested-With': 'XMLHttpRequest'
			},
			credentials: 'same-origin',
			method: 'POST',
			body: formData
		})
			.then(res => res.json())
			.then(() => {
				dispatch(uninviteSuccess(userNumbers));
				dispatch(openBulkActionMenu(false));
				setTimeout(() => { dispatch(refreshMembers()); }, 2000);
				wmAlert({
					type: 'success',
					message: 'The selected users have been uninvited from the talent pool.'
				});
			},
			() => {
				dispatch(uninviteFail());
				dispatch(openBulkActionMenu(false));
				wmAlert({
					type: 'danger',
					message: 'There was a problem uninviting the selected users.'
				});
			});
	};
};

export const approve = (id, userNumbers, baseUrl = '') => {
	return (dispatch) => {
		const urlString = `${baseUrl}/groups/${id}/approve_users`;
		const formData = new FormData();
		userNumbers.forEach((userNumber) => {
			formData.append('userNumbers[]', userNumber);
		});

		return fetch(urlString, {
			headers: {
				Accept: 'application/json, text/javascript, */*; q=0.01',
				'X-CSRF-Token': getCSRFToken(),
				'X-Requested-With': 'XMLHttpRequest'
			},
			credentials: 'same-origin',
			method: 'POST',
			body: formData
		})
			.then(res => res.json())
			.then(() => {
				dispatch(approveSuccess(userNumbers));
				dispatch(openBulkActionMenu(false));
				setTimeout(() => { dispatch(refreshMembers()); }, 2000);
				wmAlert({
					type: 'success',
					message: 'The selected users have been approved for membership.'
				});
			},
			() => {
				dispatch(approveFail());
				dispatch(openBulkActionMenu(false));
				wmAlert({
					type: 'danger',
					message: 'There was a problem approving the selected users.'
				});
			});
	};
};

export const approveParticipants = (id, participants, baseUrl = '') => {
	return (dispatch) => {
		const urlString = `${baseUrl}/groups/${id}/approve_participants`;

		return fetch(urlString, {
			headers: {
				Accept: 'application/json, text/javascript, */*; q=0.01',
				'Content-Type': 'application/json',
				'X-CSRF-Token': getCSRFToken(),
				'X-Requested-With': 'XMLHttpRequest'
			},
			credentials: 'same-origin',
			method: 'POST',
			body: JSON.stringify(participants)
		})
			.then(res => res.json())
			.then(() => {
				dispatch(approveParticipantsSuccess(participants));
				dispatch(openBulkActionMenu(false));
				setTimeout(() => {
					dispatch(refreshMembers());
				}, 2000);
				wmAlert({
					type: 'success',
					message: 'The selected participants have been approved for membership.'
				});
			},
			() => {
				dispatch(approveParticipantsFail());
				dispatch(openBulkActionMenu(false));
				wmAlert({
					type: 'danger',
					message: 'There was a problem approving the selected participants.'
				});
			});
	};
};

export const downloadDocumentation = (id, userNumbers, baseUrl = '') => {
	return (dispatch) => {
		const urlString = `${baseUrl}/groups/${id}/documentations`;
		const formData = new FormData();
		userNumbers.forEach((userNumber) => {
			formData.append('userNumbers[]', userNumber);
		});
		return fetch(urlString, {
			headers: {
				Accept: 'application/json, text/javascript, */*; q=0.01',
				'X-CSRF-Token': getCSRFToken(),
				'X-Requested-With': 'XMLHttpRequest'
			},
			credentials: 'same-origin',
			method: 'POST',
			body: formData
		})
			.then(res => res.json())
			.then(() => {
				dispatch(downloadDocumentationSuccess());
				dispatch(openBulkActionMenu(false));
				dispatch(toggleSelectAll(false));
				wmAlert({
					type: 'success',
					message: 'Your download is processing. You will receive a notification when the download is ready.'
				});
			},
			() => {
				dispatch(downloadDocumentationFail());
				dispatch(openBulkActionMenu(false));
				wmAlert({
					type: 'danger',
					message: 'There was a problem downloading the documentation for these users.'
				});
			});
	};
};

export const handleMemberPagination = (direction, page, pageSize, totalResults) => {
	return (dispatch) => {
		let currentPage = page;
		if (direction === 'prev' && currentPage > 1) {
			currentPage -= 1;
		} else if (direction === 'next' && currentPage < Math.ceil(totalResults / pageSize)) {
			currentPage += 1;
		}
		dispatch(memberPaginationChanged(currentPage));
	};
};

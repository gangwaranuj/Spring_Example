import 'isomorphic-fetch';
import nock from 'nock';
import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';
import $ from 'jquery';
import * as actions from '../index';
import * as types from '../../constants/actionTypes';
import * as interactionModes from '../../constants/interactionModes';

const middlewares = [thunk];
const mockStore = configureMockStore(middlewares);

describe('Talent Pool Actions', () => {
	const setupMockGetServer = (options) => {
		return nock('http://localhost:8080')
			.filteringPath(() => {
				return options.url;
			})
			.get(options.url)
			.reply(200, options.response);
	};
	const setupMockPostServer = (options) => {
		return nock('http://localhost:8080')
			.post(options.url)
			.reply(200, options.response);
	};
	const mockStorage = () => {
		const storage = {};
		return {
			setItem (key, value) {
				storage[key] = value || '';
			},
			getItem (key) {
				return storage[key];
			},
			removeItem (key) {
				delete storage[key];
			},
			get length () {
				return Object.keys(storage).length;
			},
			key (i) {
				const keys = Object.keys(storage);
				return keys[i] || null;
			}
		};
	};

	beforeAll(() => {
		global.window.parent.$ = $;
		global.window.sessionStorage = mockStorage();
		global.window.localStorage = mockStorage();
	});

	afterEach(() => {
		nock.cleanAll();
	});

	it('should create a change field action', () => {
		const testField = 'foo';
		const expectedAction = {
			type: types.CHANGE_FIELD,
			name: 'testField',
			value: testField
		};

		expect(actions.changeField('testField', testField)).toEqual(expectedAction);
	});

	it('should create a change requirement field action', () => {
		const testField = 'foo';
		const expectedAction = {
			type: types.CHANGE_REQUIREMENT_FIELD,
			name: 'testField',
			value: testField
		};

		expect(actions.changeRequirementField('testField', testField)).toEqual(expectedAction);
	});

	it('should create a change deep requirement field action', () => {
		const testFieldName = 'foo';
		const testFieldValue = 'fooValue';
		const expectedAction = {
			type: types.CHANGE_DEEP_REQUIREMENT_FIELD,
			name: 'testSelectField',
			fieldName: testFieldName,
			value: testFieldValue
		};

		expect(actions.changeDeepRequirementField('testSelectField', testFieldName, testFieldValue)).toEqual(expectedAction);
	});

	it('should create a close drawer action', () => {
		const expectedAction = {
			type: types.CLOSE_DRAWER
		};

		expect(actions.closeDrawer()).toEqual(expectedAction);
	});

	it('should create a fetch talent pools request action', () => {
		const expectedAction = {
			type: types.FETCH_TALENT_POOLS,
			isRequesting: true
		};

		expect(actions.fetchTalentPoolsRequest(true)).toEqual(expectedAction);
	});

	it('should create a fetch talent pools success action', () => {
		const expectedAction = {
			type: types.FETCH_TALENT_POOLS_SUCCESS,
			readOnly: false,
			response: { foo: 'bar' }
		};

		expect(actions.fetchTalentPoolsSuccess({ foo: 'bar' }, false)).toEqual(expectedAction);
	});

	it('should create a fetch talent pools fail action', () => {
		const expectedAction = {
			type: types.FETCH_TALENT_POOLS_FAIL,
			error: { foo: 'bar' }
		};

		expect(actions.fetchTalentPoolsFail({ foo: 'bar' })).toEqual(expectedAction);
	});

	it('should create a change tab action', () => {
		const expectedAction = {
			type: types.CHANGE_TAB,
			tab: 'foo',
			groupId: 123
		};

		expect(actions.changeTab('foo', 123)).toEqual(expectedAction);
	});

	it('should create a submit form request action', () => {
		const expectedAction = {
			type: types.SUBMIT_TALENT_POOL_FORM,
			isRequesting: true
		};

		expect(actions.submitTalentPoolFormRequest(true)).toEqual(expectedAction);
	});

	it('should create a submit form success action', () => {
		const expectedAction = {
			type: types.SUBMIT_TALENT_POOL_FORM_SUCCESS,
			response: { foo: 'bar' },
			updatedTalentPool: { bar: 'foo' }
		};

		expect(actions.submitTalentPoolFormSuccess({ foo: 'bar' }, { bar: 'foo' })).toEqual(expectedAction);
	});

	it('should create a submit form fail action', () => {
		const expectedAction = {
			type: types.SUBMIT_TALENT_POOL_FORM_FAIL,
			error: { foo: 'bar' }
		};

		expect(actions.submitTalentPoolFormFail({ foo: 'bar' })).toEqual(expectedAction);
	});

	it('should create a get members request action', () => {
		const expectedAction = {
			type: types.GET_MEMBERS,
			isRequesting: true
		};

		expect(actions.getMembersRequest(true)).toEqual(expectedAction);
	});

	it('should create a get members success action', () => {
		const expectedAction = {
			type: types.GET_MEMBERS_SUCCESS,
			response: { foo: 'bar' },
			count: 1
		};

		expect(actions.getMembersSuccess({ foo: 'bar' }, 1)).toEqual(expectedAction);
	});

	it('should create a get members fail action', () => {
		const expectedAction = {
			type: types.GET_MEMBERS_FAIL,
			error: { foo: 'bar' }
		};

		expect(actions.getMembersFail({ foo: 'bar' })).toEqual(expectedAction);
	});

	it('should create an update filters action', () => {
		const expectedAction = {
			type: types.UPDATE_FILTERS,
			filters: { foo: 'bar' }
		};

		expect(actions.updateFilters({ foo: 'bar' })).toEqual(expectedAction);
	});

	it('should create a new talent pool action', () => {
		const expectedAction = {
			type: types.NEW_TALENT_POOL
		};

		expect(actions.newTalentPool()).toEqual(expectedAction);
	});

	it('should create an add requirement action with default `multiSelect` and `requirementType` options ', () => {
		const expectedAction = {
			type: types.ADD_REQUIREMENT,
			requirement: { foo: 'bar' },
			multiSelect: false,
			requirementType: null
		};
		const dispatch = jest.fn();
		actions.addRequirement({ foo: 'bar' })(dispatch);
		expect(dispatch).toHaveBeenCalledWith(expectedAction);
	});

	it('should create a clear toggled requirements action', () => {
		const expectedAction = {
			type: types.CLEAR_TOGGLED_REQUIREMENTS
		};

		expect(actions.clearToggledRequirements()).toEqual(expectedAction);
	});

	it('should create a remove requirement action', () => {
		const expectedAction = {
			type: types.REMOVE_REQUIREMENT,
			requirementKey: 'key',
			requirementValue: 666
		};

		expect(actions.removeRequirement('key', 666)).toEqual(expectedAction);
	});

	// it('should create a route talentpool action that dispatches', () => {
	// 	const dispatch = jest.fn();
	// 	const expectedRoutingAction = {
	// 		type: types.ROUTING_TALENT_POOLS,
	// 		path: '/groups'
	// 	};
	// 	actions.routeTalentPools('/groups')(dispatch);
	// 	expect(dispatch.getCall(0).args[0]).toEqual(expectedRoutingAction);
	// 	expect(dispatch).toHaveBeenCalledTimes(1);
	// });

	it('should create a load existing group action', () => {
		const expectedAction = {
			type: types.LOAD_EXISTING_GROUP,
			group: { foo: 'bar' }
		};

		expect(actions.loadExistingGroupForEdit({ foo: 'bar' })).toEqual(expectedAction);
	});

	it('should create a requirement apply action', () => {
		const expectedAction = {
			type: types.APPLY_REQUIREMENT,
			requirement: { foo: 'bar' }
		};

		expect(actions.applyRequirement({ foo: 'bar' })).toEqual(expectedAction);
	});

	it('should create a requirement cancel action', () => {
		const expectedAction = {
			type: types.CANCEL_REQUIREMENT
		};

		expect(actions.cancelRequirement()).toEqual(expectedAction);
	});

	it('should create a set active requirement type action', () => {
		const expectedAction = {
			type: types.SET_ACTIVE_REQUIREMENT_TYPE,
			requirementType: 'foobar'
		};

		expect(actions.setActiveRequirementType('foobar')).toEqual(expectedAction);
	});

	it('should create a remove org unit type action', () => {
		const expectedAction = {
			type: types.REMOVE_ORG_UNIT,
			value: 'org-001'
		};

		expect(actions.removeOrgUnit('org-001')).toEqual(expectedAction);
	});

	it('should create an org mode change type action', () => {
		const testOrgMode = { uuid: 'org-001', name: 'Org 1', paths: [] };
		const expectedAction = {
			type: types.CHANGE_ORG_MODE,
			value: testOrgMode
		};

		expect(actions.changeOrgMode(testOrgMode)).toEqual(expectedAction);
	});

	it('should have a fetchOrgUnit action that dispatches', (done) => {
		setupMockGetServer({
			url: '/v2/orgStructure/subTreePaths?orgUnitUuid=org-123',
			response: {
				data: {
					paths: [
						{
							uuid: 'org-200',
							name: 'Org B',
							path: ['Org A']
						},
						{
							uuid: 'org-300',
							name: 'Org C',
							path: ['Org A']
						}
					]
				}
			}
		});
		mockStore();
		const dispatch = jest.fn();
		const expectedAction = {
			type: types.FETCH_ORG_UNITS_SUCCESS,
			response: [
				{
					uuid: 'org-200',
					name: 'Org B',
					path: ['Org A']
				},
				{
					uuid: 'org-300',
					name: 'Org C',
					path: ['Org A']
				}
			]
		};

		return actions.fetchOrgUnits('org-123', 'http://localhost:8080/v2/orgStructure/subTreePaths')(dispatch)
			.then(() => {
				try {
					dispatch.should.have.been.calledWith(expectedAction);
					done();
				} catch (error) {
					done(error);
				}
			});
	});

	it('should have a fetchOrgUnitMembers action that dispatches', (done) => {
		setupMockGetServer({
			url: '/v2/orgStructure/orgUnitMembers?orgUnitUuids=org-123',
			response: {
				data: {
					paths: [
						{
							uuid: 'user-100',
							id: 100,
							firstName: 'Alex',
							lastName: 'Astonishing'
						},
						{
							uuid: 'user-200',
							id: 200,
							firstName: 'Beth',
							lastName: 'Blissful'
						}
					]
				}
			}
		});
		mockStore();
		const dispatch = jest.fn();
		const expectedAction = {
			type: types.FETCH_ORG_UNIT_MEMBERS_SUCCESS,
			response: [
				{
					uuid: 'user-100',
					id: 100,
					firstName: 'Alex',
					lastName: 'Astonishing'
				},
				{
					uuid: 'user-200',
					id: 200,
					firstName: 'Beth',
					lastName: 'Blissful'
				}
			]
		};

		return actions.fetchOrgUnitMembers(['org-123'], 'http://localhost:8080')(dispatch)
			.then(() => {
				try {
					dispatch.should.have.been.calledWith(expectedAction);
					done();
				} catch (error) {
					done(error);
				}
			});
	});

	it('should have a fetchTalentPools action that dispatches', (done) => {
		setupMockGetServer({
			url: '/groups/view/list_groups',
			response: {
				data: {
					readOnly: false,
					groups: [
						{
							foo: 'bar'
						}
					]
				}
			}
		});
		mockStore();
		const dispatch = jest.fn();
		const expectedAction = {
			type: types.FETCH_TALENT_POOLS_SUCCESS,
			readOnly: false,
			response: [
				{
					foo: 'bar'
				}
			]
		};

		return actions.fetchTalentPools('http://localhost:8080/groups/view/list_groups')(dispatch)
		.then(() => {
			try {
				dispatch.should.have.been.calledWith(expectedAction);
				done();
			} catch (error) {
				done(error);
			}
		});
	});

	it('should have a submitTalentPoolForm action that creates and dispatches', (done) => {
		setupMockPostServer({
			url: '/groups/v2/create',
			response: {
				data: {
					result: {
						success: true,
						id: 123
					}
				}
			}
		});
		mockStore();
		const dispatch = jest.fn();
		const expectedAction = {
			type: types.SUBMIT_TALENT_POOL_FORM_CREATE_SUCCESS,
			response: {
				success: true,
				id: 123
			},
			createdTalentPool: {
				name: 'hello',
				id: 123,
				interactionMode: interactionModes.MANAGE
			}
		};

		const formData = {
			name: 'hello',
			id: 0
		};

		return actions.submitTalentPoolCreateForm(formData, 'http://localhost:8080/groups/v2')(dispatch)
		.then(() => {
			try {
				expect(dispatch).toHaveBeenCalledWith(expectedAction);
				done();
			} catch (error) {
				done(error);
			}
		});
	});

	it('should have a submitTalentPoolForm action that updates and dispatches', (done) => {
		setupMockPostServer({
			url: '/groups/v2/update/123',
			response: {
				data: {
					result: {
						success: true,
						id: 123
					}
				}
			}
		});
		mockStore();
		const dispatch = jest.fn();
		const expectedAction = {
			type: types.SUBMIT_TALENT_POOL_FORM_SUCCESS,
			response: {
				success: true,
				id: 123
			},
			updatedTalentPool: {
				name: 'hello',
				isSaving: false,
				id: 123
			}
		};

		const formData = {
			name: 'hello',
			id: 123
		};

		return actions.submitTalentPoolForm(formData, 'http://localhost:8080/groups/v2')(dispatch)
		.then(() => {
			try {
				expect(dispatch).toHaveBeenCalledWith(expectedAction);
				done();
			} catch (error) {
				done(error);
			}
		});
	});

	it('should have a getMembers action that dispatches', (done) => {
		setupMockGetServer({
			url: 'search/retrieve',
			response: {
				results_count: 1,
				filters: [
					{
						foo: 'bar'
					}
				],
				results: [
					{
						name: 'johnny',
						position: 1
					}
				]
			}
		});
		mockStore();
		const dispatch = jest.fn();
		const expectedAction = {
			type: types.GET_MEMBERS_SUCCESS,
			count: 1,
			response: [
				{
					name: 'johnny',
					position: 1
				}
			]
		};

		return actions.getMembers(123, [], false, 1, 25, 'http://localhost:8080/search/retrieve')(dispatch)
		.then(() => {
			try {
				expect(dispatch).toHaveBeenCalledWith(expectedAction);
				done();
			} catch (error) {
				done(error);
			}
		});
	});

	it('should fetch a requirement set', (done) => {
		setupMockGetServer({
			url: '/groups/123/requirements',
			response: {
				active: false,
				groupId: 5163,
				$type: 'RequirementSet',
				id: 93,
				name: '(Group) TEST',
				required: false
			}
		});
		const store = mockStore();
		const expectedActions = [
			{
				type: types.GET_REQUIREMENT_SET_SUCCESS,
				response: {
					active: false,
					groupId: 5163,
					$type: 'RequirementSet',
					id: 93,
					name: '(Group) TEST',
					required: false
				}
			}
		];

		return store.dispatch(actions.getRequirementSet(123, 'http://localhost:8080/groups'))
		.then(() => {
			try {
				expect(store.getActions()).toEqual(expectedActions);
				done();
			} catch (error) {
				done(error);
			}
		});
	});

	it('should fetch requirements from a set', (done) => {
		setupMockGetServer({
			url: '/requirement_sets/123/requirements',
			response: [
				{
					$type: 'TestRequirement',
					$humanTypeName: 'Test',
					name: 'Test 123',
					value: 123
				}
			]
		});
		const store = mockStore();
		const expectedActions = [
			{
				type: types.GET_REQUIREMENTS_SUCCESS,
				response: [{
					$type: 'TestRequirement',
					$humanTypeName: 'Test',
					name: 'Test 123',
					value: 123
				}]
			}
		];

		return store.dispatch(actions.getRequirements(123, 'http://localhost:8080/requirement_sets'))
		.then(() => {
			try {
				expect(store.getActions()).toEqual(expectedActions);
				done();
			} catch (error) {
				done(error);
			}
		});
	});

	it('should have an inviteMembers action that updates and dispatches', (done) => {
		setupMockPostServer({
			url: '/groups/invite_workers/123',
			response: {
				result: {
					successful: true
				},
				messages: [
					'Successful Message'
				]
			}
		});
		const dispatch = jest.fn();
		const expectedAction = {
			type: types.INVITE_MEMBERS_SUCCESS,
			response: {
				result: {
					successful: true
				},
				messages: [
					'Successful Message'
				]
			},
			id: 123
		};

		const selectedUsers = [
			{
				userNumber: '234'
			},
			{
				userNumber: '456'
			}
		];

		return actions.inviteMembers(123, selectedUsers, 'http://localhost:8080/groups/invite_workers')(dispatch)
		.then(() => {
			try {
				expect(dispatch).toHaveBeenCalledWith(expectedAction);
				done();
			} catch (error) {
				done(error);
			}
		});
	});

	// it('should have a manageGroup action that dispatches loading existing group and managing group', () => {
	// 	const dispatch = jest.fn();
	// 	const simpleGroup = { groupId: 1, name: 'simpleGroup', open_membership: false };
	// 	const expectedLoadGroupAction = {
	// 		type: types.LOAD_EXISTING_GROUP,
	// 		group: simpleGroup
	// 	};
	// 	const expectedManageGroupAction = {
	// 		type: types.MANAGE_GROUP,
	// 		group: simpleGroup,
	// 		memberTabMode: 'manage'
	// 	};
	// 	actions.manageGroup(simpleGroup, 'manage')(dispatch);
	// 	expect(dispatch.getCall(0).args[0]).toEqual(expectedLoadGroupAction);
	// 	expect(dispatch.getCall(1).args[0]).toEqual(expectedManageGroupAction);
	// 	expect(dispatch).toHaveBeenCalledTimes(2);
	// });

	it('should have a sortTalentPools action that dispatches', () => {
		const dispatch = jest.fn();
		const expectedActionAsc = {
			type: types.TALENT_POOL_SORT_CHANGED,
			sortedTalentPools: [
				{
					name: '123',
					count: '1'
				},
				{
					name: 'aaa',
					count: '2'
				},
				{
					name: 'zzz',
					count: '3'
				}
			],
			fieldName: 'name',
			sortDirection: 'asc'
		};

		const expectedActionDesc = {
			type: types.TALENT_POOL_SORT_CHANGED,
			sortedTalentPools: [
				{
					name: 'zzz',
					count: '3'
				},
				{
					name: 'aaa',
					count: '2'
				},
				{
					name: '123',
					count: '1'
				}
			],
			fieldName: 'name',
			sortDirection: 'desc'
		};

		const talentPools = [
			{
				name: 'zzz',
				count: '3'
			},
			{
				name: '123',
				count: '1'
			},
			{
				name: 'aaa',
				count: '2'
			}
		];

		actions.sortTalentPools(talentPools, 'name', 'asc', false, false)(dispatch);
		expect(dispatch).toHaveBeenCalledWith(expectedActionAsc);

		actions.sortTalentPools(talentPools, 'name', 'desc', false, false)(dispatch);
		expect(dispatch).toHaveBeenCalledWith(expectedActionDesc);

		actions.sortTalentPools(talentPools, 'count', 'asc', true, false)(dispatch);
		expect(dispatch).toHaveBeenCalledWith(expectedActionAsc);

		actions.sortTalentPools(talentPools, 'count', 'desc', true, false)(dispatch);
		expect(dispatch).toHaveBeenCalledWith(expectedActionDesc);

		actions.sortTalentPools(talentPools, 'name', 'asc', false, true)(dispatch);
		expect(dispatch).toHaveBeenCalledWith(expectedActionDesc);
	});

	it('should create a talent pool sort changed action', () => {
		const expectedAction = {
			type: types.TALENT_POOL_SORT_CHANGED,
			sortedTalentPools: [
				{
					name: 'aaa',
					count: '3'
				}
			],
			fieldName: 'count',
			sortDirection: 'asc'
		};

		const talentPools = [
			{
				name: 'aaa',
				count: '3'
			}
		];

		expect(actions.talentPoolSortChanged(talentPools, 'count', 'asc')).toEqual(expectedAction);
	});

	it('should have a removeOrDecline action that dispatches', (done) => {
		setupMockPostServer({
			url: '/groups/123/remove_users',
			response: {
				data: {
					result: {
						success: true
					}
				}
			}
		});
		mockStore();
		const dispatch = jest.fn();
		const expectedAction = {
			type: types.REMOVE_DECLINE_SUCCESS,
			users: [
				123
			]
		};

		return actions.removeOrDecline(123, [123], 'http://localhost:8080')(dispatch)
		.then(() => {
			try {
				expect(dispatch).toHaveBeenCalledWith(expectedAction);
				done();
			} catch (error) {
				done(error);
			}
		});
	});

	it('should have an uninvite action that dispatches', (done) => {
		setupMockPostServer({
			url: '/groups/123/cancel_invitations',
			response: {
				data: {
					result: {
						success: true
					}
				}
			}
		});
		mockStore();
		const dispatch = jest.fn();
		const expectedAction = {
			type: types.UNINVITE_SUCCESS,
			users: [
				123
			]
		};

		return actions.uninvite(123, [123], 'http://localhost:8080')(dispatch)
		.then(() => {
			try {
				expect(dispatch).toHaveBeenCalledWith(expectedAction);
				done();
			} catch (error) {
				done(error);
			}
		});
	});

	it('should have an approve action that dispatches', (done) => {
		setupMockPostServer({
			url: '/groups/123/approve_users',
			response: {
				data: {
					result: {
						success: true
					}
				}
			}
		});
		mockStore();
		const dispatch = jest.fn();
		const expectedAction = {
			type: types.APPROVE_SUCCESS
		};

		return actions.approve(123, [123], 'http://localhost:8080')(dispatch)
		.then(() => {
			try {
				expect(dispatch).toHaveBeenCalledWith(expectedAction);
				done();
			} catch (error) {
				done(error);
			}
		});
	});

	it('should have a downloadDocumentation action that dispatches', (done) => {
		setupMockPostServer({
			url: '/groups/123/documentations',
			response: {
				data: {
					result: {
						success: true
					}
				}
			}
		});
		mockStore();
		const dispatch = jest.fn();
		const expectedAction = {
			type: types.DOWNLOAD_DOCUMENTATION_SUCCESS
		};

		return actions.downloadDocumentation(123, [{ userNumber: 123 }], 'http://localhost:8080')(dispatch)
		.then(() => {
			try {
				expect(dispatch).toHaveBeenCalledWith(expectedAction);
				done();
			} catch (error) {
				done(error);
			}
		});
	});

	it('should have a handleMemberPagination action that dispatches', () => {
		mockStore();
		const dispatch = jest.fn();
		const expectedAction = {
			type: types.MEMBER_PAGINATION_CHANGED,
			page: 2
		};

		actions.handleMemberPagination('next', 1, 25, 50)(dispatch);
		expect(dispatch).toHaveBeenCalledWith(expectedAction);
	});
});

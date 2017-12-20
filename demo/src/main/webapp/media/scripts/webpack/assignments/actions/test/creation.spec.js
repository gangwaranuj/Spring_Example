/* global describe, it, before, after, afterEach */

import 'isomorphic-fetch';
import nock from 'nock';
import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';
import * as actions from '../creation';
import { sendAssignmentAnalytics } from '../creation';

const middlewares = [ thunk ];
const mockStore = configureMockStore(middlewares);

describe('Assignment Creation Actions ::', () => {

	let analyticsSpy;

	const getMockState = (config) => {
		const blankState = {
			configuration: {
				uniqueExternalIdEnabled: false,
				customFieldsEnabled: false,
				shipmentsEnabled: false
			},
			location: {
				contact: {},
				secondaryContact: {}
			},
			customFieldGroups: []
		};

		return Object.assign({}, blankState, config);
	};

	beforeAll(() => {
		const analytics = { track: () => {} };
		global.window.analytics = analytics;
		analyticsSpy = jest.fn();
	});

	afterAll(() => {
		global.window.analytics = null;
		analyticsSpy.mockReset();
	});

	describe('Fetch Template ::', () => {

		const setupMockServer = (options) => {
			return nock('http://localhost:8080')
				.get(/(\/employer\/v2\/assignments\/templates\/).*/)
				.reply(200, options.response);
		};

		afterEach(() => {
			nock.cleanAll();
		});

		xit('should dispatch HYDRATE_STATE action with template state', () => {
			const templateId = '1017';
			const mockState = getMockState();
			const mockServer = setupMockServer({
				response: {
					results: [
						{
							id: templateId,
							assignment: mockState
						}
					]
				}
			});
			const store = mockStore();
			const expectedActions = [
				{ type: 'HYDRATE_STATE', state: mockState }
			];

			return store.dispatch(actions.fetchTemplate(templateId, 'http://localhost:8080/employer/v2/assignments/templates'))
				.then(() => {
					expect(store.getActions()).toEqual(expectedActions);
				});
		});

		xit('should call analytics function when template is loaded', () => {
			const templateId = '1017';
			const mockState = getMockState({
				id: templateId
			});
			const mockServer = setupMockServer({
				response: {
					results: [
						{
							id: templateId,
							assignment: mockState
						}
					]
				}
			});

			const store = mockStore();
			const analyticsMessage = 'Assignment Creation';
			const analyticsProps = {
				version: '2',
				action: 'Template loaded',
				templateId,
				success: true
			};

			return store.dispatch(actions.fetchTemplate(templateId, 'http://localhost:8080/employer/v2/assignments/templates'))
				.then(() => {
					expect(analyticsSpy).toHaveBeenCalledWith(analyticsMessage, analyticsProps);
				});
		});
	});

	describe('Save methods ::', () => {
		describe('Analytics ::', () => {

			afterEach(() => {
				analyticsSpy.mockReset();
			});

			xit('should fire analytics', () => {
				const analyticsMessage = 'Assignment Creation';
				const analyticsProps = { assignmentId: 37 };
				sendAssignmentAnalytics(analyticsProps);
				expect(analyticsSpy).toHaveBeenCalledWith(analyticsMessage, analyticsProps);
			});
		});
	});
});

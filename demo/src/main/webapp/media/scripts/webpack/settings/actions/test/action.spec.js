/* eslint no-unused-vars: ["error", { "varsIgnorePattern": "mockServer" }]*/
import nock from 'nock';
import { Map } from 'immutable';
import { initialFundsState } from '../../reducers/fundsReducer';
import { initialTaxState } from '../../reducers/taxReducer';
import { initialAddEmployeeState } from '../../reducers/addEmployeeReducer';
import { initialAssignmentPreferencesState } from '../../reducers/assignmentPreferencesReducer';
import * as actions from '../index';
import * as types from '../../constants/actionTypes';

const files = Map({
	'd60c8fc3-13e8-4329-b48e-fa4c7564684d': Map({
		file: new File(
			['The first file.'],
			'filename-1.txt',
			{
				type: 'text/plain',
				lastModified: new Date()
			}
		),
		progress: 13,
		abort: () => {}
	}),
	'e60c8fc3-13e8-4329-b48e-fa4c7564684d': Map({
		file: new File(
			['The second file.'],
			'filename-2.txt',
			{
				type: 'text/plain',
				lastModified: new Date()
			}
		),
		progress: 100,
		abort: () => {}
	}),
	'f60c8fc3-13e8-4329-b48e-fa4c7564684d': Map({
		file: new File(
			['The third file.'],
			'filename-3.txt',
			{
				type: 'text/plain',
				lastModified: new Date()
			}
		),
		progress: false,
		abort: () => {},
		error: ['You have an error! Deal with it!']
	})
});

describe('Settings Form Actions', () => {
	const setupMockServer = (options) => {
		return nock('http://localhost:8080')
			.get(/(\/employer\/v2\/settings\/).*/)
			.reply(200, options.response)
			.post(/(\/users\/import\/).*/)
			.reply(200, options.response)
			.post(/(\/employer\/v2\/settings\/).*/)
			.reply(200, options.response)
			.post(/(\/v2\/employer\/assignments\/).*/)
			.reply(200, options.response);
	};

	afterEach(() => {
		nock.cleanAll();
	});

	it('should create a change field action', () => {
		const firstName = 'Cowboy Tim';
		const expectedAction = {
			type: types.CHANGE_FIELD,
			name: 'firstName',
			value: firstName
		};

		expect(actions.changeField('firstName', firstName)).toEqual(expectedAction);
	});

	it('should create a change location field action', () => {
		const value = '123 Easy Street';
		const expectedAction = {
			type: types.CHANGE_LOCATION_FIELD,
			name: 'addressLine1',
			value
		};

		expect(actions.changeLocationField('addressLine1', value)).toEqual(expectedAction);
	});

	it('should create a change funds field action', () => {
		const value = 'Bank of Texas';
		const expectedAction = {
			type: types.CHANGE_FUNDS_FIELD,
			name: 'bankName',
			value
		};

		expect(actions.changeFundsField('bankName', value)).toEqual(expectedAction);
	});

	it('should create a change tax field action', () => {
		const value = 'Cinco Corporation';
		const expectedAction = {
			type: types.CHANGE_TAX_FIELD,
			name: 'lastName',
			value
		};

		expect(actions.changeTaxField('lastName', value)).toEqual(expectedAction);
	});

	it('should create a blur tax field action', () => {
		const value = 'Cinco Corporation';
		const expectedAction = {
			type: types.BLUR_TAX_FIELD,
			name: 'lastName',
			value
		};

		expect(actions.blurTaxField('lastName', value)).toEqual(expectedAction);
	});

	it('should create a change credit card field action', () => {
		const value = 'Eliseo Barrow';
		const expectedAction = {
			type: types.CHANGE_CREDIT_CARD_FIELD,
			name: 'nameOnCard',
			value
		};

		expect(actions.changeCreditCardField('nameOnCard', value)).toEqual(expectedAction);
	});

	it('should create a change add employee field action', () => {
		const value = 'Gisela';
		const expectedAction = {
			type: types.CHANGE_ADD_EMPLOYEE_FIELD,
			name: 'firstName',
			value
		};

		expect(actions.changeAddEmployeeField('firstName', value)).toEqual(expectedAction);
	});

	it('should have a googleAPILoaded action that dispatches GOOGLE_INITIALIZED', () => {
		const expectedAction = {
			type: types.GOOGLE_INITIALIZED
		};
		const dispatch = jest.fn();

		actions.googleAPILoaded()(dispatch);
		expect(dispatch).toHaveBeenCalledWith(expectedAction);
	});

	it('should have a receiveOnboardingProgress action that dispatches progress actions', () => {
		const expectedAction = {
			type: types.PROFILE_FORM_SUBMIT_SUCCESS
		};
		const progressInfo = {
			completedActions: [
				'OVERVIEW'
			]
		};
		const dispatch = jest.fn();
		actions.receiveOnboardingProgress(progressInfo)(dispatch);
		expect(dispatch).toHaveBeenCalledWith(expectedAction);

		dispatch.mockReset();
		progressInfo.completedActions.push('TAX', 'BANK', 'ASSIGNMENT_SETTINGS');
		actions.receiveOnboardingProgress(progressInfo)(dispatch);
		expect(dispatch).toHaveBeenCalledTimes(4);
	});

	describe('onSubmitFundsForm', () => {
		const submitUrl = 'http://localhost:8080/employer/v2/settings/funds/accounts';
		it('should dispatch FUNDS_FORM_SUBMIT_REQUEST action', () => {
			const mockServer = setupMockServer({
				response: {}
			});
			const dispatch = jest.fn();
			actions.onSubmitFundsForm(initialFundsState, submitUrl)(dispatch);
			expect(dispatch).toHaveBeenCalledWith({
				type: types.FUNDS_FORM_SUBMIT_REQUEST
			});
		});

		it('should submit form data to funds endpoint', () => {
			const mockServer = setupMockServer({
				response: {}
			});
			const fundsState = initialFundsState;
			const dispatch = jest.fn();
			actions.onSubmitFundsForm(fundsState, submitUrl)(dispatch)
				.then(() => {
					try {
						mockServer.isDone();
					} catch (e) {
						throw e;
					}
				});
		});

		it('should dispatch success action if request successful', () => {
			const mockServer = setupMockServer({
				response: {},
				ok: true
			});
			const dispatch = jest.fn();
			const expectedAction = {
				type: types.FUNDS_FORM_SUBMIT_SUCCESS
			};
			actions.onSubmitFundsForm(initialFundsState, submitUrl)(dispatch)
				.then(() => {
					try {
						expect(dispatch).toHaveBeenCalledWith(expectedAction);
					} catch (e) {
						throw e;
					}
				});
		});

		it('should dispatch error action if request not successful', () => {
			const mockServer = nock('http://localhost:8080')
				.post(/(\/employer\/v2\/settings\/).*/)
				.reply(500, {
					results: [
						{
							message: 'You done messed up'
						}
					]
				});
			const dispatch = jest.fn();
			const errorAction = {
				type: types.FUNDS_FORM_SUBMIT_ERROR,
				error: ['You done messed up']
			};
			actions.onSubmitFundsForm(initialFundsState, submitUrl)(dispatch)
				.then(() => {
					try {
						expect(dispatch).toHaveBeenCalledTimes(2);
						expect(dispatch.getCall(1).args[0]).toEqual(errorAction);
					} catch (e) {
						throw e;
					}
				});
		});
	});

	describe('onSubmitTaxForm', () => {
		const submitUrl = 'http://localhost:8080/employer/v2/settings/tax';
		it('should dispatch TAX_FORM_SUBMIT_REQUEST action', () => {
			const mockServer = setupMockServer({
				response: {}
			});
			const dispatch = jest.fn();
			actions.onSubmitTaxForm(initialTaxState, submitUrl)(dispatch);
			expect(dispatch).toHaveBeenCalledWith({
				type: types.TAX_FORM_SUBMIT_REQUEST
			});
		});

		it('should submit form data to tax endpoint', () => {
			const mockServer = setupMockServer({
				response: {}
			});
			const taxState = initialTaxState;
			const dispatch = jest.fn();
			actions.onSubmitTaxForm(taxState, submitUrl)(dispatch)
				.then(() => {
					try {
						mockServer.isDone();
					} catch (e) {
						throw e;
					}
				});
		});

		it('should dispatch success action if request successful', () => {
			const mockServer = setupMockServer({
				response: {},
				ok: true
			});
			const dispatch = jest.fn();
			const expectedAction = {
				type: types.TAX_FORM_SUBMIT_SUCCESS
			};
			actions.onSubmitTaxForm(initialTaxState, submitUrl)(dispatch)
				.then(() => {
					try {
						expect(dispatch).toHaveBeenCalledWith(expectedAction);
					} catch (e) {
						throw e;
					}
				});
		});

		it('should dispatch error action if request not successful', () => {
			const mockServer = nock('http://localhost:8080')
				.post(/(\/employer\/v2\/settings\/).*/)
				.reply(500, {
					results: [
						{
							message: 'You done messed up'
						}
					]
				});
			const dispatch = jest.fn();
			const errorAction = {
				type: types.TAX_FORM_SUBMIT_ERROR,
				error: ['You done messed up']
			};
			actions.onSubmitTaxForm(initialTaxState, submitUrl)(dispatch)
				.then(() => {
					try {
						expect(dispatch).toHaveBeenCalledTimes(2);
						expect(dispatch.getCall(1).args[0]).toEqual(errorAction);
					} catch (e) {
						throw e;
					}
				});
		});
	});

	describe('onSubmitAddEmployeeForm', () => {
		const submitUrl = 'http://localhost:8080/employer/v2/settings/users';

		it('should dispatch ADD_EMPLOYEE_FORM_SUBMIT_REQUEST action', () => {
			const mockServer = setupMockServer({
				response: {}
			});
			const dispatch = jest.fn();
			const getState = jest.fn(() => ({ addEmployee: Map(initialAddEmployeeState) }));

			actions.onSubmitAddEmployeeForm(initialAddEmployeeState, submitUrl)(dispatch, getState);
			expect(dispatch).toHaveBeenCalledWith({
				type: types.ADD_EMPLOYEE_FORM_SUBMIT_REQUEST
			});
		});

		it('should submit form data to add employee endpoint', () => {
			const mockServer = setupMockServer({
				response: {}
			});
			const addEmployeeState = Map(initialAddEmployeeState);
			const dispatch = jest.fn();
			const getState = jest.fn(() => ({ addEmployee: addEmployeeState }));

			actions.onSubmitAddEmployeeForm(addEmployeeState, submitUrl)(dispatch, getState)
				.then(() => {
					try {
						mockServer.isDone();
					} catch (e) {
						throw e;
					}
				});
		});

		it('should dispatch success action if request successful', () => {
			const mockServer = setupMockServer({
				response: {},
				ok: true
			});
			const addEmployeeState = Map(initialAddEmployeeState);
			const dispatch = jest.fn();
			const getState = jest.fn(() => ({ addEmployee: addEmployeeState }));
			const expectedAction = {
				type: types.ADD_EMPLOYEE_FORM_SUBMIT_SUCCESS
			};
			actions.onSubmitAddEmployeeForm(initialTaxState, submitUrl)(dispatch, getState)
				.then(() => {
					try {
						dispatch.should.have.been.calledWith(expectedAction);
					} catch (e) {
						throw e;
					}
				});
		});

		it('should dispatch error action if request not successful', () => {
			const mockServer = nock('http://localhost:8080')
				.post(/(\/employer\/v2\/settings\/).*/)
				.reply(500, {
					results: [
						{
							message: 'There was a problem submitting your request'
						}
					]
				});
			const addEmployeeState = Map(initialAddEmployeeState);
			const dispatch = jest.fn();
			const getState = jest.fn(() => ({ addEmployee: addEmployeeState }));
			const errorAction = {
				type: types.ADD_EMPLOYEE_FORM_SUBMIT_ERROR,
				error: ['There was a problem submitting your request']
			};
			actions.onSubmitAddEmployeeForm(initialTaxState, submitUrl)(dispatch, getState)
				.then(() => {
					try {
						dispatch.should.have.been.calledTwice();
						dispatch.getCall(1).args[0].should.deep.equal(errorAction);
					} catch (e) {
						throw e;
					}
				});
		});
	});

	describe('processBulkUpload', () => {
		const submitUrl = 'http://localhost:8080/users/import';
		const id = 1;
		const uuid = 'd60c8fc3-13e8-4329-b48e-fa4c7564684d';

		it('should dispatch BULK_PROCESS_EMPLOYEES_START action', () => {
			const mockServer = setupMockServer({
				response: {}
			});
			const dispatch = jest.fn();

			actions.processBulkUpload(id, uuid, submitUrl)(dispatch);
			expect(dispatch).toHaveBeenCalledWith({
				type: types.BULK_PROCESS_EMPLOYEES_START,
				id,
				uuid
			});
		});

		it('should submit an `id` and `uuid`', () => {
			const mockServer = setupMockServer({
				response: {}
			});
			const dispatch = jest.fn();

			actions.processBulkUpload(id, uuid, submitUrl)(dispatch)
				.then(() => {
					try {
						mockServer.isDone();
					} catch (e) {
						throw e;
					}
				});
		});

		it('should dispatch success action if request successful', () => {
			const mockServer = setupMockServer({
				response: {},
				ok: true
			});
			const dispatch = jest.fn();
			const expectedAction = {
				type: types.BULK_PROCESS_EMPLOYEES_SUCCESS,
				id
			};
			actions.processBulkUpload(id, uuid, submitUrl)(dispatch)
				.then(() => {
					try {
						expect(dispatch).toHaveBeenCalledWith(expectedAction);
					} catch (e) {
						throw e;
					}
				});
		});

		it('should dispatch error action if request not successful', () => {
			const errorMessage = 'There was an error in processing the file. Please upload the file again.';
			const mockServer = nock('http://localhost:8080')
				.post(/(\/users\/import\/).*/)
				.reply(500, {
					results: [
						{
							message: errorMessage
						}
					]
				});
			const dispatch = jest.fn();
			const errorAction = {
				type: types.BULK_UPLOAD_EMPLOYEES_ERROR,
				id,
				error: [errorMessage]
			};
			actions.processBulkUpload(id, uuid, submitUrl)(dispatch)
				.then(() => {
					try {
						expect(dispatch).toHaveBeenCalledTimes(2);
						expect(dispatch.getCall(1).args[0]).toEqual(errorAction);
					} catch (e) {
						throw e;
					}
				});
		});
	});

	describe('cancelBulkUpload', () => {
		it('should dispatch BULK_UPLOAD_EMPLOYEES_ABORT action', () => {
			const id = 'd60c8fc3-13e8-4329-b48e-fa4c7564684d';
			const dispatch = jest.fn();
			const getState = jest.fn(() => ({ files }));

			actions.cancelBulkUpload(id)(dispatch, getState);
			expect(dispatch).toHaveBeenCalledWith({
				type: types.BULK_UPLOAD_EMPLOYEES_ABORT,
				id
			});
		});
	});

	describe('onSubmitAssignmentPreferences', () => {
		const submitUrl = 'http://localhost:8080/v2/employer/assignments/configuration';

		it('should dispatch ASSIGNMENT_PREFERENCES_FORM_SUBMIT_REQUEST action', () => {
			const mockServer = setupMockServer({
				response: {}
			});
			const dispatch = jest.fn();
			const getState = jest.fn(() => ({
				assignmentPreferences: Map(initialAssignmentPreferencesState)
			}));

			actions.onSubmitAssignmentPreferences(submitUrl)(dispatch, getState);
			expect(dispatch).toHaveBeenCalledWith({
				type: types.ASSIGNMENT_PREFERENCES_FORM_SUBMIT_REQUEST
			});
		});

		it('should submit form data to add employee endpoint', () => {
			const mockServer = setupMockServer({
				response: {}
			});
			const assignmentPreferencesState = Map(initialAssignmentPreferencesState);
			const dispatch = jest.fn();
			const getState = jest.fn(() => ({
				assignmentPreferences: assignmentPreferencesState
			}));

			actions.onSubmitAssignmentPreferences(submitUrl)(dispatch, getState)
				.then(() => {
					try {
						mockServer.isDone();
					} catch (e) {
						throw e;
					}
				});
		});

		it('should dispatch success action if request successful', () => {
			const mockServer = setupMockServer({
				response: {},
				ok: true
			});
			const assignmentPreferencesState = Map(initialAssignmentPreferencesState);
			const dispatch = jest.fn();
			const getState = jest.fn(() => ({
				assignmentPreferences: assignmentPreferencesState
			}));
			const expectedAction = {
				type: types.ASSIGNMENT_PREFERENCES_FORM_SUBMIT_SUCCESS
			};
			actions.onSubmitAssignmentPreferences(submitUrl)(dispatch, getState)
				.then(() => {
					try {
						expect(dispatch).toHaveBeenCalledWith(expectedAction);
					} catch (e) {
						throw e;
					}
				});
		});

		it('should dispatch error action if request not successful', () => {
			const mockServer = nock('http://localhost:8080')
				.post(/(\/v2\/employer\/assignments\/).*/)
				.reply(500, {
					results: [
						{
							message: 'There was a problem submitting your request'
						}
					]
				});
			const assignmentPreferencesState = Map(initialAssignmentPreferencesState);
			const dispatch = jest.fn();
			const getState = jest.fn(() => ({
				assignmentPreferences: assignmentPreferencesState
			}));
			const errorAction = {
				type: types.ASSIGNMENT_PREFERENCES_FORM_SUBMIT_ERROR,
				error: ['There was a problem submitting your request']
			};
			actions.onSubmitAssignmentPreferences(submitUrl)(dispatch, getState)
				.then(() => {
					try {
						expect(dispatch).toHaveBeenCalledTimes(2);
						expect(dispatch.getCall(1).args[0]).toEqual(errorAction);
					} catch (e) {
						throw e;
					}
				});
		});
	});
});

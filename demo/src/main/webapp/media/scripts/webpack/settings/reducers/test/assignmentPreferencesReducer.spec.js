import { Map } from 'immutable';
import assignmentPreferencesReducer, { initialAssignmentPreferencesState } from '../assignmentPreferencesReducer';
import * as types from '../../constants/actionTypes';

const assignmentPreferencesData = {
	paymentTermsDays: 7,
	termsOfAgreementEnabled: false,
	termsOfAgreement: '',
	codeOfConductEnabled: false,
	codeOfConduct: '',
	autoCloseEnabled: false,
	autoRateEnabled: false,
	printSettingsEndUserTermsEnabled: false,
	printSettingsSignatureEnabled: false,
	printSettingsBadgeEnabled: false,
	printSettingsLogoOption: 'string',
	printSettingsEndUserTerms: 'string',
	printSettingsSignature: 'string',
	customFieldsEnabled: false,
	shipmentsEnabled: false,
	requirementSetsEnabled: false,
	deliverablesEnabled: false,
	surveysEnabled: false,
	uniqueExternalIdEnabled: false,
	uniqueExternalIdDisplayName: 'string',
	followersEnabled: false,
	instantNetworkEnabled: false,
	useWorkMarketPrintout: false,
	projectBudgetManagementEnabled: false,
	requireClientProjectEnabled: false,
	workerRequirementsEnabled: false,
	autoCloseHours: 'string',
	ivrEnabled: false,
	agingAssignmentsEnabled: false,
	agingAssignmentsEmailAddress: 'string'
};

describe('Assignment Preferences Reducer', () => {
	const initialState = Map(initialAssignmentPreferencesState);

	it('should return the initial state', () => {
		expect(assignmentPreferencesReducer(undefined, {})).toEqual(initialState);
	});

	it('should handle `CHANGE_ASSIGNMENT_PREFERENCES_FIELD` action', () => {
		const value = 'Test me!';
		const action = {
			type: types.CHANGE_ASSIGNMENT_PREFERENCES_FIELD,
			name: 'value',
			value
		};
		const state = initialState.set(
			'value',
			value
		);

		expect(assignmentPreferencesReducer(undefined, action)).toEqual(state);
	});

	it('should handle `GET_ASSIGNMENT_PREFERENCES_SUCCESS` action', () => {
		const action = {
			type: types.GET_ASSIGNMENT_PREFERENCES_SUCCESS,
			value: assignmentPreferencesData
		};
		const modifiedState = Map(assignmentPreferencesData);

		expect(assignmentPreferencesReducer(initialState, action)).toEqual(modifiedState);
	});
});

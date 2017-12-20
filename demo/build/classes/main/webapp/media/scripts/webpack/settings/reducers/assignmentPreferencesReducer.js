import { Map } from 'immutable';
import * as types from '../constants/actionTypes';

const initialAssignmentPreferencesState = Map({
	paymentTermsDays: 0,
	termsOfAgreement: '',
	codeOfConduct: '',
	printSettingsEndUserTermsEnabled: false,
	printSettingsSignatureEnabled: false,
	printSettingsBadgeEnabled: false,
	printSettingsLogoOption: 'wm',
	useWorkMarketPrintout: true
});
export { initialAssignmentPreferencesState };

const assignmentPreferences = (
	state = Map(initialAssignmentPreferencesState),
	{ type, name, value }
) => {
	switch (type) {
	case types.CHANGE_ASSIGNMENT_PREFERENCES_FIELD:
		return state.set(name, value);

	case types.GET_ASSIGNMENT_PREFERENCES_SUCCESS:
		return Map(value);

	default:
		return state;
	}
};

export default assignmentPreferences;

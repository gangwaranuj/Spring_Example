import Immutable from 'immutable';
import CompanyProfile from './models/CompanyProfile';
import * as types from '../constants/actionTypes';

const initialCompanyProfileState = new CompanyProfile({});

const companyProfile = (
	state = initialCompanyProfileState,
	{
		type,
		name,
		value
	},
) => {
	switch (type) {
	case types.RETRIEVE_PROFILE_SUCCESS:
		if (!value) { return state; }
		// clean out null values and set to default values prior to returning new state
		return state.mergeDeepWith((prev, next) => {
			return !next ? prev : next;
		}, new CompanyProfile(Immutable.fromJS(value)));
	case types.CHANGE_FIELD:
		return state
				.setIn(name.split('.'), value);
	default:
		return state;
	}
};

export { initialCompanyProfileState };
export default companyProfile;

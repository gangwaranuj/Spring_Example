import { combineReducers } from 'redux';
import { Map } from 'immutable';
import onboardingProgress, { initialOnboardingProgressState } from './onboardingProgressReducer';

const initialState = Map({});
initialState.onboardingProgress = initialOnboardingProgressState;

export { initialState };

export default combineReducers({
	onboardingProgress
});

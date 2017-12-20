import { Map } from 'immutable';
import { onboardingProgressBarActionTypes as actions } from '../constants';

const initialOnboardingProgressState = Map({
	progress: 0
});

export { initialOnboardingProgressState };

const onboardingProgress = (state = initialOnboardingProgressState, { type, value }) => {
	switch (type) {
	case actions.RECEIVE_ONBOARDING_PROGRESS:
		return state
			.set('progress', value);
	default:
		return state;
	}
};

export default onboardingProgress;

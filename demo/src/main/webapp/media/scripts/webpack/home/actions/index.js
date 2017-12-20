import fetch from 'isomorphic-fetch';
import { onboardingProgressBarActionTypes as actionTypes } from '../constants';

export const getOnboardingProgress = (
	completenessUrl = '/employer/v2/settings/completeness_percentage'
) => (dispatch) => {
	return fetch(completenessUrl, {
		credentials: 'same-origin'
	})
		.then(res => res.json())
		.then((res) => {
			const percentage = res.results[0].percentage;
			dispatch({
				type: actionTypes.RECEIVE_ONBOARDING_PROGRESS,
				value: percentage
			});
		});
};

const actions = {
	getOnboardingProgress
};

export default actions;

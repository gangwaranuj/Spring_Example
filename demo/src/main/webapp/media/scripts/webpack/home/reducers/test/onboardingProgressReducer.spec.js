import { Map } from 'immutable';
import onboardingProgress, { initialOnboardingProgressState } from '../onboardingProgressReducer';
import { onboardingProgressBarActionTypes as actionTypes } from '../../constants';

describe('Onboarding Progress Bar Reducer ::', () => {
	describe('Initial State', () => {
		it('should return an Immutable.js Map', () => {
			expect(initialOnboardingProgressState).toBeInstanceOf(Map);
		});

		it('should return progress as 0', () => {
			expect(initialOnboardingProgressState.get('progress')).toEqual(0);
		});
	});

	describe('Reducer', () => {
		const getState = () => {
			return Map(initialOnboardingProgressState);
		};

		it('should return initial state', () => {
			expect(onboardingProgress(undefined, {})).toEqual(getState());
		});

		it('should handle RECEIVE_ONBOARDING_PROGRESS action', () => {
			const progress = 20;
			const action = {
				type: actionTypes.RECEIVE_ONBOARDING_PROGRESS,
				value: progress
			};
			let state = getState();
			state = state.set('progress', progress);
			expect(onboardingProgress(undefined, action)).toEqual(state);
		});
	});
});

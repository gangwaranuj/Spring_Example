/* eslint no-unused-vars: ["error", { "varsIgnorePattern": "mockServer" }]*/
import nock from 'nock';
import { getOnboardingProgress } from '../index';
import { onboardingProgressBarActionTypes as actionTypes } from '../../constants';

describe('Onboarding Progress Bar Actions ::', () => {
	const setupMockServer = (options) => {
		return nock('http://localhost:8080')
			.get(/(\/employer\/v2\/settings\/).*/)
			.reply(200, options.response);
	};

	afterEach(() => {
		nock.cleanAll();
	});

	it('should have a getOnboardingProgress action that dispatches RECEIVE_ONBOARDING_PROGRESS action', (done) => {
		const mockServer = setupMockServer({
			response: {
				results: [
					{
						percentage: 25
					}
				]
			}
		});
		const dispatch = jest.fn();
		getOnboardingProgress('http://localhost:8080/employer/v2/settings/completeness_percentage')(dispatch)
		.then(() => {
			try {
				expect(dispatch).toHaveBeenCalledWith({
					type: actionTypes.RECEIVE_ONBOARDING_PROGRESS,
					value: 25
				});
				done();
			} catch (e) {
				done(e);
			}
		});
	});
});

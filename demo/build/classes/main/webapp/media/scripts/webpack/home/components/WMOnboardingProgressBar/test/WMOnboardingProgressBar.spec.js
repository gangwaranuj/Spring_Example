import React from 'react';
import { shallow } from 'enzyme';
import { Map } from 'immutable';
import {
	WMPaper,
	WMFontIcon,
	WMRaisedButton,
	WMLinearProgress
} from '@workmarket/front-end-components';
import WMOnboardingProgressBar from '../template';

describe('<WMOnboardingProgressBar /> ::', () => {
	const shallowRenderComponent = (
		getOnboardingProgress = () => {},
		onboardingProgress = Map({
			progress: 20
		})
	) => shallow(
		<WMOnboardingProgressBar
			getOnboardingProgress={ getOnboardingProgress }
			onboardingProgress={ onboardingProgress }
		/>
	);

	describe('Rendering ::', () => {
		it('should render a <WMPaper />', () => {
			const wrapper = shallowRenderComponent();
			const paper = wrapper.find(WMPaper);
			expect(paper).toHaveLength(1);
			expect(paper.prop('rounded')).toBeTruthy();
		});

		it('should render a info <WMFontIcon />', () => {
			const wrapper = shallowRenderComponent();
			const icon = wrapper.find(WMFontIcon);
			expect(icon).toBeDefined();
			expect(icon.prop('children')).toEqual('play_circle_outline');
			expect(icon.prop('color')).toEqual('#53b3f3');
			expect(icon.prop('id')).toEqual('home__play-icon-welcome');
			expect(icon.prop('className')).toEqual('material-icons');
		});

		it('should render text describing percentage of onboarding completed', () => {
			let expectedText = 'Your account configuration is 20% Complete';
			const wrapper = shallowRenderComponent();
			let text = wrapper.find('#home__onboardingPercentageCompleteText');
			expect(text.text()).toEqual(expectedText);
			const newProgress = Map({
				progress: 40
			});
			wrapper.setProps({
				onboardingProgress: newProgress
			});
			expectedText = 'Your account configuration is 40% Complete';
			text = wrapper.find('#home__onboardingPercentageCompleteText');
			expect(text.text()).toEqual(expectedText);
		});

		it('should render a button that links to Onboarding', () => {
			const wrapper = shallowRenderComponent();
			const button = wrapper.find(WMRaisedButton);
			expect(button).toBeDefined();
			expect(button.prop('href')).toEqual('/settings/onboarding');
			expect(button.prop('label')).toEqual('COMPLETE SETUP');
			expect(button.prop('backgroundColor')).toEqual('#5BC75D');
			expect(button.prop('linkButton')).toEqual(true);
		});

		it('should render a <WMLinearProgress /> bar', () => {
			const wrapper = shallowRenderComponent();
			let bar = wrapper.find(WMLinearProgress);
			expect(bar).toBeDefined();
			expect(bar.prop('mode')).toEqual('determinate');
			expect(bar.prop('value')).toEqual(20);
			const newProgress = Map({
				progress: 40
			});
			wrapper.setProps({
				onboardingProgress: newProgress
			});
			bar = wrapper.find(WMLinearProgress);
			expect(bar.prop('value')).toEqual(40);
		});

		it('should render empty div if onboardingProgress is 0', () => {
			const wrapper = shallowRenderComponent(
				() => {},
				Map({
					progress: 0
				})
			);
			const paper = wrapper.find(WMPaper);
			expect(paper).toHaveLength(0);
		});

		it('should render empty div if onboardingProgress is 100', () => {
			const wrapper = shallowRenderComponent(
				() => {},
				Map({
					progress: 100
				})
			);
			const paper = wrapper.find(WMPaper);
			expect(paper).toHaveLength(0);
		});
	});

	it('should call getOnboardingProgress on mount', () => {
		const getProgressSpy = jest.fn();
		const wrapper = shallowRenderComponent(getProgressSpy);
		wrapper.instance().componentWillMount();
		expect(getProgressSpy).toHaveBeenCalled();
	});
});

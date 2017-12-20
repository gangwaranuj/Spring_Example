/* eslint-disable jest/no-disabled-tests */
import PropTypes from 'prop-types';
import React from 'react';
import { mount, shallow } from 'enzyme';
import { WMCheckList } from '../';
import configureStore from '../../../configureStore';

describe('<WMChecklist />', () => {
	const deepRenderComponent = (
		getOnboardingProgress = () => {}
	) => mount(
		<WMCheckList
			getOnboardingProgress={ getOnboardingProgress }
		/>
	, {
		context: {
			state: PropTypes.func,
			store: configureStore()
		},
		childContextTypes: {
			state: PropTypes.func,
			store: PropTypes.func,
			dispatch: PropTypes.func
		}
	});

	const shallowRenderComponent = () =>
		shallow(
			<WMCheckList />
		);

	describe('Rendering ::', () => {
		xit('should contain a <h3 />', () => {
			const wrapper = shallowRenderComponent();
			const element = wrapper.find('h3');
			expect(element.length).toEqual(1);
		});

		xit('should contain a <p/>', () => {
			const wrapper = shallowRenderComponent();
			const element = wrapper.find('p');
			expect(element.length).toEqual(1);
		});

		xit('should call getOnboardingProgress on mount', () => {
			jest.fn(WMCheckList.prototype, 'render');
			const progressSpy = jest.fn();
			deepRenderComponent(progressSpy);
			expect(progressSpy).toHaveBeenCalled();
		});
	});
});

/* eslint-disable jest/no-disabled-tests */
import React from 'react';
import { shallow } from 'enzyme';
import {
	commonStyles,
	WMPaper,
	WMRaisedButton,
	WMFontIcon
} from '@workmarket/front-end-components';
import WMSettingsAssignment from '../';

const { baseColors } = commonStyles.colors;

describe('<WMSettingsAssignment />', () => {
	const shallowRenderComponent = () => shallow(
		<WMSettingsAssignment />
	);

	describe('Rendering ::', () => {
		xit('should be wrapped in <WMPaper />', () => {
			const wrapper = shallowRenderComponent();
			const component = wrapper.find(WMPaper);
			expect(component.type()).toEqual(WMPaper);
		});

		xit('should have a <WMRaisedButton />', () => {
			const wrapper = shallowRenderComponent();
			const button = wrapper.find(WMRaisedButton);
			expect(button.prop('href')).toEqual('/settings/onboarding/assignment-preferences');
			expect(button.prop('label')).toEqual('SETTINGS');
			expect(button.prop('backgroundColor')).toEqual(baseColors.green);
			expect(button.prop('labelColor')).toEqual(baseColors.white);
			expect(button.prop('linkButton')).toEqual(true);
		});

		xit('should have a "settings_applications" <WMFontIcon />', () => {
			const wrapper = shallowRenderComponent();
			const icon = wrapper.find(WMFontIcon);
			expect(icon.type()).toEqual(WMFontIcon);
			expect(icon.prop('children')).toEqual('application_settings');
			expect(icon.prop('color')).toEqual(baseColors.grey);
		});
	});
});

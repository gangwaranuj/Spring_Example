import React from 'react';
import { shallow } from 'enzyme';
import WMSettingsEmployees from '../index';

describe('<WMSettingsEmployees />', () => {
	describe('Rendering ::', () => {
		it('renders correctly', () => {
			const component = shallow(
				<WMSettingsEmployees />
			);
			expect(component).toMatchSnapshot();
		});
	});
});

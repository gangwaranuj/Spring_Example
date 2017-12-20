import React from 'react';
import { shallow } from 'enzyme';
import WMWorkerBanner from '../index';

describe('<WMWorkerBanner />', () => {
	describe('Rendering ::', () => {
		it('renders correctly', () => {
			const component = shallow(
				<WMWorkerBanner
					invitationsCount={ 14 }
				/>
			);
			expect(component).toMatchSnapshot();
		});
	});
});

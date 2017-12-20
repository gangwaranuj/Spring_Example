import React from 'react';
import { shallow } from 'enzyme';
import { WMTextField } from '@workmarket/front-end-components';
import WMGoogleLocationInput from '../';

describe('<WMGoogleLocationInput /> ::', () => {
	const shallowRenderComponent = (
		googleInitialized = false,
		onGoogleAPILoaded = () => {},
		changeGoogleAddress = () => {}
	) =>
		shallow(
			<WMGoogleLocationInput
				googleInitialized={ googleInitialized }
				onGoogleAPILoaded={ onGoogleAPILoaded }
				changeGoogleAddress={ changeGoogleAddress }
			/>
		);

	describe('Rendering', () => {
		it('should render a <WMTextField />', () => {
			const wrapper = shallowRenderComponent();
			expect(wrapper.find(WMTextField).length).toEqual(1);
		});
	});
});

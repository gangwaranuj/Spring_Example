import React from 'react';
import { shallow } from 'enzyme';
import WMCompanyPagesSPA from 'wm-company-pages-spa';
import { WMRaisedButton, WMValidatingForm, WMValidatingTextField } from '@workmarket/front-end-components';
import LandingPage from '../template';


describe('<LandingPage />', () => {
	const renderComponent = () => shallow(
		<LandingPage
			encryptedId="1234"
			isInvitation={ false }
		/>
	);
	let wrapper;

	beforeEach(() => {
		wrapper = renderComponent();
	});

	describe('Rendering', () => {
		it('should have two WMRaisedButtons', () => {
			const component = wrapper.find(WMRaisedButton);
			expect(component.length).toEqual(2);
		});

		it('should have a WMValidatingForm', () => {
			const component = wrapper.find(WMValidatingForm);
			expect(component.length).toEqual(1);
		});

		it('should have 4 WMValidatingTextFields', () => {
			const component = wrapper.find(WMValidatingTextField);
			expect(component.length).toEqual(4);
		});

		it('should have a WMCompanyPagesSPA', () => {
			const component = wrapper.find(WMCompanyPagesSPA);
			expect(component.length).toEqual(1);
		});
	});
});

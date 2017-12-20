/* eslint-disable no-shadow */
import React from 'react';
import { shallow } from 'enzyme';
import WMSettingsTax from '../template';
import { initialTaxState } from '../../../reducers/taxReducer';
import { initialSettingsState } from '../../../reducers/settingsReducer';

describe('<WMSettingsTax />', () => {
	const info = {
		tax: initialTaxState,
		settings: initialSettingsState
	};
	const onSubmitForm = () => {};
	const onChangeField = () => {};
	const onBlurField = () => {};
	const formValid = true;

	const shallowRenderComponent = (
		info = {
			tax: initialTaxState,
			settings: initialSettingsState
		},
		onSubmitForm = () => {},
		onChangeField = () => {},
		onBlurField = () => {},
		formValid = true
	) => shallow(
		<WMSettingsTax
			info={ info }
			onSubmitForm={ onSubmitForm }
			onChangeField={ onChangeField }
			onBlurField={ onBlurField }
			formValid={ formValid }
		/>
	);

	describe('Rendering ::', () => {
		it('renders corrects', () => {
			const component = shallow(
				<WMSettingsTax
					info={ info }
					onSubmitForm={ onSubmitForm }
					onChangeField={ onChangeField }
					onBlurField={ onBlurField }
					formValid={ formValid }
				/>
			);
			expect(component).toMatchSnapshot();
		});

		describe('Tax Submitted ::', () => {
			let wrapper;
			const settingsState = initialSettingsState.set('taxSubmitted', true);
			const info = {
				settings: settingsState,
				tax: initialTaxState
			};

			beforeEach(() => {
				wrapper = shallowRenderComponent(info);
			});

			it('should allow expanded state to be true if tax card not submitted', () => {
				wrapper = shallowRenderComponent();
				wrapper.instance().handleExpandChange(true);
				expect(wrapper.state('expanded')).toBeTruthy();
			});

			it('should not set expanded state to true if tax card gets submitted', () => {
				wrapper.instance().handleExpandChange(true);
				expect(wrapper.state('expanded')).toBeFalsy();
			});

			it('should set expanded state to false if tax is submitted', () => {
				wrapper = shallowRenderComponent();
				wrapper.setState({ expanded: true });
				expect(wrapper.state('expanded')).toBeTruthy();
				wrapper.instance().componentWillReceiveProps({ info });
				expect(wrapper.state('expanded')).toBeFalsy();
			});
		});
	});
});

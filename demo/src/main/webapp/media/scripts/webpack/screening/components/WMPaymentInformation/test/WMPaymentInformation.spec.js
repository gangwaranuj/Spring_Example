import React from 'react';
import { shallow } from 'enzyme';
import { mapStateToProps, mapDispatchToProps } from '../index';
import WMPaymentInformation from '../template';
import WMAddress from '../../WMAddress';
import { initialState } from '../../../configureStore/state';
import {
	WMFormRow,
	WMTextField,
	WMSelectField,
	WMMenuItem,
	WMSelectMonth,
	WMSelectYear,
	WMRadioButtonGroup,
	WMRadioButton
} from '@workmarket/front-end-components';

const shallowRenderComponent = (
	state = initialState,
	onBlurField = () => {},
	onChangeField = () => {}
) => shallow(
	<WMPaymentInformation
		info={ state }
		onBlurField={ onBlurField }
		onChangeField={ onChangeField }
	/>
);

describe('<WMPaymentInformation />', () => {
	const fieldNames = [
		'cardNumber',
		'cardExpirationMonth',
		'cardExpirationYear',
		'cardSecurityCode',
		'firstNameOnCard',
		'lastNameOnCard'
	];
	const errorText = 'You have an error!';

	describe('Rendering', () => {
		let wrapper;

		beforeEach(() => {
			wrapper = shallowRenderComponent();
		});

		describe('Billing Name', () => {
			it('should render 1 <WMFormRow />', () => {
				const component = wrapper.find('[data-component-identifier="screening__billingInformationWrap"]');
				expect(component.type()).toEqual(WMFormRow);
			});

			it('should render 2 <WMTextField />s', () => {
				const row = wrapper.find('[data-component-identifier="screening__billingInformationWrap"]');
				const textFields = row.find(WMTextField);
				expect(textFields.length).toEqual(2);
				textFields.forEach(textField => expect(textField.type()).toEqual(WMTextField));
			});

			it('should render 1 <WMTextField /> with a floating label `First`', () => {
				const firstNameOnCardField = wrapper.find('[data-component-identifier="screening__firstNameOnCard"]');
				expect(firstNameOnCardField.prop('floatingLabelText')).toEqual('First');
			});

			it('should render 1 <WMTextField /> with a floating label `Last`', () => {
				const lastNameOnCardField = wrapper.find('[data-component-identifier="screening__lastNameOnCard"]');
				expect(lastNameOnCardField.prop('floatingLabelText')).toEqual('Last');
			});
		});

		describe('Billing Address', () => {
			it('should render 1 <WMAddress />', () => {
				const component = wrapper.find(WMAddress);
				expect(component).toHaveLength(1);
			});
		});

		describe('Expiration', () => {
			it('should render 1 <WMFormRow />', () => {
				const component = wrapper.find('[data-component-identifier="screening__expirationRow"]');
				expect(component.type()).toEqual(WMFormRow);
			});

			describe('Billing Month', () => {
				it('should render 1 <WMSelectMonth />', () => {
					const row = wrapper.find('[data-component-identifier="screening__cardExpirationMonth"]');
					const component = row.find(WMSelectMonth);
					expect(component.type()).toEqual(WMSelectMonth);
				});
			});

			describe('Billing Year', () => {
				it('should render 1 <WMSelectYear />', () => {
					const row = wrapper.find('[data-component-identifier="screening__cardExpirationYear"]');
					const component = row.find(WMSelectYear);
					expect(component.type()).toEqual(WMSelectYear);
				});
			});
		});

		describe('Security Code', () => {
			it('should render 1 <WMFormRow />', () => {
				const component = wrapper.find('[data-component-identifier="screening__cardSecurityCodeWrap"]');
				expect(component.type()).toEqual(WMFormRow);
			});

			it('should have 1 <WMTextField />', () => {
				const securityCode = wrapper.find('[data-component-identifier="screening__cardSecurityCode"]');
				expect(securityCode.type()).toEqual(WMTextField);
				expect(securityCode).toHaveLength(1);
			});
		});

		describe('Card Number', () => {
			it('should render 1 <WMFormRow />', () => {
				const component = wrapper.find('[data-component-identifier="screening__creditCardWrap"]');
				expect(component.type()).toEqual(WMFormRow);
			});

			it('should have 1 <WMTextField />', () => {
				const cardNumber = wrapper.find('[data-component-identifier="screening__cardNumber"]');
				expect(cardNumber.type()).toEqual(WMTextField);
				expect(cardNumber).toHaveLength(1);
			});

			it('should have 1 <WMSelectField />', () => {
				const cardType = wrapper.find('[data-component-identifier="screening__cardType"]');
				expect(cardType.type()).toEqual(WMSelectField);
			});

			it('<WMSelectField /> should render 3 `WMMenuItem`s', () => {
				const cardType = wrapper.find(WMMenuItem);
				expect(cardType).toHaveLength(3);
			});
		});

		describe('Payment Method', () => {
			it('should render 1 <WMFormRow />', () => {
				const component = wrapper.find('[data-component-identifier="screening__paymentMethodWrap"]');
				expect(component.type()).toEqual(WMFormRow);
			});

			it('should have 1 <WMRadioButtonGroup />', () => {
				const paymentMethod = wrapper.find('[data-component-identifier="screening__paymentMethodGroup"]');
				expect(paymentMethod.type()).toEqual(WMRadioButtonGroup);
				expect(paymentMethod).toHaveLength(1);
			});

			it('should render 2 <WMRadioButton />s within <WMRadioButtonGroup />', () => {
				const row = wrapper.find('[data-component-identifier="screening__paymentMethodGroup"]');
				const radioButtons = row.find(WMRadioButton);

				expect(radioButtons).toHaveLength(2);
				radioButtons.forEach(radioButton => expect(radioButton.type()).toEqual(WMRadioButton));
			});

			it('should render 2 <WMRadioButton />s', () => {
				const paymentMethod1 = wrapper.find('[data-component-identifier="screening__paymentMethod1"]');
				const paymentMethod2 = wrapper.find('[data-component-identifier="screening__paymentMethod2"]');

				expect(paymentMethod1.type()).toEqual(WMRadioButton);
				expect(paymentMethod1).toHaveLength(1);
				expect(paymentMethod2.type()).toEqual(WMRadioButton);
				expect(paymentMethod2).toHaveLength(1);
			});

			it('should render 1 `billingWrap` if payment method is `cc`', () => {
				const component = wrapper.find('[data-component-identifier="screening__billingWrap"]');
				const row = wrapper.find('[data-component-identifier="screening__paymentMethod1"]');
				expect(row.prop('value')).toEqual('cc');
				expect(component).toHaveLength(1);
			});

			it('should not render `billingWrap` if payment method is `account`', () => {
				const component = wrapper.find('[data-component-identifier="screening__billingWrap"]');
				const row = wrapper.find('[data-component-identifier="screening__paymentMethod2"]');
				expect(row.prop('value')).toEqual('account');
				expect(component).toHaveLength(1);
			});
		});

		describe('Errors', () => {
			fieldNames.forEach(key => {
				it(`should render error text for ${key}`, () => {
					const newValue = initialState.get(key).set('error', errorText);
					const specialState = initialState.set(key, newValue);
					wrapper = shallowRenderComponent(specialState);

					const component = wrapper.find(`[data-component-identifier="screening__${key}"]`);
					expect(component.prop('errorText')).toEqual(errorText);
				});
			});
		});
	});

	describe('Mapping functions', () => {
		let state;

		describe('`mapStateToProps`', () => {
			beforeEach(() => {
				state = initialState;
			});

			it('should return an object', () => {
				expect(typeof mapStateToProps(state)).toBe('object');
			});

			it('should return an object with property `info`', () => {
				const result = mapStateToProps(state);
				expect(result).toHaveProperty('info');
				expect(result.info).toEqual(initialState);
			});
		});

		describe('`mapDispatchToProps`', () => {
			let dispatch;
			let result;

			beforeEach(() => {
				dispatch = jest.fn();
				result = mapDispatchToProps(dispatch);
			});

			it('should return an object', () => {
				expect(typeof mapDispatchToProps(dispatch)).toBe('object');
			});

			it('should return an object with property `onChangeField`', () => {
				expect(result).toHaveProperty('onChangeField');
				expect(typeof result.onChangeField).toBe('function');
			});

			it('should fire `dispatch` when `onChangeField` is called', () => {
				result.onChangeField('firstNameOnCard', 'test');
				expect(dispatch).toHaveBeenCalled();
			});

			it('should return an object with property `onBlurField`', () => {
				expect(result).toHaveProperty('onBlurField');
				expect(typeof result.onBlurField).toBe('function');
			});

			it('should fire `dispatch` when `onBlurField` is called', () => {
				result.onBlurField('billngFirstName', 'test');
				expect(dispatch).toHaveBeenCalled();
			});
		});
	});
});

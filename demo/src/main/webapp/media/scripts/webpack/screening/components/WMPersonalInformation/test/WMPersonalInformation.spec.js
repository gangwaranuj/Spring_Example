import React from 'react';
import { shallow } from 'enzyme';
import { mapStateToProps, mapDispatchToProps } from '../index';
import WMPersonalInformation from '../template';
import WMAddress from '../../WMAddress';
import { initialState } from '../../../configureStore/state';
import {
	WMFormRow,
	WMTextField,
	WMSelectDay,
	WMSelectMonth,
	WMSelectYear
} from '@workmarket/front-end-components';

const shallowRenderComponent = (
	state = initialState,
	onBlurField = () => {},
	onChangeField = () => {}
) => shallow(
	<WMPersonalInformation
		info={ state }
		onBlurField={ onBlurField }
		onChangeField={ onChangeField }
	/>
);

describe('<WMPersonalInformation />', () => {
	const errorText = 'You have an error!';
	const fieldNames = [
		'firstName',
		'lastName',
		'SSN',
		'birthDay',
		'birthMonth',
		'birthYear',
		'email'
	];

	describe('Rendering', () => {
		let wrapper;

		beforeEach(() => {
			wrapper = shallowRenderComponent();
		});

		describe('Name', () => {
			it('should render 1 <WMFormRow />', () => {
				const component = wrapper.find('[data-component-identifier="screening__nameRow"]');
				expect(component.type()).toEqual(WMFormRow);
			});

			it('should render 2 <WMTextField />s', () => {
				const row = wrapper.find('[data-component-identifier="screening__nameRow"]');
				const textFields = row.find(WMTextField);
				expect(textFields).toHaveLength(2);
				textFields.forEach(textField => expect(textField.type()).toEqual(WMTextField));
			});

			it('should render 1 <WMTextField /> with a floating label `First`', () => {
				const firstNameField = wrapper.find('[data-component-identifier="screening__firstName"]');
				expect(firstNameField.prop('floatingLabelText')).toEqual('First');
			});

			it('should render 1 <WMTextField /> with a floating label `Last`', () => {
				const lastNameField = wrapper.find('[data-component-identifier="screening__lastName"]');
				expect(lastNameField.prop('floatingLabelText')).toEqual('Last');
			});
		});

		describe('SSN', () => {
			it('should render 1 <WMFormRow />', () => {
				const component = wrapper.find('[data-component-identifier="screening__SSNRow"]');
				expect(component.type()).toEqual(WMFormRow);
			});

			it('should render 1 <WMTextField />', () => {
				const row = wrapper.find('[data-component-identifier="screening__SSNRow"]');
				const textFields = row.find(WMTextField);
				expect(textFields).toHaveLength(1);
				textFields.forEach(textField => expect(textField.type()).toEqual(WMTextField));
			});
		});

		describe('Address', () => {
			it('should render 1 <WMAddress />', () => {
				const component = wrapper.find(WMAddress);
				expect(component).toHaveLength(1);
			});
		});

		describe('Date of Birth', () => {
			it('should render 1 <WMFormRow />', () => {
				const component = wrapper.find('[data-component-identifier="screening__birthRow"]');
				expect(component.type()).toEqual(WMFormRow);
			});

			describe('Day', () => {
				it('should render 1 <WMSelectDay />', () => {
					const row = wrapper.find('[data-component-identifier="screening__birthRow"]');
					const component = row.find(WMSelectDay);
					expect(component.type()).toEqual(WMSelectDay);
				});
			});

			describe('Month', () => {
				it('should render 1 <WMSelectMonth />', () => {
					const row = wrapper.find('[data-component-identifier="screening__birthRow"]');
					const component = row.find(WMSelectMonth);
					expect(component.type()).toEqual(WMSelectMonth);
				});
			});

			describe('Year', () => {
				it('should render 1 <WMSelectYear />', () => {
					const row = wrapper.find('[data-component-identifier="screening__birthRow"]');
					const component = row.find(WMSelectYear);
					expect(component.type()).toEqual(WMSelectYear);
				});
			});
		});

		describe('Email', () => {
			it('should render 1 <WMFormRow />', () => {
				const component = wrapper.find('[data-component-identifier="screening__emailRow"]');
				expect(component.type()).toEqual(WMFormRow);
			});

			it('should have 1 <WMTextField />', () => {
				const email = wrapper.find('[data-component-identifier="screening__email"]');
				expect(email.type()).toEqual(WMTextField);
				expect(email.length).toEqual(1);
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

			it('should render error text for SIN', () => {
				const newCountry = initialState.get('country').set('value', 'CAN');
				const newSIN = initialState.get('SIN').set('error', errorText);
				const specialState = initialState
					.set('country', newCountry)
					.set('SIN', newSIN);

				wrapper = shallowRenderComponent(specialState);

				const component = wrapper.find('[data-component-identifier="screening__SIN"]');
				expect(component.prop('errorText')).toEqual(errorText);
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
				result.onChangeField('firstName', 'test');
				expect(dispatch).toHaveBeenCalled();
			});

			it('should return an object with property `onBlurField`', () => {
				expect(result).toHaveProperty('onBlurField');
				expect(typeof result.onBlurField).toBe('function');
			});

			it('should fire `dispatch` when `onBlurField` is called', () => {
				result.onBlurField('firstName', 'test');
				expect(dispatch).toHaveBeenCalled();
			});
		});
	});
});

import React from 'react';
import { shallow } from 'enzyme';
import WMAddress from '../index';
import { createId, createName } from '../index';
import { initialState } from '../../../configureStore/state';
import {
	WMFormRow,
	WMStateProvince,
	WMCountrySelect,
	WMTextField,
} from '@workmarket/front-end-components';

describe('<WMAddress />', () => {
	const errorText = 'This is an error!';
	const fieldNames = [
		'address1',
		'city',
		'state',
		'zip',
		'country'
	];

	describe('#createId', () => {
		const prefix = 'diet';
		const suffix = 'coke';

		it('should return a string', () => {
			expect(typeof createId()).toBe('string');
		});

		it(`should concatenate two strings ("${prefix}" + "${suffix}")`, () => {
			expect(createId(prefix, suffix)).toEqual(`${prefix}__${suffix}`);
		});

		it('should add "__" between the first and second string', () => {
			expect(createId(prefix, suffix)
				.slice(prefix.length, prefix.length + 2))
				.toEqual('__');
		});
	});

	describe('#createName', () => {
		const prefix = 'diet';
		const suffix = 'coke';

		it('should return a string', () => {
			expect(typeof createName()).toBe('string');
		});

		it(`should concatenate two strings ("${prefix}" + "${suffix}")`, () => {
			expect(createName(prefix, suffix)).toEqual('dietCoke');
		});

		it('should capitalize the first letter of the second string', () => {
			expect(createName(prefix, suffix)
				.charAt(prefix.length))
				.toEqual('C');
		});
	});

	describe('Rendering', () => {
		let wrapper;

		beforeEach(() => {
			wrapper = shallow(
				<WMAddress
					info={ initialState }
					onBlurField={ () => {} }
					onChangeField={ () => {} }
				/>
			);
		});

		it('should render 1 <WMFormRow />', () => {
			const component = wrapper.find('[data-component-identifier="wm-address__row"]');
			expect(component.type()).toEqual(WMFormRow);
		});

		it('should render the row label text as "Address"', () => {
			const component = wrapper.find('[data-component-identifier="wm-address__row"]');
			expect(component.prop('labelText')).toEqual('Address');
		});

		it('should custom row label text', () => {
			const newLabel = 'Fun Label!';
			wrapper = shallow(
				<WMAddress
					info={ initialState }
					onBlurField={ () => {} }
					onChangeField={ () => {} }
					rowLabel={ newLabel }
				/>
			);

			const component = wrapper.find('[data-component-identifier="wm-address__row"]');
			expect(component.prop('labelText')).toEqual(newLabel);
		});

		describe('Street', () => {
			it('should have 2 address <WMTextField />s', () => {
				const address1 = wrapper.find('[data-component-identifier="wm-address__address1"]');
				const address2 = wrapper.find('[data-component-identifier="wm-address__address2"]');

				expect(address1.type()).toEqual(WMTextField);
				expect(address1).toHaveLength(1);
				expect(address2.type()).toEqual(WMTextField);
				expect(address2).toHaveLength(1);
			});

			it('should have 1 address <WMTextField /> with the label `Street`', () => {
				const address1 = wrapper.find('[data-component-identifier="wm-address__address1"]');
				expect(address1.prop('floatingLabelText')).toEqual('Street');
			});

			it('should have 1 address <WMTextField /> with the label `Apartment, suite #, bldg. (optional)`', () => {
				const address2 = wrapper.find('[data-component-identifier="wm-address__address2"]');
				expect(address2.prop('floatingLabelText')).toEqual('Apartment, suite #, bldg. (optional)');
			});
		});

		describe('City', () => {
			it('should have 1 <WMTextField />', () => {
				const city = wrapper.find('[data-component-identifier="wm-address__city"]');
				expect(city.type()).toEqual(WMTextField);
				expect(city).toHaveLength(1);
			});

			it('should render with a floating label `City`', () => {
				const city = wrapper.find('[data-component-identifier="wm-address__city"]');
				expect(city.prop('floatingLabelText')).toEqual('City');
			});
		});

		describe('Postal Code', () => {
			describe('(USA)', () => {
				it('should have 1 <WMTextField />', () => {
					const zip = wrapper.find('[data-component-identifier="wm-address__zip"]');
					expect(zip.type()).toEqual(WMTextField);
					expect(zip).toHaveLength(1);
				});

				it('should render with a floating label `Zip Code`', () => {
					const zip = wrapper.find('[data-component-identifier="wm-address__zip"]');
					expect(zip.prop('floatingLabelText')).toEqual('Zip Code');
				});
			});

			describe('(CAN)', () => {
				beforeEach(() => {
					const newCountry = initialState.get('country').set('value', 'CAN');
					const specialState = initialState.set('country', newCountry);

					wrapper = shallow(
						<WMAddress
							info={ specialState }
							onBlurField={ () => {} }
							onChangeField={ () => {} }
						/>
					);
				});

				it('should have 1 <WMTextField />', () => {
					const postalCode = wrapper.find('[data-component-identifier="wm-address__postal-code"]');
					expect(postalCode.type()).toEqual(WMTextField);
					expect(postalCode).toHaveLength(1);
				});

				it('should render with a floating label `Postal Code`', () => {
					const postalCode = wrapper.find('[data-component-identifier="wm-address__postal-code"]');
					expect(postalCode.prop('floatingLabelText')).toEqual('Postal Code');
				});
			});

			describe('(Other)', () => {
				beforeEach(() => {
					const newCountry = initialState.get('country').set('value', 'AFG');
					const specialState = initialState.set('country', newCountry);

					wrapper = shallow(
						<WMAddress
							info={ specialState }
							onBlurField={ () => {} }
							onChangeField={ () => {} }
						/>
					);
				});

				it('should have 1 <WMTextField />', () => {
					const postalCode = wrapper.find('[data-component-identifier="wm-address__postal-code"]');
					expect(postalCode.type()).toEqual(WMTextField);
					expect(postalCode).toHaveLength(1);
				});

				it('should render with a floating label `Postal Code`', () => {
					const postalCode = wrapper.find('[data-component-identifier="wm-address__postal-code"]');
					expect(postalCode.prop('floatingLabelText')).toEqual('Postal Code');
				});
			});
		});

		describe('State/Province/Territory', () => {
			describe('(USA)', () => {
				it('should render 1 <WMStateProvince />', () => {
					const component = wrapper.find('[data-component-identifier="wm-address__state"]');
					expect(component.type()).toEqual(WMStateProvince);
					expect(component).toHaveLength(1);
				});

				it('should render the label as `State`', () => {
					const component = wrapper.find('[data-component-identifier="wm-address__state"]');
					expect(component.prop('floatingLabelText')).toEqual('State');
				});
			});

			describe('(CAN)', () => {
				beforeEach(() => {
					const newCountry = initialState.get('country').set('value', 'CAN');
					const specialState = initialState.set('country', newCountry);

					wrapper = shallow(
						<WMAddress
							info={ specialState }
							onBlurField={ () => {} }
							onChangeField={ () => {} }
						/>
					);
				});

				it('should render 1 <WMStateProvince /> if country is `CAN`', () => {
					const component = wrapper.find('[data-component-identifier="wm-address__province"]');
					expect(component.type()).toEqual(WMStateProvince);
					expect(component).toHaveLength(1);
				});

				it('should render the label as `Province`', () => {
					const component = wrapper.find('[data-component-identifier="wm-address__province"]');
					expect(component.prop('floatingLabelText')).toEqual('Province');
				});
			});
		});

		describe('Country', () => {
			it('should render 1 `<WMCountrySelect />`', () => {
				const row = wrapper.find('[data-component-identifier="wm-address__row"]');
				const component = row.find(WMCountrySelect);
				expect(component).toHaveLength(1);
				expect(component.type()).toEqual(WMCountrySelect);
			});

			it('should render with a floating label `Country`', () => {
				const countryField = wrapper.find('[data-component-identifier="wm-address__country"]');
				expect(countryField.prop('floatingLabelText')).toEqual('Country');
			});

			it('should not be selectable by default', () => {
				const row = wrapper.find('[data-component-identifier="wm-address__row"]');
				const component = row.find(WMCountrySelect);
				expect(component.prop('disabled')).toBeTruthy();
			});

			it('should be set to "United States" by default', () => {
				const row = wrapper.find('[data-component-identifier="wm-address__row"]');
				const component = row.find(WMCountrySelect);
				expect(component.prop('value')).toEqual('USA');
			});

			it('should be selectble if `allowedCountries` is not "USA"', () => {
				wrapper = shallow(
					<WMAddress
						info={ initialState }
						onBlurField={ () => {} }
						onChangeField={ () => {} }
						allowedCountries="USA_CAN"
					/>
				);

				const row = wrapper.find('[data-component-identifier="wm-address__row"]');
				const component = row.find(WMCountrySelect);
				expect(component.prop('disabled')).toBeFalsy();
			});
		});

		describe('Errors', () => {
			fieldNames.forEach(key => {
				it(`should render error text for ${key}`, () => {
					const newValue = initialState.get(key).set('error', errorText);
					const specialState = initialState.set(key, newValue);
					wrapper = shallow(
						<WMAddress
							info={ specialState }
							onBlurField={ () => {} }
							onChangeField={ () => {} }
						/>
					);

					const component = wrapper.find(`[data-component-identifier="wm-address__${key}"]`);
					expect(component.prop('errorText')).toEqual(errorText);
				});
			});

			it('should render error text for Postal Code', () => {
				const newCountry = initialState.get('country').set('value', 'CAN');
				const newPostalCode = initialState.get('postalCode').set('error', errorText);
				const specialState = initialState
					.set('country', newCountry)
					.set('postalCode', newPostalCode);

				wrapper = shallow(
					<WMAddress
						info={ specialState }
						onBlurField={ () => {} }
						onChangeField={ () => {} }
					/>
				);

				const component = wrapper.find('[data-component-identifier="wm-address__postal-code"]');
				expect(component.prop('errorText')).toEqual(errorText);
			});

			it('should render error text for Province', () => {
				const newCountry = initialState.get('country').set('value', 'CAN');
				const newProvince = initialState.get('province').set('error', errorText);
				const specialState = initialState
					.set('country', newCountry)
					.set('province', newProvince);

				wrapper = shallow(
					<WMAddress
						info={ specialState }
						onBlurField={ () => {} }
						onChangeField={ () => {} }
					/>
				);

				const component = wrapper.find('[data-component-identifier="wm-address__province"]');
				expect(component.prop('errorText')).toEqual(errorText);
			});
		});
	});
});

import PropTypes from 'prop-types';
import React from 'react';
import InputMask from 'react-input-mask';
import {
	WMFormRow,
	WMTextField,
	WMStateProvince,
	WMCountrySelect
} from '@workmarket/front-end-components';
import styles from './styles';

export const createId = (prefix = '', suffix = '') => {
	let id = suffix;

	if (prefix) {
		id = `${prefix}__${suffix}`;
	}

	return id;
};

export const createName = (prefix = '', suffix = '') => {
	let name = suffix;

	if (prefix) {
		name = name.charAt(0).toUpperCase() + name.slice(1);
	}

	return prefix + name;
};

const WMAddress = ({
	info,
	onBlurField,
	onChangeField,
	rowLabel = 'Address',
	prefix = '',
	allowedCountries = 'USA'
}) => (
	<WMFormRow
		floating
		data-component-identifier="wm-address__row"
		labelText={ rowLabel }
		id={ createId(prefix, 'address1') }
		required
		baseStyle={ styles.formRow }
		fieldStyle={ { flexWrap: 'wrap' } }
	>
		<WMTextField
			id={ createId(prefix, 'address1') }
			data-component-identifier="wm-address__address1"
			floatingLabelText="Street"
			fullWidth
			onChange={ (event, value) => onChangeField(createName(prefix, 'address1'), value) }
			onBlur={ () => onBlurField(createName(prefix, 'address1')) }
			value={ info.get(createName(prefix, 'address1')).get('value') }
			errorText={ info.get(createName(prefix, 'address1')).get('error') }
			disabled={ info.get('submitting') }
		/>
		<WMTextField
			id={ createId(prefix, 'address2') }
			data-component-identifier="wm-address__address2"
			floatingLabelText="Apartment, suite #, bldg. (optional)"
			fullWidth
			onChange={ (event, value) => onChangeField(createName(prefix, 'address2'), value) }
			onBlur={ () => onBlurField(createName(prefix, 'address2')) }
			value={ info.get(createName(prefix, 'address2')).get('value') }
			disabled={ info.get('submitting') }
		/>
		<WMTextField
			id={ createId(prefix, 'city') }
			data-component-identifier="wm-address__city"
			floatingLabelText="City"
			style={ { width: 'calc(50% - 1em)', marginRight: '1em' } }
			onChange={ (event, value) => onChangeField(createName(prefix, 'city'), value) }
			onBlur={ () => onBlurField(createName(prefix, 'city')) }
			value={ info.get(createName(prefix, 'city')).get('value') }
			errorText={ info.get(createName(prefix, 'city')).get('error') }
			disabled={ info.get('submitting') }
		/>
		{
			info.get(createName(prefix, 'country')).get('value') === 'USA' &&
				<WMStateProvince
					id={ createId(prefix, 'state') }
					floatingLabelText="State"
					data-component-identifier="wm-address__state"
					onChange={ (event, index, value) => onChangeField(createName(prefix, 'state'), value) }
					value={ info.get(createName(prefix, 'state')).get('value') }
					locale={ info.get('country').get('value') }
					style={ { width: 'calc(50% - 1em)', marginLeft: '1em' } }
					errorText={ info.get(createName(prefix, 'state')).get('error') }
					disabled={ info.get('submitting') }
				/>
		}
		{
			info.get(createName(prefix, 'country')).get('value') === 'CAN' &&
				<WMStateProvince
					id={ createId(prefix, 'province') }
					floatingLabelText="Province"
					data-component-identifier="wm-address__province"
					onChange={ (event, index, value) => onChangeField(createName(prefix, 'province'), value) }
					value={ info.get(createName(prefix, 'province')).get('value') }
					locale={ info.get('country').get('value') }
					style={ { width: 'calc(50% - 1em)', marginLeft: '1em' } }
					errorText={ info.get(createName(prefix, 'province')).get('error') }
					disabled={ info.get('submitting') }
				/>
		}
		{
			info.get(createName(prefix, 'country')).get('value') === 'USA' &&
				<WMTextField
					id={ createId(prefix, 'zip') }
					data-component-identifier="wm-address__zip"
					floatingLabelText="Zip Code"
					style={ { width: 'calc(50% - 1em)', marginRight: '1em' } }
					onChange={ (event, value) => onChangeField(createName(prefix, 'zip'), value) }
					onBlur={ () => onBlurField(createName(prefix, 'zip')) }
					value={ info.get(createName(prefix, 'zip')).get('value') }
					errorText={ info.get(createName(prefix, 'zip')).get('error') }
					disabled={ info.get('submitting') }
				/>
		}
		{
			info.get(createName(prefix, 'country')).get('value') !== 'USA' &&
				<WMTextField
					id={ createId(prefix, 'postal-code') }
					data-component-identifier="wm-address__postal-code"
					floatingLabelText="Postal Code"
					style={ { width: 'calc(50% - 1em)', marginRight: '1em' } }
					onChange={ (event, value) => onChangeField(createName(prefix, 'postalCode'), value) }
					onBlur={ () => onBlurField(createName(prefix, 'postalCode')) }
					value={ info.get(createName(prefix, 'postalCode')).get('value') }
					errorText={ info.get(createName(prefix, 'postalCode')).get('error') }
					disabled={ info.get('submitting') }
				/>
		}
		<WMCountrySelect
			id={ createId(prefix, 'country') }
			floatingLabelText="Country"
			data-component-identifier="wm-address__country"
			disabled={ allowedCountries === 'USA' }
			onChange={ (event, index, value) => onChangeField(createName(prefix, 'country'), value) }
			value={ info.get(createName(prefix, 'country')).get('value') }
			style={ { width: 'calc(50% - 1em)', marginLeft: '1em' } }
			errorText={ info.get(createName(prefix, 'country')).get('error') }
		/>
	</WMFormRow>
);

WMAddress.propTypes = {
	info: PropTypes.object.isRequired,
	onBlurField: PropTypes.func.isRequired,
	onChangeField: PropTypes.func.isRequired,
	rowLabel: PropTypes.string,
	prefix: PropTypes.string,
	allowedCountries: PropTypes.oneOf(['USA', 'USA_CAN', 'world'])
};

export default WMAddress;

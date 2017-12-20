import PropTypes from 'prop-types';
import React from 'react';
import InputMask from 'react-input-mask';
import {
	WMFormRow,
	WMTextField,
	WMSelectDay,
	WMSelectMonth,
	WMSelectYear
} from '@workmarket/front-end-components';
import WMAddress from '../WMAddress';
import styles from './styles';

const WMPersonalInformation = ({
	info,
	onBlurField,
	onChangeField
}) => (
	<div>
		<p style={ styles.copy }>Please enter your information exactly as it appears in legal documents</p>
		<WMFormRow
			floating
			data-component-identifier="screening__nameRow"
			labelText="Name"
			id="name"
			required
			baseStyle={ styles.formRow }
		>
			<WMTextField
				id="first-name"
				data-component-identifier="screening__firstName"
				fullWidth
				floatingLabelText="First"
				onChange={ (event, value) => onChangeField('firstName', value) }
				onBlur={ () => onBlurField('firstName') }
				value={ info.get('firstName').get('value') }
				errorText={ info.get('firstName').get('error') }
				disabled={ info.get('submitting') }
				style={ { width: 'calc(50% - 1em)', marginRight: '1em' } }
			/>
			<WMTextField
				id="last-name"
				data-component-identifier="screening__lastName"
				fullWidth
				floatingLabelText="Last"
				onChange={ (event, value) => onChangeField('lastName', value) }
				onBlur={ () => onBlurField('lastName') }
				value={ info.get('lastName').get('value') }
				errorText={ info.get('lastName').get('error') }
				disabled={ info.get('submitting') }
				style={ { width: 'calc(50% - 1em)', marginLeft: '1em' } }
			/>
		</WMFormRow>
		<WMFormRow
			labelText={
				info.get('country').get('value') === 'USA'
					? 'Social Security Number'
					: 'Social Insurance Number'
			}
			data-component-identifier="screening__SSNRow"
			id="WIN"
			required
			baseStyle={ styles.formRow }
		>
			{
				info.get('country').get('value') === 'USA' &&
					<WMTextField
						id="SSN"
						data-component-identifier="screening__SSN"
						fullWidth
						type="number"
						hintText="XXX-XX-XXXX"
						onChange={ (event, value) => onChangeField('SSN', value) }
						onBlur={ () => onBlurField('SSN') }
						value={ info.get('SSN').get('value') }
						errorText={ info.get('SSN').get('error') }
						disabled={ info.get('submitting') }
						style={ { width: 'calc(50% - 1em)' } }
					>
						<InputMask mask="999-99-9999" maskChar="X" />
					</WMTextField>
			}
			{
				info.get('country').get('value') === 'CAN' &&
					<WMTextField
						id="SIN"
						data-component-identifier="screening__SIN"
						fullWidth
						type="number"
						hintText="XXX XXX XXX"
						onChange={ (event, value) => onChangeField('SIN', value) }
						onBlur={ () => onBlurField('SIN') }
						value={ info.get('SIN').get('value') }
						errorText={ info.get('SIN').get('error') }
						disabled={ info.get('submitting') }
						style={ { width: 'calc(50% - 1em)' } }
					>
						<InputMask mask="999 999 999" maskChar="X" />
					</WMTextField>
			}
		</WMFormRow>
		<WMAddress
			info={ info }
			onChangeField={ onChangeField }
			onBlurField={ onBlurField }
		/>
		<WMFormRow
			floating
			data-component-identifier="screening__birthRow"
			labelText="Date of Birth"
			id="birth"
			required
			baseStyle={ styles.formRow }
		>
			<WMSelectMonth
				id="birth-month"
				data-component-identifier="screening__birthMonth"
				onChange={ (event, index, value) => onChangeField('birthMonth', value) }
				month={ info.get('birthMonth').get('value') }
				errorText={ info.get('birthMonth').get('error') }
				style={ { width: `calc(${1 / 3 * 100}% - ${4 / 3}em)`, margin: '0 1em' } }
				disabled={ info.get('submitting') }
			/>
			<WMSelectDay
				id="birth-day"
				data-component-identifier="screening__birthDay"
				onChange={ (event, index, value) => onChangeField('birthDay', value) }
				day={ info.get('birthDay').get('value') }
				month={ info.get('birthMonth').get('value') }
				year={ info.get('birthYear').get('value') }
				errorText={ info.get('birthDay').get('error') }
				style={ { width: `calc(${1 / 3 * 100}% - ${4 / 3}em)`, margin: '0 1em' } }
				disabled={ info.get('submitting') }
			/>
			<WMSelectYear
				id="birth-year"
				data-component-identifier="screening__birthYear"
				onChange={ (event, index, value) => onChangeField('birthYear', value) }
				year={ info.get('birthYear').get('value') }
				errorText={ info.get('birthYear').get('error') }
				style={ { width: `calc(${1 / 3 * 100}% - ${4 / 3}em)`, marginLeft: '1em' } }
				disabled={ info.get('submitting') }
			/>
		</WMFormRow>
		<WMFormRow
			labelText="Email Address"
			data-component-identifier="screening__emailRow"
			id="email"
			required
			baseStyle={ styles.formRow }
		>
			<WMTextField
				id="email"
				data-component-identifier="screening__email"
				fullWidth
				onChange={ (event, value) => onChangeField('email', value) }
				onBlur={ () => onBlurField('email') }
				value={ info.get('email').get('value') }
				errorText={ info.get('email').get('error') }
				disabled={ info.get('submitting') }
			/>
		</WMFormRow>
	</div>
);

WMPersonalInformation.propTypes = {
	info: PropTypes.object.isRequired,
	onBlurField: PropTypes.func.isRequired,
	onChangeField: PropTypes.func.isRequired
};

export default WMPersonalInformation;

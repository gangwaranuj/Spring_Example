import PropTypes from 'prop-types';
import React from 'react';
import InputMask from 'react-input-mask';
import {
	WMFormRow,
	WMTextField,
	WMRadioButton,
	WMRadioButtonGroup,
	WMSelectMonth,
	WMSelectYear,
	WMSelectField,
	WMMenuItem,
	WMCheckbox
} from '@workmarket/front-end-components';
import WMAddress from '../WMAddress';
import styles from './styles';

const WMPaymentInformation = React.createClass({
	propTypes: {
		info: PropTypes.object.isRequired,
		onBlurField: PropTypes.func.isRequired,
		onChangeField: PropTypes.func.isRequired,
		onCheckField: PropTypes.func.isRequired
	},

	getInitialState () {
		return { open: true, valueSelected: 'cc' };
	},

	closeCreditCard () {
		this.setState({ open: false, valueSelected: 'account' });
	},

	openCreditCard () {
		this.setState({ open: true, valueSelected: 'cc' });
	},

	render () {
		const { open, valueSelected } = this.state;
		const { info, onBlurField, onChangeField, onCheckField } = this.props;
		return (
			<div>
				<p style={ styles.copy }>Please enter your payment information.</p>
				<WMFormRow
					labelText="Payment Method"
					data-component-identifier="screening__paymentMethodWrap"
					id="payment-method-wrap"
					required
					baseStyle={ styles.formRow }
				>
					<WMRadioButtonGroup
						defaultSelected="cc"
						labelPosition="right"
						name="Payment Method"
						data-component-identifier="screening__paymentMethodGroup"
						id="payment-method-group"
						valueSelected={ valueSelected }
						onChange={ (event, value) => onChangeField('paymentType', value) }
					>
						<WMRadioButton
							label="Pay with credit card"
							value="cc"
							data-component-identifier="screening__paymentMethod1"
							id="payment-method1"
							disabled={ info.get('submitting') }
							onClick={ this.openCreditCard }
						/>
						<WMRadioButton
							label="Pay with funds on my Work Market account"
							value="account"
							disabled={ !info.get('hasSufficientFunds') || info.get('submitting') }
							data-component-identifier="screening__paymentMethod2"
							id="payment-method2"
							onClick={ this.closeCreditCard }
						/>
					</WMRadioButtonGroup>
				</WMFormRow>
				<WMFormRow
					id="payment-method-info"
				>
					<span style={styles.information}>Funds earned on the Work Market platform are available
					for withdrawal and if funds are sufficient, for payment towards workers services.</span>
				</WMFormRow>
				<div
					data-component-identifier="screening__billingWrap"
					id="billing-wrap"
					style={ (open) ? { display: 'block' } : { display: 'none' } }
				>
					<WMFormRow
						labelText="Card Number"
						data-component-identifier="screening__creditCardWrap"
						id="card-number-wrap"
						required
						baseStyle={ styles.formRow }
					>
						<WMTextField
							id="card-number"
							data-component-identifier="screening__cardNumber"
							onChange={ (event, value) => onChangeField('cardNumber', value) }
							onBlur={ () => onBlurField('cardNumber') }
							value={ info.get('cardNumber').get('value') }
							errorText={ info.get('cardNumber').get('error') }
							disabled={ info.get('submitting') }
							style={ { width: 'calc(50% - 1em)', marginRight: '1em' } }
						>
							<InputMask mask="9999999999999999" maskChar="" />
						</WMTextField>
						<WMSelectField
							id="card-type"
							data-component-identifier="screening__cardType"
							value={ info.get('cardType').get('value') }
							style={ { width: 'calc(50% - 1em)', marginLeft: '1em' } }
							onChange={ (event, index, value) => onChangeField('cardType', value) }
							errorText={ info.get('cardType').get('error') }
							hintText="Select a Card Type"
						>
							<WMMenuItem key={ 0 } value="visa" primaryText="Visa" />
							<WMMenuItem key={ 1 } value="mastercard" primaryText="Mastercard" />
							<WMMenuItem key={ 2 } value="amex" primaryText="Amex" />
						</WMSelectField>
					</WMFormRow>
					<WMFormRow
						floating
						labelText="Expiration"
						id="expiration-wrap"
						data-component-identifier="screening__expirationRow"
						required
						baseStyle={ styles.formRow }
					>
						<WMSelectMonth
							id="billing-month"
							data-component-identifier="screening__cardExpirationMonth"
							onChange={ (event, index, value) => onChangeField('cardExpirationMonth', value) }
							month={ info.get('cardExpirationMonth').get('value') }
							errorText={ info.get('cardExpirationMonth').get('error') }
							disabled={ info.get('submitting') }
							useDigits
							leadingZero
							style={ { width: 'calc(50% - 1em)', marginRight: '1em' } }
						/>
						<WMSelectYear
							id="billing-year"
							data-component-identifier="screening__cardExpirationYear"
							onChange={ (event, index, value) => onChangeField('cardExpirationYear', value) }
							year={ info.get('cardExpirationYear').get('value') }
							min={ (new Date()).getUTCFullYear() }
							max={ (new Date()).getUTCFullYear() + 20 }
							errorText={ info.get('cardExpirationYear').get('error') }
							disabled={ info.get('submitting') }
							style={ { width: 'calc(50% - 1em)', marginLeft: '1em' } }
						/>
					</WMFormRow>
					<WMFormRow
						labelText="Security Code"
						data-component-identifier="screening__cardSecurityCodeWrap"
						id="card-security-code-wrap"
						required
						baseStyle={ styles.formRow }
					>
						<WMTextField
							id="card-security-code"
							data-component-identifier="screening__cardSecurityCode"
							onChange={ (event, value) => onChangeField('cardSecurityCode', value) }
							onBlur={ () => onBlurField('cardSecurityCode') }
							value={ info.get('cardSecurityCode').get('value') }
							errorText={ info.get('cardSecurityCode').get('error') }
							disabled={ info.get('submitting') }
							style={ { width: 'calc(50% - 1em)' } }
						/>
					</WMFormRow>
					<WMFormRow
						id="card-security-code-info"
					>
						<span style={ styles.information }>The back panel of most Visa/MasterCard
						cards contain the full 16-digit account number, followed by the 3-digit
						CVV/CVC code. The American Express CVV/CVC is a 4-digit number found on the
						front of the card.</span>
					</WMFormRow>
					<WMFormRow
						labelText="Name on Card"
						floating
						data-component-identifier="screening__billingInformationWrap"
						id="billing-information-wrap"
						required
						baseStyle={ styles.formRow }
						fieldStyle={ { flexWrap: 'wrap' } }
					>
						<WMTextField
							id="billing-first-name"
							data-component-identifier="screening__firstNameOnCard"
							floatingLabelText="First"
							onChange={ (event, value) => onChangeField('firstNameOnCard', value) }
							onBlur={ () => onBlurField('firstNameOnCard') }
							value={ info.get('firstNameOnCard').get('value') }
							errorText={ info.get('firstNameOnCard').get('error') }
							disabled={ info.get('submitting') }
							style={ { width: 'calc(50% - 1em)', marginRight: '1em' } }
						/>
						<WMTextField
							id="billing-last-name"
							data-component-identifier="screening__lastNameOnCard"
							floatingLabelText="Last"
							onChange={ (event, value) => onChangeField('lastNameOnCard', value) }
							onBlur={ () => onBlurField('lastNameOnCard') }
							value={ info.get('lastNameOnCard').get('value') }
							errorText={ info.get('lastNameOnCard').get('error') }
							disabled={ info.get('submitting') }
							style={ { width: 'calc(50% - 1em)', marginLeft: '1em' } }
						/>
					</WMFormRow>
					<WMFormRow
						floating
						data-component-identifier="screening__billingAddressCheckWrap"
						id="billing-address-check-wrap"
						required
						baseStyle={ styles.formRow }
					>
						<WMCheckbox
							id="billing-address-check"
							data-component-identifier="screening__billingAddressCheck"
							label="My billing address is the same as listed above."
							onCheck={ () => onCheckField() }
							checked={ info.get('checked') }
						/>
					</WMFormRow>
					{
						!info.get('checked') &&
							<WMAddress
								info={ info }
								onChangeField={ onChangeField }
								onBlurField={ onBlurField }
								prefix="billing"
								allowedCountries="world"
							/>
					}
				</div>
			</div>
		);
	}
});

export default WMPaymentInformation;

import PropTypes from 'prop-types';
import React, { Component } from 'react';
import { Map } from 'immutable';
import {
	WMFontIcon,
	WMCard,
	WMCardHeader,
	WMCardText,
	WMCardActions,
	WMRaisedButton,
	WMFormRow,
	WMTextField,
	WMRadioButton,
	WMRadioButtonGroup,
	WMSelectField,
	WMMenuItem,
	WMMessageBanner
} from '@workmarket/front-end-components';
import WMFetchSelect from '../WMFetchSelect';
import componentStyles from './styles';
import baseStyles from '../styles';

const mediaPrefix = window.mediaPrefix;
const styles = Object.assign({}, baseStyles, componentStyles);

class WMSettingsBank extends Component {
	constructor (props) {
		super(props);
		this.state = {
			expanded: false
		};

		this.handleExpandChange = this.handleExpandChange.bind(this);
	}

	componentWillReceiveProps (nextProps) {
		const { info } = nextProps;
		const { settings } = info;
		const fundsSubmitted = settings.get('fundsSubmitted');
		if (fundsSubmitted) {
			// if already submitted, close this card
			this.setState({ expanded: false });
		}
	}

	handleExpandChange (expanded) {
		const { info } = this.props;
		const { settings } = info;
		const fundsSubmitted = settings.get('fundsSubmitted');

		if (!fundsSubmitted) {
			// if already submitted, prevent this card from opening
			this.setState({ expanded });
		}
	}

	render () {
		const { info, onChangeField, onSubmitForm, onBlurField, formValid } = this.props;
		const { funds, settings } = info;
		const fundsSubmitted = settings.get('fundsSubmitted');
		const fundsSubmitDisabled = settings.get('fundsSubmitDisabled');
		const errors = settings.get('fundsError');
		const { expanded } = this.state;

		return (
			<WMCard
				style={ styles.card }
				onExpandChange={ this.handleExpandChange }
				expanded={ expanded }
			>
				<WMCardHeader
					title="Link Your Bank Account"
					subtitle="Link your bank account for easy funding in the future."
					style={ styles.cardHeader }
					textStyle={ styles.cardHeaderText }
					titleStyle={ styles.cardHeaderTitle }
					actAsExpander
					showExpandableButton={ !fundsSubmitted }
				>
					<img
						id="wm-settings-bank__icon"
						src={ `${mediaPrefix}/images/settings/link.bank.account.svg` }
						alt="Link Bank Account Icon"
						style={ styles.cardIcon }
					/>
					{ fundsSubmitted && (
						<WMFontIcon
							className="material-icons"
							id="wm-settings-profile-completed-icon"
							color="#5eb65f"
							style={ styles.completedIcon }
						>
							check_circle
						</WMFontIcon>
					) }
				</WMCardHeader>
				<WMCardText
					style={ styles.cardText }
					expandable
				>
					{ errors.length > 0 &&
						errors.map((error, index) => {
							return (
								<WMMessageBanner
									status="error"
									key={ index } // eslint-disable-line react/no-array-index-key
								>
									{ error }
								</WMMessageBanner>
							);
						})
					}
					<p>Fill out the form below to initiate two small deposits into your bank account.</p>
					<p>
						{
							'Once these deposits appear in your account (approximately 3 days), you\'ll need ' +
							'to verify the deposit amount in Settings > Payment Accounts.'
						}
					</p>
					<WMFormRow
						baseStyle={ styles.formRow }
						labelText="Country*"
						data-component-identifier="settings__countryRow"
						id="country-wrap"
					>
						<WMSelectField
							id="settings__country"
							data-component-identifier="settings__country"
							onChange={ (event, index, value) => onChangeField('country', value) }
							value={ funds.getIn(['country', 'value']) }
							disabled
							fullWidth
						>
							<WMMenuItem
								value={ 'USA' }
								primaryText="USA"
							/>
						</WMSelectField>
					</WMFormRow>
					<WMFormRow
						baseStyle={ styles.formRow }
						labelText="Payment Method*"
						data-component-identifier="settings__paymentMethodRow"
						id="payment-method-wrap"
					>
						<WMSelectField
							data-component-identifier="settings__paymentMethod"
							onChange={ (event, index, value) => onChangeField('paymentMethod', value) }
							value={ funds.getIn(['paymentMethod', 'value']) }
							disabled
							fullWidth
						>
							<WMMenuItem
								value={ 'Bank Account' }
								primaryText="Bank Account"
							/>
						</WMSelectField>
					</WMFormRow>
					<WMFormRow
						baseStyle={ styles.formRow }
						labelText="Name On Account*"
						id="name-on-account-wrap"
						data-component-identifier="settings__nameOnAccountTypeRow"
					>
						<WMFetchSelect
							fetchURL={ '/employer/v2/settings/funds/accounts/admins' }
							onSelectChange={ (event, index, value) => onChangeField('nameOnAccount', value) }
							value={ funds.getIn(['nameOnAccount', 'value']) }
							fullWidth
						/>
					</WMFormRow>
					<WMFormRow
						baseStyle={ styles.formRow }
						labelText="Account Type*"
						id="account-type-wrap"
						data-component-identifier="settings__fundsAccountTypeRow"
					>
						<WMRadioButtonGroup
							valueSelected={ funds.getIn(['bankAccountTypeCode', 'value']) }
							name="account-type"
							data-component-identifier="settings__fundsAccountTypeGroup"
							onChange={ (event, index) => onChangeField('bankAccountTypeCode', index) }
							id="account-type-group"
						>
							<WMRadioButton
								label="Checking"
								value="checking"
								data-component-identifier="settings__fundsAccountType"
							/>
							<WMRadioButton
								label="Savings"
								value="savings"
								data-component-identifier="settings__fundsAccountType"
							/>
						</WMRadioButtonGroup>
					</WMFormRow>
					<WMFormRow
						baseStyle={ styles.formRow }
						labelText="Bank Name*"
						data-component-identifier="settings__bankNameRow"
						id="custom-location-wrap"
					>
						<WMTextField
							id="bank-name"
							data-component-identifier="settings__bankName"
							onChange={ (event, value) => onChangeField('bankName', value) }
							onBlur={ () => onBlurField('bankName', funds.getIn(['bankName', 'value'])) }
							value={ funds.getIn(['bankName', 'value']) }
							errorText={ funds.getIn(['bankName', 'error']) }
							fullWidth
						/>
					</WMFormRow>
					<WMFormRow
						id="routing-number-wrap"
						baseStyle={ styles.formRow }
						labelText="Routing Number*"
						data-component-identifier="settings__routingNumberRow"
					>
						<WMTextField
							id="routing-number"
							data-component-identifier="settings__routingNumber"
							value={ funds.getIn(['routingNumber', 'value']) }
							onChange={ (event, value) => onChangeField('routingNumber', value) }
							onBlur={ () => onBlurField('routingNumber', funds.getIn(['routingNumber', 'value'])) }
							errorText={ funds.getIn(['routingNumber', 'error']) }
							fullWidth
						/>
					</WMFormRow>
					<WMFormRow
						baseStyle={ styles.formRow }
						labelText="Account Number*"
						data-component-identifier="settings__accountNumberRow"
						id="account-number-wrap"
					>
						<WMTextField
							id="account-number"
							data-component-identifier="settings__accountNumber"
							onChange={ (event, value) => onChangeField('accountNumber', value) }
							onBlur={ () => onBlurField('accountNumber', funds.getIn(['accountNumber', 'value'])) }
							errorText={ funds.getIn(['accountNumber', 'error']) }
							fullWidth
						/>
					</WMFormRow>
					<WMFormRow
						baseStyle={ styles.formRow }
						labelText="Confirm Account Number*"
						data-component-identifier="settings__confirmAccountNumberRow"
						id="confirm-account-number-wrap"
					>
						<WMTextField
							id="confirm-account-number"
							data-component-identifier="settings__confirmAccountNumber"
							onChange={ (event, value) => onChangeField('accountNumberConfirm', value) }
							onBlur={ () => onBlurField(
									'accountNumberConfirm',
									funds.getIn(['accountNumberConfirm', 'value'])
								) }
							errorText={ funds.getIn(['accountNumberConfirm', 'error']) }
							fullWidth
						/>
					</WMFormRow>
					<div
						style={ styles.imageWrapper }
					>
						<img
							style={ styles.image }
							src="/media/images/samplecheck.gif"
							alt="USA Check"
							id="settings__checkImage"
						/>
					</div>
				</WMCardText>
				<WMCardActions
					style={ styles.actions }
					data-component-identifier="settings__actions"
					expandable
				>
					<WMRaisedButton
						data-component-identifier="settings__fundsSubmit"
						label="Initiate Account Linking"
						onClick={ () => onSubmitForm(funds) }
						primary
						disabled={ fundsSubmitDisabled || !formValid }
					/>
				</WMCardActions>
			</WMCard>
		);
	}
}

export default WMSettingsBank;

WMSettingsBank.propTypes = {
	info: PropTypes.shape({
		funds: PropTypes.instanceOf(Map),
		settings: PropTypes.instanceOf(Map)
	}).isRequired,
	onChangeField: PropTypes.func.isRequired,
	onSubmitForm: PropTypes.func.isRequired,
	onBlurField: PropTypes.func.isRequired,
	formValid: PropTypes.bool.isRequired
};

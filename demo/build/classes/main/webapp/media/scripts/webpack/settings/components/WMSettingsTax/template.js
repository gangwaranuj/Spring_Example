import PropTypes from 'prop-types';
import React, { Component } from 'react';
import { Map } from 'immutable';
import {
	WMFontIcon,
	WMCheckbox,
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
	WMStateProvince,
	WMMessageBanner,
	WMCountrySelect
} from '@workmarket/front-end-components';
import TaxFormModal from './TaxFormModal';
import componentStyles from './styles';
import baseStyles from '../styles';

const mediaPrefix = window.mediaPrefix;
const styles = Object.assign({}, baseStyles, componentStyles);

class WMSettingsTax extends Component {
	constructor (props) {
		super(props);
		this.state = {
			expanded: false,
			showFormModal: false
		};

		this.handleExpandChange = this.handleExpandChange.bind(this);
	}

	componentWillReceiveProps (nextProps) {
		const { info } = nextProps;
		const { settings } = info;
		const taxSubmitted = settings.get('taxSubmitted');
		if (taxSubmitted) {
			// if already submitted, close this card
			this.setState({ expanded: false });
		}
	}

	handleExpandChange (expanded) {
		const { info } = this.props;
		const { settings } = info;
		const taxSubmitted = settings.get('taxSubmitted');

		if (!taxSubmitted) {
			// if already submitted, prevent this card from opening
			this.setState({ expanded });
		}
	}

	render () {
		const {
			info,
			onChangeField,
			onBlurField,
			onSubmitForm,
			formValid
		} = this.props;
		const { settings, tax } = info;
		const taxSubmitted = settings.get('taxSubmitted');
		const isUS = tax.getIn(['taxCountry', 'value']) === 'usa';
		const { expanded, showFormModal } = this.state;
		const addressTwo = `${tax.getIn(['city', 'value'])}, ${tax.getIn(['state', 'value'])} ${tax.getIn(['postalCode', 'value'])}`;
		const EID = tax.getIn(['taxNumber', 'value']);
		const errors = settings.get('taxError');
		const radioSelectedTaxEntity = () => {
			const selectedTaxEntity = tax.getIn(['taxEntityTypeCode', 'value']);
			if (
				selectedTaxEntity === 'llc-c-corp' ||
				selectedTaxEntity === 'llc-s-corp' ||
				selectedTaxEntity === 'llc-part'
			) {
				return 'llc-c-corp';
			}
			return selectedTaxEntity;
		};

		return (
			<div>
				{ showFormModal &&
					<TaxFormModal
						open={ showFormModal }
						taxName={ tax.getIn(['taxName', 'value']) }
						dbaName={ tax.getIn(['businessName', 'value']) }
						taxEntityTypeCode={ tax.getIn(['taxEntityTypeCode', 'value']) }
						address={ tax.getIn(['address', 'value']) }
						addressTwo={ addressTwo }
						eid={ EID }
						signature={ tax.getIn(['signature', 'value']) }
						signatureDateString={ tax.getIn(['signatureDateString', 'value']) }
						onSubmitForm={ () => {
							this.setState({ showFormModal: false });
							onSubmitForm(tax);
						} }
						onChangeField={ (name, value) => onChangeField(name, value) }
						closeModal={ () => this.setState({ showFormModal: false }) }
					/>
				}
				<WMCard
					style={ styles.card }
					onExpandChange={ this.handleExpandChange }
					expanded={ expanded }
				>
					<WMCardHeader
						title="Set Your Tax Information"
						subtitle="You will need your tax ID and documentation."
						style={ styles.cardHeader }
						textStyle={ styles.cardHeaderText }
						titleStyle={ styles.cardHeaderTitle }
						actAsExpander
						showExpandableButton={ !taxSubmitted }
					>
						<img
							id="wm-settings-tax__icon"
							src={ `${mediaPrefix}/images/settings/tax.svg` }
							alt="Tax Icon"
							style={ styles.cardIcon }
						/>
						{ taxSubmitted && (
							<WMFontIcon
								className="material-icons"
								id="wm-settings-tax-completed-icon"
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
						<WMFormRow
							baseStyle={ styles.formRow }
							labelText="Country *"
							data-component-identifier="settings__taxCountry"
							id="tax_country-wrap"
						>
							<WMSelectField
								onChange={ (event, index, value) => onChangeField('taxCountry', value) }
								value={ tax.getIn(['taxCountry', 'value']) }
								fullWidth
							>
								<WMMenuItem value={ 'usa' } primaryText="United States" />
								<WMMenuItem value={ 'canada' } primaryText="Canada" />
								<WMMenuItem value={ 'other' } primaryText="Other" />
							</WMSelectField>
						</WMFormRow>
						{
							// united states is toggled
							tax.get('taxCountry').get('value') === 'usa' &&
								<div>
									<WMFormRow
										baseStyle={ styles.formRow }
										labelText="Employer ID Number *"
										data-component-identifier="settings__taxNumberRow"
										id="tax-number"
									>
										<WMTextField
											id="tax-number"
											data-component-identifier="settings__taxNumber"
											value={ tax.getIn(['taxNumber', 'value']) }
											errorText={ tax.getIn(['taxNumber', 'error']) }
											onChange={ (event, value) => onChangeField('taxNumber', value) }
											onBlur={ () => onBlurField('taxNumber', tax.getIn(['taxNumber', 'value'])) }
											fullWidth
										/>
									</WMFormRow>
									<WMFormRow
										baseStyle={ styles.formRow }
										labelText="Company Name *"
										data-component-identifier="settings__companyTaxNameRow"
										id="tax-name"
									>
										<WMTextField
											id="tax-name"
											data-component-identifier="settings__companyTaxName"
											value={ tax.getIn(['lastName', 'value']) }
											errorText={ tax.getIn(['lastName', 'error']) }
											onChange={ (event, value) => onChangeField('lastName', value) }
											onBlur={ () => onBlurField('lastName', tax.getIn(['lastName', 'value'])) }
											fullWidth
										/>
									</WMFormRow>
									<WMFormRow
										baseStyle={ Object.assign({}, styles.formRow, styles.address1FormRow) }
										fieldStyle={ styles.address1FormField }
										labelText="Address *"
										data-component-identifier="settings__taxAddressRow"
										id="tax-address"
									>
										<WMTextField
											id="tax-address-street"
											data-component-identifier="settings__taxAddress"
											hintText="Street"
											floatingLabelText="Street"
											value={ tax.getIn(['address', 'value']) }
											onChange={ (event, value) => onChangeField('address', value) }
											onBlur={ () => onBlurField('address', tax.getIn(['address', 'value'])) }
											errorText={ tax.getIn(['address', 'error']) }
											fullWidth
										/>
									</WMFormRow>
									<WMFormRow
										baseStyle={ styles.formRow }
										fieldStyle={ styles.address2FormField }
										data-component-identifier="settings__taxAddress2"
										id="tax-classification-wrap"
									>
										<WMTextField
											id="tax-address-city"
											data-component-identifier="settings__taxCity"
											hintText="City"
											floatingLabelText="City"
											value={ tax.getIn(['city', 'value']) }
											errorText={ tax.getIn(['city', 'error']) }
											onChange={ (event, value) => onChangeField('city', value) }
											onBlur={ () => onBlurField('city', tax.getIn(['city', 'value'])) }
											style={ styles.addressRow2Field }
										/>
										<WMStateProvince
											locale="us"
											hintText="State"
											hide={ 'fullText' }
											floatingLabelText="State"
											value={ tax.getIn(['state', 'value']) }
											onChange={ (event, index, value) => onChangeField('state', value) }
											style={ styles.addressRow2Field }
										/>
										<WMTextField
											id="tax-address-zip"
											data-component-identifier="settings__taxPostalCode"
											hintText="Zip"
											floatingLabelText="Zip"
											value={ tax.getIn(['postalCode', 'value']) }
											onChange={ (event, value) => onChangeField('postalCode', value) }
											onBlur={ () => onBlurField('postalCode', tax.getIn(['postalCode', 'value'])) }
											errorText={ tax.getIn(['postalCode', 'error']) }
										/>
									</WMFormRow>
									<WMFormRow
										baseStyle={ styles.classificationFormRow }
										fieldStyle={ styles.classificationFormField }
										labelText="Federal Tax Classification"
										data-component-identifier="settings__tax-classification"
										id="tax-classification-wrap"
									>
										<WMRadioButtonGroup
											onChange={ (event, value) => onChangeField('taxEntityTypeCode', value) }
											valueSelected={ radioSelectedTaxEntity() }
											name="taxEntityTypeCodeGroup"
											fullWidth
										>
											<WMRadioButton
												label="Limited Liability Company"
												value={ 'llc-c-corp' }
											/>
											<WMRadioButton
												value={ 'individual' }
												label="Sole Proprietor"
											/>
											<WMRadioButton
												value={ 'c_corp' }
												label="C Corporation"
											/>
											<WMRadioButton
												value={ 's_corp' }
												label="S Corporation"
											/>
											<WMRadioButton
												value={ 'partner' }
												label="Partnership"
											/>
											<WMRadioButton
												value={ 'trust' }
												label="Trust/Estate"
											/>
										</WMRadioButtonGroup>
									</WMFormRow>
									{ (tax.getIn(['taxEntityTypeCode', 'value']) === 'llc-c-corp' ||
										tax.getIn(['taxEntityTypeCode', 'value']) === 'llc-s-corp' ||
										tax.getIn(['taxEntityTypeCode', 'value']) === 'llc-part') && (
											<WMFormRow
												baseStyle={ styles.classificationFormRow }
												fieldStyle={ styles.classificationFormField }
												labelText="Federal Tax Classification"
												data-component-identifier="settings__tax-classification-llc"
												id="tax-classification-wrap-llc"
											>
												<WMSelectField
													onChange={ (event, index, value) => onChangeField('taxEntityTypeCode', value) }
													value={ tax.getIn(['taxEntityTypeCode', 'value']) }
													name="taxEntityTypeCodeGroupLLC"
													fullWidth
												>
													<WMMenuItem value={ 'llc-c-corp' } primaryText="C Corporation" />
													<WMMenuItem value={ 'llc-s-corp' } primaryText="S Corporation" />
													<WMMenuItem value={ 'llc-part' } primaryText="Partnership" />
												</WMSelectField>
											</WMFormRow>
										)
									}
									<WMFormRow
										baseStyle={ Object.assign({}, styles.formRow, styles.dbaFormRow) }
										fieldStyle={ styles.dbaFormField }
										labelText="Do you have a business name or disregarded entity name that is different than the name that you provided above, such as a doing business as (DBA) name?"
										id="dba-wrap"
										data-component-identifier="settings__dbaRow"
									>
										<WMRadioButtonGroup
											valueSelected={ tax.getIn(['businessNameFlag', 'value']) }
											name="dba"
											data-component-identifier="settings__dbaGroup"
											onChange={ (event, value) => onChangeField('businessNameFlag', value) }
											id="dba-group"
										>
											<WMRadioButton
												label="Yes"
												value
												data-component-identifier="settings__dbaRadio"
											/>
											<WMRadioButton
												label="No"
												value={ false }
												data-component-identifier="settings__dbaRadio"
											/>
										</WMRadioButtonGroup>
									</WMFormRow>
									{ tax.getIn(['businessNameFlag', 'value']) &&
										<WMFormRow
											baseStyle={ styles.formRow }
											fieldStyle={ styles.dbaNameFormField }
											labelText="Business name / Disregarded entity name"
											id="dbaName-wrap"
											data-component-identifier="settings__dbaNameRow"
										>
											<WMTextField
												id="tax-dba-name"
												data-component-identifier="settings__taxDbaName"
												hintText="Only enter if different from above"
												value={ tax.getIn(['businessName', 'value']) }
												onChange={ (event, value) => onChangeField('businessName', value) }
												errorText={ tax.getIn(['businessName', 'error']) }
												fullWidth
											/>
										</WMFormRow>
									}
									<div style={ styles.agreement }>
										<WMCheckbox
											data-component-identifier="settings__policyAgreement"
											checked={ tax.getIn(['deliveryPolicyFlag', 'value']) }
											onCheck={ () => onChangeField('deliveryPolicyFlag', !tax.getIn(['deliveryPolicyFlag', 'value'])) }
											label="I read and agree to WorkMarket Electronic Communcations Delivery Policy stated in the Terms of Use.
														I understand that Work Market will provide me with information about my account electronically. I confirm that I can access emails, web pages, and PDF files."
										/>
									</div>
								</div>
						}
						{
							// maple leaves are toggled
							tax.get('taxCountry').get('value') === 'canada' &&
								<div>
									<WMFormRow
										baseStyle={ styles.formRow }
										labelText="Business Number*"
										data-component-identifier="settings__taxNumberRow"
										id="tax-number"
									>
										<WMTextField
											id="tax-number"
											data-component-identifier="settings__taxNumber"
											value={ tax.getIn(['taxNumber', 'value']) }
											errorText={ tax.getIn(['taxNumber', 'error']) }
											onChange={ (event, value) => onChangeField('taxNumber', value) }
											fullWidth
										/>
									</WMFormRow>
									<WMFormRow
										baseStyle={ styles.formRow }
										labelText="Company Name *"
										data-component-identifier="settings__companyTaxNameRow"
										id="tax-name"
									>
										<WMTextField
											id="tax-name"
											data-component-identifier="settings__companyTaxName"
											value={ tax.getIn(['taxName', 'value']) }
											errorText={ tax.getIn(['taxName', 'error']) }
											onChange={ (event, value) => onChangeField('taxName', value) }
											onBlur={ () => onBlurField('taxName', tax.getIn(['taxName', 'value'])) }
											fullWidth
										/>
									</WMFormRow>
									<WMFormRow
										baseStyle={ styles.formRow }
										fieldStyle={ styles.address1FormField }
										labelText="Address *"
										data-component-identifier="settings__taxAddressRow"
										id="tax-address"
									>
										<WMTextField
											id="tax-address-street"
											data-component-identifier="settings__taxAddress"
											hintText="Street"
											floatingLabelText="Street"
											value={ tax.getIn(['address', 'value']) }
											onChange={ (event, value) => onChangeField('address', value) }
											onBlur={ () => onBlurField('address', tax.getIn(['address', 'value'])) }
											errorText={ tax.getIn(['address', 'error']) }
											fullWidth
										/>
									</WMFormRow>
									<WMFormRow
										baseStyle={ styles.formRow }
										fieldStyle={ styles.address2FormField }
										data-component-identifier="settings__taxAddress2"
										id="tax-classification-wrap"
									>
										<WMTextField
											id="tax-address-city"
											data-component-identifier="settings__taxCity"
											hintText="City"
											floatingLabelText="City"
											value={ tax.getIn(['city', 'value']) }
											errorText={ tax.getIn(['city', 'error']) }
											onChange={ (event, value) => onChangeField('city', value) }
											onBlur={ () => onBlurField('city', tax.getIn(['city', 'value'])) }
											style={ styles.addressRow2Field }
										/>
										<WMStateProvince
											locale="ca"
											hintText="Province"
											hide={ 'fullText' }
											floatingLabelText="Province"
											value={ tax.getIn(['state', 'value']) }
											onChange={ (event, index, value) => onChangeField('state', value) }
											style={ styles.addressRow2Field }
										/>
										<WMTextField
											id="tax-address-zip"
											data-component-identifier="settings__taxPostalCode"
											hintText="Postal Code"
											floatingLabelText="Postal Code"
											onChange={ (event, value) => onChangeField('postalCode', value) }
											onBlur={ () => onBlurField('postalCode', tax.getIn(['postalCode', 'value'])) }
											value={ tax.getIn(['postalCode', 'value']) }
											errorText={ tax.getIn(['postalCode', 'error']) }
										/>
									</WMFormRow>
								</div>
						}
						{
							// other country is toggled
							tax.get('taxCountry').get('value') === 'other' &&
								<div>
									<WMFormRow
										baseStyle={ styles.formRow }
										labelText="Company Name"
										data-component-identifier="settings__companyTaxNameRow"
										id="tax-name"
									>
										<WMTextField
											id="tax-name"
											data-component-identifier="settings__companyTaxName"
											value={ tax.getIn(['taxName', 'value']) }
											errorText={ tax.getIn(['taxName', 'error']) }
											onChange={ (event, value) => onChangeField('taxName', value) }
											onBlur={ () => onBlurField('taxName', tax.getIn(['taxName', 'value'])) }
											fullWidth
										/>
									</WMFormRow>
									<WMFormRow
										baseStyle={ styles.formRow }
										labelText="Country of incorporation"
										data-component-identifier="settings__companyTaxCountryOfIncorporationRow"
										id="tax-name"
									>
										<WMCountrySelect
											id="tax-countryOfIncorporation"
											data-component-identifier="settings__companyTaxCountryOfIncorporation"
											value={ tax.getIn(['countryOfIncorporation', 'value']) }
											errorText={ tax.getIn(['countryOfIncorporation', 'error']) }
											isISO={ false }
											onChange={ (event, index, value) => onChangeField('countryOfIncorporation', value) }
											fullWidth
										/>
									</WMFormRow>
									<WMFormRow
										baseStyle={ styles.classificationFormRow }
										fieldStyle={ styles.classificationFormField }
										labelText="Type of beneficial owner"
										data-component-identifier="settings__tax-classification"
										id="tax-classification-wrap"
									>
										<WMRadioButtonGroup
											onChange={ (event, value) => onChangeField('taxEntityTypeCode', value) }
											valueSelected={ tax.getIn(['taxEntityTypeCode', 'value']) }
											name="taxEntityTypeCodeGroup"
											fullWidth
										>
											<WMRadioButton
												value={ 'llc-dis' }
												label="Disregarded entity"
											/>
											<WMRadioButton
												value={ 'corp' }
												label="Corporation"
											/>
											<WMRadioButton
												value={ 'partner' }
												label="Partnership"
											/>
										</WMRadioButtonGroup>
									</WMFormRow>
									<WMFormRow
										baseStyle={ styles.formRow }
										fieldStyle={ styles.address1FormField }
										labelText="Permanent residence address"
										data-component-identifier="settings__taxAddressRow"
										id="tax-address"
									>
										<WMTextField
											id="tax-address-street"
											data-component-identifier="settings__taxAddress"
											hintText="Street"
											floatingLabelText="Street"
											value={ tax.getIn(['address', 'value']) }
											onChange={ (event, value) => onChangeField('address', value) }
											onBlur={ () => onBlurField('address', tax.getIn(['address', 'value'])) }
											errorText={ tax.getIn(['address', 'error']) }
											fullWidth
										/>
									</WMFormRow>
									<WMFormRow
										baseStyle={ styles.formRow }
										fieldStyle={ styles.address2FormField }
										data-component-identifier="settings__taxAddress2"
										id="tax-classification-wrap"
									>
										<WMTextField
											id="tax-address-city"
											data-component-identifier="settings__taxCity"
											hintText="City or Town"
											floatingLabelText="City or Town"
											value={ tax.getIn(['city', 'value']) }
											errorText={ tax.getIn(['city', 'error']) }
											onChange={ (event, value) => onChangeField('city', value) }
											onBlur={ () => onBlurField('city', tax.getIn(['city', 'value'])) }
											style={ styles.addressRow2Field }
										/>
										<WMTextField
											hintText="State or Province"
											hintStyle={ { fontSize: '12px' } }
											data-component-identifier="settings__taxState"
											floatingLabelStyle={ { fontSize: '12px' } }
											floatingLabelText="State or Province"
											value={ tax.getIn(['state', 'value']) }
											onChange={ (event, value) => onChangeField('state', value) }
											style={ styles.addressRow2Field }
										/>
										<WMTextField
											id="tax-address-zip"
											data-component-identifier="settings__taxPostalCode"
											hintText="Postal Code"
											floatingLabelText="Postal Code"
											onChange={ (event, value) => onChangeField('postalCode', value) }
											onBlur={ () => onBlurField('postalCode', tax.getIn(['postalCode', 'value'])) }
											value={ tax.getIn(['postalCode', 'value']) }
											errorText={ tax.getIn(['postalCode', 'error']) }
										/>
									</WMFormRow>
									<WMFormRow
										baseStyle={ styles.formRow }
										labelText="National ID *"
										data-component-identifier="settings__taxNumberRow"
										id="tax-number"
									>
										<WMTextField
											id="tax-number"
											data-component-identifier="settings__taxNumber"
											value={ tax.getIn(['taxNumber', 'value']) }
											errorText={ tax.getIn(['taxNumber', 'error']) }
											onChange={ (event, value) => onChangeField('taxNumber', value) }
											fullWidth
										/>
									</WMFormRow>
								</div>
						}
					</WMCardText>
					<WMCardActions
						style={ styles.actions }
						data-component-identifier="settings__actions"
						expandable
					>
						<WMRaisedButton
							disabled={ settings.get('taxSubmitDisabled') || !formValid }
							data-component-identifier="settings__taxSubmit"
							onClick={ () => {
								if (isUS) {
									this.setState({ showFormModal: true });
								} else {
									onSubmitForm(tax);
								}
							} }
							label={ isUS ? 'Next' : 'Initiate Account Linking' }
							primary
						/>
					</WMCardActions>
				</WMCard>
			</div>
		);
	}
}

export default WMSettingsTax;

WMSettingsTax.propTypes = {
	info: PropTypes.shape({
		tax: PropTypes.instanceOf(Map),
		settings: PropTypes.instanceOf(Map)
	}).isRequired,
	onChangeField: PropTypes.func.isRequired,
	onBlurField: PropTypes.func.isRequired,
	onSubmitForm: PropTypes.func.isRequired,
	formValid: PropTypes.bool.isRequired
};

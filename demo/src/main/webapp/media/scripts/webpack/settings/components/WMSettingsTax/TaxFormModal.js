/* eslint-disable react/require-default-props */
import PropTypes from 'prop-types';
import React from 'react';
import {
	WMModal,
	WMFontIcon,
	WMTextField,
	WMFlatButton
} from '@workmarket/front-end-components';
import styles from './styles';

const TaxFormModal = ({
	open,
	taxName,
	dbaName,
	taxEntityTypeCode,
	address,
	addressTwo,
	eid,
	signature,
	signatureDateString,
	onSubmitForm,
	onChangeField,
	closeModal
}) => {
	const entityStyle = styles.taxEntityTypeCode[taxEntityTypeCode];
	const modalActions = [
		<WMFlatButton
			label="Go Back"
			secondary
			onClick={ () => closeModal() }
		/>,
		<WMFlatButton
			label="Initiate Account Linking"
			primary
			disabled={ !(signatureDateString && signature) }
			onClick={ () => onSubmitForm() }
		/>
	];

	return (
		<div>
			<WMModal
				open={ open }
				modal
				style={ styles.modal }
				bodyStyle={ styles.bodyStyle }
				contentStyle={ styles.bodyStyle }
				autoDetectWindowHeight
				autoScrollBodyContent
				actions={ modalActions }
			>
				<div
					style={ styles.taxFormBg }
				>
					<div
						style={ Object.assign({}, styles.w9Fields, styles.taxFormName) }
						id="taxFormName"
					>
						{ taxName }
					</div>
					<div
						style={ Object.assign({}, styles.w9Fields, styles.taxFormDba) }
						id="taxFormDba"
					>
						{ dbaName }
					</div>
					<div
						style={ Object.assign({}, styles.w9Fields, entityStyle) }
						id="taxFormEntity"
					>
						<WMFontIcon
							id="settings__taxCodeCheck"
							className="material-icons"
							color="#CCC"
							style={ styles.w9CheckStyle }
						>
							check
						</WMFontIcon>
					</div>
					<div
						style={ Object.assign({}, styles.w9Fields, styles.taxFormAddress) }
						id="taxFormAddress"
					>
						{ address }
					</div>
					<div
						style={ Object.assign({}, styles.w9Fields, styles.taxFormAddressTwo) }
						id="taxFormAddressTwo"
					>
						{ addressTwo }
					</div>
					<div
						style={ Object.assign({}, styles.w9Fields, styles.eid) }
						id="taxFormEid"
					>
						{ eid }
					</div>
					<WMTextField
						id="tax-signature"
						data-component-identifier="settings__taxSignature"
						style={ Object.assign({}, styles.w9Fields, styles.w9SignatureField) }
						inputStyle={ styles.w9SignatureFieldInputs }
						underlineShow={ false }
						value={ signature }
						onChange={ (event, value) => onChangeField('signature', value) }
					/>
					<WMTextField
						id="tax-signatureDateString"
						data-component-identifier="settings__taxSignatureDateString"
						style={ Object.assign({}, styles.w9Fields, styles.w9SignatureDateStringField) }
						inputStyle={ styles.w9SignatureFieldInputs }
						underlineShow={ false }
						value={ signatureDateString }
						onChange={ (event, value) => onChangeField('signatureDateString', value) }
					/>
				</div>
			</WMModal>
		</div>
	);
};

export default TaxFormModal;

TaxFormModal.propTypes = {
	open: PropTypes.bool.isRequired,
	taxName: PropTypes.string.isRequired,
	dbaName: PropTypes.string,
	taxEntityTypeCode: PropTypes.string.isRequired,
	address: PropTypes.string.isRequired,
	addressTwo: PropTypes.string.isRequired,
	eid: PropTypes.string.isRequired,
	signature: PropTypes.string.isRequired,
	signatureDateString: PropTypes.string.isRequired,
	onSubmitForm: PropTypes.func.isRequired,
	onChangeField: PropTypes.func.isRequired,
	closeModal: PropTypes.func.isRequired
};

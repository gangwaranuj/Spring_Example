import PropTypes from 'prop-types';
import React from 'react';
import { connect } from 'react-redux';
import {
		WMFormRow,
		WMMenuItem,
		WMRaisedButton,
		WMSelectField
} from '@workmarket/front-end-components';
import * as actions from '../../actions';

const SignatureRequirement = ({
	handleChange,
	applyRequirement,
	requirementComponentData
	}) => {
	const {
		eSignature,
		eSignatures
		} = requirementComponentData;
	const eSignatureList = eSignatures.map(eSignatureMap => (
		<WMMenuItem
			key={ eSignatureMap.id }
			value={ eSignatureMap.id }
			primaryText={ eSignatureMap.title }
		/>
	));
	return (
		<WMFormRow id="requirements-eSignature" >
			<div style={ { display: 'flex', flexDirection: 'column' } } >
				<WMSelectField
					onChange={ (event, index, value) => handleChange(value) }
					fullWidth
					name="E-Signature"
					hintText="Select an eSignature template"
					value={ eSignature }
				>
					{ eSignatureList }
				</WMSelectField>
				<div style={ { flexDirection: 'row-reverse', display: 'flex' } }>
					<WMRaisedButton
						secondary
						label="Add this requirement"
						style={ { margin: '1em 0 0 1em' } }
						disabled={ !eSignature }
						onClick={ () => {
							let esignature = eSignatures.find(signatureItem => (signatureItem.id === eSignature));
							const data = {
								id: esignature.id,
								name: esignature.title,
								$type: 'EsignatureRequirement',
								requirable: Object.assign(esignature, { name: esignature.title }),
								$humanTypeName: 'eSignature',
								templateUuid: esignature.templateUuid
							};
							applyRequirement(data);
						} }
					/>
				</div>
			</div>
		</WMFormRow>
	);
};

const mapStateToProps = ({ requirementsData }) => ({
	requirementComponentData: requirementsData.toJS()
});

const mapDispatchToProps = (dispatch) => {
	return {
		handleChange: (value) => {
			dispatch(actions.changeRequirementField('eSignature', value));
		},
		applyRequirement: (data) => {
			dispatch(actions.addRequirement(data));
		}
	};
};

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(SignatureRequirement);

SignatureRequirement.propTypes = {
	handleChange: PropTypes.func.isRequired,
	applyRequirement: PropTypes.func.isRequired,
	requirementComponentData: PropTypes.shape({
		eSignature: PropTypes.string,
		eSignatures: PropTypes.array.isRequired
	})
};

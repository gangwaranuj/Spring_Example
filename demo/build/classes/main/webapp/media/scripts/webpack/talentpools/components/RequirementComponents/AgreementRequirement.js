import PropTypes from 'prop-types';
import React from 'react';
import { connect } from 'react-redux';
import { WMRaisedButton, WMFormRow, WMSelectField, WMMenuItem } from '@workmarket/front-end-components';
import * as actions from '../../actions';

const AgreementRequirement = ({
	handleChange,
	applyRequirement,
	requirementComponentData
	}) => {
	const {
		agreement,
		agreements
		} = requirementComponentData;
	const agreementList = agreements.map(agreementMap => (
		<WMMenuItem
			key={ agreementMap.id }
			value={ agreementMap.id }
			primaryText={ agreementMap.name }
		/>
	));
	return (
		<WMFormRow id="requirements-agreement" >
			<div style={ { display: 'flex', flexDirection: 'column' } } >
				<WMSelectField
					onChange={ (event, index, value) => handleChange(value) }
					fullWidth
					name="agreement"
					hintText="Select an agreement"
					value={ agreement }
				>
					{ agreementList }
				</WMSelectField>
				<div style={ { flexDirection: 'row-reverse', display: 'flex' } }>
					<WMRaisedButton
						secondary
						label="Add this requirement"
						style={ { margin: '1em 0 0 1em' } }
						disabled={ !agreement }
						onClick={ () => {
							const data = {
								name: agreements.find(agreementItem => (agreementItem.id === agreement)).name,
								$type: 'AgreementRequirement',
								requirable: agreements.find(agreementItem => (agreementItem.id === agreement)),
								$humanTypeName: 'Agreement'
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
			dispatch(actions.changeRequirementField('agreement', value));
		},
		applyRequirement: (data) => {
			dispatch(actions.addRequirement(data));
		}
	};
};

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(AgreementRequirement);

AgreementRequirement.propTypes = {
	handleChange: PropTypes.func.isRequired,
	applyRequirement: PropTypes.func.isRequired,
	requirementComponentData: PropTypes.shape({
		agreement: PropTypes.string,
		agreements: PropTypes.array.isRequired
	})
};

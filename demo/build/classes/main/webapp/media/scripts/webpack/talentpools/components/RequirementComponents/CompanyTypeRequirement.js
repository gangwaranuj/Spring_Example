import PropTypes from 'prop-types';
import React from 'react';
import { connect } from 'react-redux';
import { WMFormRow, WMMenuItem, WMRaisedButton, WMSelectField } from '@workmarket/front-end-components';
import * as actions from '../../actions';

const CompanyTypeRequirement = ({
	handleChange,
	applyRequirement,
	requirementComponentData
}) => {
	const {
		companyType,
		companyTypes
		} = requirementComponentData;
	const companyTypeList = companyTypes.map(companyTypeMap => (
		<WMMenuItem
			key={ companyTypeMap.id }
			value={ companyTypeMap.id }
			primaryText={ companyTypeMap.name }
		/>
	));
	return (
		<WMFormRow id="requirements-companyType" >
			<div style={ { display: 'flex', flexDirection: 'column' } } >
				<WMSelectField
					onChange={ (event, index, value) => handleChange(value) }
					fullWidth
					name="companyType"
					hintText="Select a company type"
					value={ companyType }
				>
					{ companyTypeList }
				</WMSelectField>
				<div style={ { flexDirection: 'row-reverse', display: 'flex' } }>
					<WMRaisedButton
						secondary
						label="Add this requirement"
						style={ { margin: '1em 0 0 1em' } }
						disabled={ !companyType }
						onClick={ () => {
							const data = {
								name: companyTypes.find(companyTypeItem =>
									(companyTypeItem.id === companyType)).name,
								$type: 'CompanyTypeRequirement',
								requirable: companyTypes.find(companyTypeItem =>
									(companyTypeItem.id === companyType)),
								$humanTypeName: 'Company Type'
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
			dispatch(actions.changeRequirementField('companyType', value));
		},
		applyRequirement: (data) => {
			dispatch(actions.addRequirement(data));
		}
	};
};

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(CompanyTypeRequirement);

CompanyTypeRequirement.propTypes = {
	handleChange: PropTypes.func.isRequired,
	applyRequirement: PropTypes.func.isRequired,
	requirementComponentData: PropTypes.shape({
		companyType: PropTypes.number,
		companyTypes: PropTypes.array.isRequired
	})
};

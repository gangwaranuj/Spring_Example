import PropTypes from 'prop-types';
import React from 'react';
import { connect } from 'react-redux';
import { WMRaisedButton, WMFormRow, WMSelectField, WMMenuItem } from '@workmarket/front-end-components';
import * as actions from '../../actions';

const IndustryRequirement = ({
	handleChange,
	applyRequirement,
	requirementComponentData }) => {
	const {
		industry,
		industries
	} = requirementComponentData;
	const industryList = industries.map(industryMap => (
		<WMMenuItem
			key={ industryMap.id }
			value={ industryMap.id }
			primaryText={ industryMap.name }
		/>
	));
	return (
		<WMFormRow id="requirements-industry" >
			<div style={ { display: 'flex', flexDirection: 'column' } } >
				<WMSelectField
					fullWidth
					onChange={ (event, index, value) => handleChange(value) }
					name="industry"
					hintText="Select an industry"
					value={ industry }
				>
					{ industryList }
				</WMSelectField>
				<div style={ { flexDirection: 'row-reverse', display: 'flex' } }>
					<WMRaisedButton
						secondary
						disabled={ !industry }
						label="Add this requirement"
						style={ { margin: '1em 0 0 1em' } }
						onClick={ () => {
							const data = {
								name: industries.find(industryItem =>
									(industryItem.id === industry)).name,
								$type: 'IndustryRequirement',
								$humanTypeName: 'Industry',
								requirable: industries.find(industryItem =>
									(industryItem.id === industry))
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
			dispatch(actions.changeRequirementField('industry', value));
		},
		applyRequirement: (data) => {
			dispatch(actions.addRequirement(data));
		}
	};
};

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(IndustryRequirement);

IndustryRequirement.propTypes = {
	handleChange: PropTypes.func.isRequired,
	applyRequirement: PropTypes.func.isRequired,
	requirementComponentData: PropTypes.shape({
		industry: PropTypes.number,
		industries: PropTypes.array.isRequired
	})
};

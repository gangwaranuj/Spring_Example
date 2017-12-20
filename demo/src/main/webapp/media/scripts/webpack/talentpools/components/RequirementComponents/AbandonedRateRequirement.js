import PropTypes from 'prop-types';
import React from 'react';
import { connect } from 'react-redux';
import { WMRaisedButton, WMFormRow, WMSlider } from '@workmarket/front-end-components';
import * as actions from '../../actions';

const AbandonedRateRequirement = ({
	sliderChange,
	applyRequirement,
	requirementComponentData
}) => {
	const {
		maxAbandonedValue
	} = requirementComponentData;

	return (
		<WMFormRow
			data-component-identifier="requirements_abandonedRow"
			id="requirements-abandoned"
		>
			<div style={ { display: 'flex', flexDirection: 'column' } } >
				<div>Maximum Number of Acceptable Abandoned Assignments</div>
				<div style={ { alignSelf: 'flex-end' } }>{ maxAbandonedValue }</div>
				<div style={ { margin: '-1em 0' } } >
					<WMSlider
						data-component-identifier="requirements_abandonedSlider"
						max={ 50 }
						min={ 0 }
						name={ 'maxAbandonedValue' }
						onChange={ (event, value) => sliderChange(value) }
						value={ maxAbandonedValue }
						step={ 1 }
					/>
				</div>
				<div style={ { flexDirection: 'row-reverse', display: 'flex' } }>
					<WMRaisedButton
						secondary
						data-component-identifier="requirements_buttons"
						label="Add this requirement"
						style={ { margin: '1em 0 0 1em' } }
						disabled={ !maxAbandonedValue }
						onClick={ () => {
							const data = {
								name: `${maxAbandonedValue} within last 6 months`,
								$type: 'AbandonRequirement',
								maximumAllowed: maxAbandonedValue,
								$humanTypeName: 'Maximum Abandoned Assignments'
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
	requirementComponentData: requirementsData.toObject()
});

const mapDispatchToProps = (dispatch) => {
	return {
		sliderChange: (value) => {
			dispatch(actions.changeRequirementField('maxAbandonedValue', value));
		},
		applyRequirement: (data) => {
			dispatch(actions.addRequirement(data));
		}
	};
};

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(AbandonedRateRequirement);

AbandonedRateRequirement.propTypes = {
	sliderChange: PropTypes.func.isRequired,
	applyRequirement: PropTypes.func.isRequired,
	requirementComponentData: PropTypes.shape({
		maxAbandonedValue: PropTypes.number
	}).isRequired
};

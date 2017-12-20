import PropTypes from 'prop-types';
import React from 'react';
import { connect } from 'react-redux';
import { WMRaisedButton, WMFormRow, WMSlider } from '@workmarket/front-end-components';
import * as actions from '../../actions';

const CancelledRateRequirement = ({
	sliderChange,
	applyRequirement,
	requirementComponentData
}) => {
	const {
		maxCancelledValue
	} = requirementComponentData;

	return (
		<WMFormRow
			data-component-identifier="requirements_cancelledRow"
			id="requirements-cancelled"
		>
			<div style={ { display: 'flex', flexDirection: 'column' } } >
				<div>Maximum Number of Acceptable Cancelled Assignments</div>
				<div style={ { alignSelf: 'flex-end' } }>{ maxCancelledValue }</div>
				<div style={ { margin: '-1em 0' } } >
					<WMSlider
						data-component-identifier="requirements_cancelledSlider"
						max={ 50 }
						min={ 0 }
						name={ 'maxCancelledValue' }
						onChange={ (event, value) => sliderChange(value) }
						value={ maxCancelledValue }
						step={ 1 }
					/>
				</div>
				<div style={ { flexDirection: 'row-reverse', display: 'flex' } }>
					<WMRaisedButton
						secondary
						data-component-identifier="requirements_buttons"
						label="Add this requirement"
						style={ { margin: '1em 0 0 1em' } }
						disabled={ !maxCancelledValue }
						onClick={ () => {
							const data = {
								name: `${maxCancelledValue} within last 6 months`,
								$type: 'CancelledRequirement',
								maximumAllowed: maxCancelledValue,
								$humanTypeName: 'Maximum Cancelled Assignments'
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
			dispatch(actions.changeRequirementField('maxCancelledValue', value));
		},
		applyRequirement: (data) => {
			dispatch(actions.addRequirement(data));
		}
	};
};

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(CancelledRateRequirement);

CancelledRateRequirement.propTypes = {
	sliderChange: PropTypes.func.isRequired,
	applyRequirement: PropTypes.func.isRequired,
	requirementComponentData: PropTypes.shape({
		maxCancelledValue: PropTypes.number
	}).isRequired
};

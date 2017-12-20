import PropTypes from 'prop-types';
import React from 'react';
import { connect } from 'react-redux';
import { WMRaisedButton, WMFormRow, WMSlider } from '@workmarket/front-end-components';
import * as actions from '../../actions';

const OnTimeRequirement = ({
	sliderChange,
	applyRequirement,
	requirementComponentData
}) => {
	const {
		minOnTimePercentage
	} = requirementComponentData;
	return (
		<WMFormRow
			data-component-identifier="requirements_formRow"
			id="requirements-onTime"
		>
			<div style={ { display: 'flex', flexDirection: 'column' } } >
				<div>Minimum On Time Percentage</div>
				<div style={ { alignSelf: 'flex-end' } }>{ minOnTimePercentage } %</div>
				<div style={ { margin: '-1em 0' } } >
					<WMSlider
						data-component-identifier="requirements_slider"
						description="Minimum On Time Percentage"
						max={ 100 }
						min={ 0 }
						name={ 'minOnTimePercentage' }
						onChange={ (event, value) => sliderChange(value) }
						value={ minOnTimePercentage }
						step={ 1 }
					/>
				</div>
				<div style={ { flexDirection: 'row-reverse', display: 'flex' } }>
					<WMRaisedButton
						secondary
						data-component-identifier="requirements_buttons"
						label="Add this requirement"
						style={ { margin: '1em 0 0 1em' } }
						disabled={ !minOnTimePercentage }
						onClick={ () => {
							const data = {
								name: `${minOnTimePercentage}% within last 3 months`,
								$type: 'OntimeRequirement',
								minimumPercentage: minOnTimePercentage,
								$humanTypeName: 'Minimum On-time Arrival'
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
			dispatch(actions.changeRequirementField('minOnTimePercentage', value));
		},
		applyRequirement: (data) => {
			dispatch(actions.addRequirement(data));
		}
	};
};

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(OnTimeRequirement);

OnTimeRequirement.propTypes = {
	sliderChange: PropTypes.func.isRequired,
	applyRequirement: PropTypes.func.isRequired,
	requirementComponentData: PropTypes.shape({
		minOnTimePercentage: PropTypes.number
	}).isRequired
};

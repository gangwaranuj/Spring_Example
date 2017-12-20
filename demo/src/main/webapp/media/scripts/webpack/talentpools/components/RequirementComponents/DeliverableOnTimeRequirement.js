import PropTypes from 'prop-types';
import React from 'react';
import { connect } from 'react-redux';
import { WMRaisedButton, WMFormRow, WMSlider } from '@workmarket/front-end-components';
import * as actions from '../../actions';

const DeliverableOnTimeRequirement = ({
	sliderChange,
	applyRequirement,
	requirementComponentData
}) => {
	const {
		minDeliverableOnTimePercentage
	} = requirementComponentData;
	return (
		<WMFormRow
			data-component-identifier="requirements_formRow"
			id="requirements-deliverableOnTime"
		>
			<div style={ { display: 'flex', flexDirection: 'column' } } >
				<div>Minimum Deliverable On Time Percentage</div>
				<div style={ { alignSelf: 'flex-end' } }>{ minDeliverableOnTimePercentage } %</div>
				<div style={ { margin: '-1em 0' } } >
					<WMSlider
						data-component-identifier="requirements_slider"
						max={ 100 }
						min={ 0 }
						name={ 'minDeliverableOnTimePercentage' }
						onChange={ (event, value) => sliderChange(value) }
						value={ minDeliverableOnTimePercentage }
						step={ 1 }
					/>
				</div>
				<div style={ { flexDirection: 'row-reverse', display: 'flex' } }>
					<WMRaisedButton
						secondary
						data-component-identifier="requirements_buttons"
						label="Add this requirement"
						style={ { margin: '1em 0 0 1em' } }
						disabled={ !minDeliverableOnTimePercentage }
						onClick={ () => {
							const data = {
								$type: 'DeliverableOnTimeRequirement',
								$humanTypeName: 'Minimum On-time Deliverables',
								minimumPercentage: minDeliverableOnTimePercentage,
								name: `${minDeliverableOnTimePercentage}% within last 3 months`
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
			dispatch(actions.changeRequirementField('minDeliverableOnTimePercentage', value));
		},
		applyRequirement: (data) => {
			dispatch(actions.addRequirement(data));
		}
	};
};

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(DeliverableOnTimeRequirement);

DeliverableOnTimeRequirement.propTypes = {
	sliderChange: PropTypes.func.isRequired,
	applyRequirement: PropTypes.func.isRequired,
	requirementComponentData: PropTypes.shape({
		minDeliverableOnTimePercentage: PropTypes.number
	}).isRequired
};

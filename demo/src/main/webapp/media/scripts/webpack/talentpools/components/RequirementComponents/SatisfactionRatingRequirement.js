import PropTypes from 'prop-types';
import React from 'react';
import { connect } from 'react-redux';
import { WMRaisedButton, WMFormRow, WMSlider } from '@workmarket/front-end-components';
import * as actions from '../../actions';

const SatisfactionRatingRequirement = ({
	sliderChange,
	applyRequirement,
	requirementComponentData
}) => {
	const {
		minSatisfactionRating
	} = requirementComponentData;

	return (
		<WMFormRow
			id="requirement__satisfactionrating"
			data-component-identifier="requirements_formRow"
		>
			<div style={ { display: 'flex', flexDirection: 'column' } } >
				<div>Minimum Satisfaction Rating</div>
				<div style={ { alignSelf: 'flex-end' } }>{ minSatisfactionRating } %</div>
				<div style={ { margin: '-1em 0' } }>
					<WMSlider
						data-component-identifier="requirements_slider"
						description="Minimum Satisfaction Rating"
						max={ 100 }
						min={ 0 }
						name={ 'minSatisfactionRating' }
						onChange={ (event, value) => sliderChange(value) }
						value={ minSatisfactionRating }
						step={ 1 }
					/>
				</div>
				<div style={ { flexDirection: 'row-reverse', display: 'flex' } }>
					<WMRaisedButton
						secondary
						data-component-identifier="requirements_buttons"
						label="Add this requirement"
						style={ { margin: '1em 0 0 1em' } }
						disabled={ !minSatisfactionRating }
						onClick={ () => {
							const data = {
								$type: 'RatingRequirement',
								name: minSatisfactionRating,
								value: minSatisfactionRating,
								$humanTypeName: 'Minimum Satisfaction Rating'
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
			dispatch(actions.changeRequirementField('minSatisfactionRating', value));
		},
		applyRequirement: (data) => {
			dispatch(actions.addRequirement(data));
		}
	};
};

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(SatisfactionRatingRequirement);

SatisfactionRatingRequirement.propTypes = {
	sliderChange: PropTypes.func.isRequired,
	applyRequirement: PropTypes.func.isRequired,
	requirementComponentData: PropTypes.shape({
		minSatisfactionRating: PropTypes.number
	}).isRequired
};

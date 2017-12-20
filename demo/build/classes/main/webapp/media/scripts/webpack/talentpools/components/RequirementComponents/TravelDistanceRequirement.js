import PropTypes from 'prop-types';
import React from 'react';
import { connect } from 'react-redux';
import { WMRaisedButton, WMFormRow, WMSlider } from '@workmarket/front-end-components';
import * as actions from '../../actions';
import WMGoogleLocationInput from '../../../components/WMGoogleLocationInput';

const TravelDistanceRequirement = ({
	sliderChange,
	applyRequirement,
	onGoogleAPILoaded,
	onChangeGoogleAddress,
	requirementComponentData
}) => {
	const {
		maxTravelDistanceValue,
		travelDistanceLocation,
		googleInitialized } = requirementComponentData;
	const { address1,
		city,
		state,
		country,
		postalCode,
		latitude,
		longitude } = travelDistanceLocation;
	const hasAddress1 = address1 !== undefined && address1 !== '';
	const startOfLocation = hasAddress1 ? `${address1}, ` : '';
	const location = `${startOfLocation}${city}, ${state}, ${postalCode}, ${country}`;
	return (
		<WMFormRow
			data-component-identifier="requirements_row"
			id="requirements-abandoned"
		>
			<div style={ { display: 'flex', flexDirection: 'column' } } >
				<div>Distance</div>
				<div style={ { alignSelf: 'flex-end' } }>{ maxTravelDistanceValue } miles</div>
				<div style={ { margin: '-1em 0' } } >
					<WMSlider
						data-component-identifier="requirements_slider"
						max={ 100 }
						min={ 10 }
						name={ 'maxTravelDistanceValue' }
						onChange={ (event, value) => sliderChange(value) }
						value={ maxTravelDistanceValue }
						step={ 1 }
					/>
				</div>
				<WMGoogleLocationInput
					data-component-identifier="requirements__travelDistanceLocation"
					value={ location }
					googleInitialized={ googleInitialized }
					onGoogleAPILoaded={ onGoogleAPILoaded }
					changeGoogleAddress={ onChangeGoogleAddress }
				/>
				<div style={ { flexDirection: 'row-reverse', display: 'flex' } }>
					<WMRaisedButton
						secondary
						data-component-identifier="requirements_button"
						label="Add this requirement"
						style={ { margin: '1em 0 0 1em' } }
						disabled={ !postalCode }
						onClick={ () => {
							const data = {
								$type: 'TravelDistanceRequirement',
								$humanTypeName: 'Travel Distance',
								distance: maxTravelDistanceValue,
								address: location,
								latitude,
								longitude,
								name: `Within ${maxTravelDistanceValue} miles of ${location}`
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
		sliderChange: (value) => {
			dispatch(actions.changeRequirementField('maxTravelDistanceValue', value));
		},
		applyRequirement: (data) => {
			dispatch(actions.addRequirement(data));
		},
		cancelRequirement: () => {
			dispatch(actions.cancelRequirement());
		},
		onGoogleAPILoaded: () => {
			dispatch(actions.googleAPILoaded());
		},
		onChangeGoogleAddress: (addressObj) => {
			Object.keys(addressObj).forEach((name) => {
				dispatch(actions.changeLocationField('travelDistanceLocation', name, addressObj[name]));
			});
		}
	};
};

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(TravelDistanceRequirement);

TravelDistanceRequirement.propTypes = {
	sliderChange: PropTypes.func.isRequired,
	applyRequirement: PropTypes.func.isRequired,
	onGoogleAPILoaded: PropTypes.func.isRequired,
	onChangeGoogleAddress: PropTypes.func.isRequired,
	requirementComponentData: PropTypes.shape({
		maxTravelDistanceValue: PropTypes.number.isRequired,
		travelDistanceLocation: PropTypes.shape({
			address1: PropTypes.string,
			city: PropTypes.string,
			state: PropTypes.string,
			postalCode: PropTypes.string,
			country: PropTypes.string,
			longitude: PropTypes.number,
			latitude: PropTypes.number
		})
	})
};

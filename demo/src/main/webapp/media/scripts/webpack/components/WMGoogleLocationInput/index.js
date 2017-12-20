import PropTypes from 'prop-types';
import React from 'react';
import loadGoogleMapsAPI from 'load-google-maps-api';
import { WMTextField } from '@workmarket/front-end-components';
import GooglePlaces from '../../funcs/googlePlaces';

export default class WMGoogleLocationInput extends React.Component {
	constructor (props) {
		super(props);
		this.handleGoogleAddress = this.handleGoogleAddress.bind(this);
	}

	componentDidMount () {
		if (!this.props.googleInitialized) {
			loadGoogleMapsAPI({
				key: GOOGLE_MAPS_API_TOKEN,
				libraries: 'places'
			}).then(() => {
				this.googlePlaces = new GooglePlaces(null, this.handleGoogleAddress);
				this.props.onGoogleAPILoaded();
			});
		} else {
			this.googlePlaces = new GooglePlaces(null, this.handleGoogleAddress);
		}
	}

	handleGoogleAddress (addressObj) {
		this.props.changeGoogleAddress(addressObj);
	}

	render () {
		return (
			<div>
				<div id="location-typeahead">
					<WMTextField
						id="addressTyper"
						fullWidth
					/>
				</div>
			</div>
		);
	}
}

WMGoogleLocationInput.propTypes = {
	googleInitialized: PropTypes.bool.isRequired,
	onGoogleAPILoaded: PropTypes.func.isRequired,
	changeGoogleAddress: PropTypes.func.isRequired
};

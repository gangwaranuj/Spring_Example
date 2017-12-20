/*jshint camelcase:false*/

'use strict';
import Template from './templates/location.hbs';
import $ from 'jquery';
import _ from 'underscore';
import OnboardSlideView from './onboard_slide_view';
import wmFloatLabel from '../funcs/wmFloatLabel';
import googleMap from '../funcs/googleMap';
import loadGoogleMapsAPI from 'load-google-maps-api';
import wmSlider from '../funcs/wmSlider';
import wmFetchNearbyAssignments from '../funcs/wmFetchNearbyAssignments';

export default OnboardSlideView.extend({
	el: '#locationView .wm-modal--content',
	template: Template,
	events: _.defaults({
		'change [name="maxWorkTravelRadius"]' : 'setMaxWorkTravelRadius'
	}, OnboardSlideView.prototype.events),

	render: function () {
		this.$el.html(this.template({
			address: this.model.get('address'),
			numberOfNearbyAssignments: this.model.get('numberOfNearbyAssignments'),
			isLocaleEnabled: window.workmarket.isLocaleEnabled
		}));

		loadGoogleMapsAPI({
			key: GOOGLE_MAPS_API_TOKEN,
			libraries: 'places'
		}).then(() => {
			// Create and bind the google map with the autocomplete
			this.googleMap = googleMap({
				map: this.$('.map').get(0),
				autocomplete: this.$('[name="address"]').get(0),
				autocompleteCallback: _.bind(this.changeGooglePlace, this)
			});
		});

		// Trigger all the floating label animations for populated fields
		wmFloatLabel({ root: this.el });
		wmSlider({ root: this.el });

		this.trigger('render');

		if (this.model.isValid()) {
			this.$('[data-slide="next"]').removeAttr('disabled');
		}

		return this;
	},

	setMaxWorkTravelRadius: function() {
		this.updateGoogleMapZoom();
		this.setMaxTravelDistance();
	},

	updateGoogleMapZoom: function () {
		var milesRadiusSliderValue = this.$('[name="maxWorkTravelRadius"]').val();
		if (!_.isUndefined(milesRadiusSliderValue)) {
			this.googleMap.updateRadius(Number(milesRadiusSliderValue));
		}
	},

	changeGooglePlace: function(place, isInit) {
		try {
			var location = place.geometry.location,
				coordinates = {
					latitude: location.lat(),
					longitude: location.lng()
				},
				hasStreetNumber = false,
				hasPostalCode = false,
				hasStreet = false,
				postalCode = '',
				country = '',
				state = '',
				city = '',
				address1 = '';

			if (isInit) { //only set lat/long since we have the rest of the data
				this.model.set(_.extend({}, coordinates));
				this.model.trigger('change');
				this.setNearbyAssignmentCount(coordinates);
				return;
			}

			if (place.address_components) {
				_.each(place.address_components, function (component) {
					_.each(component.types, function (type) {
						var shortName = component.short_name,
							longName = component.long_name,
							isShortNameNotNull = !_.isNull(shortName),
							isLongNameNotNull = !_.isNull(longName);

						if (type === 'postal_code' && isShortNameNotNull) {
							hasPostalCode = true;
							postalCode = shortName;
						}
						if (type === 'country' && isShortNameNotNull) {
							country = shortName;
						}
						if (type === 'administrative_area_level_1' && isShortNameNotNull) {
							state = shortName;
						}
						if (type === 'administrative_area_level_2' && isShortNameNotNull && state === '') {
							state = shortName;
						}
						if (type === 'locality' && longName != null) {
							city = longName;
						}
						if (type === 'sublocality' && isLongNameNotNull && city === '') {
							city = longName;
						}
						if (type === 'administrative_area_level_1' && isLongNameNotNull && city === '') {
							city = longName;
						}
						if (type === 'administrative_area_level_2' && isLongNameNotNull && city === '') {
							city = longName;
						}
						if (type === 'street_number' && isShortNameNotNull) {
							hasStreetNumber = true;
							address1 = shortName;
						}
						if (type === 'route' && isShortNameNotNull) {
							hasStreet = true;
							// When the street number entered isn't a real address, google doesn't return a street number component
							var prefix = address1.trim().length > 0 ? address1 = address1 + ' ' : '';
							address1 = prefix + shortName;
						}
						if (type === 'premise' && address1 === '' && isShortNameNotNull) {
							address1 = shortName;
						}
						if (type.indexOf('sublocality_level_') >= 0 &&  isShortNameNotNull) {
							if (parseInt(type.charAt(type.length-1)) > 1) { //only want sublocalities greater than 1
								address1 = address1.trim().length > 0 ? address1 + ' ' + shortName : shortName;
							}
						}
					});
				});

				if (!_.isUndefined(_.findWhere(place.address_components, { 'long_name': 'Puerto Rico' }))) {
					state = 'PR';
					country = 'USA';
				}
			}

			// If user did not provide us with address or postal code use lat long to reverse geo-code postal
			if (!hasPostalCode) {
				getReverseGeocodingDataFor('postal_code', coordinates.latitude, coordinates.longitude, function (zip) {
					postalCode = zip;
				});
			}

			/*
			If Google couldn't return a street number for the user's search,
			it's likely that the address entered doesn't exist,
			so suggest to the user a verifiable address.
			Do this only if a street was entered into autocomplete.
			*/
			if (!hasStreetNumber && hasStreet) {
				getReverseGeocodingDataFor('street_number', coordinates.latitude, coordinates.longitude, function (streetNumber) {
					this.$('.map').get(0).trigger('googleAutocompleteSuggestion', [{ streetNumber : streetNumber, street : address1 }]);
				});
			}

			this.updateGoogleMapZoom();

			this.$('.location-address').removeClass('-invalid');

			if (address1 === '' && place.vicinity) {
				//some have street address info + city in vicinity, check that
				const index = place.vicinity.indexOf(city) - 2; //minus space and comma
				address1 = index >= 0 ? place.vicinity.substring(0, index) : place.vicinity;
			}

			this.model.set(_.extend({
				address: place.formatted_address,
				address1: address1,
				city: city,
				countryIso: country,
				postalCode: postalCode,
				stateShortName: state
			}, coordinates));
			this.model.trigger('change');
			this.setNearbyAssignmentCount(coordinates);
		}
		catch (err) {
			this.$('.location-address').addClass('-invalid');
		}
	},

	setMaxTravelDistance: function () {
		this.model.set('maxTravelDistance', this.$('[name="maxWorkTravelRadius"]').val());
		this.setNearbyAssignmentCount();
	},

	setNearbyAssignmentCount: function (options) {
		options = _.extend({
			lat: this.model.get('latitude'),
			lon: this.model.get('longitude'),
			d: this.model.get('maxTravelDistance'),
			w: 'all',
			a: true,
			res: true
		}, options);

		var fetchedNearbyAssignments = wmFetchNearbyAssignments(options);
		var setNearbyAssignments = _.bind(function (data) {
			this.model.set('numberOfNearbyAssignments', data.totalCount);
			this.$('.number-of-assignments').text(data.totalCount);
			if (!_.isUndefined(this.googleMap)) {
				this.googleMap.updateMarkers(data.results);
			}
		}, this);

		$.when(fetchedNearbyAssignments).then(setNearbyAssignments);
	},

	setData: function (event) {
		var element = event.currentTarget,
			name = element.name,
			value = element.value;

		// Type cast some stringified booleans
		value = value === 'true' ? true : value;
		value = value === 'false' ? false : value;

		// In the event the location is set outside of google's autocomplete,
		// clear out all attributes, because you can't do that!
		if (name === 'address') {
			name = {
				address: value,
				address1: null,
				city: null,
				countryIso: null,
				postalCode: null,
				stateShortName: null
			};
			value = undefined;
		}

		// If set fails, returning false will prevent bubbling, so the error
		// notification won't display
		return this.model.set(name, value);
	}
});

function getReverseGeocodingDataFor(componentName, lat, lng, callback) {
	var latlng = new google.maps.LatLng(lat, lng),
		geocoder = new google.maps.Geocoder(); // This is the reverse geocode request';

	geocoder.geocode({ 'latLng': latlng }, function (results) {
		var componentVal = '';
		if (!_.isNull(results) && !_.isNull(results[0].address_components)) {

			var matchingComponent = _.filter(results[0].address_components, function (component) {
				return _.include(component.types, componentName)
			});

			if (matchingComponent.length > 0) {
				componentVal = matchingComponent[0].short_name;
			}

			if (componentVal.length > 0) {
				callback(componentVal);
			}
		}
	});
}

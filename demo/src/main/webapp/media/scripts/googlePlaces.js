var wm = wm || {};

wm.googlePlaces = function (parentView) {
	'use strict';

	var input = document.getElementById('addressTyper');
	var autocomplete = new google.maps.places.Autocomplete(input),
		$postalCode = $('#postalCode'),
		$country = $('#country'),
		$state = $('#state'),
		$city = $('#city'),
		$longitude = $('#longitude'),
		$latitude = $('#latitude'),
		$address1 = $('#address1');

	google.maps.event.addListener(autocomplete, 'place_changed', function () {
		$(parentView).trigger('clearMessages');
		fillInAddress();
	});

	function fillInAddress() {
		var place = autocomplete.getPlace();
		var lat = place.geometry.location.lat(),
			lng = place.geometry.location.lng(),
			hasStreetNumber = false,
			hasPostalCode = false,
			hasStreet = false;

		// Clear form
		_.each([$postalCode, $country, $state, $city, $longitude, $latitude, $address1], function (el) {
			el.val('');
		});

		if (place.address_components) {
			_.each(place.address_components, function (component) {
				_.each(component.types, function (type) {
					var shortName = component.short_name,
						longName = component.long_name,
						isShortNameNotNull = !_.isNull(shortName),
						isLongNameNotNull = !_.isNull(longName);

					if (type === 'postal_code' && isShortNameNotNull) {
						hasPostalCode = true;
						$postalCode.val(shortName);
					}
					if (type === 'country' && isShortNameNotNull) {
						$country.val(shortName);
					}
					if (type === 'administrative_area_level_1' && isShortNameNotNull) {
						$state.val(shortName);
					}
					if (type === 'administrative_area_level_2' && isShortNameNotNull && $state.val() === '') {
						$state.val(shortName);
					}
					if (type === 'locality' && longName != null) {
						$city.val(longName);
					}
					if (type === 'sublocality' && isLongNameNotNull && $city.val() === '') {
						$city.val(longName);
					}
					if (type === 'administrative_area_level_1' && isLongNameNotNull && $city.val() === '') {
						$city.val(longName);
					}
					if (type === 'administrative_area_level_2' && isLongNameNotNull && $city.val() === '') {
						$city.val(longName);
					}
					if (type === 'street_number' && isShortNameNotNull) {
						hasStreetNumber = true;
						$address1.val(shortName);
					}
					if (type === 'route' && isShortNameNotNull) {
						hasStreet = true;
						// When the street number entered isn't a real address, google doesn't return a street number component
						var prefix =  $address1.val().trim().length > 0 ? $address1.val() + ' ' : '';
						$address1.val(prefix + shortName);
					}
				});
			});

			if (!_.isUndefined(_.findWhere(place.address_components, { 'long_name': 'Puerto Rico' }))) {
				$state.val('PR');
				$country.val('USA');
			}
		}

		// If user did not provide us with address or postal code use lat long to reverse geo-code postal
		if (!hasPostalCode) {
			getReverseGeocodingDataFor('postal_code', lat, lng, function (postalCode) {
				$postalCode.val(postalCode);
			});
		}

		/*
		 If Google couldn't return a street number for the user's search,
		 it's likely that the address entered doesn't exist,
		 so suggest to the user a verifiable address.
		 Do this only if a street was entered into autocomplete.
		  */
		if (!hasStreetNumber && hasStreet && parentView !== undefined) {
			getReverseGeocodingDataFor('street_number', lat, lng, function (streetNumber) {
				$(parentView).trigger('googleAutocompleteSuggestion', [{ streetNumber : streetNumber, street : $address1.val() }]);
			});
		}

		if (place.formatted_address) {
			$('#formatted_address').val(place.formatted_address);
		}

		$latitude.val(lat);
		$longitude.val(lng);
	}

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

				if (componentVal.length > 0 ) {
					callback(componentVal);
				}
			}
		});
	}

	google.maps.event.addDomListener(input, 'keydown', function (e) {
		if (e.keyCode === 13) {
			if (e.preventDefault) {
				e.preventDefault();
			} else {
				// Since the google event handler framework does not handle early IE versions, we have to do it by our self. :-(
				e.cancelBubble = true;
				e.returnValue = false;
			}
		}
	});

};

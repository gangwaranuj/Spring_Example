'use strict';

import _ from 'underscore';

export default function (options) {

	var nearbyAssignmentMarkers = [],
		map, autocomplete, marker, mileRadiusCircle;

	var settings = _.extend({
		map: null,
		autocomplete: null,
		// Work Market
		// 254 W 31 St, New York, NY
		latitude: 40.749868,
		longitude: -73.994427,
		strokeColor: '#f5b25f',
		strokeOpacity: 0.8,
		strokeWeight: 3,
		fillColor: '#a6c8fb',
		fillOpacity: 0.4,
		zoom: 17, // Why 17? Because it looks good.
		styles: [
			{
				featureType: 'road',
				elementType: 'geometry',
				stylers: [
					{ color: '#cfcfcd' },
					{ lightness: 25 }
				]
			},
			{
				featureType: 'road.highway',
				elementType: 'geometry',
				stylers: [
					{ color: '#cfcfcd' },
					{ lightness: -10 }
				]
			},
			{
				featureType: 'water',
				elementType: 'geometry',
				stylers: [
					{ lightness: -5 }
				]
			}
		],
		markerUrl: '/media/images/placemark.png',
		nearbyAssignmentMarkerUrl: '/media/images/map/work_pin.png'
	}, options);

	// Sets the map on all markers in the array.
	var setAllMap = function (map) {
		_.each(nearbyAssignmentMarkers, function (marker) { marker.setMap(map); });
	};

	// Removes the markers from the map, but keeps them in the nearbyAssignmentMarkers array.
	var clearMarkers = function () {
		setAllMap(null);
	};

	// Deletes all markers in the nearbyAssignmentMarkers array by removing references to them.
	var deleteMarkers = function () {
		clearMarkers();
		nearbyAssignmentMarkers = [];
	};

	/******************
	 *** Public API ***
	 ******************/
	var googleMap = {
		/* updateZoom
		 *
		 * Changes the zoom level of the Google map.
		 *
		 * @param level - Google maps zoom level
		 * @return true
		 */
		updateZoom: function (level) {
			map.setZoom(level);

			return this;
		},

		updateMarkers: function (markerData) {
			deleteMarkers();

			nearbyAssignmentMarkers = _.map(markerData, function (dataPoint) {
				var location = new google.maps.LatLng(dataPoint.latitude, dataPoint.longitude);

				var marker = new google.maps.Marker({
					position: location,
					map: map,
					animation: google.maps.Animation.DROP
				});

				marker.setIcon(/** @type {google.maps.Icon} */({
					url: settings.nearbyAssignmentMarkerUrl,
					size: new google.maps.Size(71, 71),
					origin: new google.maps.Point(0, 0),
					anchor: new google.maps.Point(17, 34),
					scaledSize: new google.maps.Size(35, 35)
				}));

				return marker;
			});

			setAllMap(map);

			return this;
		},

		/* updateRadius
		 *
		 * Changes the bounds of the Google map to accommodate a radis of "distance".
		 *
		 * @param distance - unitless radius in terms of distance
		 * @param unit - string, plural unit of distance
		 * @return true
		 */
		updateRadius: function (distance, unit) {
			var conversionFactor;

			unit = _.isUndefined(unit) ? 'miles' : unit.toLowerCase();
			if (unit === 'meters') {
				conversionFactor = 1;
			} else if (unit === 'miles') {
				conversionFactor = 1609.34;
			}

			if (!_.isUndefined(mileRadiusCircle) && !_.isNull(mileRadiusCircle)) {
				// If a circle already exists on the map, remove it from the map
				mileRadiusCircle.setMap(null);
			}

			var distanceInMeters = distance * conversionFactor;

			mileRadiusCircle = new google.maps.Circle(_.defaults({
				radius: distanceInMeters,
				center: marker.getPosition(),
				map: map
			}, settings));

			map.fitBounds(mileRadiusCircle.getBounds());
			map.setCenter(map.getCenter());
			return this;
		},

		/* centerMap
		 *
		 * Changes the center of the Google map.
		 *
		 * @param location - LatLong object
		 * @param zoom (optional) - Google map zoom level
		 * @return true
		 */
		centerMap: function (location, zoom) {
			zoom = _.isUndefined(zoom) ? settings.zoom : zoom;

			map.setCenter(location);
			map.setZoom(zoom);

			return this;
		},

		/* setMarker
		 *
		 * Changes the location of the marker on the Google map.
		 *
		 * @param location - LatLong object
		 * @return true
		 */
		setMarker: function (location) {
			marker.setIcon(/** @type {google.maps.Icon} */({
				url: settings.markerUrl,
				size: new google.maps.Size(29, 34),
				origin: new google.maps.Point(0, 0),
				anchor: new google.maps.Point(14, 29)
			}));
			marker.setDraggable(true);
			marker.setPosition(location);
			marker.setVisible(true);

			return this;
		}
	};

	// Main method, which runs on instantiation. Creates the map, marker, and
	// autocomplete objects, and binds the autocomplete to the Google map.
	// Whenever the autocomplete changes, the map's center location and marker is
	// updated.
	var initialize = function () {
		settings.center = new google.maps.LatLng(settings.latitude, settings.longitude);

		map = new google.maps.Map(settings.map, settings);
		marker = new google.maps.Marker({ map: map });
		autocomplete = new google.maps.places.Autocomplete(settings.autocomplete);
		autocomplete.bindTo('bounds', map);

		google.maps.event.addListener(autocomplete, 'place_changed', _.bind(function () {
			var place = autocomplete.getPlace().geometry;
			if (place) {
				// If the place has a geometry, then present it on a map.
				if (place.viewport) {
					map.fitBounds(place.viewport);
				} else {
					this.centerMap(place.location);
				}
				this.setMarker(place.location);
			}
			if (settings.autocompleteCallback) {
				settings.autocompleteCallback(autocomplete.getPlace());
			}
		}, this));

		google.maps.event.addListener(marker, 'dragend', function () {
			var geocoder = new google.maps.Geocoder();
			geocoder.geocode({ 'latLng': marker.getPosition() }, _.bind(function (results, status) {
				if (status === google.maps.GeocoderStatus.OK) {
					var dragAddress = results[0].formatted_address;
					results[0].formatted_address = dragAddress;
					settings.autocomplete.value = dragAddress;
					if (settings.autocompleteCallback) {
						settings.autocompleteCallback(results[0]);
					}
				}
			}, this));
		});

		// Set the initial map view on a pre-populated location
		if (!_.isNull(settings.autocomplete) && settings.autocomplete.value) {
			var geocoder = new google.maps.Geocoder();
			geocoder.geocode({ 'address': settings.autocomplete.value }, _.bind(function (results, status) {
				if (status === google.maps.GeocoderStatus.OK) {
					var location = results[0].geometry.location;
					this.centerMap(location);
					this.setMarker(location);
					if (settings.autocompleteCallback) {
						settings.autocompleteCallback(results[0], true);
					}
				}
			}, this));
		}

		return this;
	};

	return initialize.call(googleMap);
};

import $ from 'jquery';
import _ from 'underscore';
import Backbone from 'backbone';
import pinInfoTemp from './templates/pin-info.hbs';
import 'js-marker-clusterer';

export default Backbone.View.extend({
	el: 'body',
	pinInfoTemplate: pinInfoTemp,

	events: {
		'click .map-status-button'     : 'refreshMap',
		'change #map-address-location' : 'relocateMap'
	},

	initialize() {
		_.bindAll(this, 'render');
		this.mapOptions = {
			zoom: 4,
			center: new google.maps.LatLng(40, -100),
			mapTypeId: google.maps.MapTypeId.ROADMAP
		};
		this.map = new google.maps.Map(document.getElementById('map-canvas'), this.mapOptions);
		this.markers = [];
		this.markerCluster = new MarkerClusterer(this.map, this.markers);

		this.render(this);
	},

	render(map) {
		this.status = $('#status').val();

		$.getJSON('map/useDashboardData', { status: map.status }, function (data) {
			var assignmentList = new Backbone.Collection();
			var marker, i;
			var infowindow = new google.maps.InfoWindow();

			_.each(map.markers, function (mark) {
				mark.setMap(null);
			});

			// efficiently clear the array
			map.markers.length = 0;
			map.markerCluster.clearMarkers();

			_.each(data, function (item) {
				var assignment = new Backbone.Model();
				assignment.set({
					lat: item.lat,
					lng: item.lng,
					title: item.title,
					price: item.price,
					rawStatus: item.rawStatus,
					status: item.status,
					scheduledDate: item.scheduledDate,
					workNumber: item.workNumber
				});
				assignmentList.add(assignment);
			});

			assignmentList.each(function (assignment) {
				var iconBase = '/media/images/';
				var icon = iconBase + assignment.get('rawStatus') + '_pin.png';
				marker = new google.maps.Marker({
					position: new google.maps.LatLng(assignment.get('lat'), assignment.get('lng')),
					icon: icon,
					map: map.map,
					detail_url: '/assignments/details/' + assignment.get('workNumber'),
					title: assignment.get('title'),
					scheduledDate: assignment.get('scheduledDate'),
					price: assignment.get('price'),
					rawStatus: assignment.get('rawStatus'),
					status: assignment.get('status')
				});
				map.markers.push(marker);
				google.maps.event.addListener(marker, 'click', (function (marker) {
					return function () {
						var content = $('<div>');
						content.append(map.pinInfoTemplate(marker));
						infowindow.setContent(content[0]);
						infowindow.open(map.map, marker);
					};
				})(marker));

			});
			map.markerCluster.addMarkers(map.markers);
		});
	},

	refreshMap(e) {
		$('#status').val($(e.target).val());
		this.render(this);
	},

	relocateMap() {
		var address = $('#map-address-location').val();
		var geocoder = new google.maps.Geocoder();
		geocoder.geocode( { address: address }, _.bind(function (results, status) {

			if (status === google.maps.GeocoderStatus.OK) {
				var center = results[0].geometry.location;
				var location_type = results[0].geometry.location_type;
				var zooms = {
					ROOFTOP: 15,
					RANGE_INTERPOLATED: 10,
					GEOMETRIC_CENTER: 8,
					APPROXIMATE: 6
				};
				this.map.setCenter(center);
				this.map.setZoom(zooms[location_type]);
			}
		}, this));
	}
});

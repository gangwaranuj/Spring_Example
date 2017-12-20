/*global google */
'use strict';

import Application from '../core';
import $ from 'jquery';
import UserProfilePopup from '../assignments/user_profile_popup_view';
import BundleMainView from './bundle_main_view';
import AssignmentForm from '../assignmentcreation/form';
import '../dependencies/jquery.bootstrap-dropdown';
import '../dependencies/jquery.bootstrap-collapse';
import '../config/wysiwyg';

new UserProfilePopup({
	el: '.main.container'
});

if (config.form.isAssignmentBundle) {

	window.configureMap = function () {
		var mapOptions = {
			center: new google.maps.LatLng(-34.397, 150.644)
		};

		var map = new google.maps.Map(document.getElementById('map-canvas'), mapOptions);

		$.ajax({
			url: '/assignments/get_all_bundle_info.json',
			data: {
				bundle_id: $('#parentId').val()
			},
			accepts: { text: 'application/json' },
			dataType: 'json',
			success: function(response) {
				var mapData = response.mapData,
					bounds = new google.maps.LatLngBounds(),
					infowindow = new google.maps.InfoWindow(),
					marker;
				for (var i = 0; i < mapData.length; i++) {
					marker = new google.maps.Marker({
						position: new google.maps.LatLng(parseFloat(mapData[i].latitude), parseFloat(mapData[i].longitude)),
						map: map
					});
					bounds.extend(marker.position);
					google.maps.event.addListener(marker, 'mouseover', (function(marker, i) {
						return function() {
							infowindow.setContent(mapData[i].title + '<br>' + mapData[i].address);
							infowindow.open(map, marker);
						};
					})(marker, i));
				}
				if (mapData.length > 0) {
					$('#bundle_map').show();
					google.maps.event.trigger(map, 'resize');
					console.log(bounds);
					map.fitBounds(bounds);
				}
			}
		});
	};
	Application.init({ name: 'bundles', features: config }, () => {});
	new BundleMainView ({
		model: config.form.workEncoded,
		auth: config.form.authEncoded,
		isDispatcher: config.form.isDispatcher,
		companyName: config.form.companyName,
		currentUserCompanyName: config.form.currentUserCompanyName,
		bundleParentId: config.form.bundleParentId,
		isEligibleToTakeAction: config.form.isEligibleToTakeAction,
		isWorkActive: config.form.isWorkActive,
		hasInvitedAtLeastOneVendor: config.form.hasInvitedAtLeastOneVendor,
		isSent: config.form.isSent,
		workNumber: config.form.workNumber,
		assignToFirstResource: config.form.workEncoded.configuration.assignToFirstResource
	});
	var assignments = new AssignmentForm();
	assignments.updateSendControls();
	assignments.workerTypeahead();
	$.getScript('//maps.google.com/maps/api/js?key=' + config.form.googleAPIKey + '&libraries=places&callback=configureMap');
}

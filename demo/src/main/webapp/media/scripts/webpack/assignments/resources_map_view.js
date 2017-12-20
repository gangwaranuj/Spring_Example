'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import WorkerActionsView from './worker_actions_view';
import WorkerModel from './resource_model';
import WorkerCollection from './resource_list_collection';
import jdenticon from '../dependencies/jquery.jdenticon';
import ProfileCardTemplate from '../profile/templates/profile-card.hbs';
import WorkerMapCountTemplate from './templates/details/workerMapCount.hbs';
import 'js-marker-clusterer';

export default Backbone.View.extend({
	el: 'body',
	template: ProfileCardTemplate,
	events: {
		'click #applied_workers'                     : 'showApplied',
		'click #all_workers'                         : 'showAll',
		'click .workers-map [name="workerNumber"]'   : 'clickWorker',
		'click .worker-link'                         : 'showActions'
	},

	initialize: function (options) {
		window.mapsLoaded = function () {
			this.loadMaps(options);
		}.bind(this);

		let script = document.createElement('script');
		script.type = 'text/javascript';
		script.src = 'https://maps.googleapis.com/maps/api/js?v=3.23&key=AIzaSyAWD12qVRbpnGyNF_fmYMERR0gyvdbHNvE&callback=mapsLoaded';
		document.body.appendChild(script);
	},

	loadMaps: function (options) {

		this.latitude = options.latitude;
		this.longitude = options.longitude;
		this.workNumber = options.workNumber;

		var mapCenterLatitudeOffset = 0.1;
		var mapCenterLongitudeOffset = 0.5;
		var highwayColor = '#B6B6B4';
		var styles = [{
			featureType: 'road',
			elementType: 'geometry',
			stylers: [
				{ color: highwayColor }
			]
		}
		];

		var styledMap = new google.maps.StyledMapType(styles, {name: 'Styled Map'});
		this.mapOptions = {
			center: new google.maps.LatLng(this.latitude - mapCenterLatitudeOffset, this.longitude - mapCenterLongitudeOffset),
			zoom: 10,
			mapTypeControlOptions: {
				mapTypeIds: [google.maps.MapTypeId.ROADMAP, 'map_style']
			},
			panControl: true,
			panControlOptions: {
				position: google.maps.ControlPosition.RIGHT_CENTER
			},
			zoomControl: true,
			zoomControlOptions: {
				style: google.maps.ZoomControlStyle.LARGE,
				position: google.maps.ControlPosition.RIGHT_CENTER
			}
		};

		this.map = new google.maps.Map(document.getElementById('map-canvas'), this.mapOptions);
		this.map.mapTypes.set('map_style', styledMap);
		this.map.setMapTypeId('map_style');

		this.directionsDisplay = new google.maps.DirectionsRenderer();
		this.directionsDisplay.setMap(this.map);

		this.markers = [];
		this.markerCluster = new MarkerClusterer(this.map, this.markers);

		// Icon Pins
		this.iconBaseUrl = '/media/images/map/';
		this.workIcon = this.iconBaseUrl + 'work_pin.png';
		this.appliedIcon = this.iconBaseUrl + 'worker_applied.png';
		this.allIcon = this.iconBaseUrl + 'worker_invite.png';

		this.workerList = new WorkerCollection();
		this.infoWindow = new google.maps.InfoWindow();

		// Assignment
		this.assignmentMarker = new google.maps.Marker({
			position: new google.maps.LatLng(this.latitude, this.longitude),
			map: this.map,
			icon: this.workIcon,
			title: 'Assignment'
		});

		this.table = this.$('table.workers-map');

		// full version of workers
		$.getJSON('/assignments/' + options.workNumber + '/full-workers', _.bind(function (response) {

			_.each(response.data.results, _.bind(function (item) {
				var worker = new WorkerModel(item);
				this.workerList.add(worker);
			}, this));

			var appliedWorkerList = this.workerList.byNegotiationStatus('0'),
				workerCount = $('.workers-count'),
				counts = {
					all: this.workerList.length,
					applied: appliedWorkerList.length,
					mediaPrefix: mediaPrefix
				};

			workerCount.empty();
			workerCount.append(WorkerMapCountTemplate(counts));
			// Default load applied workers
			if (counts.applied > 0) {
				this.renderWorkerDetail(appliedWorkerList);
				this.refreshMarkers(appliedWorkerList);
				$('#applied_workers').addClass('active');
			} else {
				this.renderWorkerDetail(this.workerList);
				this.refreshMarkers(this.workerList);
				$('#all_workers').addClass('active');
			}
			workerCount.show();
		}, this));


	},
	showApplied: function () {
		$('#applied_workers').addClass('active');
		$('#all_workers').removeClass('active');
		this.clearAll();
		var workerList = this.workerList.byNegotiationStatus('0');
		this.renderWorkerDetail(workerList);
		this.refreshMarkers(workerList);
	},

	showAll: function () {
		$('#all_workers').addClass('active');
		$('#applied_workers').removeClass('active');
		this.clearAll();
		this.renderWorkerDetail(this.workerList);
		this.refreshMarkers(this.workerList);
	},

	createMarker: function (resource, options) {
		var self = this;
		resource.marker = new google.maps.Marker({
			position: new google.maps.LatLng(resource.get('latitude'), resource.get('longitude')),
			map: self.map,
			icon: resource.get('has_negotiation') ? self.appliedIcon : self.allIcon,
			resourceUserId: resource.get('user_id'),
			resourceUserNumber: resource.get('user_number'),
			animation: options.animation
		});
		self.markers.push(resource.marker);
		google.maps.event.addListener(resource.marker, 'click', (function (marker) {
			return function () {
				var selector = '.resources-map [name=' + '"' + 'workerNumber' + '"' + '][value=' + '"' + resource.marker.get('resourceUserId').toString() + '"' + ']';
				self.workerList.moveToFirst(resource);
				self.renderWorkerDetail(self.workerList);
				$(selector).prop('checked', true);
				$(selector).click();
				$(selector).prop('checked', true);
			};
		})(resource.marker));

	},

	refreshMarkers: function (workerList) {
		for (var i = 0; i < this.markers.length; i++) {
			this.markers[i].setMap(null);
		}
		// efficiently clear the array
		this.markers.length = 0;
		this.markerCluster.clearMarkers();
		var infowindow = new google.maps.InfoWindow();

		workerList.each(_.bind(function (worker) {
			this.createMarker(worker, {animation: google.maps.Animation.DROP});
		}, this));
		$('#workers-map').find('.wm-spinner').hide();
	},

	renderWorkerDetail: function (workerList) {
		var tableBody = $('> tbody', this.table);
		tableBody.empty();
		if(workerList.size() === 0) {
			$('.no-worker').show();
			this.table.hide();
		} else {
			$('.no-worker').hide();
			this.table.show();

			workerList.each(function (item) {
				var data = item.toJSON();
				data.scoreCardData = {
					showrecent: true,
					values: this.scoreCardParse({
						allScorecard: data.resource_scorecard,
						companyScorecard: data.resource_scorecard_for_company,
						paidassignforcompany: 0
					}).scoreCard
				};
				data.assignment = { status: 'sent' };
				data.number = data.user_number;
				tableBody.append(this.template(data));
			}, this);
		}

		jdenticon();
		$('.worker-scroll').scrollTop(0);

	},

	// Copied from app/models/profile
	// Delete when this file goes to RequireJS
	scoreCardParse: function (response) {
		// Compile some score card data from the controller payload
		response.scoreCard = {
			abandoned: getValues('ABANDONED_WORK'),
			cancelled: getValues('CANCELLED_WORK'),
			paidAssignments: getValues('COMPLETED_WORK'),
			deliverables: getValues('DELIVERABLE_ON_TIME_PERCENTAGE'),
			onTime: getValues('ON_TIME_PERCENTAGE'),
			satisfaction: getValues('SATISFACTION_OVER_ALL'),
			paidAssignmentsForCompany: response.paidassignforcompany
		};

		// Satisfaction is returned to us as a decimal, we need it as a percentage
		_.chain(response.scoreCard)
			.pick('satisfaction', 'onTime', 'deliverables')
			.each(function (value) {
				value.all.all = Math.round(value.all.all * 100);
				value.all.net90 = Math.round(value.all.net90 * 100);
				value.company.all = Math.round(value.company.all * 100);
				value.company.net90 = Math.round(value.company.net90 * 100);
			});

		return response;

		function getValues(property) {
			return {
				all: _.pick(response.allScorecard.values[property], 'all', 'net90'),
				company: _.pick(response.companyScorecard.values[property], 'all', 'net90')
			};
		}
	},

	toggleScoreCard: function (event) {
		var isAllValues = $(event.currentTarget).val() === 'all';
		this.$('.score-card').toggleClass('-company', !isAllValues);
	},

	clickWorker: function (e) {
		var worker = this.workerList.find(function (model) {
			return model.get('userNumber') === e.target.value;
		});

		worker.marker.setMap(null);
		if (e.target.checked) {

			// uncheck other checkboxes first and then check target
			$('[name="workerNumber"]').prop('checked', false);
			$('.selected-worker').removeClass('selected-worker');
			$(e.target).prop('checked', true);
			$(e.target).parent().addClass('selected-worker');
			$(e.target).parent().siblings().addClass('selected-worker');

			this.createMarker(worker, {animation: google.maps.Animation.BOUNCE});

			// Drawing the route
			var start = worker.get('latitude') + ',' + worker.get('longitude');
			var end = this.options.latitude + ',' + this.options.longitude;
			var request = {
				origin:start,
				destination:end,
				travelMode: google.maps.TravelMode.DRIVING
			};

			var directionsService = new google.maps.DirectionsService();
			directionsService.route(request, _.bind(function (response, status) {
				if (status === google.maps.DirectionsStatus.OK) {
					this.directionsDisplay.setOptions({
						preserveViewport: true,
						suppressMarkers: true
					});
					var distance = response.routes[0].legs[0].distance.text;
					var infoText = 'The driving distance is about ' + distance + '.' ;
					this.popupInfoWindow(this.assignmentMarker, infoText);
					this.directionsDisplay.setDirections(response);
				}
			}, this));
			this.directionsDisplay.setMap(this.map);

			setTimeout(function () {
				worker.marker.setAnimation(null);
			}, 2000);

		} else {
			this.createMarker(worker, {animation: null});
			this.infoWindow.close();
			this.directionsDisplay.setMap(null);
		}
		this.markers.push(worker.marker);
	},

	popupInfoWindow: function (marker, content) {
		this.infoWindow.setContent(content);
		this.infoWindow.open(this.map, marker);
	},

	clearAll: function () {
		this.infoWindow.close();
		this.directionsDisplay.setMap(null);
	},

	showActions: function (event) {
		var userNumber = $(event.target).data('usernumber').substring(1);
		var worker = this.workerList.find(function (model) {
			return model.get('userNumber') === userNumber;
		});

		// Render the worker actions.
		var workerAction = new WorkerActionsView({
			successCallback: function () {
				window.location.reload();
			}
		});

		workerAction.render({
			assignment : {
				work_number: this.workNumber
			},
			worker : worker.attributes
		}, event);
	}

});

/* global google,confirm */
import 'datatables.net';
import 'js-marker-clusterer';
import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import wmSelect from '../funcs/wmSelect';
import wmNotify from '../funcs/wmNotify';
import getCSRFToken from '../funcs/getCSRFToken';
import wmMaskInput from '../funcs/wmMaskInput';
import wmModal from '../funcs/wmModal';
import GooglePlaces from '../funcs/googlePlaces';
import wmTabs from '../funcs/wmTabs';
import ClientFilterRowForContactsTemplate from './templates/client_filter_row_for_contacts.hbs';
import ClientFilterRowForLocationsTemplate from './templates/client_filter_row_for_locations.hbs';
import GroupListRowTemplate from './templates/group_list_row.hbs';
import WorkerPinInfoTemplate from './templates/worker_pin_info.hbs';
import LocationInfoPinMarkerTemplate from './templates/location_pin_info.hbs';
import BulkImportTemplate from './templates/bulk-import.hbs';
import '../dependencies/jquery.tmpl';
import '../funcs/googleMap';
import qq from '../funcs/fileUploader';

export default Backbone.View.extend({
	el: 'body',

	events: {
		'click a.cta-manage-location': 'openManageLocation',
		'click a.cta-manage-contact': 'openManageContact',
		'click #contact_submit_form': 'submitManageContact',
		'click #location_submit_form': 'submitManageLocation',
		'click a.cta-delete-location': 'deleteLocationContact',
		'click a.cta-delete-contact': 'deleteLocationContact',
		'click a.cta-import': 'openImport',
		'click #submit_upload_content': 'submitFormImport',
		'click a.remove-upload': 'removeUploadedContent',
		'click a.close': 'closeDialog',
		'addClient #client_list': 'addClient',
		'editClient #client_list': 'editClient',
		'removeClient #client_list': 'removeClient',
		'redrawLocationTable #client_list': 'redrawLocationTable',
		'redrawContactTable #client_list': 'redrawContactTable',
		'change #select-all-clients': 'toggleSelectAllLocations',
		'change .select-location': 'toggleDeleteLocationsButton',
		'click #delete-selected-locations-button': 'deleteSelectedLocations'
	},

	initialize (options) {
		this.$customMessage = $('#custom_message');
		this.$contactsList = $('#contacts_list');
		this.$locationsList = $('#locations_list');
		this.$selectGroups = $('#select-groups');
		this.$clientFilterLocation = $('#client-filter-location');
		this.$clientFilterContact = $('#client-filter-contact');
		this.$locationMap = $('#location-map');
		this.$clientFilters = $('.clientFilter');
		this.modal = '.wm-modal--content';
		this.clientFilterRowContact = ClientFilterRowForContactsTemplate;
		this.clientFilterRowLocation = ClientFilterRowForLocationsTemplate;
		this.clientLocationCache = { '': [] }; // add cache for locations for no clients
		this.customFormMessage = '#custom_form_message';
		this.activeTab = 'ul.location-manager li.active';
		this.options = options || {};
		this.loadLocationTable();
		this.loadContactTable();
		this.loadClientFilters();
		this.currentPage = {
			location: 1,
			contact: 1,
			client: 1
		};
		this.INITIAL_MAP_VALUES = {
			latitude: 40,
			longitude: -100,
			zoom: 4
		};
		this.MARKER_VALUES = {
			height: 50,
			width: 50,
			largeTextSize: 12,
			smallTextSize: 10
		};
		this.mapOverlays = {
			locations: {
				toggleStyling: _.partial(this.toggleStyling, '#client_filter_location_chosen', 'location-selected')
			},
			workers: {
				toggleStyling: _.partial(this.toggleStyling, '#select_groups_chosen', 'worker-selected')
			}
		};
		this.spinner = {
			show: () => {
				this.$locationMap.find('.wm-spinner').show();
				this.$locationMap.find('#map-canvas').removeClass('loaded');
			},
			hide: () => {
				this.$locationMap.find('.wm-spinner').hide();
				this.$locationMap.find('#map-canvas').addClass('loaded');
			}
		};
		this.updateLocationCountInClientFilter = {
			add: () => this.increaseLocationCountInClientFilter,
			remove: (numberOfLocations, clientId) => {
				this.increaseLocationCountInClientFilter(numberOfLocations * -1, clientId);
			}
		};

		this.loadGroupsList();
		this.initMapAddressTyper();
		this.initMapView();
		wmTabs();
	},

	render () {
	},

	toggleStyling (parent, className, selection) {
		const sel = _.isString(selection) ? selection : JSON.stringify(selection);
		$(parent).find('.chzn-single, .chzn-drop').toggleClass(className, !_.isEmpty(sel));
		$(parent).siblings('label').toggleClass('loaded', !_.isEmpty(sel));
	},

	// Expects clientId to be a number
	increaseLocationCountInClientFilter (numberOfLocations, clientId) {
		// If client filter is not enabled OR clientId is empty, return
		if (_.isNull(clientId)) {
			return;
		}
		const client = this.getClient(clientId);
		if (_.isUndefined(client)) {
			return;
		}

		// Update and save new count
		const indexOfClient = this.clientData.indexOf(client);
		client.locationCount += numberOfLocations;
		this.clientData[indexOfClient] = client;

		// Sort the client data by location count
		this.clientData = _.sortBy(this.clientData, (val) => {
			return -val.locationCount;
		});

		// Render
		this.renderClientFilterLocation();
	},

	loadGroupsList () {
		const self = this;

		$.get('/addressbook/group/get_groups', (results) => {
			if (results.successful) {
				this.$selectGroups.empty();
				const renderedGroups = GroupListRowTemplate({
					groupsList: results.data.groupsList
				});
				this.$selectGroups
					.append(renderedGroups)
					.change(function onChange () {
						const choice = $(this).val();

						self.mapOverlays.workers.toggleStyling(choice);
						self.clearResourceMap();

						if (choice === '') {
							self.spinner.hide();
							return;
						}

						self.spinner.show();

						$.get(`/addressbook/group/get_members/${choice}`, (data) => {
							if (data.successful && data.data && data.data.users) {
								self.loadResourceMap(data.data.users);
							}
							if (!data.successful) {
								self.$customMessage.find('.alert-error div').html(data.messages);
								self.$customMessage.find('.alert-error').show().delay(6000).fadeOut();
							}
						});
					});
				wmSelect({ selector: this.$selectGroups });
			}
		});
	},

	loadClientFilters (postOperationCallback) {
		if (!postOperationCallback) {
			postOperationCallback = this.initializeClientFilters; // eslint-disable-line no-param-reassign
		}

		$.ajax({
			url: '/addressbook/client/get_all_full',
			dataType: 'json',
			context: this,
			success (results) {
				this.clientData = results.data.clients;
				postOperationCallback.call(this);
			}
		});
	},

	addClient (event, newClient) {
		newClient.locationCount = 0; // eslint-disable-line no-param-reassign
		this.clientData.push(newClient);
		this.renderClientFilters();
	},

	editClient (event, editedClient) {
		const client = this.getClient(editedClient.id);
		if (_.isUndefined(client)) {
			return;
		}
		const indexOfClient = this.clientData.indexOf(client);
		client.name = editedClient.name;
		this.clientData[indexOfClient] = client;
		this.renderClientFilters();
	},

	removeClient (event, clientId) {
		const client = this.getClient(clientId);
		if (_.isUndefined(client)) {
			return;
		}
		this.clientData = _.without(this.clientData, client);
		this.$clientFilters.find(`option[value=${clientId}]`).remove();
	},

	setSelectedClient (clientId) {
		const client = this.getClient(clientId);
		if (_.isUndefined(client)) {
			return;
		}
		const indexOfClient = this.clientData.indexOf(client);
		client.isSelected = true;
		this.clientData[indexOfClient] = client;
	},

	deselectAllClients () {
		this.clientData.map((client) => {
			return {
				...client,
				isSelected: false
			};
		});
	},

	getClient (clientId) {
		return _.find(this.clientData, (client) => {
			return client.id === clientId;
		});
	},

	isClientSelected (clientId) {
		return _.some(this.clientData, (client) => {
			return client.id === clientId && client.isSelected;
		});
	},

	getSelectedClient () {
		return _.find(this.clientData, (client) => {
			return client.isSelected;
		});
	},

	renderClientFilters () {
		this.renderClientFilterContact();
		this.renderClientFilterLocation();
	},

	renderClientFilterContact () {
		this.$clientFilterContact
			.empty()
			.append(this.clientFilterRowContact({ clientList: this.clientData }));
	},

	renderClientFilterLocation () {
		this.$clientFilterLocation
			.empty()
			.append(this.clientFilterRowLocation({ clientList: this.clientData }));
	},

	importUploader () {
		this.uploader = new qq.FileUploader({
			element: document.getElementById('addressbook-import-uploader'),
			action: '/upload/uploadqq',
			allowedExtensions: ['csv'],
			CSRFToken: getCSRFToken(),
			sizeLimit: 10 * 1024 * 1024, // 2MB
			multiple: false,
			template: $('#qq-uploader-tmpl').html(),
			onSubmit () {
				$('.messages').hide();
				$('.qq-upload-list').hide();
			},
			onComplete (id, fileName, data) {
				$('#addressbook-import-uploader').hide();

				if (data.successful) {
					$('#submit_upload_content').removeAttr('disabled');
					$('a.uploaded').attr('href', `/upload/download/${data.uuid}`)
						.attr('alt', data.uuid)
						.text(fileName);
					$('a.remove-upload').show();
				} else {
					$('a.uploaded').text(`${fileName} Failed`);
				}
			},
			showMessage (message) {
				wmNotify({
					type: 'danger',
					message
				});
			}
		});
	},

	initMapAddressTyper () {
		const input = document.getElementById('map-address');
		const autocomplete = new google.maps.places.Autocomplete(input);
		google.maps.event.addListener(autocomplete, 'place_changed', _.bind(this.relocateMap, this));
	},

	initMapView () {
		_.bindAll(this, 'render');
		this.mapOptions = {
			zoom: this.INITIAL_MAP_VALUES.zoom,
			center: new google.maps.LatLng(
				this.INITIAL_MAP_VALUES.latitude,
				this.INITIAL_MAP_VALUES.longitude
			),
			mapTypeId: google.maps.MapTypeId.ROADMAP
		};
		this.map = new google.maps.Map(document.getElementById('map-canvas'), this.mapOptions);
		this.infoWindow = new google.maps.InfoWindow();
		this.locationMarkers = [];
		this.resourceMarkers = [];
		this.createLocationMapCluster();
		// Coming upon this line much later, assuming that MarkerClusterer is a global var
		// introduced by this module's imports
		this.resourceMarkerCluster = new MarkerClusterer( // eslint-disable-line no-undef
			this.map,
			this.resourceMarkers,
			{
				styles: [
					this.clusterStyler({
						filename: 'worker_cluster.svg',
						textSize: this.MARKER_VALUES.largeTextSize
					}),
					this.clusterStyler({
						filename: 'worker_cluster.svg',
						textSize: this.MARKER_VALUES.largeTextSize
					}),
					this.clusterStyler({
						filename: 'worker_cluster.svg',
						textSize: this.MARKER_VALUES.smallTextSize
					})
				]
			}
		);
		// Had to use png here because, for whatever reason, svgs are not rendering properly in Firefox
		// Alex 2014 06 04
		this.locationMarkerIcon = {
			url: '/media/images/map/Map_locations_x2.png',
			size: null,
			origin: null,
			anchor: null,
			scaledSize: new google.maps.Size(39, 43)
		};
		this.resourceMarkerIcon = {
			url: '/media/images/map/Map_worker_x2.png',
			size: null,
			origin: null,
			anchor: null,
			scaledSize: new google.maps.Size(32, 37)
		};

		$('#contacts-tab, #clients-tab, #locations-tab').on('click', document.documentElement.scrollTop = 0);

		google.maps.event.addListenerOnce(this.map, 'idle', this.spinner.hide);

		$('#map-reset-all').on('click', _.bind(this.mapResetAll, this));
	},

	createLocationMapCluster () {
		this.locationMarkerCluster = new MarkerClusterer( // eslint-disable-line no-undef
			this.map,
			this.locationMarkers,
			{
				styles: [
					this.clusterStyler({
						filename: 'client_cluster.svg',
						textSize: this.MARKER_VALUES.largeTextSize
					}),
					this.clusterStyler({
						filename: 'client_cluster.svg',
						textSize: this.MARKER_VALUES.largeTextSize
					}),
					this.clusterStyler({
						filename: 'client_cluster.svg',
						textSize: this.MARKER_VALUES.smallTextSize
					})
				]
			}
		);
	},

	clusterStyler (options) {
		return {
			url: `/media/images/map/${options.filename}`,
			height: 50,
			width: 50,
			textColor: '#ffffff',
			fontFamily: 'proxima',
			textSize: options.textSize
		};
	},

	mapZoomToFit (markers) {
		// If there are no new markers to zoom to, then return
		if (!markers.length) {
			return;
		}

		const currentBounds = this.map.getBounds();
		const north = currentBounds.getNorthEast().lat();
		const east = currentBounds.getNorthEast().lng();
		const south = currentBounds.getSouthWest().lat();
		const west = currentBounds.getSouthWest().lng();

		const newBounds = new google.maps.LatLngBounds(
			new google.maps.LatLng(south, west),
			new google.maps.LatLng(north, east)
		);

		_.each(markers, (marker) => {
			newBounds.extend(marker.getPosition());
		});

		if (this.doNewBoundsNotFitCurrentBounds({
			newBounds,
			currentBounds
		})) {
			this.map.fitBounds(newBounds);
		}
	},

	doNewBoundsNotFitCurrentBounds (params) {
		const newBoundsNE = params.newBounds.getNorthEast();
		const newBoundsSW = params.newBounds.getSouthWest();
		const currentBoundsNE = params.currentBounds.getNorthEast();
		const currentBoundsSW = params.currentBounds.getSouthWest();

		return newBoundsNE.lat() > currentBoundsNE.lat() || newBoundsSW.lat() < currentBoundsSW.lat() ||
			newBoundsNE.lng() > currentBoundsNE.lng() || newBoundsSW.lng() < currentBoundsSW.lng();
	},

	mapResetAll () {
		this.spinner.hide();
		this.clearMapControls();
		this.clearResourceMap();
		this.clearLocationMap();
		this.map.setCenter(
			new google.maps.LatLng(this.INITIAL_MAP_VALUES.latitude, this.INITIAL_MAP_VALUES.longitude)
		);
		this.map.setZoom(this.INITIAL_MAP_VALUES.zoom);
		this.datatableObjLocations.fnFilter('', 3); // Clear the client location filter on the location table
		this.datatableObjLocations.fnFilter('');
	},

	clearMapControls () {
		$('#client-filter-location, #select-groups').val('');
		this.mapOverlays.workers.toggleStyling('');
		this.mapOverlays.locations.toggleStyling('');
		$('#map-address').val('');
	},

	clearResourceMap () {
		_.each(this.resourceMarkers, (marker) => {
			marker.setMap(null);
		});
		this.resourceMarkers.length = 0;
		this.resourceMarkerCluster.clearMarkers();
	},

	loadResourceMap (resources) {
		if (!resources) {
			return;
		}

		_.each(resources, (resource) => {
			this.resourceMarkers.push(this.renderResourcePin(resource));
		});

		this.resourceMarkerCluster.addMarkers(this.resourceMarkers);
		this.mapZoomToFit(this.resourceMarkers);
		this.spinner.hide();
	},

	renderResourcePin (resource) {
		const marker = new google.maps.Marker({
			position: new google.maps.LatLng(resource.latitude, resource.longitude),
			map: this.map,
			icon: this.resourceMarkerIcon,
			resourceName: resource.resourceName,
			companyName: resource.companyName,
			userNumber: resource.userNumber,
			animation: google.maps.Animation.DROP
		});

		google.maps.event.addListener(marker, 'click', () => {
			this.infoWindow.setContent(WorkerPinInfoTemplate(marker));
			this.infoWindow.open(this.map, marker);
		});

		return marker;
	},

	clearLocationMap () {
		_.each(this.locationMarkers, (marker) => {
			marker.setMap(null);
		});
		this.locationMarkers.length = 0;
		this.locationMarkerCluster.clearMarkers();
	},

	// Expects clientId of type string
	loadLocationMap (clientId) {
		const self = this;

		// Add or remove our custom selected styling for map controls
		this.mapOverlays.locations.toggleStyling(clientId);
		this.clearLocationMap();
		this.deselectAllClients();

		if (clientId === '') {
			this.spinner.hide();
			return;
		}

		// After empty string check, covert to number
		const clientIdNum = parseInt(clientId, 10); //
		this.spinner.show();

		// Check cache first
		const locationCacheForClient = this.clientLocationCache[clientIdNum];
		if (!_.isUndefined(locationCacheForClient)) {
			this.drawLocationMap(locationCacheForClient, clientIdNum);
		} else {
			$.ajax({
				type: 'GET',
				url: `/addressbook/location/map/${clientIdNum}`,
				context: this,
				success (data) {
					if (data.successful) {
						this.clientLocationCache[clientIdNum] = data.data.locations;
						this.drawLocationMap(data.data.locations, clientIdNum);
					} else {
						this.manageCallback(data, self.$customMessage);
					}
				}
			});
		}
	},

	drawLocationMap (locations, clientId) {
		_.each(locations, (location) => {
			this.locationMarkers.push(this.renderLocationPin(location));
		});
		this.setSelectedClient(clientId);
		this.locationMarkerCluster.addMarkers(this.locationMarkers);
		this.mapZoomToFit(this.locationMarkers);
		this.spinner.hide();
	},

	renderLocationPin (location) {
		const marker = new google.maps.Marker({
			position: new google.maps.LatLng(location.latitude, location.longitude),
			map: this.map,
			icon: this.locationMarkerIcon,
			id: location.id,
			name: location.name,
			number: location.number,
			client: location.client,
			address: location.address,
			contact: location.contact,
			moreContacts: location.moreContacts,
			animation: google.maps.Animation.DROP,
			optimized: false
		});

		this.locationMarkers.push(marker);

		google.maps.event.addListener(marker, 'click', () => {
			this.infoWindow.setContent(LocationInfoPinMarkerTemplate(marker));
			this.infoWindow.open(this.map, marker);
		});

		return marker;
	},

	relocateMap () {
		const address = $('#map-address').val();
		const geocoder = new google.maps.Geocoder();

		geocoder.geocode({ address }, (results, status) => {
			if (status === google.maps.GeocoderStatus.OK) {
				const center = results[0].geometry.location;
				const locationType = results[0].geometry.location_type;
				const zooms = {
					ROOFTOP: 15,
					RANGE_INTERPOLATED: 10,
					GEOMETRIC_CENTER: 8,
					APPROXIMATE: 6
				};
				this.map.setCenter(center);
				this.map.setZoom(zooms[locationType]);
			}
		});
	},

	closeDialog (e) {
		$(e.target).parent().hide();
	},

	submitFormImport () {
		const data = {
			type: $('input[name="import_type"]:checked').val(),
			uuid: $('a.uploaded').attr('alt')
		};

		this.importType = data.type;

		$('#submit_upload_content').attr('disabled', true);

		$.ajax({
			url: '/addressbook/import',
			type: 'POST',
			dataType: 'json',
			data,
			context: this,
			success (response) {
				const responseData = { ...response };
				if (response.successful) {
					responseData.messages = ['Import was successful'];
					if (this.importType === 'location') {
						this.datatableObjLocations.fnDraw();
					} else {
						this.datatableObjContacts.fnDraw();
					}
				}
				this.manageCallback(responseData, this.customFormMessage);
				this.removeUploadedContent();

				const client = this.getSelectedClient();

				if (client) {
					// Reset cache to force update of latest from server
					this.clientLocationCache = { '': [] };

					const clientId = client.id;
					this.loadClientFilters(function loadClientFilters () {
						this.renderClientFilters(); // render so we don't duplicate change handlers

						// Reset dropdown to what it was before
						if (clientId) {
							this.$clientFilterLocation.val(clientId);
							this.mapOverlays.locations.toggleStyling(clientId);
						}
					});
					this.loadLocationMap(client.id);
				}

				this.loadContactTable();
				this.loadLocationTable();
			}
		});
	},

	removeUploadedContent () {
		$('a.uploaded').removeAttr('href alt').empty();
		$('#addressbook-import-uploader').show();
		$('#submit_upload_content').prop('disabled', true);
		$('a.remove-upload').hide();
	},

	openImport () {
		wmModal({
			autorun: true,
			title: 'Confirm',
			destroyOnClose: true,
			content: BulkImportTemplate()
		});

		this.importUploader();

		if ($(this.activeTab).text() === 'Locations') {
			$('#form-import').find('input[value="location"]').prop('checked', true);
		} else if ($(this.activeTab).text() === 'Contacts') {
			$('#form-import').find('input[value="contact"]').prop('checked', true);
		}
	},

	deleteLocationContact (e) {
		e.preventDefault();
		const url = $(e.currentTarget).attr('href');
		const type = $(e.currentTarget).attr('rel').toLowerCase();

		if (confirm(`Are you sure you want to remove this ${type}?`)) { // eslint-disable-line no-alert
			$.ajax({
				url,
				type: 'DELETE',
				dataType: 'json',
				context: this,
				success (data) {
					if (data.successful) {
						this.$customMessage.find('.alert-success div').html(`You have successfully deleted the ${type}`);
						this.$customMessage.find('.alert-success').show().delay(6000).fadeOut();
						this.redrawLocationTable();
						this.redrawContactTable();
					} else {
						this.$customMessage.find('.alert-error div').html(data.messages.join('<br>'));
						this.$customMessage.find('.alert-error').show().delay(10000).fadeOut();
					}
				}
			});
		}
	},

	removeLocationFromMap (location, clientId) {
		// If the location's client is not the current filter, return
		if (!this.isClientSelected(clientId)) {
			return;
		}

		// Retrieve markers.
		const locationsToRemove = this.getLocationMarker(location.id);
		if (typeof locationsToRemove === 'undefined') {
			return;
		}

		// Remove their references to the map
		_.each(locationsToRemove, (locationVal) => {
			locationVal.setMap(null);
		});

		// Remove markers from marker array
		this.locationMarkers = _.reject(this.locationMarkers, (marker) => {
			return marker.id === location.id;
		});

		// Remove markers from cluster
		this.locationMarkerCluster.removeMarkers(locationsToRemove);
	},

	addLocationToMap (location) {
		// If the location's client is not the current filter, return
		if (!this.isClientSelected(location.clientId)) {
			return;
		}

		const newLocationMarker = this.renderLocationPin(location);
		this.locationMarkers.push(newLocationMarker);
		this.locationMarkerCluster.clearMarkers();
		this.locationMarkerCluster.addMarkers(this.locationMarkers);
	},

	editLocationOnMap (newProperties) {
		// If the location's client is not the current filter, return
		if (!this.isClientSelected(newProperties.clientId)) {
			return;
		}

		const markerToEdit = this.getLocationMarker(newProperties.id)[0];
		if (typeof markerToEdit === 'undefined') {
			return;
		}
		markerToEdit.setOptions(newProperties);
		this.infoWindow.close();
	},

	// Returns TWO markers
	getLocationMarker (id) {
		return _.filter(this.locationMarkers, (el) => {
			return el.id === id;
		});
	},

	removeLocation (location, clientId) {
		this.clientLocationCache[clientId] = _.reject(this.clientLocationCache[clientId], (el) => {
			return el.id === location.id;
		});
	},

	addLocation (location) {
		const locationCacheForClient = this.clientLocationCache[location.clientId];
		if (!_.isUndefined(locationCacheForClient)) {
			locationCacheForClient.push(location);
		}
	},

	editLocation (location, clientId) {
		this.removeLocation(location, clientId);
		this.addLocation(location);
	},

	manageCallback (data) {
		if (data.successful) {
			_.each(data.messages, (message) => {
				wmNotify({ message });
			});
			$('.wm-modal--close').trigger('click');
		} else {
			_.each(data.messages, (message) => {
				wmNotify({
					message,
					type: 'danger'
				});
			});
		}
	},

	openManageLocation (event, id) {
		let action = 'edit';
		let href = '/addressbook/location/manage';
		let oldClientId = null;

		if (event) {
			event.preventDefault();
			action = $(event.currentTarget).attr('rel');
			href = $(event.currentTarget).attr('href');
			oldClientId = $(event.currentTarget).attr('client');
		} else if (id) {
			href += `?id=${id}`;
		}

		$.ajax({
			type: 'GET',
			url: href,
			context: this,
			success (response) {
				if (!_.isEmpty(response)) {
					wmModal({
						autorun: true,
						title: `${(action === 'edit') ? 'Edit' : 'Create New'} Location`,
						destroyOnClose: true,
						content: response
					});

					const locationId = $('input[name="id"]').val();
					const $locationForm = $('#form_location_manage');
					const $controls = $('.controls');

					if (action === 'edit' && locationId !== '') {
						this.fetchSelectedContacts(locationId);
					} else {
						this.initContactSelectBox();
					}

					wmSelect({ selector: $controls.find('#location_type') });
					wmSelect({ selector: $controls.find('#clients') });

					// Initialize google autocomplete
					new GooglePlaces('#form_location_manage'); // eslint-disable-line no-new
					$locationForm.bind('googleAutocompleteSuggestion', (ev, suggestion) => {  // Listen for autocomplete suggestions
						this.manageCallback({
							successful: false,
							messages: [`Did you mean ${suggestion.streetNumber} ${suggestion.street}?`]
						}, this.customFormMessage);
					});
					$locationForm.bind('clearMessages', () => {
						$(this.customFormMessage).hide();
					});
					$locationForm.find('#location_submit_form').attr('client', oldClientId);
				}
			}

		});
	},

	submitManageLocation (event) {
		event.preventDefault();

		const oldClientId = ($(event.currentTarget).attr('client') === '') ? null : parseInt($(event.currentTarget).attr('client'), 10);

		$.ajax({
			type: 'POST',
			url: $(this.modal).find('form').attr('action'),
			data: $(this.modal).find('form').serialize(),
			dataType: 'json',
			context: this,
			success (data) {
				const responseData = { ...data };
				if (responseData.successful) {
					const action = $('.wm-modal--title').html() === 'Edit Location';
					responseData.messages = [`You have successfully ${action ? 'edited' : 'added'} the location`];
					this.redrawLocationTable();
					this.redrawContactTable();

					const locationToUpdate = responseData.data.location;
					if (action) {
						// If the location is currently displayed, edit the location on the map
						if (this.isClientSelected(locationToUpdate.clientId)) {
							this.editLocationOnMap(locationToUpdate);

							// If the location is currently displayed
							// but the location now belongs to a client whose locations are not currently filtered
							// then remove the location from the map
						} else if (
							this.isClientSelected(oldClientId) &&
							!this.isClientSelected(locationToUpdate.clientId)
						) {
							this.removeLocationFromMap(locationToUpdate, oldClientId);
						}

						this.editLocation(locationToUpdate, oldClientId);

						if (oldClientId !== locationToUpdate.clientId) {
							this.updateLocationCountInClientFilter.remove(1, oldClientId);
							this.updateLocationCountInClientFilter.add(1, locationToUpdate.clientId);
						}
					} else {
						this.addLocationToMap(locationToUpdate);
						this.addLocation(locationToUpdate);
						this.updateLocationCountInClientFilter.add(1, locationToUpdate.clientId);
					}
				}
				this.manageCallback(responseData, this.customFormMessage);
			}
		});
	},

	fetchSelectedContacts (locationId) {
		$.ajax({
			type: 'GET',
			url: `/addressbook/location/get_contacts/${locationId}`,
			context: this,
			success (response) {
				if (response.successful) {
					this.initContactSelectBox(response.data.contacts);
				} else {
					this.manageCallback(response, this.customFormMessage);
				}
			}
		});
	},

	openManageContact (event, id) {
		let action = 'edit';
		let href = '/addressbook/contact/manage';

		if (event) {
			event.preventDefault();
			action = $(event.currentTarget).attr('rel');
			href = $(event.currentTarget).attr('href');
		} else if (id) {
			href += `?id=${id}`;
		}

		$.ajax({
			type: 'GET',
			url: href,
			context: this,
			success (response) {
				if (!_.isEmpty(response)) {
					wmModal({
						autorun: true,
						title: `${(action === 'edit') ? 'Edit' : 'Create New'} Contact`,
						destroyOnClose: true,
						content: response
					});

					const contactId = $('input[name="id"]').val();
					const $controls = $('.controls');

					if (action === 'edit' && contactId !== '') {
						this.fetchSelectedLocations(contactId);
					} else {
						this.initLocationSelectBox();
					}

					wmSelect({ root: $controls, selector: '#client_company' });
					wmMaskInput({ root: this.$el, selector: '#contact-mobile-phone' });
					wmMaskInput({ root: this.$el, selector: '#contact-work-phone' });
				}
			}

		});
	},

	submitManageContact (event) {
		event.preventDefault();
		$.ajax({
			type: 'POST',
			url: $(this.modal).find('form').attr('action'),
			data: $(this.modal).find('form').serialize(),
			dataType: 'json',
			context: this,
			success (data) {
				const action = $('.wm-modal--title').html() === 'Edit Contact';
				const responseData = { ...data };
				if (data.successful) {
					responseData.messages = [`You have successfully ${(action === 'edit') ? 'edited' : 'added'} the contact.`];
					this.redrawLocationTable();
					this.redrawContactTable();
				}
				this.manageCallback(responseData, this.customFormMessage);
			}
		});
	},

	fetchSelectedLocations (contactId) {
		$.ajax({
			type: 'GET',
			url: `/addressbook/contact/get_locations/${contactId}`,
			context: this,
			success (response) {
				if (response.successful) {
					this.initLocationSelectBox(response.data.locations);
				} else {
					this.manageCallback(response, this.customFormMessage);
				}
			}
		});
	},

	initLocationSelectBox (prePopulatedLocations) {
		const selectSelector = '#client_location_typeahead';
		this.clientLocationSelectize = wmSelect({ selector: selectSelector }, {
			valueField: 'id',
			labelField: 'name',
			searchField: 'name',
			options: [],
			persist: false,
			loadThrottle: 200,
			create: false,
			allowEmptyOption: true,
			render: {
				option (item, escape) {
					return `<div>${escape(_.unescape(item.name) + (_.isEmpty(item.number) ? '' : `: Location Number (${item.number})`))}</div>`;
				},
				item (item, escape) {
					return `<div>${escape(_.unescape(item.name) + (_.isEmpty(item.number) ? '' : `: Location Number (${item.number})`))}</div>`;
				}
			},
			load (query, callback) {
				if (!query.length) {
					callback();
				}

				$.ajax({
					url: '/addressbook/get_clientlocations.json',
					type: 'GET',
					dataType: 'json',
					data: {
						locationFilter: query
					},
					error () {
						callback();
					},
					success (res) {
						callback(res.data.locations);
					}
				});
			}
		});

		if (!_.isEmpty(prePopulatedLocations)) {
			this.prePopulateDropdownData(selectSelector, prePopulatedLocations);
		}
	},

	initContactSelectBox (prePopulatedContacts) {
		const selectSelector = '#location-contacts';
		wmSelect({ selector: selectSelector }, {
			valueField: 'id',
			labelField: 'name',
			searchField: 'name',
			options: [],
			persist: false,
			loadThrottle: 200,
			create: false,
			allowEmptyOption: true,
			render: {
				option (item, escape) {
					return `<div>${escape(_.unescape(item.name) + (_.isEmpty(item.number) ? '' : ` | (phone number: ${item.number})`))}${_.isEmpty(item.email) ? '' : `(email: ${item.email})`}</div>`;
				},
				item (item, escape) {
					return `<div>${escape(_.unescape(item.name) + (_.isEmpty(item.number) ? '' : ` | (phone number: ${item.number})`))}${_.isEmpty(item.email) ? '' : `(email: ${item.email})`}</div>`;
				}
			},
			load (query, callback) {
				if (!query.length) {
					callback();
				}

				$.ajax({
					url: '/addressbook/get_clientcontacts.json?id='.concat($('#clients').val().toString()),
					type: 'GET',
					dataType: 'json',
					data: {
						contactFilter: query
					},
					error () {
						callback();
					},
					success (res) {
						callback(res.data.contacts);
					}
				});
			}
		});

		if (!_.isEmpty(prePopulatedContacts)) {
			this.prePopulateDropdownData(selectSelector, prePopulatedContacts);
		}
	},

	prePopulateDropdownData (selectSelector, data) {
		const $dropdown = $(selectSelector);
		if ($dropdown.length && $dropdown[0].selectize) {
			_.each(data, (item) => {
				$dropdown[0].selectize.addOption({
					id: item.id,
					name: item.name
				});
				$dropdown[0].selectize.addItem(item.id);
			});
		}
	},

	loadContactTable () {
		let meta;

		this.datatableObjContacts = this.$contactsList.dataTable({
			sPaginationType: 'full_numbers',
			bLengthChange: true,
			bFilter: true,
			bStateSave: false,
			bProcessing: true,
			bServerSide: true,
			iDisplayLength: 50,
			bRetrieve: true,
			sDom: 'rtip',
			aoColumnDefs: [
				{
					mRender: (data, type, val, metaData) => {
						return $('#name-contact-cell-tmpl').tmpl({
							meta: meta[metaData.row]
						}).html();
					},
					aTargets: [0]
				},
				{
					mRender: (data, type, val, metaData) => {
						return $('#location-contact-cell-tmpl').tmpl({
							meta: meta[metaData.row]
						}).html();
					},
					aTargets: [6]
				},
				{
					mRender: (data, type, val, metaData) => {
						return $('#edit-contact-cell-tmpl').tmpl({
							meta: meta[metaData.row]
						}).html();
					},
					aTargets: [8]
				},
				{
					mRender: (data, type, val, metaData) => {
						return $('#delete-contact-cell-tmpl').tmpl({
							meta: meta[metaData.row]
						}).html();
					},
					aTargets: [9]
				},
				{ bSortable: false, aTargets: [1, 2, 3, 4, 5, 6, 7, 8, 9] }
			],
			bSort: true,
			sAjaxSource: '/addressbook/contact/get_all',
			fnServerData (sSource, aoData, fnCallback) {
				$.getJSON(sSource, aoData, (json) => {
					meta = json.aMeta;
					fnCallback(json);
				});
			}
		});
	},

	redrawContactTable () {
		this.datatableObjContacts.fnDraw(false);
	},

	loadLocationTable () {
		let meta;

		this.datatableObjLocations = this.$locationsList.dataTable({
			sPaginationType: 'full_numbers',
			bLengthChange: true,
			bFilter: true,
			bStateSave: false,
			bProcessing: true,
			bServerSide: true,
			iDisplayLength: 200,
			bRetrieve: true,
			sDom: 'rtip',
			aoColumnDefs: [
				{
					mRender: (data, type, val, metaData) => {
						return $('#select-location-cell-tmpl').tmpl({
							meta: meta[metaData.row]
						}).html();
					},
					aTargets: [0],
					orderable: false
				},
				{
					mRender: (data, type, val, metaData) => {
						return $('#name-location-cell-tmpl').tmpl({
							meta: meta[metaData.row]
						}).html();
					},
					aTargets: [1]
				},
				{
					mRender: (data, type, val) => {
						return val[1];
					},
					aTargets: [2]
				},
				{
					mRender: (data, type, val) => {
						return val[2];
					},
					aTargets: [3]
				},
				{
					mRender: (data, type, val) => {
						return val[3];
					},
					aTargets: [4]
				},
				{
					mRender: (data, type, val) => {
						return val[4];
					},
					aTargets: [5]
				},
				{
					mRender: (data, type, val, metaData) => {
						return $('#contact-location-cell-tmpl').tmpl({
							meta: meta[metaData.row]
						}).html();
					},
					aTargets: [6]
				},
				{
					mRender: (data, type, val, metaData) => {
						return $('#edit-location-cell-tmpl').tmpl({
							meta: meta[metaData.row]
						}).html();
					},
					aTargets: [7]
				},
				{
					mRender: (data, type, val, metaData) => {
						return $('#delete-location-cell-tmpl').tmpl({
							meta: meta[metaData.row]
						}).html();
					},
					aTargets: [8]
				},
				{
					bSortable: false,
					aTargets: [2, 3, 4, 5, 6, 7, 8]
				}
			],
			bSort: true,
			sAjaxSource: '/addressbook/location/get_all',
			fnServerData (sSource, aoData, fnCallback) {
				$.getJSON(sSource, aoData, (json) => {
					meta = json.aMeta;
					fnCallback(json);
				});
			}
		});
	},

	redrawLocationTable () {
		this.datatableObjLocations.fnDraw(false);
	},

	initializeClientFilters () {
		const self = this;

		/*
		 3 is used to map this value to client_ID
		 see request.setFilterMapping in AddressBookController::getAllClientCompanyLocations
		 */
		self.renderClientFilters();
		self.$clientFilterContact
			.change(function onChange () {
				self.datatableObjContacts.fnFilter($(this).val(), 3);
			});
		self.$clientFilterLocation
			.change(function onChange () {
				const clientId = $(this).val();
				self.loadLocationMap(clientId);
				self.datatableObjLocations.fnFilter(clientId, 3);
			});

		$('.chzn-container').addClass('controls');
		wmSelect(self.$clientFilterContact);
		wmSelect(self.$clientFilterLocation);
	},

	toggleSelectAllLocations (event) {
		$('.select-location').prop('checked', event.currentTarget.checked);
		$('#delete-selected-locations-button').toggle(event.currentTarget.checked);
	},

	toggleDeleteLocationsButton () {
		$('#delete-selected-locations-button').toggle($('.select-location:checked').length > 0);
	},

	deleteSelectedLocations () {
		const ids = $.map($('.select-location:checked'), (obj) => {
			return parseInt(obj.value, 10);
		});

		if (confirm(`Are you sure you want to remove these ${ids.length} Locations?`)) { // eslint-disable-line no-alert
			$.ajax({
				url: '/addressbook/location/delete',
				type: 'POST',
				context: this,
				data: { ids },
				success (data) {
					if (data.successful) {
						this.datatableObjLocations.fnDraw();
					}
				}
			});
		}
	}

});

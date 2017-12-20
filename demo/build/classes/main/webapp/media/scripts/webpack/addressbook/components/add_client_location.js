import fetch from 'isomorphic-fetch';
import moment from 'moment';
import $ from 'jquery';
import PropTypes from 'prop-types';
import React, { Component } from 'react';
import loadGoogleMapsAPI from 'load-google-maps-api';
import { WMRadioButton, WMRadioButtonGroup, WMFlatButton, WMRaisedButton, WMFontIcon, WMToggle } from '@workmarket/front-end-components';
import _ from 'underscore';
import wmSelect from '../../funcs/wmSelect';
import wmMaskInput from '../../funcs/wmMaskInput';
import googleMap from '../../funcs/googleMap';
import Application from '../../core';
import '../../config/datepicker';

const isSet = (val) => {
	return (val && val > 0);
};

export default class AddClientLocationComponent extends Component {
	static 	checkValidation (location, locationMode) {
		let isValid = false;
		const { name, addressLine1, city, country, state, zip, id, clientCompanyId } = location;

		// nothing required if location is virtual
		if (locationMode === 'virtual') {
			isValid = true;
		} else if (locationMode === 'new' &&
		(clientCompanyId === null ||
		!clientCompanyId.length)
		) {
			if (
				addressLine1 !== null &&
				addressLine1.length &&
				city !== null &&
				city.length &&
				country !== null &&
				country.length &&
				state !== null &&
				state.length &&
				zip !== null &&
				zip.length
			) {
				isValid = true;
			}
		} else if (locationMode === 'new' &&
		(clientCompanyId !== null ||
		clientCompanyId.length)
		) {
			if (
				name !== null &&
				name.length &&
				addressLine1 !== null &&
				addressLine1.length &&
				city !== null &&
				city.length &&
				country !== null &&
				country.length &&
				state !== null &&
				state.length &&
				zip !== null &&
				zip.length
			) {
				isValid = true;
			}
		} else if (id) {
			isValid = true;
		}
		return isValid;
	}

	constructor (props) {
		super(props);
		this.state = {
			mode: 'new',
			googleInitialized: false,
			addressOpen: false,
			newClient: {
				industryId: '1000'
			},
			newProject: {},
			newProjectOpen: false,
			newClientOpen: false,
			secondaryContactOpen: false,
			disableAddress1: true
		};

		this.changeGooglePlace = this.changeGooglePlace.bind(this);
	}

	componentDidMount () {
		const root = this.node;

		wmMaskInput(
			{
				selector: '[type="tel"]'
			}
		);

		this.clientSelector = wmSelect({ selector: '[name="client"]', root }, {
			labelField: 'name',
			valueField: 'id',
			searchField: 'name',
			options: [],
			preload: true,
			render: {
				option: item => `<div>${item.name} | ID: ${item.id}</div>`,
				item: item => `<div>${item.name} | ID: ${item.id}</div>`
			},
			onLoad: () => {
				if (!this.clientSelector.items[0]) {
					const clientCompanyId = this.props.location.clientCompanyId || '';
					this.clientSelector.setValue(clientCompanyId, true);
				}
			},
			onChange: (clientCompanyId) => {
				this.props.updateClientCompanyId(clientCompanyId);
				if (this.projectSelector) {
					this.projectSelector.enable();
					this.projectSelector.clearOptions();
				}
				if (this.clientLocationSelector) {
					this.clientLocationSelector.clearOptions();
				}
				if (this.state.mode !== 'new') {
					this.props.clearLocationFields();
				}
				this.setState({ disableAddress1: true });
				if (clientCompanyId === '') {
					if (this.state.mode === 'contact-manager') {
						this.setState({ mode: 'new' });
					}
				} else {
					this.projectSelector.load((callback) => {
						fetch(`/employer/v2/client_companies/${clientCompanyId}/projects?fields=id,name`, { credentials: 'same-origin' })
							.then(res => res.json())
							.then(res => callback(res.results));
					});
				}
			},
			load: (query, callback) => fetch('/employer/v2/client_companies?fields=id,name', { credentials: 'same-origin' })
				.then(res => res.json())
				.then(res => callback(res.results))
		})[0].selectize;

		this.projectSelector = wmSelect({ selector: '[name="project"]', root }, {
			labelField: 'name',
			valueField: 'id',
			searchField: 'name',
			options: [],
			preload: true,
			render: {
				option: item => `<div>${item.name} | ID: ${item.id}</div>`,
				item: item => `<div>${item.name} | ID: ${item.id}</div>`
			},
			onLoad: () => {
				if (!this.projectSelector.items[0]) {
					const projectId = this.props.projectId || '';
					this.projectSelector.setValue(projectId);
				}
			},
			onChange: projectId => this.props.updateProjectId(projectId),
			load: (query, callback) => {
				const clientCompanyId = this.props.location.clientCompanyId;
				if (clientCompanyId) {
					fetch(`/employer/v2/client_companies/${clientCompanyId}/projects?fields=id,name`, { credentials: 'same-origin' })
						.then(res => res.json())
						.then(res => callback(res.results));
				}
			}
		})[0].selectize;

		this.initLocationTypeSelector();

		if (!this.state.googleInitialized) {
			loadGoogleMapsAPI({
				key: GOOGLE_MAPS_API_TOKEN,
				libraries: 'places'
			}).then(() => {
				this.setState({ googleInitialized: true });
				this.googleMap = googleMap({
					map: $('.map').get(0),
					autocomplete: $('[name="addressTyper"]').get(0),
					autocompleteCallback: _.bind(this.changeGooglePlace, this)
				});
			});
		}

		// set initial valid state for component
		if (this.state.mode === 'virtual') {
			this.props.setModuleValidation(true, this.props.id);
		}
	}

	componentWillReceiveProps (nextProps) {
		if (!this.state.googleInitialized) {
			loadGoogleMapsAPI({
				key: GOOGLE_MAPS_API_TOKEN,
				libraries: 'places'
			}).then(() => {
				this.setState({ googleInitialized: true });
				this.googleMap = googleMap({
					map: $('.map').get(0),
					autocomplete: $('[name="addressTyper"]').get(0),
					autocompleteCallback: _.bind(this.changeGooglePlace, this)
				});
			});
		}
		const {
			projectId,
			location: { id, addressLine1, clientCompanyId, locationType, latitude, longitude,
				secondaryContact }
		} = nextProps;
		if (this.state.mode !== 'contact-manager' &&
			(isSet(id) && clientCompanyId)) {
			this.setState({
				mode: 'contact-manager'
			});
		}
		if (latitude && longitude &&
			(this.state.latitude !== latitude || this.state.longitude !== longitude)) {
			const latlng = new google.maps.LatLng(latitude, longitude); // eslint-disable-line no-undef
			this.googleMap.centerMap(latlng, 15);
			this.googleMap.setMarker(latlng);
		}
		if (!clientCompanyId) {
			this.projectSelector.disable();
		} else {
			this.projectSelector.enable();
		}
		if (id === 0 && addressLine1 && this.state.mode !== 'new') {
			this.setState({
				mode: 'new'
			});
		} else if (id === -1 && !addressLine1) {
			this.setState({
				mode: 'virtual'
			});
		}
		if (clientCompanyId !== this.props.location.clientCompanyId) {
			this.clientSelector.setValue(clientCompanyId, true);
		}
		if (this.locationTypeSelector) {
			this.locationTypeSelector.setValue(locationType, true);
		}
		if (projectId !== this.props.projectId) {
			this.projectSelector.setValue(projectId, true);
			if (clientCompanyId) {
				this.projectSelector.load((callback) => {
					fetch(`/employer/v2/client_companies/${clientCompanyId}/projects?fields=id,name`, { credentials: 'same-origin' })
						.then(res => res.json())
						.then(res => callback(res.results));
				});
			}
		}

		if (secondaryContact !== null) {
			const { firstName, lastName, workPhone, workPhoneExtension, email } = secondaryContact;
			const secondaryContactExists = (!!firstName || !!lastName || !!workPhone ||
				!!workPhoneExtension || !!email);

			this.setState({ secondaryContactOpen: secondaryContactExists });
		}
	}

	componentDidUpdate (prevProps, prevState) {
		if (this.state.mode === 'contact-manager' && prevState.mode !== 'contact-manager') {
			this.initLocationTypeSelector();
			this.locationTypeSelector.disable();
			this.initClientLocationSelector();
			if (isSet(this.props.location.id)) {
				this.clientLocationSelector.addOption({
					id: this.props.location.id,
					name: this.props.location.name,
					locationNumber: this.props.location.number
				});
				this.clientLocationSelector.addItem(this.props.location.id, true);
			}
			this.prepareMap();
		} else if (this.state.mode === 'virtual' && prevState.mode !== 'virtual') {
			if (this.clientLocationSelector) {
				this.clientLocationSelector.destroy()
				this.clientLocationSelector = null;
			}
			this.locationTypeSelector = null;
			this.props.updateLocationId(-1);
			this.props.updateLocationMode(-1);
		} else if (this.state.mode === 'new' && prevState.mode !== 'new') {
			if (this.clientLocationSelector) {
				this.clientLocationSelector.destroy()
				this.clientLocationSelector = null;
			}
			this.initLocationTypeSelector();
			this.locationTypeSelector.enable();
			this.props.updateLocationId(0);
			this.props.updateLocationMode(0);
			this.prepareMap();
		}

		const isModuleValid = AddClientLocationComponent.checkValidation(
			this.props.location,
			this.state.mode
		);
		this.props.setModuleValidation(isModuleValid, this.props.id);
	}

	toggleSecondaryContact () {
		const isSecondaryContactOpen = !this.state.secondaryContactOpen;

		this.setState({
			secondaryContactOpen: isSecondaryContactOpen
		});

		if (!isSecondaryContactOpen) {
			this.clearSecondaryContact();
		}
	}

	clearSecondaryContact () {
		this.props.updateSecondaryContactEmail('');
		this.props.updateSecondaryContactFirstName('');
		this.props.updateSecondaryContactLastName('');
		this.props.updateSecondaryContactWorkPhone('');
		this.props.updateSecondaryContactWorkPhoneExtension('');
	}

	prepareMap () {
		if (!this.state.googleInitialized) {
			loadGoogleMapsAPI({
				key: GOOGLE_MAPS_API_TOKEN,
				libraries: 'places'
			}).then(() => {
				this.setState({ googleInitialized: true });
				this.googleMap = googleMap({
					map: $('.map').get(0),
					autocomplete: $('[name="addressTyper"]').get(0),
					autocompleteCallback: _.bind(this.changeGooglePlace, this)
				});
			});
		} else {
			this.googleMap = googleMap({
				map: $('.map').get(0),
				autocomplete: $('[name="addressTyper"]').get(0),
				autocompleteCallback: _.bind(this.changeGooglePlace, this)
			});
		}
	}

	initLocationTypeSelector () {
		const root = this.node;
		if (!this.locationTypeSelector) {
			this.locationTypeSelector = wmSelect({ selector: '[name="locationType"]', root }, {
				onChange: value => this.props.updateLocationType(value)
			})[0].selectize;
		}
	}

	initClientLocationSelector () {
		const root = this.node;
		this.clientLocationSelector = wmSelect({ selector: '[name="clientLocation"]', root }, {
			valueField: 'id',
			labelField: 'name',
			searchField: 'name',
			options: [],
			preload: true,
			render: {
				option: item => `<div>${item.name} | Location Number (${item.locationNumber})</div>`,
				item: item => `<div>${item.name}</div>`
			},
			onChange: (id) => {
				if (id.length > 0) {
					this.props.updateLocationId(id);
					this.setState({ addressOpen: true });
					$.ajax({
						url: '/assignments/get_clientlocation_contacts',
						global: false,
						type: 'GET',
						data: ({ id }),
						dataType: 'json',
						success: (data) => {
							if (data && data.success) {
								if (typeof data.address === 'object') {
									this.props.updateLocationLatitude(data.address.latitude);
									this.props.updateLocationLongitude(data.address.longitude);
									this.props.updateLocationName(data.address.location_name_text);
									this.props.updateLocationNumber(data.address.location_number_text);
									this.props.updateLocationAddressLine1(data.address.address_one_text);
									this.props.updateLocationAddressLine2(data.address.address_two_text);
									this.props.updateLocationCity(data.address.city_text);
									this.props.updateLocationState(data.address.state_dropdown);
									this.props.updateLocationZip(data.address.zip_text);
									this.props.updateLocationCountry(data.address.location_country.id);
									this.props.updateLocationInstructions(data.address.location_instructions);
									this.props.updateLocationType(data.address.location_type_id);
								}
							}
						}
					});
				} else {
					this.props.clearLocationFields();
				}
			},
			load: (query, callback) => {
				if (query) {
					const clientId = this.props.location.clientCompanyId || '';
					fetch(`/employer/v2/client_locations?fields=locationNumber,name,id&locationName=${query}&clientId=${clientId}`, { credentials: 'same-origin' })
						.then(res => res.json())
						.then(res => callback(res.results));
				}
			}
		})[0].selectize;
	}

	addNewClientForm () {
		return (
			<div>
				<label className="assignment-creation--label -required" htmlFor="location-new-client-name">Client Name</label>
				<div className="assignment-creation--field">
					<input
						id="location-new-client-name"
						type="text"
						name="location-new-client-name"
						value={ this.state.newClient.name || '' }
						onChange={ ({ target: { value } }) => this.updateNewClient('name', value) }
					/>
				</div>
				<label className="assignment-creation--label" htmlFor="location-new-client-id">Client ID</label>
				<div className="assignment-creation--field">
					<input
						id="location-new-client-id"
						type="text"
						name="location-new-client-id"
						value={ this.state.newClient.customerId || '' }
						onChange={ ({ target: { value } }) => this.updateNewClient('customerId', value) }
					/>
				</div>
				<label className="assignment-creation--label" htmlFor="location-new-client-region">Region</label>
				<div className="assignment-creation--field">
					<input
						id="location-new-client-region"
						type="text"
						name="location-new-client-region"
						value={ this.state.newClient.region || '' }
						onChange={ ({ target: { value } }) => this.updateNewClient('region', value) }
					/>
				</div>
				<label className="assignment-creation--label" htmlFor="location-new-client-division">Division</label>
				<div className="assignment-creation--field">
					<input
						type="text"
						id="location-new-client-division"
						name="location-new-client-division"
						value={ this.state.newClient.division || '' }
						onChange={ ({ target: { value } }) => this.updateNewClient('division', value) }
					/>
				</div>
				<label className="assignment-creation--label" htmlFor="location-new-client-industry">Industry</label>
				<div className="assignment-creation--field">
					<select
						id="location-new-client-industry"
						name="location-new-client-industry"
						className="wm-select"
					/>
				</div>
				<label className="assignment-creation--label" htmlFor="location-new-client-website">Website</label>
				<div className="assignment-creation--field">
					<input
						type="text"
						id="location-new-client-website"
						name="location-new-client-website"
						value={ this.state.newClient.website || '' }
						onChange={ ({ target: { value } }) => this.updateNewClient('website', value) }
					/>
				</div>
				<label className="assignment-creation--label" htmlFor="location-new-client-phone">Work Phone</label>
				<div className="assignment-creation--field" style={ { height: '35px' } }>
					<input
						type="tel"
						id="location-new-client-phone"
						name="location-new-client-phone"
						placeholder="(___) ___-____"
						autoComplete="off"
						value={ this.state.newClient.phoneNumber || '' }
						onChange={ ({ target: { value } }) => this.updateNewClient('phoneNumber', value) }
					/>
					<input
						type="tel"
						id="location-new-client-phone-ext"
						name="location-new-client-phone-ext"
						placeholder="Ext."
						value={ this.state.newClient.phoneExtension || '' }
						onChange={ ({ target: { value } }) => this.updateNewClient('phoneExtension', value) }
					/>
				</div>

				<div className="assignment-creation--field">
					<WMRaisedButton
						primary
						label="Add New Client"
						style={ { marginBottom: '10px', marginTop: '10px' } }
						onClick={ () => this.addNewClient() }
					/>
				</div>
			</div>
		);
	}

	updateNewClient (label, value) {
		const newClient = this.state.newClient;
		newClient[label] = value;
		this.setState({ newClient });
	}

	addNewClient () {
		fetch('/employer/v2/assignments/add_client', {
			method: 'POST',
			credentials: 'same-origin',
			body: JSON.stringify(this.state.newClient),
			headers: new Headers({
				'Content-Type': 'application/json',
				'X-CSRF-Token': Application.CSRFToken,
				'Data-Type': 'json'
			})
		})
		.then(res => res.json())
		.then(res => res.results[0])
		.then((client) => {
			if (client && client.clientCompanyId) {
				this.clientSelector.addOption({ id: client.clientCompanyId, name: client.name });
				this.props.updateClientCompanyId(client.clientCompanyId);
				this.setState({ newClientOpen: false });
				if (this.state.mode === 'virtual') {
					this.setState({ mode: 'contact-manager' });
				}
			}
		});
	}

	initNewClientIndustrySelect () {
		if (this.state.newClientOpen) {
			const root = this.node;
			this.clientIndustrySelect = wmSelect({ root, selector: '[name="location-new-client-industry"]' }, {
				valueField: 'id',
				searchField: ['id', 'name'],
				sortField: 'name',
				labelField: 'name',
				preload: true,
				openOnFocus: true,
				onChange: value => this.updateNewClient('industryId', value),
				load: (query, callback) => {
					fetch('/industries-list', { credentials: 'same-origin' })
						.then(res => res.json())
						.then(res => callback(res));
				}
			})[0].selectize;
		}
	}

	initNewProjectForm () {
		if (this.state.newProjectOpen) {
			const root = this.node;
			const selector = '[name="location-new-project-owner"]';

			this.projectOwnerSelect = wmSelect({ root, selector }, {
				valueField: 'id',
				searchField: ['id', 'firstName', 'lastName'],
				sortField: [
					{ field: 'lastName' },
					{ field: 'firstName' }
				],
				labelField: 'fullName',
				preload: true,
				openOnFocus: true,
				onChange: value => this.updateNewProject('ownerId', value),
				load: (query, callback) => {
					fetch('/employer/v2/projects/users?fields=id,firstName,lastName',
						{ credentials: 'same-origin' })
						.then(res => res.json())
						.then((res) => {
							res.results.forEach((item) => {
								const user = item;
								user.fullName = `${item.firstName} ${item.lastName}`;
							});
							return res.results;
						})
						.then(results => callback(results));
				}
			})[0].selectize;

			$('[name="location-new-project-due-date"]').datepicker({
				dateFormat: 'mm/dd/yy',
				onSelect: value => this.updateNewProject('dueDate', value),
				showOptions: {
					direction: 'up'
				}
			});
		}
	}

	addNewProjectForm () {
		return (
			<div>
				<label className="assignment-creation--label -required" htmlFor="location-new-project-name">Title</label>
				<div className="assignment-creation--field">
					<input
						id="location-new-project-name"
						type="text"
						name="location-new-project-name"
						value={ this.state.newProject.name || '' }
						onChange={ ({ target: { value } }) => this.updateNewProject('name', value) }
					/>
				</div>
				<label className="assignment-creation--label -required" htmlFor="location-new-project-description">Description</label>
				<div className="assignment-creation--field">
					<input
						id="location-new-project-description"
						type="text"
						name="location-new-project-description"
						value={ this.state.newProject.description || '' }
						onChange={ ({ target: { value } }) => this.updateNewProject('description', value) }
					/>
				</div>
				<label className="assignment-creation--label -required" htmlFor="location-new-project-owner">Project Owner</label>
				<div className="assignment-creation--field">
					<select
						id="location-new-project-owner"
						name="location-new-project-owner"
						className="wm-select"
					/>
				</div>
				<label className="assignment-creation--label" htmlFor="location-new-project-due-date">Completion Date</label>
				<div className="assignment-creation--field">
					<input
						id="location-new-project-due-date"
						type="text"
						name="location-new-project-due-date"
						value={ this.state.newProject.dueDate || '' }
						onChange={ ({ target: { value } }) => this.updateNewProject('dueDate', value) }
					/>
				</div>
				{ Application.Features.hasReserveFunds && (
					<div>
						<label className="assignment-creation--label" htmlFor="location-new-project-reserved-funds-enabled">
							Enable Project Reserved Funds
						</label>
						<div className="assignment-creation--field">
							<WMToggle
								toggled={ !!this.state.newProject.reservedFundsEnabled }
								onToggle={ ({ target: { checked } }) =>
									this.updateNewProject('reservedFundsEnabled', checked)
								}
							/>
						</div>
					</div>
				) }
				<div className="assignment-creation--field">
					<WMRaisedButton
						primary
						label="Add New Project"
						style={ { marginBottom: '10px', marginTop: '10px' } }
						onClick={ () => this.addNewProject() }
					/>
				</div>
			</div>
		);
	}

	updateNewProject (label, value) {
		const newProject = this.state.newProject;
		newProject[label] = value;
		this.setState({ newProject });
	}

	addNewProject () {
		const newProject = this.state.newProject;
		newProject.clientCompanyId = this.props.location.clientCompanyId;

		if (newProject.dueDate) {
			const dueDate = moment(newProject.dueDate, 'MM/DD/YYYY', true);
			if (dueDate.isValid()) {
				newProject.dueDate = dueDate.startOf('day').format();
			} else {
				newProject.dueDate = null;
			}
		}

		fetch('/employer/v2/projects', {
			method: 'POST',
			credentials: 'same-origin',
			body: JSON.stringify(newProject),
			headers: new Headers({
				'Content-Type': 'application/json',
				'X-CSRF-Token': Application.CSRFToken,
				'Data-Type': 'json'
			})
		})
			.then(res => res.json())
			.then(res => res.results[0])
			.then((project) => {
				if (project && project.projectId) {
					this.projectSelector.addOption({ id: project.projectId, name: project.name });
					this.props.updateProjectId(project.projectId);
					this.setState({ newProjectOpen: false, newProject: { } });
				}
			});
	}

	changeGooglePlace (place) {
		const location = place.geometry.location;
		const coordinates = {
			lat: location.lat(),
			lng: location.lng()
		};
		let postalCode = '';
		let country = '';
		let state = '';
		let city = '';
		let address1 = '';

		if (place.address_components) {
			_.each(place.address_components, (component) => {
				_.each(component.types, (type) => {
					const shortName = component.short_name;
					const longName = component.long_name;
					const isShortNameNotNull = !_.isNull(shortName);
					const isLongNameNotNull = !_.isNull(longName);

					if (type === 'postal_code' && isShortNameNotNull) {
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
						address1 = shortName;
					}
					if (type === 'route' && isShortNameNotNull) {
						const prefix = address1.trim().length > 0 ? `${address1} ` : '';
						address1 = prefix + shortName;
					}
				});
			});

			if (!_.isUndefined(_.findWhere(place.address_components, { long_name: 'Puerto Rico' }))) {
				state = 'PR';
				country = 'USA';
			}
		}

		this.setState({ disableAddress1: address1 !== undefined && address1 !== '' });
		this.props.updateLocationLatitude(coordinates.lat === undefined ? '' : coordinates.lat);
		this.props.updateLocationLongitude(coordinates.lng === undefined ? '' : coordinates.lng);
		this.props.updateLocationId(0);
		this.props.updateLocationAddressLine1(address1 === undefined ? '' : address1);
		this.props.updateLocationCity(city === undefined ? '' : city);
		this.props.updateLocationCountry(country === undefined ? '' : country);
		this.props.updateLocationZip(postalCode === undefined ? '' : postalCode);
		this.props.updateLocationState(state === undefined ? '' : state);
	}

	checkClientSelected () {
		const clientCompanyId = this.props.location.clientCompanyId;
		return typeof clientCompanyId === 'number' ||
			(typeof clientCompanyId === 'string' && clientCompanyId.length > 0);
	}

	render () {
		const disableLocationFields =
			this.state.mode !== 'new' ||
			(isSet(this.props.location.id) && this.props.location.clientCompanyId);
		const isClientSelected = this.checkClientSelected();

		return (
			<div
				ref={ node => (this.node = node) }
			>
				<div className="assignment-creation--container">
					<label className="assignment-creation--label" htmlFor="location-client">Client</label>
					<div className="assignment-creation--field" style={ { paddingBottom: '12px' } } >
						<select id="location-client" name="client" className="wm-select" />
						<WMFlatButton
							primary
							label={ this.state.newClientOpen ? 'Hide Add New Client' : 'Add New Client' }
							onClick={ () => {
								this.setState({ newClientOpen: !this.state.newClientOpen },
									() => this.initNewClientIndustrySelect());
							} }
							icon={
								this.state.newClientOpen ?
									<WMFontIcon
										id="assignment-creation__add-icon"
										className="material-icons"
										style={ { fontSize: '12px' } }
									>
										remove
									</WMFontIcon>
								:
									<WMFontIcon
										id="assignment-creation__remove-icon"
										className="material-icons"
										style={ { fontSize: '12px' } }
									>
										add
									</WMFontIcon>
							}
							style={ { marginTop: '-12px' } }
						/>
						{ this.state.newClientOpen && this.addNewClientForm() }
					</div>
				</div>

				<div className="assignment-creation--container">
					<label className="assignment-creation--label" htmlFor="location-project">Project</label>
					<div className="assignment-creation--field">
						<select
							id="location-project"
							name="project"
							className="wm-select"
							readOnly={ !isClientSelected }
							disabled={ !isClientSelected }
						/>
						{ (!Application.Features.hasProjectPermission || Application.Features.hasProjectAccess)
							&& (
								<WMFlatButton
									primary
									disabled={ !isClientSelected }
									label={ this.state.newProjectOpen ? 'Hide Add New Project' : 'Add New Project' }
									style={ { marginBottom: '1em', marginTop: '-12px' } }
									onClick={ () => {
										this.setState({ newProjectOpen: !this.state.newProjectOpen },
											() => this.initNewProjectForm());
									} }
									icon={
										this.state.newProjectOpen ?
											<WMFontIcon
												id="assignment-creation__add-icon"
												className="material-icons"
												style={ { fontSize: '12px' } }
											>
												remove
											</WMFontIcon>
											:
											<WMFontIcon
												id="assignment-creation__remove-icon"
												className="material-icons"
												style={ { fontSize: '12px' } }
											>
												add
											</WMFontIcon>
									}
								/>
						) }
						{ this.state.newProjectOpen && this.addNewProjectForm() }
					</div>
				</div>

				<div className="assignment-creation--container">
					<label className="assignment-creation--label" htmlFor="location-mode">Location</label>
					<div className="assignment-creation--field">
						<WMRadioButtonGroup
							name="location-mode"
							onChange={
								(event, value) => {
									if (this.state.mode !== value) {
										this.props.clearLocationFields();
										$('[name="addressTyper"]').val('');
										this.setState({
											mode: value,
											disableAddress1: true
										});
									}
								}
							}
							valueSelected={ this.state.mode }
						>
							<WMRadioButton
								label="Select location from Contact Manager"
								value="contact-manager"
								disabled={ !this.props.location.clientCompanyId }
							/>
							<WMRadioButton
								label="Create New Location"
								value="new"
							/>
							<WMRadioButton
								label="Virtual / Offsite"
								value="virtual"
							/>
						</WMRadioButtonGroup>
					</div>
				</div>

				{ this.state.mode !== 'virtual' ? (
					<div>
						{ disableLocationFields ? (
							<div className="assignment-creation--container">
								<label className="assignment-creation--label -required" htmlFor="location-clientLocation">Location Name</label>
								<div className="assignment-creation--field">
									<select id="location-clientLocation" name="clientLocation" placeholder="Input client location name here" className="wm-select" />
								</div>
							</div>
						) : (
							<div className="assignment-creation--container">
								<label
									className={ this.props.location.clientCompanyId ? 'assignment-creation--label -required' : 'assignment-creation--label' }
									htmlFor="location-name"
								>Location Name</label>
								<div className="assignment-creation--field">
									<input
										id="location-name"
										type="text"
										name="location-name"
										value={ this.props.location.name }
										onChange={ ({ target: { value } }) => this.props.updateLocationName(value) }
										hidden={ disableLocationFields }
									/>
								</div>
							</div>
						) }

						<div className="assignment-creation--container">
							<label className="assignment-creation--label" htmlFor="location-number">Location Number</label>
							<div className="assignment-creation--field">
								<input
									id="location-number"
									type="text"
									name="location-number"
									value={ this.props.location.number }
									onChange={ ({ target: { value } }) => this.props.updateLocationNumber(value) }
									disabled={ disableLocationFields }
									readOnly={ disableLocationFields }
								/>
							</div>
						</div>

						<div className="assignment-creation--container">
							<label className="assignment-creation--label" htmlFor="location-type">Location Type</label>
							<div className="assignment-creation--field">
								<select
									id="location-type"
									name="locationType"
									className="wm-select"
								>
									<option value="1">Commercial</option>
									<option value="2">Residential</option>
									<option value="3">Government</option>
									<option value="4">Education</option>
								</select>
							</div>
						</div>

						<div className="assignment-creation--container">
							<label
								className={ disableLocationFields ?
									'assignment-creation--label'
									:
									'assignment-creation--label -required'
								}
								htmlFor="location-addressLine1"
							>
								Location Address
							</label>
							<div
								id="location-typeahead"
								className="assignment-creation--field"
							>
								<input
									type="text"
									size="500"
									name="addressTyper"
									id="addressTyper"
									placeholder="Input address here (ex: 240 W 37th. Street New York NY)"
									hidden={ disableLocationFields }
								/>
							</div>
						</div>

						<div className="assignment-creation--container">
							<div className="assignment-creation--label" />
							<div id="location-addressLine1" className="assignment-creation--field">
								<input
									type="text"
									name="location-addressLine1"
									id="address1"
									value={ this.props.location.addressLine1 }
									placeholder="Address"
									onChange={ ({ target: { value } }) => {
										this.props.updateLocationAddressLine1(value);
									} }
									readOnly={ this.state.disableAddress1 }
									disabled={ this.state.disableAddress1 }
								/>
								<input
									type="text"
									name="location-addressLine2"
									id="address2"
									value={ this.props.location.addressLine2 }
									placeholder="Suite / Floor"
									onChange={ ({ target: { value } }) => {
										this.props.updateLocationAddressLine2(value);
									} }
									disabled={ disableLocationFields }
									readOnly={ disableLocationFields }
								/>
								<input
									type="text"
									name="location-city"
									id="city"
									value={ this.props.location.city }
									placeholder="City"
									readOnly
									disabled
								/>
								<input
									type="text"
									name="location-state"
									id="state"
									value={ this.props.location.state }
									placeholder="State"
									readOnly
									disabled
								/>
								<input
									type="text"
									name="location-zip"
									id="postalCode"
									value={ this.props.location.zip }
									placeholder="Zip"
									readOnly
									disabled
								/>
								<input
									type="text"
									name="location-country"
									id="country"
									value={ this.props.location.country }
									placeholder="Country"
									readOnly
									disabled
								/>
							</div>
						</div>

						<div className="assignment-creation--container">
							<div className="assignment-creation--label" />
							<div className="map" />
						</div>

						<div className="assignment-creation--container">
							<label className="assignment-creation--label" htmlFor="location-instructions">Travel Instructions</label>
							<div className="assignment-creation--field">
								<textarea
									name="location-instructions"
									id="instructions"
									maxLength="500"
									value={ this.props.location.instructions }
									onChange={
										({ target: { value } }) => this.props.updateLocationInstructions(value)
									}
									disabled={ disableLocationFields }
									readOnly={ disableLocationFields }
								/>
							</div>
						</div>

						<div className="assignment-creation--container">
							<h2 className="assignment-creation--subheading">Location Contact</h2>
						</div>

						<div className="assignment-creation--container">
							<label className="assignment-creation--label" htmlFor="location-contact-firstName">Name</label>
							<div className="assignment-creation--field">
								<input
									type="text"
									id="location-contact-firstName"
									name="location-contact-firstName"
									value={ this.props.location.contact.firstName }
									placeholder="First"
									onChange={ ({ target: { value } }) => this.props.updateContactFirstName(value) }
								/>
								<input
									type="text"
									name="location-contact-lastName"
									value={ this.props.location.contact.lastName }
									placeholder="Last"
									onChange={ ({ target: { value } }) => this.props.updateContactLastName(value) }
								/>
							</div>
						</div>

						<div className="assignment-creation--container">
							<label className="assignment-creation--label" htmlFor="location-contact-workPhone">Phone</label>
							<div className="assignment-creation--field">
								<input
									type="tel"
									id="location-contact-workPhone"
									name="location-contact-workPhone"
									value={ this.props.location.contact.workPhone }
									placeholder="(___) ___-____"
									onChange={ ({ target: { value } }) => this.props.updateContactWorkPhone(value) }
								/>
								<input
									type="tel"
									id="location-contact-workPhoneExtension"
									name="location-contact-workPhoneExtension"
									placeholder="Ext."
									value={ this.props.location.contact.workPhoneExtension }
									onChange={
										({ target: { value } }) => this.props.updateContactWorkPhoneExtension(value)
									}
								/>
							</div>
						</div>
						<div className="assignment-creation--container">
							<label className="assignment-creation--label" htmlFor="location-contact-email">Email</label>
							<div className="assignment-creation--field">
								<input
									type="email"
									id="location-contact-email"
									name="location-contact-email"
									value={ this.props.location.contact.email }
									onChange={
										({ target: { value } }) => this.props.updateContactEmail(value)
									}
								/>
							</div>
						</div>

						<WMFlatButton
							primary
							label={ this.state.secondaryContactOpen ?
								'Remove Secondary Contact' : 'Add Secondary Contact'
							}
							style={ { width: '100%', marginBottom: '1em' } }
							onClick={ () => this.toggleSecondaryContact() }
						/>

						{ this.state.secondaryContactOpen && (
							<div>
								<div className="assignment-creation--container">
									<h2 className="assignment-creation--subheading">Secondary Location Contact</h2>
								</div>

								<div className="assignment-creation--container">
									<label className="assignment-creation--label" htmlFor="location-secondaryContact-firstName">Name</label>
									<div className="assignment-creation--field">
										<input
											type="text"
											id="location-secondaryContact-firstName"
											name="location-secondaryContact-firstName"
											value={ this.props.location.secondaryContact.firstName }
											placeholder="First"
											onChange={
												({ target: { value } }) => this.props.updateSecondaryContactFirstName(value)
											}
										/>
										<input
											type="text"
											name="location-secondaryContact-lastName"
											value={ this.props.location.secondaryContact.lastName }
											placeholder="Last"
											onChange={
												({ target: { value } }) => this.props.updateSecondaryContactLastName(value)
											}
										/>
									</div>
								</div>

								<div className="assignment-creation--container">
									<label className="assignment-creation--label" htmlFor="location-secondaryContact-workPhone">Phone</label>
									<div className="assignment-creation--field">
										<input
											type="tel"
											id="location-secondaryContact-workPhone"
											name="location-secondaryContact-workPhone"
											value={ this.props.location.secondaryContact.workPhone }
											placeholder="(___) ___-____"
											onChange={
												({ target: { value } }) => this.props.updateSecondaryContactWorkPhone(value)
											}
										/>
										<input
											type="tel"
											id="location-secondaryContact-workPhoneExtension"
											name="location-secondaryContact-workPhoneExtension"
											value={ this.props.location.secondaryContact.workPhoneExtension }
											placeholder="Ext."
											onChange={
												({ target: { value } }) =>
													this.props.updateSecondaryContactWorkPhoneExtension(value)
											}
										/>
									</div>
								</div>

								<div className="assignment-creation--container">
									<label className="assignment-creation--label" htmlFor="location-secondaryContact-email">Email</label>
									<div className="assignment-creation--field">
										<input
											type="email"
											id="location-secondaryContact-email"
											name="location-secondaryContact-email"
											value={ this.props.location.secondaryContact.email }
											onChange={
												({ target: { value } }) => this.props.updateSecondaryContactEmail(value)
											}
										/>
									</div>
								</div>
							</div>

						) }
					</div>
				) : '' }
			</div>
		);
	}
}

AddClientLocationComponent.propTypes = {
	id: PropTypes.oneOfType([
		PropTypes.string,
		PropTypes.number
	]).isRequired,
	location: PropTypes.shape({
		addressLine1: PropTypes.string,
		addressLine2: PropTypes.string,
		city: PropTypes.string,
		state: PropTypes.string,
		zip: PropTypes.string,
		country: PropTypes.string,
		locationType: PropTypes.number,
		instructions: PropTypes.string,
		id: PropTypes.number.isRequired,
		latitude: PropTypes.number,
		longitude: PropTypes.number,
		name: PropTypes.string,
		number: PropTypes.string,
		clientCompanyId: PropTypes.oneOfType([
			PropTypes.string,
			PropTypes.number
		]),
		contact: PropTypes.shape({
			email: PropTypes.string,
			firstName: PropTypes.string,
			id: PropTypes.string,
			lastName: PropTypes.string,
			mobilePhone: PropTypes.string,
			workPhone: PropTypes.string,
			workPhoneExtension: PropTypes.string
		}),
		secondaryContact: PropTypes.shape({
			email: PropTypes.string,
			firstName: PropTypes.string,
			id: PropTypes.string,
			lastName: PropTypes.string,
			mobilePhone: PropTypes.string,
			workPhone: PropTypes.string,
			workPhoneExtension: PropTypes.string
		})
	}),
	clientCompanyId: PropTypes.oneOfType([
		PropTypes.string,
		PropTypes.number
	]),
	projectId: PropTypes.oneOfType([
		PropTypes.string,
		PropTypes.number
	]),
	updateProjectId: PropTypes.func.isRequired,
	updateClientCompanyId: PropTypes.func.isRequired,
	setModuleValidation: PropTypes.func.isRequired,
	updateLocationMode: PropTypes.func.isRequired,
	updateLocationId: PropTypes.func.isRequired,
	updateLocationAddressLine1: PropTypes.func.isRequired,
	updateLocationCity: PropTypes.func.isRequired,
	updateLocationCountry: PropTypes.func.isRequired,
	updateLocationType: PropTypes.func.isRequired,
	updateLocationInstructions: PropTypes.func.isRequired,
	updateLocationZip: PropTypes.func.isRequired,
	updateLocationState: PropTypes.func.isRequired,
	updateLocationName: PropTypes.func.isRequired,
	updateLocationNumber: PropTypes.func.isRequired,
	updateLocationAddressLine2: PropTypes.func.isRequired,
	updateLocationLatitude: PropTypes.func.isRequired,
	updateLocationLongitude: PropTypes.func.isRequired,
	updateContactFirstName: PropTypes.func.isRequired,
	updateContactLastName: PropTypes.func.isRequired,
	updateContactWorkPhone: PropTypes.func.isRequired,
	updateContactWorkPhoneExtension: PropTypes.func.isRequired,
	updateContactEmail: PropTypes.func.isRequired,
	updateSecondaryContactFirstName: PropTypes.func.isRequired,
	updateSecondaryContactLastName: PropTypes.func.isRequired,
	updateSecondaryContactWorkPhone: PropTypes.func.isRequired,
	updateSecondaryContactWorkPhoneExtension: PropTypes.func.isRequired,
	updateSecondaryContactEmail: PropTypes.func.isRequired,
	clearLocationFields: PropTypes.func.isRequired
};

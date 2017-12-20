'use strict';

import {
	WMFontIcon,
	WMRadioButtonGroup,
	WMRadioButton,
	WMCheckbox,
	WMMessageBanner
} from '@workmarket/front-end-components';
import $ from 'jquery';
import React from 'react';
import ReactDOM from 'react-dom';
import wmSelect from '../../funcs/wmSelect';
import styles from './styles';

export default class AddRoutingComponent extends React.Component {
	constructor (props) {
		super(props);
		this.state = {
			routingMode: null,
			resourceNumbers: false,
			autoAssignResourceNumbers: false,
			vendors: false,
			talentPools: false,
			autoAssignTalentPools: false,
			autoAssignVendors: false
		};

		this.updateRoutingMode = this.updateRoutingMode.bind(this);
	}

	componentWillUpdate (nextProps, nextState) {
		if (this.state.routingMode === 'invite' && nextState.routingMode !== 'invite') {
			// clear out any selections from this routing mode from the UI and props
			this.talentPoolsSelect = null;
			this.firstToApplyTalentPoolsSelect = null;
			this.workerSelect = null;
			this.firstToApplyWorkerSelect = null;
			this.vendorSelect = null;
			this.firstToApplyVendorSelect = null;
			this.props.clearInvitees();
		}
	}

	componentDidUpdate (prevProps, prevState) {
		let isModuleValid = false;
		isModuleValid = this.checkValidation(this.props.routing, this.state.routingMode);
		this.props.updateValidity(isModuleValid);
		this.props.setModuleValidation(isModuleValid, this.props.id);
		if (prevState.routingMode !== 'invite' && this.state.routingMode === 'invite') {
			this.workerTypeahead();
			this.talentPoolsTypeahead(this.props.routing.needToApplyCandidates.groupIds);
			this.vendorTypeahead();
			this.firstToApplyWorkerTypeahead();
			this.firstToApplyTalentPoolsTypeahead(this.props.routing.firstToAcceptCandidates.groupIds);
			this.firstToApplyVendorTypeahead();
		}
		if ((this.props.pricing.type === 'INTERNAL' && prevProps.pricing.type !== 'INTERNAL') ||
			(this.props.pricing.type !== 'INTERNAL' && prevProps.pricing.type === 'INTERNAL')) {
			this.props.clearInvitees();
			this.workerSelect.clearOptions();
			this.workerSelect.clearCache();
			this.firstToApplyWorkerSelect.clearOptions();
			this.firstToApplyWorkerSelect.clearCache();
		}
	}

	checkValidation (routing, routingMode) {
		let isValid = false;
		const { shownInFeed, firstToAcceptCandidates, needToApplyCandidates } = routing;

		if (shownInFeed) {
			isValid = true;
			return isValid;
		}

		if (!routingMode) {
			return isValid;
		} else {
			switch (routingMode) {
				case 'invite':
					isValid = firstToAcceptCandidates.groupIds.length ||
						firstToAcceptCandidates.resourceNumbers.length ||
						firstToAcceptCandidates.vendorCompanyNumbers.length ||
						needToApplyCandidates.groupIds.length ||
						needToApplyCandidates.resourceNumbers.length ||
						needToApplyCandidates.vendorCompanyNumbers.length ? true : false;
					break;
				case 'autoInvite':
				case 'browse':
					isValid = true;
					break;
				default:
					isValid = false;
					break;
			}

			return isValid;
		}
	}

	talentPoolsTypeahead (groupIds) {
		const root = ReactDOM.findDOMNode(this);
		this.talentPoolsSelect = wmSelect({ selector: '[name="routing-talentPools"]', root }, {
			valueField: 'id',
			labelField: 'name',
			searchField: 'name',
			options: [],
			preload: true,
			loadThrottle: 200,
			allowEmptyOption: true,
			plugins: ['remove_button'],
			render: {
				option: (item) => `<div>${item.name} | ID: ${item.id}</div>`,
				item: (item) => `<div>${item.name} | ID: ${item.id}</div>`
			},
			onChange: (value) => this.props.updateNeedToApplyGroupIds(value),
			load: (query, callback) => fetch('/assignments/batch_send/routable_groups', { credentials: 'same-origin' })
				.then((res) => res.json())
				.then((res) => callback(res))
				.then(() => {
					groupIds.forEach((group) => {
						this.talentPoolsSelect.addItem(group);
					});
				})
		})[0].selectize;
	}

	firstToApplyTalentPoolsTypeahead (groupIds) {
		const root = ReactDOM.findDOMNode(this);
		this.firstToApplyTalentPoolsSelect = wmSelect({ selector: '[name="routing-autoAssignTalentPools"]', root }, {
			valueField: 'id',
			labelField: 'name',
			searchField: 'name',
			options: [],
			preload: true,
			loadThrottle: 200,
			allowEmptyOption: true,
			plugins: ['remove_button'],
			render: {
				option: (item) => `<div>${item.name} | ID: ${item.id}</div>`,
				item: (item) => `<div>${item.name} | ID: ${item.id}</div>`
			},
			onChange: (value) => this.props.updateFirstToAcceptGroupIds(value),
			load: (query, callback) => fetch('/assignments/batch_send/routable_groups', { credentials: 'same-origin' })
				.then((res) => res.json())
				.then((res) => callback(res))
				.then(() => {
					groupIds.forEach((group) => {
						this.firstToApplyTalentPoolsSelect.addItem(group);
					});
				})
		})[0].selectize;
	}

	workerTypeahead () {
		const root = ReactDOM.findDOMNode(this);
		this.workerSelect = wmSelect({ selector: '[name="routing-resourceNumbers"]', root }, {
			valueField: 'id',
			labelField: 'name',
			searchField: 'name',
			options: [],
			loadThrottle: 200,
			allowEmptyOption: true,
			plugins: ['remove_button'],
			render: {
				option: (item) => `<div>${item.name} | ID: ${item.id} | ${item.address}</div>`,
				item: (item) => `<div>${item.name} | ID: ${item.id} | ${item.address}</div>`
			},
			onChange: (value) => this.props.updateNeedToApplyResourceNumbers(value || []),
			load: (query, callback) => {
				if (query.length < 2) {
					return callback();
				}
				$.ajax({
					url: '/search/suggest_users.json',
					type: 'GET',
					dataType: 'json',
					data: { term: query, internal_only: this.props.pricing.type === 'INTERNAL' },
					error: callback,
					success: callback
				});
			}
		})[0].selectize;
	}

	firstToApplyWorkerTypeahead () {
		const root = ReactDOM.findDOMNode(this);
		this.firstToApplyWorkerSelect = wmSelect({ selector: '[name="routing-autoAssignResourceNumbers"]', root }, {
			valueField: 'id',
			labelField: 'name',
			searchField: 'name',
			options: [],
			loadThrottle: 200,
			allowEmptyOption: true,
			plugins: ['remove_button'],
			render: {
				option: (item) => `<div>${item.name} | ID: ${item.id} | ${item.address}</div>`,
				item: (item) => `<div>${item.name} | ID: ${item.id} | ${item.address}</div>`
			},
			onChange: (value) => this.props.updateFirstToAcceptResourceNumbers(value || []),
			load: (query, callback) => {
				if (query.length < 2) {
					return callback();
				}
				$.ajax({
					url: '/search/suggest_users.json',
					type: 'GET',
					dataType: 'json',
					data: { term: query, internal_only: this.props.pricing.type === 'INTERNAL' },
					error: callback,
					success: callback
				});
			}
		})[0].selectize;
	}

	vendorTypeahead () {
		const root = ReactDOM.findDOMNode(this);
		this.vendorSelect = wmSelect({ selector: '[name="routing-vendors"]', root }, {
			valueField: 'companyNumber',
			labelField: 'name',
			searchField: 'name',
			options: [],
			loadThrottle: 200,
			allowEmptyOption: true,
			plugins: ['remove_button'],
			render: {
				option: (item) => `<div>${item.name} | ID: ${item.companyNumber} | ${item.cityStateCountry}</div>`,
				item: (item) => `<div>${item.name} | ID: ${item.companyNumber} | ${item.cityStateCountry}</div>`
			},
			onChange: (value) => this.props.updateNeedToApplyVendorNumbers(value),
			load: (query, callback) => {
				if (!query.length) {
					return callback();
				}

				$.ajax({
					url: '/search/suggest_vendors.json',
					type: 'GET',
					dataType: 'json',
					data: { term: query },
					error: callback,
					success: callback
				});
			}
		})[0].selectize;
	}

	firstToApplyVendorTypeahead () {
		const root = ReactDOM.findDOMNode(this);
		this.firstToApplyVendorSelect = wmSelect({ selector: '[name="routing-autoAssignVendors"]', root }, {
			valueField: 'companyNumber',
			labelField: 'name',
			searchField: 'name',
			options: [],
			loadThrottle: 200,
			allowEmptyOption: true,
			plugins: ['remove_button'],
			preload: true,
			render: {
				option: (item) => `<div>${item.name} | ID: ${item.companyNumber} | ${item.cityStateCountry}</div>`,
				item: (item) => `<div>${item.name} | ID: ${item.companyNumber} | ${item.cityStateCountry}</div>`
			},
			onChange: (value) => this.props.updateFirstToAcceptVendorNumbers(value),
			load: (query, callback) => {
				if (!query.length) {
					return callback();
				}

				$.ajax({
					url: '/search/suggest_vendors.json',
					type: 'GET',
					dataType: 'json',
					data: { term: query },
					error: callback,
					success: callback
				});
			}
		})[0].selectize;
	}

	updateRoutingMode ({ target: { value } }) {
		this.setState({ routingMode: value });
		if (this.props.routing.smartRoute && value !== 'autoInvite') {
			this.props.updateSmartRoute(false);
		} else if (!this.props.routing.smartRoute && value === 'autoInvite') {
			this.props.updateSmartRoute(true);
		}
		if (!this.props.routing.browseMarketplace && value === 'browse') {
			this.props.toggleBrowseMarketplace(true);
		} else if (this.props.routing.browseMarketplace && value !== 'browse') {
			this.props.toggleBrowseMarketplace(false);
		}
	}

	render () {
		return (
			<div>
				<div className="assignment-routing">
					<div className="section-heading">Publish</div>
					<div className="public-marketplace routing-option">
						<WMCheckbox
							checked={ this.props.routing.shownInFeed }
							label="Post to Marketplace"
							onCheck={ this.props.toggleShownInFeed }
						/>
						<small>Selecting this option will make the assignment visible to any worker or vendor on Work Market.</small>
					</div>
					<div className="section-heading">Invite Talent</div>
					<div className="wm-accordion routing-selector">
						<div className="wm-accordion--heading routing-option" data-accordion-target="direct_send_options">
							<WMRadioButtonGroup
								name="routing-sendSpecificTalent"
								onChange={ this.updateRoutingMode }
								valueSelected={ this.state.routingMode }
							>
								<WMRadioButton
									label="Send to Specific Talent"
									value="invite"
								/>
							</WMRadioButtonGroup>
							<label className="subtext" htmlFor="send_type">Send assignments to specific Workers, Vendors or Talent Pools.</label>
						</div>
						<div className={ `wm-accordion--content ${this.state.routingMode === 'invite' ? 'open' : ''}` } data-accordion-name="direct_send_options">
							{ this.state.routingMode === 'invite' ? (
								<div className="inner">
									<label className="heading" htmlFor="talent_select">Talent</label>
									<div className="subtext">
										<div style={ styles.title }>
											<span>Invite to Apply</span>
											<WMFontIcon
												className="material-icons"
												id="invite-routing-resourceNumbers"
												style={ styles.icon }
												onMouseLeave={ () => {
													if (!this.state.resourceNumbers) {
														this.setState({ resourceNumbers: true });
													} else {
														this.setState({ resourceNumbers: false });
													}
												} }
												onMouseEnter={ () => {
													if (!this.state.resourceNumbers) {
														this.setState({ resourceNumbers: true });
													} else {
														this.setState({ resourceNumbers: false });
													}
												} }
											>
												info
											</WMFontIcon>
										</div>
										{ this.state.resourceNumbers &&
											<small>&#8216;Invite to apply&#8217; allows workers to apply. You will then be able to select a worker.</small>
										}
										<select className="wm-select" name="routing-resourceNumbers" id="routing-resourceNumbers" multiple />
										<div style={ styles.subtext }>
											<span>Auto Assign</span>
											<WMFontIcon
												className="material-icons"
												id="auto-assign-routing-autoAssignResourceNumbers"
												style={ styles.icon }
												onMouseLeave={ () => {
													if (!this.state.autoAssignResourceNumbers) {
														this.setState({ autoAssignResourceNumbers: true });
													} else {
														this.setState({ autoAssignResourceNumbers: false });
													}
												} }
												onMouseEnter={ () => {
													if (!this.state.autoAssignResourceNumbers) {
														this.setState({ autoAssignResourceNumbers: true });
													} else {
														this.setState({ autoAssignResourceNumbers: false });
													}
												} }
											>
												info
											</WMFontIcon>
										</div>
										{ this.state.autoAssignResourceNumbers &&
											<small>&#8216;Auto Assign&#8217; will automatically assign the first applicant that applies.</small>
										}
										<select className="wm-select" name="routing-autoAssignResourceNumbers" id="routing-autoAssignResourceNumbers" multiple />
									</div>

									<label className="heading" htmlFor="talent_pool_select">Talent Pools</label>
									<div className="subtext">
										<div style={ styles.subtext }>
											<span>Invite to Apply</span>
											<WMFontIcon
												className="material-icons"
												id="invite-routing-talentPools"
												style={ styles.icon }
												onMouseLeave={ () => {
													if (!this.state.talentPools) {
														this.setState({ talentPools: true });
													} else {
														this.setState({ talentPools: false });
													}
												} }
												onMouseEnter={ () => {
													if (!this.state.talentPools) {
														this.setState({ talentPools: true });
													} else {
														this.setState({ talentPools: false });
													}
												} }
											>
												info
											</WMFontIcon>
										</div>
										{ this.state.talentPools &&
											<small>&#8216;Invite to apply&#8217; allows workers to apply. You will then be able to select a worker.</small>
										}
										<select className="wm-select" name="routing-talentPools" id="routing-talentPools" multiple />
										<div style={ styles.subtext }>
											<span>Auto Assign</span>
											<WMFontIcon
												className="material-icons"
												id="auto-assign-routing-autoAssignTalentPools"
												style={ styles.icon }
												onMouseLeave={ () => {
													if (!this.state.autoAssignTalentPools) {
														this.setState({ autoAssignTalentPools: true });
													} else {
														this.setState({ autoAssignTalentPools: false });
													}
												} }
												onMouseEnter={ () => {
													if (!this.state.autoAssignTalentPools) {
														this.setState({ autoAssignTalentPools: true });
													} else {
														this.setState({ autoAssignTalentPools: false });
													}
												} }
											>
												info
											</WMFontIcon>
										</div>
										{ this.state.autoAssignTalentPools &&
											<small>&#8216;Auto Assign&#8217; will automatically assign the first applicant that applies.</small>
										}
										<select className="wm-select" name="routing-autoAssignTalentPools" id="routing-autoAssignTalentPools" multiple />
									</div>

									<label className="heading" htmlFor="talent_pool_select">Vendors</label>
									<div className="subtext">
										<div style={ styles.subtext }>
											<span>Invite to Apply</span>
											<WMFontIcon
												className="material-icons"
												id="invite-routing-vendors"
												style={ styles.icon }
												onMouseLeave={ () => {
													if (!this.state.vendors) {
														this.setState({ vendors: true });
													} else {
														this.setState({ vendors: false });
													}
												} }
												onMouseEnter={ () => {
													if (!this.state.vendors) {
														this.setState({ vendors: true });
													} else {
														this.setState({ vendors: false });
													}
												} }
											>
												info
											</WMFontIcon>
										</div>
										{ this.state.vendors &&
											<small>&#8216;Invite to apply&#8217; allows workers to apply. You will then be able to select a worker.</small>
										}
										<select className="wm-select" name="routing-vendors" id="routing-vendors" multiple />
										<div style={ styles.subtext }>
											<span>Auto Assign</span>
											<WMFontIcon
												className="material-icons"
												id="auto-assign-routing-autoAssignVendors"
												style={ styles.icon }
												onMouseLeave={ () => {
													if (!this.state.autoAssignVendors) {
														this.setState({ autoAssignVendors: true });
													} else {
														this.setState({ autoAssignVendors: false });
													}
												} }
												onMouseEnter={ () => {
													if (!this.state.autoAssignVendors) {
														this.setState({ autoAssignVendors: true });
													} else {
														this.setState({ autoAssignVendors: false });
													}
												} }
											>
												info
											</WMFontIcon>
										</div>
										{ this.state.autoAssignVendors &&
											<small>&#8216;Auto Assign&#8217; will automatically assign the first applicant that applies.</small>
										}
										<select className="wm-select" name="routing-autoAssignVendors" id="routing-autoAssignVendors" multiple />
									</div>
								</div>
							) : '' }
						</div>
						<div className="wm-accordion--heading routing-option">
							<WMRadioButtonGroup
								name="routing-autoInvite"
								onChange={ this.updateRoutingMode }
								valueSelected={ this.state.routingMode }
							>
								<WMRadioButton
									label="Auto-Invite"
									value="autoInvite"
								/>
							</WMRadioButtonGroup>
							<label htmlFor="send_type" className="subtext">This option will automatically determine the best workers for your assignment.</label>
						</div>
						<div className="wm-accordion--heading routing-option">
							<WMRadioButtonGroup
								name="routing-autoInvite"
								onChange={ this.updateRoutingMode }
								valueSelected={ this.state.routingMode }
							>
								<WMRadioButton
									label="Browse Talent Marketplace"
									value="browse"
								/>
							</WMRadioButtonGroup>
							<label htmlFor="send_type" className="subtext">Search the Talent Marketplace to find the best people for the job.</label>
						</div>
					</div>
				</div>
			</div>
		);
	}
}

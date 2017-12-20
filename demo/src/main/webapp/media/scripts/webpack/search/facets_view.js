import $ from 'jquery';
import 'jquery-ui';
import Backbone from 'backbone';
import _ from 'underscore';
import FacetsModel from './facets_model';
import SearchModeModel from './search_modes_model';
import LaneTypeModel from './lane_type_model';
import MemberStatusModel from './member_status_model';
import AssessmentStatusModel from './assessment_status_model';
import wmSelect from '../funcs/wmSelect';
import wmNotify from '../funcs/wmNotify';
import profileCard from '../funcs/wmProfileCard';
import NoResultsTemplate from './templates/no_results_message.hbs';
import ResultsItemTemplate from './templates/results_item.hbs';
import wmSlider from '../funcs/wmSlider';
import jdenticon from '../dependencies/jquery.jdenticon';
import SearchFilterUI from '../funcs/wmSearchFilter';
import SearchTracker from './search_tracker';
import Application from '../core';

export default Backbone.View.extend({
	el: '.main.container',

	events: {
		'change ul.filter_items input[type="checkbox"]': 'clickFilter',
		'change [name="avatar"]': 'loadData',
		'click .wm-pagination--back': 'showPreviousPage',
		'click .wm-pagination--next': 'showNextPage',
		'click #keyword_go, #location_go': 'searchData',
		'click #clear_facets': 'clearEverything',
		'change #sortby-control': 'clickSort',
		'change [name="slider"]': 'sliderChange',
		'click [name*="_showall"], [name="groupStatusShowall"]': 'clickShowAll',
		'change #page_size': 'changePageSize'
	},

	initialize (options) {
		this.model = new FacetsModel(_.omit(options, 'searchKeywords'));
		this.searchModes = new SearchModeModel();
		this.laneType = new LaneTypeModel();
		this.memberStatus = new MemberStatusModel();
		this.assessmentStatus = new AssessmentStatusModel();
		this.options = options || {};
		this.sortby = $('#sortby');
		this.sortbyControl = $('#sortby-control');
		this.restrictLanes = [];
		this.restrictToCountries = [];
		this.lanes = '#lanes';
		this.industries = '#industries';
		this.assessments = '#assessments';
		this.assessmentStatusGroup = '#assessment_status';
		this.verifications = '#verifications';
		this.certifications = '#certifications';
		this.licenses = '#licenses';
		this.ratings = '#ratings';
		this.radius = '#radius';
		this.countries = '#countries';
		this.companytypes = '#companytypes';
		this.groups = '#groups';
		this.sharedGroups = '#shared-groups';
		this.searchResults = $('#search_results');
		this.noResultsMessage = NoResultsTemplate;
		this.locationInput = '#address';
		this.keywordInput = '#keyword';
		this.isInternalPricing = this.model.get('pricing_type') === 'INTERNAL' || this.model.get('isDispatch');
		this.autoSelectedLanes = this.isInternalPricing ? [0, 1] : [];
		this.searchType = this.isInternalPricing ? 'workers' : options.searchType || 'workers';
		this.searchTracker = new SearchTracker();
		this.disableDeepLinking = this.options.disableDeepLinking;
		this.searchAjaxRequests = null;

		this.sortby.val('relevance');

		if (!_.isUndefined(config.disableDeepLinking) && config.disableDeepLinking !== '') {
			this.disableDeepLinking = config.disableDeepLinking;
		}

		this.searchFilter = new SearchFilterUI({
			el: '.search-filter-bucket',
			hideDateRange: true,
			change: this.searchData.bind(this),
			placeholderText: 'Search for workers, companies, job titles, and skills'
		});

		if (!_.isUndefined(config.addressZip) && config.addressZip !== '') {
			this.searchFilter.handleFilterEntry({
				label: 'Location',
				title: `Within 60 miles of ${config.addressZip}`,
				filterValue: {
					name: 'location',
					label: 'Location',
					value: {
						address: config.addressZip,
						radius: 60
					}
				}
			});
		}

		this.searchTracker.trackSession('2.0', this.model.get('mode'));

		if (!this.disableDeepLinking) {
			// store query parameters for use in loading initial search
			let match;
			const pl = /\+/g;
			const search = /([^&=]+)=?([^&]*)/g;
			const decode = function decodeFunc (s) { return decodeURIComponent(s.replace(pl, ' ')); };
			const query = window.location.search.substring(1);
			this.deepLinkedSearchParams = [];

			while (match = search.exec(query)) {
				this.deepLinkedSearchParams.push({
					[decode(match[1])]: decode(match[2])
				});
			}

			// set model values to the query parameters where needed
			if (!_.isUndefined(_.compact(_.pluck(this.deepLinkedSearchParams, 'searchType'))[0])) {
				const value = _.compact(_.pluck(this.deepLinkedSearchParams, 'searchType'))[0];
				this.searchType = value;
			}

			if (!_.isUndefined(this.getDeepLinkedParam('start'))) {
				this.model.set('results_start', parseInt(_.compact(_.pluck(this.deepLinkedSearchParams, 'start'))[0], 10));
			}

			if (!_.isUndefined(this.getDeepLinkedParam('limit'))) {
				this.model.set('results_per_page', parseInt(this.getDeepLinkedParam('limit'), 10));
			}
			if (!_.isUndefined(this.getDeepLinkedParam('sortby'))) {
				const value = this.getDeepLinkedParam('sortby');
				this.sortby.val(value);
				$(`#sortby-control option[value="${value}"]`).attr('selected', 'selected');
			}
		}
		this.loadData();
		this.toggleFilters();

		_.each(this.model.attributes.existingWorkers, function func (i) {
			this.model.attributes.existingWorkersLookup[i] = true;
		}, this);

		_.each(this.model.attributes.appliedWorkers, function func (i) {
			this.model.attributes.appliedWorkersLookup[i] = true;
		}, this);

		_.each(this.model.attributes.declinedWorkers, function func (i) {
			this.model.attributes.declinedWorkersLookup[i] = true;
		}, this);

		$(this.model.get('form_id')).sortable({
			animation: 300,
			draggable: '.sidebar-card',
			handle: '.sidebar-card--title',
			store: {
				get: () => {
					return this.model.get('filterOrder') ? this.model.get('filterOrder').split('|') : [];
				},
				set (sortable) {
					$.ajax({
						url: '/search/filters',
						type: 'POST',
						data: { filters: sortable.toArray().join('|') },
						dataType: 'JSON'
					});
				}
			}
		});

		Backbone.Events.on('loadData', this.loadData, this);
		$('#page_size').val(this.model.attributes.results_per_page);
	},

	getDeepLinkedParam (param) {
		return _.compact(_.pluck(this.deepLinkedSearchParams, param))[0];
	},

	// Pagination
	resetPagination () {
		this.model.attributes.results_start = 0;
	},

	showNextPage () {
		if (this.getCurrentPage() === this.getTotalPages()) {
			return false;
		}
		this.model.attributes.results_start += this.model.attributes.results_per_page;
		this.loadData();

		return true;
	},

	showPreviousPage () {
		if (this.getCurrentPage() === 1) {
			return false;
		}
		this.model.attributes.results_start -= this.model.attributes.results_per_page;
		this.loadData();

		return true;
	},

	getTotalPages () {
		return Math.ceil(
			this.model.attributes.results_count / this.model.attributes.results_per_page
		) || 1;
	},

	getCurrentPage () {
		return Math.floor(
			this.model.attributes.results_start / this.model.attributes.results_per_page
		) + 1;
	},

	redrawPagination () {
		this.$('.wm-pagination').attr('data-min', this.getCurrentPage());
		this.$('.wm-pagination').attr('data-max', this.getTotalPages());
		this.redrawFilteredIndustries();
		this.$('.wm-pagination--back').prop('disabled', this.getCurrentPage() === 1);
		this.$('.wm-pagination--next').prop('disabled', this.getCurrentPage() === this.getTotalPages());
	},

	clickFilter () {
		this.handleSelectAll();
		this.resetPagination();
		this.loadData();
	},

	handleSelectAll () {
		_.each([
			this.groups,
			this.industries,
			this.lanes,
			this.sharedGroups,
			this.assessments,
			this.certifications,
			this.licenses,
			this.companytypes,
			this.assessmentStatusGroup
		],
			(facet) => {
				if (
					$(facet).find('.filter_items input').is(':checked') ||
					$(facet).find('.more-filters option').length
				) {
					$(facet).find('input:first').attr('checked', false);
				} else {
					$(facet).find('input:first').click();
				}
			}
		);
	},

	handleDeepLinkedSearch (params, filters) {
		this.deepLinkedSearchParams.forEach((param) => {
			const key = Object.keys(param)[0];
			const value = param[key];
			let filterToSearch;
			let label;

			switch (key) {
			case 'group':
				filterToSearch = filters.groups;
				label = 'Talent Pool';
				break;
			case 'sharedgroup':
				filterToSearch = filters.sharedgroups;
				label = 'Shared Talent Pool';
				break;
			case 'industry':
				filterToSearch = filters.industries;
				label = 'Industry';
				break;
			case 'company':
				label = 'Company';
				filterToSearch = filters.companies;
				break;
			case 'verification':
				label = 'Verification';
				filterToSearch = filters.verifications;
				break;
			case 'lane':
				label = 'Type';
				filterToSearch = filters.lanes;
				break;
			case 'license':
				label = 'License';
				filterToSearch = filters.licenses;
				break;
			case 'certification':
				label = 'Certification';
				filterToSearch = filters.certifications;
				break;
			case 'assessment':
				label = 'Tests';
				filterToSearch = filters.assessments;
				break;
			case 'address':
				{
					const radius = this.deepLinkedSearchParams.find(({ radius }) => typeof radius !== 'undefined');
					const countriesToFilterOn = !_.isUndefined(countries) ? countries.countries : null;
					const locationFilter = this.buildLocationFilter(value, radius, countriesToFilterOn);
					this.searchFilter.handleFilterEntry(locationFilter, true);
					break;
				}
			case 'satisfactionRate':
				label = 'Min Satisfaction';
				break;
			case 'onTimePercentage':
				label = 'Min On Time';
				break;
			case 'deliverableOnTimePercentage':
				label = 'Min Deliverable On Time';
				break;
			case 'keyword':
				if (_.isString(value) && value.trim() !== '') {
					label = 'Keyword';
				}
				break;
			default:
				break;
			}

			if (!_.isUndefined(filterToSearch) && filterToSearch.length > 0) {
				const fullFilter = _.findWhere(filterToSearch, { id: value });
				if (!_.isUndefined(fullFilter)) {
					this.searchFilter.handleFilterEntry({
						label,
						title: fullFilter.name,
						filterValue: {
							name: key,
							label: fullFilter.name,
							value
						}
					}, true);
				}
			} else if (!_.isUndefined(label)) {
				this.searchFilter.handleFilterEntry({
					label,
					title: value,
					filterValue: {
						name: key,
						label,
						value
					}
				}, true);
			}
		});
		this.deepLinkedSearchParams = {};
	},

	loadData () {
		$('.results-list').hide();
		$('.search-filter-loadstate').show();
		const activeFilters = this.searchFilter.getFilterObject();
		let filterData = [];
		if (
			!_.isUndefined(this.deepLinkedSearchParams) &&
			Object.keys(this.deepLinkedSearchParams).length > 0
		) {
			this.deepLinkedSearchParams.forEach((param) => {
				filterData.push(param);
			});
		} else {
			filterData = [
				{
					searchType: this.searchType
				},
				{
					sortby: this.sortby.val()
				},
				{
					start: this.model.get('results_start')
				},
				{
					limit: this.model.get('results_limit')
				}
			];

			if (this.model.get('mode') === 'assignment') {
				filterData.push({ search_type: 'PEOPLE_SEARCH_ASSIGNMENT' });
				filterData.push({ work_number: this.model.get('work_number') });
			} else if (this.model.get('mode') === 'people-search') {
				filterData.push({ search_type: 'PEOPLE_SEARCH' });
			} else if (this.model.get('mode') === 'group-detail') {
				filterData.push({ search_type: 'PEOPLE_SEARCH_GROUP_MEMBER' });
			} else {
				// there really should be other conditions here, just not sure how to distinguish
				// between invite to test (PEOPLE_SEARCH_ASSESSMENT_INVITE)
				// and invite to group (PEOPLE_SEARCH_GROUP)
				filterData.push({ search_type: 'PEOPLE_SEARCH' });
			}

			if (this.model.get('mode') === 'group-detail') {
				filterData.push({ group_id: this.model.get('groupId') });
			}

			if (this.model.get('isDispatch') === true) {
				filterData.push({ resource_mode: 'dispatch' });
			} else {
				filterData.push({ resource_mode: 'workers' });
			}
		}

		if (this.isInternalPricing) {
			filterData.push({ internal_only: true });
		}

		activeFilters.forEach((filter) => {
			const objectToPush = {};
			if (filter.name === 'location') {
				if (filter.value.address !== undefined && filter.value.address !== null) {
					filterData.push({ address: filter.value.address });
					filterData.push({ radius: filter.value.radius });
				}
				if (filter.value.countries !== undefined && filter.value.countries !== null) {
					filterData.push({ countries: filter.value.countries });
				}
			} else if (filter.value === 'avatar') {
				objectToPush.avatar = true;
				filterData.push(objectToPush);
			} else if (filter.value === 'WORKER' || filter.value === 'VENDOR') {
				objectToPush.userTypes = filter.value;
				filterData.push(objectToPush);
			} else if (filter.value === 'BLOCKED_WORKER') {
				objectToPush.showBlockedUsers = true;
				filterData.push(objectToPush);
			} else if (filter.value.indexOf('companyType:') === 0) {
				objectToPush.companytypes = filter.value.substring(filter.value.indexOf(':') + 1);
				filterData.push(objectToPush);
			} else if (filter.name === 'groupstatus' && filter.value === 'member') {
				objectToPush.member = true;
				filterData.push(objectToPush);
			} else if (filter.name === 'groupstatus' && filter.value === 'memberoverride') {
				objectToPush.memberoverride = true;
				filterData.push(objectToPush);
			} else if (filter.name === 'groupstatus' && filter.value === 'pending') {
				objectToPush.pending = true;
				filterData.push(objectToPush);
			} else if (filter.name === 'groupstatus' && filter.value === 'pendingoverride') {
				objectToPush.pendingoverride = true;
				filterData.push(objectToPush);
			} else if (filter.name === 'groupstatus' && filter.value === 'invited') {
				objectToPush.invited = true;
				filterData.push(objectToPush);
			} else if (filter.name === 'groupstatus' && filter.value === 'declined') {
				objectToPush.declined = true;
				filterData.push(objectToPush);
			} else {
				objectToPush[filter.name] = filter.value;
				filterData.push(objectToPush);
			}
		});

		const queryStringArray = [];
		for (const key in filterData) {
			const filterName = Object.keys(filterData[key])[0];
			const filterVal = filterData[key][filterName];
			queryStringArray.push(`${encodeURIComponent(filterName)}=${encodeURIComponent(filterVal)}`);
		}
		const startTime = new Date().getTime();
		const queryString = queryStringArray.join('&');

		if (this.searchAjaxRequests != null) {
			this.searchAjaxRequests.abort();
		}
		this.searchAjaxRequests = $.ajax({
			context: this,
			url: this.getSearchTypeUrl(),
			dataType: 'json',
			data: queryString,
			success: (response) => {
				if (!this.disableDeepLinking) {
					history.replaceState({}, '', `/search?${queryString}`);
				}
				this.restrictLanes = this.getRestrictedLanes(response.mode);
				$('.results-list').show();
				$('.search-filter-loadstate').hide();
				this.handleLoadData(response);
				const searchVersion = ('search_version' in response) ? response.search_version : '1.0';
				this.searchTracker.trackSearch(
					searchVersion,
					queryString,
					new Date().getTime() - startTime,
					response.results_count);
				this.searchAjaxRequests = null;
			}
		});
	},

	getSearchTypeUrl () {
		return `/search/${this.searchType === 'vendors' ? 'vendors' : 'retrieve'}`;
	},

	handleLoadData (data) {
		this.model.attributes.filters = data.filters;
		this.model.attributes.results_count = data.results_count;

		// Redraw elements.
		$('#select_all').prop('checked', false);
		$('input[name="select_all"]').prop('checked', false);

		this.redrawPagination();
		if (data.exception) {
			wmNotify({
				message: 'There was a problem retrieving your results. Please try again!',
				type: 'danger'
			});
		} else {
			Backbone.Events.trigger('searchResultsLoaded', data.results);
			this.redrawResults(data.results);
			this.redrawFilters(data.filters, data.hasMarketplace);
			Application.Events.trigger('search:changePage');
		}

		if (data.warnings) {
			if (data.warnings.location) {
				wmNotify({
					message: data.warnings.location,
					type: 'danger'
				});
			}
		}

		if (!_.isUndefined(this.deepLinkedSearchParams) &&
			Object.keys(this.deepLinkedSearchParams).length > 0) {
			this.handleDeepLinkedSearch(this.deepLinkedSearchParams, data.filters);
		}
	},

	getRestrictedLanes (mode) {
		if (mode == null) {
			return [];
		}

		if (this.isInternalPricing) {
			return [2, 3, 4];
		} else if (mode === 'workers') {
			return [1];
		} else if (mode === 'dispatch') {
			return [0, 2, 3, 4];
		} else {
			return [];
		}
	},

	stripProtocol (str) {
		if (str) {
			str = str.replace('http://', '//');
			str = str.replace('https://', '//');
		}

		return str;
	},

	redrawResults (data) {
		let lane4_only = true;

		this.searchResults.empty();
		if (!_.isEmpty(data)) {
			_.each(data, function (user) {
				user.ontime_reliability = Math.round(user.ontime_reliability);
				user.rating = Math.round(user.rating);
				user.deliverable_on_time_reliability = Math.round(user.deliverable_on_time_reliability);

				const isExistingWorker = this.model.get('existingWorkersLookup')[user.userNumber] && user.userType === 'WORKER';
				const isDeclinedWorker = this.model.get('declinedWorkersLookup')[user.userNumber] && user.userType === 'WORKER';
				const isAppliedWorker = this.model.get('appliedWorkersLookup')[user.userNumber] && user.userType === 'WORKER';
				const isInvitedVendor = _.contains(this.model.get('existingVendorNumbers'), user.userNumber) && user.userType === 'VENDOR';
				const isDeclinedVendor = _.contains(this.model.get('declinedVendorNumbers'), user.userNumber) && user.userType === 'VENDOR';

				_.defaults(user, {
					isExistingWorker,
					isDeclinedWorker,
					isAppliedWorker,
					isInvitedVendor,
					isDeclinedVendor,
					mode: this.model.get('mode'),
					pricing_type: this.model.get('pricing_type'),
					work_number: this.model.get('work_number'),
					group_id: this.model.get('groupId'),
					searchModes: this.searchModes.attributes,
					memberStatus: this.model.get('mode') === this.searchModes.get('groupDetail') ? this.memberStatus.attributes : {},
					assessmentStatuses: this.model.get('mode') === this.searchModes.get('assessment') ? this.assessmentStatus.attributes : {},
					isGroupAdmin: this.model.get('isGroupAdmin'),
					isDispatch: this.model.get('isDispatch'),
					isVendor: false,
					disableActions: false,
					user,
					publicWorkers: false,
					lastWork: false,
					assignToFirstWorker: this.model.get('assignToFirstWorker'),
					eligibility: (user.eligibility !== null && typeof user.eligibility !== 'undefined') ? user.eligibility : '',
					avatarAssetUri: this.stripProtocol(user.avatar_asset_uri),
					orUserNumberOrCompanyNumber: user.userNumber || user.companyNumber,
					isCheckboxBlocked: isExistingWorker || isInvitedVendor || isDeclinedVendor,
					isUsa: user.country === 'United States' || user.country === 'USA',
					isValidEmail: user.lane === (0, 1, 2, 3),
					firstGroup: (user.groups !== null && typeof user.groups !== 'undefined') ? user.groups[0] : '',
					firstCompanyAssessment: (user.company_assessments !== null && typeof user.company_assessments !== 'undefined') ? user.company_assessments[0] : '',
					firstCertifications: (user.certifications !== null && typeof user.certifications !== 'undefined') ? user.certifications[0] : '',
					firstLicenses: (user.licenses !== null && typeof user.licenses !== 'undefined') ? user.licenses[0] : '',
					firstInsurances: user.insurances ? user.insurances[0] : '',
					searchHighlights: this.summarizeSnippets(user.userType, user.snippets)
				});

				this.replaceWithSnippetData(user);
				this.searchResults.append(ResultsItemTemplate(user));

				// If user is not lane 4, set flag.
				if (user.lane !== 4) {
					lane4_only = false;
				}
			}, this);

			$('#select_all').prop('disabled', false);
		} else {
			if (this.model.attributes.mode === this.searchModes.attributes.groupDetail) {
				this.searchResults.append(this.noResultsMessage({
					title: 'No Talent Pool Members have been added yet',
					messages: [
						'Some suggestions on how to improve your results:',
						'-Expand your search by removing one or more search filters.',
						'-Try changing or removing any keywords you\'re using.'
					]
				}));
			} else {
				this.searchResults.append(this.noResultsMessage({
					title: 'Suggestions:',
					messages: [
						'Double check the spelling of your search.',
						'Try another keyword or broaden your results by removing one or more search filters.'
					]
				}));
			}

			$('#select_all').prop('disabled', true);
		}

		jdenticon();
		profileCard();
	},

	redrawFilters (data, hasMarketplace) {
		// should we check this before removeAllFilters?
		if (_.isUndefined(data)) {
			data = {};
		}

		// clean slate
		this.searchFilter.removeAllFilters();

		if (!_.isUndefined(data.assessments) && data.assessments.length > 0) {
			this.buildFacet(data.assessments, 'assessment', 'Tests', 'default');
		}

		// add "Licenses"
		if (!_.isUndefined(data.licenses) && data.licenses.length > 0) {
			this.buildFacet(data.licenses, 'license', 'Licenses', 'default');
		}

		// add "Certifications"
		if (!_.isUndefined(data.certifications) && data.certifications.length > 0) {
			this.buildFacet(data.certifications, 'certification', 'Certification', 'default', '/search/suggest_certifications.json');
		}

		// add "Worker Type"
		if (!_.isUndefined(data.lanes) && data.lanes.length > 0) {
			const workerTypeFilterOptions = [];
			for (let i = 0; i < data.lanes.length; i++) {
				// don't expose lane 0 - internal users and lane 4 - everyone else
				if (data.lanes[i].id !== '0' && data.lanes[i].id !== '4') {
					workerTypeFilterOptions.push({ label: 'Type', id: data.lanes[i].id, title: data.lanes[i].name });
				}
			}

			if (hasMarketplace) {
				workerTypeFilterOptions.push({ label: 'Worker Type', id: 'WORKER', title: 'Workers Only' });
				workerTypeFilterOptions.push({ label: 'Worker Type', id: 'VENDOR', title: 'Companies Only' });
			}

			// NOTE: we currently don't want to provide option to show blocked companies
			// therefore only blocked workers option here
			workerTypeFilterOptions.push({ label: 'Worker Type', id: 'BLOCKED_WORKER', title: 'My Blocked Workers'});

			this.searchFilter.addFilter({
				name: 'lane',
				title: 'Type',
				template: 'default',
				options: workerTypeFilterOptions
			});
		}

		// add "insurance"
		if (this.searchType !== 'vendors') {
			this.searchFilter.addAdvancedFilter({
				name: 'insurance',
				title: 'Insurance',
				template: 'insurance',
				renderCallback: function () {
					wmSlider();
					$('input[name="slider"]').on('change', (event) => {
						const $target = $(event.currentTarget);
						const percentage = $target.val();
						const id = $target.attr('id');
						$('.wm-search-filter--insurance').find(`input[id="${id}"]`).val(percentage);
					});

					$('.apply-insurance').on('click', () => {
						if ($('input[id="errorsAndOmissionsCoverage"]').val() > 0) {
							this.searchFilter.removeSelectionByName('Errors And Omissions');
							this.searchFilter.handleFilterEntry({
								label: 'Errors And Omissions',
								title: $('input[id="errorsAndOmissionsCoverage"]').val(),
								filterValue: {
									name: 'errorsAndOmissionsCoverage',
									label: 'Errors And Omissions',
									value: $('input[id="errorsAndOmissionsCoverage"]').val()
								}
							});
						}

						if ($('input[id="generalLiabilityCoverage"]').val() > 0) {
							this.searchFilter.removeSelectionByName('General Liability');
							this.searchFilter.handleFilterEntry({
								label: 'General Liability',
								title: $('input[id="generalLiabilityCoverage"]').val(),
								filterValue: {
									name: 'generalLiabilityCoverage',
									label: 'General Liability',
									value: $('input[id="generalLiabilityCoverage"]').val()
								}
							});
						}

						if ($('input[id="workersCompCoverage"]').val() > 0) {
							this.searchFilter.removeSelectionByName('Workers Compensation');
							this.searchFilter.handleFilterEntry({
								label: 'Workers Compensation',
								title: $('input[id="workersCompCoverage"]').val(),
								filterValue: {
									name: 'workersCompCoverage',
									label: 'Workers Compensation',
									value: $('input[id="workersCompCoverage"]').val()
								}
							});
						}

						if ($('input[id="automobileCoverage"]').val() > 0) {
							this.searchFilter.removeSelectionByName('Automobile');
							this.searchFilter.handleFilterEntry({
								label: 'Automobile',
								title: $('input[id="automobileCoverage"]').val(),
								filterValue: {
									name: 'automobileCoverage',
									label: 'Automobile',
									value: $('input[id="automobileCoverage"]').val()
								}
							});
						}

						if ($('input[id="contractorsCoverage"]').val() > 0) {
							this.searchFilter.removeSelectionByName('Contractors Insurance');
							this.searchFilter.handleFilterEntry({
								label: 'Contractors Insurance',
								title: $('input[id="contractorsCoverage"]').val(),
								filterValue: {
									name: 'contractorsCoverage',
									label: 'Contractors Insurance',
									value: $('input[id="contractorsCoverage"]').val()
								}
							});
						}

						if ($('input[id="commercialGeneralLiabilityCoverage"]').val() > 0) {
							this.searchFilter.removeSelectionByName('Commercial General Liability');
							this.searchFilter.handleFilterEntry({
								label: 'Commercial General Liability',
								title: $('input[id="commercialGeneralLiabilityCoverage"]').val(),
								filterValue: {
									name: 'commercialGeneralLiabilityCoverage',
									label: 'Commercial General Liability',
									value: $('input[id="commercialGeneralLiabilityCoverage"]').val()
								}
							});
						}

						if ($('input[id="businessLiabilityCoverage"]').val() > 0) {
							this.searchFilter.removeSelectionByName('Business Liability');
							this.searchFilter.handleFilterEntry({
								label: 'Business Liability',
								title: $('input[id="businessLiabilityCoverage"]').val(),
								filterValue: {
									name: 'businessLiabilityCoverage',
									label: 'Business Liability',
									value: $('input[id="businessLiabilityCoverage"]').val()
								}
							});
						}
						$('.close-advanced-facet').trigger('click');
					});
				}.bind(this)
			});
		}

		// add "ratings"
		this.searchFilter.addAdvancedFilter({
			name: 'ratings',
			title: 'Ratings',
			template: 'ratings',
			renderCallback: function () {
				wmSlider();
				$('input[name="slider"]').on('change', (event) => {
					const $target = $(event.currentTarget);
					const percentage = $target.val();
					const id = $target.attr('id');
					$('.wm-search-filter--ratings').find(`input[id="${id}"]`).val(percentage);
				});

				$('.apply-ratings').on('click', () => {
					if ($('input[id="satisfactionRate"]').val() > 0) {
						this.searchFilter.removeSelectionByName('Min Satisfaction');
						this.searchFilter.handleFilterEntry({
							label: 'Min Satisfaction',
							title: $('input[id="satisfactionRate"]').val(),
							filterValue: {
								name: 'satisfactionRate',
								label: 'Min Satisfaction',
								value: $('input[id="satisfactionRate"]').val()
							}
						});
					}

					if ($('input[id="onTimePercentage"]').val() > 0) {
						this.searchFilter.removeSelectionByName('Min On Time');
						this.searchFilter.handleFilterEntry({
							label: 'Min On Time',
							title: $('input[id="onTimePercentage"]').val(),
							filterValue: {
								name: 'onTimePercentage',
								label: 'Min On Time',
								value: $('input[id="onTimePercentage"]').val()
							}
						});
					}

					if ($('input[id="deliverableOnTimePercentage"]').val() > 0) {
						this.searchFilter.removeSelectionByName('Min Deliverable On Time');
						this.searchFilter.handleFilterEntry({
							label: 'Min Deliverable On Time',
							title: $('input[id="deliverableOnTimePercentage"]').val(),
							filterValue: {
								name: 'deliverableOnTimePercentage',
								label: 'Min Deliverable On Time',
								value: $('input[id="deliverableOnTimePercentage"]').val()
							}
						});
					}

					$('.close-advanced-facet').trigger('click');
				});
			}.bind(this)
		});

		// add "verifications"
		if (!_.isUndefined(data.verifications) && data.verifications.length > 0) {
			this.buildFacet(data.verifications, 'verification', 'Verification', 'default');
		}

		// add "Org Units"
		if (!_.isUndefined(data.orgUnits) && data.orgUnits.length > 0) {
			this.buildFacet(data.orgUnits, 'orgUnits', 'Org. Unit', 'default');
		}

		// add "Talent Pools"
		if (!_.isUndefined(data.groups) && data.groups.length > 0) {
			this.buildFacet(data.groups, 'group', 'Talent Pool', 'default');
		}

		if (!_.isUndefined(data.sharedgroups) && data.sharedgroups.length > 0) {
			this.buildFacet(data.sharedgroups, 'sharedgroup', 'Shared Pool', 'default');
		}

		// add "Industries"
		if (!_.isUndefined(data.industries) && data.industries.length > 0) {
			this.buildFacet(data.industries, 'industry', 'Industry', 'default');
		}

		// add talent pool status
		if (this.model.get('mode') === 'group-detail') {
			const groupDetailOptions = [
				{
					id: 'member',
					name: 'Member'
				},
				{
					id: 'memberoverride',
					name: 'Member Override'
				},
				{
					id: 'pending',
					name: 'Pending - Meets Requirements'
				},
				{
					id: 'pendingoverride',
					name: 'Pending - Requirements Not Met'
				},
				{
					id: 'invited',
					name: 'Invited'
				},
				{
					id: 'declined',
					name: 'Declined'
				}
			];
			this.buildFacet(groupDetailOptions, 'groupstatus', 'Talent Pool Status', 'default');
		}

		// add "Location"
		this.searchFilter.addAdvancedFilter({
			name: 'location',
			title: 'Location',
			template: 'location',
			renderCallback: function () {
				const activeLocationFilter = this.searchFilter.getFilterObject().filter( (obj) => {
					return obj.label === 'Location';
				}).pop(); //get last one

				if (activeLocationFilter) {
					$(this.locationInput).val(activeLocationFilter.value.address);
					$(this.radius).val(activeLocationFilter.value.radius);
				}

				wmSelect({ selector: this.radius });
				wmSelect({ selector: this.countries }, {
					plugins: ['remove_button'],
					maxItems: null,
					placeholder: 'All Countries...',
					labelField: 'name',
					valueField: 'id',
					searchField: ['id', 'name']
				});
				if (!_.isUndefined(data.countries)) {
					const $selectize = $(this.countries)[0].selectize;
					$selectize.loadedSearches = {};
					$selectize.userOptions = {};
					$selectize.renderCache = {};
					$selectize.options = $selectize.sifter.items = {};
					$selectize.lastQuery = null;
					data.countries.forEach((country) => {
						$selectize.addOption(country);
					});
				} else {
					$('#countries-filters').hide();
				}

				$('.apply-location').on('click', () => {
					let address = $(this.locationInput).val(),
						radius = $(this.radius).val(),
						countries = $(this.countries).val();

					this.searchFilter.removeSelectionByName('Location');
					const locationFilter = this.buildLocationFilter(address, radius, countries);
					if (locationFilter !== null) {
						this.searchFilter.handleFilterEntry(locationFilter);
					}
				});
			}.bind(this)
		});
	},

	buildLocationFilter (address, radius, countries) {
		if (address === '' && countries === null) {
			return;
		}

		let locationString = '';
		const locationValue = {};

		if (address !== '') {
			locationString = `Within ${radius} miles of ${address}`;
			locationValue.address = address;
			locationValue.radius = radius;
			if (countries !== null) {
				locationString += `, ${countries}`;
				locationValue.countries = countries;
			}
		} else if (countries !== null) {
			locationString = `Within ${countries}`;
			locationValue.countries = countries;
		}

		return {
			label: 'Location',
			title: locationString,
			filterValue: {
				name: 'location',
				label: 'Location',
				value: locationValue
			}
		};
	},

	buildFacet (filterObj, filterName, filterLabel, filterTemplate, loadUrl) {
		const filterOptions = filterObj.reduce((memo, { id, name }) => {
		    const title = name;
		    const label = filterLabel;
		    return [...memo, { label, id, title }];
		}, []);

		this.searchFilter.addFilter({
			name: filterName,
			title: filterLabel,
			template: filterTemplate,
			options: filterOptions,
			loadUrl
		});
	},

	redrawFilteredIndustries () {
		const industries = [];

		if (typeof this.model.attributes !== 'undefined' && typeof this.model.attributes.filters !== 'undefined') {
			_.each(this.model.attributes.filters.industries, function (elem) {
				if (elem.filter_on) {
					industries.push(this.model.attributes.filters.labels.industries[elem.id]);
				}
			}, this);
		}

		if (!$('input[name="boost_industry"]').val()) {
			$('#search_industries').text(industries.length === 0 ? 'all industries' : industries.join(', '));
		}
	},

	resetFilters () {
		this.searchFilter.reset();
	},

	clickShowAll () {

	},

	clickSort () {
		if (_.where(this.searchFilter.getFilterObject(), { name: 'location' }).length === 0 && this.sortbyControl.val() === 'distance_asc') {
			wmNotify({
				message: 'Please specify a location before sorting by distance',
				type: 'danger'
			});
		} else {
			// set the current sort
			this.sortby.val(this.sortbyControl.val());
			this.resetPagination();
			this.loadData();
		}
	},

	getUserNameAndUserNumber ($row) {
		return {
			userName: `${$row.data('first_name')  } ${  $row.data('last_name')}`,
			userNumber: $row.data('userNumber')
		};
	},

	convertJSONtoCSV (objArray) {
		const array = typeof objArray !== 'object' ? JSON.parse(objArray) : objArray;
		let str = '';
		for (let i = 0; i < array.length; i++) {
			let line = '';
			for (let y = 0; y < array[i].length; y++) {
				const value = `${array[i][y]  }`;
				line += `"${  value  }",`;
			}
			line = line.slice(0, -1);
			str += `${line  }\r\n`;
		}
		return `data:text/csv;charset=utf-8,${encodeURIComponent(str)}`;
	},

	clearEverything () {
		this.resetFilters();
		this.resetPagination();
		this.loadData();
	},

	searchData () {
		this.resetPagination();
		this.loadData();
	},

	toggleSearchType (event) {
		const toggleStatus = $(event.currentTarget).is(':checked');
		if (toggleStatus === true) {
			this.searchType = 'vendors';
		} else {
			this.searchType = 'workers';
		}
		this.loadData();
		this.toggleFilters();
		Backbone.Events.trigger('searchTypeChanged', this.searchType);
	},

	toggleFilters () {
		/* if (this.searchType === 'vendors') {
			this.hideFilters();
			this.showFilters(['searchType', 'keywords', 'location', 'ratings']);
			$('#verifications').hide();
			$('#sortby-container').hide();
		} else {
			this.showFilters();
			$('#verifications').show();
			$('#sortby-container').show();
		}
		analytics.track('Search Toggle', { 'type': this.searchType });*/
	},

	showFilters () {
		/* var $filterForm = this.$('#filter_form');
		$filterForm.detach();
		if (typeof filterList !== 'undefined' && filterList.length > 0) {
			for (var i = 0; i < filterList.length; i++) {
				$filterForm.find('#' + filterList[i]).show();
			}
		} else {
			$filterForm.find('.sidebar-card').show();
		}

		$('.search-facets').append($filterForm);*/
	},

	hideFilters () {
		/*
		var $filterForm = this.$('#filter_form');
		$filterForm.detach();
		$filterForm.find('.sidebar-card').hide();
		$('.search-facets').append($filterForm);
		*/
	},

	changePageSize (event) {
		const pages = parseInt($(event.currentTarget).val(), 10);
		if (this.model.attributes.results_limit !== pages) {
			this.model.attributes.results_limit = pages;
			this.model.attributes.results_per_page = pages;
			this.clickFilter();
		}
	},

	snippetsInfo : {
		WORKER: {
			skillNames: {label: '<label>Skills:</label> ', max: 5, snippetArray: 'skillNames'},
			specialtyNames: {label: '<label>Skills:</label> ', max: 5, snippetArray: 'skillNames'},
			toolNames: {label: '<label>Skills:</label> ', max: 5, snippetArray: 'skillNames'},
			none: {label: ''}
		},
		VENDOR: {
			skillNames: {label: '<label>Skills:</label> ', max: 5, snippetArray: 'skillNames'},
			specialtyNames: {label: '<label>Skills:</label> ', max: 5, snippetArray: 'skillNames'},
			toolNames: {label: '<label>Skills:</label> ', max: 5, snippetArray: 'skillNames'},
			none: {label: ''}
		}
	},

	replaceWithSnippetData: function (user) {
		if (user.snippets) {
			if (user.snippets.jobFunctions) {
				user.job_title = user.snippets.jobFunctions.join(', ');
				user.jobTitleHighlighted = true;
			}

			if (user.snippets.licenseNames) {
				user.firstLicenses =  _.uniq(user.snippets.licenseNames).join(', ');
				user.licensesHighlighted = true;
			}

			if (user.snippets.certificationNames) {
				user.firstCertifications = _.uniq(user.snippets.certificationNames).join(', ');
				user.certificationNamesHighlighted = true;
			}

			if (user.snippets.insuranceNames) {
				user.firstInsurances = _.uniq(user.snippets.insuranceNames).join(', ');
				user.insurancesHighlighted = true;
			}
		}
	},

	summarizeSnippets: function (userType = 'WORKER', snippets = []) {
		userType = userType || 'WORKER'; //ES6 defaults doesn't work for nulls
		snippets = snippets || []; //ES6 defaults doesn't work for nulls

		let results = {
			jobFunctions: [],
			skillNames: []
		}

		let snippetsType = this.snippetsInfo[userType];

		Object.keys(snippets).forEach(key =>  {
			let info = snippetsType[key];
			if (info) {
				results[info.snippetArray].push(...snippets[key]);
			}
		});

		let summarizedSnippets = [];
		Object.keys(results).forEach(key =>  {
			let info = snippetsType[key];
			let result = _.uniq(results[key]);
			if (result.length > 0) {
				summarizedSnippets.push(info.label + result.slice(0, info.max).join(', '));
			}
		});

		if (snippets.length > 0 && summarizedSnippets.length === 0) {
			summarizedSnippets.push(snippetsType.none.label);
		}

		return summarizedSnippets;
	}
});

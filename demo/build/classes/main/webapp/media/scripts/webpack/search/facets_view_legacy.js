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
import CheckboxTemplate from '../funcs/templates/checkbox.hbs';
import wmSlider from '../funcs/wmSlider';
import jdenticon from '../dependencies/jquery.jdenticon';
import SearchTracker from './search_tracker';
import Application from '../core';

export default Backbone.View.extend({
	el: '.main.container',

	events: {
		'click .sidebar-card .wm-checkbox': 'clickFilter',
		'change [name="avatar"]': 'loadData',
		'click .wm-pagination--back': 'showPreviousPage',
		'click .wm-pagination--next': 'showNextPage',
		'click #keyword_go, #location_go': 'searchData',
		'click #clear_facets': 'clearEverything',
		'change #sortby-control': 'clickSort',
		'change [name="slider"]': 'sliderChange',
		'change #search-type input[type="radio"]': 'toggleSearchType',
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
		this.isInternalPricing = this.model.get('pricing_type') === 'INTERNAL';
		this.autoSelectedLanes = this.isInternalPricing ? [0, 1] : [];
		this.searchType = this.isInternalPricing ? 'workers' : options.searchType || 'workers';
		this.searchTracker = new SearchTracker();
		this.searchAjaxRequests = null;

		this.$(this.keywordInput).one('keypress', function () {
			$('#keyword_go').addClass('-active-search');
		});

		this.$(this.locationInput).one('keypress', function () {
			$('#location_go').addClass('-active-search');
		});

		if (!_.isEmpty(this.model.get('addressFilter'))) {
			this.$(this.locationInput).val(this.model.get('addressFilter'));
		}

		this.sortby.val('relevance');
		this.setLimitCountry($('[name="limit_country"]').val());

		this.searchTracker.trackSession('1.0', this.model.get('mode'));
		this.$(this.keywordInput).val(options.searchKeywords);
		this.loadData();
		this.toggleFilters();

		wmSelect({ selector: this.radius });
		wmSelect({ selector: this.countries }, {
			plugins: ['remove_button'],
			maxItems: null,
			placeholder: 'All Countries...',
			labelField: 'name',
			valueField: 'id',
			searchField: ['id','name'],
			onChange: _.bind(this.loadData, this)
		});
		wmSlider();

		_.each(this.model.attributes.existingWorkers, function (i) {
			this.model.attributes.existingWorkersLookup[i] = true;
		}, this);

		_.each(this.model.attributes.appliedWorkers, function (i) {
			this.model.attributes.appliedWorkersLookup[i] = true;
		}, this);

		_.each(this.model.attributes.declinedWorkers, function (i) {
			this.model.attributes.declinedWorkersLookup[i] = true;
		}, this);

		$(this.model.get('form_id')).sortable({
			animation: 300,
			draggable: '.sidebar-card',
			handle: '.sidebar-card--title',
			store: {
				get: _.bind(function () {
					return this.model.get('filterOrder') ? this.model.get('filterOrder').split('|') : [];
				}, this),
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
		$("#page_size").val(this.model.attributes.results_per_page);
	},

	setLimitCountry (country) {
		if (!country) {
			return;
		}
		this.restrictToCountries = [country];
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
		return Math.ceil(this.model.attributes.results_count / this.model.attributes.results_per_page) || 1;
	},

	getCurrentPage () {
		return Math.floor(this.model.attributes.results_start / this.model.attributes.results_per_page) + 1;
	},

	redrawPagination () {
		this.$('.wm-pagination').attr('data-min', this.getCurrentPage());
		this.$('.wm-pagination').attr('data-max', this.getTotalPages());
		this.redrawFilteredIndustries();
		this.$('.wm-pagination--back').prop('disabled', this.getCurrentPage() === 1);
		this.$('.wm-pagination--next').prop('disabled', this.getCurrentPage() === this.getTotalPages());
	},

	clickFilter (event) {
		this.handleSelectAll(event);
		this.resetPagination();
		this.loadData();
	},

	handleSelectAll (event) {
		let { currentTarget, value } = event;
		let container = $(currentTarget).closest('.sidebar-card')[0];
		let selectAllCheckbox = container.querySelector('.wm-checkbox--show-all');
		let allOtherCheckboxes = container.querySelectorAll('.wm-checkbox:not(.wm-checkbox--show-all)');
		let allOtherCheckboxesAreUnchecked = [...allOtherCheckboxes].every(checkbox => checkbox === currentTarget || !checkbox.classList.contains('is-checked'));

		if (!selectAllCheckbox) {
			return;
		}

		if (value === null && allOtherCheckboxesAreUnchecked) {
			selectAllCheckbox.MaterialCheckbox.check();
		} else if (Array.isArray(value)) {
			selectAllCheckbox.MaterialCheckbox.uncheck();
		} else {
			let isChecked = currentTarget.classList.contains('is-checked');
			let isCheckingSelectAll = currentTarget === selectAllCheckbox && !isChecked;
			let isUncheckingSelectAll = currentTarget === selectAllCheckbox && isChecked;
			let isCheckingOtherCheckbox = currentTarget !== selectAllCheckbox && !isChecked;
			let isUncheckingOtherCheckbox = currentTarget !== selectAllCheckbox && isChecked;
			let hasOptionsSelected = container.querySelectorAll('option').length;

			if (isUncheckingSelectAll) {
				event.preventDefault();
			} else if (isCheckingSelectAll) {
				[...allOtherCheckboxes].forEach(checkbox => checkbox.MaterialCheckbox.uncheck());
				let dropdownFilters = container.querySelector('.more-filters');
				dropdownFilters && dropdownFilters.selectize && dropdownFilters.selectize.clearOptions();
			} else if (isCheckingOtherCheckbox) {
				selectAllCheckbox.MaterialCheckbox.uncheck();
			} else if (isUncheckingOtherCheckbox && allOtherCheckboxesAreUnchecked && !hasOptionsSelected) {
				selectAllCheckbox.MaterialCheckbox.check();
			}
		}
	},

	loadData () {
		let startTime = new Date().getTime();
		let queryString = $('#filter_form').serialize() + '&' + $.param({
			start: this.model.get('results_start'),
			limit: this.model.get('results_limit')});

		if (this.searchAjaxRequests != null) {
			this.searchAjaxRequests.abort();
		}
		this.searchAjaxRequests = $.ajax({
			context: this,
			url: this.getSearchTypeUrl(),
			dataType: 'json',
			data: queryString,
			success (response) {
				this.restrictLanes = this.getRestrictedLanes(response.mode);
				this.handleLoadData(response);
				let searchVersion = ('search_version' in response) ? response.search_version : '1.0';
				this.searchTracker.trackSearch(searchVersion, queryString, response.request_id, new Date().getTime() - startTime, response.results_count);
				this.searchAjaxRequests = null;
			}
		});
	},

	getSearchTypeUrl () {
		return '/search/' + (this.searchType === 'vendors' ? 'vendors' : 'retrieve');
	},

	handleLoadData (data) {
		this.model.attributes.filters = data.filters;
		this.model.attributes.results_count = data.results_count;

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

	redrawFilteredIndustries () {
		var industries = [];

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

	stripProtocol (str) {
		if (str) {
			str = str.replace('http://', '//');
			str = str.replace('https://', '//');
		}

		return str;
	},

	redrawResults (data) {
		var lane4_only = true;

		this.searchResults.empty();
		if (!_.isEmpty(data)) {
			_.each(data, function (user) {
				user.ontime_reliability = Math.round(user.ontime_reliability)
				user.rating = Math.round(user.rating);
				user.deliverable_on_time_reliability = Math.round(user.deliverable_on_time_reliability);

				var isVendor = this.searchType === 'vendors',
					isInvitedVendor = _.contains(this.model.get('existingVendorsUids'), user.id),
					isDeclinedVendor = _.contains(this.model.get('declinedVendorsUids'), user.id);

				_.defaults(user, {
					isExistingWorker: this.model.get('existingWorkersLookup')[user.id],
					isDeclinedWorker: this.model.get('declinedWorkersLookup')[user.id],
					isAppliedWorker: this.model.get('appliedWorkersLookup')[user.id],
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
					isVendor,
					disableActions: false,
					user,
					publicWorkers: false,
					lastWork: false,
					assignToFirstWorker: this.model.get('assignToFirstWorker'),
					eligibility: '',
					avatarAssetUri: this.stripProtocol(user.avatar_asset_uri),
					orUserNumberOrCompanyNumber: user.userNumber || user.companyNumber,
					isCheckboxBlocked: this.model.get('existingWorkersLookup')[user.id] || isInvitedVendor || isDeclinedVendor,
					isUsa: user.country === 'United States' || user.country === 'USA',
					isValidEmail: user.lane === (0,1,2,3),
					firstGroup: (user.groups !== null && typeof user.groups !== 'undefined') ? user.groups[0] : '',
					firstCompanyAssessment: (user.company_assessments !== null && typeof user.company_assessments !== 'undefined') ? user.company_assessments[0] : '',
					firstCertifications: (user.certifications !== null && typeof user.certifications !== 'undefined') ? user.certifications[0] : '',
					firstLicenses: (user.licenses !== null && typeof user.licenses !== 'undefined') ? user.licenses[0] : ''
				})

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

	buildChecklist (data, name, label, div, url) {
		var checkedItems = this.$(div).find('.filter_items');

		if (checkedItems) {
			var checkedFilterIds = this.previouslyCheckedItems(checkedItems),
				moreContainer = $(div).find('.select-container'),
				moreSelect = moreContainer.find('.more-filters');

			checkedItems.empty();

			if (data) {
				if (data.length > 10) {
					if (!$(div).find('.selectized').length) {
						this.loadUrl = url;
						wmSelect({ selector: moreSelect }, {
							plugins: ['remove_button'],
							maxItems: null,
							placeholder: 'Select or type for more...',
							labelField: 'name',
							valueField: 'id',
							searchField: ['id', 'name'],
							onChange: (value) => {
								let currentTarget = moreContainer[0];
								return this.clickFilter({ currentTarget, value });
							},
							render: {
								option (data, escape) {
									return '<div class="option">' + escape(_.unescape(data.name)) + '</div>';
								},
								item(data, escape) {
									return '<div>' + escape(_.unescape(data.name)) + '</div>';
								}
							},
							load: (query, callback) => {
								if (this.loadUrl) {
									if (!query.length) {
										return callback();
									}
									const searchData = { term: query };
									$.ajax({
										url: this.loadUrl,
										traditional: true,
										type: 'GET',
										dataType: 'json',
										data: searchData,
										error: callback,
										success: callback
									});
								}
							}
						});
					}
					moreContainer.show();
				} else {
					moreContainer.hide();
				}

				_.each(data, function (option, i) {
					if (i < 10) {
						checkedItems.append(CheckboxTemplate({
							name,
							id: name + '_' + i,
							value: option.id,
							text: label[option.id],
							badge: option.count
						}));
					} else {
						moreSelect[0].selectize.addOption(option);
						if (option.filter_on) {
							moreSelect[0].selectize.addItem(option.id, true);
						}

					}
				});
			}

			this.recheckInputsWithValues(checkedItems, checkedFilterIds);
		}
	},

	redrawFilters (data) {
		var $filters;

		if (!data) {
			return;
		}

		if (data.keywords) {
			this.$(this.keywordInput).val(data.keywords);
		}

		if (data.address) {
			this.$(this.locationInput).val(data.address.address);
			$('#radius').val(data.address.radius);
		}

		this.redrawLaneFilters(data);
		this.buildChecklist(data.groups, 'group', this.model.attributes.filters.labels.groups, this.groups);
		this.buildChecklist(data.licenses, 'license', this.model.attributes.filters.labels.licenses, this.licenses);
		this.buildChecklist(data.industries, 'industry', this.model.attributes.filters.labels.industries, this.industries);
		this.buildChecklist(data.assessments, 'assessment', this.model.attributes.filters.labels.assessments, this.assessments);
		this.buildChecklist(data.sharedgroups, 'sharedgroup', this.model.attributes.filters.labels.sharedgroups, this.sharedGroups);
		this.buildChecklist(data.companytypes, 'companytypes', this.model.attributes.filters.labels.companytypes, this.companytypes);
		this.buildChecklist(data.verifications, 'verification', this.model.attributes.filters.labels.verifications, this.verifications);
		this.buildChecklist(data.certifications, 'certification', this.model.attributes.filters.labels.certifications, this.certifications, '/search/suggest_certifications.json');

		// what is this?
		if (this.model.attributes.mode === this.searchModes.attributes.groupDetail) {
			this.$('#verification_1').closest('li').remove();
		}

		if (data.countries) {
			var $selectize =  $(this.countries)[0].selectize;
			//the following 5 lines of code basically run clearOptions() silently
			$selectize.loadedSearches = {};
			$selectize.userOptions = {};
			$selectize.renderCache = {};
			$selectize.options = $selectize.sifter.items = {};
			$selectize.lastQuery = null;

			_.each(data.countries, function (country) {
				$selectize.addOption(country);
				if (country.filter_on) {
					$selectize.addItem(country.id, true);
				}
			});

			if (this.restrictToCountries.length) {
				$selectize.disable();
				if ('USA' === _.first(this.restrictToCountries)) {
					$('#cross_border_tooltip').show();
				} else if ('CAN' === _.first(this.restrictToCountries)) {
					$('#cross_border_tooltip').show();
				}
			} else {
				$selectize.enable();
			}
		}

		if (!_.isEmpty(data.sharedgroups)) {
			$(this.sharedGroups).show();
			// Add tooltip to display company owner
			_.each($(this.sharedGroups).find('li'), function (filter, i) {
				var companyName = data.sharedgroups[i].group_owner;
				$(filter).attr({'aria-label': 'This group is shared from ' + companyName});
			});
		}


		// Assessment Status Filters for Test Invite Page
		if (this.model.attributes.mode === this.searchModes.attributes.assessment) {
			$filters = $('#assessmentStatus').find('.filter_items');
			if ($filters.length > 0) {
				this.fillStatusRow($filters, '#notinvitedassessment', data.notinvitedassessments[0]);
				this.fillStatusRow($filters, '#invitedassessment', data.invitedassessments[0]);
				this.fillStatusRow($filters, '#passedassessment', data.passedassessments[0]);
				this.fillStatusRow($filters, '#failedtest', data.failedtests[0]);
			}
		}

		// Group Status Filters for Group Members Page
		if (this.model.attributes.mode === this.searchModes.attributes.groupDetail) {
			$filters = $('#groupStatus').find('.filter_items');
			$('#member').attr('data-badge', data.member[0].count);
			$('#memberoverride').attr('data-badge', data.memberoverride[0].count);
			$('#pending').attr('data-badge', data.pending[0].count);
			$('#pendingoverride').attr('data-badge', data.pendingoverride[0].count);
			$('#invited').attr('data-badge', data.invited[0].count);
			$('#declined').attr('data-badge', data.declined[0].count);

			if ($filters.length) {
				// Arguments
				let argList = [
					[$filters, '#member', data.member[0]],
					[$filters, '#memberoverride', data.memberoverride[0]],
					[$filters, '#pending', data.pending[0]],
					[$filters, '#pendingoverride', data.pendingoverride[0]],
					[$filters, '#invited', data.invited[0]],
					[$filters, '#declined', data.declined[0]]
				];

				// Apply arguments (inc keeps track off how many filters are marked as on)
				let inc = argList.reduce((memo, args) => memo + this.fillStatusRow(...args), 0);

				// If all filters are on, uncheck all filters and check select-all
				if (inc === $filters.find('input[type="checkbox"]').length) {
					$filters.parent().find('.show-all').prop('checked', true);
					$filters.find('input[type="checkbox"]').prop('checked', false);
				}
			}
		}

		componentHandler.upgradeElements(document.querySelectorAll('.filter_items .mdl-checkbox'));
	},

	redrawLaneFilters (data) {
		this.buildChecklist(data.lanes, 'lane', this.laneType.attributes, this.lanes);

		if (this.restrictLanes.length) {
			this.$('.lane-restriction').show();
		} else {
			this.$('.lane-restriction').hide();
			$(this.lanes).find('input[type="checkbox"]').prop('disabled', false);
		}

		if (data.lanes) {
			_.each(this.restrictLanes, function (lane) {
				var $input = $('#lanes').find('input[value="' +  lane + '"]');
				$input.prop('disabled', true);
				$input.parent().addClass('-disabled');
			});

			var checkedItems = $(this.lanes).find('.filter_items');
			var checkedFilterIds = this.previouslyCheckedItems(checkedItems);
			if (checkedFilterIds.length == 0) {
				_.each(this.autoSelectedLanes, function (lane) {
					$('#lanes').find('input[value="' + lane + '"]').prop('checked', true);
				});
			}
		}
	},

	fillStatusRow ($filters, filter_selector, data) {
		if (typeof data !== 'undefined') {
			let label = $filters.find(filter_selector);
			label.attr('data-badge', data.count);
			if (data.filter_on) {
				label.find('[type="checkbox"]').prop('checked', true);
				return 1;
			}
		}
		return 0;
	},

	resetFilters () {
		var exclusions = [':button', ':submit', ':reset', '[type="hidden"]', '.show-all', '[name*="_showall"]', '[name="searchType"]'],
			selectAllExclusions = [],
			$form = $('#' + this.model.get('form_id'));

		// Don't clear out certain filters when in assignment mode.
		if (this.model.get('mode') === this.searchModes.get('assignment')) {
			exclusions.push('#address');
			exclusions.push('#radius');
			$('input[name="boost_industry"]').val('');
		} else {
			this.$(this.countries)[0].selectize.clearOptions();
			this.$(this.locationInput).val('');
		}

		this.$(this.keywordInput).val('');
		this.$(this.radius)[0].selectize.setValue('60');

		$form.find(':input').not(exclusions.join(','))
			.prop('disabled', false)
			.prop('checked', false)
			.prop('selected', false);

		_.each([this.groups, this.sharedGroups, this.licenses, this.industries], function (div) {
			if ($(div).find('.selectized').length) {
				$(div).find('select')[0].selectize.clearOptions();
			}
		});

		this.$(this.ratings).find('input').val('0');
		this.$(this.ratings).find('.wm-slider--text').text('0');
		$('.wm-slider--progress-bar').css('width', 0);

		// Groups and certifications should clear their hidden inputs too
		$form.find('#shared-groups :input').prop('checked', false);

		$form.find('.show-all').add('[name*="_showall"]').not(selectAllExclusions.join(',')).prop('checked', true);
	},

	clickSort () {
		if (this.$(this.locationInput).val() === '' && this.sortbyControl.val() === 'distance_asc') {
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

	getFacetCount (selector) {
		return +($(selector).siblings().find('.facet-count').text());
	},

	setFacetCount (selector, value) {
		if (value < 0) { value = 0; }
		$(selector).siblings().find('.facet-count').text(value);
	},

	getElementValues ($parent) {
		return _.map($parent, function (child) {
			return $(child).val();
		});
	},

	getUserNameAndUserNumber ($row) {
		return {
			userName: $row.data('first_name') + ' ' + $row.data('last_name'),
			userNumber: $row.data('userNumber')
		};
	},

	previouslyCheckedItems (container) {
		return _.union(this.getElementValues(container.find('.wm-checkbox :checked')), this.getElementValues(container.parent().find('option')))
	},

	recheckInputsWithValues ($context, values) {
		_.each(values, function (value) {
			$context.find('input[value=' + value + ']').prop('checked', true);
		});
	},

	convertJSONtoCSV (objArray) {
		var array = typeof objArray !== 'object' ? JSON.parse(objArray) : objArray;
		var str = '';
		for (var i = 0; i < array.length; i++) {
			var line = '';
			for (var y = 0; y < array[i].length; y++) {
				var value = array[i][y] + '';
				line += '"' + value + '",';
			}
			line = line.slice(0, -1);
			str += line + '\r\n';
		}
		return 'data:text/csv;charset=utf-8,' + encodeURIComponent(str);
	},

	clearEverything () {
		this.resetFilters();
		this.resetPagination();
		this.loadData();
	},

	sliderChange (event) {
		var $target = $(event.currentTarget),
			percentage = $target.val(),
			id = $target.attr('id');

		$('.sliders').find('input[name="' + id + '"]').val(percentage);
		this.resetPagination();
		this.loadData();
	},

	searchData (event) {
		event.preventDefault();
		this.resetPagination();
		this.loadData();
	},

	toggleSearchType (event) {
		this.searchType = $(event.currentTarget).val();
		this.loadData();
		this.toggleFilters();
		Backbone.Events.trigger('searchTypeChanged', this.searchType);
	},

	toggleFilters () {
		if (this.searchType === 'vendors') {
			this.hideFilters();
			this.showFilters(['searchType', 'keywords', 'location', 'ratings']);
			$('#verifications').hide();
			$('#sortby-container').hide();
		} else {
			this.showFilters();
			$('#verifications').show();
			$('#sortby-container').show();
		}
		analytics.track('Worker Search', {
			action: 'toggle',
			type: this.searchType
		});
	},

	showFilters (filterList) {
		var $filterForm = this.$('#filter_form');
		$filterForm.detach();
		if (typeof filterList !== 'undefined' && filterList.length > 0) {
			for (var i = 0; i < filterList.length; i++) {
				$filterForm.find('#' + filterList[i]).show();
			}
		} else {
			$filterForm.find('.sidebar-card').show();
		}

		$('.search-facets').append($filterForm);
	},

	hideFilters () {
		var $filterForm = this.$('#filter_form');
		$filterForm.detach();
		$filterForm.find('.sidebar-card').hide();
		$('.search-facets').append($filterForm);
	},

	changePageSize(event){
		var pages = parseInt($(event.currentTarget).val());
		if (this.model.attributes.results_limit != pages) {
			this.model.attributes.results_limit = pages;
			this.model.attributes.results_per_page = pages;
			this.resetPagination();
			this.loadData();
		}
	}
});

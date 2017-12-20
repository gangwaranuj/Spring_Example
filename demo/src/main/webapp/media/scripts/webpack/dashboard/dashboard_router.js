import Application from '../core';
import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import MainView from './dashboard_view';
import CalendarView from './calendar_view';
import AssignmentListView from './assignment_list_view';
import BuyerStatusesView from './buyer_statuses_view';
import SellerStatusesView from './seller_statuses_view';
import BuyerSubStatusesView from './buyer_substatuses_view';
import MainModel from './dashboard_model';
import AssignmentListCollection from './assignment_list_collection';
import BuyerStatusesCollections from './buyer_statuses_collection';
import SellerStatusesCollections from './seller_statuses_collection';
import BuyerSubStatusesCollections from './buyer_substatuses_collection';
import PaginationSelectionView from '../pagination/pagination-selection_view';
import PaginationView from '../pagination/pagination_view';
import ajaxSendInit from '../funcs/ajaxSendInit';

export default Backbone.Router.extend({
	routes: {
		'': 'index', // Main entry
		'status/:status/:type': 'selectStatus',
		'substatus/:substatus/:type': 'selectSubstatus',
		'recurrence/:recurrence/:type': 'selectRecurrence',
		'substatus/:substatus/status/:status/:type': 'selectStatusAndSubstatus'
	},

	// Initialize page.
	initialize () {
		const $includeTime = $('#include_time');

		if ($includeTime !== null && $includeTime.prop('checked')) {
			$('#include_time').removeAttr('checked');
		}
		const options = this.options = {
			model: [],
			defaultFrom: $('#schedule_from').val(),
			defaultTo: $('#schedule_through').val(),
			defaultOwner: Application.Features.defaultOwner,
			hasAvatar: Application.Features.hasAvatar,
			isNotAuthorizedForPayment: Application.Features.isNotAuthorizedForPayment,
			isBuyer: Application.Features.isBuyer,
			isWorkerCompany: Application.Features.isWorkerCompany,
			hidePricing: Application.Features.hidePricing
		};
		this.load_count = 0;
		this.current_view = 'list';
		this.currentCalendarView = 'month';
		this.CALENDAR_PAGE_SIZE = 10000;
		if ($includeTime !== null) {
			this.include_time = $('#include_time').val();
		}
		const today = new Date();
		let calendar_schedule_from = new Date();
		let calendar_schedule_through = new Date();
		calendar_schedule_from.setDate(today.getDate() - 30);
		calendar_schedule_through.setDate(today.getDate() + 30);
		calendar_schedule_from = `${calendar_schedule_from.getMonth() + 1}/${calendar_schedule_from.getDate()}/${calendar_schedule_from.getFullYear()}`;
		calendar_schedule_through = `${calendar_schedule_through.getMonth() + 1}/${calendar_schedule_through.getDate()}/${calendar_schedule_through.getFullYear()}`;
		this.calendar_schedule_from = calendar_schedule_from;
		this.calendar_schedule_through = calendar_schedule_through;

		if ($('#time')) {
			this.calendar_time_from = $('#time_from').val();
			this.calendar_time_through = $('#time_through').val();
		}
		this.calendarYear = today.getFullYear();
		this.calendarMonth = today.getMonth();
		this.calendarDay = today.getDate();
		if ($includeTime !== null) {
			$includeTime.removeAttr('checked');
		}
		this.assignment_list = new AssignmentListCollection();
		// Initialize the buyer statuses display and build view.
		this.buyer_statuses = new BuyerStatusesCollections();
		// Initialize the seller statuses and build view.
		this.seller_statuses = new SellerStatusesCollections();
		// Sub Statuses
		this.buyer_substatuses = new BuyerSubStatusesCollections();
		// Initializing default sorting.
		this.default_sorts = {
			created_date: 'asc',
			scheduled_date: 'asc',
			sent_date: 'asc',
			completed_date: 'asc',
			approved_date: 'desc',
			paid_date: 'asc'
		};

		// ensure cookie values are restored before initializing everything else
		this.getFilters(() => {
			if (this.filters) {
				this.restoreFilters(this.filters);
			}

			if (this.current_view === 'list') {
				this.page_size = parseInt($('#assignment_list_size').val(), 10);
			}

			this.calendarView = new CalendarView({
				collection: this.assignment_list,
				variables: options
			});

			// Main view - attach necessary handlers, etc.
			this.main_view = new MainView(
				_.extend(this.options, {
					pagination_selection: this.pagination_selection,
					dir: this.dir
				})
			);

			// Pagination view.
			this.resetPagination();

			// Initialize filters and listen to filter events.
			this.main_view.bind('filter:apply_filters', () => { this.selectFilter(); });
			this.main_view.bind('filter:clear_filters', () => { this.selectFilter(); });
			this.main_view.bind('filter:date_filter', () => { this.selectFilter(); });
			this.main_view.bind('filter:date_sub_filter', () => { this.selectFilter(); });
			this.main_view.bind('filter:client_company_list', () => { this.selectFilter(); });
			this.main_view.bind('filter:project-dropdown', () => { this.selectFilter(); });
			this.main_view.bind('filter:internal-owner-dropdown', () => { this.selectFilter(); });
			this.main_view.bind('filter:resources-dropdown', () => { this.selectFilter(); });
			this.main_view.bind('filter:vendors-dropdown', () => { this.selectFilter(); });
			this.main_view.bind('filter:bundles-dropdown', () => { this.selectFilter(); });
			this.main_view.bind('filter:assigned_to_me', () => { this.selectFilter(); });
			this.main_view.bind('filter:dispatched_by_me', () => { this.selectFilter(); });
			this.main_view.bind('filter:following', () => { this.selectFilter(); });
			this.main_view.bind('sorting:assignmentSortAsc', () => { this.sortAscending($('#assignment_list_sorting').val()); });
			this.main_view.bind('sorting:assignmentSortDsc', () => { this.sortDescending($('#assignment_list_sorting').val()); });
			this.main_view.bind('refresh:refreshList', () => { this.getData(); });
			this.main_view.bind('refresh:resizeList', () => { this.resizePage($('#assignment_list_size').val()); });
			this.main_view.bind('action:showCalendar', () => { this.showCalendar(); });
			this.main_view.bind('action:showList', () => { this.showList(); });
			this.main_view.bind('action:showMonthView', () => { this.showMonthView(); });
			this.main_view.bind('action:showWeekView', () => { this.showWeekView(); });
			this.main_view.bind('action:showDayView', () => { this.showDayView(); });
			this.main_view.bind('action:saveCurrentDate', () => { this.saveCurrentDate(); });
			this.main_view.bind('action:apply_filters', () => { this.selectFilter(); });
		});

		Backbone.Events.on('getDashboardData', this.getData, this);

		Backbone.history.start();
	},

	getFilters (callback) {
		$.ajax({
			context: this,
			url: '/assignments/get_filters',
			type: 'GET',
			dataType: 'json'
		}).done(function (data) {
			const filters = {};
			this.page = data.navigation;
			$.each(data, (property, value) => {
				if (filters[property] === undefined) {
					filters[property] = [];
				}

				filters[property] = value.split(',');
			});

			this.filters = filters;
			if (callback) { callback(); }
		}).fail(() => {
			throw 'There was an error fetching dashboard filters.';
		});
	},

	index () {
		if (this.page) {
			this.navigate(this.page, true);
		} else {
			this.getFilters(() => {
				if (this.page) {
					this.navigate(this.page, true);
				} else {
					this.selectStatus('all', 'managing');
				}
			});
		}
	},

	getData () {
		this.savePage();

		this.sort_column = $('#assignment_list_sorting').val();
		this.dir = _.last($('[id^=assignment_list_sorting_].toggle_selected').prop('id').split('_'));

		if (this.current_view === 'calendar') {
			this.page_size = this.CALENDAR_PAGE_SIZE;
		}

		// Setup some variables
		const options = {
			status: this.status,
			sub_status: this.sub_status,
			type: this.type,
			start: this.pagination.getStart(),
			limit: this.pagination.getLimit(),
			sort: this.sort_column,
			dir: this.dir,
			page_size: this.page_size,
			include_counts: (this.load_count <= 1),
			current_view: this.current_view,
			currentCalendarView: this.currentCalendarView,
			calendarYear: this.calendarYear,
			calendarMonth: this.calendarMonth,
			calendarDay: this.calendarDay,
			calendar_schedule_from: this.calendar_schedule_from,
			calendar_time_from: this.calendar_time_from,
			calendar_schedule_through: this.calendar_schedule_through,
			calendar_time_through: this.calendar_time_through,
			isNotAuthorizedForPayment: this.options.isNotAuthorizedForPayment,
			userTimezone: Application.Features.userTimezone,
			isBuyer: this.options.isBuyer,
			recurrence: this.recurrence
		};

		const assignmentsDashboard = new MainModel(options);
		assignmentsDashboard.fetch().then((dashboardData) => {
			// If the aren't any results in the current dashboard,
			//   try again. This time go straight to all.
			if (!dashboardData.data || _.isEmpty(dashboardData.data)) {
				this.navigate('status/all/managing', { trigger: true, replace: true });
			}

			this.assignment_list.reset(assignmentsDashboard.get('data'));
			if (options.current_view === 'list') {
				this.list_view = new AssignmentListView({
					collection: this.assignment_list,
					variables: options

				});
			} else if (options.current_view === 'calendar') {
				this.calendarView.render({
					currentCalendarView: options.currentCalendarView,
					calendarYear: options.calendarYear,
					calendarMonth: options.calendarMonth,
					calendarDay: options.calendarDay
				});
			}

			const counts = assignmentsDashboard.get('counts');
			const substatuses = assignmentsDashboard.get('substatuses');
			const showBulkOps = assignmentsDashboard.get('show_bulk_ops');

			this.buyer_statuses.reset(counts.buyer);
			new BuyerStatusesView({ statuses: this.buyer_statuses, sub_menu: counts.sub_menu.buyer, activeStatus: this.status, activeSubStatus: this.sub_status });

			this.seller_statuses.reset(counts.resource);
			new SellerStatusesView({ statuses: this.seller_statuses, sub_menu: counts.sub_menu.resource });

			this.buyer_substatuses.reset(substatuses.buyer);
			const buyer_subs = new BuyerSubStatusesView();
			buyer_subs.render({ statuses: this.buyer_substatuses });

			// Render paging.
			this.pagination.setTotal(assignmentsDashboard.get('results_count'));
			this.pagination.render();
			if (this.options.isBuyer) {
				this.pagination_selection.clearFullSelectAll();
			}
			this.pagination_selection.setTotalCount(this.pagination.getTotal());
			this.pagination_selection.setVisibleCount(this.pagination.getLimit());
			this.pagination_selection.setResults(assignmentsDashboard.get('result_ids'));
			this.pagination_selection.render();

			this.main_view.pagination_selection = this.pagination_selection;
			this.main_view.options = options;
			this.main_view.render();
			this.main_view.showBulkActions();
			if (showBulkOps) {
				this.main_view.showPaymentActions();
			}

			// Update the menu selections.
			this.updateMenuSelection();

			this.load_count++;

			this.main_view.toggleBulkDropdown();
		});

		$('#filterless').val(false);
	},

	resetPagination () {
		this.page_size = $('#assignment_list_size').val();
		this.pagination = new PaginationView({
			el: '.assignments-content',
			limit: this.page_size
		});
		this.pagination_selection = new PaginationSelectionView({
			dashboardRouter: this,
			isBuyer: this.options.isBuyer
		});
		this.pagination.resetStart();
		this.pagination_selection.reset();

		// Listen for pagination events.
		this.pagination.bind('pagination:next', () => { this.changePage(); scroll(0, 0); });
		this.pagination.bind('pagination:previous', () => { this.changePage(); scroll(0, 0); });
	},

	// New status selected. Refresh the views.
	selectStatus (status, type) {
		// Store status.
		this.status = status;
		this.sub_status = '';

		this.getFilters(() => {
			// Quickly validate type before storing, it can only be 1 of 2 possibilities.
			if (type === 'working') {
				this.type = type;
			} else {
				this.type = 'managing';
			}

			if (this.filters) {
				this.restoreFilters(this.filters);
			}

			// Reset the paging when filtering.
			this.resetPagination();

			// Refresh the view data.
			this.getData();
		});
	},

	selectSubstatus (sub_status, type) {
		this.sub_status = sub_status;
		this.status = '';

		this.getFilters(() => {
			// Quickly validate type before storing, it can only be 1 of 2 possibilities.
			if (type === 'working') {
				this.type = type;
			} else {
				this.type = 'managing';
			}

			if (this.filters) {
				this.restoreFilters(this.filters);
			}

			// Reset the paging when filtering.
			this.resetPagination();

			// Refresh the view data.
			this.getData();
		});
	},

	selectRecurrence (recurrence, type) {
		this.recurrence = recurrence;

		this.getFilters(() => {
			// Quickly validate type before storing, it can only be 1 of 2 possibilities.
			if (type === 'working') {
				this.type = type;
			} else {
				this.type = 'managing';
			}

			if (this.filters) {
				this.restoreFilters(this.filters);
			}

			// Reset the paging when filtering.
			this.resetPagination();

			// Refresh the view data.
			this.getData();
		});
	},

	selectStatusAndSubstatus (sub_status, status, type) {
		this.sub_status = sub_status;
		this.status = status;

		this.getFilters(() => {
			// Quickly validate type before storing, it can only be 1 of 2 possibilities.
			if (type === 'working') {
				this.type = type;
			} else {
				this.type = 'managing';
			}

			if (this.filters) {
				this.restoreFilters(this.filters);
			}

			// Reset the paging when filtering.
			this.resetPagination();

			// Refresh the view data.
			this.getData();
		});
	},

	sortAscending (column) {
		if (typeof column === 'undefined') {
			column = 'scheduled_time';
		}
		this.sort(column, 'asc');
	},

	sortDescending (column) {
		if (typeof column === 'undefined') {
			column = 'scheduled_time';
		}
		this.sort(column, 'desc');
	},

	sort (column, order) {
		this.sort_column = column;
		this.dir = order;
		this.getData();
	},

	selectFilter () {
		// Reset sidebar counts.
		this.load_count = 0;

		// Reset the paging when filtering.
		this.resetPagination();

		this.getData();
	},

	changePage () {
		this.getData();
	},

	refreshPage () {
		this.getData();
	},

	resizePage (ps) {
		if (ps) {
			this.page_size = parseInt(ps, 10);
			this.pagination.setLimit(this.page_size);
			this.getData();
		}
	},

	showCalendar () {
		this.current_view = 'calendar';
		this.getData();
		$('#assignment_results_container').hide();
		$('.bracket').hide();
		$('#dashboard_filter_title').hide();
		$('#custom-range-dates').hide();
		$('#calendar').show();
		if (!$('#advanced-filters').is(':visible')) {
			$('.advanced-filters-toggle').click();
		}
		if ($('#include_time') && $('#include_time').val()) {
			$('#include_time').prop('checked', false);
		}
		$('#time').hide();
		$('#workDateRangeTitle').hide();
	},

	showList () {
		this.current_view = 'list';
		this.page_size = parseInt($('#assignment_list_size').val(), 10);
		this.getData();
		$('#assignment_results_container').show();
		$('.bracket').show();
		$('#dashboard_filter_title').show();
		$('#custom-range-dates').show();
		$('#calendar').hide();
		if ($('#include_time') && $('#include_time').val()) {
			$('#include_time').attr('checked', false);
			$('#time').show();
			$('#time_from').hide();
			$('#time_through').hide();
		}

		$('#workDateRangeTitle').show();
	},

	showMonthView () {
		this.currentCalendarView = 'month';
		this.saveCurrentDate();
	},

	showWeekView () {
		this.currentCalendarView = 'agendaWeek';
		this.saveCurrentDate();
	},

	showDayView () {
		this.currentCalendarView = 'agendaDay';
		this.saveCurrentDate();
	},

	saveCurrentDate () {
		const currentDate = $('#calendar').fullCalendar('getDate');
		this.calendarYear = currentDate.getFullYear();
		this.calendarMonth = currentDate.getMonth();
		this.calendarDay = currentDate.getDate();
		this.generateCalendarScheduleThrough();
		this.savePage();
		this.getData();
	},

	generateCalendarScheduleThrough () {
		const currentDate = $('#calendar').fullCalendar('getDate');
		const calendar_schedule_from = new Date(currentDate);
		const calendar_schedule_through = new Date(currentDate);

		if (this.currentCalendarView === 'month') {
			calendar_schedule_from.setDate(1);                                            // Set to first day of current month
			calendar_schedule_through.setMonth(calendar_schedule_from.getMonth() + 1);
			calendar_schedule_through.setDate(calendar_schedule_from.getDate() - 1);      // Set to last day of current month
		} else if (this.currentCalendarView === 'agendaWeek') {
			const day = currentDate.getDay();
			calendar_schedule_from.setDate(currentDate.getDate() - day);                  // Set to Sunday
			calendar_schedule_through.setDate(currentDate.getDate() - day + 6);           // Set to Saturday.
		}

		this.calendar_schedule_from = `${calendar_schedule_from.getMonth() + 1}/${calendar_schedule_from.getDate()}/${calendar_schedule_from.getFullYear()}`;
		this.calendar_schedule_through = `${calendar_schedule_through.getMonth() + 1}/${calendar_schedule_through.getDate()}/${calendar_schedule_through.getFullYear()}`;
	},


	updateMenuSelection () {
		// Set selected menu item active.
		$('#assignment_statuses_container li.active').removeClass('active');
		this.setMenuStatusSelectionActive();
		if (this.status === '') {
			this.setMenuSubStatusSelectionActive();
		}
	},

	setMenuStatusSelectionActive () {
		// Deactivate all other statuses.
		$('#assignment_statuses_container .submenu').hide();
		$('#assignment_statuses_container').find('ul.status_list li').removeClass('active');

		if (this.status !== '') {
			$(`div.submenu_${this.status}`).show();

			const tmp = `#status_${this.status}_${this.type}`;
			$(tmp).addClass('active');
		}
	},

	setMenuSubStatusSelectionActive () {
		const tmp = `#substatus_${this.sub_status}_${this.type}`;
		$(tmp).addClass('active');
	},

	actionApprovePay () {
		this.refreshPage();
	},

	savePage () {
		let $form = $('#list_filters,#form.dashboard-sort'),
			serializedForm = $form.serializeArray(),
			filters = _.object(_.pluck(serializedForm, 'name'), _.pluck(serializedForm, 'value')),
			projectDropdown = $('#project-dropdown'),
			clientCompanyDropdown = $('#client_company'),
			internalOwnersDropdown = $('#internal-owner-dropdown'),
			workersDropdown = $('#resources-dropdown'),
			bundlesDropdown = $('#bundles-dropdown'),
			isWorkersDropdownReady = workersDropdown.length && workersDropdown[0].selectize,
			isBundlesDropdownReady = bundlesDropdown.length && bundlesDropdown[0].selectize;

		if (this.options.isBuyer) {
			if (projectDropdown.length) {
				if (projectDropdown[0].selectize) {
					filters.projects = projectDropdown[0].selectize.getValue().join(',');
				}
			}
			if (clientCompanyDropdown.length) {
				if (clientCompanyDropdown[0].selectize) {
					filters.client_companies = clientCompanyDropdown[0].selectize.getValue().join(',');
				}
			}
		}

		if (internalOwnersDropdown.length) {
			if (internalOwnersDropdown[0].selectize) {
				filters.internal_owners = internalOwnersDropdown[0].selectize.getValue().join(',');
			}
		}
		filters.dir = _.last($('[id^=assignment_list_sorting_].toggle_selected').prop('id').split('_'));
		filters.current_view = this.current_view;
		filters.currentCalendarView = this.currentCalendarView;
		filters.calendarYear = this.calendarYear;
		filters.calendarMonth = this.calendarMonth;
		filters.calendarDay = this.calendarDay;
		filters.calendar_schedule_from = this.calendar_schedule_from;
		filters.calendar_schedule_through = this.calendar_schedule_through;

		delete filters.keyword;
		delete filters.filterless;


		filters.assigned_resource_names = [];
		filters.assigned_resources = [];

		if (isWorkersDropdownReady) {
			const workers = $.map(workersDropdown[0].selectize.items, (value) => {
				return workersDropdown[0].selectize.options[value];
			});

			_.each(workers, (worker) => {
				filters.assigned_resource_names.push(worker.name);
				filters.assigned_resources.push(worker.id);
			});

			filters.assigned_resource_names = filters.assigned_resource_names.join(',');
			filters.assigned_resources = filters.assigned_resources.join(',');
			if (filters.assigned_resource_names.length < 1) { delete filters.assigned_resource_names; }
			if (filters.assigned_resources.length < 1) { delete filters.assigned_resources; }
		}

		if (isBundlesDropdownReady) {
			filters.bundles_names = [];
			filters.bundles = [];

			const bundles = $.map(bundlesDropdown[0].selectize.items, (value) => {
				return bundlesDropdown[0].selectize.options[value];
			});

			_.each(bundles, (bundle) => {
				filters.bundles_names.push(bundle.name);
				filters.bundles.push(bundle.id);
			});

			filters.bundles_names = filters.bundles_names.join(',');
			filters.bundles = filters.bundles.join(',');
			if (filters.bundles_names.length < 1) { delete filters.bundles_names; }
			if (filters.bundles.length < 1) { delete filters.bundles; }
		}


		filters.navigation = window.location.hash;
		ajaxSendInit();
		$.ajax({
			url: '/assignments/set_filters',
			type: 'POST',
			dataType: 'json',
			data: filters
		});
	},

	stringifyParamsFor (key, filters) {
		if (filters[key]) {
			if (typeof filters[key] !== 'string') {
				filters[key] = filters[key].join(',');
			}
		}
	},

	updateOptionsFromFilters (filter_map) {
		if (filter_map.hasOwnProperty('workMilestone')) {
			$('#date_filter').val(filter_map.workMilestone);
		}

		if (filter_map.hasOwnProperty('workDateRange')) {
			$('#date_sub_filter').val(filter_map.workDateRange);
		}

		if (filter_map.hasOwnProperty('schedule_from')) {
			$('#schedule_from').val(filter_map.schedule_from);
		}

		if (filter_map.hasOwnProperty('schedule_through')) {
			$('#schedule_through').val(filter_map.schedule_through);
		}

		if (filter_map.hasOwnProperty('pageSize')) {
			$('#assignment_list_size').val(parseInt(filter_map.pageSize, 10));
		}

		if (filter_map.hasOwnProperty('assignment_list_sorting')) {
			$('#assignment_list_sorting').val(filter_map.assignment_list_sorting);
		}

		if ($('#tempVal').attr('title') !== '' && $('#keyword').val() === '') {
			$('#keyword').val($('#tempVal').attr('title'));
		} else {
			$('#keyword').val($('#keyword').val());
		}

		if (filter_map.hasOwnProperty('dir')) {
			if (filter_map.dir[0] === 'asc') {
				this.toggleSelected($('#assignment_list_sorting_asc'), $('#assignment_list_sorting_desc'));
			} else {
				this.toggleSelected($('#assignment_list_sorting_desc'), $('#assignment_list_sorting_asc'));
			}
		}
		if (filter_map.hasOwnProperty('current_view')) {
			if (filter_map.current_view === 'list' || filter_map.current_view === 'calendar') {
				this.current_view = filter_map.current_view.toString();
			}
		} else {
			this.current_view = 'list';
		}

		if (window.location.href.indexOf('calendar=on') !== -1) {
			this.current_view = 'calendar';
		}

		if (filter_map.hasOwnProperty('include_time')) {
			if ($('#include_time').val() === 'true') {
				$('#include_time').attr('checked', 'checked');
			}
		}

		if (filter_map.hasOwnProperty('currentCalendarView')) {
			this.currentCalendarView = filter_map.currentCalendarView.toString();
		}
		if (filter_map.hasOwnProperty('calendarYear')) {
			this.calendarYear = filter_map.calendarYear.toString();
		}
		if (filter_map.hasOwnProperty('calendarMonth')) {
			this.calendarMonth = filter_map.calendarMonth.toString();
		}
		if (filter_map.hasOwnProperty('calendarDay')) {
			this.calendarDay = filter_map.calendarDay.toString();
		}
		if (filter_map.hasOwnProperty('calendar_schedule_from')) {
			this.calendar_schedule_from = filter_map.calendar_schedule_from.toString();
		}
		if (filter_map.hasOwnProperty('calendar_schedule_through')) {
			this.calendar_schedule_through = filter_map.calendar_schedule_through.toString();
		}
		if (filter_map.hasOwnProperty('calendar_time_from')) {
			this.calendar_time_from = filter_map.calendar_time_from.toString();
		}
		if (filter_map.hasOwnProperty('calendar_time_through')) {
			this.calendar_time_from = filter_map.calendar_time_through.toString();
		}

		if (filter_map.hasOwnProperty('assigned_resource_names') && filter_map.hasOwnProperty('assigned_resources')) {
			this.prePopulateSelectData(filter_map.assigned_resource_names, filter_map.assigned_resources, '#resources-dropdown');
		}

		if (filter_map.hasOwnProperty('bundles_names') && filter_map.hasOwnProperty('bundles')) {
			this.prePopulateSelectData(filter_map.bundles_names, filter_map.bundles, '#bundles-dropdown');
		}
	},

	prePopulateSelectData (names, ids, selector) {
		if ($(selector).length && $(selector)[0].selectize) {
			$(selector)[0].selectize.clear(true);
			_.each(ids, (item, i) => {
				$(selector)[0].selectize.addOption({
					id: item,
					name: names[i]
				});
				$(selector)[0].selectize.addItem(item);
			});
		}
	},

	restoreFilters (filters) {
		this.updateOptionsFromFilters(filters);
		if (filters.hasOwnProperty('internal_owners') ||
			filters.hasOwnProperty('client_companies') ||
			filters.hasOwnProperty('projects') ||
			filters.hasOwnProperty('assigned_resources') ||
			filters.hasOwnProperty('bundles')
		) {
			$('#advanced-filters').show();
			$('.advanced-filters-toggle').html('Hide filters');
		} else {
			$('#advanced-filters').hide();
			$('.advanced-filters-toggle').html('Show more filters');
		}

		$('#list_filters, #form.dashboard-sort').find('input, select').each((i, o) => {
			const el = $(o);
			const f = filters[el.attr('name')] || [];
			for (let j = 0; j < f.length; j++) {
				const val = f[j];
				if (el.attr('type') === 'checkbox') {
					el.prop('checked', el.val() === val);
				} else if (el.is('select')) {
					el.find(`option[value="${val}"]`).attr('selected', true);
				} else if (val !== '') { // Don't try to set empty values.
					el.val(val);
				}
			}
		});

		if (filters.hasOwnProperty('dir') && filters.dir[0] === 'asc') {
			this.toggleSelected($('#assignment_list_sorting_asc'), $('#assignment_list_sorting_desc'));
		} else {
			this.toggleSelected($('#assignment_list_sorting_desc'), $('#assignment_list_sorting_asc'));
		}

		if (this.current_view === 'list') {
			if ($('#date_sub_filter').val() === '7') {
				$('#custom-range-dates').show();
			} else {
				$('#custom-range-dates').hide();
			}
		}

		if (filters.hasOwnProperty('assigned_to_me')) {
			$('#assigned_to_me').attr('checked', 'checked');
		}

		if (filters.hasOwnProperty('dispatched_by_me')) {
			$('#dispatched_by_me').attr('checked', 'checked');
		}

		if (filters.hasOwnProperty('following')) {
			$('#following').attr('checked', 'checked');
		}
	},

	toggleSelected (onBtn, offBtn) {
		onBtn.addClass('toggle_selected');
		$('i', onBtn).addClass('icon-white');
		offBtn.removeClass('toggle_selected');
		$('i', offBtn).removeClass('icon-white');
	}
});

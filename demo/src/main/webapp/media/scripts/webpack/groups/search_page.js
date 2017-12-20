'use strict';

import $ from 'jquery';
import GroupListTemplate from './templates/group_list_item.hbs';
import '../dependencies/jquery.tmpl';
import '../funcs/jquery-helpers';
import 'jquery-form/jquery.form';

export default function (resultsStart) {
	// Defaults
	let state = {
		formId: 'filter_form',
		resultsPerPage: 27,
		resultsStart,
		resultsLimit: 27,
		resultsCount: 0,
		facetOptionLimit: 5,
		facetOptionSuggestions: {},
		filters: []
	};

	const initFilters = function () {
		$('#facets')
			.delegate('div.show-hide a', 'click', clickMoreFilters)
			.delegate('input.show-all', 'click', clickShowAll)
			.delegate('ul.filter_items input[type="checkbox"]', 'click', clickFilter)
			.delegate('ul.filter_items input[type="radio"]', 'click', clickFilter)
			.delegate('select', 'change', loadData)
			.delegate('input[type="text"]:not(.filter_suggest)', 'keyup', function ({ which }) {
				// Only perform action on meaningful key presses.
				if (
					which == 8 ||
					(which >= 46 && which <= 90) ||
					(which >= 96 && which <= 111) ||
					which >= 186
				) {
					delay(() => {
						resetPagination();
						loadData();
					}, 1000);
				}
			});

		$('#filter_form').on('submit', () => {
			resetPagination();
			loadData();
			return false;
		});

		$('#search-submit-action').on('click', () => {
			resetPagination();
			loadData();
			return false;
		});

		$('#sortby').on('change', clickFilter);
		$('.prev').on('click', showPreviousPage);
		$('.next').on('click', showNextPage);

		$('#clear_facets').on('click', function () {
			$(this).parents('form').trigger('reset');
			$('input[name=group_keyword]').prop('value', '');
			resetPagination();
			loadData();
		});

		// Track filters
		$('#filter_form').trackForm({ name: 'groupFilters' });
	};

	const initResults = () => {
		const clickToggleGroupRequirements = function () {
			let el = $(this).closest('.results-row').find('.req-wrapper');
			let isVisible = el.is(':visible');
			el.toggle(!isVisible);
			$(this).html(`${isVisible ? 'Hide' : 'View'} Requirements`);
		};

		const clickToggleGroupSubRequirements = function () {
			let el = $(this).closest('li').find('.requirements');
			let isVisible = el.is(':visible');
			el.toggle(!isVisible);
			$(this).html(isVisible ? 'hide' : 'expand');
		};

		$('#search_results')
			.on('click', '.view-requirements-action', clickToggleGroupRequirements)
			.on('click', '.requirements .toggle', clickToggleGroupSubRequirements);
	};

	// Submit filters and load results.
	const loadData = (callback) => {
		let { resultsStart, resultsLimit, resultsPerPage, resultsCount } = state;
		let data = {
			start: resultsStart,
			limit: resultsLimit
		};

		const redrawPagination = (resultsStart, resultsPerPage, resultsCount) => {
			$('.current_page').text(getCurrentPage());
			$('.num_pages').text(getTotalPages());
			$('.search_result_start_index').text(resultsStart + 1);
			$('.search_result_end_index').text(resultsStart + resultsPerPage < resultsCount ? resultsStart + resultsPerPage : resultsCount);
			$('.search_result_count').text(resultsCount);

			$('.prev').toggleClass('disabled', getCurrentPage() === 1);
			$('.next').toggleClass('disabled', getCurrentPage() === getTotalPages());
		};

		const redrawResults = (data) => {
			let container = $('#search_results');
			if (data.length > 0) {
				container.empty();
				data.forEach((dataItem) => {
					let { lane, id } = dataItem;
					let item = GroupListTemplate(Object.assign({}, dataItem, { type: 'active', lane, id }));

					container.append(item);

					$('.requirements .toggle', item).click();
					$('.view-requirements-action', item).click();
				});
			} else {
				container.html('<div class="alert alert-block span11">Your search returned no talent pools. Try another keyword or broaden your results using the available filters.</div>');
			}

			// Trigger event.
			$('#search_results').trigger('wm_search_redraw_results');
		};

		const redrawFilters = (data) => {
			if (!data) {
				return;
			}

			let container = $('#industries .filter_items');
			let { filters: { labels }} = state;
			if (container) {
				container.empty();
				data.industries.forEach((industry, index) => {
					let { id, count, filter_on } = industry;
					$('#filter_item_checkbox').tmpl({
						name: 'industry[]',
						id: `industry_${index}`,
						value: id,
						label: labels.industries[id],
						count
					}).appendTo(container);
					if (filter_on) {
						$(`#industry_${index}`).prop('checked', true);
					}
				});
				$('#industries .show-hide').toggle(data.industries.length);
				showHideFilters($('#industries'));
			}

			$('#search_results').trigger('wm_search_redraw_filters');
		};

		const handleLoadData = function (data) {
			const { results, filters, results_count } = data;

			// Keep track of some data for use later.
			state.filters = filters;
			state.resultsCount = results_count;

			// Redraw elements.
			redrawPagination(state.resultsStart, state.resultsPerPage, state.resultsCount);
			redrawResults(results);
			redrawFilters(filters);
			componentHandler.upgradeAllRegistered();

			if (typeof callback === 'function') {
				callback();
			}
		};

		$(`#${state.formId}`).ajaxSubmit({
			dataType: 'json',
			data,
			success: handleLoadData
		});
	};

	// Expand/collapse filter options.
	const clickMoreFilters = function () {
		let isCollapsed = $(this).hasClass('collapse');
		$(this)
			.toggleClass('expand', isCollapsed)
			.toggleClass('collapse', !isCollapsed);
		showHideFilters($(this).closest('.omega-box'));
	};

	// Show/hide filter options.
	const showHideFilters = (container) => {
		if ($('.show-hide a', container).hasClass('expand')) {
			$('.bulk', container).slideUp('fast');
		} else {
			$('.bulk', container).slideDown('fast');
		}
	};

	// Handle show all click.
	const clickShowAll = function () {
		if ($(this).is(':checked')) {
			$(this).closest('div').find('.filter_items input[type="checkbox"]').removeProp('checked');
			resetPagination();
			loadData();
		} else {
			$(this).prop('checked', true);
		}
	};

	// Handle filter click.
	const clickFilter = function () {
		let checked = false;
		$(this).closest('ul').find('input[type="checkbox"]').each(function () {
			if ($(this).is(':checked')) {
				$(this).closest('div').find('.show-all').prop('checked', false);
				checked = true;
				return false;
			}
		});
		if (!checked) {
			$(this).closest('div').find('.show-all').prop('checked', true);
		}
		resetPagination();
		loadData();
	};

	// Pagination

	const showNextPage = (callback) => {
		if (getCurrentPage() == getTotalPages()) {
			return false;
		}
		state.resultsStart += state.resultsPerPage;
		loadData(typeof callback === 'function' ? callback : () => {});
	};

	const showPreviousPage = (callback) => {
		if (getCurrentPage() == 1) {
			return false;
		}
		state.resultsStart -= state.resultsPerPage;
		loadData(typeof callback === 'function' ? callback : () => {});
	};

	const resetPagination = () => state.resultsStart = 0;
	const getTotalPages = () => Math.ceil(state.resultsCount / state.resultsPerPage) || 1;
	const getCurrentPage = () => Math.floor(state.resultsStart / state.resultsPerPage) + 1;

	initResults();
	initFilters();
	loadData();
}

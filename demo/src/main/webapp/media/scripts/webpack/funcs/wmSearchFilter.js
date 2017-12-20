'use strict';
import $ from 'jquery';
import _ from 'underscore';
import SearchFilterTemplate from './templates/searchfilter_main.hbs';
import DefaultFilterTemplate from './templates/searchfilter_default.hbs';
import RatingsFilterTemplate from './templates/searchfilter_ratings.hbs';
import LocationFilterTemplate from './templates/searchfilter_location.hbs';
import InsuranceFilterTemplate from './templates/searchfilter_insurance.hbs';
import 'selectize';

export default (options) => {
	var settings = Object.assign({
		el: '.search-filter-bucket',
		template: SearchFilterTemplate,
		change: () => {},
		facetLoadUrl: ''
	}, typeof options === 'object' ? options : {});

	$(settings.el).html(settings.template(settings));

	if (typeof settings.filters !== 'undefined') {
		addItems(settings.filters);
	}

	var activeFilters = [];

	var $select = $('.wm-search-tags').selectize({
		create: false,
		valueField: 'title',
		labelField: 'title',
		closeAfterSelect: true,
		searchField: ['title'],
		hideSelected: true,
		onDropdownClose: function () {
			if(!$('.selectize-control.wm-search-tags input').is(':focus') || $('.selectize-control.wm-search-tags input').val() === '') {
				$('.wm-dropdown-filter').removeClass('selected');
				$('.wm-dropdown-section').removeClass('open');
			}
		},
		onDropdownOpen: function () {
			$('.wm-dropdown-section').addClass('open');
			$('.selectize-control.wm-search-tags input').focus();
		},
		render: {
			option: function (item) {
				switch (item.template) {
					default:
						return DefaultFilterTemplate(item);
				}
			},
			item: function (data, escape) {
				api.handleFilterEntry(data);
				return '';
			}
		},
		load: (query, callback) => {
			if (settings.facetLoadUrl !== '') {
				if (!query.length) {
					return callback();
				}
				const searchData = { term: query };
				$.ajax({
					url: settings.facetLoadUrl,
					traditional: true,
					type: 'GET',
					dataType: 'json',
					data: searchData,
					error: callback,
					success: (result) => {
						let filterOptions = result.reduce((memo, { id, name }) => {
						    let title = name;
						    let label = settings.activeFacetName;
						    return [...memo, { label, id, title }];
						}, []);
						for (var j = 0; j < filterOptions.length; j++) {
							let { id, title, label, avatar } = filterOptions[j];
							searchControl.addOption(
								[{
									id,
									title,
									label: label,
									filterValue: {
										name: settings.activeFacetKey,
										value: id,
										label: title
									},
									template: 'default'
								}]
							);
						}
					}
				});
			}
		}
	});

	var $input = $('#tags-input').selectize({
		create: true,
		plugins: ['remove_button'],
		persist: false,
		openOnFocus: false,
		onItemRemove: function (value) {
			activeFilters = activeFilters.filter((item) => {
				let index = value.indexOf(_.unescape(item.label));
				//added 2nd condition for cases with same words, i.e. Member and Member Override
				//modified second case to take Location into account as it has variable length
				return index  === -1 || ((index + item.label.length) !== value.length && item.label !== 'Location');
			});
			settings.change();
		}
	});

	var searchControl = $select[0].selectize,
		inputControl = $input[0].selectize;

	$('.wm-search-filter-search .selectize-input input').prop('disabled', true);

	$('#input-text').bind('keyup', function ({ type, keyCode }) {
		if ((type === 'keyup' && keyCode !== 10 && keyCode !== 13) || this.value === '') {
			return;
		}
		// prevent empty keyword
		if (type === 'keyup' && (keyCode === 10 || keyCode === 13) && this.value.trim() === '') {
			this.value = '';
			return;
		}
		var keywordObject = {
			filterValue: {
				label: this.value,
				name: 'keyword',
				value: this.value
			},
			label: 'Keyword',
			title: this.value
		};
		api.handleFilterEntry(keywordObject);
		$(this).val('');
	});

	$('#input-text').bind('keydown', function ({ keyCode }) {
		if (keyCode === 8 && this.value === '') {
			$('.selectize-input a.remove').last().click();
		}
	});

	if (!settings.hideDateRange) {

		$('.wm-search-filter-datepicker input[name="from"]').datepicker({ dateFormat: 'mm/dd/yy' });
		$('.wm-search-filter-datepicker input[name="to"]').datepicker({ dateFormat: 'mm/dd/yy' });

		$('.wm-dropdown-filter.date-filter').on('click', () => {
			$('.wm-search-filter-datepicker').css('left', $('.wm-dropdown-filter.date-filter').offset().left + 'px');
			$('.wm-search-filter-datepicker').toggle();
		});

		$('.apply-date').on('click', () => {
			var dateType = $('.wm-search-filter-datepicker select[name="datetype"]').val(),
				dateTypeString = $('.wm-search-filter-datepicker select[name="datetype"] option:selected').text(),
				rangeType = $('.wm-search-filter-datepicker select[name="rangetype"]').val(),
				from = $('.wm-search-filter-datepicker input[name="from"]').val(),
				to = $('.wm-search-filter-datepicker input[name="to"]').val();
			var dateVal = dateTypeString + ': ' + from + ' - ' + to;
			var dateObj = {
				name: 'date',
				dateType,
				from,
				to,
				label: dateVal
			};
			activeFilters.push(dateObj);
			$('.wm-search-filter-datepicker').hide();
			inputControl.setTextboxValue(dateVal);
			inputControl.createItem(dateVal);
			inputControl.refreshItems();
		});

		$('.close-date').on('click', () => $('.wm-search-filter-datepicker').hide());
	}

	$('.selectize-control.wm-search-tags input').keyup(({ keyCode }) => {
		if (keyCode === 27 || keyCode === 10 || keyCode === 13) {
			$('.wm-dropdown-filter').removeClass('selected');
			$('.wm-dropdown-section').removeClass('open');
		}
	});

	var api = {
		addFilter: function (filter) {
			var dropdownElement = $('<div id="' + filter.name + '"></div>');
			dropdownElement.addClass('wm-dropdown-filter');
			dropdownElement.append('<label>' + filter.title + '</label>');
			$('.wm-search-filter-dropdowns').prepend(dropdownElement);
			addItems([filter]);
		},
		addAdvancedFilter: function (filter) {
			let facetTemplate;
			switch (filter.template) {
				case 'ratings':
					facetTemplate = RatingsFilterTemplate;
					break;
				case 'location':
					facetTemplate = LocationFilterTemplate;
					break;
				case 'insurance':
					facetTemplate = InsuranceFilterTemplate;
					break;
				default:
					facetTemplate = DefaultFilterTemplate;
					break;
			}

			let dropdownElement = $('<div id="' + filter.name + '"></div>'),
				facetContainer = $('.wm-search-filter--advanced-facet');

			dropdownElement.addClass('wm-dropdown-filter');
			dropdownElement.addClass('advanced-filter');
			dropdownElement.append('<label>' + filter.title + '</label>');
			$('.wm-search-filter-dropdowns').prepend(dropdownElement);
			dropdownElement.on('click', (event) => {
				event.stopPropagation();
				$('.wm-dropdown-filter').removeClass('selected');
				facetContainer.html(facetTemplate());
				componentHandler.upgradeAllRegistered();
				if((dropdownElement.offset().left + facetContainer.width()) > Math.max(document.documentElement.clientWidth, window.innerWidth || 0)) {
					facetContainer.css('left', '');
					facetContainer.css('right', '0px');
				} else {
					facetContainer.css('right', '');
					facetContainer.css('left', dropdownElement.position().left + 'px');
				}
				facetContainer.show();
				filter.renderCallback();
				dropdownElement.addClass('selected');
				$('.close-advanced-facet').on('click', function () {
					facetContainer.hide();
					dropdownElement.removeClass('selected');
				});

				facetContainer.off('click').on('click', (event) => event.stopPropagation());
				$(document).off('click.advancedFacet').on('click.advancedFacet', function (event) {
					if (facetContainer.css('display') == 'block') {
						facetContainer.hide();
						dropdownElement.removeClass('selected');
					}
					$(document).off('click.advancedFacet');
				});
			});
		},
		removeFilter: (filterName) => $(`#${filterName}`).remove(),
		removeAllFilters: () => $('.wm-dropdown-filter').not('#daterange').remove(),
		reset: function (silent) {
			inputControl.clear();
			inputControl.clearOptions();
			inputControl.clearCache();
			inputControl.refreshOptions();
			activeFilters = [];
			if (!silent) {
				settings.change();
			}
		},
		getFilterObject: () => activeFilters,
		handleFilterEntry: function ({ filterValue, title, label }, silent) {
			if (_.where(activeFilters, filterValue).length === 0) {
				activeFilters.push(filterValue);
				let tagText = `${label}: ${title}`;
				inputControl.setTextboxValue(tagText);
				inputControl.createItem(_.unescape(tagText));
				inputControl.refreshItems();
				if (!silent) {
					settings.change();
				}
			}
		},
		removeSelectionByName: (name) => $(`.selectize-input div[data-value^="${name}:"]`).find('a.remove').trigger('click')
	};

	function addItems(items) {
		let datePicker = $('.wm-search-filter-datepicker');
		items.forEach((item, index) => {
			let { options, name, title, template, loadUrl = '' } = item;
			$(`#${items[index].name}`).on('click', () => {
				datePicker.hide();
				$(this).addClass('selected');
				searchControl.clearOptions();
				settings.activeFacetName = title;
				settings.activeFacetKey = name;
				if (loadUrl !== '') {
					settings.facetLoadUrl = loadUrl;
				} else {
					settings.facetLoadUrl = '';
				}
				for (var j = 0; j < options.length; j++) {
					let { id, title, label, avatar } = options[j];
					searchControl.addOption(
						[{
							id,
							title,
							label: label || null,
							avatar: avatar || null,
							filterValue: {
								name,
								value: id,
								label: title
							},
							template: template || 'default'
						}]
					);
				}
				searchControl.open();
			});
		});
	}
	return api;
};

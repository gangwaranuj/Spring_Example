'use strict';
import $ from 'jquery';
import _ from 'underscore';
import wmNotify from '../funcs/wmNotify';
import emptyResults from './templates/empty-results.hbs';
import resultsTemplate from './templates/results.hbs';
import '../funcs/jquery.feedtracker';
import '../funcs/jquery.socialize';

function create ({
		feedTrackerEl,
		limit,
		distance,
		postalCode,
		constants,
		companyHidesPricing,
		hasAnyRoleAdminManagerDispatcher,
		hasDispatchEnabled }) {
	// Load the tracker before the searcher so it can listen to feedQuery events
	$(feedTrackerEl).feedTracker({
		keywordField: 'k',
		queryFields: {
			k: 'keyword',
			p: 'postal',
			st: 'state',
			v: 'virtual',
			d: 'distance',
			i: 'industry',
			w: 'when',
			s: 'page',
			l: 'limit'
		}
	});

	// work feed search
	const feedSearcher = function (options) {
		'use strict';
		$('.dropdown-toggle').on('click',() => $('.dropdown-menu.location').show())
		var LIMIT = options.limit || 5,
		START = 0,
		VALIDATION_CONSTANTS = options.validationConstants,
		$postal = $('#feed-postal'),
		$state = $('#feed-state'),
		$virtual = $('#feed-virtual'),
		$postalCodeToggle = $('input[name="togglePostal"]'),
		searchKeys = [
			'start', 'limit', 'industry', 'togglePostal', 'postal',
			'state', 'distance', 'keyword', 'when'
		];

		$virtual.prop('disabled', true);

		// Arrrgh IE!
		var placeholders = {
			keyword: 'Title, Keywords, or Company',
			postal: 'Postal Code',
			state: 'State',
			distance: 'Distance'
		};

		var getQuery = function (data) {
			var query = {};
			_.each(searchKeys, function (key) {
				var value = data[key];

				if (value && value !== placeholders[key]) {
					if (key === 'state'){
						query.st = value;
					} else {
						query[key[0]] = value;
					}
				}
			});
			query.v = $virtual.is(':visible');
			return query;
		};

		var serialize = function (form) {
			return _(form.serializeArray()).reduce(function(obj, attr) {
				obj[attr.name] = sanitize(attr.value);
				return obj;
			}, {});
		};

		var spin = function (shouldSpin) {
			if (shouldSpin) {
				$('#search-button').hide();
				$('#spinner').show();
			} else {
				$('#spinner').hide();
				$('#search-button').show();
			}
		};

		var sanitize = function (str) {
			var div = document.createElement('div');
			div.appendChild(document.createTextNode(str));
			return div.innerHTML;
		};

		var refresh = function () {
			var errorMessage = validate();
			if (!_.isUndefined(errorMessage)) {
				showErrorMessages([errorMessage]);
				return false;
			}
			var data = _.extend(serialize($('#feed-searcher')), {start: START, limit: LIMIT});
			spin(true);

			$(window).trigger('feedQuery', getQuery(data));

			$.get('/feed/firehose' + window.location.search, getQuery(data), function (data) {
				spin(false);
				if (data.results && data.results.length > 0) {
					$('#front-feed').html(resultsTemplate({
						items: data,
						isNotFirstPage: data.page > 0,
						isMorePages: ((data.page + 1) < (data.totalCount/data.pageSize)),
						options: options
					}));
					$('[data-socialize]').socialize();
				} else {
					$('#front-feed').html(emptyResults());
					if (data.errorMessages) {
						showErrorMessages(data.errorMessages);
					}
				}
			});
		};

		var validate = function () {
			var postalCode = $postal.val().trim();
			if ($postal.is(":visible") && postalCode !== '') {
				if (postalCode.length < VALIDATION_CONSTANTS.postalCodeMin) {
					return VALIDATION_CONSTANTS.postalCodeErrors.min;
				}
				if (postalCode.length > VALIDATION_CONSTANTS.postalCodeMax) {
					return VALIDATION_CONSTANTS.postalCodeErrors.max;
				}
			}
		};

		var $container = $('.container');
		$postal.val($postal.val() || options.postal);
		$state.val($state.val() || options.state);

		var $distance = $('#feed-distance');
		$distance.val($distance.val() || options.distance);

		$('[data-action="search"]').on('click', function (e) {
			e.preventDefault();
			START = 0;
			refresh();
		});

		$container.on('change','#feed-industry', function (e) {
			e.preventDefault();
			START = 0;
			refresh();
		});

		$container.on('click','#next-page', function (e) {
			e.preventDefault();
			START = START + LIMIT;
			refresh();
		});

		$container.on('click','#prev-page', function (e) {
			e.preventDefault();
			if(START >= LIMIT){
				START = START - LIMIT;
			}
			refresh();
		});

		var toggleLocationInput = function (options) {
			var link = $(options.e.target).closest('a');
			options.e.preventDefault();
			if (link.data('searchtype') !== 'postal') {
				$postal.hide();
				$postal.val('');
			}
			if (link.data('searchtype') !== 'state') {
				$state.hide();
				$state.val('');
			}
			if (link.data('searchtype') !== 'virtual') {
				$virtual.hide();
			}
			options.target.show();
			link.parent().siblings().children('a').removeClass('active');
			link.addClass('active');

			$('.input-dropdown-icon')
			.attr('class', 'input-dropdown-icon')
			.addClass(link.find('i').attr('class'));

			$postalCodeToggle.val(link.data('searchtype') === 'postal');
			$distance.prop('disabled', link.data('searchtype') !== 'postal');
			$('.dropdown-menu.location').hide()
		};

		$('a[data-searchtype="virtual"]').on('click', function (e) {
			toggleLocationInput({target: $virtual, e: e});
		});

		$('a[data-searchtype="postal"]').on('click', function (e) {
			toggleLocationInput({target: $postal, e: e});
		});

		$('a[data-searchtype="state"]').on('click', function (e) {
			toggleLocationInput({target: $state, e: e});
		});

		var showErrorMessages = function (errors) {
			if (errors.length > 0) {
				_.each(errors, function (error) {
					wmNotify({
						message: error,
						type: 'danger'
					});
				});
			}
		};
		refresh();
	};

	feedSearcher({
		limit,
		distance,
		postal: postalCode,
		validationConstants: constants || {},
		companyHidesPricing,
		hasAnyRoleAdminManagerDispatcher,
		hasDispatchEnabled
	});
}

export default { create };

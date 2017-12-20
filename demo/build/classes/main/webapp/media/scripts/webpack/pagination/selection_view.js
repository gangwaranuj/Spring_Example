'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';

export default Backbone.View.extend({
	el: '.pagination-selection',

	events: {
		'click .select-all-visible-outlet'  : 'selectAllVisible',
		'click .select-all-outlet'          : 'selectAll',
		'click .clear-selection-outlet'     : 'clear'
	},

	totalResults: 0,
	visibleResults: 0,
	selectedResults: [],
	selectedResultsModels: [],
	results: [],

	initialize: function (options) {
		this.options = _.defaults(options || {}, {
			resultsSelector: '.results-list'
		});

		var self = this;
		$(this.options.resultsSelector).on('click', 'input[type="checkbox"]', function (e) {
			self.select(e);
		});
		this.$selectAll = $('#select-all');
	},

	render: function () {

		$('.total-count').text(this.totalResults);
		$('.selected-count').text(this.selectedResults.length);

		var checkboxes = this.checkboxes();
		checkboxes.prop('checked', false);
		_.each(this.selectedResults, function(id) {
			checkboxes.filter('[value="' + id + '"]').prop('checked', true);
		});
		$('.select-all-visible-outlet').prop('checked', this.allVisibleSelected());
		this.trigger('select:render');

		return this;
	},

	selectAll: function() {
		var self = this;

		$.each(this.results, function (i, item) {
			self._select(item, true);
		});

		this.render();

		this.trigger('select:all');
	},

	selectAllVisible: function(e) {
		var self = this;
		var checkbox = $(e.currentTarget);

		this.checkboxes().each(function() {
			self._select($(this).val(), checkbox.is(':checked'));
		});

		this.render();
		this.trigger('select:visible');
	},

	select: function(e) {
		var checkbox = $(e.currentTarget);
		var isChecked = checkbox.is(':checked');

		this._select(checkbox.val(), isChecked);
		$('.select-all-visible-outlet').prop('checked', this.allVisibleSelected());
		this.render();
		this.trigger('select:single');
	},

	_select: function (id, status) {
		if (status) {
			this.selectedResults = _.union(this.selectedResults, [id]);
		} else {
			this.selectedResults = _.without(this.selectedResults, id);
		}
	},

	clear: function () {
		this.selectedResults = [];
		this.selectedResultsModels = [];
		this.render();
	},

	setTotalCount: function (count) {
		this.totalResults = count;
	},

	setVisibleCount: function (count) {
		this.visibleResults = count;
	},

	setResults: function (results) {
		this.results = results;
	},

	getSelectedCount: function () {
		return this.selectedResults.length;
	},

	getSelected: function () {
		return this.selectedResults;
	},

	getSelectedModels: function () {
		return this.selectedResultsModels;
	},

	hasSelections: function () {
		return this.selectedResults.length > 0;
	},

	checkboxes: function () {
		return $('input[type="checkbox"]', this.options.resultsSelector);
	},

	selectedCheckboxes: function () {
		return $('input[type="checkbox"]:checked', this.options.resultsSelector);
	},

	allVisibleSelected: function () {
		var cboxes = this.checkboxes().length;
		if (cboxes > 0) {
			var sboxes = this.selectedCheckboxes().length;
			return cboxes === sboxes;
		}
		return false;
	},

	reset: function () {
		this.totalResults = 0;
		this.visibleResults = 0;
		this.selectedResults = [];
		this.selectedResultsModels = [];

		this.render();
	}
});

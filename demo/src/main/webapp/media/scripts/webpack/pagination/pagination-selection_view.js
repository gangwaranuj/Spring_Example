'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import SelectAllModel from '../pagination/pagination-selection_model';

export default Backbone.View.extend({
	el: '.pagination-selection',

	events: {
		'click .select-all-visible-outlet' : 'selectAllVisible',
		'click .select-all-outlet'         : 'selectAll',
		'click .clear-selection-outlet'    : 'clear',
		'click #full-select-all'           : 'handleFullSelectAll'
	},

	totalResults: 0,
	visibleResults: 0,
	selectedResults: [],
	selectedResultsModels: [],
	results: [],
	select_all: new SelectAllModel(),

	initialize: function (options) {
		this.options = _.defaults(options || {}, {
			results_selector: '.results-list'
		});

		var self = this;
		$(this.options.results_selector).on('click', 'input[type="checkbox"]', function (e) {
			self.select(e);
		});
		this.$selectAll = $('#select-all');
		this.$fullSelectMessage = $('.full-select-all-msg');
		this.$clearFullSelectAllMessage = $('.clear-full-select-all-msg');
		this.select_all.is_select_all = false;
	},

	render: function() {

		$('.total-count').text(this.totalResults);
		$('.selected-count').text(this.selectedResults.length);

		var checkboxes = this.checkboxes();
		checkboxes.prop('checked', false);
		_.each(this.selectedResults, function(id) {
			checkboxes.filter('[value="' + id + '"]').prop('checked', true);
		});
		$('.select-all-visible-outlet').prop('checked', this.allVisibleSelected());
		this.trigger('select:render');
		if (this.$selectAll.is(':checked') && this.select_all.is_select_all === false) {
			this.$clearFullSelectAllMessage.hide();
			var count = this.visibleResults > this.totalResults ? this.totalResults: this.visibleResults;
			this.$fullSelectMessage.html("All " + count + " assignments on this page are selected. <a href='#' id='full-select-all'>Select all assignments that match this search</a>")
			this.$fullSelectMessage.show();
		} else if (this.$selectAll.is(":checked") && this.select_all.is_select_all === true) {
			this.$fullSelectMessage.hide();
			this.$clearFullSelectAllMessage.html("All " + this.totalResults + " assignments in this search are selected.  <a href='#' id='clear-full-select-all'>Clear selection</a>");
			this.$clearFullSelectAllMessage.show();
		} else {
			this.$fullSelectMessage.hide();
			this.$clearFullSelectAllMessage.hide();
		}

		return this;
	},

	handleFullSelectAll: function () {
		this.select_all.is_select_all = true;
		this.select_all.save();
		this.render();
	},

	selectAll: function() {
		var self = this;

		$.each(this.results, function(i, item) {
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

		if (this.select_all.is_select_all) {
			this.select_all.is_select_all = false;
			this.select_all.save();
		}

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

	_select: function(id, status) {
		this._handle_model_objects(id, status);
		if (status) {
			this.selectedResults = _.union(this.selectedResults, [id]);
		} else {
			this.selectedResults = _.without(this.selectedResults, id);
		}
	},

	_handle_model_objects: function(id, status) {
		if (this.options.dashboardRouter) {
			var model_obj = _.find(this.options.dashboardRouter.assignment_list.models, function (model) { return model.id === id; }).attributes;

			if (status) {
				this.selectedResultsModels = _.union(this.selectedResultsModels, [model_obj]);
			} else {
				this.selectedResultsModels = _.reject(this.selectedResultsModels, function(model) {
					return (model.id === id)
				});
			}
		}
	},

	clear: function() {
		this.selectedResults = [];
		this.selectedResultsModels = [];
		this.select_all.is_select_all = false;
		this.render();
	},

	clearFullSelectAll: function() {
		this.select_all.is_select_all = false;
		$('.full-select-all-msg').hide();
		$('.clear-full-select-all-msg').hide();
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

	isFullSelected: function () {
		return this.select_all.is_select_all;
	},

	checkboxes: function () {
		return $('input[type="checkbox"]', this.options.results_selector);
	},

	selectedCheckboxes: function () {
		return $('input[type="checkbox"]:checked', this.options.results_selector);
	},

	allVisibleSelected: function () {
		var cboxes = this.checkboxes().length;
		if (cboxes > 0) {
			var sboxes = this.selectedCheckboxes().length;
			return cboxes == sboxes;
		}
		return false;
	},

	reset: function () {
		this.totalResults = 0;
		this.visibleResults = 0;
		this.selectedResults = [];
		this.selectedResultsModels = [];
		if (this.options.isBuyer) {
			this.select_all.is_select_all = false;
		}
		this.render();
	}
});

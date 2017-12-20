'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';

export default Backbone.View.extend({
	el: '.pagination',

	events: {
		'click .prev'                : 'showPrevious',
		'click .wm-pagination--back' : 'showPrevious',
		'click .next'                : 'showNext',
		'click .wm-pagination--next' : 'showNext',
		'click .first'               : 'showFirst',
		'click .last'                : 'showLast',
		'change .rows_per_page'      : 'changeLimit',
		'change .sort_by'            : 'changeSort',
		'click #invoice_sorting_dsc' : 'sortDescending',
		'click #invoice_sorting_asc' : 'sortAscending'
	},

	initialize: function (options) {
		this.options = _.defaults(options || {}, {
			start: 0,
			limit: 25,
			total: 0,
			sort: 'payment_status',
			sort_direction: 'asc',
			disabled_class: 'disabled',
			disable_constraints: false
		});

		this.start = this.options.start;
		this.limit = this.options.limit;
		this.total = this.options.total;
		this.sort  = this.options.sort;
		this.sort_direction = this.options.sort_direction;
	},

	render: function () {
		if (this.options.disable_constraints || this.getStart() >= this.getTotal()) {
			this.showPrevious();
		}

		$('.start_index', this.$el).text(this.getStart() + 1 < this.getTotal() ? this.getStart() + 1 : this.getTotal());
		$('.end_index', this.$el).text(this.getStart() + this.getLimit() < this.getTotal() ? this.getStart() + this.getLimit() : this.getTotal());
		$('.count', this.$el).text(this.getTotal());

		$('.current_page', this.$el).text(this.getCurrent());
		$('.num_pages', this.$el).text(this.getNumPages());
		$('.wm-pagination', this.$el).attr('data-min', this.getCurrent());
		$('.wm-pagination', this.$el).attr('data-max', this.getNumPages());

		$('.prev', this.$el).toggleClass(this.options.disabled_class, this.getCurrent() <= 1);
		$('.next', this.$el).toggleClass(this.options.disabled_class, this.getCurrent() >= this.getNumPages());

		$('.rows_per_page', this.$el).val(this.limit);

		return this;
	},

	showNext: function () {
		if (!this.options.disable_constraints && this.getCurrent() >= this.getNumPages()) {
			return false;
		}

		this.start = parseInt(this.start,10);
		this.limit = parseInt(this.limit,10);

		this.start += this.limit;

		// Trigger change event.
		this.trigger('pagination:next');
	},

	showPrevious: function () {
		if (!this.options.disable_constraints && this.getCurrent() <= 1) {
			return false;
		}

		this.start = parseInt(this.start,10);
		this.limit = parseInt(this.limit,10);
		this.start -= this.limit;

		// Trigger change event.
		this.trigger('pagination:previous');
	},

	showFirst: function () {
		this.showPage(1);
	},

	showLast: function () {
		this.showPage(this.getNumPages());
	},

	showPage: function (page) {
		if (!this.options.disable_constraints && (page < 1 || page > this.getNumPages())) {
			return;
		}

		this.start = (parseInt(page) - 1) * this.limit;

		// Trigger change event (not exactly the right event but it should be fine).
		this.trigger('pagination:next');
	},

	changeSort: function (event) {
		this.sort = $(event.currentTarget).val();
		this.trigger('pagination:sort_by');
	},

	sortDescending: function () {
		this.sort_direction = 'desc';
		this.toggleSelected($('#invoice_sorting_dsc'), $('#invoice_sorting_asc'));
		this.trigger('pagination:sort_direction');
	},

	sortAscending: function () {
		this.sort_direction = 'asc';
		this.toggleSelected($('#invoice_sorting_asc'), $('#invoice_sorting_dsc'));
		this.trigger('pagination:sort_direction');
	},

	toggleSelected: function (onBtn, offBtn) {
		onBtn.addClass('toggle_selected');
		$('i', onBtn).addClass('icon-white');
		offBtn.removeClass('toggle_selected');
		$('i', offBtn).removeClass('icon-white');
	},

	changeLimit: function (event) {
		this.start = 0;
		this.limit = parseInt($(event.currentTarget).val());
		// Trigger change event.
		this.trigger('pagination:limit_changed');
	},

	setLimit: function (lim) {
		this.start = 0;
		this.limit = lim;

		// Trigger change event.
		this.trigger('pagination:limit_changed');
	},

	getSort: function () {
		return this.sort;
	},

	getSortDirection: function () {
		return this.sort_direction;
	},

	setTotal: function (total) {
		this.total = total;
	},

	getTotal: function () {
		return this.total;
	},

	getStart: function () {
		return this.start;
	},

	getLimit: function () {
		return this.limit;
	},

	getNumPages: function () {
		return Math.ceil(this.total / this.limit) || 1;
	},

	getCurrent: function () {
		return Math.floor(this.start / this.limit) + 1;
	},

	resetStart: function () {
		this.start = 0;
	}
});

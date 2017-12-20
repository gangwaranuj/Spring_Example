'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import Application from '../core';
import PaginationView from '../pagination/pagination_view';
import PaginationSelectionView from '../pagination/pagination-selection_view';
import InvoiceListItemView from './invoice_list_item_view';
import InvoiceNoResultsView from './invoice_no_results_view';

export default Backbone.View.extend({
	initialize: function (options) {
		this.options = options;

		this.collection.bind('reset', this.render, this);

		Application.Events.on('invoices:fastFundsSuccess', () => this.refetch());

		this.pagination = new PaginationView({
			el: this.$('.mini-pagination'),
			limit: 50,
			disabled_class: 'paginate_button_disabled'
		});

		this.pagination.bind('pagination:next', this.refetch, this);
		this.pagination.bind('pagination:previous', this.refetch, this);
		this.pagination.bind('pagination:limit_changed', this.refetch, this);
		this.pagination.bind('pagination:sort_by', this.refetch, this);
		this.pagination.bind('pagination:sort_direction', this.refetch, this);
		this.pagination_selection = new PaginationSelectionView({
			results_selector: '#invoices-table tbody'
		});
		this.pagination_selection.on('select:render', () => this.enableActions());
	},

	render: function () {
		this.$('#invoices-table > tbody').empty();
		if (this.collection.getTotalResults()) {
			this.collection.each(_.bind(this.addOne, this));
		} else {
			this.noResults();
		}

		this.pagination.setTotal(this.collection.getTotalResults());
		this.pagination.render();

		this.pagination_selection.setTotalCount(this.pagination.getTotal());
		this.pagination_selection.setVisibleCount(this.pagination.getLimit());
		this.pagination_selection.render();

		if (this.options.parent.options.current_view === 'receivables') {
			$('.check input').hide();
		} else {
			$('.check input').show();
		}

		return this;
	},

	refetch: function () {
		var filters = $('form [ref="filter"]').serializeArray();

		filters.push({
			name: 'start',
			value: this.pagination.getStart()
		});

		filters.push({
			name: 'limit',
			value: this.pagination.getLimit()
		});

		filters.push({
			name: 'sort',
			value: this.pagination.getSort()
		});

		filters.push({
			name: 'sortDirection',
			value: this.pagination.getSortDirection()
		});

		this.collection.setFilters(filters);
		this.collection.fetch({
			success: function () {}
		});
	},

	addOne: function (item) {
		let invoiceId = item.get('invoiceId');
		let view = new InvoiceListItemView({
			currentView: this.options.currentView,
			model: item,
			list: this,
			mmwAutoPayEnabled: this.options.mmwAutoPayEnabled,
			hasFeatureFastFunds: this.collection.hasFastFunds(invoiceId),
			isFastFundsAvailable: this.collection.isFastFundsAvailable(invoiceId),
			isFastFundsComplete: this.collection.isFastFundsComplete(invoiceId),
			fastFundsFee: this.collection.getFastFundsFee(invoiceId)
		});
		$('#invoices-table > tbody').append(view.render().el);
	},

	noResults: function () {
		var view = new InvoiceNoResultsView();
		$('#invoices-table > tbody').append(view.render().el);
	},

	enableActions: function () {
		var disabled = true;
		var ids = this.getSelected();
		var selectedCheckboxLocked = $('#invoices-table').find('tbody').find('input[type="checkbox"]:checked').parents('tr').find('.wm-icon-lock-circle');

		if (ids.length > 0) {
			var selected = this.collection.filter(function (o) {
				// NOTE ids is list of strings
				return _.include(ids, '' + o.get('invoiceId'));
			});

			var paid = _.find(selected, function (item) {
				return item.get('invoiceStatusTypeCode') === 'paid';
			});

			disabled = (paid !== undefined);
		}

		var payOutlet = $('#pay-selected-outlet'),
			bundleOutlet = $('#bundle-selected-outlet'),
			addToBundleOutlet = $('#add-to-bundle-selected-outlet'),
			isAddToBundle = (selectedCheckboxLocked.size() > 0 || $('.bundle').length === 0 || ids.length === 0);

		payOutlet.toggleClass('disabled', disabled).attr('disabled', disabled);
		bundleOutlet.toggleClass('disabled', disabled || ids.length < 2).attr('disabled', disabled);
		addToBundleOutlet.toggleClass('disabled', isAddToBundle).attr('disabled', isAddToBundle);

		this.renderTooltip(payOutlet);
		this.renderTooltip(bundleOutlet);
		this.renderTooltip(addToBundleOutlet);
	},

	renderTooltip: function (selector) {
		if (selector.is('.disabled')) {
			selector.addClass('tooltipped');
		} else {
			selector.removeClass('tooltipped');
		}
	},

	clearSelected: function () {
		this.pagination_selection.clear();
	},

	getSelected: function () {
		return this.pagination_selection.getSelected();
	}
});

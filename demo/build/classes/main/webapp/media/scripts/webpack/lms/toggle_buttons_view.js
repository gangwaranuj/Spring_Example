'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import BoxesManageTestView from './boxes_manage_test_view';
import '../dependencies/jquery.easypaginate';

export default Backbone.View.extend({
	el: '#filter_buttons',

	events: {
		'click #invitedFilter'      : 'showInvited',
		'click #inProgressFilter'   : 'showInProgress',
		'click #passedFilter'       : 'showPassed',
		'click #gradePendingFilter' : 'showGradePending',
		'click #failedFilter'       : 'showFailed'
	},

	initialize: function (collection) {
		_.bindAll(this, 'render', 'loadPage', 'showInvited', 'showInProgress', 'showPassed', 'showGradePending', 'showFailed', 'setActiveFilter');

		this.collection = collection;
		this.options.visibleItems = 4;
		this.showCarouselItemsCallback = null;

		// By default show "Invited" tab
		this.showInvited();
	},

	render: function (filter) {

		if (this.collection.size() === 0) {
			$('#' + filter + 'Filter').append($.tmpl($('#n_items').html(), {n: 0}));
			$('#message_' + filter).removeClass('dn');
			$('#' + filter + 'Filter').removeAttr('disabled');
			return;
		}

		var startIndex = (this.collection.currentPage - 1) * this.collection.resultsPerPage;
		var endIndex = this.collection.size();

		_.each(_.toArray(this.collection.models).slice(startIndex, endIndex), function (model) {
			var t = new BoxesManageTestView({model: new BoxesManageTestView(model)});
			$('#mytest_' + filter).append(t.render().innerHTML);
		});

		if (this.collection.currentPage === 1) {
			$('#' + filter + 'Filter').append($.tmpl($('#n_items').html(), {n: this.collection.totalResults}));
			$('ul#mytest_' + filter).css('width', (260 * this.collection.totalResults));
			$('ul#mytest_' + filter).easyPaginate({
				count: this.collection.totalResults,
				step: this.options.visibleItems,
				controls: 'paginationMyTests',
				dataCallback: this.loadPage
			});
		}

		if (this.showCarouselItemsCallback !== null) {
			this.showCarouselItemsCallback();
			this.showCarouselItemsCallback = null;
		}
		$('#' + filter + 'Filter').removeAttr('disabled');
		return this;
	},

	showInvited: function () {
		this.setActiveFilter('invitedFilter');
	},

	showInProgress: function () {
		this.setActiveFilter('inProgressFilter');
	},

	showPassed: function () {
		this.setActiveFilter('passedFilter');
	},

	showGradePending: function () {
		this.setActiveFilter('gradePendingFilter');
	},

	showFailed: function () {
		this.setActiveFilter('failedFilter');
	},

	setActiveFilter: function (filterName) {
		this.currentFilter = filterName;
		$('.mytest-message').addClass('dn');
		$('.option').removeClass('active-option-manage-tests');
		$('#' + filterName).addClass('active-option-manage-tests');
		$('.nitems').remove();
		$('.paginationMyTests').empty();
		$('#mytest_' + filterName.replace('Filter', '')).empty();
		$('#mytest_' + filterName.replace('Filter', '')).css('margin-left', 0);
		$('#' + filterName).attr('disabled', 'disabled');
		this.collection.reset();
		this.collection.totalResults = 0;
		this.collection.currentPage = 1;
		this.collection.fetchData(filterName);
		$('.mytest.items').addClass('dn');
		$('#mytest_' + filterName.replace('Filter', '')).removeClass('dn');
	},

	loadPage: function (page, showCallback) {
		if ((page * this.options.visibleItems) > (this.collection.currentPage * this.collection.resultsPerPage)) {
			this.collection.currentPage++;
			this.collection.fetchData(this.currentFilter);
			this.showCarouselItemsCallback = showCallback;
		} else {
			showCallback();
		}
	}
});

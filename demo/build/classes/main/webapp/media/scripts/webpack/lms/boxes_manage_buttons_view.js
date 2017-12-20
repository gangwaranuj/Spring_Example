'use strict';

import $ from 'jquery';
import _ from 'underscore';
import Backbone from 'backbone';
import BoxesManageTestView from './boxes_manage_test_view';
import TestModel from './boxes_manage_test_model';

export default Backbone.View.extend({
	el: '#filter_buttons',

	events: {
		'click #activeFilter'   : 'showActive',
		'click #allFilter'      : 'showAll',
		'click #testIOwnFilter' : 'showTestIOwn',
		'click #inactiveFilter' :  'showInactive'
	},

	initialize: function () {
		_.bindAll(this, 'showActive', 'showTestIOwn', 'showAll', 'showInactive', 'setActiveFilter', 'fetchData', 'fetchMoreData');
		this.options.filterNames = {
			'active'   : 'ACTIVE',
			'testIOwn' : 'TESTS_I_OWN',
			'all'      : 'ALL',
			'inactive' : 'INACTIVE'
		};

		this.collection = new Backbone.Collection();
		this.collection.currentPage = 1;
		this.options.currentFilter = 'ACTIVE';
		this.showActive();
	},

	render: function (filterName) {
		var $items = $('#items');
		if ((_.isEmpty(this.collection.models))) {
			$('#message_no_tests').show();
			$('#manage-tests').hide();
		}
		_.each(this.collection.models, function (model) {
			var t = new BoxesManageTestView({
				model: new BoxesManageTestView(model)
			});
			$items.append(t.render());
		});

		$('#' + filterName).removeAttr('disabled');
		return this;
	},

	showAll: function () {
		this.setActiveFilter('allFilter');
	},

	showTestIOwn: function () {
		this.setActiveFilter('testIOwnFilter');
	},

	showActive: function () {
		this.setActiveFilter('activeFilter');
	},

	showInactive: function () {
		this.setActiveFilter('inactiveFilter');
	},

	setActiveFilter: function (filterName) {
		if (this.options.currentFilter !== filterName) {
			this.collection.currentPage = 1;
			$('#items').empty();
			$('#' + filterName).attr('disabled', 'disabled');
			this.fetchData(filterName);
		}
	},

	updateActiveToggle: function (filterName, nitems) {
		$('.option').removeClass('active-option-manage-tests');
		var filterDiv = $('#' + filterName);
		filterDiv.addClass('active-option-manage-tests');
		$('.nitems').remove();
		filterDiv.append($.tmpl($('#n_items').html(), { n: nitems }));
	},

	fetchData: function (filterName) {
		var filter = filterName.replace('Filter', '');
		var filterParam = this.options.filterNames[filter];

		if (filterParam != null) {
			$.ajax({
				context: this,
				url: '/lms/manage/' + this.collection.currentPage + '?type=' + filterParam,
				processData: false,
				success: _.bind(function (response) {
					if (response.successful && response.data.assessments !== 'undefined') {
						this.collection.reset();
						_.each(response.data.assessments, function (test) {
							this.collection.add(new TestModel(test));
						}, this);
						this.updateActiveToggle(filterName, response.data.rowCount);
						this.options.currentFilter = filterName;
						this.render(filterName);
					}
				}, this)
			});
		}

		return this.results;
	},

	fetchMoreData: function () {
		this.collection.currentPage++;
		this.fetchData(this.options.currentFilter);
		$('.loader').remove();
	}
});

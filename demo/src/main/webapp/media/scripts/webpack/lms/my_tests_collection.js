'use strict';

import $ from 'jquery';
import _ from 'underscore';
import Backbone from 'backbone';
import TestModel from './boxes_manage_test_model';

export default Backbone.Collection.extend({
	initialize: function () {
		this.filterNames = {
			'invited': 'INVITATIONS',
			'inProgress': 'IN_PROGRESS',
			'passed': 'PASSED',
			'gradePending': 'GRADE_PENDING',
			'failed': 'FAILED'
		};

		this.currentPage = 1;
		this.resultsPerPage = 20;
		this.totalResults = 0;
	},

	fetchData: function (filterName) {
		var self = this;
		var filter = filterName.replace('Filter', '');
		var filterParam = this.filterNames[filter];

		$.ajax({
			url: '/lms/mytests/' + this.currentPage + '?type=' + filterParam,
			processData: false,
			success: function (response) {
				if (response.successful && response.data.assessments !== 'undefined') {
					if (self.totalResults === 0) {
						self.totalResults = response.data.rowCount;
					}

					_.each(response.data.assessments, function (test) {
						self.add(new TestModel(test));
					});
					self.totalResults = response.data.rowCount;

					Backbone.Events.trigger('myTestsDataLoaded', filter);
				}
			}
		});
	}
});

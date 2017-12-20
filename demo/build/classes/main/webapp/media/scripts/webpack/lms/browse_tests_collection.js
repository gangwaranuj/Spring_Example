'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import TestModel from './boxes_manage_test_model';

export default Backbone.Collection.extend({
	initialize: function () {
		this.model = TestModel;
		this.currentPage = 1;
		this.resultsPerPage = 20;
		this.totalResults = 0;
	},

	fetchData: function () {
		var self = this;

		$.ajax({
			url: '/lms/browse/' + this.currentPage,
			processData: false,
			success: function (response) {
				if (response.successful && response.data.assessments !== 'undefined') {
					if (self.totalResults === 0) {
						self.totalResults = response.data.rowCount;
					}

					_.each(response.data.assessments, function (test) {
						self.add(new TestModel(test));
					});

					if (response.data.assessments.length > 0) {
						Backbone.Events.trigger('browseDataLoaded');
					}
				}
			}
		});
	}
});

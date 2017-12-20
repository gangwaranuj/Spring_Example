'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import TestModel from './boxes_manage_test_model'; 

export default Backbone.Collection.extend({
	fetchData: function (view) {
		this.totalResults = 0;

		$.ajax({
			context: this,
			url: '/lms/recommended',
			processData: false,
			success: _.bind(function (response) {
				if (response.successful && response.data.assessments !== 'undefined') {

					if (this.totalResults === 0) {
						this.totalResults = response.data.rowCount;
					}

					_.each(response.data.assessments, function (test) {
						this.add(new TestModel(test));
					}, this);
					view.render();
				}
			}, this)
		});
	}
});

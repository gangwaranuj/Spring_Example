'use strict';

import Backbone from 'backbone';
import _ from 'underscore';
import AssignmentView from './assignment_view';
import NoResultsView from './no-results_view';

export default Backbone.View.extend({
	el: '#realtime_results',

	initialize: function () {
		this.rows = {};
		this.tmp_rows = {};
		this.update_count = 0;
	},

	render: function () {
		// Clear previous results.
		this.$el.empty();

		// Copy row information for lookup purposes.
		this.tmp_rows = this.rows;
		this.rows = {};

		if (this.model.results_count) {
			// Render rows.
			_.each(this.model.results, this.addOne, this);
		} else {
			// No results found.
			var view = new NoResultsView();
			this.$el.append(view.render().el);
		}

		// Clear copy of rows.
		this.tmp_rows = {};
		this.update_count++;
	},

	addOne: function (assignment, index) {
		var view = new AssignmentView({
			model: assignment,
			index: index,
			parent: this.options.parent
		});
		this.$el.append(view.render().el);

		// Check if this assigned existed in the last load.
		if (typeof this.tmp_rows[assignment.work_number] != 'undefined') {
			// If assignment was previous expanded, expand it again.
			if (this.tmp_rows[assignment.work_number].expanded) {
				view.expandAssignment();
			}
		}

		// Handle delta highlighting.
		if (this.update_count > 0) {
			var old_row = this.tmp_rows[assignment.work_number];
			if (typeof old_row == 'undefined') {
				// This is a new row so highlight the whole thing.
				view.highlight();
			} else {
				if (old_row.model.questions != assignment.questions) {
					view.highlightQuestions();
				}
				if (old_row.model.offers != assignment.offers) {
					view.highlightOffers();
				}
				if (old_row.model.declines != assignment.declines) {
					view.highlightDeclines();
				}
				for (var user_number in assignment.resources) {
					var index = old_row.model.resources_index[user_number];
					if (typeof old_row.model.resources[index] == 'undefined') {
						view.highlightResource(user_number);
					} else {
						var old_resource = old_row.model.resources[index];
						if (!_.isEqual(assignment.resources[index].icons, old_resource.icons)) {
							view.highlightResource(user_number);
						}
					}
				}
			}
		}

		this.rows[assignment.work_number] = view;
	},

	expandAll: function () {
		_.each(this.rows, function (item) {
			item.expandAssignment();
		});
	},

	collapseAll: function () {
		_.each(this.rows, function (item) {
			item.collapseAssignment();
		});
	}
})

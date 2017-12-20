'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import '../dependencies/jquery.tmpl';

export default Backbone.View.extend({
	el: '#assignment_seller_statuses',

	initialize: function () {
		this.statuses = this.options.statuses;

		this.render();
	},

	render: function() {
		this.$el.empty();

		// Iterate through and build out buyer statuses
		if (this.statuses.length > 0) {
			this.$el.append('<li id="status_all_working" class="active"><a href="#status/all/working" title="All"><span class="overflow"> All </span></a></li>');

			var data = '';

			_.each(this.statuses.models, function(item) {
				if (!item.get('count')) return;

				data += $('#tmpl-statuses_list').tmpl(_.extend(item.toJSON(), {type: 'working'})).html();
			});

			this.$el.append(data);

			$('.dragAdd').draggable({
				revert: 'invalid',
				helper: function () {
					var newItem = $(this).clone().addClass('label').addClass('nowrap');

					var labelColour = $(this).children('.label.fr').css('backgroundColor');
					newItem.css({backgroundColor: labelColour});

					newItem.children('.overflow').removeClass('overflow');
					newItem.children('.label').remove();

					return newItem;
				}
			});

			// Display the section.
			var sum = _.reduce(this.statuses.models, function (memo, o) {
				return memo + o.get('count');
			}, 0);

			$('#assignment_seller_statuses_container').toggle(sum > 0);
		}
	}
});

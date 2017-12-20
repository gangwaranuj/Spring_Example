'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';

export default Backbone.View.extend({
	el: '#assignment_buyersubstatuses_container',
	activeContainer: $('#assignment_buyersubstatuses'),
	inactiveContainer: $('#assignment_buyersubstatuses_inactive'),
	moreContainer: $('#assignment_buyersubstatuses_more'),
	events: {
		'click a.cta-toggle-more' : 'toggleMore'
	},

	render: function (options) {
		this.statuses = options.statuses;

		// Iterate through and build out buyer statuses
		$(this.activeContainer).empty();
		$(this.inactiveContainer).empty();

		var activeSubStatuses = '';
		var inactiveSubStatuses = '';

		// Iterate through and build out buyer statuses
		if (this.statuses.length > 0) {
			$(this.activeContainer).append('<li id="substatus_all_managing" class="active"><a href="#substatus/all/managing" title="All"><span class="overflow"> All </span></a></li>');

			_.each(this.statuses.models, function (item) {
				if (item.attributes.dashboard_display_type !== 'HIDE') {
					var sub = $('#tmpl-substatuses_list').tmpl(_.extend(item.toJSON(), {type: 'working'}));

					if (item.attributes.count === 0) {
						if (item.attributes.dashboard_display_type ==='SHOW') {
							inactiveSubStatuses += sub.html();
						}
					} else {
						activeSubStatuses += sub.html();
					}
				}
			});

			if (inactiveSubStatuses === '') {
				$(this.moreContainer).hide();
			} else {
				$('#assignment_buyersubstatuses_container .cta-toggle-more').html('Show Unused');
				$(this.moreContainer).show();
			}

			$(this.activeContainer).append(activeSubStatuses);
			$(this.inactiveContainer).append(inactiveSubStatuses);
			$(this.inactiveContainer).hide();

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
			this.$el.show();
		}
		else {
			this.$el.hide();
		}
	},

	toggleMore: function (e) {
		e.stopImmediatePropagation();

		var el = $(e.currentTarget);
		if (el.html() === 'Show Unused') {
			$(this.inactiveContainer).show();
			el.html('Hide Unused');
		} else {
			$(this.inactiveContainer).hide();
			el.html('Show Unused');
		}
	}
});

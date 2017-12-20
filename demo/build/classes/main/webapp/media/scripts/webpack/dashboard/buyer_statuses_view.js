'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';

export default Backbone.View.extend({
	el: '#assignment_buyer_statuses',
	status_type: 'managing',
	events: {
		'click a.cta-toggle-more' : 'toggleMore'
	},

	initialize: function () {
		// Call model to load statuses
		this.statuses        = this.options.statuses;
		this.sub_menu        = this.options.sub_menu;
		this.activeStatus    = this.options.activeStatus;
		this.activeSubStatus = this.options.activeSubStatus;
		this.maxShow         = 3;

		this.render();
	},

	render: function () {
		this.$el.empty();
		var self = this;

		// Iterate through and build out buyer statuses
		if (this.statuses.length > 0) {
			$(this.el).append('<li id="status_all_managing" class="active"><a href="#status/all/managing" title="All"><span class="overflow"> All </span></a></li>');

			var data = '';

			_.each(this.statuses.models, function(item) {
				if (!item.get('count')) return;

				var submenuData = '';

				if (typeof self.sub_menu[item.id] !== 'undefined' && self.sub_menu[item.id].length > 0) {
					var activeSubItems = 0;

					_.each(self.sub_menu[item.id], function (subitem) {
						// Only include if it has records
						if (!subitem.count) return;

						var sub = $('#tmpl-statuses_list').tmpl(_.extend(subitem, {type: self.status_type, substatus: 1}));

						if (activeSubItems >= self.maxShow) {
							sub.find('li').addClass('dn');
						}

						submenuData += sub.html();
						activeSubItems++;
					});

					if (activeSubItems > self.maxShow) {
						submenuData+= '<li class="toggle_more"><a href="javascript:void(0);" class="cta-toggle-more">';
						submenuData+= 'More...';
						submenuData+= '</a></li>';
					}
				}

				data += $('#tmpl-statuses_list').tmpl(_.extend(item.toJSON(), {type: self.status_type, submenu: submenuData})).html();
				if (submenuData) {
					data += '<div class="ml submenu submenu_'+self.sub_menu[item.id][0].parent+'">';
					data += submenuData;
					data += '</div>';
				}
			});

			$(this.el).append(data);

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
			var sum = _.reduce(this.statuses.models, function(memo, o) { return memo + o.get('count'); }, 0);
			$('#assignment_buyer_statuses_container').toggle(sum > 0);
		}
	},

	toggleMore: function(e) {
		e.stopImmediatePropagation();

		var el = $(e.currentTarget);
		if (el.html() === 'More...') {
			el.closest('.submenu').find('li').removeClass('dn');
			el.html('Less...');
		} else {
			el.closest('.submenu').find('li:not(.toggle_more)').slice(this.maxShow).addClass('dn');
			el.html('More...');
		}
	}
});

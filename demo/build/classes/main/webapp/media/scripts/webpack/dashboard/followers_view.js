'use strict';

import $ from 'jquery';
import _ from 'underscore';
import Backbone from 'backbone';
import wmNotify from '../funcs/wmNotify';
import Template from './templates/follower.hbs';

export default Backbone.View.extend({
	events: {
		'click .remove_follower'       : 'removeFollower',
		'click #new_followers_save'    : 'addFollowers'
	},

	initialize: function (options) {
		this.workNumber = options.workNumber;
		this.openElement = options.e.currentTarget;

		this.followersList = this.$el.find('#followers_list');
		this.newFollowers = this.$('#followers');
		this.followersList.empty();
		this.collection.fetch({
			reset: true,
			success: _.bind(function (response) {
				if (response.isEmpty()) {
					this.followersList.html('Currently no followers. Type a name below to start adding.');
				} else {
					response.each(function (item) {
						this.followersList.append(
							Template({
								id: item.attributes.id,
								workNumber: this.workNumber,
								fullName: item.attributes.fullName
							})
						);
					}, this);
				}
			}, this)
		});
	},

	removeFollower: function(e) {
		e.preventDefault();

		var self = this;

		$.ajax({
			type: 'GET',
			url: $(e.currentTarget).attr('href'),
			dataType: 'json',
			success: function (response) {
				if (response.successful) {
					$(e.currentTarget).closest('.existing_follower').remove();

					if (self.followersList.children().length === 0) {
						$(self.followersList).html('None');

						if (response.data.removed_current_user && self.onCurrentUserStopFollow) {
							self.onCurrentUserStopFollow(self.openElement);
						}
					}
				}
			}
		});
	},

	addFollowers: function () {
		var newFollowers = this.newFollowers.val();

		if (!newFollowers || newFollowers.length === 0) {
			wmNotify({
				message: 'You must add at least one name to save as a follower.',
				type: 'danger'
			});
			return;
		}

		$.ajax({
			context: this,
			type: 'POST',
			url: '/assignments/add_followers/' + this.workNumber,
			data: { followers: newFollowers.join(',') },
			dataType: 'json',
			success: _.bind(function (response) {
				if (response.successful) {
					this.options.modal.destroy();
					wmNotify({
						message: 'Followers of this assignment have been updated.'
					});

					if (response.data.added_current_user && this.onCurrentUserFollow) {
						this.onCurrentUserFollow(this.openElement);
					}
				} else {
					wmNotify({
						message: 'An error while saving.',
						type: 'danger'
					});
				}
			}, this),
			error: function () {
				wmNotify({
					message: 'An error while saving.',
					type: 'danger'
				});
			}
		});
	}
});

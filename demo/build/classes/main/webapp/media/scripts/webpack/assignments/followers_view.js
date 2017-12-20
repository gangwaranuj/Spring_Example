'use strict';

import Template from '../dashboard/templates/follower.hbs';
import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import FollowersCollection from './followers_collection';
import wmNotify from '../funcs/wmNotify';
import wmSelect from '../funcs/wmSelect';

export default Backbone.View.extend({
	el: '#followers_container',
	events: {
		'click .remove_follower'       : 'removeFollower',
		'click #new_followers_save'    : 'addFollowers'
	},

	initialize: function (options) {
		this.followersList = this.$('#followers_list');
		this.newFollowers = this.$('#followers');
		this.collection = new FollowersCollection([],{
			id: this.options.workNumber
		});
		this.render();
	},

	render: function () {
		this.followersList.empty();
		this.collection.fetch({
			success: _.bind(function (response) {
				if (response.isEmpty()) {
					this.followersList.html('Currently no followers. Type a name below to start adding.');
				} else {
					_.each(response.models, function (item) {
						this.followersList.append(
							Template({
								id: item.attributes.id,
								workNumber: this.options.workNumber,
								fullName: item.attributes.fullName
							})
						);
					}, this);
				}
			}, this)
		});

		wmSelect({
			selector: '#followers'
		}, {
			valueField: 'id',
			labelField: 'name',
			searchField: ['id', 'name'],
			sortField: 'name',
			maxItems: null
		});
	},

	removeFollower: function (event) {
		event.preventDefault();

		$.ajax({
			type: 'GET',
			url: $(event.currentTarget).attr('href'),
			dataType: 'json',
			context: this,
			success: function (response) {
				if (response && response.successful) {
					$(event.currentTarget).closest('.existing_follower').remove();

					if (this.followersList.children().length === 0) {
						$(this.followersList).html('None');
					}

					if (response.data.removed_current_user) {
						// update eyeball
						this.parentView.closeFollowEye($('.js-follow'));
					}
				}
			}
		});
	},

	addFollowers: function (event) {
		event.preventDefault();

		var newFollowers = this.newFollowers.val();

		if (!newFollowers || newFollowers.length === 0) {
			wmNotify({
				message: 'You must add at least one name to save as a follower.',
				type: 'danger'
			});
			return;
		}

		$.ajax({
			type: 'POST',
			url: $(event.currentTarget).attr('href'),
			data: { followers: newFollowers.join(',') },
			dataType: 'json',
			context: this,
			success: function (response) {
				if (response && response.successful) {
					this.render();
					// need to reset the dropdown so it removes the users we just added
					if (response.data.added_current_user) {
						// update eyeball
						this.parentView.openFollowEye($('.js-follow'));
					}
				} else {
					wmNotify({
						message: 'An error while saving.',
						type: 'danger'
					});
				}
			},
			error: function () {
				wmNotify({
					message: 'An error while saving.',
					type: 'danger'
				});
			}
		});
	}
});

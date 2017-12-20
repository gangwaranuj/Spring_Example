import $ from 'jquery';
import _ from 'underscore';
import Backbone from 'backbone';
import RatingsView from './ratings_view';
import CommentsView from './comments_view';
import TeamTab from '../profile/team_view';
import GroupsCollection from './groups_collection';
import TestsCollection from './tests_collection';
import wmSelect from '../funcs/wmSelect';
import wmNotify from '../funcs/wmNotify';
import wmTabs from '../funcs/wmTabs';
import VendorProfileModalTemplate from './templates/vendor_profile_modal_content.hbs';
import UserProfileModalTemplate from './templates/user_profile_modal_content.hbs';
import SpinnerTemplate from '../funcs/templates/spinner.hbs';
import jdenticon from '../dependencies/jquery.jdenticon';
import '../dependencies/jquery.bootstrap-collapse';

export default Backbone.View.extend({

	attributes: function () {
		return {
			id: 'profile-' + this.model.get('userNumber')
		};
	},

	className: 'profile-container',

	template: function(data) {
		if (this.model.get('isVendor')) {
			return VendorProfileModalTemplate(data);
		} else {
			return UserProfileModalTemplate(data);
		}
	},

	events: {
		'click .show-full-profile'            : 'showFullProfile',
		'click .create-assignment'            : 'createAssignment',
		'click .groups-quick-action'          : 'loadGroups',
		'click .tests-quick-action'           : 'loadTests',
		'click .tags-quick-action'            : 'loadTags',
		'click .assignment-quick-action'      : 'goToSendAssignment',
		'click .ratings-tab'                  : 'showRatings',
		'click .comments-tab'                 : 'showComments',
		'click .profile--quick-actions-close' : 'closeQuickActions',
		'click .profile--invite-group'        : 'pushToGroup',
		'click .profile--invite-test'         : 'pushToTest',
		'click .profile--add-comment'         : 'pushComment',
		'click .profile--add-tag'             : 'pushTag',
		'click .profile--view-profile'        : 'showProfile',
		'click #show-ratings'                 : 'openRatings',
		'change [name="score-card-toggle"]'   : 'toggleScoreCard',
		'click .esignature-link'              : 'linkToEsignature'
	},

	initialize: function (options) {
		this.root = $(options.root);
		this.listenTo(this.model, 'change', this.renderContent);
		this.render();
		this.model.fetch();
	},

	render: function () {
		this.root.append(this.$el.html(SpinnerTemplate()));
		this.spinner = this.$('.profile-spinner');
	},

	renderContent: function () {
		this.spinner.remove();
		this.root.append(this.$el.html(this.template(this.model.toJSON())));
		this.setUpJdenticon();
		this.setUpToggles();

		new TeamTab({
			el: this.$('.team-tab'),
			companyNumber: this.model.get('facade').companyNumber,
			companyId: this.model.get('facade').companyId
		});

		wmTabs({ root: this.el });
	},

	toggleScoreCard: function (event) {
		var isAllValues = $(event.currentTarget).val() === 'all';
		this.$('.score-card').toggleClass('-company', !isAllValues);
	},

	showRatings: function () {
		if (!this.ratingsLoaded) {
			var userNumber = this.model.get('userNumber'),
				el = '#user-ratings-' + userNumber;
			new RatingsView({ el: el, userNumber: userNumber });
			this.ratingsLoaded = true;
		}
	},

	openRatings: function () {
		this.$('.ratings-tab').click();
	},

	showComments: function () {
		if (!this.commentsLoaded) {
			var userNumber = this.model.get('userNumber'),
				el = '#user-comments-' + userNumber;
			new CommentsView({ el: el, userNumber: userNumber });
			this.commentsLoaded = true;
		}
	},

	setUpToggles: function () {
		this.$toggles = this.$('.profile--quick-actions .switch--checkbox');
		$('#collapse-' + this.model.get('userNumber')).on('show', _.bind(function (event) {
			this.$toggles.prop('checked', false);
			this.$('[href="#' + event.target.id+'"]').prev().click();
			this.$el.addClass('drawer-open');
		}, this));
		$('#collapse-' + this.model.get('userNumber')).on('shown', _.bind(function (event) {
			this.$('#' + event.target.id).css({ overflow: 'visible' });
		}, this));
		$('#collapse-' + this.model.get('userNumber')).on('hide', _.bind(function (event) {
			this.$('#' + event.target.id).css({ overflow: 'hidden' });
			this.$('[href="#' + event.target.id+'"]').prev().click();
			this.$el.removeClass('drawer-open');
		}, this));
	},

	setUpJdenticon: function () {
		if (!this.model.get('avatarLargeAssetUri')) {
			jdenticon();
		}
	},

	createAssignment: function (event) {
		window.location = $(event.currentTarget).data('href');
	},

	loadGroups: function () {
		if (!this.groups) {
			var groups = new GroupsCollection(),
				self = this;
			groups.fetch().then(function (response) {
				if (response.data.groups.length) {
					var render = {
						render: {
							option: function (data, escape) {
								return '<div class="option" data-privacy=' + escape(data.isPublic) + '>' + escape(data.name) + '</div>';
							}
						}
					};
					self.setUpSelectize(response.data.groups, 'group', render);
				} else {
					wmNotify({
						message: 'You have yet to create a talent pool yet. Please create one to use this feature.',
						type: 'danger'
					});
				}
			});
		} else {
			this.setUpSelectize(this.groups);
		}
		this.$el.off('click', '.groups-quick-action');
	},

	loadTests: function () {
		if (!this.tests) {
			var tests = new TestsCollection(),
				self = this;
			tests.fetch().then(function (response) {
				self.setUpSelectize(response, 'test');
			});
		}
		this.$el.off('click', '.tests-quick-action');
	},

	loadTags: function () {
		this.$('.tags-input').selectize({
			placeholder: 'Select or type to choose...',
			create: function(input) {
				return {
					value: input,
					text: input
				}
			}
		});
		this.$el.off('click', '.tags-quick-action');
	},

	linkToEsignature: function (event) {
		$.ajax({
			context: this,
			async: false,
			url: '/v2/esignature/get_signed',
			type: 'GET',
			data: {
				userNumber: this.model.get('userNumber'),
				templateUuid: event.target.dataset.templateuuid
			},
			dataType: 'json',
			success: function(response) {
				event.target.href = response.results[0].executedUrl;
			},
			error: function () {
				e.preventDefault();
			}
		});
	},

	goToSendAssignment: function (event) {
		event.preventDefault();
		const loadAssignmentCreationModal = async() => {
			const module = await import(/* webpackChunkName: "newAssignmentCreation" */ '../assignments/creation_modal');
			return module.default;
		};
		loadAssignmentCreationModal().then(CreationModal => new CreationModal());
	},

	setUpSelectize: function (data, type, render) {
		var $select = this.$('.' + type + '-select'),
			options = {
				valueField: 'id',
				labelField: 'name',
				placeholder: 'Select or type to choose...',
				searchField: ['id', 'name'],
				options: data || []
			};
		if (!!render) {
			_.extend(options, render);
		}
		wmSelect({ selector: $select }, options);
	},

	closeQuickActions: function () {
		this.$('.switch--checkbox:checked').next().click();
	},

	pushToGroup: function () {
		var $groupControl = this.$('select.group-select');
		if (!!$groupControl.val()) {
			$.ajax({
				context: this,
				url: '/groups//invite_workers/' + $groupControl.val(),
				type: 'POST',
				data: {
					selected_workers: [this.model.get('userNumber')]
				},
				dataType: 'json',
				success: function(response) {
					this.quickActionCallback(response);
					$groupControl[0].selectize.clear()
				}
			});
		} else {
			wmNotify({
				message: 'Please select a group.',
				type: 'danger'
			});
		}
	},

	pushToTest: function () {
		var $testControl = this.$('select.test-select');
		if (!!$testControl.val()) {
			$.ajax({
				url: '/search/cart/push_to_test',
				type: 'POST',
				data: {
					'id': $testControl.val(),
					'selected_workers': [this.model.get('userNumber')]
				},
				dataType: 'json',
				success: _.bind(function(response) {
					this.quickActionCallback(response);
					$testControl[0].selectize.clear();
				}, this)
			});
		} else {
			wmNotify({
				message: 'Please select a test.',
				type: 'danger'
			});
		}
	},

	pushComment: function () {
		var $commentControl = this.$('.comment-input');

		if (!!$commentControl.val()) {
			$.ajax({
				url: '/profile/add_comment_to_user',
				type: 'POST',
				data: {
					'id': this.model.get('userNumber'),
					'comment': $commentControl.val()
				},
				dataType: 'json',
				success: _.bind(function(response) {
					this.quickActionCallback(response);
					$commentControl.val('');
				}, this)
			});
		} else {
			wmNotify({
				message: 'Please add a comment before submitting.',
				type: 'danger'
			});
		}
	},

	pushTag: function () {
		var $tagControl = this.$('input.tags-input');
		if (!!$tagControl.val()) {
			$.ajax({
				url: '/tags/tag_user',
				type: 'POST',
				data: {
					resource_id: this.model.get('facade').id,
					'tags_list[tags]': $tagControl.val().split(',')
				},
				dataType: 'json',
				success: _.bind(function(response) {
					this.quickActionCallback(response);
					$tagControl.val('');
				}, this)
			});
		} else {
			wmNotify({
				message: 'Please add a comment before submitting.',
				type: 'danger'
			});
		}
	},

	quickActionCallback: function (response) {
		if (response.successful) {
			wmNotify({message: response.messages[0]});
			this.closeQuickActions()
		} else {
			wmNotify({
				message: response.messages[0] || 'There was an error with your invite. Please try again.',
				type: 'danger'
			});
		}
	}
});

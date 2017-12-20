import Backbone from 'backbone';
import $ from 'jquery';
import _ from 'underscore';
import AddNoteView from './add_note_view';
import PaymentView from './payment_view';
import AssignmentRowView from './assignment_row_view';
import FollowersView from './followers_view';
import PaymentModel from './payment_model';
import FollowersCollection from './followers_view';
import wmNotify from '../funcs/wmNotify';
import wmModal from '../funcs/wmModal';
import wmSelect from '../funcs/wmSelect';
import SingleNoteAddTemplate from './templates/single_note_add.hbs';
import NoResultsClientTemplate from './templates/no_results.hbs';
import NoResultsWorkerTemplate from './templates/no_results_worker.hbs';

export default Backbone.View.extend({
	el: '#assignment_list_results',
	className: 'results-list',
	events: {
		'mouseover .results-row': 'rowOver',
		'mouseout .results-row': 'rowOut',
		'click .add_note_action': 'showAddNoteModal',
		'click .view_line_item_modal': 'showLineItemModal',
		'click .show_payment_details_action': 'showPaymentDetails',
		'click .payment_button_hide_action': 'hidePaymentDetails',
		'click .sendback_action': 'showLineItemModal',
		'click a.profile_link': 'showProfile',
		'click .add_followers_action': 'showAddFollowersModal',
		'click .js-dashboard-invite-more-workers': 'dashboardInviteMoreWorkers'
	},

	initialize () {
		this.render();
		const $includeTime = $('#include_time');
		if ($includeTime.prop('checked')) {
			$includeTime.removeAttr('checked');
		}
		$('#assignment_results_container').show();
		$('#calendar').hide();
		$('.bracket').show();
		$('#dashboard_filter_title').show();
		$('#show_list').addClass('active');
		$('#show_calendar').removeClass('active');
	},

	render () {
		// Clear previous results.
		this.$el.empty();
		if (this.collection.length > 0) {
			_.each(this.collection.models, this.addOneLine, this);
		} else if (this.options.variables.isBuyer) {
			this.$el.append(NoResultsClientTemplate);
		} else {
			this.$el.append(NoResultsWorkerTemplate);
		}
	},

	actionApprovePay () {
		this.trigger('payment:approve_payment');
	},

	addOneLine (assignment, index) {
		const view = new AssignmentRowView(_.extend({ model: assignment }, { index }, { variables: this.variables }));
		const self = this;
		view.bind('payment:actions_approve_pay', () => { self.actionApprovePay(); });
		this.$el.append(view.render().el);
	},

	rowOver (e) {
		$(e.currentTarget).addClass('active').siblings().removeClass('active');
	},

	rowOut (e) {
		$(e.currentTarget).removeClass('active');
	},

	showLineItemModal (event) {
		event.preventDefault();

		$.ajax({
			type: 'GET',
			url: $(event.currentTarget).attr('href'),
			context: this
		}).done((response) => {
			if (!_.isEmpty(response)) {
				wmModal({
					autorun: true,
					title: $(event.currentTarget).data('title'),
					destroyOnClose: true,
					content: response
				});
			}
		}).fail(() => {
			wmNotify({
				type: 'danger',
				message: 'There was an error fetching this modal. Please try again'
			});
		});
	},

	showAddNoteModal (e) {
		this.modal = wmModal({
			autorun: true,
			title: 'Add Note',
			destroyOnClose: true,
			content: SingleNoteAddTemplate()
		});

		new AddNoteView({
			modal: this.modal,
			assignmentId: $(e.currentTarget).closest('.results-row .assignment-actions').data('assignmentNumber')
		});
	},

	showPaymentDetails (e) {
		// Grab the assignment ID
		const assignmentId = $(e.currentTarget).closest('.results-row .assignment-actions').data('assignmentNumber');

		// Grab payment details
		const options = {
			id: assignmentId,
			isNotAuthorizedForPayment: this.options.variables.isNotAuthorizedForPayment
		};

		const actionsContainer = $(e.currentTarget).closest('.assignment-actions');
		const paymentInfoContainer = $(e.currentTarget).closest('.results-row').find('.payment_info_container');

		const pricing = new PaymentModel(options);
		pricing.fetch({
			success () {
				const assignmentPaymentRow = new PaymentView({ model: pricing });
				$(paymentInfoContainer).html(assignmentPaymentRow.render().el);
			},
			error () {
				const assignmentPaymentRow = new PaymentView({ error: true });
				$(paymentInfoContainer).html(assignmentPaymentRow.render().el);
			}
		});

		// Display the payment container
		$(paymentInfoContainer).show();
		// Hide the View for payment
		$(actionsContainer).find('.show_payment_details_action').hide();
		// Show hide details
		$(actionsContainer).find('.payment_button_hide_action').show();
	},

	hidePaymentDetails (e) {
		const actionsContainer = $(e.currentTarget).closest('.assignment-actions');
		const paymentInfoContainer = $(e.currentTarget).closest('.results-row').find('.payment_info_container');

		// Hide the payment container
		$(paymentInfoContainer).hide();
		// Show the View for payment
		$(actionsContainer).find('.show_payment_details_action').show();
		// Hide details
		$(actionsContainer).find('.payment_button_hide_action').hide();
	},

	showProfile (e) {
		e.preventDefault();
		const $profileBody = $('.profile-body');
		const $profilePopup = $('#user-profile-popup');

		$profileBody.empty();
		$profilePopup.modal('show');
		$profilePopup.find('.profile-spinner').show();
		$.get(`${$(e.currentTarget).attr('href')}?popup=1`, (result) => {
			$profilePopup.find('.profile-spinner').hide();
			$profileBody.html(result);
		});
	},

	dashboardInviteMoreWorkers () {
		analytics.track('Assignments Dashboard', {
			action: 'Invite Workers Clicked',
			source: 'dashboard'
		});
	},

	showAddFollowersModal (e) {
		let workNumber = $(e.currentTarget).closest('.results-row .assignment-actions').data('assignmentNumber'),
			container = '#followers_container',
			modal = '.wm-modal--content';

		this.modal = wmModal({
			autorun: true,
			title: 'Add Followers',
			destroyOnClose: true,
			content: $(container).parent().html()
		});

		wmSelect({
			selector: '#followers',
			root: '.wm-modal--content'
		}, {
			valueField: 'id',
			labelField: 'name',
			searchField: ['id', 'name'],
			sortField: 'name',
			maxItems: null
		});

		this.followersView = new FollowersView({
			el: $('.wm-modal--content').find(container),
			collection: new FollowersCollection([], { id: workNumber }),
			workNumber,
			e,
			modal: this.modal
		});

		// callbacks for keeping the toggle follow in sync
		this.followersView.onCurrentUserFollow = function (openElement) {
			$(openElement).closest('.results-row').find('.follow').addClass('follow-true');
		};

		this.followersView.onCurrentUserStopFollow = function (openElement) {
			$(openElement).closest('.results-row').find('.follow').removeClass('follow-true');
		};
	}
});

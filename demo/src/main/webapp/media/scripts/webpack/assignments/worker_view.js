

import ProfileCardTemplate from '../profile/templates/profile-card.hbs';
import AcceptApplyModalTemplate from '../assignments/templates/details/dispatcher/accept-apply_modal.hbs';
import WorkersTemplate from '../assignments/templates/details/workers.hbs';
import ConfirmActionTemplate from '../funcs/templates/confirmAction.hbs';
import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import WorkerActionsView from './worker_actions_view';
import CancelNegotiationModel from './cancel_negotiation_model';
import PaginationView from '../pagination/pagination_view';
import wmNotify from '../funcs/wmNotify';
import wmModal from '../funcs/wmModal';
import wmSelect from '../funcs/wmSelect';
import wmAlert from '../funcs/wmAlert';
import getCSRFToken from '../funcs/getCSRFToken';
import jdenticon from '../dependencies/jquery.jdenticon';

export default Backbone.View.extend({
	el: '#workers-bucket',
	template: ProfileCardTemplate,
	events: {
		'click .rating_toggle': 'toggleRatings',
		'click .worker-link': 'showActions',
		'click [data-behavior="remove-label"]': 'removeLabelModal',
		'click [data-behavior="remove-auto"]': 'removeAutoAssign',
		'click #feedback': 'toggleRatingForm',
		'click #edit-feedback': 'editFeedback',
		'click #submit-feedback': 'submitFeedback',
		'click .-accept-invitation': 'dispatcherAcceptInvitation',
		'click .-cancel-application': 'dispatcherCancelApplication',
		'click .-send-application': 'dispatcherSendApplication',
		'change [name="sortColumn"]': 'refetchOnSortColumnChange',
		'change [name="sortDirection"]': 'refetch',
		'change [name*="score-card-toggle"]': 'toggleScoreCard',
		'change .assignment-workers--filter-number': 'changeLimit',
		'click .vendor-decline': 'sendVendorDecline',
		'click .work-notify': 'sendWorkNotify',
		'change [name="pageSize"]': 'changeLimit'
	},

	initialize () {
		this.renderContainerTemplate();
		_.bindAll(this, 'render');

		this.workerActions = new WorkerActionsView({
			successCallback () {
				window.location.reload();
			}
		});

		this.pagination = new PaginationView({
			el: this.$el,
			limit: 50
		});

		wmSelect({ root: this.el });

		this.pagination.bind('pagination:next', this.refetch, this);
		this.pagination.bind('pagination:previous', this.refetch, this);
		this.pagination.bind('pagination:limit_changed', this.refetch, this);

		this.collection.bind('reset', this.render);
		this.refetch();
	},

	render () {
		const $container = this.$('.assignment-workers--feed');

		if (this.collection.totalLength) {
			$container.empty();
			this.$('.limit-container').toggle(this.collection.totalLength > 10);
		} else {
			if (this.options.isDispatcher) {
				$container.html('You have not added any candidates.');
			} else {
				$container.html('There are 0 workers. Please invite more to see workers here.');
			}
			this.$('.wm-pagination').hide();
		}

		this.collection.each(function (item) {
			const data = item.toJSON();
			data.scoreCardData = {
				showrecent: true,
				values: this.scoreCardParse({
					allScorecard: data.resource_scorecard,
					companyScorecard: data.resource_scorecard_for_company,
					paidassignforcompany: 0
				}).scoreCard
			};
			data.assignment = {
				workNumber: this.options.collection.id,
				status: this.options.collection.status,
				disablePriceNegotiation: this.options.collection.disablePriceNegotiation,
				isDeputy: this.options.collection.isDeputy,
				isIndividualBundledAssignment: this.options.collection.isIndividualBundledAssignment,
				isBuyerAuthorizedToApproveCounter: this.options.collection.isBuyerAuthorizedToApproveCounter,
				isParentBundle: this.options.collection.isParentBundle,
				isDispatcher: this.options.isDispatcher,
				isBlocked: data.blocked, 
				isAdmin: this.options.isAdmin,
				assignToFirstResource: this.options.assignToFirstResource,
				accountPricingType: this.options.assignment && this.options.assignment.accountPricingType
			};
			data.disableBulkActions = this.options.disableBulkActions;
			data.resourceType = this.options.resourceType;
			data.number = this.options.resourceType == 'vendors' ? data.companyNumber : data.user_number;
			if (this.options.resourceType === 'vendors') {
				data.name = data.company_name;
			}
			if (this.options.collection.status !== 'active') {
				data.assignmentIsNotActive = true;
			}
			this.$('.assignment-workers--feed').append(this.template(data));
		}, this);

		this.pagination.setTotal(this.collection.totalLength);
		this.pagination.render();
		jdenticon();

		return this;
	},

	changeLimit () {
		const resultsPerPage = this.$('[name="pageSize"]').val();
		this.pagination.start = 0;
		this.pagination.limit = parseInt(resultsPerPage, 10);
		// Trigger change event.
		this.pagination.trigger('pagination:limit_changed');
	},

	renderContainerTemplate () {
		const containerTemplateData = {
			work: this.options.assignment,
			hasInvitedAtLeastOneVendor: this.options.hasInvitedAtLeastOneVendor,
			showActions: this.options.showActions,
			showAssignButton: this.options.showAssignButton,
			isSent: this.options.isSent,
			showVendorActions: this.options.showVendorActions,
			showBundleActions: this.options.showBundleActions,
			isDispatcher: this.options.isDispatcher,
			bundleTitle: this.options.bundleTitle,
			bundleId: this.options.bundleId,
			showSort: this.options.showSort
		};
		this.$el.html(WorkersTemplate(containerTemplateData));
	},

	// Copied from app/profile/profile_model
	// Delete when this file goes to RequireJS
	scoreCardParse (response) {
		// Compile some score card data from the controller payload
		response.scoreCard = {
			abandoned: getValues('ABANDONED_WORK'),
			cancelled: getValues('CANCELLED_WORK'),
			paidAssignments: getValues('COMPLETED_WORK'),
			deliverables: getValues('DELIVERABLE_ON_TIME_PERCENTAGE'),
			onTime: getValues('ON_TIME_PERCENTAGE'),
			satisfaction: getValues('SATISFACTION_OVER_ALL'),
			paidAssignmentsForCompany: response.paidassignforcompany
		};

		// Satisfaction is returned to us as a decimal, we need it as a percentage
		_.chain(response.scoreCard)
			.pick('satisfaction', 'onTime', 'deliverables')
			.each((value) => {
				value.all.all = Math.round(value.all.all * 100);
				value.all.net90 = Math.round(value.all.net90 * 100);
				value.company.all = Math.round(value.company.all * 100);
				value.company.net90 = Math.round(value.company.net90 * 100);
			});

		return response;

		function getValues (property) {
			return {
				all: _.pick(response.allScorecard.values[property], 'all', 'net90'),
				company: _.pick(response.companyScorecard.values[property], 'all', 'net90')
			};
		}
	},

	toggleScoreCard (event) {
		let $target = this.$(event.currentTarget),
			isAllValues = $target.val() === 'all';
		$target.parent().parent().parent().toggleClass('-company', !isAllValues);
	},

	refetchOnSortColumnChange () {
		const selectize = this.$('[name=sortColumn]')[0].selectize;
		const direction = selectize.options[selectize.getValue()]['default-direction'];

		if (typeof direction !== 'undefined') {
			this.$('[name="sortDirection"]')[0].selectize.setValue(direction, true);
		}

		this.refetch();
	},

	refetch () {
		this.collection.fetch({
			data: {
				start: this.pagination.getStart(),
				limit: this.pagination.getLimit(),
				sortColumn: this.getSortColumn(),
				sortDirection: this.getSortDirection()
			}
		});
	},

	toggleRatings (event) {
		let $button = $(event.target),
			val = $button.val(),
			scorecard = $button.parents('.scorecard');

		$button.addClass('active').siblings().removeClass('active');
		scorecard.find('.ratings span').hide().filter(`[ref=${val}]`).show();
		scorecard.find('table').hide().filter(`[ref=${val}]`).show();
	},

	showActions (event) {
		let userNumber = $(event.target).data('usernumber').substring(1),
			worker = this.collection.findWhere({ user_number: userNumber });

		// Render the resource actions.
		this.workerActions.render({
			assignment: {
				work_number: this.collection.id
			},
			worker: worker.attributes
		}, event);
	},

	removeAutoAssign (e) {
		e.preventDefault();
		const link = $(e.target).closest('a');
		$.post($(link).attr('href'), _.bind(function (result) {
			if (result.success) {
				this.redirect(location.pathname, [result.success], 'success');
			} else {
				wmNotify({
					type: 'danger',
					message: result.error
				});
			}
		}, this), 'json');
	},

	removeLabelModal (event) {
		event.preventDefault();

		const link = $(event.target).closest('a');
		$.ajax({
			type: 'GET',
			url: $(link).attr('href'),
			context: this,
			success (response) {
				if (!_.isEmpty(response)) {
					wmModal({
						autorun: true,
						title: 'Remove Issue',
						destroyOnClose: true,
						content: response
					});
				}
			}
		});
	},

	toggleRatingForm () {
		if (this.$('#feedback-form').hasClass('dn')) {
			this.$('#feedback-form').show();
			this.$('#feedback').hide();
		}
	},

	editFeedback () {
		this.$('#feedback-result').hide();
		this.$('#feedback-form').show();
	},

	submitFeedback (event) {
		const formFields = $('#feedback_form').serializeArray();
		const button = $(event.target);

		if (button.hasClass('disabled')) {
			return;
		}
		button.button('saving');

		$.ajax({
			url: `/assignments/submit_feedback/${this.collection.id}`,
			type: 'POST',
			data: formFields,
			dataType: 'json',
			context: this,
			success (data) {
				if (data && data.successful) {
					location.reload();
				} else {
					_.each(data.messages, (theMessage) => {
						wmNotify({
							message: theMessage,
							type: 'danger'
						});
					});
				}
			}
		});
	},

	getSortColumn () {
		return this.$('[name="sortColumn"]').val();
	},

	getSortDirection () {
		return this.$('[name="sortDirection"]').val();
	},

	dispatcherAcceptInvitation (event) {
		const userNumber = $(event.currentTarget).data('id');
		Backbone.Events.trigger('cleanNegotiationView');
		wmModal({
			autorun: true,
			title: ' ',
			destroyOnClose: true,
			content: AcceptApplyModalTemplate({
				mode: 'accept',
				companyName: this.options.companyName,
				currentUserCompanyName: this.options.currentUserCompanyName,
				paymentTime: this.options.paymentTime,
				pricingType: this.options.pricingType,
				disablePriceNegotiation: this.collection.disablePriceNegotiation,
				isParentBundle: this.collection.isParentBundle,
				work: this.options.assignment,
				userNumber
			})
		});
	},

	dispatcherSendApplication (event) {
		const userNumber = $(event.currentTarget).data('id');
		Backbone.Events.trigger('cleanNegotiationView');
		this.applicationModal = wmModal({
			autorun: true,
			title: ' ',
			destroyOnClose: true,
			content: AcceptApplyModalTemplate({
				mode: 'apply',
				companyName: this.options.companyName,
				currentUserCompanyName: this.options.currentUserCompanyName,
				paymentTime: this.options.paymentTime,
				pricingType: this.options.pricingType,
				disablePriceNegotiation: this.collection.disablePriceNegotiation,
				isParentBundle: this.collection.isParentBundle,
				work: this.options.assignment,
				userNumber
			})
		});
	},

	dispatcherCancelApplication (event) {
		let $action = $(event.currentTarget),
			negotiation = new CancelNegotiationModel({
				workId: $action.data('workId')
			});
		negotiation.fetch({
			data: {
				id: $action.data('id'),
				workerNumber: $action.data('worker-id')
			},
			success () {
				location.reload();
			}
		});
	},

	sendVendorDecline () {
		const modalTitle = this.collection.isParentBundle ? 'Decline Bundle' : 'Decline Assignment';
		const workType = this.collection.isParentBundle ? 'assignment bundle' : 'assignment';
		this.confirmModal = wmModal({
			autorun: true,
			title: modalTitle,
			destroyOnClose: true,
			content: ConfirmActionTemplate({
				message: `Are you sure you want to decline this ${workType}?`
			})
		});

		$('.cta-confirm-yes').on('click', _.bind(function () {
			$.post(`/assignments/vendor/reject/${this.options.assignment.workNumber}`, _.bind(function (result) {
				if (result.success) {
					this.redirect('/assignments', [result.success], 'success');
				} else {
					wmNotify({
						type: 'danger',
						message: result.error
					});
				}
				$('[data-tabs] a[href="#overview"]').trigger('click');
			}, this), 'json');
			this.confirmModal.hide();
		}, this));
	},

	redirect (url, msg, type) {
		if (msg) {
			const e = $("<form class='dn'></form>");
			e.attr({
				action: '/message/create',
				method: 'POST'
			});
			if (typeof msg === 'string') { msg = [msg]; }
			for (let i = 0; i < msg.length; i++) {
				e.append(
					$('<input>').attr({
						name: 'message[]',
						value: msg[i]
					}));
			}
			e.append(
				$('<input>').attr({
					name: 'type',
					value: type
				}));
			e.append(
				$('<input>').attr({
					name: 'url',
					value: url
				}));
			e.append(
				$('<input>').attr({
					name: '_tk',
					value: getCSRFToken()
				}));
			$('body').append(e);
			e.submit();
		} else {
			window.location = url;
		}
	},

	sendWorkNotify () {
		$.post(`/assignments/workNotify/${this.options.assignment.workNumber}`, (result) => {
			if (result.success) {
				wmAlert({
					type: 'success',
					message: result.success
				});
			} else {
				wmAlert({
					type: 'danger',
					message: result.error
				});
			}
			$('[data-tabs] a[href="#overview"]').trigger('click');
		}, 'json');
	}
});

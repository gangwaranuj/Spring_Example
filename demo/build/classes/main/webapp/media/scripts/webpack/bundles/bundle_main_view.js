'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import WorkerListCollection from '../assignments/worker_list_collection';
import AssignmentBundleCollection from '../bundles/view_assignment_bundle_collection';
import AcceptAssignmentModel from '../assignments/accept_assignment_model';
import ApplyAssignmentModel from '../assignments/apply_assignment_model';
import NegotiationModel from '../assignments/negotiation_model';
import BlockClientModel from '../assignments/block_client_model';
import WorkerListView from '../assignments/worker_view';
import NegotiateView from '../assignments/negotiations_view';
import NotesView from '../assignments/notes_view';
import BundleView from './view_bundle_view';
import wmNotify from '../funcs/wmNotify';
import wmModal from '../funcs/wmModal';
import wmTabs from '../funcs/wmTabs';
import getCSRFToken from '../funcs/getCSRFToken';
import AssignmentRouting from '../routing/main';
import '../dependencies/jquery.serializeObject';
import BlockCompanyTemplate from '../assignments/templates/details/dispatcher/block-company.hbs';
import ConfirmActionTemplate from '../funcs/templates/confirmAction.hbs';
import colorbox from 'jquery-colorbox';

export default Backbone.View.extend({
	el: 'body',

	events: function() {
		var agent = navigator.userAgent.toLowerCase(),
			clickOrTouch = agent.match(/ip(hone|od|ad)/i) || agent.match(/android/i) ? 'touchend' : 'click';

		var ret = {
			'click .ask_question_action'          : 'showModal',
			'click .resource-apply-toggle'        : 'toggleResourceApply',
			'click #dispatcher-block-client'      : 'dispatcherBlockClient',
			'click .dispatcher-show-block-client' : 'dispatcherShowBlockClient',
			'click #dispatcher-apply'             : 'dispatcherApply',
			'click #dispatcher-accept'            : 'dispatcherAccept',
			'click .show-vendors'                 : 'showVendorsTab',
			'click .show-workers'                 : 'showWorkersTab',
			'click .worker-decline'               : 'sendWorkerDecline',
			'click [rel="prompt_decline_negotiation"]'  : 'promptDeclineNegotiation'
		};

		ret[clickOrTouch + ' .resend_invite']                        = 'resendWorkerInvitation';

		return ret;
	},

	initialize: function () {
		this.loadWorkers();
		if (this.options.hasInvitedAtLeastOneVendor && !this.options.isDispatcher) {
			this.$('#vendors-bucket').hide();
			this.loadVendors();
			this.vendors.collection.on('reset', function () {
				$('.vendor-count').text(this.vendors.collection.totalLength);
			}.bind(this));
			this.workers.collection.on('reset', function () {
				$('.worker-count').text(this.workers.collection.totalLength);
			}.bind(this));
		}
		this.bundle = new BundleView({
			bundleParentId: this.options.bundleParentId,
			workNumber: this.options.workNumber,
			isEligibleToTakeAction: this.options.isEligibleToTakeAction,
			isWorkActive: this.options.isWorkActive,
			collection: new AssignmentBundleCollection([], {
				parentId: this.options.bundleParentId
			})
		});
		this.notes = new NotesView();
		var questionDiv = $('.question-div');
		if (questionDiv.length) {
			_.each(questionDiv, function (i) {
				if (!_.isUndefined(i.dataset.id)) {
					$.ajax({
						url: '/user/' + this.isValidUserNumber(i.dataset.id),
						type: 'GET',
						dataType: 'json',
						success: function (data) {
							$('.questioner-' + i.dataset.id).html(data.firstName + ' ' + data.lastName + ' ');
						}
					});
				}
				if (!_.isUndefined(i.answererId)) {
					$.ajax({
						url: '/user/' + this.isValidUserNumber(i.answererId),
						type: 'GET',
						dataType: 'JSON',
						success: function (data) {
							$('.answerer-' + i.answererId).html(data.firstName + ' ' + data.lastName + ' ');
						}
					});
				}
			});
		}
		wmTabs();

		$.get('/assignments/batch_send/routable_groups', (result) => {
			let properties = {
				routableGroups: result,
				isBundle: true
			};
			let workNumber = this.options.workNumber;
			let el = '.routing-bucket';
			this.routing = new AssignmentRouting.Main({ properties, workNumber, el });
			$('#submit-form').on('click', (event) => {
				event.preventDefault();
				this.getRoutingAndSave();
			});
		}, 'json');
	},

	isValidUserNumber: function (data) {
		return _.reduce(_.range(7 - (data += '').length), function (memo) { return '0' + memo; }, data);
	},

	toggleResourceApply: function (e) {
		var form = $(e.currentTarget).closest('form');
		var config = form.find('[ref=configuration]');

		config.toggle();
		$('.toggler').toggleClass('toggled');

		if (config.is(':visible') && !this.applyView) {
			this.applyView = new NegotiateView({'el': '#apply-form'});
		}
	},

	dispatcherShowBlockClient: function (event) {
		event.preventDefault();
		this.dispatcherBlockClientModal = wmModal({
			autorun: true,
			title: ' ',
			destroyOnClose: true,
			root: '#apply-form',
			content: BlockCompanyTemplate()
		});
	},

	dispatcherBlockClient: function (event) {
		event.preventDefault();
		var blockClient = new BlockClientModel({
			workId: this.model.workNumber
		});
		blockClient.save();
		this.dispatcherBlockClientModal.destroy();
	},

	dispatcherApply: function (event) {
		event.preventDefault();
		this.applyFormSubmit();
		var data = $('#apply-form').serializeObject();
		var application = new ApplyAssignmentModel(_.extend(data, {
			workId: this.model.workNumber
		}));
		application.save({}, {
			success: _.bind(function(response) {
				if (response.attributes.successful) {
					wmNotify({ message: response.attributes.messages});
				} else {
					wmNotify({
						message: response.attributes.messages,
						type: 'danger'
					});
				}
				this.workers.applicationModal.hide();
			}, this)
		});
	},

	dispatcherAccept: function (event) {
		event.preventDefault();

		if (this.counteroffer) {
			this.applyFormSubmit();
			var data = $('#apply-form').serializeObject();
			var counteroffer = new NegotiationModel(_.extend(data, {
				workId: this.model.workNumber
			}));
			counteroffer.save({}, {
				success: function () {
					location.reload();
				}
			});
		} else {
			var accept = new AcceptAssignmentModel({
				workId: this.model.workNumber,
				workerNumber: parseInt(this.$('#workerNumber').val(), 10)
			});
			accept.save({}, {
				success: function () {
					location.reload();
				}
			});
		}
	},

	applyFormSubmit : function () {
		if ($.browser.msie) {
			$('input[placeholder="Select Date"]').each(function () {
				var input = this;
				if (input.value === 'Select Date'){
					input.value='';
				}
			});

			$('input[placeholder="Select Time"]').each(function () {
				var input = this;
				if (input.value === 'Select Time'){
					input.value='';
				}
			});
		}
	},

	promptDeclineNegotiation: function promptDeclineNegotiation(e) {
		e.preventDefault();

		var button = $(e.target);

		$.colorbox({
			inline: true,
			href: '#decline_negotiation',
			title: 'Decline Negotiation',
			transition: 'none',
			innerWidth: 500,
			onOpen: function onOpen() {
				$('#decline_negotiation_id').val(button.attr('data-negotiation-id'));
				$('#decline_negotiation_note').val('');

				$('#decline_negotiation_cancel').on('click', function () {
					$.colorbox.close();
				});
			}
		});
	},

	loadWorkers: _.once(function () {
		this.workers = new WorkerListView({
			collection: new WorkerListCollection([], {
				id: $('#parentWorkNumber').val(),
				status: this.model.status.code,
				disablePriceNegotiation: this.model.configuration.disablePriceNegotiation,
				isBuyerAuthorizedToApproveCounter: this.options.auth.isBuyerAuthorizedToApproveCounter,
				isDeputy: this.options.auth.isDeputy,
				isIndividualBundledAssignment: this.options.auth.isIndividualBundledAssignment,
				isParentBundle: this.options.auth.isParentBundle
			}),
			isDispatcher: this.options.isDispatcher,
			companyName: this.options.companyName,
			currentUserCompanyName: this.options.currentUserCompanyName,
			resourceType: 'workers',
			showAssignButton: false,
			isSent: this.options.isSent,
			showActions: !(this.options.isDispatcher),
			showBundleActions: false,
			bundleTitle: this.options.bundleTitle,
			hasInvitedAtLeastOneVendor: this.options.hasInvitedAtLeastOneVendor,
			showSort: true,
			assignment: this.model
		});
	}),

	loadVendors: _.once(function () {
		this.vendors = new WorkerListView({
			collection: new WorkerListCollection([], {
				id: this.model.workNumber,
				status: this.model.status.code,
				disablePriceNegotiation: this.model.configuration.disablePriceNegotiation,
				isBuyerAuthorizedToApproveCounter: this.options.auth.isBuyerAuthorizedToApproveCounter,
				isDeputy: this.options.auth.isDeputy,
				isIndividualBundledAssignment: this.options.auth.isIndividualBundledAssignment,
				isParentBundle: this.options.auth.isParentBundle,
				resourceType: 'vendors'
			}),
			isDispatcher: this.options.isDispatcher,
			assignToFirstResource: this.options.assignToFirstResource,
			companyName: this.options.companyName,
			currentUserCompanyName: this.options.currentUserCompanyName,
			paymentTime: this.options.paymentTime,
			assignment: this.model,
			disableBulkActions: true,
			resourceType: 'vendors',
			el: '#vendors-bucket',
			showAssignButton: false,
			isSent: this.options.isSent,
			showActions: true,
			showVendorActions: false,
			showBundleActions: false,
			bundleTitle: this.options.bundleTitle,
			hasInvitedAtLeastOneVendor: this.options.hasInvitedAtLeastOneVendor,
			showSort: false
		});
	}),

	showVendorsTab: function () {
		this.$('input[name="workers"]').prop('checked', false);
		this.$('input[name="vendors"]').prop('checked', true);
		this.$('#workers-bucket').hide();
		this.$('#vendors-bucket').show();
	},

	showWorkersTab: function () {
		this.$('input[name="vendors"]').prop('checked', false);
		this.$('input[name="workers"]').prop('checked', true);
		this.$('#workers-bucket').show();
		this.$('#vendors-bucket').hide();
	},

	sendWorkerDecline: function () {
		this.confirmModal = wmModal({
			autorun: true,
			title: 'Decline Assignment Bundle',
			destroyOnClose: true,
			content: ConfirmActionTemplate({
				message: 'Are you sure you want to decline this assignment bundle?'
			})
		});

		$('.cta-confirm-yes').on('click', _.bind(function () {
			$.post('/assignments/reject/' + this.options.model.workNumber, function (result) {
				if (result.success) {
					this.redirect('/assignments', [result.success], 'success');
				} else {
					wmNotify({
						type: 'danger',
						message: result.error
					});
				}
				$('[data-tabs] a[href="#overview"]').trigger('click');
			}, 'json');
			this.confirmModal.hide();
		}, this));
	},

	redirect: function (url, msg, type) {
		if (msg) {
			var e = $("<form class='dn'></form>");
			e.attr({
				'action': '/message/create',
				'method': 'POST'
			});
			if (typeof msg === 'string') { msg = [msg]; }
			for (var i=0; i < msg.length; i++) {
				e.append(
					$("<input>").attr({
						'name': 'message[]',
						'value': msg[i]
					}));
			}
			e.append(
				$("<input>").attr({
					'name': 'type',
					'value': type
				}));
			e.append(
				$("<input>").attr({
					'name': 'url',
					'value': url
				}));
			e.append(
				$("<input>").attr({
					'name':'_tk',
					'value':getCSRFToken()
				}));
			$('body').append(e);
			e.submit();
		} else {
			window.location = url;
		}
	},

	showModal: function (e, params) {
		if ($(e.currentTarget).is('.disabled')) {
			e.preventDefault();
			return false;
		}

		$.ajax({
			type: 'GET',
			url: e.currentTarget.href,
			context: this,
			success: function (response) {
				if (!_.isEmpty(response)) {
					wmModal({
						autorun: true,
						title: $(e.currentTarget).attr('title') || $(e.currentTarget).text(),
						destroyOnClose: true,
						content: response
					});
				}
			}
		});

		e.preventDefault();
		return false;
	},

	getRoutingAndSave: function () {
		var routingObj = this.routing.getRoutingObject();
		var showInFeed = document.createElement('input');
		showInFeed.type = 'hidden';
		showInFeed.name = 'show_in_feed';
		showInFeed.value = routingObj.routing.showInFeed;
		if (routingObj.routing.sendType === 'direct_send') {
			var groups = document.createElement('input');
				groups.type = 'hidden';
				if (routingObj.routing.assignToFirstGroup) {
					groups.name = 'routing.assignToFirstToAcceptGroupIds';
				} else {
					groups.name = 'routing.needToApplyGroupIds';
				}
				groups.value = routingObj.routing.groupIds;
			$('#assignments_form').append(groups);
			var workers = document.createElement('input');
				workers.type = 'hidden';
				if (routingObj.routing.assignToFirstTalent) {
					workers.name = 'routing.assignToFirstToAcceptUserNumbers';
				} else {
					workers.name = 'routing.needToApplyUserNumbers';
				}
				workers.value = routingObj.routing.resourceIds;
			$('#assignments_form').append(workers);
			var vendors = document.createElement('input');
				vendors.type = 'hidden';
				if (routingObj.routing.assignToFirstVendor) {
					vendors.name = 'routing.assignToFirstToAcceptVendorCompanyNumbers';
				} else {
					vendors.name = 'routing.needToApplyVendorCompanyNumbers';
				}
				vendors.value = routingObj.routing.companyIds;
			$('#assignments_form').append(vendors);
			
		} else if (routingObj.routing.sendType === 'work_send') {
			var smartRoute = document.createElement('input');
				smartRoute.type = 'hidden';
				smartRoute.name = 'smart_route';
				smartRoute.value = true;
			$('#assignments_form').append(smartRoute);
		}
		$('#assignments_form').submit();
	},

	resendWorkerInvitation: function (e) {
		e.preventDefault();

		var url, params;
		if ($(e.target).is('button')) {
			var form = $(e.target).closest('form');
			url = form.attr('action');
			params = $('input[name="workerNumber"]', form).serialize();
		} else {
			url = e.target.href;
			params = $('input[name="workerNumber"]').serialize();
		}

		$.ajax({
			type: 'GET',
			url: url + '?' + params,
			context: this,
			success: function (response) {
				if (!_.isEmpty(response)) {
					wmModal({
						autorun: true,
						title: $(e.currentTarget).attr('title') || $(e.currentTarget).text(),
						destroyOnClose: true,
						content: response
					});
				}
			}
		});
	}
});

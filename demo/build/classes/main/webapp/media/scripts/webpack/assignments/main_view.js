import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import 'jquery-form/jquery.form'; // eslint-disable-line
import Application from '../core';
import DocumentsListView from './documents_list_view';
import AddLabelView from './add_label_view';
import CustomFieldsView from './custom_fields_view';
import TimeTrackingListView from './time_tracking_list_view';
import FollowersView from './followers_view';
import PricingResourceCompleteView from './pricing_resource_complete_view';
import DeliverablesView from './deliverables_view';
import NonRequiredDeliverablesView from './non_required_deliverables_view';
import CompletionBarView from './completion_bar_view';
import ActivitiesView from './activities_view';
import ActivitiesCollection from './activities_collection';
import MessagesView from './messages_view';
import MessagesCollection from './messages_collection';
import DeliverableRequirementModel from './deliverable_requirement_model';
import PartsCollection from './parts_withtracking_collection';
import WorkerListCollection from './worker_list_collection';
import DeliverableAssetsCollection from './deliverable_assets_collection';
import AcceptAssignmentModel from './accept_assignment_model';
import ApplyAssignmentModel from './apply_assignment_model';
import NegotiationModel from './negotiation_model';
import BlockClientModel from './block_client_model';
import PartsView from './parts_withtracking_view';
import WorkerListView from './worker_view';
import UsersCollection from './users_collection';
import NegotiateScheduleView from './negotiation_schedule_view';
import NegotiateView from './negotiations_view';
import DescriptionView from './description_view';
import DeliverableRequirementsCollection from './deliverable_requirements_collection';
import PricingPage from './pricing_page';
import wmNotify from '../funcs/wmNotify';
import wmModal from '../funcs/wmModal';
import wmTabs from '../funcs/wmTabs';
import getCSRFToken from '../funcs/getCSRFToken';
import '../funcs/wmAssignmentPricing';
import '../dependencies/jquery.serializeObject';
import '../funcs/autoresizeTextarea';
import '../dependencies/jquery.rating';
import ConfirmActionTemplate from '../funcs/templates/confirmAction.hbs';
import BlockCompanyTemplate from '../assignments/templates/details/dispatcher/block-company.hbs';
import launchOfflinePaymentsModal from './offline_payments_modal';

const loadAssignmentCreationModal = async() => {
	const module = await import(/* webpackChunkName: "newAssignmentCreation" */ './creation_modal');
	return module.default;
};

export default Backbone.View.extend({
	el: 'body',
	clientRatingEl: $('.create_client_rating'),
	resourceRatingEl: $('.create_resource_rating'),

	events () {
		let agent = navigator.userAgent.toLowerCase(),
			clickOrTouch = agent.match(/ip(hone|od|ad)/i) || agent.match(/android/i) ? 'touchend' : 'click';

		const ret = {
			'shown .wm-tabs [data-content="#overview"]': 'showOverview',
			'submit #apply-form': 'applyFormSubmit'
		};

		ret[`${clickOrTouch} .ask_question_action`] = 'showModal';
		ret[`${clickOrTouch} .stop_payment_action`] = 'showModal';
		ret[`${clickOrTouch} .negotiate_action`] = 'negotiate';
		ret[`${clickOrTouch} .conflict_apply`] = 'conflictApply';
		ret[`${clickOrTouch} .assignment_action_accept`] = 'accept';
		ret[`${clickOrTouch} .budget_increase_action`] = 'negotiatePrice';
		ret[`${clickOrTouch} .reimbursement_action`] = 'requestReimbursement';
		ret[`${clickOrTouch} .bonus_action`] = 'negotiateBonus';
		ret[`${clickOrTouch} .negotiate_schedule_action`] = 'negotiateSchedule';
		ret[`${clickOrTouch} .delegate_action`] = 'showModal';
		ret[`${clickOrTouch} .delete_action`] = 'showModal';
		ret[`${clickOrTouch} .void_action`] = 'showVoid';
		ret[`${clickOrTouch} .cancel_action`] = 'showCancel';
		ret[`${clickOrTouch} .unassign_action`] = 'showUnassign';
		ret[`${clickOrTouch} .abandon_action`] = 'showModal';
		ret[`${clickOrTouch} .blockclient_action`] = 'showBlockClient';
		ret[`${clickOrTouch} .reprice_action`] = 'showReprice';
		ret[`${clickOrTouch} .assign_action`] = 'showAssign';
		ret[`${clickOrTouch} .reassign_internal_action`] = 'showReassignInternal';
		ret[`${clickOrTouch} .edit_location_contact_action`] = 'showEditLocationModal';
		ret[`${clickOrTouch} .edit_support_contact_action`] = 'showModal';
		ret[`${clickOrTouch} .edit_internal_owner_action`] = 'showModal';
		ret[`${clickOrTouch} .send_reminder_to_complete_action`] = 'showModal';
		ret[`${clickOrTouch} .resend_invite`] = 'resendWorkerInvitation';
		ret[`${clickOrTouch} .sendback_action`] = 'showSendBackModal';
		ret[`${clickOrTouch} .add_label_action`] = 'showModalAddLabel';
		ret[`${clickOrTouch} .remove_label_action`] = 'showModal';
		ret[`${clickOrTouch} .js-follow`] = 'actionFollow';
		ret[`${clickOrTouch} .resource-checkin-toggle`] = 'toggleResourceCheckin';
		ret[`${clickOrTouch} .resource-complete-toggle`] = 'toggleResourceComplete';
		ret[`${clickOrTouch} .buyer-complete-toggle`] = 'toggleBuyerComplete';
		ret[`${clickOrTouch} .resource-complete-save`] = 'saveResourceComplete';
		ret[`${clickOrTouch} .resource-apply-toggle`] = 'toggleResourceApply';
		ret[`${clickOrTouch} .view_survey_results`] = 'showSurveyResults';
		ret[`${clickOrTouch} .on-behalf-complete-save`] = 'saveCompleteOnBehalf';
		ret[`${clickOrTouch} [data-toggle="collapse"]`] = 'toggleIconPlusMinus';
		ret[`${clickOrTouch} .toggle-icon`] = 'toggleIcon';
		ret[`${clickOrTouch} [rel="prompt_decline_negotiation"]`] = 'promptDeclineNegotiation';
		ret[`${clickOrTouch} [data-behavior="remove-label"]`] = 'removeLabelModal';
		ret[`${clickOrTouch} .edit-rating`] = 'editRating';
		ret[`${clickOrTouch} .rating-sidebar`] = 'showModal';
		ret[`${clickOrTouch} .select-all-radio`] = 'selectAllRatings';
		ret[`${clickOrTouch} .inline_editors`] = 'toggleInlineEditing';
		ret[`${clickOrTouch} .inline-update`] = 'inlineUpdate';
		ret[`${clickOrTouch} #toggle_active_resource_address`] = 'toggleAddress';
		ret[`${clickOrTouch} #messaging_tab`] = 'showMessagesTab';
		ret[`${clickOrTouch} .resource-apply-toggle.-dispatcher`] = 'toggleSubmitButtonText';
		ret[`${clickOrTouch} .dispatcher-show-block-client`] = 'dispatcherShowBlockClient';
		ret[`${clickOrTouch} #dispatcher-block-client`] = 'dispatcherBlockClient';
		ret[`${clickOrTouch} #dispatcher-apply`] = 'dispatcherApply';
		ret[`${clickOrTouch} #dispatcher-accept`] = 'dispatcherAccept';
		ret[`${clickOrTouch} #activity-tab`] = 'showActivityTab';
		ret[`${clickOrTouch} .flag-rating`] = 'flagRating';
		ret[`${clickOrTouch} .show-vendors`] = 'showVendorsTab';
		ret[`${clickOrTouch} .show-workers`] = 'showWorkersTab';
		ret[`${clickOrTouch} .js-nav-invite-more-workers`] = 'navInviteMoreWorkers';
		ret[`${clickOrTouch} .js-workers-invite-more-workers`] = 'workersInviteMoreWorkers';
		ret[`${clickOrTouch} .worker-decline`] = 'sendWorkerDecline';
		ret[`${clickOrTouch} .edit-assignment`] = 'newEditAssignment';
		ret[`${clickOrTouch} .js-nav-invite-more-workers`] = 'newEditAssignment';
		ret[`${clickOrTouch} .js-workers-invite-more-workers`] = 'newEditAssignment';
		ret[`${clickOrTouch} .add_resources_action`] = 'newEditAssignment';
		ret[`${clickOrTouch} .buyer-approve`] = 'buyerApprove';

		return ret;
	},

	initialize () {
		const workMeta = {
			workId: this.model.id,
			workNumber: this.model.workNumber,
			millisOffset: this.options.millisOffset
		};

		// event aggregator
		const vent = _.extend({}, Backbone.Events);

		wmTabs();

		if (this.options.showDocuments) {
			this.documents = new DocumentsListView(_.extend({}, workMeta, {
				collection: this.model.assets || [],
				visibilitySettings: this.options.visibilitySettings,
				isAdmin: this.options.auth.isAdmin,
				isActiveWorker: this.options.auth.isActiveResource
			}));
		}
		this.descriptionView = new DescriptionView({
			workNumber: this.model.workNumber
		});

		if (this.model.partGroup) {
			const partsColl = new PartsCollection([], {
				workNumber: workMeta.workNumber,
				isMobile: false,
				partsConstants: this.options.partsConstants
			});

			new PartsView(_.extend({}, workMeta, {
				collection: partsColl,
				partGroup: this.model.partGroup,
				isMobile: false,
				isReturn: false,
				isNotSentOrDraft: this.options.isNotSentOrDraft,
				isOwnerOrAdmin: this.options.auth.isAdmin || this.options.auth.isOwner,
				isSuppliedByWorker: this.options.isSuppliedByWorker,
				el: '#partsSent'
			}));

			if (this.model.partGroup.returnRequired) {
				new PartsView(_.extend({}, workMeta, {
					collection: partsColl,
					partGroup: this.model.partGroup,
					isMobile: false,
					isReturn: true,
					isNotSentOrDraft: this.options.isNotSentOrDraft,
					isOwnerOrAdmin: this.options.auth.isAdmin || this.options.auth.isOwner,
					isSuppliedByWorker: this.options.isSuppliedByWorker,
					el: '#partsReturn'
				}));
			}

			partsColl.fetch({ remove: false });
		}

		let activeOrComplete = this.model.status.code === 'active' || this.model.status.code === 'complete';

		if (!_.isUndefined(this.model.deliverableRequirementGroupDTO) &&
			!_.isUndefined(this.model.deliverableRequirementGroupDTO.deliverableRequirementDTOs) &&
				this.model.deliverableRequirementGroupDTO.deliverableRequirementDTOs.length > 0) {
			// Set up the Deliverable Requirement models from the DTOS
			const deliverableRequirementModels = _.map(this.model.deliverableRequirementGroupDTO.deliverableRequirementDTOs, function (dto) {
				// Filter to only those assets for this deliverable requirement group
				let filteredAssets = _.filter(this.model.deliverableAssets, (asset) => {
					return asset.deliverableRequirementId === dto.id;
				});

				// Set the work number on each asset
				filteredAssets = _.map(filteredAssets, _.bind(function (asset) {
					return _.extend(asset, { workNumber: this.model.workNumber });
				}, this));

				// Group assets by position
				const assetsGroupedByPosition = _.groupBy(filteredAssets, (el) => { return el.position; });

				// Set the asset history each position's most recent upload
				filteredAssets =
					_.map(assetsGroupedByPosition, (group) => {
							// Sort group by descending upload date (most recent first)
						group = _.sortBy(group, (asset) => { return -1 * asset.uploadDate; });
						group[0].assetHistory = _.rest(group);
						return group[0];
					});

				// Extend the model
				const deliverableRequirementModel = new DeliverableRequirementModel(_.extend({}, dto, workMeta, this.options.deliverablesConstants));

				// Init the deliverable assets collection on the deliverable requirement
				deliverableRequirementModel.deliverableAssets = new DeliverableAssetsCollection(filteredAssets);

				return deliverableRequirementModel;
			}, this);

			const deliverableModel = new Backbone.Model(this.model.deliverableRequirementGroupDTO);
			deliverableModel.deliverableRequirements = new DeliverableRequirementsCollection(deliverableRequirementModels);

			this.deliverables = new DeliverablesView(_.extend({}, workMeta, {
				model: deliverableModel,
				status: this.model.status.code,
				assignmentStartTime: this.model.schedule.through !== null ? this.model.schedule.from : this.model.schedule.through,
				isWorker: activeOrComplete && this.options.auth.isResource,
				isAdmin: this.options.auth.isAdmin,
				isOwner: this.options.auth.isOwner
			}));
		} else if (this.model.status.code !== 'sent' && this.model.status.code !== 'draft') {
			this.deliverables = new NonRequiredDeliverablesView(_.extend({}, workMeta, {
				model: this.model.deliverableAssets,
				isActiveWorker: activeOrComplete && this.options.auth.isActiveResource,
				isAdmin: this.options.auth.isAdmin
			}));
		}

		this.paneCustomFields = new CustomFieldsView(_.extend({}, workMeta, {
			model: this.model.customFieldGroups,
			el: $('#pane-custom-fields'),
			type: 'pane',
			duringCompletion: false,
			auth: this.options.auth,
			vent
		}));
		this.timetracking = new TimeTrackingListView(_.extend({}, workMeta, {
			model: this.model.activeResource,
			checkoutNoteRequiredFlag: this.model.checkoutNoteRequiredFlag,
			vent
		}));
		this.pricingResourceComplete = new PricingResourceCompleteView({ model: this.model.pricing });
		this.loadWorkers();
		if (this.options.hasInvitedAtLeastOneVendor) {
			this.$('#vendors-bucket').hide();
			this.loadVendors();
			this.vendors.collection.on('reset', () => {
				$('.vendor-count').text(this.vendors.collection.totalLength);
			});
			this.workers.collection.on('reset', () => {
				$('.worker-count').text(this.workers.collection.totalLength);
			});
		}
		this.followers = new FollowersView({
			workNumber: this.model.workNumber
		});
		this.followers.parentView = this;
		this.completionBar = new CompletionBarView();
		this.counteroffer = false;

		// Star ratings on the resource complete and buyer pay callouts

		$('input.stars').rating({
			required: true,
			focus (value, link) {
				const tip = $('.rating-text');
				tip.data('label', tip.data('label') || tip.html());
				tip.html(link.title || `value: ${value}`);
			},

			blur () {
				const tip = $('.rating-text');
				tip.html(tip.data('label') || '');
			},

			callback (value, link) {
				$('.block-if-low-rating-outlet')[value < 60 ? 'show' : 'hide']();
				$('.rating-text').html(link.title).data('label', link.title);
			}
		});

		$('.three-level').click(() => {
			if ($('.rating-value:checked').val() === 1) {
				$('.block-if-low-rating-outlet').show();
			} else {
				$('.block-if-low-rating-outlet').hide();
			}
		});

		if (this.model.configuration.disablePriceNegotiation) {
			this.disableBudgetIncrease();
		}

		if (this.model.pricing.type === 'INTERNAL') {
			this.hideBudgetIncrease();
			this.hideExpenseReimbursement();
			this.hideBonus();
		}

		// this should be removed when the messages feature toggle is removed
		if (!_.isUndefined(this.model.questionAnswerPairs)) {
			_.each(this.model.questionAnswerPairs, function (i) {
				if (!_.isUndefined(i.questionerId)) {
					$.ajax({
						url: `/user/${this.isValidUserNumber(i.questionerId)}`,
						type: 'GET',
						dataType: 'json',
						success (data) {
							$(`.questioner-${i.questionerId}`).html(`${data.firstName} ${data.lastName} `);
						}
					});
				}
				if (!_.isUndefined(i.answererId) && i.answererId !== 0) {
					$.ajax({
						url: `/user/${this.isValidUserNumber(i.answererId)}`,
						type: 'GET',
						dataType: 'json',
						success (data) {
							$(`.answerer-${i.answererId}`).html(`${data.firstName} ${data.lastName} `);
						}
					});
				}
			}, this);
		}

		this.addClientRating();
		this.addResourceRating();
		this.initIconPlusMinus();
		this.render();

		Backbone.Events.on('cleanNegotiationView', function () {
			this.applyView = undefined;
		}, this);

		if (this.$('.tab-content #workers') && this.model.status.code === 'sent' &&
			(!this.options.isDispatcher || (this.options.isDispatcher && document.referrer.indexOf('/contact/') > -1))) {
			$('.wm-tabs [data-content="#workers"]').trigger('click');
		}

		$('.assign-drop .edit_action, .assign-drop .reschedule_action').on('click', e => this.newEditAssignment(e));
		$('.copy_action').on('click', e => this.newCopyAssignment(e));
	},

	newEditAssignment (e) {
		e.preventDefault();
		const assignmentId = this.model.workNumber;
		const title = 'Edit Assignment';
		const isInviteButton = (/contact/).test(e.target.href);
		if ((isInviteButton && !this.options.isSent) || !isInviteButton) {
			const scrollToTab = isInviteButton && !this.options.isSent
				? 'routing'
				: '';
			// TODO[tim-mc] Error handling for loading module
			loadAssignmentCreationModal()
				.then(CreationModal => new CreationModal({ assignmentId, title, scrollToTab }));
		} else {
			window.location = e.target.href;
		}
	},

	newCopyAssignment (e) {
		e.preventDefault();
		const assignmentId = this.model.workNumber;
		const title = 'Copy Assignment';
		// TODO[tim-mc] Error handling for loading module
		loadAssignmentCreationModal().then(CreationModal => new CreationModal({ assignmentId, title }));
	},
	render () {
		if (this.documents) {
			this.documents.render();
		}
		if (this.deliverables) {
			this.deliverables.render();
		}
		this.paneCustomFields.render();
		this.timetracking.render();
		this.pricingResourceComplete.render();
		this.completionBar.render();

		if (this.$('.callout').children().size() === 0) {
			this.$('.callout').hide();
		}

		if (!this.model.delegationAllowed) {
			this.$('.delegate_action').hide();
		}
		const reim = this.$('.sidebar .reimbursement_action');
		const budget = this.$('.sidebar .budget_increase_action');
		const bonus = this.$('.sidebar .bonus_action');

		// set different nav button text for buyer
		if (reim.length > 0 && budget.length > 0 && bonus.length > 0) {
			const prefix = (this.options.auth.isAdmin || this.options.auth.isInternal) ? 'Add ' : 'Request ';
			reim.text(prefix + reim.text());
			budget.text(prefix + budget.text());
			bonus.text(prefix + bonus.text());
		}

		return this;
	},

	reassign (e) {
		e.preventDefault();

		$.ajax({
			type: 'GET',
			url: e.currentTarget.href,
			context: this,
			success (response) {
				if (!_.isEmpty(response)) {
					wmModal({
						autorun: true,
						title: 'Reassign Assignment',
						destroyOnClose: true,
						content: response
					});

					$('#form_reassign').ajaxForm({
						success (data) {
							if (data.successful) {
								window.location.reload();
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

					// Handle cancel fee calculations.
					$('#form_reassign #amount').keyup(function () {
						let val;
						if (!isNaN(parseFloat($(this).val()))) {
							val = parseFloat($(this).val()) + (parseFloat($(this).val()) * (parseFloat($('#work_fee').val()) / 100));
						} else {
							val = 0;
						}
						$('#form_reassign #cancel_total').html(`$${val}`);
					});
				}
			}

		});
	},

	isValidUserNumber (data) {
		return _.reduce(_.range(7 - (data += '').length), (memo) => { return `0${memo}`; }, data);
	},

	showModalAddLabel (e) {
		e.preventDefault();

		$.ajax({
			type: 'GET',
			url: e.currentTarget.href,
			context: this,
			success (response) {
				if (!_.isEmpty(response)) {
					wmModal({
						autorun: true,
						title: $(e.currentTarget).attr('title') || $(e.currentTarget).text(),
						destroyOnClose: true,
						content: response
					});

					new AddLabelView({
						el: '#add_label_form',
						millisOffset: this.options.millisOffset
					});
				}
			}

		});
	},

	showVoid (e) {
		e.preventDefault();

		$.ajax({
			type: 'GET',
			url: e.currentTarget.href,
			context: this,
			success (response) {
				if (!_.isEmpty(response)) {
					wmModal({
						autorun: true,
						title: 'Void Assignment',
						destroyOnClose: true,
						content: response
					});

					$('#void_work_form').ajaxForm({
						context: this,
						success (data) {
							if (data.successful) {
								this.redirectWithFlash(data.redirect, 'success', data.messages[0]);
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
				}
			}

		});
	},

	redirectWithFlash (url, type, msg) {
		const e = $('<form></form>');
		e.attr({
			action: '/message/create',
			method: 'POST'
		});
		e.append(
			$('<input>').attr({
				name: 'message[]',
				value: msg
			}));
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
	},

	showCancel (e) {
		e.preventDefault();

		$.ajax({
			type: 'GET',
			url: e.currentTarget.href,
			context: this,
			success (response) {
				if (!_.isEmpty(response)) {
					wmModal({
						autorun: true,
						title: 'Cancel Assignment',
						destroyOnClose: true,
						content: response
					});

					$('#cancel_work_form').ajaxForm({
						context: this,
						success (data) {
							if (data.successful) {
								this.redirectWithFlash(data.redirect, 'success', data.messages);
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

					// Handle cancel fee calculations.
					$('#cancel_work_form #amount').on('keyup', function () {
						let val;
						if (!isNaN(parseFloat($(this).val()))) {
							val = parseFloat($(this).val()) + (parseFloat($(this).val()) * (parseFloat($('#work_fee').val()) / 100));
						} else {
							val = 0;
						}
						$('#cancel_work_form #cancel_total').html(`$${val}`);
					});

					$('#cancel_work_form [name=reason]').on('change', (event) => {
						const value = $(event.target).val();
						$('.cancellation-notice-outlet')[value === 'buyer_cancelled' ? 'hide' : 'show']();
					});
				}
			}

		});
	},

	showUnassign (e) {
		e.preventDefault();

		$.ajax({
			type: 'GET',
			url: e.currentTarget.href,
			context: this,
			success (response) {
				if (!_.isEmpty(response)) {
					wmModal({
						autorun: true,
						title: 'Unassign',
						destroyOnClose: true,
						content: response
					});

					if(!$('#reason').length) {
						$('#unassign_note').closest('.control-group').toggle();
					}
					// Toggle notes field on whether a unassign reason is selected
					$('#reason').on('change', function () {
						$('#unassign_note').closest('.control-group').toggle($(this).val().length > 0);
					});
					$('#unassign_form').ajaxForm({
						context: this,
						success (data) {
							if (data.successful) {
								this.redirectWithFlash(data.redirect, 'success', data.messages);
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
				}
			}
		});
	},

	toggleResourceApply (e) {
		const form = $(e.currentTarget).closest('form');
		const config = form.find('[ref=configuration]');

		config.toggle();
		$('.toggler').toggleClass('toggled');

		if (config.is(':visible') && !this.applyView) {
			this.applyView = new NegotiateView({ el: '#apply-form' });
		}
	},

	conflictApply (e) {
		e.preventDefault();
		wmModal({
			autorun: true,
			title: 'Schedule Conflict',
			destroyOnClose: true,
			content: $('#schedule_conflict_dialog_container').html()
		});
	},

	negotiate (e) {
		e.preventDefault();

		if ($(e.currentTarget).attr('disabled')) {
			return false;
		}

		$.ajax({
			type: 'GET',
			url: e.currentTarget.href,
			context: this,
			success (response) {
				if (!_.isEmpty(response)) {
					wmModal({
						autorun: true,
						title: 'Request a Counteroffer',
						destroyOnClose: true,
						content: response
					});

					new NegotiateView({ el: '#negotiate-work', isModal: true });
				}
			}
		});
	},

	accept (e) {
		e.preventDefault();
		const button = $(e.target);

		if (button.hasClass('disabled') || button.attr('disabled') === 'disabled') {
			return;
		}
		button.addClass('disabled');

		const postAcceptAssignment = (workNumber, data) => {
			$.ajax({
				url: `/assignments/accept/${workNumber}`,
				type: 'GET',
				data,
				dataType: 'json',
				// TODO[backend]: fix API to respond with appropriate status codes
				// the API is not set up to respond with success/fail
				// instead, the button is a link to an endpoint that responds with 302 redirect
				// which is a fake way of reloading the page, no error handling
				complete: () => location.reload()
			});
		};

		if (Application.Features.offlinePaymentEnabled) {
			launchOfflinePaymentsModal(
				true,
				'Agree and Accept',
				() => postAcceptAssignment(this.model.workNumber),
				() => button.removeClass('disabled'),
				this.options.companyName
			);
		} else {
			postAcceptAssignment(this.model.workNumber);
		}
	},

	showAssign (e) {
		e.preventDefault();

		if ($(e.currentTarget).attr('disabled')) {
			return false;
		}

		$.ajax({
			type: 'GET',
			url: $(e.currentTarget).attr('href'),
			context: this,
			success (response) {
				if (!_.isEmpty(response)) {
					wmModal({
						autorun: true,
						title: $(e.currentTarget).attr('title') || $(e.currentTarget).text(),
						destroyOnClose: true,
						content: response
					});

					$('#form_assign').ajaxForm({
						dataType: 'json',
						success (response) {
							if (response && response.successful) {
								const id = response.data.id;
								const note = response.data.note;
								$.ajax({
									data: {
										note
									},
									url: `/assignments/assign_work_to_employee/${id}`,
									type: 'POST',
									dataType: 'json'
								}).then((response) => {
									if (response && response.successful) {
										window.location.reload();
									} else {
										_.each(response.messages, (theMessage) => {
											wmNotify({
												message: theMessage,
												type: 'danger'
											});
										});
									}
								});
							} else {
								_.each(response.messages, (theMessage) => {
									wmNotify({
										message: theMessage,
										type: 'danger'
									});
								});
							}
						}
					});
				}
			}
		});
	},

	showReprice (e) {
		e.preventDefault();
		if ($(e.currentTarget).is('.disabled')) {
			return false;
		}

		$.ajax({
			type: 'GET',
			url: e.currentTarget.href,
			context: this,
			success (response) {
				if (!_.isEmpty(response)) {
					wmModal({
						autorun: true,
						title: $(e.currentTarget).attr('title') || $(e.currentTarget).text(),
						destroyOnClose: true,
						content: response
					});

					const $form = $('#form_price');
					$form.wmAssignmentPricing({ modal: true });

					new PricingPage({
						wmFee: parseFloat($('#work-fee').val()),
						pricingType: $('#pricing-type').val()
					});
				}
			}
		});
	},

	showReassignInternal (e) {
		e.preventDefault();

		$.ajax({
			type: 'GET',
			url: e.currentTarget.href,
			context: this,
			success (response) {
				if (!_.isEmpty(response)) {
					wmModal({
						autorun: true,
						title: $(e.currentTarget).attr('title') || $(e.currentTarget).text(),
						destroyOnClose: true,
						content: response
					});

					$('#form_reassigninternal').ajaxForm({
						dataType: 'json',
						success (response) {
							if (response && response.successful) {
								const id = response.data.id;
								const note = response.data.note;
								$.ajax({
									data: {
										note
									},
									url: `/assignments/assign_work_to_employee/${id}`,
									type: 'POST',
									dataType: 'json'
								}).then((response) => {
									if (response && response.successful) {
										window.location.reload();
									} else {
										_.each(response.messages, (theMessage) => {
											wmNotify({
												message: theMessage,
												type: 'danger'
											});
										});
									}
								});
							} else {
								_.each(response.messages, (theMessage) => {
									wmNotify({
										message: theMessage,
										type: 'danger'
									});
								});
							}
						}
					});
				}
			}
		});
	},

	negotiatePrice (e) {
		e.preventDefault();

		function displayNum (num) {
			if (isNaN(num) || num < 0)				{
				return 0;
			}
			return num.toFixed(2);
		}


		function get_as_num (num) {
			num = num.toString().replace(/\$|\,/g, '');
			if (isNaN(num))				{
				return 0;
			}			else				{
				return parseFloat(num);
			}
		}

		$.ajax({
			type: 'GET',
			url: e.currentTarget.href,
			context: this,
			success (response) {
				if (!_.isEmpty(response)) {
					wmModal({
						autorun: true,
						title: `${this.options.auth.isAdmin ? 'Add' : 'Request'} a Budget Increase`,
						destroyOnClose: true,
						content: response
					});

					const wmFee = parseFloat($('#wmFee').val());
					const maxFee = parseFloat($('#maxFee').val());
					const pctMultiplier = 1 + (wmFee / 100.0);
					const isSpend = $('#isSpend').val();
					const isAdmin = $('#isAdmin').val();
					const additionalExpenses = parseFloat($('#additionalExpenses').val());
					const bonus = parseFloat($('#bonus').val());

					const number_value = function (field) {
						let num = $(field).val();
						num = num.toString().replace(/\$|\,/g, '');
						return parseFloat(num) || 0.00;
					};

					if ($('#isFlat').val() === 'true') {
						$('[name="flat_price"]').keyup((e) => {
							const flatPrice = get_as_num(number_value($(e.target)));
							const budgetNoFee = flatPrice + additionalExpenses + bonus;
							let budget = (isAdmin === '1') ? budgetNoFee * pctMultiplier : budgetNoFee;
							let fee = budget - budgetNoFee;
							if (maxFee < fee) {
								fee = maxFee;
								budget = budgetNoFee + maxFee;
							}
							const resourceEarns = budgetNoFee - additionalExpenses - fee;

							$('#budgetIncreaseForm #assignment-budget').html(`$${displayNum(budget)}`);
							$('#budgetIncreaseForm #transaction-fee').html(`$${displayNum(fee)}`);
						});
					} else if ($('#isPerHour').val() === 'true') {
						const hourlyRate = $('#perHourRate').val();
						$('[name="max_number_of_hours"]').keyup((e) => {
							const maxHours = get_as_num(number_value($(e.target)));
							const resourceEarns = maxHours * hourlyRate;
							const budgetNoFee = resourceEarns + additionalExpenses + bonus;
							let budget = (isAdmin === '1') ? budgetNoFee * pctMultiplier : budgetNoFee;
							let fee = budget - budgetNoFee;
							if (maxFee < fee) {
								fee = maxFee;
								budget = budgetNoFee + maxFee;
							}

							$('#budgetIncreaseForm #resource-earns').html(`$${displayNum(resourceEarns)}`);
							$('#budgetIncreaseForm #transaction-fee').html(`$${displayNum(fee)}`);
							$('#budgetIncreaseForm #assignment-budget').html(`$${displayNum(budget)}`);
						});
					} else if ($('#isPerUnit').val() === 'true') {
						const unitRate = parseFloat($('#perUnitPrice').val());
						$('[name="max_number_of_units"]').keyup((e) => {
							const maxHours = get_as_num(number_value($(e.target)));
							const resourceEarns = maxHours * unitRate;
							const budgetNoFee = resourceEarns + additionalExpenses + bonus;
							let budget = (isAdmin === '1') ? budgetNoFee * pctMultiplier : budgetNoFee;
							let fee = budget - budgetNoFee;
							if (maxFee < fee) {
								fee = maxFee;
								budget = budgetNoFee + maxFee;
							}

							$('#budgetIncreaseForm #resource-earns').html(`$${displayNum(resourceEarns)}`);
							$('#budgetIncreaseForm #transaction-fee').html(`$${displayNum(fee)}`);
							$('#budgetIncreaseForm #assignment-budget').html(`$${displayNum(budget)}`);
						});
					} else if ($('#isBlendedPerHour').val() === 'true') {
						const initialSpend = parseFloat($('#initialSpend').val());
						const secondaryRate = parseFloat($('#secondaryRate').val());
						$('[name="max_blended_number_of_hours"]').keyup((e) => {
							const maxHours = get_as_num(number_value($(e.target)));
							const newSecondarySpend = secondaryRate * maxHours;
							const resourceEarns = initialSpend + newSecondarySpend;
							const budgetNoFee = resourceEarns + additionalExpenses + bonus;
							let budget = (isAdmin === '1') ? budgetNoFee * pctMultiplier : budgetNoFee;
							let fee = budget - budgetNoFee;
							if (maxFee < fee) {
								fee = maxFee;
								budget = budgetNoFee + maxFee;
							}

							$('#budgetIncreaseForm #resource-earns').html(`$${displayNum(resourceEarns)}`);
							$('#budgetIncreaseForm #transaction-fee').html(`$${displayNum(fee)}`);
							$('#budgetIncreaseForm #assignment-budget').html(`$${displayNum(budget)}`);
						});
					}

					$('#price_negotiation, #schedule_negotiation').click(function () {
						const config = `#${$(this).attr('id')}_config`;
						$(config)[$(this).is(':checked') ? 'show' : 'hide']();
					});
				}
			}
		});
	},

	disableBudgetIncrease () {
		this.$('.sidebar .budget_increase_action')
			.addClass('disabled')
			.addClass('nowrap')
			.append(' <span class="tooltipped tooltipped-n" aria-label="The client has chosen to disable budget increases for this assignment."><i class="wm-icon-question-filled"></i></span>');
	},

	hideBudgetIncrease () {
		this.$('.sidebar .budget_increase_action').detach();
	},

	hideExpenseReimbursement () {
		this.$('.sidebar .reimbursement_action').detach();
	},

	hideBonus () {
		this.$('.sidebar .bonus_action').detach();
	},

	requestReimbursement (e) {
		e.preventDefault();

		function get_as_num (num) {
			num = num.toString().replace(/\$|\,/g, '');
			if (isNaN(num))				{
				return 0;
			}			else				{
				return parseFloat(num);
			}
		}

		const number_value = function (field) {
			let num = $(field).val();
			num = num.toString().replace(/\$|\,/g, '');
			return parseFloat(num) || 0.00;
		};

		$.ajax({
			type: 'GET',
			url: e.currentTarget.href,
			context: this,
			success (response) {
				if (!_.isEmpty(response)) {
					wmModal({
						autorun: true,
						title: `${this.options.auth.isAdmin ? 'Add' : 'Request'} an Expense Reimbursement`,
						destroyOnClose: true,
						content: response
					});

					if (this.options.isAdmin) {
						const wmFee = parseFloat($('#wmFee').val());
						const maxFee = parseFloat($('#maxFee').val());
						const pctMultiplier = 1 + (wmFee / 100.0);

						$('[name="additional_expenses"]').keyup((e) => {
							const expenseAmount = get_as_num(number_value($(e.target)));
							const amount = expenseAmount * pctMultiplier;
							let fee = amount - expenseAmount;
							let costDisplay = amount.toFixed(2);
							if (maxFee < fee) {
								fee = maxFee;
								costDisplay = (expenseAmount + maxFee).toFixed(2);
							}
							$('#reimbursementForm #total-reimbursement').html(`$${costDisplay}`);
							$('#reimbursementForm #transaction-fee').html(`$${fee.toFixed(2)}`);
						});
					}

					$('#reimbursementForm').wmAssignmentPricing({ modal: true });
				}
			}
		});
	},

	negotiateBonus (e) {
		e.preventDefault();

		function get_as_num (num) {
			num = num.toString().replace(/\$|\,/g, '');
			if (isNaN(num))				{
				return 0;
			}			else				{
				return parseFloat(num);
			}
		}

		const number_value = function (field) {
			let num = $(field).val();
			num = num.toString().replace(/\$|\,/g, '');
			return parseFloat(num) || 0.00;
		};

		$.ajax({
			type: 'GET',
			url: e.currentTarget.href,
			context: this,
			success (response) {
				if (!_.isEmpty(response)) {
					wmModal({
						autorun: true,
						title: `${this.options.auth.isAdmin ? 'Add' : 'Request'} a Bonus`,
						destroyOnClose: true,
						content: response
					});

					if (this.options.isAdmin) {
						const wmFee = parseFloat($('#wmFee').val());
						const maxFee = parseFloat($('#maxFee').val());
						const pctMultiplier = 1 + (wmFee / 100.0);

						$('[name="bonus"]').keyup((e) => {
							const bonusAmount = get_as_num(number_value($(e.target)));
							const amount = bonusAmount * pctMultiplier;
							let fee = amount - bonusAmount;
							let costDisplay = amount.toFixed(2);
							if (maxFee < fee) {
								fee = maxFee;
								costDisplay = (bonusAmount + maxFee).toFixed(2);
							}
							$('#bonusForm #total-bonus').html(`$${costDisplay}`);
							$('#bonusForm #transaction-fee').html(`$${fee.toFixed(2)}`);
						});
					}

					$('#bonusForm').wmAssignmentPricing({ modal: true });
				}
			}
		});
	},

	negotiateSchedule (e) {
		e.preventDefault();

		const self = this;
		$.ajax({
			type: 'GET',
			url: e.currentTarget.href,
			context: this,
			success (response) {
				if (!_.isEmpty(response)) {
					wmModal({
						autorun: true,
						title: 'Scheduling Update',
						destroyOnClose: true,
						content: response
					});
					new NegotiateScheduleView({
						el: '#reschedule-work',
						millisOffset: self.options.millisOffset
					});
				}
			}

		});
	},

	resendWorkerInvitation (e) {
		e.preventDefault();

		let url,
			params;
		if ($(e.target).is('button')) {
			const form = $(e.target).closest('form');
			url = form.attr('action');
			params = $('input[name="workerNumber"]', form).serialize();
		} else {
			url = e.target.href;
			params = $('input[name="workerNumber"]').serialize();
		}

		$.ajax({
			type: 'GET',
			url: `${url}?${params}`,
			context: this,
			success (response) {
				if (!_.isEmpty(response)) {
					wmModal({
						autorun: true,
						title: $(e.currentTarget).attr('title') || $(e.currentTarget).text(),
						destroyOnClose: true,
						content: response
					});

					$('#bonusForm').wmAssignmentPricing({ modal: true });
				}
			}
		});
	},

	showSendBackModal (event) {
		event.preventDefault();
		$.ajax({
			type: 'GET',
			url: $(event.currentTarget).attr('href'),
			context: this
		}).done((response) => {
			if (!_.isEmpty(response)) {
				wmModal({
					autorun: true,
					title: 'Request additional assignment details',
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

	toggleResourceCheckin () {
		if ($('#checkin-todo').is(':visible')) {
			$('#checkin-todo').hide();
			$('#checkin').show();
		} else {
			$('#checkin-todo').show();
			$('#checkin').hide();
		}
		return false;
	},
	toggleAddress () {
		const $address = $('#active_resource_address');
		if ($address.is(':visible')) {
			$('#toggle_active_resource_address').html('View Address');
			$address.hide();
		} else {
			$('#toggle_active_resource_address').html('Hide Address');
			$address.show();
		}
	},

	toggleResourceComplete (e) {
		if ($(e.currentTarget).hasClass('disabled')) {
			return false;
		} else {
			const self = this;
			$('#complete-todo').hide();
			$('#complete').show();


			$.getJSON(`/assignments/hours_worked/${this.model.workNumber}`, (data) => {
				if (data.successful) {
					const completeDiv = $('#complete');
					const hoursInput = completeDiv.find('[name=hours]');
					const minutesInput = completeDiv.find('[name=minutes]');

					if (hoursInput.length && !hoursInput.val() &&
						minutesInput.length && !minutesInput.val()) {
						hoursInput.val(data.data.hours);
						minutesInput.val(data.data.minutes);

						self.pricingResourceComplete.render();
					}
				}
			});
		}
	},

	toggleBuyerComplete () {
		const self = this;
		$('#buyer_complete_toggle').hide();
		$('#buyer_complete').show();

		$.getJSON(`/assignments/hours_worked/${this.model.workNumber}`, (data) => {
			if (data.successful) {
				const buyerDiv = $('#buyer_complete');
				const hoursInput = buyerDiv.find('[name=hours]');
				const minutesInput = buyerDiv.find('[name=minutes]');

				if (hoursInput.length && !hoursInput.val() && minutesInput.length && !minutesInput.val()) {
					hoursInput.val(data.data.hours);
					minutesInput.val(data.data.minutes);

					self.pricingResourceComplete.render();
				}
			}
		});
	},

	saveResourceComplete (e) {
		const formFields = $('#complete_form').serializeArray();
		const button = $(e.target);

		if (button.hasClass('disabled')) {
			return;
		}
		button.addClass('disabled');

		const postCompleteAssignment = (workNumber, data) => {
			$.ajax({
				url: `/assignments/complete/${workNumber}`,
				type: 'POST',
				data,
				dataType: 'json',
				success (response) {
					if (response && response.successful === true) {
						location.reload();
					} else {
						_.each(response.messages, (message) => {
							wmNotify({
								message,
								type: 'danger'
							});
						});
						button.removeClass('disabled');
					}
				}
			});
		};

		if (Application.Features.offlinePaymentEnabled) {
			launchOfflinePaymentsModal(
				true,
				'Agree and Complete',
				() => postCompleteAssignment(this.model.workNumber, formFields),
				() => button.removeClass('disabled'),
				this.model.company.name
			);
		} else {
			postCompleteAssignment(this.model.workNumber, formFields);
		}
	},

	buyerApprove (e) {
		if (!Application.Features.offlinePaymentEnabled) {
			return;
		}
		e.preventDefault();
		const formFields = $('#approve_assignment_form').serializeArray();
		const button = $(e.target);

		if (button.hasClass('disabled') || button.attr('disabled') === 'disabled') {
			return;
		}
		button.addClass('disabled');

		const postApproveAssignment = (workNumber, data) => {
			$.ajax({
				url: `/assignments/pay/${workNumber}`,
				type: 'POST',
				data,
				dataType: 'json',
				// TODO[backend]: fix API to respond with appropriate status codes
				// the API is not set up to respond with success/fail
				// instead, the button is a link to an endpoint that responds with 302 redirect
				// which is a fake way of reloading the page, no error handling
				complete: () => location.reload()
			});
		};

		launchOfflinePaymentsModal(
			false,
			'Agree and Approve',
			() => postApproveAssignment(this.model.workNumber, formFields),
			() => button.removeClass('disabled'),
			this.model.company.name
		);
	},

	addRating (type) {
		let $type;
		if (type === 'client') {
			$type = this.clientRatingEl;
		} else {
			$type = this.resourceRatingEl;
		}

		if ($type.length !== 0) {
			const obj = {};
			obj.data = '';
			obj.meta = {
				work_id: this.model.id,
				resource_id: this.model.activeResource.user.id,
				buyer_id: this.model.buyer.id
			};
			const rated = (type === 'resource') ? this.model.activeResource.user.id : this.model.buyer.id;
			const rater = (type === 'resource') ? this.model.buyer.id : this.model.activeResource.user.id;
			const postData = {
				ratedUserId: rated,
				raterUserId: rater,
				review: '',
				value: 0,
				workId: this.model.id
			};

			$type.html(this.renderRatingForm(obj));
			$('.three-level').click(() => {
				$('.rating-extras').show();
			});
			this.delegateEditReviewButton($type);
			this.submitRating($type, postData);
		}
	},

	delegateEditReviewButton ($el) {
		$el.on('click', '[data-behavior="cancel-review"]', function () {
			const container = $(this).parents('.rating');
			$('textarea', container).val('');
			$('.rating-extras', container).hide();
			$('input', container).each(function () {
				$(this).removeAttr('checked');
			});

			container.find('.rating-cancel').trigger('click');
		});

		$el.on('click', '[data-behavior="finish-review"]', function () {
			const container = $(this).parents('.rating');
			$('.rating-extras', container).hide();
		});
	},

	convertRatingCode (data) {
		if (data.value) {
			data.valueCode = this.getRatingCode(data.value);
			data.valueDescription = this.getRatingDescription(data.value);
		}

		if (data.quality) {
			data.qualityCode = this.getRatingCode(data.quality);
			data.qualityDescription = this.getRatingDescription(data.quality);
		}

		if (data.professionalism) {
			data.professionalismCode = this.getRatingCode(data.professionalism);
			data.professionalismDescription = this.getRatingDescription(data.professionalism);
		}

		if (data.communication) {
			data.communicationCode = this.getRatingCode(data.communication);
			data.communicationDescription = this.getRatingDescription(data.communication);
		}

		return data;
	},

	getRatingDescription (value) {
		switch (value) {
		case '3': return 'Excellent';
		case '2': return 'Satisfied';
		case '1': return 'Not Satisfied';
		default: return 'Not applicable';
		}
	},

	getRatingCode (value) {
		switch (value) {
		case '3': return 'excellent';
		case '2': return 'satisfied';
		case '1': return 'not-satisfied';
		default: return 'not-applicable';
		}
	},

	selectAllRatings (e) {
		const self = e.currentTarget;
		$(self).closest('.controls').find(`input[type=radio][rel=${$(self).attr('rel')}]:not([disabled])`).prop('checked', $(self).is(':checked'));
	},

	editRating (e) {
		const target = $(e.target.nextElementSibling);

		const obj = {};
		obj.data = '';
		obj.meta = {
			work_id: this.model.id,
			resource_id: this.model.activeResource.user.id,
			buyer_id: this.model.buyer.id,
			value: target.find('.overall').attr('value'),
			quality: target.find('.quality').attr('value'),
			professionalism: target.find('.professionalism').attr('value'),
			communication: target.find('.communication').attr('value'),
			review: target.find('.review').html()
		};

		const type = target.find('.type').attr('value');

		const rated = (type === 'resource') ? this.model.activeResource.user.id : this.model.buyer.id;
		const rater = (type === 'resource') ? this.model.buyer.id : this.model.activeResource.user.id;

		const rating = {
			ratedUserId: rated,
			raterUserId: rater,
			review: '',
			value: $(e).find('.overall').val(),
			workId: this.model.id
		};

		target.html(this.renderRatingForm(obj));
		$('.edit-rating').hide();
		$('.three-level').click(() => {
			$('.rating-extras').show();
		});
		this.submitRating(target, rating);
	},

	submitRating (e, rating) {
		const self = this;
		e.on('click', '[data-behavior="finish-review"]', () => {
			rating.review = e.find('textarea').val();
			rating.value = e.find('.rating-value:checked').val();
			rating.quality = e.find('.rating-quality:checked').val();
			rating.professionalism = e.find('.rating-professionalism:checked').val();
			rating.communication = e.find('.rating-communication:checked').val();
			if (rating.value === 0 || rating.quality === 0 || rating.professionalism === 0 || rating.communication === 0) {
				return false;
			}
			$.ajax({
				url: '/assignments/rate_assignment',
				type: 'POST',
				data: rating,
				dataType: 'json',
				success (data) {
					if (data && data.successful === true) {
						self.ratingData = rating;
						const result = self.convertRatingCode(rating);
						$('.allow-rate').hide();
						$('.last-rating-buyer').hide();
						e.html($('#rating-result-tmpl').tmpl({ meta: result }).html());
					} else {
						_.each(data.messages, (theMessage) => {
							wmNotify({
								message: theMessage,
								type: 'danger'
							});
						});
						e.find('.rating-actions .cancel').click();
					}
				},
				complete () {
					e.find('.rating-actions .cancel').click();
				}
			});
		});
	},

	addClientRating () {
		this.addRating('client');
	},

	addResourceRating () {
		this.addRating('resource');
	},

	renderRatingForm (obj) {
		return $('#rating-form-cell-tmpl').tmpl({ data: obj.data, meta: obj.meta }).html();
	},

	renderRatingResult (data) {
		return $('#rating-result-tmpl').tmpl(data).html();
	},

	actionFollow (e) {
		e.preventDefault();
		let $followLink = $(e.currentTarget),
			url = $followLink.data('href'),
			self = this;

		$.ajax({
			url,
			type: 'get',
			dataType: 'json',
			success (response) {
				if (response.successful) {
					if (!$followLink.hasClass('is-following')) {
						self.openFollowEye($followLink);
					} else {
						self.closeFollowEye($followLink);
					}
				}
			}
		});
	},

	closeFollowEye (eye) {
		eye.removeClass('is-following').html('Follow').attr('aria-label', 'Follow this assignment');
	},

	openFollowEye (eye) {
		eye.addClass('is-following').html('Unfollow').attr('aria-label', 'Unfollow this assignment');
	},

	showSurveyResults (e) {
		e.preventDefault();

		const self = this;
		$.ajax({
			type: 'GET',
			url: `${$(e.target).parent().attr('href')}?popup=1`,
			context: this,
			success (response) {
				if (!_.isEmpty(response)) {
					wmModal({
						autorun: true,
						title: 'Survey Results',
						destroyOnClose: true,
						content: response
					});
				}
			}
		});
	},

	saveCompleteOnBehalf (e) {
		e.preventDefault();
		const button = $(e.target);
		if (button.hasClass('disabled')) {
			return;
		}
		button.addClass('disabled');
		const formFields = $('#complete_and_approve_assignment_form').serializeArray();
		const postCompleteOnBehalf = (workNumber, data) => {
			$.ajax({
				url: `/assignments/complete_work_on_behalf/${this.model.workNumber}`,
				type: 'POST',
				data,
				dataType: 'json',
				success (response) {
					if (response && response.successful) {
						location.reload();
					} else {
						_.each(response.messages, (message) => {
							wmNotify({
								message,
								type: 'danger'
							});
						});
					}
				}
			});
		};
		if (Application.Features.offlinePaymentEnabled) {

			launchOfflinePaymentsModal(
				false,
				'Agree and Approve',
				() => postCompleteOnBehalf(this.model.workNumber, formFields),
				() => button.removeClass('disabled'),
				this.options.companyName
			);
		} else {
			postCompleteOnBehalf(this.model.workNumber, formFields);
		}
	},

	initIconPlusMinus () {
		$('#overview').find('[data-toggle="collapse"]').each(function () {
			if ($(this).parent('.accordion-heading').next().hasClass('in')) {
				$(this).find('i').removeClass('icon-plus-sign').addClass('icon-minus-sign');
			} else {
				$(this).find('i').removeClass('icon-minus-sign').addClass('icon-plus-sign');
			}
		});
	},

	toggleIconPlusMinus (e) {
		e.preventDefault();
		const active = $(e.target);

		if (active.find('i').hasClass('icon-minus-sign')) {
			active.find('i').removeClass('icon-minus-sign').addClass('icon-plus-sign');
		} else {
			active.find('i').removeClass('icon-plus-sign').addClass('icon-minus-sign');
		}
	},

	toggleIcon (e) {
		e.preventDefault();
		const active = $(e.target);

		if (active.hasClass('icon-minus-sign')) {
			active.removeClass('icon-minus-sign').addClass('icon-plus-sign');
		} else {
			active.removeClass('icon-plus-sign').addClass('icon-minus-sign');
		}
	},

	showOverview () {
		// Run autoresizeTextarea on custom field and document fields
		this.$('.field_value, .asset-description').autoresizeTextarea();
	},

	applyFormSubmit (e) {
		if (Application.Features.offlinePaymentEnabled) {
			e.preventDefault();
			const formFields = $('#apply-form').serializeArray();
			const button = $(e.target);

			if (button.hasClass('disabled')) {
				return;
			}
			button.addClass('disabled');

			const postApplyForAssignment = (workNumber, data) => {
				$.ajax({
					url: `/assignments/apply/${workNumber}`,
					type: 'POST',
					data,
					dataType: 'json',
					success (response) {
						if (response && response.successful) {
							location.reload();
						} else {
							_.each(response.messages, (message) => {
								wmNotify({
									message,
									type: 'danger'
								});
							});
							button.removeClass('disabled');
						}
					}
				});
			};

			launchOfflinePaymentsModal(
				true,
				'Agree and Apply',
				() => postApplyForAssignment(this.model.workNumber, formFields),
				() => button.removeClass('disabled'),
				this.options.companyName
			);
		} else if ($.browser.msie) {
			$('input[placeholder="Select Date"]').each(function () {
				const input = this;
				if (input.value === 'Select Date') {
					input.value = '';
				}
			});
			$('input[placeholder="Select Time"]').each(function () {
				const input = this;
				if (input.value === 'Select Time') {
					input.value = '';
				}
			});
		}
	},

	promptDeclineNegotiation (e) {
		e.preventDefault();

		const button = $(e.target);

		wmModal({
			autorun: true,
			title: 'Decline Negotiation',
			destroyOnClose: true,
			content: $('#decline_negotiation').html()
		});

		$('#decline_negotiation_id').val(button.attr('data-negotiation-id'));
		$('#decline_negotiation_note').val('');
	},

	removeLabelModal (e) {
		e.preventDefault();

		const link = $(e.target).closest('a');

		if ($(e.currentTarget).is('.disabled')) {
			return false;
		}

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

	toggleInlineEditing (e) {
		e.preventDefault();

		const $target = $(e.currentTarget);
		const $type = $target.data('type');
		const $section = $($target.data('container'));
		$section.find('.inline-contain').toggle();

		if ($type === 'externalId') {
			$section.find('.dn').toggle();
		} else {
			$section.find('.wysiwyg').toggle();
		}
	},
	inlineUpdate (e) {
		const {
			workNumber
		} = this.model;
		const $target = $(e.currentTarget);
		const $type = $target.data('type');
		let $newHtml;
		if ($type === 'externalId')			{
			$newHtml = $($target.data('editor')).val();
		}		else			{
			$newHtml = $($target.data('editor')).wysiwyg('getContent');
		}

		$.ajax({
			url: `/assignments/save/${workNumber}`,
			type: 'POST',
			dataType: 'json',
			data: {
				type: $type,
				newHtml: $newHtml
			},
			success: (response) => {
				if (response.successful) {
					this.toggleInlineEditing(e);
					if ($type === 'description') {
						this.descriptionView.fetchAndRender({ workNumber });
					} else if ($type === 'externalId') {
						$('#externalId-text').val($newHtml);
						$newHtml = `<b>${response.data.uniqueIdDisplayName}</b>: ${$newHtml}`;
						$('.externalId-container').html($newHtml);
					} else {
						$('.special-container').html($newHtml);
					}
					wmNotify({ message: response.messages[0] });
				} else {
					wmNotify({
						message: response.messages[0],
						type: 'danger'
					});
				}
			}
		});
	},

	showBlockClient (e) {
		e.preventDefault();

		const form = $('#block_client_form');
		const action = form.attr('action');
		form.attr('action', action.replace(/\/user\/block_client.*$/, `/assignments/block_client/${this.model.workNumber}`));

		wmModal({
			autorun: true,
			title: 'Block Client',
			destroyOnClose: true,
			content: $('#block_client_dialog_container').html()
		});
	},

	showMessagesTab: _.once(function () {
		window.App = {};

		let activeWorker = null;
		if (this.model.activeResource) {
			activeWorker = UsersCollection.add({
				id: this.model.activeResource.user.userNumber,
				firstName: this.model.activeResource.user.name.firstName,
				lastName: this.model.activeResource.user.name.lastName,
				thumbnail: this.model.activeResource.user.avatarSmall ? this.model.activeResource.user.avatarSmall.uri : null,
				isWorker: true
			}, { parse: true });
		}

		new MessagesView(_.defaults({
			questions: new MessagesCollection([], {
				id: parseInt(this.model.workNumber, 10),
				hasQuestions: true,
				isResource: this.options.auth.isResource
			}),
			messages: new MessagesCollection([], { id: parseInt(this.model.workNumber, 10) }),
			status: this.options.model.status.code,
			activeWorker,
			companyId: this.options.companyId
		}, this.options.auth));
	}),

	showActivityTab: _.once(function () {
		new ActivitiesView({
			collection: new ActivitiesCollection([], { id: parseInt(this.model.workNumber, 10) })
		});
	}),

	loadWorkers: _.once(function () {
		this.workers = new WorkerListView({
			collection: new WorkerListCollection([], {
				id: this.model.workNumber,
				status: this.model.status.code,
				disablePriceNegotiation: this.model.configuration.disablePriceNegotiation,
				isBuyerAuthorizedToApproveCounter: this.options.auth.isBuyerAuthorizedToApproveCounter,
				isDeputy: this.options.auth.isDeputy,
				isIndividualBundledAssignment: this.options.auth.isIndividualBundledAssignment,
				isParentBundle: this.options.auth.isParentBundle
			}),
			isDispatcher: this.options.isDispatcher,
			isAdmin: this.options.isAdmin,
			assignToFirstResource: this.options.assignToFirstResource,
			companyName: this.options.companyName,
			currentUserCompanyName: this.options.currentUserCompanyName,
			paymentTime: this.options.paymentTime,
			disableBulkActions: this.options.isDispatcher,
			resourceType: 'workers',
			assignment: this.model,
			isInternal: this.options.isInternal,
			showAssignButton: this.options.showAssignButton,
			isSent: this.options.isSent,
			showActions: this.options.showActions,
			showVendorActions: this.options.showVendorActions,
			showBundleActions: this.options.showBundleActions,
			bundleTitle: this.options.bundleTitle,
			bundleId: this.options.bundleId,
			hasInvitedAtLeastOneVendor: this.options.hasInvitedAtLeastOneVendor,
			showSort: true
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
			isInternal: this.options.isInternal,
			showAssignButton: this.options.showAssignButton,
			isSent: this.options.isSent,
			showActions: this.options.showActions,
			showVendorActions: this.options.showVendorActions,
			showBundleActions: this.options.showBundleActions,
			bundleTitle: this.options.bundleTitle,
			hasInvitedAtLeastOneVendor: this.options.hasInvitedAtLeastOneVendor,
			el: '#vendors-bucket',
			showSort: false
		});
	}),

	showVendorsTab () {
		this.$('input[name="workers"]').prop('checked', false);
		this.$('input[name="vendors"]').prop('checked', true);
		this.$('#workers-bucket').hide();
		this.$('#vendors-bucket').show();
	},

	showWorkersTab () {
		this.$('input[name="vendors"]').prop('checked', false);
		this.$('input[name="workers"]').prop('checked', true);
		this.$('#workers-bucket').show();
		this.$('#vendors-bucket').hide();
	},

	// *
	// **
	// *** Dispatcher events
	toggleSubmitButtonText (event) {
		const form = $(event.currentTarget).closest('form');
		const config = form.find('[ref=configuration]');
		if (config.is(':visible')) {
			this.$('#dispatcher-accept').text('Counteroffer on Behalf');
			this.counteroffer = true;
		} else {
			this.$('#dispatcher-accept').text('Accept on Behalf');
			this.counteroffer = false;
		}
	},

	dispatcherShowBlockClient (event) {
		event.preventDefault();
		this.dispatcherBlockClientModal = wmModal({
			autorun: true,
			title: ' ',
			destroyOnClose: true,
			root: '#apply-form',
			content: BlockCompanyTemplate()
		});
	},

	dispatcherBlockClient (event) {
		event.preventDefault();
		const blockClient = new BlockClientModel({
			workId: this.model.workNumber
		});
		blockClient.save();
		this.dispatcherBlockClientModal.destroy();
	},

	dispatcherApply (event) {
		event.preventDefault();
		this.applyFormSubmit();
		const data = $('#apply-form').serializeObject();
		const application = new ApplyAssignmentModel(_.extend(data, {
			workId: this.model.workNumber
		}));
		application.save({}, {
			success: _.bind(function (response) {
				if (response.attributes.successful) {
					wmNotify({ message: response.attributes.messages });
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

	dispatcherAccept (event) {
		event.preventDefault();

		if (this.counteroffer) {
			this.applyFormSubmit();
			const data = $('#apply-form').serializeObject();
			const counteroffer = new NegotiationModel(_.extend(data, {
				workId: this.model.workNumber
			}));
			counteroffer.save({}, {
				success () {
					location.reload();
				}
			});
		} else {
			const accept = new AcceptAssignmentModel({
				workId: this.model.workNumber,
				workerNumber: parseInt(this.$('#workerNumber').val(), 10)
			});
			accept.save({}, {
				success () {
					location.reload();
				}
			});
		}
	},

	showModal (e) {
		e.preventDefault();
		if ($(e.currentTarget).is('.disabled')) {
			return false;
		}

		$.ajax({
			type: 'GET',
			url: e.currentTarget.href,
			context: this,
			success (response) {
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

		return false;
	},

	showEditLocationModal (event) {
		event.preventDefault();
		let clientContacts;

		function preview_location_contact (id) {
			const selContact = clientContacts[id];
			const isEmptyClientCompany = $('[name="isEmptyClientCompany"]').val() === 'true';

			$('#onsite-contact-edit-flag').val('');

			if (typeof selContact !== 'undefined') {
				$('#onsite-firstname-selected').html(selContact.first_name);
				$('#onsite-lastname-selected').html(selContact.last_name);
				$('#onsite-phone-selected').html(selContact.work_phone);
				$('#onsite-email-selected').html(selContact.email);
				$('#onsite-contact-selected').show();
				$('#onsite-contact').hide();

				if (isEmptyClientCompany) {
					$('#onsite-contact-edit').show();
				}
			} else if (id == 'new') {
				$('#onsite-contact-selected').hide();
				$('#onsite-contact').show();

				if (isEmptyClientCompany) {
					$('#onsite-contact-edit').hide();
					$('#onsite-contact-edit-flag').val('1');
				}
			} else {
				$('#onsite-contact-selected').hide();
				$('#onsite-contact').hide();

				if (isEmptyClientCompany) {
					$('#onsite-contact-edit').hide();
				}
			}
		}

		function preview_secondary_location_contact (id) {
			const selContact = clientContacts[id];
			const isEmptyClientCompany = $('[name="isEmptyClientCompany"]').val() === 'true';
			$('#onsite-secondary-contact-edit-flag').val('');

			if (typeof selContact !== 'undefined') {
				$('#onsite-secondary-firstname-selected').html(selContact.first_name);
				$('#onsite-secondary-lastname-selected').html(selContact.last_name);
				$('#onsite-secondary-phone-selected').html(selContact.work_phone);
				$('#onsite-secondary-email-selected').html(selContact.email);

				$('#onsite-secondary-contact-selected').show();
				$('#onsite-secondary-contact').hide();

				if (isEmptyClientCompany) {
					$('#onsite-secondary-contact-edit').show();
				}
			} else if (id == 'new') {
				$('#onsite-secondary-contact-selected').hide();
				$('#onsite-secondary-contact').show();

				if (isEmptyClientCompany) {
					$('#onsite-secondary-contact-edit').hide();
					$('#onsite-secondary-contact-edit-flag').val('1');
				}
			} else {
				$('#onsite-secondary-contact-selected').hide();
				$('#onsite-secondary-contact').hide();

				if (isEmptyClientCompany) {
					$('#onsite-secondary-contact-edit').hide();
				}
			}
		}

		function populate_location_contact_form (id) {
			const selContact = clientContacts[id];

			if (typeof selContact !== 'undefined') {
				$('#onsite-firstname').val(selContact.first_name);
				$('#onsite-lastname').val(selContact.last_name);
				$('#onsite-phone').val(selContact.work_phone);
				$('#onsite-email').val(selContact.email);

				if (typeof selContact.phone_id !== 'undefined') {
					$('#onsite-phone-id').val(selContact.phone_id);
				}				else {
					$('#onsite-phone-id').val('');
				}
				if (typeof selContact.email_id !== 'undefined') {
					$('#onsite-email-id').val(selContact.email_id);
				}				else {
					$('#onsite-email-id').val('');
				}

				$('#onsite-contact-selected').hide();
				$('#onsite-contact').show();
			} else {
				$('#onsite-firstname').val('');
				$('#onsite-lastname').val('');
				$('#onsite-phone').val('');
				$('#onsite-email').val('');
				$('#onsite-phone-id').val('');
				$('#onsite-email-id').val('');

				$('#onsite-contact-selected').hide();
				$('#onsite-contact').hide();
			}

			$('#onsite-contact-edit-flag').val('1');
		}

		function populate_secondary_location_contact_form (id) {
			const selContact = clientContacts[id];

			if (typeof selContact !== 'undefined') {
				$('#onsite-secondary-firstname').val(selContact.first_name);
				$('#onsite-secondary-lastname').val(selContact.last_name);
				$('#onsite-secondary-phone').val(selContact.work_phone);
				$('#onsite-secondary-email').val(selContact.email);
				if (typeof selContact.phone_id !== 'undefined') {
					$('#onsite-secondary-phone-id').val(selContact.phone_id);
				}				else {
					$('#onsite-secondary-phone-id').val('');
				}
				if (typeof selContact.email_id !== 'undefined') {
					$('#onsite-secondary-email-id').val(selContact.email_id);
				}				else {
					$('#onsite-secondary-email-id').val('');
				}

				$('#onsite-secondary-contact-selected').hide();
				$('#onsite-secondary-contact').show();
			} else {
				$('#onsite-secondary-firstname').val('');
				$('#onsite-secondary-lastname').val('');
				$('#onsite-secondary-phone').val('');
				$('#onsite-secondary-email').val('');
				$('#onsite-secondary-phone-id').val('');
				$('#onsite-secondary-email-id').val('');

				$('#onsite-secondary-contact-selected').hide();
				$('#onsite-secondary-contact').hide();
			}

			$('#onsite-secondary-contact-edit-flag').val('1');
		}

		$.ajax({
			type: 'GET',
			url: event.currentTarget.href,
			context: this,
			success (response) {
				if (!_.isEmpty(response)) {
					wmModal({
						autorun: true,
						title: $(event.currentTarget).attr('title') || $(event.currentTarget).text(),
						destroyOnClose: true,
						content: response
					});

					$.unescapeHTML = function (html) {
						return $('<div/>').html(html).text();
					};

					$.unescapeAndParseJSON = function (json) {
						return $.parseJSON($.unescapeHTML(json));
					};

					clientContacts = $.unescapeAndParseJSON($('#json_client_contacts').html());

					const onsiteContactDropdownElem = $('#onsite-contact-dropdown');
					const onsiteSecondaryContactDropdownElem = $('#onsite-secondary-contact-dropdown');
					onsiteContactDropdownElem.change(function () {
						preview_location_contact($(this).val());
					});

					onsiteSecondaryContactDropdownElem.change(function () {
						preview_secondary_location_contact($(this).val());
					});

					preview_location_contact(onsiteContactDropdownElem.val());
					preview_secondary_location_contact(onsiteSecondaryContactDropdownElem.val());
					populate_location_contact_form(onsiteContactDropdownElem.val());
					populate_secondary_location_contact_form(onsiteSecondaryContactDropdownElem.val());

					$('#onsite-contact-edit').click(() => {
						populate_location_contact_form(onsiteContactDropdownElem.val());
					});

					$('#onsite-secondary-contact-edit').click(() => {
						populate_secondary_location_contact_form(onsiteSecondaryContactDropdownElem.val());
					});
				}
			}
		});
	},

	flagRating (event) {
		$.post(`/ratings/flag/${$(event.currentTarget).data('id')}`)
			.done((response) => {
				wmNotify({
					message: response.message
				});
			})
			.fail((response) => {
				wmNotify({
					message: response.message,
					type: 'danger'
				});
			});
	},

	navInviteMoreWorkers () {
		analytics.track('Assignment Details', {
			action: 'Invite Workers Clicked',
			source: 'nav'
		});
	},

	workersInviteMoreWorkers () {
		analytics.track('Assignment Details', {
			action: 'Invite Workers Clicked',
			source: 'workers'
		});
	},

	sendWorkerDecline () {
		this.confirmModal = wmModal({
			autorun: true,
			title: 'Decline Assignment',
			destroyOnClose: true,
			content: ConfirmActionTemplate({
				message: 'Are you sure you want to decline this assignment?'
			})
		});

		$('.cta-confirm-yes').on('click', _.bind(function () {
			$.post(`/assignments/reject/${this.options.model.workNumber}`, (result) => {
				function redirect (url, msg, type) {
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
				}

				if (result.success) {
					redirect('/assignments', [result.success], 'success');
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
	}
});

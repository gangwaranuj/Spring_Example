import fetch from 'isomorphic-fetch';
import $ from 'jquery';
import 'jquery-ui';
import Backbone from 'backbone';
import _ from 'underscore';
import 'underscore.inflection';
import React from 'react';
import ReactDOM, { render } from 'react-dom';
import '../dependencies/jquery.serializeObject';
import SearchModesModel from './search_modes_model';
import UserProfile from '../profile/profile_model';
import WorkerListCollection from './worker_list_collection';
import GroupsCollection from './groups_collection';
import TestsCollection from './tests_collection';
import RowView from './row_view';
import UserProfileModal from './user_profile_modal_view';
import GroupsCartView from './groups_cart_view';
import AcceptAssignmentModel from '../assignments/accept_assignment_model';
import ApplyAssignmentModel from '../assignments/apply_assignment_model';
import NegotiationModel from '../assignments/negotiation_model';
import NegotiateView from '../assignments/negotiate_view';
import BlockClientModel from '../assignments/block_client_model';
import PaginationView from '../pagination/pagination_view';
import wmSelect from '../funcs/wmSelect';
import wmNotify from '../funcs/wmNotify';
import wmModal from '../funcs/wmModal';
import wmActionMenu from '../funcs/wmActionMenu';
import DrawerTemplate from './templates/drawer.hbs';
import CartTemplate from './templates/cart.hbs';
import SearchBulkModal from './templates/bulk_group_modal.hbs';
import BulkTestModal from './templates/bulk_test_modal.hbs';
import UserProfileModalContainer from './templates/user_profile_modal_container.hbs';
import DispatcherTemplate from '../assignments/templates/details/dispatcher/block-company.hbs';
import AcceptApplyModal from '../assignments/templates/details/dispatcher/accept-apply_modal.hbs';
import getCSRFToken from '../funcs/getCSRFToken';
import SearchTracker from './search_tracker';
import Application from '../core';

const toggleSendButton = (
	buttonSelector = 'button -bulk-action send-work bulk'
) => {
	const buttonEl = document.getElementsByClassName(buttonSelector)[0];
	if (buttonEl) {
		buttonEl.disabled = !buttonEl.disabled;
	}
};

// TODO[tim-mc] Write utility function for async loading modules
const loadAssignmentCreationModal = async() => {
	const module = await import(/* webpackChunkName: "newAssignmentCreation" */ '../assignments/creation_modal');
	return module.default;
};
const loadWMCompanyProfileSPA = async() => {
	const module = await import(/* webpackChunkName: "WMCompanyProfileSPA" */ 'wm-company-profile-spa');
	return module.default;
};

export default Backbone.View.extend({
	el: '#cart',
	modal: $('#cart-modal'),
	toolbar: '.search-controls',

	events: {
		'click .tests-quick-action': 'loadTests',
		'click .groups-quick-action': 'loadGroups',
		'click .group-modal': 'showBulkGroupModal',
		'click .test-modal': 'showBulkTestModal',
		'click .add-to-network': 'pushToWorkerPool',
		'click .remove-from-network': 'removeWorkerFromWorkerPool',
		'click .send-work': 'pushToWork',
		'click .send-assignment': 'sendWorkerAssignment',
		'click .profile-card--invite-group': 'pushToGroup',
		'click .profile-card--invite-test': 'pushToTest',
		'click .profile-card--add-comment': 'pushComment',
		'click .profile-card--view-profile': 'showProfile',
		'click .profile-card--name': 'showProfile',
		'click .profile-card--avatar': 'showProfile',
		'click input[name="profile"]': 'checkWorker',
		'change #select_all': 'handleSelectAll',
		'click .selection_count': 'showCart',
		'click .internal-export-csv': 'exportToCsv',
		'click .dispatch-forward': 'dispatcherForward',
		'click .dispatch-accept': 'dispatcherInvitation',
		'click .dispatch-apply': 'dispatcherSendApplication',
		'click .profile-card--route-assignment': 'sendVendorAssignment'
	},

	initialize (options) {
		this.options = _.extend({
			limit: 12,
			workNumber: null
		}, options);

		this.collection = new WorkerListCollection();
		this.groupsCollection = new GroupsCollection();
		this.testsCollection = new TestsCollection();
		this.searchModes = new SearchModesModel();
		this.selectAll = this.el.querySelector('#select_all');
		this.$searchResults = $('#search_results');
		this.searchResultsCollection = [];
		this.drawer = DrawerTemplate;
		this.list = CartTemplate;
		this.testSelect = '.test-select';
		this.bulkTestSelect = '.bulk-test-select';
		this.groupSelect = '.group-select';
		this.bulkGroupSelect = '.bulk-group-select';
		this.cartList = '.selected-workers ul';
		this.drawerDiv = '#cart-drawer';
		this.searchType = this.capitalize(this.options.searchType);
		this.searchTracker = new SearchTracker();

		if (this.options.mode === this.searchModes.get('groupDetail')) {
			// TODO[any]: correct usage below
			new GroupsCartView({ // eslint-disable-line no-new
				groupId: this.options.groupId,
				collection: this.collection
			});
		}

		Backbone.Events.on('searchResultsLoaded', (data) => {
			this.searchResultsCollection = _.pluck(data, this.searchType === 'Vendors' ? 'companyNumber' : 'userNumber');
		});

		Backbone.Events.on('resetWorkers', this.resetWorkers, this);
		Backbone.Events.on('deselectAll', this.deselectAllWorkers, this);

		Backbone.Events.on('searchTypeChanged', (searchType) => {
			this.searchType = this.capitalize(searchType);
			this.selectAll.MaterialCheckbox.uncheck();
			this.collection.reset();
			this.updateSelectCount();
			this.buildDrawer();
		});

		// ********* Show/Hide bulk actions toolbar code ***********
		let lastScrollY;
		let scheduledAnimationFrame;

		function readAndUpdatePage () {
			const shouldShow = lastScrollY > 80;
			this.$(this.toolbar).toggleClass('-fixed', shouldShow);
			scheduledAnimationFrame = false;
		}

		function onScroll () {
			lastScrollY = window.scrollY;

			if (scheduledAnimationFrame) {
				return;
			}

			scheduledAnimationFrame = true;
			requestAnimationFrame(_.bind(readAndUpdatePage, this));
		}

		$(window).scroll(_.bind(onScroll, this));
		// ********************************************************

		this.buildDrawer();

		wmActionMenu();

		Application.Events.on('search:changePage', () => {
			const selection = this.collection.pluck('userNumber');
			selection.forEach((selectedUserNumber) => {
				const parentEl = document.querySelector(`.profile-card--photo[data-usernumber="${selectedUserNumber}"]`);
				if (parentEl !== null) {
					parentEl.querySelector('.mdl-checkbox').MaterialCheckbox.check();
				}
			});

			const checkSelectAll = this.areAllChecked('.profile-card--photo input[type="checkbox"]');

			if (this.selectAll && this.selectAll.MaterialCheckbox) {
				if (checkSelectAll) {
					this.selectAll.MaterialCheckbox.check();
				} else {
					this.selectAll.MaterialCheckbox.uncheck();
				}
			}
		});
	},

	capitalize (string) {
		return string.charAt(0).toUpperCase() + string.substring(1);
	},

	showCart (e) {
		e.preventDefault();
		if (this.hasEmptyCart()) {
			return;
		}
		this.modal.html(this.list({
			message: this.options.message,
			mode: this.options.mode,
			collection: this.collection
		}));

		this.cartModal = wmModal({
			title: `${this.collection.length + _.pluralize(' Talent', this.collection.length)} Selected`,
			content: this.modal.html(),
			destroyOnClose: true
		});
		this.cartModal.show();
		this.pagination = new PaginationView({
			limit: this.options.limit,
			el: '.cart-modal .pagination'
		});
		this.pagination.setTotal(this.collection.length);
		this.pagination.delegateEvents();

		this.populateCartItems();

		this.pagination.bind('pagination:next', () => {
			this.populateCartItems();
		});

		this.pagination.bind('pagination:previous', () => {
			this.populateCartItems();
		});

		$('.selected-workers').on('click', '.remove-all', _.bind(this.deselectAllWorkers, this));
		analytics.track('Worker Search', {
			action: 'Render cart',
			type: this.searchType
		});
	},

	populateCartItems () {
		$(this.cartList).html('');
		if (this.collection.length) {
			_.each(
				this.collection.models.slice(
					this.pagination.getStart(), this.pagination.getStart() + this.pagination.getLimit()
				),
				this.addOne,
				this
			);
		}
		this.pagination.render();
	},

	buildDrawer () {
		this.$(this.drawerDiv).html(this.drawer({
			isGroupDetail: this.options.mode === 'group-detail',
			isAssignment: this.options.mode === 'assignment',
			mode: this.options.mode,
			auto_generated: this.options.auto_generated,
			export_csv: this.options.export_csv,
			isVendor: this.searchType === 'Vendors'
		}));
	},

	loadTests (e) {
		const $target = $(e.currentTarget);
		const isBulk = $target.hasClass('bulk');
		const $select = isBulk ? $(this.bulkTestSelect) : $target.closest('.profile-card').find(this.testSelect);

		if (this.testsCollection.isEmpty()) {
			this.testsCollection.fetch().then((response) => {
				if (!_.isEmpty(response)) {
					this.renderTestSelect($select, response);
				} else {
					wmNotify({
						message: 'You have yet to create a test. Please create one to use this feature.',
						type: 'danger'
					});
				}
			});
		} else if (!$select.find('option').length) {
			this.renderTestSelect($select, this.testsCollection.toJSON());
		}
	},

	renderTestSelect (select, data) {
		wmSelect({ selector: select }, {
			valueField: 'id',
			labelField: 'name',
			placeholder: 'Select or type to choose...',
			searchField: ['id', 'name'],
			options: data || [],
			render: {
				option (item, escape) {
					return `<div class="option">${escape(_.unescape(item.name))}</div>`;
				},
				item (item, escape) {
					return `<div>${escape(_.unescape(item.name))}</div>`;
				}
			}
		});
	},

	loadGroups (e) {
		const $target = $(e.currentTarget);
		const isBulk = $target.hasClass('bulk');
		const $select = isBulk ? $(this.bulkGroupSelect) : $target.closest('.profile-card').find(this.groupSelect);

		if (this.groupsCollection.isEmpty()) {
			this.groupsCollection.fetch().then((response) => {
				if (!_.isEmpty(response.data.groups)) {
					this.renderGroupSelect($select, response.data.groups);
				} else {
					wmNotify({
						message: 'You have yet to create a talent pool. Please create one to use this feature.',
						type: 'danger'
					});
				}
			});
		} else if (!$select.find('option').length) {
			this.renderGroupSelect($select, this.groupsCollection.toJSON());
		}
	},

	renderGroupSelect (select, data) {
		wmSelect({ selector: select }, {
			valueField: 'id',
			labelField: 'name',
			placeholder: 'Select or type to choose...',
			searchField: ['id', 'name'],
			options: data || [],
			onChange: this.handleGroupChange,
			render: {
				option (item, escape) {
					return `<div class="option" data-privacy=${escape(item.isPublic)}>${escape(_.unescape(item.name))}</div>`;
				},
				item (item, escape) {
					return `<div>${escape(_.unescape(item.name))}</div>`;
				}
			}
		});
	},

	showBulkGroupModal (e) {
		e.preventDefault();

		if (this.hasEmptyCart()) {
			return;
		}

		this.bulkGroupModal = this.bulkGroupModal || wmModal({
			title: 'Bulk Talent Pool Invite',
			root: this.el,
			template: SearchBulkModal
		});
		this.loadGroups(e);
		this.bulkGroupModal.show();
	},

	showBulkTestModal (e) {
		e.preventDefault();

		if (this.hasEmptyCart()) {
			return;
		}

		this.bulkTestModal = this.bulkTestModal || wmModal({
			title: 'Bulk Test Invite',
			root: this.el,
			template: BulkTestModal
		});
		this.loadTests(e);
		this.bulkTestModal.show();
	},

	pushToWorkerPool (event) {
		event.preventDefault();

		const $target = $(event.currentTarget);
		const isBulk = $target.hasClass('bulk');

		if (isBulk && this.hasEmptyCart()) {
			return;
		}

		$.ajax({
			context: this,
			url: '/search/cart/push_to_worker_pool',
			type: 'POST',
			data: {
				selected_workers: isBulk ? this.collection.pluck('userNumber') : [$target.data('usernumber')]
			},
			dataType: 'json',
			success (data) {
				wmNotify({
					message: isBulk ? data.messages[0] : 'This worker was added to your worker pool.',
					type: data.successful ? 'success' : 'danger'
				});
				this.toggleNetworkIcon(event.target.offsetParent, false);
			},
			complete: () => {
				if (isBulk) {
					this.searchTracker.trackAction('pushToWorkerPool', null);
				} else {
					this.searchTracker.trackAction('pushToWorkerPool', null, $target.data('usernumber'), this.searchResultsCollection);
				}
			}
		});
	},

	removeWorkerFromWorkerPool (event) {
		event.preventDefault();

		const userNumber = $(event.currentTarget).data('usernumber');
		$.ajax({
			context: this,
			url: '/relationships/removefromlane',
			dataType: 'json',
			data: JSON.stringify({ userNumber }),
			type: 'POST',
			contentType: 'application/json',
			success (response) {
				wmNotify({
					message: response.message
				});
				this.toggleNetworkIcon(event.target.offsetParent, true);
			},
			error (response) {
				wmNotify({
					message: response.message,
					type: 'danger'
				});
			},
			complete: () => {
				this.searchTracker.trackAction('removeWorkerFromWorkerPool', null, userNumber, this.searchResultsCollection);
			}
		});
	},

	toggleNetworkIcon (element, shouldToggleAdd) {
		element.setAttribute('aria-label', shouldToggleAdd ? 'Add to Network' : 'Remove from Network');
		element.classList.toggle('remove-from-network', !shouldToggleAdd);
		element.classList.toggle('add-to-network', shouldToggleAdd);
		element.setAttribute('id', shouldToggleAdd ? 'add-to-network-submit' : 'remove-from-network-submit');

		const icon = element.querySelector('.wm-icon-checkmark-circle, .wm-icon-plus');
		icon.classList.toggle('wm-icon-checkmark-circle', !shouldToggleAdd);
		icon.classList.toggle('wm-icon-plus', shouldToggleAdd);
	},

	pushToWork (e) {
		e.preventDefault();
		toggleSendButton();
		if (this.hasEmptyCart()) {
			toggleSendButton();
			return;
		}
		if (this.collection.pluck('userNumber').length > 1 && this.options.mode !== 'assignment') {
			wmNotify({
				message: `Please select one ${_.pluralize(this.searchType, 1)} to send an assignment to.`,
				type: 'danger'
			});
			toggleSendButton();
			return;
		}

		this.searchTracker.trackAction('pushToWork', this.options.mode === 'assignment' ? this.options.workNumber : null);

		if (this.options.mode === 'assignment') {
			analytics.track('Worker Search', {
				action: 'Assigned work',
				type: this.searchType
			});
			const workerNumbers = this.getUserNumbersByUserType('WORKER');
			const vendorNumbers = this.getUserNumbersByUserType('VENDOR');
			if (workerNumbers.length && vendorNumbers.length) {
				this.pushToWorkRequest(this.options.workNumber, workerNumbers, 'WORKER')
					.then((workerRes) => {
						if (workerRes.redirect) {
							let messages = workerRes.messages;
							this.pushToWorkRequest(this.options.workNumber, vendorNumbers, 'VENDOR')
								.then((vendorRes) => {
									if (vendorRes.redirect) {
										messages = messages.concat(vendorRes.messages);
										if (workerRes.successful === vendorRes.successful) {
											// if both worker and vendor requests success or error,
											// use worker res as redirect
											this.redirect(workerRes.redirect, messages, workerRes.successful ? 'success' : 'error');
										} else if (!workerRes.successful) {
											this.redirect(workerRes.redirect, messages, 'error');
										} else {
											this.redirect(vendorRes.redirect, messages, 'error');
										}
									} else {
										messages.push('A problem occurred while adding vendors!');
										this.redirect(`/assignments/contact/${this.options.workNumber}`, messages, 'error');
									}
								});
						} else {
							this.redirect(`/assignments/contact/${this.options.workNumber}`, ['A problem occurred while adding workers!'], 'error');
						}
					});
			} else if (workerNumbers.length) {
				this.pushToWorkRequest(this.options.workNumber, workerNumbers, 'WORKER')
					.then((workerRes) => {
						if (workerRes.redirect) {
							this.redirect(workerRes.redirect, workerRes.messages, workerRes.successful ? 'success' : 'error');
						} else {
							this.redirect(`/assignments/contact/${this.options.workNumber}`, ['A problem occurred while adding workers!'], 'error');
						}
					});
			} else if (vendorNumbers.length) {
				this.pushToWorkRequest(this.options.workNumber, vendorNumbers, 'VENDOR')
					.then((vendorRes) => {
						if (vendorRes.redirect) {
							this.redirect(vendorRes.redirect, vendorRes.messages, vendorRes.successful ? 'success' : 'error');
						} else {
							this.redirect(`/assignments/contact/${this.options.workNumber}`, ['A problem occurred while adding vendors!'], 'error');
						}
					});
			}
		} else {
			this.redirect(`assignments/add?for=${[this.collection.pluck('userNumber')]}`);
		}
	},

	redirect (url, msg, type) {
		if (msg) {
			const message = typeof msg === 'string' ? [msg] : msg;
			const e = $("<form class='dn'></form>");
			e.attr({
				action: '/message/create',
				method: 'POST'
			});
			for (let i = 0; i < message.length; i += 1) {
				e.append(
					$('<input>').attr({
						name: 'message[]',
						value: message[i]
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

	pushToWorkRequest (workNumber, userNumbers, userType) {
		const url = `/search/cart/${userType === 'VENDOR' ? 'invite_vendor' : 'push_to_assignment'}`;
		const formData = new FormData();
		formData.append('id', workNumber);
		formData.append('selected[]', userNumbers);
		return fetch(url, {
			headers: {
				'X-CSRF-Token': getCSRFToken(),
				Accept: 'application/json, text/javascript, */*; q=0.01',
				'X-Requested-With': 'XMLHttpRequest' },
			credentials: 'same-origin',
			method: 'POST',
			body: formData
		}).then(res => res.json());
	},

	getParticipants () {
		const participants = [];
		this.collection.forEach((user) => {
			const participant = {
				number: user.get('userNumber'),
				participantType: user.get('userType'),
				requestType: 'INVITED'
			};
			participants.push(participant);
		});
		return participants;
	},

	getUserNumbersByUserType (userType) {
		return this.collection.where({ userType }).map(user => user.get('userNumber'));
	},

	dispatcherShowBlockClient (event) {
		event.preventDefault();
		this.dispatcherBlockClientModal = wmModal({
			autorun: true,
			title: ' ',
			destroyOnClose: true,
			root: '#apply-form',
			content: DispatcherTemplate()
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

	dispatcherInvitation (event) {
		const userNumber = $(event.currentTarget).data('usernumber');
		Backbone.Events.trigger('cleanNegotiationView');
		wmModal({
			autorun: true,
			title: ' ',
			destroyOnClose: true,
			content: AcceptApplyModal({
				mode: 'accept',
				companyName: this.options.companyName,
				currentUserCompanyName: this.options.currentUserCompanyName,
				paymentTime: this.options.paymentTime,
				pricingType: this.options.pricingType,
				disablePriceNegotiation: this.options.disablePriceNegotiation,
				isParentBundle: this.options.isBundle,
				work: this.options.work,
				userNumber
			})
		});
		$('#dispatcher-accept').on('click', this.dispatcherAccept.bind(this));
		$('.resource-apply-toggle').on('click', this.toggleResourceApply.bind(this));
		$('.dispatcher-show-block-client').on('click', this.dispatcherShowBlockClient.bind(this));
		$('#dispatcher-block-client').on('click', this.dispatcherBlockClient.bind(this));
	},

	dispatcherForward (event) {
		const userNumber = $(event.currentTarget).data('usernumber');
		$.ajax({
			context: this,
			url: '/search/cart/push_to_assignment',
			type: 'POST',
			data: {
				id: this.options.workNumber,
				selected: [userNumber]
			},
			dataType: 'json',
			success: (response) => {
				if (response.redirect) {
					this.redirect(response.redirect, response.messages, ((response.successful) ? 'success' : 'error'));
				} else {
					this.redirect(`/assignments/contact/${this.options.workNumber}`, ['A problem occurred while adding the candidate.'], 'error');
				}
			},
			complete: () => {
				this.searchTracker.trackAction('dispatcherForwardWork', this.options.workNumber, userNumber, this.searchResultsCollection);
			}
		});
	},
	dispatcherSendApplication (event) {
		const userNumber = $(event.currentTarget).data('usernumber');
		this.searchTracker.trackAction('dispatcherSendApplication', this.options.workNumber, userNumber, this.searchResultsCollection);
		Backbone.Events.trigger('cleanNegotiationView');
		this.applicationModal = wmModal({
			autorun: true,
			title: ' ',
			destroyOnClose: true,
			content: AcceptApplyModal({
				mode: 'apply',
				companyName: this.options.companyName,
				currentUserCompanyName: this.options.currentUserCompanyName,
				paymentTime: this.options.paymentTime,
				pricingType: this.options.pricingType,
				disablePriceNegotiation: this.collection.disablePriceNegotiation,
				isParentBundle: this.options.isBundle,
				work: this.options.work,
				userNumber
			})
		});
		$('#dispatcher-apply').on('click', this.dispatcherApply.bind(this));
		$('.resource-apply-toggle').on('click', this.toggleResourceApply.bind(this));
		$('.dispatcher-show-block-client').on('click', this.dispatcherShowBlockClient.bind(this));
		$('#dispatcher-block-client').on('click', this.dispatcherBlockClient.bind(this));
	},

	dispatcherAccept (event) {
		event.preventDefault();

		if (this.counteroffer) {
			const data = $('#apply-form').serializeObject();
			this.applyFormSubmit();
			const counteroffer = new NegotiationModel(_.extend(data, {
				workId: this.options.workNumber
			}));
			counteroffer.save({}, {
				success: () => {
					window.location = `/assignments/details/${this.options.workNumber}`;
				}
			});
		} else {
			const accept = new AcceptAssignmentModel({
				workId: this.options.workNumber,
				workerNumber: $(event.currentTarget).data('usernumber')
			});
			accept.save({}, {
				success: () => {
					window.location = `/assignments/details/${this.options.workNumber}`;
				}
			});
		}
	},

	dispatcherApply (event) {
		event.preventDefault();
		this.applyFormSubmit();
		const data = $('#apply-form').serializeObject();
		const application = new ApplyAssignmentModel(_.extend(data, {
			workId: this.options.workNumber
		}));
		application.save({}, {
			success: (response) => {
				if (response.attributes.successful) {
					window.location = `/assignments/details/${this.options.workNumber}`;
				} else {
					this.applicationModal.hide();
					wmNotify({
						message: response.attributes.messages,
						type: 'danger'
					});
				}
			}
		});
	},

	toggleResourceApply (event) {
		const form = $(event.currentTarget).closest('form');
		const config = form.find('[ref=configuration]');

		config.toggle();
		$('.toggler').toggleClass('toggled');

		if (config.is(':visible')) {
			$('#dispatcher-accept').text('Counteroffer on Behalf');
			this.counteroffer = true;
			if (!this.applyView) {
				this.applyView = new NegotiateView({ el: '#apply-form' });
			}
		} else {
			$('#dispatcher-accept').text('Accept on Behalf');
			this.counteroffer = false;
		}
	},

	applyFormSubmit () {
		$('input[placeholder="Select Time"]').each(() => {
			const input = this;
			if (input.value === 'Select Time') {
				input.value = '';
			}
		});
	},

	goToSendAssignment (userNumber, userType) {
		this.searchTracker.trackAction(
			'goToSendAssignment',
			this.options.mode === 'assignment' ? this.options.workNumber : null,
			userNumber,
			this.searchResultsCollection);
		if (this.options.mode === 'assignment') {
			this.pushToWorkRequest(this.options.workNumber, [userNumber], userType)
				.then((res) => {
					if (res.redirect) {
						this.redirect(res.redirect, res.messages, res.successful ? 'success' : 'error');
					} else {
						this.redirect(`/assignments/contact/${this.options.workNumber}`, [`A problem occurred while adding ${userType}!`], 'error');
					}
				});
		} else {
			// TODO[tim-mc] Error handling for loading module
			loadAssignmentCreationModal().then(CreationModal => new CreationModal());
		}
	},

	pushParticipantsToGroup (e) {
		const $target = $(e.currentTarget);
		const isBulk = $target.hasClass('bulk');
		const id = isBulk ? $(this.bulkGroupSelect).val() : $target.siblings('label').find(this.groupSelect).val();

		if (!id.length) {
			wmNotify({
				message: 'Please select a talent pool before sending',
				type: 'danger'
			});
			return;
		}

		const participants = isBulk ? this.getParticipants() : [{ number: $target.data('userNumber'), participantType: $target.data('userType'), requestType: 'INVITED' }];
		if (!participants.length) {
			wmNotify({
				message: 'Please select talent before sending to a group',
				type: 'danger'
			});
			return;
		}

		$.ajax({
			context: this,
			url: `/groups/invite_participants/${id}`,
			dataType: 'json',
			data: JSON.stringify(participants),
			type: 'POST',
			contentType: 'application/json',
			success (response) {
				const messages = response.messages;
				const hasMessages = Array.isArray(messages) && messages.length;

				if (hasMessages) {
					wmNotify({
						message: messages[0],
						type: response.successful ? null : 'danger'
					});
				}

				if (isBulk) {
					this.bulkGroupModal.hide();
				}
			},
			complete: () => {
				if (isBulk) {
					this.searchTracker.trackAction('pushToGroup', id);
				} else {
					this.searchTracker.trackAction('pushToGroup', id, $target.data('user-number'), this.searchResultsCollection);
				}
			}
		});
	},

	// deprecated in favor of pushParticipantsToGroup
	// TODO remove once we're 100% live with vendor in talent pools
	pushToGroup (e) {
		if (this.options.hasVendorPoolsFeature) {
			this.pushParticipantsToGroup(e);
			return;
		}

		const $target = $(e.currentTarget);
		const isBulk = $target.hasClass('bulk');
		const id = isBulk ? $(this.bulkGroupSelect).val() : $target.siblings('label').find(this.groupSelect).val();

		if (!id.length) {
			wmNotify({
				message: 'Please select a talent pool before sending',
				type: 'danger'
			});
			return;
		}

		const selectedWorkers = isBulk ? this.getUserNumbersByUserType('WORKER') : [$target.data('user-number')];
		if (!selectedWorkers.length) {
			wmNotify({
				message: 'Please select workers before sending to a group (vendors are not supported)',
				type: 'danger'
			});
			return;
		}

		$.ajax({
			context: this,
			url: `/groups/invite_workers/${id}`,
			type: 'POST',
			data: {
				selected_workers: selectedWorkers
			},
			dataType: 'json',
			success (response) {
				const message = response.messages[0];
				if (response.successful) {
					wmNotify({ message });
					if (isBulk) {
						this.bulkGroupModal.hide();
					}
				} else {
					wmNotify({
						message: _.first(response.messages),
						type: 'danger'
					});
				}
			},
			complete: () => {
				if (isBulk) {
					this.searchTracker.trackAction('pushToGroup', id);
				} else {
					this.searchTracker.trackAction('pushToGroup', id, $target.data('user-number'), this.searchResultsCollection);
				}
			}
		});
	},

	pushToTest (e) {
		const $target = $(e.currentTarget);
		const isBulk = $target.hasClass('bulk');
		const id = isBulk ? $(this.bulkTestSelect).val() : $target.siblings('label').find(this.testSelect).val();

		if (!id.length) {
			wmNotify({
				message: 'Please select a test before sending',
				type: 'danger'
			});
			return;
		}

		const selectedWorkers = isBulk ? this.getUserNumbersByUserType('WORKER') : [$target.data('user-number')];
		const selectedVendors = isBulk ? this.getUserNumbersByUserType('VENDOR') : [$target.data('user-number')];
		if (!selectedWorkers.length && !selectedVendors.length) {
			wmNotify({
				message: 'Please select workers before sending to a test (vendors are not supported)',
				type: 'danger'
			});
			return;
		}

		$.ajax({
			context: this,
			url: '/search/cart/push_to_test',
			type: 'POST',
			data: {
				id,
				selected_workers: selectedWorkers,
				selected_vendors: selectedVendors
			},
			dataType: 'json',
			success (response) {
				const message = response.messages[0];
				if (response.successful) {
					wmNotify({ message });
					if (isBulk) {
						this.bulkTestModal.hide();
					}
				} else {
					wmNotify({
						message: 'There was an error with your invite. Please try again.',
						type: 'danger'
					});
				}
			},
			complete: () => {
				if (isBulk) {
					this.searchTracker.trackAction('pushToTest', id);
				} else {
					this.searchTracker.trackAction('pushToTest', id, $target.data('user-number'), this.searchResultsCollection);
				}
			}
		});
	},

	pushComment (e) {
		const $target = $(e.currentTarget);
		const userNumber = $target.data('user-number');
		const $comment = $target.prev().val();

		if (_.isEmpty($comment)) {
			wmNotify({
				message: 'Please add a comment before submitting.',
				type: 'danger'
			});
			return;
		}

		$.ajax({
			url: '/profile/add_comment_to_user',
			type: 'POST',
			data: {
				id: userNumber,
				comment: $comment
			},
			dataType: 'json',
			success (response) {
				const message = response.messages[0];
				if (response.successful) {
					wmNotify({ message });
				} else {
					wmNotify({
						message: _.first(response.messages),
						type: 'danger'
					});
				}
			},
			complete: () => {
				this.searchTracker.trackAction('pushComment', null, userNumber, this.searchResultsCollection);
			}
		});
	},

	showProfile (event) {
		event.preventDefault();
		analytics.track('Worker Search', {
			action: 'View profile',
			type: this.searchType
		});

		const userNumber = $(event.currentTarget).data('user-number');
		const userType = $(event.currentTarget).data('user-type');
		this.searchTracker.trackAction('showProfile', null, userNumber, this.searchResultsCollection);

		// check for userType && searchType
		// searchType is linked to feature toggle for the R15 release
		if (userType === 'VENDOR' && this.searchType !== 'Vendors') {
			const csrfToken = getCSRFToken();
			this.renderVendorProfile(userNumber.toString(), csrfToken);
		} else {
			if (this.searchType === 'Vendors' && ($(event.currentTarget).hasClass('profile-card--avatar') ||
			$(event.currentTarget).hasClass('profile-card--name'))) {
				return;
			}

			if (userNumber) {
				this.getUser(userNumber);
			}
		}
	},

	getUser (userNumber) {
		$('.wm-modal--content > div').css('display', 'none');
		const userProfileId = `profile-${userNumber}`;
		const url = this.searchType === 'Vendors' ? `/profile/company/${userNumber}` : `/profile/${userNumber}`;
		const userProfileView	= $(`#${userProfileId}`);
		this.currentProfile = userNumber;
		this.$('.user-profile-popup .wm-modal--title a').attr('href', url);
		if (userProfileView.length > 0) {
			userProfileView.css({ display: 'block' });
			this.popup.show();
		} else {
			const self = this;
			this.popup = this.popup || wmModal({
				title: url,
				root: self.el,
				template: UserProfileModalContainer
			});

			this.popup.show();
			const profile = new UserProfile({
				userNumber,
				isVendor: this.searchType === 'Vendors',
				isDispatch: this.options.isDispatch
			});
			// TODO[any]: correct usage below
			new UserProfileModal({ // eslint-disable-line no-new
				model: profile,
				root: '.wm-modal .wm-modal--content',
				groups: this.groupsCollection.models,
				tests: this.testsCollection.models
			});
		}
	},

	checkWorker (e) {
		const $row = $(e.currentTarget).closest('.profile-card--photo');
		if ($(e.currentTarget).is(':checked')) {
			const user = this.getWorkerResultNameAndNumber($row);
			this.add([user]);
			this.searchTracker.trackSelection(
				[user.userNumber],
				this.searchResultsCollection,
				true,
				false
			);
		} else if (this.searchType === 'Vendors') {
			this.remove([$row.data('companynumber')]);
			this.searchTracker.trackSelection([$row.data('companynumber')], this.searchResultsCollection, false, false);
		} else {
			this.remove([$row.data('usernumber')]);
			this.searchTracker.trackSelection([$row.data('usernumber')], this.searchResultsCollection, false, false);
		}

		this.updateToggleAll();
		this.updateSelectCount();
		this.updateBulkActions();
	},

	updateBulkActions () {
		const $actions = this.$(this.drawerDiv).find('.bulk');
		// Selecting 'Send Assignment' and 'Invite to Test' as common actions
		const $commonActions = $($actions);
		const selectionHasItems = this.collection.models.length > 0;
		$commonActions.toggle(selectionHasItems);
	},
	updateSelectCount () {
		$('.selection_count').text(`${this.collection.length + _.pluralize(' Talent', this.collection.length)} Selected`);
	},

	add (userData) {
		userData.forEach((user) => {
			const worker = new Backbone.Model();
			worker.set({
				userNumber: user.userNumber,
				userName: user.userName,
				userType: user.userType
			});
			this.collection.add(worker);
		});
	},

	remove (userNumbers) {
		const modelsToBeRemoved = this.collection.filter(model => userNumbers.includes(model.get('userNumber')));
		this.collection.remove(modelsToBeRemoved);
	},

	getWorkerResultNameAndNumber ($row) {
		return {
			userName: `${$row.data('firstName')} ${$row.data('lastName')}`,
			userNumber: $row.data('usernumber'),
			userType: $row.data('usertype')
		};
	},

	hasEmptyCart () {
		if (this.collection.isEmpty()) {
			wmNotify({
				message: `Your cart is empty, please select at least one ${_.pluralize(this.searchType, 1)}.`,
				type: 'danger'
			});
			return true;
		}
		return false;
	},

	addOne (model) {
		const view = new RowView({ model });
		$(this.cartList).append(view.render().el);
	},

	handleSelectAll ({ target }) {
		const $rows = $('.profile-card--photo:has(input[type="checkbox"])');

		const isChecked = $(target).is(':checked');
		if (isChecked) {
			const userData = $rows.map((i, row) => this.getWorkerResultNameAndNumber($(row))).toArray();
			this.add(userData);
			this.searchTracker.trackSelection(_.pluck(userData, 'userNumber'), this.searchResultsCollection, true, true);
		} else {
			this.deselectAllWorkersOnPage();
		}

		const checkedValue = isChecked ? 'check' : 'uncheck';
		this.setCheckboxes(checkedValue);

		this.updateSelectCount();
		this.updateBulkActions();
	},

	deselectAllWorkers () {
		this.collection.reset();
		if (this.cartModal) { this.cartModal.hide(); }
		this.setCheckboxes('uncheck');
		this.searchTracker.trackSelection([], [], false, true);
		this.selectAll.MaterialCheckbox.uncheck();
		this.updateSelectCount();
	},

	deselectAllWorkersOnPage () {
		const checkboxesToDeselect = document.querySelectorAll('.profile-card--photo input[type="checkbox"]');
		const userNumbersToDeselect = [];

		let index = 0;
		for (index = 0; index < checkboxesToDeselect.length; index += 1) {
			const userNumberToDeselect = parseInt(checkboxesToDeselect[index].getAttribute('value'), 10);
			userNumbersToDeselect.push(userNumberToDeselect);
		}

		this.remove(userNumbersToDeselect);
		this.setCheckboxes('uncheck');
		this.searchTracker.trackSelection([], [], false, true);
	},

	resetWorkers () {
		this.deselectAllWorkers();
		Backbone.Events.trigger('loadData');
	},

	setCheckboxes (checkedValue) {
		this.$('.profile-card .mdl-checkbox').each((index, el) => el.MaterialCheckbox[checkedValue]());
	},

	updateToggleAll () {
		const checkSelectAll = this.areAllChecked('.profile-card--photo input[type="checkbox"]');

		if (checkSelectAll) {
			this.selectAll.MaterialCheckbox.check();
		} else {
			this.selectAll.MaterialCheckbox.uncheck();
		}
	},

	areAllChecked (selector) {
		const checkboxes = document.querySelectorAll(selector);

		let index = 0;

		if (!checkboxes.length) {
			return false;
		}

		for (index = 0; index < checkboxes.length; index += 1) {
			if (!checkboxes[index].checked) {
				return false;
			}
		}

		return true;
	},

	exportToCsv (event) {
		event.preventDefault();

		$.ajax({
			url: '/search/export_csv',
			type: 'POST',
			dataType: 'json',
			data: $('#filter_form').serialize()
		});

		wmNotify({ message: 'Success. You will receive an email with the file as attachment.' });
	},

	renderVendorProfile (
		companyNumber,
		csrfToken = getCSRFToken()
	) {
		loadWMCompanyProfileSPA()
			.then((WMCompanyProfileSPA) => {
				const vendorProfileDiv = document.createElement('div');
				vendorProfileDiv.setAttribute('id', 'app');
				document.body.appendChild(vendorProfileDiv);
				const unmountModal = () => {
					ReactDOM.unmountComponentAtNode(vendorProfileDiv);
				};

				render(
					<WMCompanyProfileSPA
						companyNumber={ companyNumber }
						isModal
						csrf={ csrfToken }
						onClose={ unmountModal }
					/>,
					vendorProfileDiv
				);
			});
	},

	sendWorkerAssignment (event) {
		event.preventDefault();
		const userNumber = $(event.currentTarget).parent().prev().data('user-number');
		this.goToSendAssignment(userNumber, 'WORKER');
	},

	sendVendorAssignment (event) {
		event.preventDefault();
		const userNumber = $(event.currentTarget).data('user-number');
		this.goToSendAssignment(userNumber, 'VENDOR');
	}
});

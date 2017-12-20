/* global __insp */
import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import PricingPage from '../assignments/pricing_page';
import BulkChangeCustomFieldsView from './bulk_change_custom_fields_view';
import BulkEditProjectView from './bulk_edit_project_view';
import AddMultipleNoteView from './add_multiple_notes_view';
import DeleteVoidView from './delete_assignments_view';
import BulkDownloadsView from './bulk_download_view';
import BulkRescheduleView from './bulk_reschedule_view';
import BulkLabelRemoveView from './bulk_label_remove_view';
import BulkCancelWorksView from './bulk_cancel_view';
import RemoveMultipleAttachmentsView from './bulk_remove_multiple_attachments_view';
import AddMultipleAttachmentsView from './add_multiple_attachments_view';
import CreateBundleModalView from '../bundles/create_bundle_view';
import BulkLabelView from './bulk_label_view';
import BulkRoutingView from './bulk_routing_view';
import wmSelect from '../funcs/wmSelect';
import wmNotify from '../funcs/wmNotify';
import wmActionMenu from '../funcs/wmActionMenu';
import getCSRFToken from '../funcs/getCSRFToken';
import wmModal from '../funcs/wmModal';
import BulkNoteTemplate from './templates/bulk_notes.hbs';
import BulkDownloadTemplate from './templates/bulk_download.hbs';
import DeleteAssignmentsTemplate from './templates/bulk_delete_assignments.hbs';
import BulkUpdateProjectTemplate from './templates/bulk_update_project.hbs';
import BulkAddLabelTemplate from './templates/bulk_add_label.hbs';
import CreateBundleErrorsTemplate from './templates/create_bundle_errors.hbs';
import CreateBundleTemplate from './templates/create_bundle.hbs';
import BulkRescheduleTemplate from './templates/bulk_reschedule.hbs';
import BulkRemoveLabelTemplate from './templates/bulk_remove_label.hbs';
import qq from '../funcs/fileUploader';
import '../dependencies/jquery.safeEnter';
import '../funcs/wmSearch';

const loadAssignmentCreationModal = async() => {
	const module = await import(/* webpackChunkName: "newAssignmentCreation" */ '../assignments/creation_modal');
	return module.default;
};

export default Backbone.View.extend({
	el: 'body',

	events: {
		'click #results_workids': 'toggleBulkDropdown',
		'click #apply_filters': 'applyFilters',
		'click #clear_filters': 'clearAllFilters',
		'click #include_time': 'includeTime',
		'change #date_filter': 'filterDateRange',
		'change #date_sub_filter': 'toggleDatePickers',
		'change #client_company': 'filterClientCompanyList',
		'change #project-dropdown': 'filterProject',
		'change #internal-owner-dropdown': 'filterOwner',
		'change #bundles-dropdown': 'filterBundle',
		'change #resources-dropdown': 'filterResource',
		'change #vendors-dropdown': 'filterVendor',
		'change #assigned_to_me': 'filterAssignedToMe',
		'change #dispatched_by_me': 'filterDispatchedByMe',
		'change #following': 'filterFollowing',
		'change #assignment_list_size': 'changeListSize',
		'click .assignment_actions': 'doBulkAction',
		'change #assignment_list_sorting': 'sort',
		'click #assignment_list_sorting_asc': 'sortAscending',
		'click #assignment_list_sorting_desc': 'sortDescending',
		'click #refresh_resources': 'refreshList',
		'click .advanced-filters-toggle': 'toggleAdvanced',
		'click .select-all-visible-outlet': 'selectAllVisible',
		'click #select-all': 'toggleBulkDropdown',
		'click #full-select-all': 'activateFullSelectAll',
		'click #clear-full-select-all': 'clearFullSelectAll',
		'click #show_calendar': 'showCalendar',
		'click #show_list': 'showList',
		'click .fc-button-month': 'showMonthView',
		'click .fc-button-agendaWeek': 'showWeekView',
		'click .fc-button-agendaDay': 'showDayView',
		'click .fc-button-prev': 'saveCurrentDate',
		'click .fc-button-next': 'saveCurrentDate',
		'click .fc-button-today': 'saveCurrentDate',
		'click .fc-widget-content': 'showDayView',
		'change .gear-dropdown': 'gearDropdown',
		'click .dashboard--new-assignment': 'newAssignment',
		'click #assignment-actions-copy': 'copyAssignment',
		'click #assignment-actions-edit': 'editAssignment'
	},

	initialize (options) {
		this.options = options || {};

		this.defaultFrom = options.defaultFrom;
		this.defaultTo = options.defaultTo;
		this.defaultOwner = options.defaultOwner;
		this.current_view = options.current_view;
		this.pagination_selection = options.pagination_selection;
		this.uploader = null;
		let $scheduleFrom = $('#schedule_from'),
			$scheduleThrough = $('#schedule_through'),
			dropdownSelectizeOptions = {};


		$scheduleFrom.datepicker({ dateFormat: 'mm/dd/yy' });
		$scheduleThrough.datepicker({ dateFormat: 'mm/dd/yy' });

		dropdownSelectizeOptions = {
			valueField: 'id',
			labelField: 'name',
			searchField: ['id', 'name'],
			sortField: 'name'
		};

		if (options.isBuyer) {
			this.clientCompanySelect = wmSelect({
				selector: '#client_company',
				root: this.el
			}, dropdownSelectizeOptions)[0].selectize;

			this.projectDropdownSelect =
				wmSelect({
					selector: '#project-dropdown',
					root: this.el
				}, {
					valueField: 'id',
					labelField: 'name',
					searchField: 'name',
					options: [],
					persist: false,
					create: false,
					allowEmptyOption: false,
					loadThrottle: 200,
					render: {
						option (item, escape) {
							return `<div> ${escape(item.name)} </div>`;
						},
						item (item, escape) {
							return `<div> ${escape(item.name)} </div>`;
						}
					},
					load (query, callback) {
						if (!query.length) {
							return callback();
						}

						$.ajax({
							url: '/assignments/projects_suggestion',
							type: 'GET',
							dataType: 'json',
							data: {
								term: query
							},
							error () {
								callback();
							},
							success (res) {
								callback(res);
							}
						});
					}
				})[0].selectize;

			if (!this.options.hasAvatar) {
				this.initializeFileUploader();
			}
		}

		this.resourcesDropdownSelect = wmSelect({
			selector: '#resources-dropdown',
			root: this.el
		}, {
			valueField: 'id',
			labelField: 'name',
			searchField: 'name',
			options: [],
			persist: false,
			loadThrottle: 200,
			create: false,
			allowEmptyOption: false,
			render: {
				option (item, escape) {
					return `<div>${escape(`${item.name}, ID: ${item.userNumber} | ${item.address}`)}</div>`;
				},
				item (item, escape) {
					return `<div>${escape(item.name)}</div>`;
				}
			},
			load (query, callback) {
				if (query.length < 2) {
					return callback();
				}

				$.ajax({
					url: '/assignments/assigned_resources',
					type: 'GET',
					dataType: 'json',
					data: {
						term: query
					},
					error () {
						callback();
					},
					success (res) {
						callback(res);
					}
				});
			}
		})[0].selectize;

		this.vendorsDropdownSelect = wmSelect({
			selector: '#vendors-dropdown',
			root: this.el
		}, {
			valueField: 'id',
			labelField: 'name',
			searchField: 'name',
			options: [],
			persist: false,
			loadThrottle: 200,
			create: false,
			allowEmptyOption: false,
			render: {
				option(item, escape) {
					return '<div>' + escape(item.name + ', ID: ' + item.vendorNumber + ' | ' + item.address) + '</div>';
				},
				item(item, escape) {
					return '<div>' + escape(item.name) + '</div>';
				}
			},
			load(query, callback) {
				if (!query.length) {
					return callback();
				}

				$.ajax({
					url: '/assignments/assigned_vendors',
					type: 'GET',
					dataType: 'json',
					data: {
						term: query
					},
					error() {
						callback();
					},
					success(res) {
						callback(res);
					}
				});
			}
		})[0].selectize;

		this.bundlesDropdownSelect = wmSelect({
			selector: '#bundles-dropdown',
			root: this.el
		}, {
			valueField: 'id',
			labelField: 'name',
			searchField: 'name',
			options: [],
			persist: false,
			loadThrottle: 200,
			create: false,
			allowEmptyOption: false,
			render: {
				option (item, escape) {
					return `<div>${escape(`${item.name} | ${item.internalOwner}`)}</div>`;
				},
				item (item, escape) {
					return `<div>${escape(item.name)}</div>`;
				}
			},
			load (query, callback) {
				if (!query.length) {
					return callback();
				}

				$.ajax({
					url: '/assignments/bundles_suggestion',
					type: 'GET',
					dataType: 'json',
					data: {
						term: query
					},
					error () {
						callback();
					},
					success (res) {
						callback(res);
					}
				});
			}
		})[0].selectize;

		this.internalOwnerDropdownSelect = wmSelect({
			selector: '#internal-owner-dropdown',
			root: this.el
		}, dropdownSelectizeOptions)[0].selectize;

		const $keyword = $('#keyword');
		if ($keyword.length) {
			$keyword.val('');

			$keyword.listenForEnter().bind('keypress', _.bind(function (event) {
				if (event.keyCode === 13) {
					this.applyFilters();
				}
			}, this));
		}

		if ($('#time').length) {
			let $timeFrom = $('#time_from'),
				$timeThrough = $('#time_through');

			$timeFrom.css('width', '56px');
			$timeThrough.css('width', '56px');

			$('#include_time').removeAttr('checked');
			$timeFrom.hide();
			$timeThrough.hide();
		}

		wmActionMenu();
		this.sort();

		if ($('#show_creation').length && (window.location.search.indexOf('?launchAssignmentModal') > -1)) {
			$('#show_creation').click();
		}
	},

	render () {
		$(this.pagination_selection).show(); // this has to go when bulk actions are done

		$('.select-all-visible-outlet').prop('checked', this.pagination_selection.allVisibleSelected());
		this.trigger('select:render');
	},

	newAssignment (e) {
		e.preventDefault();
		// Tag inspectlet session so we can observe usage
		if (typeof __insp !== 'undefined') {
			__insp.push(['virtualPage', 'assignment-creation-modal']);
		}
		// TODO[tim-mc] Error handling for loading module
		loadAssignmentCreationModal().then(CreationModal => new CreationModal());
	},

	copyAssignment (e) {
		e.preventDefault();
		const assignmentId = $(e.currentTarget).closest('.results-row .assignment-actions').data('assignmentNumber');
		const title = 'Copy Assignment';
		// TODO[tim-mc] Error handling for loading module
		loadAssignmentCreationModal().then(CreationModal => new CreationModal({ assignmentId, title }));
	},

	editAssignment (e) {
		e.preventDefault();
		const assignmentId = $(e.currentTarget).closest('.results-row .assignment-actions').data('assignmentNumber');
		const title = 'Edit Assignment';
		// TODO[tim-mc] Error handling for loading module
		loadAssignmentCreationModal().then(CreationModal => new CreationModal({ assignmentId, title }));
	},

	initializeFileUploader() {
		this.uploader = new qq.FileUploader({
			element: document.getElementById('file-uploader'),
			action: '/account/logoupload',
			allowedExtensions: ['jpg', 'jpeg', 'gif', 'png', 'bmp'],
			sizeLimit: 2 * 1024 * 1024, // 2MB
			CSRFToken: getCSRFToken(),
			multiple: false,
			template: $('#qq-uploader-tmpl').html(),
			onSubmit (id, fileName) {},
			onComplete: _.bind(function (id) {
				$(this.uploader._getItemByFileId(id)).remove();
			}, this),
			showMessage (message) {}
		});
	},

	selectAllVisible (e) {
		this.pagination_selection.selectAllVisible(e);
		this.render();
	},

	gearDropdown () {
		const link = $('select.gear-dropdown.action-menu').find(':selected').val();
		if (link.length) {
			window.location = link;
		}
	},

	activateFullSelectAll (e) {
		e.preventDefault();
		this.pagination_selection.handleFullSelectAll(e);
		$('#download_closeout_attachments_bulk_action').hide();
		$('#delete_void_bulk_action').hide();
		$('#custom_fields_bulk_action').hide();
		$('#edit_projects_bulk_action').hide();
		$('#reschedule_bulk_action').hide();
		$('#reprice_bulk_action').hide();
	},

	clearFullSelectAll (e) {
		e.preventDefault();
		this.pagination_selection.clear(e);
		this.resetBulkDropdownActions();
	},

	resetBulkDropdownActions () {
		$('#approve_for_payment_bulk_action').show();
		$('#download_closeout_attachments_bulk_action').show();
		$('#routing_action').show();
		$('#delete_void_bulk_action').show();
		$('#custom_fields_bulk_action').show();
		$('#edit_projects_bulk_action').show();
		$('#add_label_bulk_action').show();
		$('#reschedule_bulk_action').show();
		$('#reprice_bulk_action').show();
		$('#cancel_works_bulk_action').show();
		$('#create_assignment_bundle').show();
	},

	toggleBulkDropdown () {
		const isSelected = ($('#assignment_list_results').find('input:checkbox[name="work_ids[]"]:checked').length > 0);
		if (isSelected) {
			$('#assignment_actions_dropdown')
				.removeClass('disabled')
				.removeClass('tooltipped')
				.removeAttr('disabled');
			$('#approve_for_payment_bulk_action').show();
			$('#download_closeout_attachments_bulk_action').show();
			$('#delete_void_bulk_action').show();
			$('#custom_fields_bulk_action').show();
			$('#edit_projects_bulk_action').show();
			$('#add_label_bulk_action').show();
		} else {
			$('#assignment_actions_dropdown')
				.addClass('disabled')
				.addClass('tooltipped')
				.attr('disabled', true);
		}

		this.updateBulkActions();
		this.render();
	},

	updateBulkActions () {
		this.resetBulkDropdownActions();
		if (_.contains(['paymentPending', 'cancelled', 'paid', 'complete'], this.options.status)) {
			$('#cancel_works_bulk_action, #reschedule_bulk_action, #delete_void_bulk_action').hide();
		}		else if (_.contains(['sent', 'draft', 'declined'], this.options.status)) {
			$('#cancel_works_bulk_action').hide();
		}		else if (_.contains(['inprogress', 'active'], this.options.status)) {
			$('#delete_void_bulk_action').hide();
		}
		if (_.contains(['active', 'paymentPending', 'cancelled', 'paid', 'complete'], this.options.status)) {
			$('#routing_action').hide();
			$('#reprice_bulk_action').hide();
		}
	},

	doBulkAction (e) {
		e.preventDefault();

		const selOpt = e.target.id;
		const params = { ids: this.pagination_selection.getSelected() };
		const paramsModels = { models: this.pagination_selection.getSelectedModels() };
		const count = this.pagination_selection.isFullSelected() ? this.pagination_selection.totalResults : this.pagination_selection.selectedResults.length;
		const allIds = [];
		_.each(params, (item) => {
			allIds.push(item);
		});
		this.selectedWorkNumbers = allIds.join();

		if (selOpt === 'add_note_bulk_action') {
			this.modal = wmModal({
				autorun: true,
				title: count > 1 ? `Add Note to (${count}) Assignments` : 'Add Note to Assignment',
				destroyOnClose: true,
				content: BulkNoteTemplate()
			});

			new AddMultipleNoteView({
				selectedWorkNumbers: this.selectedWorkNumbers,
				modal: this.modal
			});
		}

		if (selOpt === 'add_attachment_bulk_action') {
			new AddMultipleAttachmentsView({
				selectedWorkNumbers: this.selectedWorkNumbers,
				count
			});
		}

		if (selOpt === 'remove_attachments_bulk_action') {
			new RemoveMultipleAttachmentsView({
				selectedWorkNumbers: this.selectedWorkNumbers
			});
		}

		if (selOpt === 'download_closeout_attachments_bulk_action') {
			this.modal = wmModal({
				autorun: true,
				title: 'Download Attachments',
				destroyOnClose: true,
				content: BulkDownloadTemplate()
			});

			new BulkDownloadsView({
				selectedWorkNumbers: this.selectedWorkNumbers,
				modal: this.modal
			});
		}

		if (selOpt === 'approve_for_payment_bulk_action') {
			$.ajax({
				type: 'GET',
				url: '/assignments/bulk_approve',
				context: this,
				data: { 'ids[]': this.selectedWorkNumbers }
			}).done((response) => {
				if (!_.isEmpty(response)) {
					wmModal({
						autorun: true,
						title: 'Bulk Payment',
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
		}

		if (selOpt === 'routing_action') {
			var inBundle = _.reduce(
				paramsModels.models,
				(memo, model) => {
					if (model.parent_id > 0) {
						memo.push(model);
					}
					return memo;
				},
				[]
			);

			if (inBundle.length > 0) {
				wmNotify({
					type: 'danger',
					message: 'Assignments that are part of a bundle cannot be routed in bulk.'
				});
			} else {
				this.modal = wmModal({
					title: 'Route Assignments',
					content: '<div class="routing-bucket"></div>',
					fixedScroll: true,
					destroyOnClose: true,
					autorun: true,
					controls: [
						{
							text: 'Cancel',
							close: true
						},
						{
							text: 'Route',
							classList: 'bulk-routing-apply',
							primary: true
						}
					]
				});
				new BulkRoutingView({
					selectedWorkNumbers: this.selectedWorkNumbers,
					modal: this.modal
				});
			}
		}

		if (selOpt === 'delete_void_bulk_action') {
			this.modal = wmModal({
				autorun: true,
				title: 'Delete Assignments',
				destroyOnClose: true,
				content: DeleteAssignmentsTemplate()
			});

			new DeleteVoidView({
				modal: this.modal,
				selectedWorkNumbers: this.selectedWorkNumbers
			});
		}

		if (selOpt === 'edit_projects_bulk_action') {
			this.modal = wmModal({
				autorun: true,
				title: 'Update Clients / Projects',
				destroyOnClose: true,
				content: BulkUpdateProjectTemplate()
			});

			new BulkEditProjectView({
				selectedWorkNumbers: this.selectedWorkNumbers,
				modal: this.modal
			});
		}

		if (selOpt === 'add_label_bulk_action') {
			$.ajax({
				type: 'GET',
				url: '/assignments/get_labels_mult_assignments',
				context: this,
				data: { workNumbers: this.selectedWorkNumbers }
			}).done(function (response) {
				if (!_.isEmpty(response)) {
					this.modal = wmModal({
						autorun: true,
						title: 'Bulk Add Label',
						destroyOnClose: true,
						content: BulkAddLabelTemplate()
					});
					$('#add_note_container_labels').html(response);
					wmSelect({
						selector: '#bulk_edit_label',
						root: '.wm-modal--content'
					});

					new BulkLabelView({
						selectedWorkNumbers: this.selectedWorkNumbers,
						modal: this.modal
					});
				}
			}).fail(() => {
				wmNotify({
					type: 'danger',
					message: 'There was an error fetching this modal. Please try again'
				});
			});
		}

		if (selOpt === 'custom_fields_bulk_action') {
			new BulkChangeCustomFieldsView({
				selectedWorkNumbers: this.selectedWorkNumbers
			});
		}

		if (selOpt === 'create_assignment_bundle') {
			let template;
			const nonDraft = _.reduce(
				paramsModels.models,
				(memo, model) => {
					if (model.status !== 'Draft') {
						memo.push(model);
					}
					return memo;
				},
				[]
			);
			var inBundle = _.reduce(
				paramsModels.models,
				(memo, model) => {
					if (model.parent_id > 0) {
						memo.push(model);
					}
					return memo;
				},
				[]
			);
			if (nonDraft.length > 0 || inBundle.length > 0) {
				template = CreateBundleErrorsTemplate({
					nonDraft,
					inBundle
				});
			} else {
				template = CreateBundleTemplate();
			}

			$.ajax({
				context: this,
				url: '/assignments/get_all_draft_bundles.json',
				accepts: {
					text: 'application/json'
				},
				dataType: 'json'
			}).done(function (response) {
				this.modal = wmModal({
					autorun: true,
					title: 'Bundle Actions',
					destroyOnClose: true,
					content: template
				});

				wmSelect({ selector: '#bundle_select' }, {
					create: false,
					valueField: 'id',
					searchField: 'title',
					maxItems: 1,
					openOnFocus: true,
					options: response,
					render: {
						option (item, escape) {
							return `<div>${escape(`${item.title}: ${item.workNumber}`)}</div>`;
						},
						item (item, escape) {
							return `<div>${escape(`${item.title}: ${item.workNumber}`)}</div>`;
						}
					}
				});

				new CreateBundleModalView({
					assignment_models: paramsModels.models
				});
			}).fail(() => {
				wmNotify({
					type: 'danger',
					message: 'There was an error fetching this modal. Please try again'
				});
			});
		}

		if (selOpt === 'reprice_bulk_action') {
			$.ajax({
				type: 'GET',
				url: '/assignments/edit_price_multiple',
				context: this,
				data: { workNumbers: this.selectedWorkNumbers },
				success: (response) => {
					if (!_.isEmpty(response)) {
						const modal = wmModal({
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

						$form.attr('action', `/assignments/edit_price_multiple.json?workNumbers=${this.selectedWorkNumbers}`);
						$form.ajaxForm({
							context: this,
							dataType: 'json',
							success: (postResponse) => {
								if (postResponse.successful) {
									wmNotify({ message: postResponse.messages[0] });
									modal.destroy();
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
		}

		if (selOpt === 'reschedule_bulk_action') {
			this.modal = wmModal({
				autorun: true,
				title: count > 1 ? `Reschedule (${count}) Assignments` : 'Reschedule Assignment',
				destroyOnClose: true,
				content: BulkRescheduleTemplate({
					timezone: this.options.userTimezone
				})
			});

			new BulkRescheduleView({
				selectedWorkNumbers: this.selectedWorkNumbers,
				modal: this.modal
			});
		}

		if (selOpt === 'remove_label_bulk_action') {
			$.ajax({
				type: 'GET',
				url: '/assignments/label_remove_multiple',
				context: this,
				data: { workNumbers: this.selectedWorkNumbers }
			}).done(function (response) {
				if (!_.isEmpty(response)) {
					this.modal = wmModal({
						autorun: true,
						title: 'Remove Label',
						destroyOnClose: true,
						content: BulkRemoveLabelTemplate()
					});
					$('#label-remove-form-container').html(response);
					wmSelect({
						selector: '#bulk_edit_label',
						root: '.wm-modal--content'
					});

					new BulkLabelRemoveView({
						selectedWorkNumbers: this.selectedWorkNumbers,
						modal: this.modal
					});
				}
			}).fail(() => {
				wmNotify({
					type: 'danger',
					message: 'There was an error fetching this modal. Please try again'
				});
			});
		}

		if (selOpt === 'cancel_works_bulk_action') {
			$.ajax({
				type: 'GET',
				url: '/assignments/cancel_works_multiple',
				data: {
					workNumbers: this.selectedWorkNumbers
				},
				context: this
			}).done(function (response) {
				if (!_.isEmpty(response)) {
					this.modal = wmModal({
						autorun: true,
						title: 'Cancel Assignments',
						destroyOnClose: true,
						content: response
					});

					new BulkCancelWorksView({
						selectedWorkNumbers: this.selectedWorkNumbers,
						modal: this.modal
					});
				}
			}).fail(() => {
				wmNotify({
					type: 'danger',
					message: 'There was an error fetching this modal. Please try again'
				});
			});
		}
	},

	applyFilters () {
		// Trigger change event.
		this.trigger('filter:apply_filters');
	},

	includeTime () {
		// Should display the time filters when it is clicked
		if ($('#include_time').prop('checked')) {
			$('.ui-datepicker-trigger').hide();
			$('#schedule_from').css('width', '65px');
			$('#schedule_through').css('width', '65px');
			$('#time_from').show();
			$('#time_through').show();
		} else {
			$('#time_from').hide();
			$('#time_through').hide();
			$('.ui-datepicker-trigger').show();
		}
	},

	clearAllFilters () {
		$('#date_filter option').attr('selected', false);
		$('#date_filter option:first-child').attr('selected', true);

		$('#date_sub_filter option').attr('selected', false);
		$('#date_sub_filter option:first-child').attr('selected', true);

		$('#schedule_from').val('');
		$('#schedule_through').val('');

		if ($('#keyword').length) {
			$('#keyword').val('');
		}

		if ($('#include_time').length) {
			$('#time_from').val('');
			$('#time_through').val('');
			$('#include_time').removeAttr('checked');
			$('#time_from').hide();
			$('#time_through').hide();
		}

		if (this.current_view == 'list') {
			$('#custom-range-dates').show();
		}

		this.clearAdvancedFilters();
	},

	clearAdvancedFilters () {
		if (this.options.isBuyer) {
			this.clientCompanySelect.clear(true);
			this.projectDropdownSelect.clear(true);
		}
		this.internalOwnerDropdownSelect.clear(true);
		this.resourcesDropdownSelect.clear(true);
		this.vendorsDropdownSelect.clear(true);
		this.bundlesDropdownSelect.clear(true);
		$('#assigned_to_me').removeAttr('checked');
		$('#dispatched_by_me').removeAttr('checked');
		$('#following').removeAttr('checked');
		this.trigger('filter:clear_filters');
	},

	toggleDatePickers (e) {
		if (e) e.preventDefault();
		if ($('#date_sub_filter').val() === '7') {
			$('#custom-range-dates').show();
		} else {
			$('#custom-range-dates').hide();
		}
		this.trigger('filter:date_filter');
	},

	filterDateRange () {
		// Trigger change event
		this.trigger('filter:date_filter');
		if ($('#include_time').val() === 'true') {
			$('#incldue_time').attr('checked', 'checked');
		}
	},

	filterClientCompanyList () {
		// Trigger change event.
		this.trigger('filter:client_company_list');
	},

	filterProject () {
		// Trigger change event.
		this.trigger('filter:project-dropdown');
	},

	filterOwner () {
		// Trigger change event.
		this.trigger('filter:internal-owner-dropdown');
	},

	filterBundle () {
		this.trigger('filter:bundles-dropdown');
	},

	filterResource () {
		// Trigger change event.
		this.trigger('filter:resources-dropdown');
	},

	filterVendor () {
		// Trigger change event.
		this.trigger('filter:vendors-dropdown');
	},

	filterDispatchedByMe () {
		this.trigger('filter:dispatched_by_me');
	},

	filterAssignedToMe () {
		// Trigger change event.
		this.trigger('filter:assigned_to_me');
	},

	filterFollowing () {
		// Trigger change event.
		this.trigger('filter:following');
	},

	toggleAdvanced (e) {
		e.preventDefault();

		$('#advanced-filters').toggle();
		if ($('#advanced-filters').is(':visible')) {
			$(e.target).text('Hide filters');
		}		else {
			this.clearAdvancedFilters();
			$(e.target).text('Show more filters');
		}
	},

	showBulkActions () {
		$('#approve_for_payment_bulk_action').show();
		$('#approve_for_payment_bulk_action_wrapper').hide();
		$('#download_closeout_attachments_bulk_action').show();
		$('#delete_void_bulk_action').show();
		$('#custom_fields_bulk_action').show();
		$('#edit_projects_bulk_action').show();
		$('#add_label_bulk_action').show();
		$('#create_assignment_bundle').show();
	},

	showPaymentActions () {
		$('#approve_for_payment_bulk_action_wrapper').show();
	},

	sortAscending () {
		this.toggleSelected($('#assignment_list_sorting_asc'), $('#assignment_list_sorting_desc'));
		this.trigger('sorting:assignmentSortAsc');
	},

	sortDescending () {
		this.toggleSelected($('#assignment_list_sorting_desc'), $('#assignment_list_sorting_asc'));
		this.trigger('sorting:assignmentSortDsc');
	},

	sort () {
		if (this.options.dir === 'asc') {
			this.sortAscending();
		} else {
			this.sortDescending();
		}
	},

	toggleSelected (onBtn, offBtn) {
		onBtn.addClass('toggle_selected');
		$('i', onBtn).addClass('icon-white');
		offBtn.removeClass('toggle_selected');
		$('i', offBtn).removeClass('icon-white');
	},

	refreshList () {
		this.trigger('refresh:refreshList');
	},

	changeListSize () {
		this.trigger('refresh:resizeList');
	},

	showCalendar () {
		this.trigger('action:showCalendar');
		$('#show_calendar').addClass('active');
		$('#show_list').removeClass('active');
	},
	showList () {
		this.trigger('action:showList');
		$('#show_list').addClass('active');
		$('#show_calendar').removeClass('active');
	},

	showMonthView () {
		this.trigger('action:showMonthView');
	},
	showWeekView () {
		this.trigger('action:showWeekView');
	},
	showDayView () {
		this.trigger('action:showDayView');
	},
	saveCurrentDate () {
		this.trigger('action:saveCurrentDate');
	}
});

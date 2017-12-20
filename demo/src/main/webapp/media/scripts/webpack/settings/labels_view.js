import $ from 'jquery';
import 'jquery-ui';
import _ from 'underscore';
import Backbone from 'backbone';
import LabelFormModel from './label-form-model';
import EmployeeCollection from './employees_collection';
import wmSelect from '../funcs/wmSelect';
import wmNotify from '../funcs/wmNotify';
import wmModal from '../funcs/wmModal';
import wmEmployeeList from '../funcs/wmEmployeeList';
import getCSRFToken from '../funcs/getCSRFToken';
import ConfirmActionTemplate from '../funcs/templates/confirmAction.hbs';
import SettingsLabelModal from '../settings/templates/labels_modal.hbs';
import '../dependencies/syronex-colorpicker';

export default Backbone.View.extend({
	el: 'body',

	events: {
		'click a.colorpicker': 'openColorpicker',
		'click a.cta-manage-label': 'openManageLabel',
		'click button.cta-delete-label': 'openDeleteConfirm',
		'click a.cta-change-display-label': 'changeLabelDisplayType',
		'click a.cta-success': 'changeLabelSuccess',
		'click a.cta-error': 'changeLabelError',
		'click #save-label': 'submitLabel'
	},

	initialize (options) {
		this.options = options || {};
		this.templates = '#workTemplateIds';
		this.users = new EmployeeCollection();
	},

	render () {},

	openColorpicker (e) {
		e.stopPropagation();

		const el = $(e.currentTarget);
		const offset = el.offset();

		const colors = [
			'#AC725D', '#D06A63', '#F83A21', '#FA563C', '#FF7436', '#FFAD45',
			'#41D692', '#15A765', '#7BD147', '#B3DC6C', '#FBE983', '#FAD165',
			'#92E1C0', '#9FE1E7', '#9FC6E7', '#4986E7', '#9A9BFB', '#B999FB',
			'#C2C2C2', '#CABDBF', '#CCA6AC', '#F691B2', '#CD73E6', '#A47AE2'
		];

		$('#color_picker_popup').remove();
		const popup = $('<div>').attr('id', 'color_picker_popup').css({
			top: offset.top + 16,
			left: offset.left
		})
			.click((ev) => {
				ev.stopPropagation();
			})
			.colorPicker({
				defaultColor: $.inArray(el.data('color'), colors),
				color: colors,
				columns: 6,
				click (color) {
					$.ajax({
						url: '/settings/manage/label_color',
							// data: JSON.stringify({id: el.data('id'), color: color.substr(1)}),
						data: { id: el.data('id'), color: color.substr(1) },
						dataType: 'json',
							// contentType: 'application/json',
						type: 'POST',
						beforeSend (jqXHR) {
							jqXHR.setRequestHeader('x-csrf-token', getCSRFToken());
						}
					}).done(() => {
						el.data('color', color);
						el.css({ backgroundColor: color });
						$('#color_picker_popup').remove();
					});
						// trackEvent('label', 'color', color);
				}
			});

		popup.appendTo('body');

		$('body').click(() => {
			$('#color_picker_popup').remove();
		});
	},

	openManageLabel (e) {
		e.preventDefault();
		const action = $(e.currentTarget).attr('rel');
		this.labelModel = new LabelFormModel({
			url: action === 'edit' ? $(e.currentTarget).attr('href') : '/settings/manage/labels_manage'
		});
		this.labelModel.fetch({
			success: (labelData) => {
				this.labelModal = wmModal({
					title: `${(action === 'edit') ? 'Edit' : 'Create New'} Label`,
					root: this.el,
					destroyOnClose: true,
					content: SettingsLabelModal(labelData.get('data'))
				});
				// select default dropdown values
				this.labelModal.show();
				wmSelect();
				this.templateSelect = wmSelect({ selector: this.templates }, {
					plugins: ['remove_button'],
					maxItems: null,
					placeholder: 'Enter template name',
					labelField: 'name',
					valueField: 'id',
					searchField: ['id', 'name'],
					onChange: _.bind(this.scopeWarning, this)
				});
				_.each(labelData.get('data').form.workTemplateIds, (template) => {
					$(this.templateSelect)[0].selectize.addItem(template);
				});
				this.renderModal();

				const staticList = [
					{
						id: 'io',
						fullName: 'Internal Owner',
						group: 'static',
						icon: 'wm-icon-lock-circle',
						hideId: true
					},
					{
						id: 'r',
						fullName: 'Assigned Worker',
						group: 'static',
						icon: 'wm-icon-checkmark-circle',
						hideId: true
					}
				];
				const staticListName = '';

				function populatePersistedEmployeesCallback () {
					const notify = labelData.get('data').form.notify;
					const $labelNotifications = this.$('#label-notifications')[0].selectize;

					if (notify === 'io_r') {
						$labelNotifications.addItems(['io', 'r']);
					} else {
						$labelNotifications.addItem(notify);
					}

					_.each(labelData.get('data').form.workSubStatusTypeRecipientIds, (item) => {
						$labelNotifications.addItem(parseInt(item, 10));
					});
				}

				wmEmployeeList({
					selector: '#label-notifications',
					companyId: labelData.get('data').companyId,
					staticList,
					staticListName,
					populatePersistedEmployees: _.bind(populatePersistedEmployeesCallback, this)
				});
			}
		});
	},

	renderModal () {
		const $scopeWarning = $('#scopeWarning');

		$('div#action_checkboxes input[type="checkbox"]').on('click', (e) => {
			this.toggleAction($(e.currentTarget));
		});

		this.toggleAction($('input[name="includeInstructions"]'));
		this.toggleAction($('input[name="scheduleRequired"]'));
		// initialize the other simple select dropdowns

		// Create and initialize label-scope range slider
		const workStatusTypeScopeRangeFrom = parseInt($('#scope_range_from').val(), 10);
		const workStatusTypeScopeRangeTo = parseInt($('#scope_range_to').val(), 10);

		const sliderLabel = $('#labels_container').find('.slider_label');
		sliderLabel.slice(workStatusTypeScopeRangeFrom, workStatusTypeScopeRangeTo + 1).addClass('active');
		$('#labelScopeRange').slider({
			range: true,
			min: 0,
			max: 4,
			values: [workStatusTypeScopeRangeFrom, workStatusTypeScopeRangeTo],
			step: 1,
			slide (event, ui) {
				$('#scope_range_from').val([ui.values[0]]);
				$('#scope_range_to').val([ui.values[1]]);
				sliderLabel.removeClass('active');
				sliderLabel.slice(ui.values[0], ui.values[1] + 1).addClass('active');

				// Show warning if changing scope when editing (not adding)
				if ($scopeWarning.length > 0 && $scopeWarning.hasClass('dn')) {
					$scopeWarning.removeClass('dn');
				}
			}
		});
	},

	scopeWarning () {
		const $scopeWarning = $('#scopeWarning');

		// Show warning if changing scope when editing (not adding)
		if (this.$(this.templates).length > 0) {
			$(this.templates).on('change', () => {
				if ($scopeWarning.length > 0 && $scopeWarning.hasClass('dn')) {
					$scopeWarning.removeClass('dn');
				}
			});
		}
	},

	submitLabel (e) {
		e.preventDefault();

		const recipients = [];
		const directRecipients = [];
		_.each($('#label-notifications').val().split(','), (item) => {
			if (isNaN(item)) {
				directRecipients.push(item);
			} else {
				recipients.push(item);
			}
		});

		this.$('#label-notifications').val(recipients);

		if (_.indexOf(directRecipients, 'io') >= 0 && _.indexOf(directRecipients, 'r') >= 0) {
			this.$('#notify').val('io_r');
		} else if (_.indexOf(directRecipients, 'io') >= 0 || _.indexOf(directRecipients, 'r') >= 0) {
			this.$('#notify').val(_.first(directRecipients));
		}

		let formData = $('#form_labels_manage').serialize();
		const $templates = $('#workTemplateIds').val();
		// this is some bologna because jquery serialize does not include false fields
		// we need if they were checked and then unchecked :)
		_.each(['alert', 'noteRequired', 'includeInstructions', 'scheduleRequired', 'removeAfterReschedule', 'removeOnVoidOrCancelled', 'active'], (field) => {
			formData += `&${field}=${$(`[name="${field}"]`).prop('checked')}`;
		});

		if (!$templates) {
			formData += '&workTemplateIds=';
		}

		$.ajax({
			context: this,
			type: 'POST',
			url: '/settings/manage/labels_manage',
			data: formData,
			dataType: 'json',
			beforeSend (jqXHR) {
				jqXHR.setRequestHeader('x-csrf-token', getCSRFToken());
			},
			success (response) {
				function redirectWithFlash (url, type, msg) {
					const ev = $('<form></form>');
					ev.attr({
						action: '/message/create',
						method: 'POST'
					});
					ev.append(
						$('<input>').attr({
							name: 'message[]',
							value: msg
						}));
					ev.append(
						$('<input>').attr({
							name: 'type',
							value: type
						}));
					ev.append(
						$('<input>').attr({
							name: 'url',
							value: url
						}));
					ev.append(
						$('<input>').attr({
							name: '_tk',
							value: getCSRFToken()
						}));
					$('body').append(ev);
					ev.submit();
				}

				if (response.successful) {
					this.labelModal.hide();
					const message = `You have successfully ${(this.options.action === 'edit') ? 'edited' : 'added'} a label.`;
					if (this.options.reloadOnComplete) {
						redirectWithFlash(location.pathname, 'success', message);
					} else {
						if (response.data.id > 0) {
							Backbone.Events.trigger('renderLabels', response.data.id);
						}
						wmNotify({ message });
					}

					// trackEvent('label', this.options.action);
				} else {
					wmNotify({
						type: 'danger',
						message: _.first(response.messages)
					});
				}
			}
		});
	},

	toggleAction (el) {
		if ($(el).is(':checked')) {
			$(el).parent().next('.additional_fields').show();
		} else {
			$(el).parent().next('.additional_fields').hide();
		}
	},

	openDeleteConfirm (e) {
		e.preventDefault();

		if (this.labelModal) {
			this.labelModal.hide();
		}

		this.confirmModal = wmModal({
			autorun: true,
			title: 'Confirm',
			destroyOnClose: true,
			content: ConfirmActionTemplate({
				message: 'Warning: deleting will permanently remove all historic data associated with this custom label. This cannot be undone. Are you absolutely sure?'
			})
		});

		$('.cta-confirm-yes').on('click', () => {
			window.location = $(e.currentTarget).attr('href');
			this.confirmModal.hide();
		});
	},

	changeLabelDisplayType (e) {
		e.preventDefault();
		$.ajax({
			type: 'POST',
			dataType: 'json',
			url: $(e.currentTarget).attr('href'),
			beforeSend (jqXHR) {
				jqXHR.setRequestHeader('x-csrf-token', getCSRFToken());
			},
			success (data) {
				if (data.successful) {
					const row = $(e.currentTarget).closest('tr');
					$(row).find('.cta-success').removeClass('text-success');
					$(row).find('.cta-error').removeClass('text-error');
					$(e.currentTarget).addClass('labels_selected_display_type');
				}
			}
		});
	},

	changeLabelSuccess (e) {
		e.preventDefault();
		$.ajax({
			type: 'POST',
			dataType: 'json',
			url: $(e.currentTarget).attr('href'),
			beforeSend (jqXHR) {
				jqXHR.setRequestHeader('x-csrf-token', getCSRFToken());
			},
			success (data) {
				if (data.successful) {
					const row = $(e.currentTarget).closest('tr');
					$(row).find('.cta-error').removeClass('text-error');
					$(row).find('.labels_selected_display_type').removeClass('labels_selected_display_type');
					$(e.currentTarget).addClass('text-success');
				}
			}
		});
	},

	changeLabelError (e) {
		e.preventDefault();
		$.ajax({
			type: 'POST',
			dataType: 'json',
			url: $(e.currentTarget).attr('href'),
			beforeSend (jqXHR) {
				jqXHR.setRequestHeader('x-csrf-token', getCSRFToken());
			},
			success (data) {
				if (data.successful) {
					const row = $(e.currentTarget).closest('tr');
					$(row).find('.cta-success').removeClass('text-success');
					$(row).find('.labels_selected_display_type').removeClass('labels_selected_display_type');
					$(e.currentTarget).addClass('text-error');
				}
			}
		});
	}
});

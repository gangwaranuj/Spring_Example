import $ from 'jquery';
import _ from 'underscore';
import '../funcs/autoresizeTextarea';

export default (jsonFieldGroup) => {
	let fieldCount = -1;
	const data = jsonFieldGroup || {
		work_custom_field_group_id: '',
		name: '',
		work_custom_fields: []
	};

	const toggleCustomFieldOptions = function toggleCustomFieldOptions (el) {
		const options = $(el).parent().parent().find('ul');
		options.toggle();
		$(options).find('input').each(function eachFunc () {
			$(this).prop('disabled', !$(this).prop('disabled'));
		});
	};

	const addField = (type, fieldData) => {
		const fields = $(`#${type}-custom-field-template`).tmpl({
			index: fieldCount += 1,
			data: fieldData
		}).appendTo(`#${type}-custom-fields`);

		fields.find('.field_value').autoresizeTextarea();

		return fields;
	};

	const renumberChoices = () => {
		// Update the owner custom field locations
		let listItems = $('#owner-custom-fields li.custom_field_set');
		listItems.each((idx, li) => {
			const item = $(li);

			$('input.field_id', item).attr('name', `workCustomFields[${idx}].id`);
			$('input.field_type', item).attr('name', `workCustomFields[${idx}].workCustomFieldTypeCode`);
			$('input.field_name', item).attr('name', `workCustomFields[${idx}].name`);
			$('textarea.field_value', item).attr('name', `workCustomFields[${idx}].value`);
			$('input.field_required', item).attr('name', `workCustomFields[${idx}].requiredFlag`);

			$('input.field_show_on_sent_status', item).attr('name', `workCustomFields[${idx}].showOnSentStatus`);
			$('input.field_visible', item).attr('name', `workCustomFields[${idx}].visibleToResourceFlag`);
			$('input.field_show_on_dashboard', item).attr('name', `workCustomFields[${idx}].showOnDashboard`);
			$('input.field_show_in_assignment_email', item).attr('name', `workCustomFields[${idx}].showInAssignmentEmail`);
			$('input.field_show_on_invoice', item).attr('name', `workCustomFields[${idx}].showOnInvoice`);
			$('input.field_show_in_assignment_header', item).attr('name', `workCustomFields[${idx}].showInAssignmentHeader`);
			$('input.field_show_on_printout', item).attr('name', `workCustomFields[${idx}].showOnPrintout`);
		});

		// Update the resource custom field locations
		const offset = listItems.length;
		listItems = $('#resource-custom-fields li.custom_field_set');
		listItems.each((idx, li) => {
			const item = $(li);
			const newIndex = idx + offset;

			$('input.field_id', item).attr('name', `workCustomFields[${newIndex}].id`);
			$('input.field_type', item).attr('name', `workCustomFields[${newIndex}].workCustomFieldTypeCode`);
			$('input.field_visible', item).attr('name', `workCustomFields[${newIndex}].visibleToResourceFlag`);
			$('input.field_name', item).attr('name', `workCustomFields[${newIndex}].name`);
			$('textarea.field_value', item).attr('name', `workCustomFields[${newIndex}].value`);
			$('input.field_required', item).attr('name', `workCustomFields[${newIndex}].requiredFlag`);
			$('input.field_show_on_invoice', item).attr('name', `workCustomFields[${newIndex}].showOnInvoice`);
		});
	};

	$('#field-group-name').html(data.name);
	$('#field_group_name').val(_.unescape(data.name));
	if (data.work_custom_fields != null) {
		$.each(data.work_custom_fields, (i, item) => {
			const field = addField((item.work_custom_field_type_code === 'owner') ? 'owner' : 'resource', item);
			field.find('input.field_required').prop('checked', item.required_flag);
			field.find('input.field_show_on_invoice').prop('checked', item.show_on_invoice);
			field.find('input.field_show_on_dashboard').prop('checked', item.show_on_dashboard);
			field.find('input.field_show_on_printout').prop('checked', item.show_on_printout);
			field.find('input.field_show_on_sent_status').prop('checked', item.show_on_sent_status);
			if (item.show_on_sent_status === true) {
				field.find('input.field_visible').prop('checked', item.show_on_sent_status);
			} else {
				field.find('input.field_visible').prop('checked', item.visible_to_resource_flag);
			}
			field.find('input.field_show_in_assignment_header').prop('checked', item.show_in_assignment_header);
			field.find('input.field_show_in_assignment_email').prop('checked', item.show_in_assignment_email);
		});
	}

	$('#work-show-build-step3').on('click', () => {
		$('#work-display-build1').hide();
		$('#field-group-name').html($('#field_group_name').val());
	});

	$('#save-custom-field-group').on('click', function onClick () {
		$(this).closest('form').trigger('submit');
	});

	$('#add-owner-custom-field').on('click', () => {
		addField('owner', {}).find('input[type=text]').first().focus();
		return false;
	});
	$('#add-resource-custom-field').on('click', () => {
		addField('resource', {}).find('input[type=text]').first().focus();
		return false;
	});


	$('#field_group_required').click(function onClick () {
		if ($(this).prop('checked')) {
			// eslint-disable-next-line no-alert
			if (!confirm('Are you sure you want to require this field set? All existing templates will update to use \nthis fieldset and overwrite previous template settings and cannot be reverted. \nThis will not affect existing assignments.')) {
				$(this).removeProp('checked');
			}
		}
	});

	$('input[id^=assigned-resources]').each(function eachFunc () {
		$(this).click((e) => {
			toggleCustomFieldOptions(e.target);
		});
		if (!$(this).is(':checked')) {
			toggleCustomFieldOptions(this);
		}
	});

	// Rig up the sorting handles
	$('#owner-custom-fields').sortable({
		handle: '.owner_sort_handle',
		stop: () => {
			renumberChoices('owner');
		}
	});

	$('#resource-custom-fields').sortable({
		handle: '.resource_sort_handle',
		stop: () => {
			renumberChoices('resource');
		}
	});

	// Rig up the delete buttons
	$('#workCustomFieldGroupDTO').on('click', '.remove_choice', function onClick () {
		// eslint-disable-next-line no-alert
		if (confirm('Deleting fields is a permanent action and will remove the field from existing assignments and reports. Only proceed if you are sure.')) {
			$(this).closest('li.custom_field_set').remove();
			fieldCount -= 1;
			renumberChoices();
		}
	});

	$('input.field_show_on_sent_status').on('click', (e) => {
		const el = $(e.currentTarget);
		const assignedEl = el.parents('.inputs-list').find('.field_visible');
		const checked = el.is(':checked');

		if (checked) {
			assignedEl.attr('checked', true);
			assignedEl.prop('disabled', true);
		} else {
			assignedEl.attr('checked', false);
			assignedEl.prop('disabled', false);
		}
	});
};

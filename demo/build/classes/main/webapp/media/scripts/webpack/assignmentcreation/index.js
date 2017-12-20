import $ from 'jquery';
import _ from 'underscore';
import Application from '../core';
import PricingPage from '../assignments/pricing_page';
import AssignmentDeliverables from './deliverables';
import AssignmentDocuments from './documents';
import RequirementSets from './requirementsets';
import AssignmentParts from './parts';
import AssignmentForm from './form';
import Segment from './segment';
import wysiwygHtmlClean from '../funcs/wysiwygHtmlClean';
import '../config/datepicker';
import '../config/wysiwyg';
import '../dependencies/jquery.bootstrap-dropdown';
import '../dependencies/jquery.bootstrap-tab';

Application.init({ name: 'assignmentcreation', features: config }, () => {});

var defaultFilters = [],
	assignments,
	hasRequired;

if (config.main.isMmwGlobal) {
	defaultFilters.mmwGlobal = config.main.mmwGlobal;
}

if (config.main.isMmwTemplates) {
	defaultFilters.mmwTemplates = config.main.mmwTemplatesJson;
}

if (config.main.isOnsiteContact) {
	defaultFilters.onsiteContact = config.main.onSiteContact;
}

if (config.main.isOnsiteSecondaryContact) {
	defaultFilters.onsiteContactSecondary = config.main.onSiteSecondaryContact;
}

if (config.main.isTemplateId) {
	defaultFilters.defaultSelectedTemplate = config.main.templateId;
}

if (config.main.isClientCompany) {
	defaultFilters.defaultClientId = config.main.clientCompany;
	defaultFilters.defaultProjectId = config.main.project;
}

defaultFilters.workerDirectSelections = config.main.workerDirectSelections;

assignments = new AssignmentForm();
assignments.initialize(defaultFilters);
// If routing to pre selected resource, set variable to true
assignments.assignmentFor = config.main.isAssignmentFor;
assignments.isTemplate = config.main.isTemplate;

// enable or disable Send button ONLY if it's not a template
if (!config.main.isTemplate) {
	assignments.updateSendControls();
}

function getSortedKeys(order) {
	order.sort(function(x, y) {
		return x.pos - y.pos;
	});
	return order;
}

function loadnormal() {

	if (config.main.isCustomFieldsJson) {
		$.unescapeHTML = function(html) {
			return $('<div/>').html(html).text();
		};

		var customFields = $.parseJSON($.unescapeHTML($('#json_custom_fields').html()));
		var order = [];

		for (var key in customFields) {
			var pos = customFields[key][0].group_pos;
			var pair = { key: key, pos: pos };
			order[order.length] = pair;
		}

		var sortedOrder = getSortedKeys(order);

		_.each(sortedOrder, function (k) {
			var key = k.key;
			if (!hasRequired || config.main.requiredCustomField !== key) {
				// Handle the schematics on the page load
				var $dropdown = $('#custom-fields-dropdown option[value="' + key + '"]');
				var $pos = customFields[key][0].group_pos;
				$('<input />')
					.attr('class', 'customFieldInput')
					.attr('type', 'hidden')
					.attr('name', 'customfield[' + $pos + ']')
					.attr('id', $dropdown.text())
					.val(key)
					.appendTo('#attached_sets_input');

				$dropdown.remove();
				// Fill in the actual fields
				assignments.populateCustomFields(customFields[key], key, false);
			}

		});
	}
}

if (config.main.isRequiredCustomField) {
	$.unescapeHTML = function(html) {
		return $('<div/>').html(html).text();
	};

	$.unescapeAndParseJSON = function(json) {
		return $.parseJSON($.unescapeHTML(json));
	};

	// Load required fields first
	hasRequired = true;
	var dropdown = $('#custom-fields-dropdown option[value="' + config.main.requiredCustomField + '"]');
	// Differentiate between an empty default set or an already added default set
	if (config.main.isCustomFieldsJsonRequiredCustomField) {
		$('<input />')
			.attr('class', 'customFieldInput')
			.attr('type', 'hidden')
			.attr('name', 'customfield')
			.attr('id', dropdown.text())
			.val(config.main.requiredCustomField)
			.appendTo('#attached_sets_input');

		assignments.populateCustomFields($.unescapeAndParseJSON($('#json_required_custom_field').html()), config.main.requiredCustomField, true);
		dropdown.remove();
		loadnormal();
	} else {
		// Synchronous to keep proper order...
		assignments.addCustomFieldSet(dropdown.val(), true, function () {
			dropdown.remove();
			loadnormal();
		});
	}
} else {
	hasRequired = false;
	loadnormal();
}

if (config.main.isOneTimeLocation) {
	assignments.toggleLocationTypeViewAndFetchContacts(config.main.clientLocations || assignments.CONTACT_MANAGER);
}

if (config.main.isClientLocationId) {
	assignments.toggleLocationTypeViewAndFetchContacts(config.main.clientLocations || assignments.CONTACT_MANAGER, config.main.clientLocationId);
}

if (!config.main.isOneTimeLocation && !config.main.isClientLocationId) {
	assignments.toggleLocationTypeViewAndFetchContacts(config.main.clientLocations);
}

if (config.main.isOneTimeLocation) {
	//need to doublecheck locationJson is formatted properly
	assignments.setOneTimeLocation(config.main.locationJson);
}

_.each(config.main.attachmentsJson, function (item) {
	$('#attachment-list-item-tmpl').tmpl({
		index: $('#attachments_list tbody').children().size(),
		id: item.id,
		uuid: item.uuid,
		description: item.description,
		file_name: item.name,
		is_upload: item.isUpload
	}).appendTo($('#attachments_list'));
});

_.each(config.main.surveysJson, function (item) {
	assignments.addSurvey(item.id, item.is_required);
});

var copyAssignmentPageContext = {};

copyAssignmentPageContext.updateSendControls = function () {
	var $routingGroupIds = $('#routing_groups_ids'),
		$routingResourceIds = $('#routing_resource_ids'),
		$submitForm = $('#submit-form'),
		$searchResources = $('#search-resources');

	if (!$routingGroupIds.val() && !$routingResourceIds.val() && !$('#show_in_feed').is(':checked') && !$('#smart_route').is(':checked')) {
		$submitForm.attr('disabled', 'disabled').addClass('dn');
	} else {
		$submitForm.removeAttr('disabled').removeClass('dn');
	}
	if ($routingGroupIds.val() || $routingResourceIds.val()) {
		$searchResources.addClass('dn');
	} else {
		$searchResources.removeClass('dn');
	}
};

copyAssignmentPageContext.hideOrShowForCopyAssignment = function (selectedValue) {
	if (selectedValue > 1) {
		// Hide resources type-ahead box
		$('.routing-configation').hide();
		// Remove selected resources from hidden input
		$('#routing_resource_ids').val('');
		// Hide [Search People] Button
		$('#search-resources').hide();
		copyAssignmentPageContext.updateSendControls();
	} else {
		$('.routing-configation').show();
		$('#search-resources').show();
	}
};

var $numberOfCopies = $('#numberOfCopies');
if ($numberOfCopies.length) {
	var numberOfCopiesSelector = $numberOfCopies;
	var selectedValue = numberOfCopiesSelector.find('option:selected').attr('value');
	copyAssignmentPageContext.hideOrShowForCopyAssignment(selectedValue);

	numberOfCopiesSelector.change(function () {
		var selectedValue = numberOfCopiesSelector.find('option:selected').attr('value');
		copyAssignmentPageContext.hideOrShowForCopyAssignment(selectedValue);
	});
}

wysiwygHtmlClean();

RequirementSets.init(config.main.requirementSetIds);
AssignmentDocuments.init(config.main.attachmentsJson, config.main.visibilitySettingsMap, config.main.defaultVisibilitySetting, config.main.isAdminOrActiveResource);

if (config.main.isPartsLogisticsEnabledFlag) {
	AssignmentParts.init({
		workNumber: config.main.workNumber,
		parts: config.main.partsJson,
		partsConstants: config.main.partsConstantsJson
	});
}

Segment();

if (config.main.isCustomCloseOutEnabledFlag) {
	var deliverablesJson = config.main.resourceCompletionJson ? JSON.parse(config.main.resourceCompletionJson) : {};
	AssignmentDeliverables.init(deliverablesJson);
}

// assignment pricing init
PricingPage({
	isPricingMode: config.main.isPricingMode,
	pricingMode: config.main.pricingMode,
	wmFee: parseFloat(config.main.wmFee),
	pricingType: config.main.pricingType,
	pricing: config.main.pricing
});

'use strict';

import DeliverableOptionsTemplate from '../assignments/templates/creation/deliverableOptionsTemplate.hbs';
import DeliverableRequirementTemplate from '../assignments/templates/creation/deliverable_requirement.hbs';
import $ from 'jquery';
import _ from 'underscore';
import '../funcs/autoresizeTextarea';

export default {
	init: function (deliverableRequirements) {
		var self = this;
		var data = deliverableRequirements || {};

		this.dropdownOptions = [
			{
				code: 'sign_off',
				description: 'Sign-Off Form',
				priority: 0
			},
			{
				code: 'photos',
				description: 'Photos',
				priority: 1
			},
			{
				code: 'other',
				description: 'Other',
				priority: 2
			}
		];

		// Add default dropdown option
		this.index = -1;
		this.dropdownOptions[this.dropdownOptions.length] = {
			code: 'default',
			description: '- Deliverable Type -',
			priority: this.index
		};
		this.index++;

		// Component state
		this.dropdownOptions = _.sortBy(self.dropdownOptions, self.deliverableComparator);
		this.numberOfTypes = this.dropdownOptions.length;

		// Templates
		this.deliverableRequirementTemplate = DeliverableRequirementTemplate;
		//check this template to ensure it works
		this.deliverableRequirementOptionsTemplate = DeliverableOptionsTemplate;

		// Document state
		this.$id = $('#deliverableRequirementId');
		this.$instructions = $('#instructions');
		this.$hoursToComplete = $('#hoursToComplete');
		this.$availableTypeOptions = $('#deliverableRequirementTypes');
		this.$numberOfFiles = $('#numberOfFiles');
		this.$addDeliverableRequirement = $('#addDeliverableRequirement');
		this.$deliverableRequirementsList = $('#deliverableRequirementsList');
		this.$deliverablesDeadlineSection = $('.deliverables-deadline-section');

		// Hook up event handlers
		$(document).on('keypress', '#assignment-internal .only-numbers', self.onlyNumericHandler);
		$(document).on('blur', '#assignment-internal .only-numbers', self.placeholderBlurHandler);
		$(document).on('focus', '#assignment-internal .only-numbers', self.placeholderFocusHandler);
		$(document).on('click', '#assignment-internal .remove-deliverable', {parentContext: self}, self.removeDeliverableRequirementHandler);
		$(document).on('mousedown', '#deliverableRequirementsList li', self.dragStartHandler);
		$(document).on('mouseup', '#deliverableRequirementsList li', self.dragEndHandler);
		$(document).on('deliverablesPaneVisible', _.bind(function () {
			this.$deliverableRequirementsList.find('.quick-description').autoresizeTextarea();
		}, this));

		this.$deliverableRequirementsList.sortable({cancel: 'a,button,:input', stop: function () {self.renumberDeliverables();}});
		this.$availableTypeOptions.change($.proxy(self.enableDisableAddButtonHandler, self));
		this.$addDeliverableRequirement.click($.proxy(self.addDeliverableRequirementHandler, self));

		// Set up form
		this.addDeliverableSharedRequirements(data);
		this.loadDeliverableRequirementsAndSetTypeOptions(data);
	},

	deliverableComparator: function (obj) {
		return obj.priority;
	},

	addDeliverableSharedRequirements: function (data) {
		// Set deliverable form id
		var deliverableId = data.id;
		this.$id.val(deliverableId);

		// Set numberOfHours
		var numberOfHours = data.hoursToComplete;
		if ($.isNumeric(numberOfHours)) {
			this.$hoursToComplete.val(numberOfHours);
		}
	},

	loadDeliverableRequirementsAndSetTypeOptions: function (data) {

		var self = this;
		// Set remaining deliverable type options (dropdown)
		var deliverableRequirements = data.deliverable_requirements;
		var allTypes = this.dropdownOptions;
		var generatedAvailableTypes = this.deliverableRequirementOptionsTemplate({typeOptions: allTypes});

		$(generatedAvailableTypes).appendTo(self.$availableTypeOptions);

		// Load deliverable requirements
		var generatedDeliverableRequirements = '';
		_.each(deliverableRequirements, function (deliverableRequirement) {
			var code = deliverableRequirement.type;
			var deliverableType = _.find(self.dropdownOptions, function (option) { return (option.code === code); });
			deliverableRequirement.priority = self.index;

			generatedDeliverableRequirements += self.deliverableRequirementTemplate({
				deliverableType: deliverableType,
				deliverableRequirement: deliverableRequirement,
				deliverableTypeCodeReplaced: deliverableType.code.replace(/_/g, ' ')
			});
			self.index++;
		});
		generatedDeliverableRequirements = $.trim(generatedDeliverableRequirements);

		if (generatedDeliverableRequirements !== '') {
			$(generatedDeliverableRequirements).appendTo(self.$deliverableRequirementsList);
		}
		self.sideEffects();
	},

	addDeliverableRequirementHandler: function () {
		var self = this;
		var disabledAttr = self.$addDeliverableRequirement.attr('disabled');
		var isAddButtonDisabled = disabledAttr !== undefined && disabledAttr !== false;
		if (isAddButtonDisabled) {
			return;
		}

		var code = self.$availableTypeOptions.val();
		var description = self.$availableTypeOptions.find(':selected').text();
		var numberOfFiles = self.$numberOfFiles.val();

		var requirement = {
			type: code,
			number_of_files: numberOfFiles === '' ? 1 : numberOfFiles,
			priority: self.index
		};

		var deliverableType = {code: code, description: description};
		var generatedRequirement = self.deliverableRequirementTemplate({
			deliverableType: deliverableType,
			deliverableRequirement: requirement,
			deliverableTypeCodeReplaced: deliverableType.code.replace(/_/g, ' ')
		});

		$(generatedRequirement).appendTo(self.$deliverableRequirementsList);
		self.index++;

		var $quickDescription = self.$deliverableRequirementsList.find('.quick-description');
		$quickDescription.last().autoresizeTextarea();

		var removeRequiredClassHelper = function (e) {
			$(e.target).toggleClass('required', _.isEmpty($(e.target).val()));
		};
		$(document).on({
			'keyup': removeRequiredClassHelper,
			'change': removeRequiredClassHelper
		}, '.quick-description');

		self.sideEffects(); // Remove deliverableType from dropdown
		return false;
	},

	removeDeliverableRequirementHandler: function (e) {
		var self = e.data.parentContext;
		var deliverableRequirement = $(this).closest('li');
		var code = $(this).parent().siblings('input.deliverable-type').val();
		var deliverableType = _.find(self.dropdownOptions, function (option) { return (option.code === code); });

		deliverableRequirement.remove();
		self.index--;
		self.renumberDeliverables();
	},

	addOrDeleteDropdownOption: function (typeOption,isDelete) {
		var self = this;
		if (isDelete) {
			var option = self.$availableTypeOptions.find('option[value="' + typeOption.code + '"]');
			option.remove();
			self.$availableTypeOptions.change();
		} else {
			var options = self.$availableTypeOptions.children();
			var newOption = $(self.deliverableRequirementOptionsTemplate({typeOptions: [typeOption]}))[0];
			options.push(newOption);
			options = _.sortBy(options, function (option) {
				var type = _.find(self.dropdownOptions, function (type) { return type.code === option.value; });
				return type.priority;
			});
			self.$availableTypeOptions.empty().append(options);
		}
	},

	renumberDeliverables: function () {
		var self = this;
		var requirements = self.$deliverableRequirementsList.find('li');
		_.each(requirements, function (requirement, ix) {
			var listItem = $(requirement);
			$('input.deliverable-id', listItem).attr('name', 'resourceCompletionForm.deliverableRequirements[' + ix + '].id');
			$('input.deliverable-type', listItem).attr('name', 'resourceCompletionForm.deliverableRequirements[' + ix + '].type');
			$('input.deliverable-number-of-files', listItem).attr('name', 'resourceCompletionForm.deliverableRequirements[' + ix + '].numberOfFiles');
			$('textarea.quick-description', listItem).attr('name', 'resourceCompletionForm.deliverableRequirements[' + ix + '].instructions');
		});

		var isThereAtLeastOneRequirement = requirements.length > 0;
		self.$deliverablesDeadlineSection.toggle(isThereAtLeastOneRequirement);

		if (!isThereAtLeastOneRequirement) {
			$('#hoursToComplete', self.$deliverablesDeadlineSection).val(0);
		}
	},

	sideEffects: function () {
		var self = this;
		// If all available types have been used, disable [addDeliverable] button
		if (self.index > (self.numberOfTypes-1)) {
			self.$addDeliverableRequirement.prop('disabled', true);
		}

		self.$numberOfFiles.val('');
		self.$numberOfFiles.attr('placeholder', '1');

		var requirements = self.$deliverableRequirementsList.find('li');

		var isThereAtLeastOneRequirement = requirements.length > 0;
		self.$deliverablesDeadlineSection.toggle(isThereAtLeastOneRequirement);

		if (!isThereAtLeastOneRequirement) {
			$('#hoursToComplete', self.$deliverablesDeadlineSection).val(0);
		}
	},

	dragStartHandler: function () {
		var self = this;

		$(self).addClass('ui-sortable-helper');
	},
	dragEndHandler: function () {
		var self = this;

		$(self).removeClass('ui-sortable-helper');
	},

	onlyNumericHandler: function (e) {
		//Exception to the rule: Allow backspace character
		if (e.which === 8) {
			return true;
		}
		var character =  String.fromCharCode(e.which);
		return $.isNumeric(character);
	},

	placeholderBlurHandler: function () {
		var self = this;
		if ($(self).val() === '') {
			if ($(self).attr('id') === 'hoursToComplete') {
				$(self).attr('placeholder', '0');
			} else if ($(self).attr('id') === 'numberOfFiles') {
				$(self).attr('placeholder', '1');
			} else {
				$(self).val(1);
			}
		}
	},

	placeholderFocusHandler: function () {
		var self = this;
		if ($(self).hasClass('deliverable-number-of-files')) {
			if ($(self).val() === '1') {
				$(self).val('');
			}
		} else {
			$(self).attr('placeholder', '');
		}
	},

	enableDisableAddButtonHandler: function () {
		var self = this;
		self.$addDeliverableRequirement.attr('disabled', (self.$availableTypeOptions.val() === 'default'));
	}
};

'use strict';

import HiddenFollowerTemplate from '../assignments/templates/details/hidden_follower.hbs';
import AssignmentRouting from '../routing/main';
import $ from 'jquery';
import _ from 'underscore';
import IntroJs from '../config/introjs';
import wmSelect from '../funcs/wmSelect';
import wmMaskInput from '../funcs/wmMaskInput';
import wmNotify from '../funcs/wmNotify';
import wmModal from '../funcs/wmModal';
import GooglePlaces from '../funcs/googlePlaces';
import wmFullscreen from '../funcs/wmFullScreen';
import getCSRFToken from '../funcs/getCSRFToken';
import 'jquery-ui';
import 'jquery-form/jquery.form';
import '../dependencies/jquery.calendrical';
import '../funcs/autoresizeTextarea';
import '../dependencies/jquery.tmpl';

export default function (data) {

	var $assignmentsForm = $('#assignments_form'),
		$submitFormBtn = $('#submit-form'),
		$templatesDropdown = $('#custom-forms-dropdown'),
		$routingResourceIds = $('#routing_resource_ids'),
		$routingGroupIds = $('#routing_groups_ids'),
		$primaryContact = $('#primary-contact'),
		$projectDropdown = $('#project-dropdown'),
		$industryDropdown = $('#industry'),
		$onsiteContactDropdown = $('#onsite-contact-dropdown'),
		$onsiteSecondaryContactDropdown = $('#onsite-secondary-contact-dropdown'),
		$onsiteSelect = $('#onsite-contact-select'),
		$secondaryContact = $('#secondary-contact'),
		$searchResources = $('#search-resources'),
		$showInFeed = $('#show_in_feed'),
		$addressTwoText = $('#address-two-selected-text'),
		$clientLocations = $('#clientlocations'),
		industryId,
		templateModal;

	_.extend(this, {
		addNewClientActive: false,
		addNewProjectActive: false,
		addressVerifications: [],
		oneTimeLocation: true,
		CONTACT_MANAGER: '-2',
		VIRTUAL_LOCATION: '-1',
		NEW_LOCATION: '0'
	});

	// Class variables.
	this.data = data || {
			mmwGlobal: [],
			mmwTemplates: []
		};

	// Initialize the default form.
	this.initialize = function (initialFilters) {
		if (initialFilters) {
			_.extend(this.data, initialFilters);
		}
		// Check the MMW global settings and see what we need to setup
		this.loadMmwGlobalCustomizations();
		this.loadInternal();
		this.loadCustomFields();
		this.loadAssessments();
		this.initLocationSelectBox();

		this.resetOnsiteContact();
		this.resetSecondaryOnsiteContact();
		// set masks
		wmMaskInput({ selector: '#onsite-phone' }, 'tel');
		wmMaskInput({ selector: '#onsite-secondary-phone' }, 'tel');
		this.loadTimeDefaults();
		this.renderFollowers();
		this.toggleSchedulingSelect($('input[name=scheduling]:checked').val());
		this.toggleLocationSelect($('input[name=location]:checked').val());

		if (this.data.defaultClientId) {
			$('#client_company_list').find('option[value="' + this.data.defaultClientId + '"]').prop('selected', true);
			this.clientCompanySelection(this.data.defaultProjectId);
		}

		if (this.savedRoutedWorkers !== null) {
			this.workerTypeahead();
		}
		wmSelect();
		wmSelect({ selector: $('#followers-dropdown') }, {
			onChange: _.bind(function () {
				this.renderFollowers();
			}, this)
		});

		this.initRecommenders();

		new GooglePlaces('#onetime-location');

		let properties = _.pick(config.form, 'routableGroups', 'groupIds', 'assignToFirstGroupIds', 'showInFeed', 'assignToFirstResource', 'isTemplate', 'isAdmin');
		let workNumber = config.form.workNumber;
		let el = '.routing-bucket';
		this.routing = new AssignmentRouting.Main({ properties, workNumber, el });
	};

	this.renderFollowers = function () {
		var hiddenFields = $('#followers-hidden-fields');
		// build hidden fields for each selected follower so jquery's
		// form serializer plays nice when we save a template
		// i.e. we want followers[0]=123&follwers[1]=156&....
		// instead of followers=123&followers=156
		hiddenFields.empty();

		var $followers = $('#followers-dropdown').children('option:selected');

		$followers.each(function (i, option) {
			hiddenFields.append(HiddenFollowerTemplate({
				i: i,
				val: $(option).val()
			}));
		});

		if ($followers.length === 0) {
			hiddenFields.append(HiddenFollowerTemplate({
				i: 0,
				val: ''
			}));
		}
	};

	this.initLocationSelectBox = function () {
		this.clientLocationSelectize = wmSelect({ selector: '#client_location_typeahead' }, {
			valueField: 'id',
			labelField: 'name',
			searchField: 'name',
			options: [],
			persist: false,
			loadThrottle: 200,
			create: false,
			allowEmptyOption: false,
			onChange: _.bind(this.locationBoxSelectionCallback, this),
			render: {
				option: function (item, escape) {
					return '<div>' + escape(_.unescape(item.name) + (_.isEmpty(item.number) ? '' :  ': Location Number (' + item.number + ')')) + '</div>';
				},
				item: function (item, escape) {
					return '<div>' + escape(_.unescape(item.name) + (_.isEmpty(item.number) ? '' :  ': Location Number (' + item.number + ')')) + '</div>';
				}
			},
			load: function (query, callback) {
				if (!query.length) {
					return callback();
				}

				$.ajax({
					url: '/addressbook/get_clientlocations.json?id='.concat($('#client_company_list').val().toString()),
					type: 'GET',
					dataType: 'json',
					data: {
						locationFilter: query
					},
					error: function () {
						callback();
					},
					success: function (res) {
						callback(res.data.locations);
					}
				});
			}
		});
	};

	this.locationBoxSelectionCallback = function () {
		$('#clientlocation_id').val(this.clientLocationSelectize.val());
		this.toggleLocationTypeViewAndFetchContacts(this.CONTACT_MANAGER, this.clientLocationSelectize.val());
	};

	this.getSelectedLocationData = function () {
		var selectedLocationName = $('#location-name-selected-text').text(),
			selectedLocationNumber = $('#location-number-selected-text').text(),
			selectedLocationId = $('#clientlocation_id').val();

		return {
			id : selectedLocationId,
			name : selectedLocationName,
			number : selectedLocationNumber
		};
	};

	//Global Company Work Market Settings (mmw)
	this.loadMmwGlobalCustomizations = function () {

		if (this.data.mmwGlobal.custom_forms_enabled_flag !== false && !this.data.isTemplate) {
			$('#custom-forms').removeClass('dn');

			// Populate the template dropdown.
			var options = '';
			var templates = this.data.mmwTemplates;

			_.each(templates, function (k, v) {
				options += '<option value="' + v + '">' + k + '<\/option>';
			});

			if (options.length > 0) {
				$templatesDropdown.append(options);
				// Sort drop-down-list by text - ignore first option
				var foption = $('option:first', $templatesDropdown);
				var soptions = $('option:not(:first)', $templatesDropdown).sort(function (a, b) {
					return a.text.toLowerCase() === b.text.toLowerCase() ? (a.text === b.text ? 0 : a.text < b.text ? -1 : 1) : a.text.toLowerCase() < b.text.toLowerCase() ? -1 : 1;
				});
				$templatesDropdown.html(soptions).prepend(foption);

				if (this.data.defaultSelectedTemplate) {
					$('option[value=' + this.data.defaultSelectedTemplate + ']', $templatesDropdown).prop('selected', true);
				}
			}

			// Remove the template option from the select list
			$('#assignment-actions-template').show();
		} else {
			// Remove the template option from the select list
			$('#assignment-actions-template').hide();
		}

		if (this.data.mmwGlobal.custom_fields_enabled_flag !== false) {
			$('#custom-fields').removeClass('dn');
		}
	};

	this.loadInternal = function () {
		var all_disabled = true;

		if (this.data.mmwGlobal.custom_close_out_enabled_flag !== false) {
			$('#assignment-closeout').removeClass('dn');
			all_disabled = false;
		}

		// If all customizations are disabled, hide entire section.
		if (all_disabled === false) {
			$('#assignment-internal').removeClass('dn');
			$(document).trigger('deliverablesPaneVisible');
		}
	};

	// Attachments / Deliverables
	$assignmentsForm.on('click', '.remove-attachment', function () {
		$(this).closest('.attachment-list-item').remove();
	});

	// Custom Fields Section
	this.loadCustomFields = function () {
		var prePos,
			self = this;

		$('#attached_field_sets_holder').sortable({
			handle: '.sort_handle',
			items: 'li:not(.requiredSet)',
			start: function (event, ui) {
				prePos = $(ui.item).index();
			},
			stop: function (event, ui) {
				var idName = $(ui.item.context.childNodes[1]).attr('id');
				self.renumberChoices(idName, prePos - $(ui.item).index());
			}
		});
	};

	this.renumberChoices = function (idName, move) {
		var down = (move < 0);
		var move = Math.abs(move);

		function rearrangeItems(item, type) {
			if (down) {
				var next = (type === 'item') ? item.next('tr') : item.next();
				if (next.length === 1) {
					next.after(item);
					if (type === 'handle') {
						next.attr('name', 'customfield[' + next.index() + ']');
					}
				}
			} else {
				var prev = (type === 'item') ? item.prev('tr') : item.prev();
				if (prev.length === 1) {
					prev.before(item);
					if (type === 'handle') {
						prev.attr('name', 'customfield[' + prev.index() + ']');
					}
				}
			}
		}

		for (var i = move; i > 0; --i) {
			$('#buyer_custom_fields_holder tr[class="customfields[' + idName + ']"]').each(function () {
				rearrangeItems($(this), 'item');
			});
			$('#resource_custom_fields_holder tr[class="customfields[' + idName + ']"]').each(function () {
				rearrangeItems($(this), 'item');
			});
			$('#attached_sets_input input[value="' + idName + '"]').each(function () {
				rearrangeItems($(this), 'handle');
				$(this).attr('name', 'customfield[' + $(this).index() + ']');
			});
		}
	};

	// Custom fields handling
	$assignmentsForm.on('click', '#add-field-set-button', _.bind(function () {
		var selectedItem = $('#custom-fields-dropdown').find(':selected');
		if (selectedItem.val() !== '') {
			this.addCustomFieldSet(selectedItem.val(), false, function () {
				selectedItem.remove();
			});
		}
	}, this));

	// Pass callback to keep things in order
	this.addCustomFieldSet = function (customfieldId, isRequired, callback) {

		if (customfieldId !== null) {
			var dropdown = $('#custom-fields-dropdown option[value="' + customfieldId + '"]');
			$('<input />')
				.attr('class', 'customFieldInput')
				.attr('type', 'hidden')
				.attr('name', 'customfield[' + $('#attached_sets_input > input').size() + ']')
				.attr('id', dropdown.text())
				.val(customfieldId)
				.appendTo('#attached_sets_input');

			// Load the field set
			return $.ajax({
				url: '/assignments/getcustomfield',
				global: false,
				type: 'GET',
				data: ({ id: customfieldId }),
				dataType: 'json',
				context: this
			}).success(_.bind(function (data) {
				if (data && data.success && data.data.length > 0) {
					this.populateCustomFields(data.data, customfieldId, isRequired);
					$('select[name=customfield] option[value=' + customfieldId + ']').prop('selected', true);
					$('select[name=customfield]').removeProp('checked');
					callback();
				} else {
					wmNotify({
						message: 'There were no custom fields available to add to this assignment.',
						type: 'danger'
					});
				}
			}, this));
		}
	};

	this.populateCustomFields = function (data, parentId, isRequired) {
		var buyer;
		var resource;

		var buyerRow = '';
		var resourceRow = '';

		$.each(data, function (i, item) {
			$.extend(item, {'parentId': parentId});
			var tmpl = (item.is_dropdown) ? '#customfields_dropdown_template' : '#customfields_template';

			if (item.type === 'owner') {
				buyer = true;
				buyerRow += $(tmpl).tmpl($.extend(item, {'readonly': false})).html();
			} else {
				resource = true;
				resourceRow += $(tmpl).tmpl($.extend(item, {'readonly': false})).html();
			}
		});

		if (buyer) {
			$('#buyer_custom').show();
			$('#buyer_custom_fields_holder > tbody:last').append('<tr class="customfields[' + parentId + ']"><td>' + buyerRow + '</td></tr>');
		}

		if (resource) {
			$('#resource_custom').show();
			$('#resource_custom_fields_holder > tbody:last').append('<tr class="customfields[' + parentId + ']"><td>' + resourceRow + '</td></tr>');
		}

		var text = $('input[value=' + parentId + '].customFieldInput').attr('id');
		$('#attached_field_sets_template').tmpl({
			'name': text,
			'id': parentId,
			'required': isRequired
		}).appendTo('#attached_field_sets_holder');
		$('#attached_sets').show();
		$('.field_value').autoresizeTextarea();
	};

	$assignmentsForm.on('click', '.cta-remove-choice', _.bind(function (event) {
		this.removeCustomFieldSet($(event.currentTarget).parent().attr('id'));
	}, this));

	this.removeCustomFieldSet = function (el) {
		// Clear the paperwork
		var removable = $('input[value=' + el + '].customFieldInput');
		$('#custom-fields-dropdown').append('<option value="' + el + '">' + removable.attr('id') + '</option>');
		$('#' + el).remove();
		removable.remove();

		// Clear any current custom fields
		$('#buyer_custom_fields_holder tr[class="customfields[' + el + ']"]').each(function () {
			$(this).remove();
		});
		$('#resource_custom_fields_holder tr[class="customfields[' + el + ']"]').each(function () {
			$(this).remove();
		});

		$('#attached_sets_input > input').each(function () {
			$(this).attr('name', 'customfield[' + $(this).index() + ']');
		});

		// If all fields cleared, clean up
		if ($('.customFieldInput').size() === 0) {
			$('select[name=customfield]').removeProp('checked');
			$('#buyer_custom').hide();
			$('#resource_custom').hide();
			$('#attached_sets').hide();
			return $.when();
		}
	};

	// Project Section
	// Show new project form
	$assignmentsForm.on('click', '#add-new-project', _.bind(function (e) {
		if ($projectDropdown.is(':disabled')) {
			return;
		}

		if (this.addNewProjectActive === false) {
			$('#new-project').slideDown().fadeIn();
			$('#add-new-project').html('Hide Add New Project');
			$('#add_new_project').attr('value', '1');
			this.addNewProjectActive = true;
		} else {
			this.closeNewProjectInputs();
		}
	}, this));


	this.closeNewProjectInputs = function () {
		$('#new-project').slideUp().fadeOut();
		$('#add-new-project').html('Add New Project');
		$('#add_new_project').attr('value', '0');
		this.addNewProjectActive = false;
	};

	$('#newproject_due_date').datepicker({dateFormat:'mm/dd/yy'});

	// Submit new project creation form.
	$('#newproject_form_submit').on('click', _.bind(function() {
		this.save_new_project($assignmentsForm.find('.newproject').fieldSerialize());
	}, this));

	this.save_new_project = function (variables) {
		$.ajax({
			url: '/assignments/add_new_project',
			type: 'POST',
			data:({ data: variables }),
			dataType: 'json',
			context: this,
			success: _.bind(function (data) {
				if (data && data.successful) {
					wmNotify({ message: 'Successfully added a new project.' });
					this.closeNewProjectInputs();
					$('.newproject').clearFields();

					// Add the new client company to the selects.
					var option = '';
					option += '<option value="' + data.data.id + '" selected="selected">' + data.data.name + '<\/option>';
					$projectDropdown.removeProp('selected');
					$projectDropdown.append(option);
				} else {
					_.each(data.errors, function (theMessage) {
						wmNotify({
							message: theMessage,
							type: 'danger'
						});
					});
				}
			}, this)
		});
	};

	this.buildClientProjectList = function (clientCompanyId, selectedProject) {
		$.ajax({
			context: this,
			url: '/assignments/get_client_projects',
			global :false,
			type: 'GET',
			data: ({ id: clientCompanyId }),
			dataType: 'json',
			success: _.bind(function (data) {
				if (data && data.success) {
					// build project list
					var options = '';
					if (data.data.length) {
						_.each(data.data, function (project) {
							options += '<option value="' + project.id + '">' + project.value + '<\/option>';
						});

						$projectDropdown.append(options);

						if (typeof selectedProject !== 'undefined') {
							$projectDropdown.find('option[value="' + selectedProject + '"]').prop('selected', true);
						}
					}
					$projectDropdown.removeProp('disabled');
					$('a#add-new-project').removeClass('disabled');
				}
			}, this)
		});
	};

	this.resetProjectsList = function () {
		// Empty the general contact select.
		$('#project-dropdown').children().remove().end().append('<option selected value="">Select<\/option>').prop('disabled', true);
		$('#new-project').slideUp().fadeOut();
		$('a#add-new-project span').html('Add New Project').parent().addClass('disabled');
		$('#add_new_project').attr('value', '0');
		$('#newproject_client_company').attr('value', '0');
		this.addNewProjectActive = false;
	};

	$projectDropdown.on('change', function () {
		if (!$projectDropdown.val()) {
			$('#assignment-project-budget').addClass('dn');
			return;
		}
		$.ajax({
			type: 'GET',
			url: '/assignments/get_project_remaining_budget',
			dataType: 'json',
			data:({
				id: $projectDropdown.val()
			}),
			success: function (data) {
				if (data.budgetEnabledFlag) {
					$('#assignment-project-budget').removeClass('dn');
				} else {
					$('#assignment-project-budget').addClass('dn');
				}
				$('#remainingBudget').html(data.remainingBudget);
			}
		});
	});

	// Clients // Location // On-site Contacts
	// Client Company dropdown selection.
	$('#client_company_list').on('change', _.bind(function () {
		this.clientCompanySelection();
	}, this));

	this.clientCompanySelection = function (selectedProject) {
		var clientCompanyId = $('#client_company_list').val(),
			$locationNameText = $('label[for="location-name-text"]');

		// Reset the location and project selects.
		this.resetOnsiteContact();
		this.resetSecondaryOnsiteContact();
		this.resetProjectsList();

		// Need this to set client company id in the project creation form.
		$('#newproject_client_company').attr('value', clientCompanyId);

		if (clientCompanyId !== '') {
			var typeAheadElem = $('#client_location_typeahead');
			typeAheadElem.attr('disabled', false);
			typeAheadElem.val('');
			$('#project-dropdown').attr('disabled', false);
			$('#badge_show_client_name').closest('label').show();

			// Build the project list.
			this.buildClientProjectList(clientCompanyId, selectedProject);

			// Make the location name look required
			$locationNameText.addClass('required');

		}
		else {
			$('#project-dropdown').attr('disabled', true);
			$('#badge_show_client_name').removeAttr('checked').closest('label').hide();
			$locationNameText.removeClass('required');
		}

		this.clearLocationSelectedContainer();
		this.initLocationSelectBox();
	};

	// Choose Client from list.
	$assignmentsForm.on('change', '.clientlocations_select', _.bind(function (el) {
		this.toggleLocationTypeViewAndFetchContacts(el.currentTarget.value);
	}, this));


	// Show new client form
	$('#add-new-client').on('click', _.bind(function (e) {
		if (this.addNewClientActive === false) {
			// open new client inputs
			$('#new-client').slideDown().fadeIn();
			$('a#add-new-client').html('Hide Add New Client');
			$('#add_new_client').attr('value', '1');
			this.addNewClientActive = true;
		} else {
			// close new client inputs
			$('#new-client').slideUp().fadeOut();
			$('a#add-new-client').html('Add New Client');
			$('#add_new_client').attr('value', '0');
			this.addNewClientActive = false;
		}
	}, this));

	// Create New Client
	$('#newclient_form_submit').on('click', _.bind(function () {
		$.ajax({
			url:'/assignments/addclientcompany',
			type:'POST',
			data:({ data: $assignmentsForm.find('.newclient').fieldSerialize() }),
			dataType: 'json',
			context: this,
			success: _.bind(function (data) {
				if (data && data.successful) {
					wmNotify({ message: 'Successfully added new client.' });
					// close new client inputs
					$('#new-client').slideUp().fadeOut();
					$('a#add-new-client').html('Add New Client');
					$('#add_new_client').attr('value', '0');
					this.addNewClientActive = false;
					$assignmentsForm.find('.newclient').clearFields();

					// Add the new client company to the selects.
					var option = '';
					option += '<option value="' + data.data.id + '" selected="selected">' + data.data.name + '<\/option>';
					$('select#client_company_list').removeProp('selected').append(option);

					// Clear the location section of any previous selections.
					this.clientCompanySelection();
				} else {
					_.each(data.errors, function (message) {
						wmNotify({
							message: message,
							type: 'danger'
						});
					});
				}
			}, this)
		});
	}, this));

	// TODO: Alex - uncouple the fetch contact logic from the location type view toggle logic
	this.toggleLocationTypeViewAndFetchContacts = function (companyLocationId, clientCompanyLocationId) {

		if (companyLocationId === undefined) {
			companyLocationId = $clientLocations.val();
		}

		// Reset the contact selects.
		this.resetOnsiteContact();
		this.resetSecondaryOnsiteContact();

		$('#location-contact-container').show();

		this.updateOnsiteContactFields('');
		this.updateSecondaryOnsiteContactFields('');

		this.clearLocationSelectedContainer();

		if (companyLocationId === this.NEW_LOCATION) {
			$('#virtual').hide();
			$('#onetime-location').show();
			$primaryContact.show();
			$secondaryContact.show();
			$('#location-selector').hide();
			$('#clientlocations3').prop('checked', true);
		} else if (companyLocationId === this.VIRTUAL_LOCATION) {
			$('#virtual').show();
			$('#onetime-location').hide();
			$primaryContact.hide();
			$secondaryContact.hide();
			$('#location-selector').hide();
			$('#clientlocations2').prop('checked', true);
		} else {
			$('#virtual').hide();
			$('#onetime-location').hide();
			$primaryContact.hide();
			$secondaryContact.hide();
			$('#location-selector').show();
			$('#clientlocations1').prop('checked', true);
		}

		if (clientCompanyLocationId) {
			$('#onetime-location').hide();
		} else {
			// Clear out onetime location ID
			// Failure to do so will ignore updates to the address.
			$('#onetime_location_id').val('');
			this.oneTimeLocation = 0;
			return;
		}

		// Do the AJAX call

		$.ajax({
			url:'/assignments/get_clientlocation_contacts',
			global:false,
			type:'GET',
			data:({ id: clientCompanyLocationId }),
			dataType:'json',
			success: _.bind(function (data) {

				if (data && data.success) {
					// Fill in the selected address.
					if (typeof data.address === 'object') {
						$('#location-name-selected-text').html(data.address.location_name_text);

						if (data.address.location_number_text) {
							$('#location-number-selected-text').html('(Location Number: ' + data.address.location_number_text + ')');
							$addressTwoText.closest('p').show();
						} else {
							$('#location-number-selected-text').empty();
							$addressTwoText.closest('p').hide();
						}
						$('#address-one-selected-text').html(data.address.address_one_text);
						if (data.address.address_two_text) {
							$addressTwoText.html(data.address.address_two_text);
							$addressTwoText.closest('p').show();
						} else {
							$addressTwoText.closest('p').hide();
						}
						$('#city-selected-text').html(data.address.city_text);
						$('#state-selected-dropdown').html(data.address.state_dropdown);
						$('#postal-code-selected-text').html(data.address.zip_text);
						$('#location-selected-country').html(data.address.location_country);
						$('#dress_code_select-selected').html(data.address.dress_code_select);
						$('#location_type-selected').html(data.address.location_type);

						// Show the text container.
						$('#location-selected-text').show();

						// Make sure select box displays selection
						if (this.clientLocationSelectize.val() === '') {
							//this.$clientLocationTypeahead.tokenInput('add', this.getSelectedLocationData());
						}
					}

					this.buildOnsiteContactList(data);
				}
			}, this)
		});
	};

	this.clearLocationSelectedContainer = function () {
		$('#location-selected-text').hide();
		$('#location-name-selected-text').empty();
		$('#location-number-selected-text').empty();
		$('#address-one-selected-text').empty();
		$addressTwoText.empty();
		$('#city-selected-text').empty();
		$('#state-selected-dropdown').empty();
		$('#postal-code-selected-text').empty();
		$('#location-selected-country').empty();
		$('#dress_code_select-selected').empty();
		$('#location_type-selected').empty();
	};

	this.buildOnsiteContactList = function (data) {
		$('#onsite-contact-dropdown, #onsite-secondary-contact-dropdown').empty();
		var option = '';
		if (data.onsite && data.onsite.length > 0) {
			for (var i = 0, size = data.onsite.length; i < size; i++) {
				option += '<option value="' + data.onsite[i].id + '">' + data.onsite[i].value + '<\/option>';
			}
			option += '<option value="new"> - Create new contact<\/option>';
			option += '<option value="" selected="selected"> - No contact<\/option>';
			$onsiteContactDropdown.append(option);
			$onsiteSecondaryContactDropdown.append(option);

			// Set the default selection if needed.
			if (typeof this.data.onsiteContact != 'undefined' && this.data.onsiteContact != '') {
				$('#onsite-contact-dropdown option[value=' + this.data.onsiteContact + ']').prop('selected', true);
			}

			// Set the default selection for secondary if needed.
			if (typeof this.data.onsiteContactSecondary != 'undefined' && this.data.onsiteContactSecondary != '') {
				$('#onsite-secondary-contact-dropdown option[value=' + this.data.onsiteContactSecondary + ']').prop('selected', true);
			}

			// If an option is selected, force looking up the contact information to populate the fields.
			var new_selected = $onsiteContactDropdown.val();
			if (typeof new_selected != 'undefined' && new_selected != '' && new_selected != 0) {
				this.updateOnsiteContactFields(new_selected);

			} else if (new_selected == '') {
				$('#onsite-contact').hide();
			}

			// If an option is selected, force looking up the secondary contact information to populate the fields.
			var new_selected_secondary = $onsiteSecondaryContactDropdown.val();
			if (typeof new_selected_secondary != 'undefined' && new_selected_secondary != '' && new_selected_secondary != 0) {
				this.updateSecondaryOnsiteContactFields(new_selected_secondary);
			} else if (new_selected_secondary == '') {

				$('#onsite-secondary-contact').hide();
			}

		} else {
			option += '<option value="new"> - Create new contact<\/option>';
			option += '<option value="" selected="selected"> - No contact<\/option>';
			$onsiteContactDropdown.append(option);
			$onsiteSecondaryContactDropdown.append(option);

			// Select the newely added option.
			$('#onsite-contact-dropdown option[value=""]').prop('selected', true);
			$('#onsite-secondary-contact-dropdown option[value=""]').prop('selected', true);

			this.updateOnsiteContactFields('');
			this.updateSecondaryOnsiteContactFields('');
		}
		$onsiteSelect.show();
		$onsiteSecondaryContactDropdown.show();
	};

	this.resetOnsiteContact = function () {
		// Empty the onsite contact select
		$onsiteContactDropdown.children().remove();
		$onsiteSelect.hide();
	};

	this.resetSecondaryOnsiteContact = function () {
		// Empty the onsite contact select
		$onsiteSecondaryContactDropdown.children().remove();
		$('#onsite-secondary-contact-select').hide();
	};

	$onsiteContactDropdown.on('change', _.bind(function () {
		this.updateOnsiteContactFields($onsiteContactDropdown.val());
	}, this));

	this.updateOnsiteContactFields = function (selected) {
		var $newContact = $('#onsite-contact-new-contact'),
			$newLocation = $('#onsite-contact-new-location-contact'),
			$selectedOnsite =$('#onsite-contact-selected');

		if (selected === '' || selected === '0' || selected === 'new') {
			this.resetOnsiteContactFieldsText();
			$primaryContact.show();

			// Check to see if a location is selected and if creating a new contact.
			if ($('#client_company_list').val() != '') {
				if ($clientLocations.val() !== '0') {
					$newLocation.hide();
					$newContact.show();
				} else if ($clientLocations === '0') {
					$newContact.hide();
					$newLocation.show();
				}
			}

			if (selected === '') {
				$primaryContact.hide();
			}

			// If new selected, clear fields
			if (selected === 'new') {
				$('#onsite-firstname').val('');
				$('#onsite-lastname').val('');
				$('#onsite-phone').val('');
				$('#onsite-email').val('');
			}

			return;
		} else if (selected === '') {
			this.resetOnsiteContactFieldsText();
			$primaryContact.hide();

			return;
		}

		$newContact.hide();

		$.ajax({
			url: '/assignments/get_client_location_contact',
			global: false,
			type: 'GET',
			data:({ id: selected }),
			dataType: 'json',
			success: function (data) {
				if (data && data.successful) {

					var str = '';
					if (typeof data.phones != "undefined" && data.phones.length > 0) {
						for (var i = 0, size = data.phones.length; i < size; i++) {
							str += 'Phone: ' + data.phones[i].phone;
							if (data.phones[i].extension) {
								str += ' x' + data.phones[i].extension;
							}
							str += '<br/>';
						}
					}
					$('#onsite-phone-selected').html(str);

					str = '';
					if (typeof data.emails != "undefined" && data.emails.length > 0) {
						for (var i = 0, size = data.emails.length; i < size; i++)
							str += 'Email: ' + data.emails[i].email + '<br/>';
					}
					$('#onsite-email-selected').html(str);

					$primaryContact.hide();
					$selectedOnsite.show();
				} else {
					if (typeof data != "undefined" && data != null) {
						wmNotify({
							message: 'An error occurred fetching this contacts data.',
							type: 'danger'
						});
					}

					$primaryContact.show();
					$selectedOnsite.hide();
				}
			}
		});
	};

	$onsiteSecondaryContactDropdown.on('change', _.bind(function () {
		this.updateSecondaryOnsiteContactFields($onsiteSecondaryContactDropdown.val());
	}, this));

	this.updateSecondaryOnsiteContactFields = function (selected) {

		if (selected === '' || selected === '0' || selected === 'new') {
			this.resetSecondaryOnsiteContactFieldsText();
			$secondaryContact.show();

			// Check to see if a location is selected and creating a new contact.
			if ($('#client_company_list').val() != '') {
				if ($clientLocations.val() != '0') {
					$('#onsite-secondary-contact-new-location-contact').hide();
					$('#onsite-secondary-contact-new-contact').show();
				} else if ($clientLocations.val() == '0') {
					$('#onsite-secondary-contact-new-contact').hide();
					$('#onsite-secondary-contact-new-location-contact').show();
				}
			}

			if (selected == '') {
				$secondaryContact.hide();
			}

			// If new selected, clear fields
			if (selected == 'new') {
				//clear secondary onsite contact fields inputs
				$('#onsite-secondary-firstname').val('');
				$('#onsite-secondary-lastname').val('');
				$('#onsite-secondary-phone').val('');
				$('#onsite-secondary-email').val('');
			}

			return;
		} else if (selected == '') {
			this.resetSecondaryOnsiteContactFieldsText();
			$secondaryContact.hide();

			return;
		}

		$onsiteSecondaryContactDropdown.hide();

		$.ajax({
			url: '/assignments/get_client_location_contact',
			global: false,
			type: 'GET',
			data: ({ id: selected }),
			dataType: 'json',
			success: function (data) {
				if (data && data.successful == true) {

					var str = '';
					if (typeof data.phones != "undefined" && data.phones.length > 0) {
						for (var i = 0, size = data.phones.length; i < size; i++) {
							str += 'Phone: ' + data.phones[i].phone;
							if (data.phones[i].extension) {
								str += ' x' + data.phones[i].extension;
							}
							str += '<br/>';
						}
					}
					$('#onsite-secondary-phone-selected').html(str);

					str = '';
					if (typeof data.emails != "undefined" && data.emails.length > 0) {
						for (var i = 0, size = data.emails.length; i < size; i++)
							str += 'Email: ' + data.emails[i].email + '<br/>';
					}
					$('#onsite-secondary-email-selected').html(str);

					$secondaryContact.hide();
					$('#onsite-secondary-contact-selected').show();
				} else {
					if (typeof data != "undefined" && data != null) {
						wmNotify({
							message: 'An error occurred fetching this contacts data.',
							type: 'danger'
						});
					}

					$secondaryContact.show();
					$('#onsite-secondary-contact-selected').hide();
				}
			}
		});
	};

	this.resetOnsiteContactFieldsText = function () {
		$('#onsite-contact-selected').hide();
		$('#onsite-firstname-selected').empty();
		$('#onsite-lastname-selected').empty();
		$('#onsite-phone-selected').empty();
		$('#onsite-email-selected').empty();
	};

	this.resetSecondaryOnsiteContactFieldsText = function () {
		$('#onsite-secondary-contact-selected').hide();
		$('#onsite-secondary-firstname-selected').empty();
		$('#onsite-secondary-lastname-selected').empty();
		$('#onsite-secondary-phone-selected').empty();
		$('#onsite-secondary-email-selected').empty();
	};

	this.setOneTimeLocation = function (address) {
		this.toggleLocationTypeViewAndFetchContacts(this.NEW_LOCATION);
		this.add_location_to_fields(address);
	};

	this.add_location_to_fields = function (address) {

		$('#location-name-text').val(address.name);
		if (address.number) {
			$('#location-number-text').val(address.number);
		}
		$('#address-one-text').val(address.address.addressLine1);
		if (address.address.addressLine2) {
			$('#address-two-text').val(address.address.addressLine2);
		}
		$('#city-text').val(address.address.city);
		$('#postal-code-text').val(address.address.zip);

		$('#state-dropdown option[value=' + address.address.state + ']').prop('selected', true);
		$('#location-country option[value=' + address.address.country + ']').prop('selected', true);
		$('#dress_code_select option[value="' + address.address.dressCode + '"]').prop('selected', true);
		$('#location_type option[value="' + address.address.type + '"]').prop('selected', true);
	};

	// Location / Address Section
	this.initAddressVerifications = function () {
		// Check if this is a template. If so, then skip verification.
		if (this.data.isTemplate) {
			return;
		}

		if ($clientLocations.val() == '0') {
			this.addressVerifications.push($('#onetime-location').address({
				'fields':{
					'address1':    'input[name="location_address1"]',
					'address2':    'input[name="location_address2"]',
					'city':        'input[name="location_city"]',
					'state':       'select[name="location_state"]',
					'postal_code': 'input[name="location_zip"]',
					'country':     'select[name="location_country"]'
				}
			}));
		}
	};

	/**
	 * Recursively iterate through all addresses registered for verification.
	 * Execute the provided callback upon completion.
	 */
	this.verify_addresses = function (startindex, callback) {
		if (startindex >= this.addressVerifications.length) {
			callback.call(this);
		}
		if (this.addressVerifications[startindex]) {
			this.addressVerifications[startindex].verify({
				'verifiedCallback': _.bind(function () {
					this.verify_addresses(startindex + 1, callback);
				}, this)
			});
		}
	};

	// On post back errors, pre-set some values
	$('input[name=location]').on('click', _.bind(function (el) {
		this.toggleLocationSelect(el.currentTarget.value)
	}, this));

	this.toggleLocationSelect = function (radioValue) {
		var $locationOneSelect = $('#location1_select'),
			$locationTwoSelect = $('#location2_select');

		$locationOneSelect.hide();
		$locationTwoSelect.hide();
		$('#set_location_options_on').show();
		$('#set_location_options_off').hide();

		if (radioValue === '2') {
			$locationTwoSelect.show();
		} else if (radioValue === '1') {
			$locationOneSelect.show();
		}
	};

	// Scheduling
	this.loadTimeDefaults = function () {
		var $to = $('#to'),
			$from = $('#from'),
			$from2 = $('#from2'),
			$fromTime = $('#fromtime');

		$fromTime.calendricalTime({
			startDate: $from
		});

		$('#fromtime2, #totime').calendricalTimeRange({
			startDate: $from2,
			endDate: $to,
			usa : true
		});

		// Initialize date/time pickers.
		$from.datepicker({dateFormat:'mm/dd/yy', onSelect:function () {
			$from.removeClass('placeholder');
		}});

		$to.datepicker({dateFormat:'mm/dd/yy', onSelect:function () {
			$('#to').removeClass('placeholder');
		}});

		$from2.datepicker({dateFormat:'mm/dd/yy', onSelect:function () {
			$from2.removeClass('placeholder');
			if ($to.val() == 'MM/DD/YYYY') {
				$to.val($('#from2').val());
				$to.removeClass('placeholder');
			}
		}});

		if (!$fromTime.val()) {
			$fromTime.val('8:00am');
		}

		if (!$('#totime').val()) {
			$('#totime').val('5:00pm');
		}

		if (!$('#fromtime2').val()) {
			$('#fromtime2').val('8:00am');
		}
	};

	$assignmentsForm.on('click', 'input[name=scheduling]', _.bind(function (el) {
		this.toggleSchedulingSelect(el.currentTarget.value);
	}, this));


	this.toggleSchedulingSelect = function (radioValue) {
		if (radioValue === '0') {
			$('#scheduling_specific').show();
			$('#scheduling_variable').hide();
		} else if (radioValue === '1') {
			$('#scheduling_specific').hide();
			$('#scheduling_variable').show();
		}
	};

// Surveys
	this.loadAssessments = function () {
		if (this.data.mmwGlobal.assessments_enabled_flag != false) {
			$('#assignment-assessments').removeClass('dn');
		}
	};

	// Attach surveys.
	$assignmentsForm.on('click', '#cta-add-survey', _.bind(function () {
		this.addSurvey(undefined, undefined, 1)
	}, this));

	$('#selected_assessments').on('click', '.cta-remove-survey', function () {
		var parent = $(this).closest('li');
		$('#assessment_id').find('option[value="' + parent.data('assessment_id') + '"]').removeAttr('disabled');

		parent.remove();

		// We need to properly reset the id order if user removes assessment out of order
		var count = 0;
		$("#selected_assessments li input[type='hidden']").each(function () {
			$(this).attr("name", "assessments[" + (count) + "].id");
		});
		$("#selected_assessments li input[type='checkbox']").each(function () {
			$(this).attr("name", "assessments[" + (count++) + "].isRequired");
		});
	});

	this.addSurvey = function (id, required) {
		var $assessmentId = $('#assessment_id');

		if (!id) {
			var id = $assessmentId.val();
			if (!id) {
				return;
			}
		}

		$assessmentId.find('option[value="' + id + '"]').attr('disabled', 'disabled');
		$assessmentId.val('');

		$('#tmpl-assessment-item')
			.tmpl({
				id: id,
				name: $assessmentId.find('option[value="' + id + '"]').text(),
				required: required,
				index: $('#selected_assessments li').size()
			})
			.data('assessment_id', id)
			.appendTo('#selected_assessments');
	};

// Pricing Options
	$assignmentsForm.on('click', '#pricing-internal', () => {
		$showInFeed
			.prop('disabled', true)
			.parents('div:first')
			.addClass('dn');
		this.routing.setInternalPricing(true);
	});

	$('#pricing-internal').parent().siblings().find('a').on('click', () => {
		$showInFeed
			.prop('disabled', false)
			.parents('div:first')
			.removeClass('dn');
		this.routing.setInternalPricing(false);
	});

// Routing & Save Options
	// If no group selected, no person selected, not for workfeed, and not sent from a profile,
	// then hide the send button and show search resources button...
	// If group or specific person selected,
	// then hide the search resources button...

	// Set the default to say that an assignment will go through the search
	// flow and not be for a specific person
	this.assignmentFor = false;

	// Set the default to say that the form is for a template, not an assignment
	this.data.isTemplate = false;

	this.updateSendControls = function () {

		if ($routingGroupIds.val() || $routingResourceIds.val()) {
			$searchResources.addClass('dn')
		} else {
			$searchResources.removeClass('dn')
		}
	};

	this.addSaveDraftHandler = function () {
		// Add listener to the Save as... select
		$assignmentsForm.one('click', '#assignment-actions-draft', _.bind(function (e) {
			e.preventDefault();
			$(e.currentTarget).prop('disabled', 'disabled');
			this.save_draft(e);
		}, this));
	};

	this.addSaveDraftHandler();

	this.save_draft = function (e) {

		// make sure nothing is selected
		$templatesDropdown.removeProp('selected');

		// save description
		$('#desc-text').wysiwyg('save');

		// Remove the placeholder text.
		this.removePlaceholders();

		// Put the placeholders back.
		this.replacePlaceholders();

		// Fire off AJAX call to save the form fields.
		$.ajax({
			url: '/assignments/save_draft',
			type: 'POST',
			data:($assignmentsForm.serializeArray()),
			dataType: 'json',
			context: this,
			success: function (data) {
				function redirectWithFlash(url, type, msg) {
					var e = $('<form></form>');
					e.attr({
						'action':'/message/create',
						'method':'POST'
					});
					e.append(
						$('<input>').attr({
							'name': 'message[]',
							'value': msg
						}));
					e.append(
						$('<input>').attr({
							'name': 'type',
							'value': type
						}));
					e.append(
						$('<input>').attr({
							'name': 'url',
							'value': url
						}));
					e.append(
						$('<input>').attr({
							'name': '_tk',
							'value': getCSRFToken()
						}));
					$('body').append(e);
					e.submit();
				}

				if (data.successful) {
					if (data.messages) {
						redirectWithFlash(data.redirect, 'success', data.messages);
					}
				} else {
					_.each(data.messages, function (theMessage) {
						wmNotify({
							message: theMessage,
							type: 'danger'
						});
					});
					$(e.currentTarget).removeAttr('disabled');

					// re-enable the 'Save Changes' button if response was not 'success'
					this.addSaveDraftHandler();
				}
			}
		});
	};

	$assignmentsForm.one('click', '#submit-form', _.bind(function (event) {
		event.preventDefault();
		this.submit_form();
	}, this));
	// If user tabs to the submit button and hits enter, then catch that event and submit the form
	$submitFormBtn.bind('keypress', _.bind(function (e) {
		if (e.keyCode == 13) {
			this.submit_form();
		}
	}, this));

	this.submit_form = function () {
		this.removePlaceholders();
		this.initAddressVerifications();
		this.verify_addresses(0, function () {
			$submitFormBtn.unbind();
			$submitFormBtn.prop('disabled', 'disabled');
			this.getRouting();
			$assignmentsForm.trigger('submit');
		});
	};

	this.getRouting = function () {
		//adapt routing v2 to old assignment creation code
		var routingObj = this.routing.getRoutingObject().routing;
		var params = [];
		params.push(
			{
				'name': 'routing.lanes',
				'value': '1'
			},
			{
				'name': 'routing.lanes',
				'value': '2'
			},
			{
				'name': 'routing.lanes',
				'value': '3'
			},
			{
				'name': 'assign_to_first_resource',
				'value': routingObj.assignToFirstGlobal
			}
		);
		if (routingObj.sendType === 'direct_send') {
			params.push(
				{
					'name': 'show_in_feed',
					'value': routingObj.showInFeed
				}
			);
			if (routingObj.assignToFirstGroup) {
				params.push(
					{
						'name': 'routing.assignToFirstToAcceptGroupIds',
						'value': routingObj.groupIds
					}
				);
			} else {
				params.push(
					{
						'name': 'routing.needToApplyGroupIds',
						'value': routingObj.groupIds
					}
				);
			}
			if (routingObj.assignToFirstGroupIds !== null) {
				params.push(
					{
						'name': 'routing.assignToFirstToAcceptGroupIds',
						'value': routingObj.assignToFirstGroupIds
					}
				);
			}
			if (routingObj.assignToFirstTalent) {
				params.push(
					{
						'name': 'routing.assignToFirstToAcceptUserNumbers',
						'value': routingObj.resourceIds
					}
				);
			} else {
				params.push(
					{
						'name': 'routing.needToApplyUserNumbers',
						'value': routingObj.resourceIds
					}
				);
			}
			if (routingObj.assignToFirstVendor) {
				params.push(
					{
						'name': 'routing.assignToFirstToAcceptVendorCompanyNumbers',
						'value': routingObj.companyIds
					}
				);
			} else {
				params.push(
					{
						'name': 'routing.needToApplyVendorCompanyNumbers',
						'value': routingObj.companyIds
					}
				);
			}
		} else if (routingObj.sendType === 'work_send') {
			params.push(
				{
					'name': 'smart_route',
					'value': true
				},
				{
					'name': 'show_in_feed',
					'value': routingObj.showInFeed
				}
			);
		} else {
			params.push(
				{
					'name': 'show_in_feed',
					'value': routingObj.showInFeed
				}
			);
		}

		analytics.track('Assignment Creation', {
			action: 'Routing Module v2 Routed',
			'Routing Type': routingObj.sendType,
			'Public Marketplace Check': routingObj.showInFeed
		});

		$assignmentsForm.append($.map(params, function (param) {
			return $('<input>', {
				type: 'hidden',
				name: param.name,
				value: param.value
			});
		}));
	};

	this.removePlaceholders = function () {
		if (!$.support.placeholder) {
			$assignmentsForm.find('.placeholder').each(function () {
				if ($(this).val() === $(this).attr('placeholder')) {
					$(this).val('');
				}
			});
		} else {
			$assignmentsForm.find('placeholder').each(function () {
				if ($(this).val() === $(this).attr('placeholder')) {
					$(this).val('');
				}
			});
		}
	};

	this.replacePlaceholders = function () {
		if (!$.support.placeholder) {
			$assignmentsForm.find('.placeholder').each(function () {
				$(this).blur();
			});
		} else {
			$assignmentsForm.find('placeholder').each(function () {
				$(this).blur();
			});
		}
	};

	this.workerTypeahead = function () {
		wmSelect({ selector: '#routing_resource_ids' }, {
			valueField: 'id',
			labelField: 'name',
			searchField: 'name',
			options: [],
			persist: false,
			loadThrottle: 200,
			create: false,
			allowEmptyOption: true,
			onChange: _.bind(this.updateSendControls, this),
			render: {
				option: function (item, escape) {
					return '<div>' + escape(item.name + ' | ID: ' + item.id + ' | ' + item.address) + '</div>'
				},
				item: function (item, escape) {
					return '<div>' + escape(item.name + ' | ID: ' + item.id + ' | ' + item.address) + '</div>'
				}
			},
			load: function (query, callback) {
				if (!query.length) {
					return callback();
				}

				$.ajax({
					url: '/search/suggest_users.json',
					type: 'GET',
					dataType: 'json',
					data: {
						term: query
					},
					error: function () {
						callback();
					},
					success: function (res) {
						callback(res);
					}
				});
			}
		});
	};

	this.savedRoutedWorkers = function () {
		if (!_.isEmpty(this.data.workerDirectSelections)) {
			return $.unescapeAndParseJSON(this.data.workerDirectSelections);
		}
	};

	$industryDropdown.on('change', _.bind(function (el) {
		$('.worker-routing-container .token-input-list').remove();
		this.workerTypeahead();
		industryId = el.currentTarget.value;
	}, this));

	// Save Template Modal
	$('#assignment-actions-template').on('click', () => {
		templateModal = wmModal({
			title: 'Save as Template',
			content: $('#give_template_name').html(),
			destroyOnClose: true,
			autorun: true
		});
	});

	// Load Template on Select
	$templatesDropdown.on('change', function () {
		// Set some form field values.
		$('#forResource').val($('#assignToUserId').val());
		$('#load_template_form-template_id').val($(this).val());
		$('#current_template_fields').val($assignmentsForm.formSerialize());

		// Submit the form to load the template.
		$('#load_template_form').submit();
	});

	// Save Template on Submit
	$(document).on('click', '#give_template_name_submit', _.bind(function () {
		this.removePlaceholders();

		var formFields = {},
			$templateForm = $('#give_template_name_form');
		formFields['name'] = $templateForm.formSerialize();
		formFields['template'] = $assignmentsForm.formSerialize();

		this.replacePlaceholders();

		$templateForm.ajaxSubmit({
			dataType: 'json',
			data: formFields,
			success: function (data) {
				if (data.successful) {
					// Push the new option into the select.
					$templatesDropdown.append('<option value="' + data.data.template_id + '">' + data.data.template_name + '<\/option>');
					$templatesDropdown.removeProp('selected');
					// Select the newely added option.
					$('#custom-forms-dropdown option[value=' + data.data.template_id + ']').prop('selected', 'selected');
					templateModal.destroy();
					wmNotify({ message: 'Your template has been successfully saved.' });
				} else {
					_.each(data.messages, function (theMessage) {
						wmNotify({
							message: theMessage,
							type: 'danger'
						});
					});
				}
			}
		});
		return false;
	}, this));

	$(function () {
		var intro = IntroJs('intro-assignment-creation-tour');
		var $introHelper = $('.intro-available-funds-helper');
		intro.setOptions({
			steps: [
				{
					intro: "<h4>Assignments are the hub of Work Market’s Freelance Management Solution (FMS).</h4><p>After an assignment is created and sent, workers will apply for the work, ask you questions or make counteroffers.</p><p>Use assignment features to find qualified candidates, define project scope and communicate with workers in real-time.</p>"
				},
				{
					element: document.querySelector('.intro-require-check-in'),
					intro: "<h4>Require check-in & check-out</h4><p>The Work Market mobile app, available on Android and iOS devices, is GPS enabled so workers can check-in and check-out from assignment sites.</p><p>This feature helps promote worker accountability and gives clients invaluable peace of mind.</p>",
					position: "right"
				},
				{
					element: document.querySelector('#price-container'),
					intro: "<h4>Price your assignment</h4><p>Assignments can be flexibly priced. You can configure your pricing calculator so your company, or worker, pays the transaction fee.</p><p>Add and verify a bank account to enable $1,000 in payment terms. <a href='https://workmarket.zendesk.com/hc/en-us/articles/210052747-What-are-payment-terms-and-how-can-I-edit-them' target='_blank'>Learn more.</a></p>",
					position: "right"
				},
				{
					element: document.querySelector('.sending-options-container'),
					intro: "<h4>Routing options</h4><p>Send assignments to your Talent Pools, individuals, or go to the Search page for more advanced matching.</p><p>If you’re ready to send your assignment, it’s time to <a href='/payments' target='_blank'>set up payment methods</a>.</p><p>Not ready to send yet? Save your assignment as a template or draft for later.</p>",
					position: "right"
				}
			]
		});

		intro.onafterchange(function (e) {
			if (e) {
				e.id == 'price-container' ? $introHelper.hide() : $introHelper.show();
			}
		});

		intro.onexit(function () {
			$introHelper.show();
		});

		intro.watchOnce();

		// This button is inside the form and has 'type=submit' (default when no type is given).
		// This causes the form to get submitted when the button is clicked. We have an 'on click' handler that will run
		// the first time the button is clicked. Here we set the button type to 'button' so that the form will
		// only be submitted by the event handler we defined.
		$('#submit-form').prop('type', 'button');

		const MAX_DECIMALS = 3;
		// prevent user from entering 'per unit' price with more than 3 decimal places
		$('#per_unit_price').on("input", function (e) {
			// http://stackoverflow.com/questions/4912788/truncate-not-round-off-decimal-numbers-in-javascript
			var re = new RegExp("(\\d+\\.\\d{" + MAX_DECIMALS + "})(\\d)"),
				m = this.value.toString().match(re);
			if(m) {
				$(this).val(m[1]);
			}
		});
	});


	this.initRecommenders = () => {
		let desiredSkillsField = $.trim($('#desired_skills').val());
		if (desiredSkillsField !== '') {
			let fields = desiredSkillsField.split('--');
			if (fields.length > 1 ) {
				$('#job-title-autocomplete').val(fields[1]);
				this.setJobTitle({uuid: null, name: fields[1]});
			}

			$.each(fields[0].split(','), (index, skill) => {
				this.addSkill(null, skill, false, true);
			});

			this.displaySkills();
		}

		this.skillsTypeahead();
		this.titlesTypeahead();
	};

	this.skillsTypeahead = () => {
		wmSelect({ selector: '.skills-autocomplete' }, {
			valueField: 'value',
			labelField: 'value',
			searchField: 'value',
			options: [],
			hideSelected: false,
			persist: false,
			delimiter: ',',
			loadThrottle: 200,
			allowEmptyOption: false,
			closeAfterSelect: true,
			create: (value) => ({id: null, value: value}),
			createOnBlur: true,
			onChange: (value) => {
				// we are checking 'value' here because selectize clear
				// and clearOptions will trigger onChange again with empty value
				if (value && value.length > 0) {
					this.addSkill(this.skillMap[value], value, false);
				}
				var thisSelectize = $('.skills-autocomplete')[0].selectize;
				thisSelectize.clear();
				thisSelectize.clearOptions();
				thisSelectize.renderCache = {};
				thisSelectize.loadedSearches = {};
            },
			load: (query, callback) => {
				if (!query.length) {
					return callback();
				}
				$.ajax({
					url: '/v2/suggest/skill',
					type: 'GET',
					dataType: 'json',
					data: {q: query},
					error: () => {
						this.skillMap = { };
						callback();
					},
					success: (res) => {
						$.each(res.results, function() {
							this.value = this.name;
						});
						this.skillMap = { };
						res.results.forEach((entry) => {
							this.skillMap[entry.value] = entry.id;
						});
						callback(res.results);
					}
				});
			}
		})[0].selectize;
		this.updateRecommendedSkills(true);
	};

	this.titlesTypeahead = () => {
		wmSelect({ selector: '#job-title-autocomplete' }, {
			valueField: 'name',
			labelField: 'name',
			searchField: 'name',
			options: [],
			hideSelected: false,
			persist: true,
			maxItems: 1,
			closeAfterSelect: true,
			loadThrottle: 200,
			allowEmptyOption: false,
			create: (value) =>  ({uuid: null, name: value}),
			createOnBlur: true,
			onChange: (value) => {
				var item = {uuid: this.jobTitleMap[value], name: value};
				this.setJobTitle(item);
				return item;
			},
			load: (query, callback) => {
				if (!query.length) {
					return callback();
				}
				$.ajax({
					url: '/v2/suggest/jobTitle',
					type: 'GET',
					dataType: 'json',
					data: {q: query},
					error: () => {
						this.jobTitleMap = { };
						callback();
					},
					success: function (res) {
						this.jobTitleMap = { };
						res.results.forEach((entry) => {
							this.jobTitleMap[entry.name] = entry.uuid;
						});
						callback(res.results);
					}
				});
			}
		})[0].selectize;
	};

	this.setJobTitle = function (title) {
		this.jobTitle = title;
		this.updateDesiredSkills();
		this.updateRecommendedSkills(true);
	};

	this.skills = new Object();
	this.recommendedSkills = [];
	this.jobTitleMap = { };
	this.skillMap = { };
	this.jobTitle = {uuid: null, name: null};

	this.addSkill = function (id, name, recommended, silent) {
		if ((!id && !this.skills[name]) || !this.skills[id]) {
			let skill = {
				id: id ? id : null,
				name: name,
				recommended: recommended
			};

			this.skills[id ? id : name] = skill;
			if (!silent) {
				this.displaySkills();
				this.updateRecommendedSkills(!id);
			}
		}
	};

	this.displaySkills = () => {
		let html = '';
		Object.keys(this.skills).forEach((key) => {
			const {
				recommended,
				name,
				id
			} = this.skills[key];
			html += `<li class="skill" recommended=${recommended} skill-id=${id}>${name}</li>`;
		});

		$('.skills-list').html(html);
		$('.your-skills li').click((e) => {
			const skill = $(e.currentTarget);
			this.removeSkill(skill.attr('skill-id'), skill.text());
		});

		this.updateDesiredSkills();
	};

	this.updateDesiredSkills = () => {
		let text = '';
		Object.keys(this.skills).forEach((key) => {
			const {
				name
			} = this.skills[key];
			text += `${name},`;
		});

		text = text !== '' ? text.slice(0, -1) : text;
		const jobTitle = this.jobTitle.name === null ? '' : this.jobTitle.name;
		$('#desired_skills').val(text + '--' + jobTitle);
	};

	this.removeSkill = (id, name) => {
		let key = id == 'null' ? name : id; //jquery text returns string null if null
		let skill = this.skills[key];
		delete this.skills[key];

		if (skill.recommended) {
			this.recommendedSkills.push(skill);
			this.displayRecommendedSkills();
		}

		this.displaySkills();
	};

	this.updateRecommendedSkills = (force) => {
		if (force || this.recommendedSkills.length === 0) {
			var requestData = {
				jobTitle: this.jobTitle && this.jobTitle.name ? this.jobTitle.name : '',
				offset: 0,
				limit: 10,
				industries: [$('#industry option').val()],
				selectedSkills: Object.keys(this.skills).map(key => this.skills[key].name),
				definedSkills: [],
				removedSkills: [],
				assignmentTitle: $('#title-input').val(),
				assignmentDescription: $('#desc-text').val()
			};

			$.ajax({
				url: '/v2/recommend/skill',
				type: 'POST',
				dataType: 'json',
				contentType: 'application/json; charset=utf-8',
				data: JSON.stringify(requestData)
			}).done((data) => {
				this.recommendedSkills = data.results;
				this.displayRecommendedSkills();
			});
		} else {
			this.displayRecommendedSkills();
		}
	};

	this.displayRecommendedSkills = () => {
		let html = '';
		let count = 0;
		$.each(this.recommendedSkills, (index, obj) => {
			if(!this.skills[obj.id] && !this.skills[obj.name]) {
				html += `<li class='skill' recommended=true skill-id=${obj.id}>${obj.name}</li>`;
				if (++count > 9) {
					return false;
				}
			}
		});

		$('.skills').html(html);
		$('.skills li').click((e) => {
			let skill = $(e.currentTarget);
			this.addSkill(skill.attr('skill-id'), skill.text(), true);
		});

	};

};

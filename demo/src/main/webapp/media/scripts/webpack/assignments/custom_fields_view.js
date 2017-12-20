'use strict';

import Template from './templates/details/custom_field_group_set_id.hbs';
import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import CustomFieldsGroupView from './custom_fields_group_view';
import CompletionBarView from './completion_bar_view';
import wmNotify from '../funcs/wmNotify';
import getCSRFToken from '../funcs/getCSRFToken';
import '../funcs/autoresizeTextarea';

export default Backbone.View.extend({
	events: {
		'click .button'               : 'save',
		'keydown textarea'            : 'shortcutSave',
		'click .toggle-add-set'       : 'toggleAddSet',
		'click #add-client-field-set' : 'addClientCustomFieldSet',
		'click .cta-remove-choice'    : 'removeClientCustomFieldSet'
	},

	initialize: function () {
		this.el = this.options.el;
		this.type = this.options.type;
		this.selectorPrefix = this.type !== '' ? '#' + this.type + '-' : '#';
		this.auth = this.options.auth;
		this.duringCompletion = this.options.duringCompletion;
		this.vent = this.options.vent;

		if ($.isArray(this.model) && this.model.length > 0) {
			this.model.sort(function (a, b) {
				return (a.position - b.position);
			});

			_.each(this.model, function (element, j) {
				element.groupIndex = j;
				_.each(element.fields, function (item, i) {
					item.groupIndex = j;
					item.index = i;
				});
			});
		}
	},

	render: function () {
		if (!this.model) {
			return this;
		}

		_.each(this.model, function (item) {
			this.$('.group_set_id_container').append(Template({'parent': item}));
		}, this);

		var bFieldsEl = $(this.selectorPrefix + 'buyer-custom-fields');
		var rFieldsEl = $(this.selectorPrefix + 'resource-custom-fields');

		var bfieldsSize = 0;
		_.each(this.model, function (item) {
			var bfields = new CustomFieldsGroupView({
				model: item,
				type: 'owner',
				auth: this.auth
			});
			var pendingSet = $('#custom-fields-dropdown option[value="' + item.id + '"]');
			bFieldsEl.append(bfields.render().el);
			pendingSet.remove();
			bfieldsSize += bfields.size();
		}, this);
		if (bfieldsSize === 0) {
			bFieldsEl.hide();
		}

		var rfieldsSize = 0;
		_.each(this.model, function (item) {
			var rfields = new CustomFieldsGroupView({
				model: item,
				type: 'resource',
				auth: this.auth
			});
			rFieldsEl.append(rfields.render().el);
			rfieldsSize += rfields.size();
		}, this);
		if (rfieldsSize === 0) {
			rFieldsEl.hide();
		}

		this.updateChecklist();
		this.$('.field_value').autoresizeTextarea();

		return this;
	},

	tgIncomplete: function () {
		$('#custom_fields_complete').hide();
		$('#cf_completion_list').removeClass('completion-success');
		$('#custom_fields_list_complete').hide();
		$('#custom_fields_list_incomplete').show();
		$('#custom_fields_incomplete').show();
	},

	tgComplete: function () {
		$('#custom_fields_incomplete').hide();
		$('#cf_completion_list').addClass('completion-success');
		$('#custom_fields_list_incomplete').hide();
		$('#custom_fields_complete').show();
		$('#custom_fields_list_complete').show();
	},

	updateChecklist: function() {
		var $table = $('#pane-resource-custom-fields');
		var $reqInput = $('input.required:text');
		var $reqSelect = $('select.required');
		var $cfCompleteList = $('#cf_completion_list');

		if ($reqInput.length === 0 && $reqSelect.length === 0) {
			$cfCompleteList.addClass('dn').removeClass('required-item');
		}

		if ($reqInput.length > 0) {
			$table.find($reqInput).each(_.bind(function (index, element) {
				if ($(element).val() === ''){
					this.tgIncomplete();
					return false;
				} else {
					if ($reqSelect.length > 0) {
						$table.find('select.required').each(_.bind(function (i, ele) {
							if ($(ele).val() === '') {
								this.tgIncomplete();
								return false;
							} else {
								this.tgComplete();
							}
						}, this));
					} else {
						this.tgComplete();
					}
				}
			}, this));
		} else {
			if ($reqSelect.length > 0) {
				$table.find('select.required').each(_.bind(function (index, element) {
					if ($(element).val() === '') {
						this.tgIncomplete();
						return false;
					} else {
						this.tgComplete();
					}
				}, this));
			}
		}
	},

	shortcutSave: function (e) {
		if (e.shiftKey && e.keyCode === 13) {
			this.save(e);
			e.stopPropagation();
			return false;
		}
	},

	save: function(e, callback) {
		e.preventDefault();
		var self = this;

		$(e.target).toggleClass('disabled');
		this.$('form input[name=onComplete]').val(this.duringCompletion && $(e.target).hasClass('resource-save-custom-fields-and-complete'));

		var $check = $('#' + $('form :focus').data('icon'));
		var pendingSets = $('.customFieldInput');
		var currentLength = this.model.length;
		var isPendingSets = pendingSets.length > 0;

		if (isPendingSets) {
			var pendingSetsArray = [];
			$('#is-pending-sets').val(true);

			_.each(pendingSets, function (cfSet) {
				var pendingFieldsArray = [];

				_.each($('*[data-parent="' + $(cfSet).val() + '"]'), function(cf) {
					var cfId = $(cf).data('id');
					pendingFieldsArray.push({
						id: cfId,
						index: $(cf).data('position'),
						isRequired: $(cf).data('required'),
						name: $(cf).data('name'),
						groupIndex: $(cfSet).data('pos') + currentLength,
						readOnly: 0,
						type: 'owner',
						value: $('[data-id="' + cfId + '"]').val(),
						visibleToOwner: 1
					});
				});
				pendingSetsArray.push({
					fields: pendingFieldsArray,
					id: $(cfSet).val(),
					name: $(cfSet).attr('id'),
					groupIndex: $(cfSet).data('pos') + currentLength,
					position: $(cfSet).data('pos') + currentLength,
					isRequired: 0
				});
			});

			for (var i = 0; i < pendingSetsArray.length; ++i) {
				this.$('.group_set_id_container').append(Template({'parent': pendingSetsArray[i]}));
				var pendingFields = new CustomFieldsGroupView({
					model: pendingSetsArray[i],
					type: 'owner',
					auth: this.auth
				});
				$('#pane-buyer-custom-fields').append(pendingFields.render().el);
			}
		}

		this.$('form').ajaxSubmit({
			dataType: 'json',
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

				$(e.target).toggleClass('disabled');
				if (data.successful) {
					if (isPendingSets) {
						redirectWithFlash(window.location, 'success', data.messages);
						return;
					} else {
						if ($.isFunction(callback)) {
							callback.call(self);
							return;
						}
						$check.removeClass('icon-exclamation').addClass('icon-ok').show();
					}
				} else {
					$check.removeClass('icon-ok').addClass('icon-exclamation').show();
				}
				_.each(data.messages, function (theMessage) {
					wmNotify({
						message: theMessage,
						type: data.successful ? 'success' : 'danger'
					});
				});
			}
		});

		this.updateChecklist();
		new CompletionBarView().render();
	},

	toggleAddSet: function () {
		var $addCfContainer = $('#add-cf-container');
		$addCfContainer.toggle();
		if ($addCfContainer.is(':visible')) {
			$('.toggle-add-set small').html('Hide add custom field set');
		} else {
			$('.toggle-add-set small').html('Show add custom field set');
		}
	},

	addClientCustomFieldSet: function () {
		var selectedItem = $('#custom-fields-dropdown').find('option:selected');
		if (selectedItem.val() !== '') {
			this.doAdd(selectedItem.val(), selectedItem.html(), false, function () {
				selectedItem.remove();
				$('.field_value').autoresizeTextarea();
			});
		}
	},

	removeClientCustomFieldSet: function (e) {
		this.doRemove($(e.currentTarget).parent().attr('id'));
	},

	doAdd: function (cfId, cfName, isRequired, callback) {
		var self = this;
		var prePos;

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

		if (cfId != null) {
			var dropdown = $('#custom-fields-dropdown option[value="' + cfId + '"]');
			var pos = $('#attached_sets_input > input').size();
			$('<input />')
				.attr('class', 'customFieldInput')
				.attr('type', 'hidden')
				.attr('name', 'customfield[' + pos + ']')
				.attr('id', dropdown.text())
				.data('pos', pos)
				.val(cfId)
				.appendTo('#attached_sets_input');

			// Load the field set
			return $.ajax({
				url: '/assignments/getcustomfield',
				type: 'GET',
				data: { id: cfId },
				dataType: 'json'
			}).success(function (data) {
				if (data && data.success && data.data.length > 0) {
					self.populateCustomFields(data.data, cfId, isRequired);
					$('select[name=customfield] option[value=' + cfId + ']').prop('selected', true);
					$('select[name=customfield]').removeProp('checked');
					callback();
				}
			});
		}
	},

	// NOTE: This duplicates the re-ordering of custom field sets once selected from dropdown (in assignment creation)
	renumberChoices: function (idName, move) {
		var down = (move < 0);
		move = Math.abs(move);

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
				rearrangeItems($(this), 'item', move);
			});

			$('#attached_sets_input input[value="' + idName + '"]').each(function () {
				rearrangeItems($(this), 'handle', move);
				$(this).attr('name', 'customfield[' + $(this).index() + ']');
			});
		}
	},

	populateCustomFields: function (data, parentId, isRequired) {
		var buyerRow = '';

		$.each(data, function (i, item) {
			$.extend(item, { parentId: parentId });
			var tmpl = (item.is_dropdown) ? '#customfields_dropdown_template' : '#customfields_template';
			buyerRow += $(tmpl).tmpl($.extend(item, {
				'readonly': false,
				'cfName': item.name,
				'cfPosition': i
			})).html();
		});

		$('#buyer_custom').show();
		$('#buyer_custom_fields_holder > tbody:last').append('<tr class="customfields[' + parentId + ']" data-set="'+ parentId + '"><td>' + buyerRow + '</td></tr>');

		var text = $('input[value=' + parentId + '].customFieldInput').attr('id');
		$('#attached_field_sets_template').tmpl({
			'name': text,
			'id': parentId,
			'required': isRequired
		}).appendTo('#attached_field_sets_holder');
		$('#attached_sets').show();
	},

	doRemove: function (el) {
		var removable = $('input[value=' + el + '].customFieldInput');
		$('#custom-fields-dropdown').append('<option value="' + el + '">' + removable.attr('id') + '</option>');
		$('#' + el).remove();
		removable.remove();

		// Clear any current custom fields
		$('#buyer_custom_fields_holder').find('tr[class="customfields[' + el + ']"]').each(function () {
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
			this.toggleAddSet(el);
			return $.when();
		}
	}
});

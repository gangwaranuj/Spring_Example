'use strict';

import $ from 'jquery';
import _ from 'underscore';
import Backbone from 'backbone';
import PreviewView from './preview_view';
import ErrorsView from './errors_view';
import WorkUploadCollectionList from './work_upload_list_collection';
import Template from '../funcs/templates/confirmAction.hbs';
import wmNotify from '../funcs/wmNotify';
import wmModal from '../funcs/wmModal';
import getCSRFToken from '../funcs/getCSRFToken';
import '../dependencies/jquery.tmpl';
import 'jquery-form/jquery.form';

export default Backbone.View.extend({
	el: 'body',

	events: {
		'click input[name="mapping[headers_provided]"]' : 'loadMapping',
		'click #save-mapping-outlet'                    : 'saveMapping',
		'click #preview-upload-outlet'                  : 'previewUpload',
		'click #save-upload-outlet'                     : 'saveUpload',
		'click #save-upload-outlet-async'               : 'saveUploadAsync',
		'change select[name$="\\.type"]'                : 'toggleActions'
	},

	initialize: function (options) {
		this.options = options || {};
		this.initialCategoryNames = this.options.initialFieldCategories;
		this.allFields = this.options.initialFieldTypes;
		this.allCategoryNames = [];

		this.preview = new PreviewView(_.extend(this.options, {
			parentView: this,
			collection: new WorkUploadCollectionList(this.options.response.uploads)
		}));

		// If no mapping group was specified, every row will error out. Don't bother showing.
		var initialErrors = (this.options.response.mappingGroup.id) ? this.options.response.errorUploads : null;

		this.errors = new ErrorsView({
			model: initialErrors
		});
		this.warnings = new ErrorsView({
			model: this.options.response.warnings,
			template: $('#tmpl-warnings').template(),
			el: $('#warnings')
		});

		// If no mapping group specified, display tip box advising user to save current mapping
		if (!(this.options.response.mappingGroup.id)) {
			$('#save-mapping-info').show();
		}

		this.render();

		$('select[name$="\\.type"] option:selected[value="user_number"]').trigger('change');
	},

	render: function () {
		var mappings = this.options.response.mappingGroup.mappings;
		var html = this.$('#field-mappings tbody');

		var additionalFields = this.getAdditionalFields();

		html.empty();
		$.each(mappings, function (i, item) {
			var row = $('#tmpl-field-mapping-row').tmpl({
				column_index: item.columnIndex,
				column_name: item.columnName,
				field_type_code: item.type.code,
				value: item.sampleValue
			});

			var select = row.find('select');
			$.each(additionalFields, function(j, field) {
				select.append(field.clone());
			});

			// If mapping value does not exist in dropdown, select the first option, '-- ignore --'
			// This will be the case if a client added a mapping for 'unique external id' and then disabled
			// unique external id in assignment settings
			// http://stackoverflow.com/questions/2248976/check-if-value-is-in-select-list-with-jquery
			if(0 != row.find('select option[value="' + item.type.code + '"]').length) {
				select.val(item.type.code);
			} else {
				select.find('option:first').attr('selected',true);
			}

			row.data('val', item.type.code);

			row.appendTo(html);
		});

		if (this.options.response.mappingGroup.name) {
			this.$('.mapping-group-name-outlet').text(this.options.response.mappingGroup.name);
		}

		this.setActionsEnabled(this.didConfigureMapping());

		this.preview.render();
		this.errors.render();
		this.warnings.render();

		return this;
	},

	getAdditionalFields: function () {
		var self = this;
		return _(this.allCategoryNames).chain().difference(this.initialCategoryNames).map(function(o) {

			var optgroup = $('<optgroup/>').attr('label', o);

			$.each(self.allCategories[o], function(key, val) {
				optgroup.append($('<option/>').val(key).text(val));
			});

			return optgroup.clone();
		}).value();
	},

	loadMapping: function (e, callback) {
		this.$('#map-form').ajaxSubmit({
			context: this,
			data: {
				preview: true
			},
			success: function (response) {
				this.handleLoadMappingResponse(response, e, callback);
			}
		});
	},

	handleLoadMappingResponse: function (response, e, callback) {

		this.preview.updateCollection(response.data.response.uploads);
		this.errors.model = response.data.response.errorUploads;
		this.warnings.model = response.data.response.warnings;
		this.options.response = response.data.response;
		this.render();

		// If errors, scroll up to reveal the situation.
		// Ideally we have a more elegant method of presenting validation errors
		// so that the page isn't 10km long.
		if (response.data.response.errorUploads) {
			window.scrollTo(0, $(this.el).offset().top);
		}

		if ($.isFunction(callback)) {
			callback.call(this, e);
		}
	},

	previewUpload: function (e) {
		this.loadMapping(e, function(e) {
			this.preview.show(e);
		});
	},

	saveUploadAsync: function () {
		this.$('#map-form').ajaxSubmit({
			context: this,
			headers: { 'X-CSRF-Token': getCSRFToken() },
			data: {
				preview: false,
				async: true
			},
			success: _.bind(function () {
				this.redirect('/home', 'The assignments from your WorkUpload&trade; are being processed. You will receive a notification when the operation has completed. ' +
					'You can track the progress of your upload in the progress bar above. Feel free to work on other tasks while we complete your upload.');
			}, this)
		});
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

	saveUpload: function (e) {
		this.$('#map-form').ajaxSubmit({
			context: this,
			data: {
				preview: false,
				async: false
			},
			dataType: 'json',
			success: function (response) {
				if (response.successful) {
					var postFormFactory = (function () {
						var _html = '';
						var _uri = '';

						var ret = {
							init: function () {
								_html = '<input type="hidden" name="_tk" id="_tk" value="' + getCSRFToken() + '">';
								return this;
							},
							uri: function (uri) {
								_uri = uri;
								return this;
							},
							add: function (name, values) {
								if (!(values instanceof Array)) {
									values = [values];
								}
								values.forEach(function (value) {
									_html += '<input type="hidden" name="' + name + '" value="' + value + '">';
								});
								return this;
							},
							build: function () {
								return $('<form>', {
									html: _html,
									action: _uri,
									method: 'POST'
								}).appendTo(document.body);
							}
						};

						ret.init();
						return ret;
					})();

					var form = postFormFactory.uri('/assignments/bulk_send').add('ids', response.data.ids);

					if (response.data.resources) {
						form.add('resources', response.data.resources);
					}

					form.build().submit();
				} else {
					// Something's bad. Let's re-load and display the appropriate errors.
					if (response.messages) {
						_.each(response.messages, function (theMessage) {
							wmNotify({
								message: theMessage,
								type: 'danger'
							});
						});
					}
					this.handleLoadMappingResponse(response, e);
				}
			}
		});
	},

	toggleActions: function (e) {
		if ($(e.currentTarget).val() !== 'ignore') {
			this.setActionsEnabled(true);
		} else {
			this.setActionsEnabled(this.didConfigureMapping());
		}

		this.toggleMappingType(e);
	},

	didConfigureMapping: function () {
		return _.reject($('[name$="type"]'), function (item) {
			return $(item).val() === 'ignore';
		}).length > 0;
	},

	setActionsEnabled: function (isEnabled) {
		this.$('#upload-actions button.btn').prop('disabled', !isEnabled);
		this.$('#upload-actions a.btn').toggleClass('disabled', !isEnabled);
	},


	//Handler for (de)selection of specific mapping types for additional functionality.

	toggleMappingType: function (e) {
		var target = $(e.currentTarget);
		var newValue = target.val();
		var oldValue = target.data('val');

		if (newValue === 'user_number') {
			this.didSelectUserNumber(target, newValue, oldValue);
		} else if (oldValue === 'user_number') {
			this.didUnSelectUserNumber(target, newValue, oldValue);
		}

		if (newValue === 'template_number') {
			this.didSelectTemplateNumber(target, newValue, oldValue);
		} else if (oldValue === 'template_number') {
			this.didUnSelectTemplateNumber(target, newValue, oldValue);
		}

		target.data('val', newValue); // save new value
	},

	didSelectUserNumber: function (target, newValue, oldValue) {
		this.confirmModal = wmModal({
			autorun: true,
			title: 'Confirm',
			destroyOnClose: true,
			content: Template({
				message: 'Any drafts with Worker IDs mapped will be sent immediately to valid resources when you click Create and Send.'
			})
		});

		$('.cta-confirm-no').on('click', _.bind(function () {
			target.val(oldValue); // undo change, doesn't matter what new value is
			this.confirmModal.hide();
		}, this));

		$('.cta-confirm-yes').on('click', _.bind(function () {
			$('#save-upload-outlet-async').text('Create Drafts / Send');
			this.confirmModal.hide();
		}, this));

	},

	didUnSelectUserNumber: function () {
		if (!$('select[name$="\\.type"] option:selected[value="user_number"]').val()) {
			$('#save-upload-outlet-async').text('Create Drafts');
		}
	},

	didSelectTemplateNumber: function () {
		var self = this;
		if ($('select[name$="\\.type"] option:selected[value="template_number"]').length !== 1) {
			return;
		}

		$.ajax({
			context: this,
			url: '/assignments/upload/customfields',
			type: 'post',
			data: $('#map-form').serialize(),
			dataType: 'json'
		}).success(function (response) {
				if (response.successful) {
					this.allCategories = response.data.categories;
					this.allCategoryNames = _.keys(self.allCategories);
					this.allFields = response.data.types;
					// This is a bit of a hack but ensures that we have an initial mappings dataset
					// if the user selects to map "Template ID" prior to a "Preview".
					this.options.response.mappingGroup = response.data.mappingGroup;

					this.render();

					if (this.allCategoryNames.length > self.initialCategoryNames.length) {
						if (response.messages.length) {
							wmNotify({ message: response.messages[0] });
						}
					}
				} else {
					if (response.messages.length) {
						_.each(response.messages, function (theMessage) {
							wmNotify({
								message: theMessage,
								type: 'danger'
							});
						});
					}
				}
			});
	},

	didUnSelectTemplateNumber: function () {
		var self = this;
		if ($('select[name$="\\.type"] option:selected[value="template_number"]').length === 0) {
			_(self.allCategoryNames).chain().difference(self.initialCategoryNames).each(function (o) {
				$('select[name$="\\.type"] optgroup[label="' + o + '"]').remove();
			});
			self.allCategoryNames = [];
		}
	},

	//Presents modal for naming and persisting an upload mapping configuration
	saveMapping: function (e) {
		if ($(e.currentTarget).is('.disabled')) {
			e.preventDefault();
			return;
		}

		e.preventDefault();

		$.ajax({
			type: 'GET',
			url: '/assignments/upload/create_mapping',
			context: this,
			success: function (response) {
				if (!_.isEmpty(response)) {
					wmModal({
						autorun: true,
						title: $(e.currentTarget).attr('title') || $(e.currentTarget).text(),
						destroyOnClose: true,
						content: response
					});
					$('#create-mapping-form').ajaxForm({
						context: this,
						data: this.$('#map-form').serializeArray(),
						dataType: 'json',
						success: function (response) {
							if (response.successful) {
								this.options.response.mappingGroup = response.data.mappingGroup;
								this.render();

								wmNotify({ message: _.first(response.messages )});
								$('.wm-modal--close').trigger('click');
							} else {
								wmNotify({
									type: 'danger',
									message: _.first(response.messages)
								});
							}
						}
					});
				}
			}
		});
	}
});

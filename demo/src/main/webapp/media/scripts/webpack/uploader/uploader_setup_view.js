'use strict';

import $ from 'jquery';
import _ from 'underscore';
import wmModal from '../funcs/wmModal';
import Backbone from 'backbone';
import Template from './templates/uploaderButton.hbs';
import LabelsView from '../settings/labels_view';
import ManageMappingsView from './manage_mappings_view';
import wmNotify from '../funcs/wmNotify';
import qq from '../funcs/fileUploader';


export default Backbone.View.extend({
	el: '#upload-form',

	events: {
		'click .mappings-outlet'   : 'manageMappingsModal'
	},

	initialize: function (options) {
		this.options = options || {};
		this.render();
		new LabelsView();
		Backbone.Events.on('renderLabels', function (data) {
			this.renderLabels(data);
		}, this);
	},

	render: function () {
		new qq.FileUploader({
			element: document.getElementById('assignment-upload'),
			action: false,
			allowedExtensions: ['csv', 'txt'],
			sizeLimit: 10 * 1024 * 1024, // 10MB
			multiple: false,
			template: Template(),
			showMessage: function (message) {
				wmNotify({
					message: message,
					type: 'danger'
				});
			}
		});

		// initialize dropdowns
		this.renderMappings();
		this.renderTemplates();
		this.renderLabels();

		return this;
	},

	renderMappings: function () {
		$('#mapping-select').html($('#tmpl-mapping-select').tmpl({
			mappings: this.options.mappingsCollection.toJSON()
		}));
	},

	renderTemplates: function () {
		$('#template-select').html($('#tmpl-template-select').tmpl({
			templates: this.options.templatesCollection.toJSON()
		}));
	},

	renderLabels: function (selected) {
		this.options.labelsCollection.fetch().then(function (labels) {
				var $select = $('#label-select');
				if ($select.length > 0) {
					$select.html($('#tmpl-label-select').tmpl({
						labels: labels
					}));
					if (typeof selected !== 'undefined') {
						$select.find('option[value=' + selected + ']').prop('selected', true);
					}
				}
			}
		);
	},

	manageMappingsModal: function (e) {
		e.preventDefault();
		$.ajax({
			type: 'GET',
			url: $(e.target).attr('href'),
			context: this,
			success: function (response) {
				if (!_.isEmpty(response)) {
					wmModal({
						autorun: true,
						title: 'Manage Mappings',
						destroyOnClose: true,
						content: response
					});
					new ManageMappingsView(this.href);
				}
			}
		});
	}

});

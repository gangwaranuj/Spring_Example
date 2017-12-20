'use strict';

import $ from 'jquery';
import _ from 'underscore';
import Backbone from 'backbone';
import wmNotify from '../funcs/wmNotify';
import getCSRFToken from '../funcs/getCSRFToken';
import qq from '../funcs/fileUploader';
import Template from '../dashboard/templates/qq_uploader.hbs';

export default Backbone.View.extend({
	el: '#assets',
	className: 'iconed-list',

	initialize: function (options) {
		this.messages = this.$('div.message');
		this.generalAssets = this.$('.general-assets');
		this.description = this.$('input[name=attachment_description]');

		if (!this.$('.attachment-uploader').size()) return;

		this.uploader = new qq.FileUploader({
			element: this.$el.find('.attachment-uploader')[0],
			action: '/assignments/add_attachments',
			allowedExtensions: [],
			CSRFToken: getCSRFToken(),
			sizeLimit: 150 * 1024 * 1024, // 150MB
			multiple: false,
			template: Template(),
			onSubmit: _.bind(function () {
				// Don't send if the value is still the placeholder value.
				var desc = '';
				if (this.description.val() !== this.description.attr('placeholder')) {
					desc = this.description.val();
				}
				this.uploader.setParams({
					work_numbers: this.options.selectedWorkNumbers,
					description: desc
				});
			}, this),

			onComplete: _.bind(function (id, fileName, data) {
				$(this.uploader._getItemByFileId(id)).remove();
				if (data.successful) {
					wmNotify({
						message: 'Attachment has been added successfully.'
					});
					Backbone.Events.trigger('getDashboardData');
				} else {
					wmNotify({
						message: 'Upload failed. Please try again.',
						type: 'danger'
					});

				}
				this.options.modal.destroy();
			}, this)
		});

	}
});

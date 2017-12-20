'use strict';

import $ from 'jquery';
import _ from 'underscore';
import Backbone from 'backbone';
import wmNotify from '../funcs/wmNotify';
import wmModal from '../funcs/wmModal';
import getCSRFToken from '../funcs/getCSRFToken';


export default Backbone.View.extend({
	el: 'body',
	COLUMN_NAME: 0,
	COLUMN_INDUSTRY: 1,

	events: {
		'click [data-action="save_tag"]'     : 'saveTag',
		'click [data-action="cancel_merge"]' : 'cancelMerge'
	},

	initialize (options) {
		this.options = options;
	},

	openModal (e, tagId, data) {
		var self = this,
			$tag = data[this.COLUMN_NAME],
			$industryId = this.$el.find('#industryId');

		// populate modal
		this.$el.find('#tagId').val(tagId);
		_.each($industryId.children(), function() {
			if ($(this).text() === data[self.COLUMN_INDUSTRY]) {
				$(this).attr('selected', 'selected');
			} else {
				$(this).removeAttr('selected');
			}
		});

		$('#mergeMessage').addClass('dn');
		$industryId.prop('disabled', false);
		$('#tagName').prop('disabled', false);

		// bind the autocomplete
		$('#tagName').autocomplete({
			minLength: 1,
			source: function (request, response) {
				var url = '/admin/suggest/' + self.options.tagType + '?term=' + request.term + '&industryId=' + $('#industryId').val();

				$.ajax({
					url: url,
					type: 'GET',
					success: function (data) {
						var mappedData = [];

						$(data).find('list').children().each(function() {
							mappedData.push({
								label: $(this).find('value').text(),
								value: $(this).find('value').text(),
								id: $(this).find('id').text()
							});
						});

						response(mappedData);
					}
				});
			},
			select: function (event, ui) {
				if (ui.item.id !== $('#tagId').val()) {
					$('#mergeIntoTagId').val(ui.item.id);
					$('#mergeMessage').removeClass('dn');
					$('#industryId').prop('disabled', true);
					$('#tagName').prop('disabled', true);
				}
			}
		});

		this.application_modal = wmModal({
			autorun: true,
			title: 'Modify Tag',
			destroyOnClose: true,
			content: $('#modify_tag_modal').html()
		});
		$('#tagName').val($tag);
	},

	saveTag () {
		var tagName = $('#tagName').val(),
			tagId = $('#tagId').val(),
			industryId = $('#industryId').val(),
			mergeIntoTagId = $('#mergeIntoTagId').val(),
			self = this;

		if (mergeIntoTagId && mergeIntoTagId !== tagId) {
			// merge into a new tag
			$.post('/admin/tags/merge_tag/' + self.options.tagType + '/' + tagId,
				{ mergeIntoTagId: mergeIntoTagId },
				function () {
					self.options.parent.updateTable();
					this.application_modal.destroy();
				});
		} else {
			// just save any changes to this tag
			$.ajax({
				url: '/admin/tags/save_tag/' + self.options.tagType + '/' + tagId,
				type: 'post',
				headers: { 'X-CSRF-Token': getCSRFToken() },
				data: { name: tagName, industryId: industryId },
				success: function (response) {
					if (response.successful) {
						self.options.parent.updateTable();
						wmNotify({ message: 'Successfully saved ' + self.options.tagType + '.' })
					} else {
						_.each(response.messages, function (theMessage) {
							wmNotify({
								message: theMessage,
								type: 'danger'
							});
						});
					}
					self.application_modal.hide();
				}
			});
		}
	},

	cancelMerge () {
		$('#mergeMessage').addClass('dn');
		$('#industryId').prop('disabled', false);
		$('#tagName').prop('disabled', false);
		$('#mergeIntoTagId').val('');
	}
});

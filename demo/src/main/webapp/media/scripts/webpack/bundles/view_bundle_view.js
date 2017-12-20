'use strict';

import OverviewTemplate from '../assignments/templates/bundles/bundleOverview.hbs';
import BundleDataTemplate from '../assignments/templates/bundles/bundleData.hbs';
import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import UnbundleView from './unbundle_view';
import wmModal from '../funcs/wmModal';
import wmNotify from '../funcs/wmNotify';
import getCSRFToken from  '../funcs/getCSRFToken';
import ajaxSendInit from '../funcs/ajaxSendInit';
import 'jquery-form/jquery.form';
import DescriptionView from '../assignments/description_view';
import wysiwygHtmlClean from '../funcs/wysiwygHtmlClean';

export default Backbone.View.extend({
	el: '#bundle',

	overviewTemplate: OverviewTemplate,
	dataTemplate: BundleDataTemplate,
	events: {
		'click #bundle_list a.delete'    : 'unbundle',
		'click .unassign_action'         : 'showUnassign',
		'click #bundle_list a.accept'    : 'accept',
		'click .blockclient_action'      : 'showBlockClient',
		'click .toggle-icon'             : 'toggleIcon',
		'click [data-toggle="collapse"]' : 'toggleIconPlusMinus',
		'click .inline_editors'          : 'toggleInlineEditing',
		'click .inline-update'           : 'inlineUpdate'
	},

	initialize: function () {
		this.refreshBundle(this.initIconPlusMinus);
		ajaxSendInit();
		this.bundleEvent = {};
		_.extend(this.bundleEvent, Backbone.Events);
		this.listenTo(this.bundleEvent, 'refreshBundle', this.refreshBundle);
		this.descriptionView = new DescriptionView({
			workNumber: this.options.workNumber
		});
	},

	render: function () {
		$('#bundle_overview').html(this.overviewTemplate({
			overview: this.collection.overview
		}));
		$('#bundle_data').html(this.dataTemplate({
			assignments: this.collection.models,
			isEligibleToTakeAction: this.options.isEligibleToTakeAction,
			isWorkActive: this.options.isWorkActive
		}));
		this.$('[name="description"]').wysiwyg();
		wysiwygHtmlClean(`${this.el.className} [name="description"]`);
		return this;
	},

	initIconPlusMinus: function () {
		$('#bundle').find('[data-toggle="collapse"]').each(function () {
			if ($(this).parent('.accordion-heading').next().hasClass('in')) {
				$(this).find('i').removeClass('icon-plus-sign').addClass('icon-minus-sign');
			} else {
				$(this).find('i').removeClass('icon-minus-sign').addClass('icon-plus-sign');
			}
		});
	},

	toggleIconPlusMinus: function (e) {
		e.preventDefault();
		var active = $(e.target);

		if (active.find('i').hasClass('icon-minus-sign')) {
			active.find('i').removeClass('icon-minus-sign').addClass('icon-plus-sign');
		} else {
			active.find('i').removeClass('icon-plus-sign').addClass('icon-minus-sign');
		}
	},

	toggleIcon: function (e) {
		e.preventDefault();
		var active = $(e.target);

		if (active.hasClass('icon-minus-sign')) {
			active.removeClass('icon-minus-sign').addClass('icon-plus-sign');
		} else {
			active.removeClass('icon-plus-sign').addClass('icon-minus-sign');
		}
	},

	toggleInlineEditing: function (e) {
		e.preventDefault();

		var $target = $(e.currentTarget);
		var $section = $($target.data('container'));

		$section.find('.inline-contain').toggle();
		$section.find('.wysiwyg').toggle();
	},

	inlineUpdate: function (e) {
		var $target = $(e.currentTarget);
		var $newHtml = $($target.data('editor')).wysiwyg('getContent');
		var $type = $target.data('type');
		$.ajax({
			url: '/assignments/save/' + this.collection.overview.workNumber,
			type: 'POST',
			dataType: 'json',
			data: {
				type: $type,
				newHtml: $newHtml
			},
			success: _.bind(function (response) {
				if (response.successful) {
					this.toggleInlineEditing(e);
					if ($type === 'description') {
						$('.description-container').html($newHtml);
					} else {
						$('.special-container').html($newHtml);
					}
					wmNotify({ message: response.messages[0] });
				} else {
					wmNotify({
						message: response.messages[0],
						type: 'danger'
					});
				}
			}, this)
		});
	},

	accept: function (e) {
		e.preventDefault();
		var self = this;
		$.post('/assignments/accept_to_bundle/' + $(e.currentTarget).data('work'))
			.done(function (response) {
				if (!response.successful) {
					wmNotify({
						message: response.messages[0],
						type: 'danger'
					});
				} else {
					wmNotify({
						message: 'Successfully accepted assignment.'
					});
				}
				self.collection.fetch({
					success: function () {
						self.render();
					}
				});
			});
	},

	unbundle: function (e) {
		e.preventDefault();
		this.unbundleView = new UnbundleView({
			e: e,
			bundleEvent: this.bundleEvent
		});
	},

	showUnassign: function (e) {
		e.preventDefault();
		if ($(e.currentTarget).is('.disabled')) {
			return false;
		}

		$.ajax({
			type: 'GET',
			url: e.currentTarget.href,
			context: this,
			success: function (response) {
				if (!_.isEmpty(response)) {
					wmModal({
						autorun: true,
						title: 'Unassign',
						destroyOnClose: true,
						content: response
					});

					// Toggle notes field on if a reason is selected
					$('#reason').on('change', function () {
						$('#unassign_note').closest('.control-group').toggle($(this).val().length > 0);
					});
					$('#unassign_form').ajaxForm({
						context: this,
						success: function (data) {
							if (data.successful) {
								this.redirectWithFlash(data.redirect, 'success', data.messages);
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
				}
			}
		});
	},

	showBlockClient: function (e) {
		e.preventDefault();

		var form = $('#block_client_form');
		var action = form.attr('action');
		form.attr('action', action.replace(/\/user\/block_client.*$/, '/assignments/block_client/' + this.collection.overview.workNumber ));

		wmModal({
			autorun: true,
			title: 'Block Client',
			destroyOnClose: true,
			content: $('#block_client_dialog_container').html()
		});
	},

	refreshBundle: function (initIconPlusMinus) {
		this.collection.fetch({
			success: _.bind(function () {
				this.render();
				if (initIconPlusMinus) initIconPlusMinus();
			}, this)
		});
	},

	redirectWithFlash: function (url, type, msg) {
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
});

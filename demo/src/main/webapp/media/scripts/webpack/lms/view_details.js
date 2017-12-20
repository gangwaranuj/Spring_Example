'use strict';

import Template from '../funcs/templates/confirmAction.hbs';
import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import getCSRFToken from '../funcs/getCSRFToken';
import wmModal from '../funcs/wmModal';
import wmNotify from '../funcs/wmNotify';
import 'datatables.net';
import '../dependencies/jquery.tmpl';
import 'jquery-form/jquery.form';

export default Backbone.View.extend({
	el: 'body',

	events: {
		'click [data-action="deactivate"]' : 'deactivateAction',
		'click [data-action="activate"]'   : 'activateAction',
		'click .delete-action'             : 'deleteAction',
		'click [data-action="copy"]'       : 'copyAction',
		'click #block-client'              : 'blockClientDialog',
		'click #unblock-client'            : 'unblockClientAction',
		'click .share-test .linkedin'      : 'shareLinkedin',
		'click .share-test .facebook'      : 'shareFacebook',
		'click .share-test .twitter'       : 'shareTwitter'
	},

	initialize: function (options) {
		var meta;
		const cellRenderer = (template) => {
			return  (data, type, val, metaData) => {
				return $(template).tmpl({
					data,
					meta: meta[metaData.row]
				}).html();
			};
		};

		this.table = $('#attempts_list').dataTable({
			'sPaginationType': 'full_numbers',
			'bLengthChange': false,
			'bFilter': false,
			'iDisplayLength': 50,
			'aaSorting': [[2,'desc']],
			'aoColumnDefs': [
				{'mRender': cellRenderer('#attempts-cell-user-tmpl'), 'aTargets': [0]},
				{'mRender': cellRenderer('#attempts-cell-status-tmpl'), 'aTargets': [1]}
			],
			'bProcessing': true,
			'bServerSide': true,
			'sAjaxSource': '/lms/manage/attempts/' + this.options.id,
			'fnServerData': function (sSource, aoData, fnCallback) {
				$.each($('#attempts_filter_form').serializeArray(), function(i, item) {
					aoData.push(item);
				});

				$.getJSON(sSource, aoData, function (json) {
					meta = json.aMeta;
					for (var i = 0, size = json.aaData.length; i < size; i++) {
						if (json.aaData[i][3]) {
							var workNumber = json.aaData[i][3];
							json.aaData[i][3] = '<a href="/assignments/details/' + workNumber + '">' + json.aaData[i][4] + '</a><br/><small class="meta">' + workNumber + '</small>';
						}
					}
					fnCallback(json);
				});
			}
		});

		// Add client id to block-client form
		if (options.userCompanyId !== options.clientIdToBlock) {
			// Get form action url
			var action = $('#block_client_form').attr('action');

			// Replace id in form with passed id
			$('#block_client_form').attr('action', action.replace(/\/block_client.*$/, '/block_client/' + options.clientIdToBlock));
		}

		this.render();
	},

	render: function() {
		if (this.options.companyBlocked) {
			this.showDisabledButtonTooltip();
		}
	},

	deactivateAction: function (event) {
		wmModal({
			autorun: true,
			title: 'Confirm',
			destroyOnClose: true,
			content: Template({
				message: 'Are you sure you want to deactivate this assessment?'
			})
		});

		$('.cta-confirm-yes').on('click', _.bind(function () {
			this.nonAjaxFormSubmit('/lms/manage/' + $(event.target).attr('data-id') + '/deactivate_assessment');
		}, this));
	},

	activateAction: function (event) {
		this.nonAjaxFormSubmit('/lms/manage/' + $(event.target).attr('data-id') + '/activate_assessment');
	},

	deleteAction: function (event) {
		event.preventDefault();
		if (confirm('Are you sure you want to delete this assessment?')) {
			this.nonAjaxFormSubmit('/lms/manage/' + $(event.target).attr('data-id') + '/delete_assessment');
		}
	},

	copyAction: function (event) {
		this.nonAjaxFormSubmit('/lms/manage/' + $(event.target).attr('data-id') + '/copy_assessment');
	},

	// Block-client popup
	blockClientDialog: function () {
		let modal = wmModal({
			autorun: true,
			title: 'Block Client',
			destroyOnClose: true,
			content: $('#block_client_dialog_container').html()
		});

		var self = this;
		$('#block_client_form').ajaxForm({
			dataType: 'json',
			success: function (response) {
				modal.destroy();

				if (response.data && response.data.status === 'OK') {
					wmNotify({ message: response.messages[0] });
					$('#block-client').addClass('dn');
					$('#unblock-client').removeClass('dn');

					self.showDisabledButtonTooltip();
				} else {
					wmNotify({
						type: 'danger',
						message: response.messages[0]
					});
				}
			}
		});
	},

	nonAjaxFormSubmit: function (action, attrMap) {
		var form = $('<form>').attr({
			'method': 'POST', // overwritten below
			'action': action
		});
		if (typeof attrMap !== 'undefined') {
			$.each(attrMap, function( key, value ) {
				form.append($('<input>').attr({
					'name': key,
					'value': value
				}));
			});
		}
		form.append($('<input>').attr({
			'name': '_tk',
			'value': getCSRFToken()
		}));
		$('body').append(form);
		form.submit();
	},

	// Unblock client
	unblockClientAction: function() {
		var self = this;

		$.post('/user/unblock_client/' + this.options.clientIdToBlock)
			.success(function (response) {
				if (response.data && response.data.status === 'OK') {
					wmNotify({ message: response.messages[0] });

					$('#block-client').removeClass('dn');
					$('#unblock-client').addClass('dn');

					$('.test_action_button')
						.removeAttr('disabled')
						.attr('href', '/lms/view/take/' + self.options.id)
						.unwrap();
				} else {
					wmNotify({
						type: 'danger',
						message: response.messages[0]
					});
				}
			});
	},

	// Display a tooltip on test-action button (used when company is blocked)
	showDisabledButtonTooltip: function () {
		$('#block-client').addClass('dn');
		$('#unblock-client').removeClass('dn');

		$('.test_action_button')
			.attr('disabled', 'true')
			.removeAttr('href')
			.wrap('<span id="blocked-client-tooltip" class="tooltipped tooltipped-n" aria-label="' + this.options.blockedClientTooltip + '">');
	},

	openNewWindow: function (url) {
		var newWindow = window.open(url, '_blank', 'toolbar=no,menubar=0,status=0,copyhistory=0,scrollbars=yes,resizable=1,location=0,Width=550,Height=525');
		newWindow.location = url;
	},

	shareLinkedin: function () {
		this.openNewWindow('http://www.linkedin.com/shareArticle?url=http%3A%2F%2Fworkmarket.com%2F&title=Work%20Market:%20Build%20your%20freelance%20profile&summary=Take%20this%20test%20on%20Work%20Market%20to%20be%20eligible%20for%20contract%20work:%20' + this.options.name + '&mini=true&source=Work%20Market');
	},

	shareTwitter: function () {
		this.openNewWindow('https://twitter.com/intent/tweet?text=Take a test and find freelance work on @workmarket &amp;url=http%3A%2F%2Fworkmarket.com%2Flms%2Fview%2Fdetails/' + this.options.id);
	},

	shareFacebook: function () {
		this.openNewWindow('https://www.facebook.com/sharer/sharer.php?s=100&p[url]=http%3A%2F%2Fworkmarket.com%2Flms%2Fview%2Fdetails/' + this.options.id + '&p[title]=Work Market: Build your freelance profile&p[summary]=Take this test on Work Market to be eligible for contract work: ' + this.options.name + '&p[images][0]=http://workmarket.com/media/images/app/wm-square-round-new.png');
	}
});

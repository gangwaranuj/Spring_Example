'use strict';

import _ from 'underscore';
import $ from 'jquery';
import Backbone from 'backbone';
import wmNotify from '../../../funcs/wmNotify';
import wmModal from '../../../funcs/wmModal';
import ajaxSendInit from '../../../funcs/ajaxSendInit';
import getCSRFToken from '../../../funcs/getCSRFToken';
import 'jquery-form/jquery.form';
import overridePaymentTermsTemplate from '../../templates/overridePaytermsModal.hbs';


export default Backbone.View.extend({
	el: '.content',

	events: {
		'click .add-btn'                  : 'addAccountServiceTypeConfiguration',
		'click .remove'                   : 'removeAccountServiceTypeConfiguration',
		'click #edit_ap_limit'            : 'editApLimit',
		'click .override_payterms_action' : 'overridePaymentTermsAction'
	},

	initialize: function (options) {
		this.options = options;
		this.index = $('#submit_transactional_service_type_form .serviceConfig').size();
		ajaxSendInit();
		$('#edit_ap_limit_form').ajaxForm({
			dataType:'json',
			success:function (responseText) {
				if (responseText.successful) {
					location.reload();
				} else {
					_.each(responseText.errors, function (theMessage) {
						wmNotify({
							message: theMessage,
							type: 'danger'
						});
					});
				}
			}
		});

		this.render();
	},

	render: function () {
		return this;
	},

	removeAccountServiceTypeConfiguration: function (e) {
		$(e.currentTarget).parent().remove();
	},

	addAccountServiceTypeConfiguration: function () {
		$('label.remove :last').show();

		$('#submit_transactional_service_type_form .form-horizontal').append(
			$.tmpl($('#add_account_service_type_configuration').html(), {
					idx : this.index
				}));
		++this.index;

		$('label.remove :last').hide();

	},

	editApLimit: function () {
		let editApLimitModal = wmModal({
			autorun: true,
			destroyOnClose: true,
			content: $('#edit_ap_limit_popup').html(),
			title: 'Modify AP Limit',
			customHandlers: [
				{ event: 'click', selector: '#edit_ap_limit_form .button', callback: (e) => {
					e.preventDefault();
					const companyId = window.location.pathname.split('/')[5];
					const apLimit = $('#ap_limit').val() * 1;
					const data = {
						_tk: getCSRFToken(),
						id: companyId,
						ap_limit: apLimit
					};

					$.ajax({
						url: '/admin/manage/company/update_ap_limit',
						type: 'POST',
						data
					})
					.done(function() {
						editApLimitModal.destroy();
						// reload the page to reflect updated info
						window.location.reload();
					});
				}}
			]
		});
	},

	overridePaymentTermsAction: function (event) {
		event.preventDefault();
		wmModal({
			autorun: true,
			title: 'Override Bank Setup',
			destroyOnClose: true,
			content: overridePaymentTermsTemplate({
				companyId: window.location.pathname.split('/')[5]
			})
		});

		$('#override_payterms_form').ajaxForm({
			success: function (response) {
				if (response.successful) {
					window.location = response.redirect;
				} else {
					_.each(response.messages, function (theMessage) {
						wmNotify({
							message: theMessage,
							type: 'danger'
						});
					});
				}
			}
		});
	}
});

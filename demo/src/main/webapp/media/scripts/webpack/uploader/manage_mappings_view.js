'use strict';

import $ from 'jquery';
import _ from 'underscore';
import Backbone from 'backbone';
import wmNotify from '../funcs/wmNotify';
import wmModal from '../funcs/wmModal';
import Template from '../funcs/templates/confirmAction.hbs';
import '../dependencies/jquery.tmpl';
import 'datatables.net';
import 'jquery-form/jquery.form';

export default Backbone.View.extend({
	el: 'body',

	events: {
		'click #mappings-table .rename' : 'renameMapping',
		'click #mappings-table .delete' : 'deleteMapping'
	},

	initialize: function () {
		let meta;
		const cellRenderer = (template) => {
			return  (data, type, val, metaData) => {
				return $(template).tmpl({
					data,
					meta: meta[metaData.row]
				}).html();
			};
		};

		this.table = $('#mappings-table').dataTable({
			'sPaginationType': 'full_numbers',
			'bLengthChange': true,
			'bFilter': false,
			'bSort': false,
			'iDisplayLength': 10,
			'bProcessing': true,
			'bServerSide': true,
			'sAjaxSource': '/assignments/upload/mappings.json',
			'aoColumnDefs': [
				{'mRender': cellRenderer('#cell-actions-tmpl'), 'aTargets': [1]}
			],
			'fnServerData': function (sSource, aoData, fnCallback) {
				$.getJSON( sSource, aoData, function (json) {
					meta = json.aMeta;
					fnCallback(json);
				});
			}
		});
	},

	render: function () {
		return this;
	},

	renameMapping: function (e) {
		e.preventDefault();

		$.ajax({
			type: 'GET',
			url: e.currentTarget.href,
			context: this,
			success: function (response) {
				if (!_.isEmpty(response)) {
					$('.wm-modal--close').trigger('click');
					const modal = wmModal({
						autorun: true,
						title: 'Rename Mapping',
						destroyOnClose: true,
						content: response
					});

					$('#rename-mapping-form').ajaxForm({
						dataType: 'json',
						success: function (response) {
							if (response.successful) {
								wmNotify({ message: _.first(response.messages) });
								modal.destroy();
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
	},

	deleteMapping: function (e) {
		$('.wm-modal--close').trigger('click');

		this.confirmModal = wmModal({
			autorun: true,
			title: 'Confirm',
			destroyOnClose: true,
			content: Template({
				message: 'Are you sure you want to delete this mapping?'
			})
		});

		$('.cta-confirm-yes').on('click', _.bind(function () {
			$.ajax(e.currentTarget.href, {
				type: 'post',
				dataType: 'json',
				success: _.bind(function (response) {
					if (response.successful) {
						wmNotify({
							message: _.first(response.messages)
						});
						this.confirmModal.destroy();
					} else {
						wmNotify({
							message: _.first(response.messages),
							type: 'danger'
						});
					}
				}, this)
			});
		}, this));

		return false;
	},

	showManageMappings: function (e, options) {
		var self = this;
		showModal(e, $.extend({
			title: 'Manage Mappings',
			href: this.options.url,
			innerHeight: 400,
			innerWidth: 500,
			transition : 'elastic',
			onComplete: function () {
				self.initialize();
			}
		}, options));
	}

});


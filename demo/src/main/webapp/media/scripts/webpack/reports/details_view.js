'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import wmModal from '../funcs/wmModal';

export default Backbone.View.extend({
	el: 'body',

	events: {
		'click .cta-delete-report' : 'deleteReport',
		'click .cta-export-report' : 'exportReport'
	},

	exportReport: function (event) {
		var $exportLink = $(event.currentTarget),
			$id = $exportLink.data('id');

		$.ajax({
			url:'/reports/custom/export_saved_to_csv?report_id=' + $id,
			success: function (data) {
				wmModal({
					autorun: true,
					title: 'Report Sent',
					destroyOnClose: true,
					content: data,
					controls: [
						{
							text: 'Close',
							close: true,
							classList: ''
						}
					]
				});
			}
		});
	},

	deleteReport: function (event) {
		event.preventDefault();

		let $deleteLink = $(event.currentTarget),
			$id = $deleteLink.data('id');
		wmModal({
			autorun: true,
			title: 'Delete Report',
			destroyOnClose: true,
			content: '<p>Are you sure you want to delete this report?</p>',
			controls: [
				{
					text: 'Cancel',
					close: true,
					classList: ''
				},
				{
					text: 'Delete',
					primary: true,
					classList: '.delete-cta'
				}
			]
		});

		$('.wm-modal .-active').find('.-primary').on('click', () => window.location = '/reports/custom/delete_report/' + $id);
	}

});


'use strict';

import $ from 'jquery';
import _ from 'underscore';
import Backbone from 'backbone';
import ModifyView from './tags_modify_view';
import wmModal from '../funcs/wmModal';
import 'datatables.net';
import '../dependencies/jquery.tmpl';
import confirmActionTemplate from '../funcs/templates/confirmAction.hbs';
import getCSRFToken from '../funcs/getCSRFToken';

export default Backbone.View.extend({
	el: '#tags_list',

	events: {
		'click [data-action="approve_tag"]' : 'approveTag',
		'click [data-action="modify_tag"]'  : 'modifyTag',
		'click [data-action="remove_tag"]'  : 'removeTag'
	},

	initialize (options) {
		this.tagsTable = this.buildDataTable();
		this.options = options;

		options.parent = this;
	},

	buildDataTable () {

		return $('#tags_list').dataTable({
			'sPaginationType': 'full_numbers',
			'bLengthChange': true,
			'iDisplayLength': 50,
			'aaSorting': [[3,'desc']],
			'bFilter': true,
			'aoColumns': [null, null, null, null, {'bSortable': false}, {'bSortable': false}, {'bSortable': false}],
			'bProcessing': true,
			'bServerSide': true,
			'sAjaxSource': '/admin/tags/list_tags/' + this.options.tagType,
			'fnServerData': (sSource, aoData, fnCallback) => {
				aoData.push();
				$.getJSON(sSource, aoData, json => {
					for (var i = 0, size = json.aaData.length; i < size; i++) {
						json.aaData[i][4] = (json.aMeta[i].approved) ? '' : '<a data-action="approve_tag" data-id="' + json.aMeta[i].id + '">Approve</a>';
						json.aaData[i][5] = '<a data-action="modify_tag" data-id="' + json.aMeta[i].id + '">Modify</a>';
						json.aaData[i][6] = '<a data-action="remove_tag" data-id="' + json.aMeta[i].id + '">Remove</a>';
					}
					fnCallback(json);
				});
			}
		});
	},

	approveTag (e) {
		var tagId = $(e.target).data('id');

		$.ajax({
			url: '/admin/tags/approve_tag/' + this.options.tagType + '/' + tagId,
			type: 'post',
			headers: { 'X-CSRF-Token': getCSRFToken() },
			success: () => { this.updateTable(); }
		});
	},

	modifyTag (event) {
		var link = $(event.target),
			row = link.closest('tr')[0],
			data = this.tagsTable.fnGetData(row);

		if (this.modifyModal === undefined) {
			this.modifyModal = new ModifyView(this.options);
		}
		this.modifyModal.openModal(event, link.data('id'), data);
	},

	removeTag (event) {

		this.confirmModal = wmModal({
			autorun: true,
			title: 'Confirm',
			destroyOnClose: true,
			content: confirmActionTemplate({
				message: 'Are you sure you want to delete this tag?'
			})
		});

		$('.cta-confirm-yes').on('click', _.bind(function () {
			$.post('/admin/tags/decline_tag/' + this.options.tagType + '/' + $(event.target).data('id'), _.bind(function () {
				this.updateTable();
				this.confirmModal.hide();
			}, this));

			$.ajax({
				url: '/admin/tags/decline_tag/' + this.options.tagType + '/' + $(event.target).data('id'),
				type: 'post',
				headers: { 'X-CSRF-Token': getCSRFToken() },
				success: _.bind(function () {
					this.updateTable();
					this.confirmModal.hide();
				}, this)
			});

		}, this));
	},

	updateTable () {
		this.tagsTable.fnDraw(false);
	}
});

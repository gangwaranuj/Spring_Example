'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import getCSRFToken from '../funcs/getCSRFToken';
import 'datatables.net';

export default Backbone.View.extend({
	el: 'body',

	events: {
		'click [data-action="delete"]'     : 'deleteAction',
		'click [data-action="activate"]'   : 'activateAction',
		'click [data-action="deactivate"]' : 'deactivateAction',
		'click [data-action="copy"]'       : 'copyAction'
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

		if (options.type === 'survey') {
			$('#surveys_list').dataTable({
				'sPaginationType': 'full_numbers',
				'aoColumnDefs': [
					{'bSortable': false, 'aTargets': [3,4,5,6]},
					{'mRender': cellRenderer('#cell-name-tmpl'), 'aTargets': [0]},
					{'mRender': cellRenderer('#cell-user-tmpl'), 'aTargets': [1]},
					{'sClass': 'actions', 'mRender': cellRenderer('#cell-activate-tmpl'), 'aTargets': [3]},
					{'sClass': 'actions', 'mRender': cellRenderer('#cell-edit-tmpl'), 'aTargets': [4]},
					{'sClass': 'actions', 'mRender': cellRenderer('#cell-copy-tmpl'), 'aTargets': [5]},
					{'sClass': 'actions', 'mRender': cellRenderer('#cell-delete-tmpl'), 'aTargets': [6]}
				],
				'bProcessing': true,
				'bRetrieve': true,
				'bServerSide': true,
				'bLengthChange': false,
				'bFilter': false,
				'iDisplayLength': 50,
				'sAjaxSource': '/lms/manage/surveys.json',
				'fnServerData': function (sSource, aoData, fnCallback) {
					$.getJSON( sSource, aoData, function (json) {
						if (json.aaData.length === 0) {
							$('#survey_hero').show();
						} else {
							$('#survey_container').show();
						}
						meta = json.aMeta;
						fnCallback(json);
					});
				}
			});
		} else {
			$('#assessments_list').dataTable({
				'sPaginationType': 'full_numbers',
				'aoColumnDefs': [
					{'bSortable': false, 'aTargets': [3,4,5,6]},
					{'mRender': cellRenderer('#cell-name-tmpl'), 'aTargets': [0]},
					{'mRender': cellRenderer('#cell-user-tmpl'), 'aTargets': [1]},
					{'sClass': 'actions', 'mRender': cellRenderer('#cell-activate-tmpl'), 'aTargets': [3]},
					{'sClass': 'actions', 'mRender': cellRenderer('#cell-edit-tmpl'), 'aTargets': [4]},
					{'sClass': 'actions', 'mRender': cellRenderer('#cell-copy-tmpl'), 'aTargets': [5]},
					{'sClass': 'actions', 'mRender': cellRenderer('#cell-delete-tmpl'), 'aTargets': [6]}
				],
				'bLengthChange': false,
				'bFilter': false,
				'iDisplayLength': 50,
				'bRetrieve': true,
				'bProcessing': true,
				'bServerSide': true,
				'sAjaxSource': '/lms/manage/index.json',
				'fnServerData': function (sSource, aoData, fnCallback) {
					$.getJSON(sSource, aoData, function (json) {
						if (json.aaData.length === 0) {
							$('#tests_hero').show();
						}
						else {
							$('#tests_container').show();
						}
						meta = json.aMeta;
						fnCallback(json);
					});
				}
			});
		}
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

	deleteAction: function (event) {
		event.preventDefault();
		if (confirm('Are you sure you want to delete this assessment?')) {
			this.nonAjaxFormSubmit('/lms/manage/' + $(event.target).attr('data-id') + '/delete_assessment');
		}
	},

	activateAction: function (event) {
		event.preventDefault();
		this.nonAjaxFormSubmit('/lms/manage/' + $(event.target).attr('data-id') + '/activate_assessment');
	},

	deactivateAction: function (event) {
		event.preventDefault();
		this.nonAjaxFormSubmit('/lms/manage/' + $(event.target).attr('data-id') + '/deactivate_assessment');
	},

	copyAction: function (event) {
		event.preventDefault();
		this.nonAjaxFormSubmit('/lms/manage/' + $(event.target).attr('data-id') + '/copy_assessment');
	}

});

'use strict';

import $ from 'jquery';
import 'datatables.net';
import getCSRFToken from '../funcs/getCSRFToken';
import ListTitleTemplate from './templates/list_title.hbs';

export default () => {
	let datatable_obj,
	list_filters = [],
	meta;

	function applyFilters() {
		list_filters = $('#filter_form').serializeArray();
		datatable_obj.fnDraw();
		return true;
	}

	function nonAjaxFormSubmit(action, attrMap) {
		let form = $('<form>').attr({
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
	}

	const $list = $('#campaigns_list');
	datatable_obj = $list.dataTable({
		'sPaginationType': 'full_numbers',
		'bLengthChange': false,
		'iDisplayLength': 50,
		'bFilter': false,
		'bProcessing': true,
		'bServerSide': true,
		'aaSorting': [
			[1, 'desc'],
			[0, 'asc']
		],
		'sDom': '<"custom-table-header-wrapper"l<"custom-table-header-outlet">>frtip',
		'sAjaxSource': '/campaigns/list?rand=' + new Date().getTime(),
		'aoColumnDefs': [
			{
				'mRender': (data, type, val, metaData) => {
					return ListTitleTemplate({
						data: data,
						meta: meta[metaData.row]
					})
				},
				'aTargets': [0]
			}
		],
		'fnServerData': function(sSource, aoData, fnCallback) {
			// Apply filters.
			for (var i = 0, size = list_filters.length; i < size; i++) {
				aoData.push(list_filters[i]);
			}
			$.getJSON(sSource, aoData, function (json) {
				meta = json.aMeta;
				fnCallback(json);

				if (json.aFilters) {
					$('#filter_form select[name="filters[status]"]').val(json.aFilters.status);
				}
			});
		}
	});

	$('#filter_form select').on('change', applyFilters);

	$list.on('click', ' a[data-action="delete"]', function (e) {
		e.preventDefault();
		if (confirm('Are you sure you want to delete this landing page?')) {
			nonAjaxFormSubmit('/campaigns/' + $(e.target).attr('data-id') + '/delete');
		}
	});

	$list.on('click', 'a[data-action="toggle"]', function (e) {
		e.preventDefault();
		nonAjaxFormSubmit('/campaigns/' + $(e.target).attr('data-id') + '/activate');
	});
};

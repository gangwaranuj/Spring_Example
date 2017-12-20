'use strict';

import $ from 'jquery';
import 'datatables.net';

export default (options) => {
	let meta,
		metaData;

	$('#recruits_list').dataTable({
		'pagingType': 'full_numbers',
		'lengthChange': false,
		'pageLength': 50,
		'searching': false,
		'processing': true,
		'serverSide': true,
		'columnDefs': [
			{
				'render': (data, type, val, index) => {
					if (metaData[index.row].lane2_status === 'pending' && metaData[index.row].confirmed) {
						return '<input name="recruits[]" value="' + metaData[index.row].user_number + '" type="checkbox" \/>';
					}
					return '';
				},
				'targets': [0]
			},
			{
				'render': (data, type, val, index) => {
					return '<a href="/profile/' + metaData[index.row].user_number + '">' + data + '<\/a>';
				},
				'targets': [1]
			},
			{ 'orderable': false, 'targets': [0,2,4,5] }
		],
		'sAjaxSource': '/campaigns/' + options.campaignId + '/recruits',
		'fnServerData': function (sSource, aoData, fnCallback) {
			aoData.push( {
				'name': 'id',
				'value': options.campaignId
			});
			$.getJSON(sSource, aoData, function (json) {
				metaData = json.aMeta;
				if (json.aaData === undefined || json.aaData.length === 0) {
					$('#table_recruits').hide();
					$('.table_recruits_msg').html('<p class="alert alert-warning">You currently have no recruits.<\/p>');
				}
				fnCallback(json);
			});
		}
	});

	$('#submit-recruit_actions').on('click', function () {
		$(this).closest('form').trigger('submit');
	});
};


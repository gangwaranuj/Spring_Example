var wm = wm || {};
wm.pages = wm.pages || {};
wm.pages.admin = wm.pages.admin || {};
wm.pages.admin.kpis = wm.pages.admin.kpis || {};

wm.pages.admin.kpis.resource = function (fundingType, industry) {

	function dataTableInit(tableName, sAjaxSource) {
		var meta;
		var datatable_obj = $('#' + tableName ).dataTable({
			'sPaginationType':'full_numbers',
			'bLengthChange':true,
			'bFilter':false,
			'bStateSave':false,
			'bProcessing':true,
			'iDisplayLength':25,
			'aoColumnDefs':[],
			'aaSorting': [[ 0, "desc" ]],
			'sAjaxSource':sAjaxSource,
			'fnServerData':function (sSource, aoData, fnCallback) {
				$.getJSON(sSource, aoData, function (json) {
					meta = json.aMeta;
					fnCallback(json)
				});
			}
		});
	}

	return function () {
		$('select#fundingType').val(fundingType);
		if (industry == 'true') {
			$('#industry').attr('checked','checked');
		}

		dataTableInit('monthlyResourceReceivingAssignment', '/admin/kpis/resource/monthlyResourceReceivingAssignment');
		dataTableInit('weeklyResourceReceivingAssignment', '/admin/kpis/resource/weeklyResourceReceivingAssignment');
		dataTableInit('weeklyResourceReceivingAssignmentTTM', '/admin/kpis/resource/weeklyResourceReceivingAssignmentTTM');

		$('.dataTables_length').hide();
		$('.dataTables_info').hide();
		$('.dataTables_paginate').hide();
	}
}
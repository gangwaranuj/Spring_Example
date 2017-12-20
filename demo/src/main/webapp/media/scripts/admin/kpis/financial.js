var wm = wm || {};
wm.pages = wm.pages || {};
wm.pages.admin = wm.pages.admin || {};
wm.pages.admin.kpis = wm.pages.admin.kpis || {};

wm.pages.admin.kpis.financial = function (fundingType) {
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

		dataTableInit('monthlyThroughput', '/admin/kpis/financial/monthlyThroughput');
		dataTableInit('weeklyThroughput', '/admin/kpis/financial/weeklyThroughput');
		dataTableInit('dailyThroughput', '/admin/kpis/financial/dailyThroughput');
		dataTableInit('monthlyTransactionFees', '/admin/kpis/financial/monthlyTransactionFees');
		dataTableInit('weeklyTransactionFees', '/admin/kpis/financial/weeklyTransactionFees');
		dataTableInit('dailyTransactionFees', '/admin/kpis/financial/dailyTransactionFees');
		dataTableInit('monthlyAssignmentsCreated', '/admin/kpis/financial/monthlyAssignmentsCreated');
		dataTableInit('weeklyAssignmentsCreated', '/admin/kpis/financial/weeklyAssignmentsCreated');
		dataTableInit('dailyAssignmentsCreated', '/admin/kpis/financial/dailyAssignmentsCreated');
		dataTableInit('monthlyAvgAssignmentsValue', '/admin/kpis/financial/monthlyAvgAssignmentsValue');
		dataTableInit('weeklyAvgAssignmentsValue', '/admin/kpis/financial/weeklyAvgAssignmentsValue');
		dataTableInit('dailyAvgAssignmentsValue', '/admin/kpis/financial/dailyAvgAssignmentsValue');
		dataTableInit('monthlyWithdrawableCash', '/admin/kpis/financial/monthlyWithdrawableCash');
		dataTableInit('monthlyTotalCash', '/admin/kpis/financial/monthlyTotalCash');

		$('.dataTables_length').hide();
		$('.dataTables_info').hide();
		$('.dataTables_paginate').hide();
	}
}
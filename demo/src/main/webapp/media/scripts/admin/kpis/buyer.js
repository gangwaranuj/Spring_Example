var wm = wm || {};
wm.pages = wm.pages || {};
wm.pages.admin = wm.pages.admin || {};
wm.pages.admin.kpis = wm.pages.admin.kpis || {};

wm.pages.admin.kpis.buyer = function (fundingType, industry) {
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

		dataTableInit('monthlyNumOfNewBuyers', '/admin/kpis/buyer/monthlyNumOfNewBuyers');
		dataTableInit('weeklyNumOfNewBuyers', '/admin/kpis/buyer/weeklyNumOfNewBuyers');
		dataTableInit('monthlyAvgAssignmentsSentByNewBuyers', '/admin/kpis/buyer/monthlyAvgAssignmentsSentByNewBuyers');
		dataTableInit('weeklyAvgAssignmentsSentByNewBuyers', '/admin/kpis/buyer/weeklyAvgAssignmentsSentByNewBuyers');
		dataTableInit('monthlyPercentageNewBuyersSubscription', '/admin/kpis/buyer/monthlyPercentageNewBuyersSubscription');
		dataTableInit('monthlyPercentageNewBuyersTransactional', '/admin/kpis/buyer/monthlyPercentageNewBuyersTransactional');
		dataTableInit('weeklyNumOfNewBuyersSendingAssignmentsTTM', '/admin/kpis/buyer/weeklyNumOfNewBuyersSendingAssignmentsTTM');

		$('.dataTables_length').hide();
		$('.dataTables_info').hide();
		$('.dataTables_paginate').hide();
	}
}
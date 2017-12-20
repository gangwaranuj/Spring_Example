'use strict';

import $ from 'jquery';
import 'datatables.net';
import 'jquery-ui';
import 'jquery-form/jquery.form';
import LedgerTemplate from './templates/ledger-description-cell.hbs';
import CustomHeaderTemplate from './templates/custom-header.hbs';

export default () => {
	var datatableObj;
	var meta;
	var listFilters = [];
	var $body = $('body');

	function renderDescriptionCell(data, type, val, metaData) {
		return LedgerTemplate({
			data: data,
			meta: meta[metaData.row],
			isDeposit: meta[metaData.row].type === 'deposit',
			depositPaymentTypeCredit: meta[metaData.row].deposit_payment_type === 'credit',
			depositPaymentTypeBank: meta[metaData.row].deposit_payment_type === 'bank',
			depositPaymentTypeWire: meta[metaData.row].deposit_payment_type === 'wire',
			depositPaymentTypeCheck: meta[metaData.row].deposit_payment_type === 'check'
		});
	}

	var createLedgerDatatable = function (tableId) {
		return $('#' + tableId).dataTable({
			'bPaginate' : (tableId === 'activity_list'),
			'sPaginationType': 'full_numbers',
			'bLengthChange': true,
			'bFilter': false,
			'bSort': false,
			'iDisplayLength': 50,
			'aoColumnDefs': [
				{'sClass': 'nowrap', 'aTargets': [0,1]},
				{'sClass': 'tar', 'aTargets': (tableId === 'activity_list') ? [3,4,5] : [3,4]},
				{'mRender': renderDescriptionCell, 'aTargets': [2]}
			],
			'bProcessing': true,
			'bServerSide': true,
			'sDom': '<"custom-table-header-wrapper"l<"custom-table-header-outlet">>frtip',
			'sAjaxSource': (tableId === 'activity_list') ? '/payments/ledger.json' : '/payments/ledger_pending',
			'fnServerData': function (sSource, aoData, fnCallback) {
				$('.custom-table-header-outlet').html(CustomHeaderTemplate);
				// Apply filters.
				if (tableId === 'activity_list'){
					listFilters = $('#filters').serializeArray();
					for (var i = 0, size = listFilters.length; i < size; i++) {
						aoData.push(listFilters[i]);
					}
				}
				$.getJSON(sSource, aoData, function (json) {
					meta = json.aMeta;
					if (tableId === 'pending_activity_list' && meta.length === 0){
						$('#pending_activity').hide();
					}
					fnCallback(json);
				});
			}
		});
	};

	datatableObj = createLedgerDatatable('activity_list');
	createLedgerDatatable('pending_activity_list');

	$body.delegate('#export-filtered-outlet', 'click', function (e) {
		var filters = $('#filters').formSerialize();
		var href = $(e.target).attr('href') + '?' + filters;
		$(e.target).attr('href', href);

		return true;
	});

	$body.delegate('#filters', 'submit', function (e) {
		e.preventDefault();
		datatableObj.fnDraw();
	});

	$body.delegate('#filters [type=reset]', 'click', function () {
		$(this).closest('form').trigger('reset');
		listFilters = [];
		datatableObj.fnDraw();
	});

	$('#start_date').datepicker({
		dateFormat: 'mm/dd/yy',
		onSelect: function(dateText, inst) {
			$('#end_date').datepicker('option', 'minDate', new Date(inst.selectedYear, inst.selectedMonth, inst.selectedDay));
		}
	});

	$('#end_date').datepicker({
		dateFormat: 'mm/dd/yy',
		onSelect: function(dateText, inst) {
			$('#start_date').datepicker('option', 'maxDate', new Date(inst.selectedYear, inst.selectedMonth, inst.selectedDay));
		}
	});
};

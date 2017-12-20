'use strict';

import _ from 'underscore';
import $ from 'jquery';
import Backbone from 'backbone';
import 'datatables.net';
import '../../dependencies/jquery.tmpl';

export default Backbone.View.extend({
	el: '#wm_invoices_container',

	events: {
		'change [name="invoice_status"]' : function(e) {
			this.clearOtherFilters(e);
			this.filterInvoiceStatusType(e);
		},
		'keyup #company_search' : 'filterCompany',
		'keyup #invoice_search' : 'filterInvoice'
	},

	initialize () {
		this.invoicesTable = this.buildDataTable();
	},

	cellRenderer (data, type, val, metaData) {
		let template;
		switch (metaData.col) {
			case 0:
				template = '#invoice-type-tmpl';
				break;
			case 2:
				template = '#invoice-id-tmpl';
				break;
			case 3:
				template = '#invoice-company-name-tmpl';
				break;
			case 7:
				template = '#invoice-amount-tmpl';
				break;
			case 11:
				template = '#invoice-action-tmpl';
				break;
		}

		return $(template).tmpl({
			data: data,
			meta: this.meta[metaData.row]
		}).html();
	},

	buildDataTable () {
		return this.$('#wm_invoices_table').dataTable({
				'sPaginationType': 'full_numbers',
				'bLengthChange': false,
				'bFilter': false,
				'bStateSave': false,
				'bProcessing': true,
				'bServerSide': true,
				'iDisplayLength': 100,
				'aoColumnDefs':[
					{'aTargets': [0], 'mRender': _.bind(this.cellRenderer, this)},
					{'aTargets': [1], 'sWidth': '250px'},
					{'aTargets': [2], 'mRender': _.bind(this.cellRenderer, this), 'sWidth': '100px'},
					{'aTargets': [3], 'mRender': _.bind(this.cellRenderer, this), 'sWidth': '200px'},
					{'aTargets': [4], 'bSortable': true},
					{'aTargets': [5], 'bSortable': true},
					{'aTargets': [6], 'bSortable': true},
					{'aTargets': [7], 'mRender': _.bind(this.cellRenderer, this)},
					{'aTargets': [8], 'bSortable': false, 'sWidth': '60px'},
					{'aTargets': [9], 'sWidth': '55px'},
					{'aTargets': [10], 'bSortable': false},
					{'aTargets': [11], 'mRender': _.bind(this.cellRenderer, this), 'sWidth': '50px'}
				],
				'bSort':true,
				'sAjaxSource':'/admin/accounting/outstanding_invoices',
				'fnServerParams' (aoData) {
					// Add invoice status filter
					aoData.push({
						'name'  : 'sStatus',
						'value' : $('[name="invoice_status"]').val()
					});

					// Add company filter
					aoData.push({
						'name'  : 'sCompany',
						'value' : $('#company_search').val()
					});

					// Add company filter
					aoData.push({
						'name'  : 'sInvoice',
						'value' : $('#invoice_search').val()
					});
				},
				'fnServerData': (sSource, aoData, fnCallback) => {
					$.getJSON(sSource, aoData, (json) => {
						this.meta = json.aMeta;
						fnCallback(json);
					});
				}
			}
		);
	},

	filterInvoiceStatusType () {
		this.invoicesTable.fnDraw();
	},

	filterCompany () {
		this.invoicesTable.fnDraw();
	},

	filterInvoice () {
		this.invoicesTable.fnDraw();
	},

	clearOtherFilters () {
		$('#company_search').val('');
		$('#invoice_search').val('');
	}
});

'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import GroupList from './group_list_collection' ;
import wmNotify from '../funcs/wmNotify';
import 'datatables.net';

export default Backbone.View.extend({
	el: 'body',

	events: {
		'change #group-filter'  : 'applyGroupFilter',
		'click #download-certs' : 'downloadCerts',
		'click #export-csv'     : 'exportCsv'
	},

	initialize: function (options) {
		this.options = options || {};
		this.groups = new GroupList(options);
		this.render();
	},

	render: function () {
		var self = this;
		this.groups.fetch({
			success: function() {
				self.groups.each(function (model) {
					var id = model.get('id');
					var name = model.get('name');
					$('#group-filter').append('<option value="' + id + '">' + name + '<//option>');
				});
			},
			error: function () {

			}
		});
	},

	applyGroupFilter: function () {
		var groupId = $('#group-filter').val();
		$('#results_spinner').show();
		$.ajax({
			dataType:'json',
			type: 'GET',
			url: '/reports/evidence/cert_report.json?groupId=' + groupId,
			success: function (data) {
				$('#results_spinner').hide();
				$('#evidence_report').dataTable({
					'aaData': data.aaData,
					'aoColumnDefs': [
						{'sTitle':'Name','sType':'string','aTargets':[0]},
						{'sTitle':'Company','sType':'string','aTargets':[1]},
						{'sTitle':'Certifying Company','sType':'string','aTargets':[2]},
						{'sTitle':'Certification','sType':'string','aTargets':[3]},
						{'sTitle':'Certification Number','sType':'string','aTargets':[4]},
						{'sTitle':'Issue Date','sType':'date','aTargets':[5]},
						{'sTitle':'Expiration Date','sType':'date','aTargets':[6]},
						{'sTitle':'Actions','sType':'html','aTargets':[7]}
					],
					'sPaginationType': 'full_numbers',
					'bLengthChange': true,
					'bFilter': false,
					'bStateSave': false,
					'bProcessing': true,
					'bServerSide': false,
					'iDisplayLength': 50,
					'bDestroy': true,
					'bAutoWidth': false
				});
				$('#download-certs').removeClass('disabled');
				$('#download-certs').enable();
				$('#export-csv').removeClass('disabled');
				$('#export-csv').enable();
			},
			error: function () {
				$('#results_spinner').hide();
			}
		});
	},

	downloadCerts: function () {
		if ($('#download-certs').hasClass('disabled') === true){
			return;
		}
		var groupId = $('#group-filter').val();
		this.showEvidenceReportAlert('Email sent with certificates to ' + this.options.recipientEmail);
		$('#results_spinner').show();
		$.ajax({
			dataType: 'json',
			type: 'GET',
			url: '/reports/evidence/downloadcertificates.json?groupId=' + groupId + '&screeningType=' + this.options.screeningType,
			success: function () {
				$('#results_spinner').hide();
			},
			error:function () {
				$('#results_spinner').hide();
			}
		});
	},

	exportCsv: function () {
		if ($('#export-csv').hasClass('disabled') === true){
			return;
		}
		this.showEvidenceReportAlert('Email sent with your evidence report to ' + this.options.recipientEmail);
		var groupId = $('#group-filter').val();
		$('#results_spinner').show();
		$.ajax({
			dataType: 'json',
			type: 'GET',
			url: '/reports/evidence/exporttocsv.json?groupId=' + groupId + '&screeningType=' + this.options.screeningType,
			success: function () {
				$('#results_spinner').hide();
			},
			error: function () {
				$('#results_spinner').hide();
			}
		});
	},

	showEvidenceReportAlert: function (message) {
		wmNotify({ message: message });
	}
});


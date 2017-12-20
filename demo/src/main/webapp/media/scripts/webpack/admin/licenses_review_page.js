'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import ajaxSendInit from '../funcs/ajaxSendInit';
import 'datatables.net';

export default () => {
	var licensesListFilters = [],
		userLicensesListFilters = [],
		licensesListObj, userLicensesListObj, meta, meta2;

	ajaxSendInit();

	const LicensesView = Backbone.View.extend({
		el: 'body',
		events: {
			'click a[name=licenses_approve]':       'approveLicenses',
			'click a[name=licenses_decline]':       'declineLicenses',
			'click a[name=licenses_unverified]':    'unverifyLicenses',
			'change #licenses_list_filter':         'updateLicensesFilter',
			'change #userlicenses_list_filter':     'updateUserLicensesFilter'
		},

		approveLicenses (event) {
			this.updateLicensing(this.$(event.target).attr('id'), '/admin/licenses/approvelicense', 'Approved!', 'An error occurred while approving this license.');
		},

		declineLicenses (event) {
			this.updateLicensing(this.$(event.target).attr('id'), '/admin/licenses/declinelicense', 'Declined!', 'An error occurred while declining this license.');
		},

		unverifyLicenses (event) {
			this.updateLicensing(this.$(event.target).attr('id'), '/admin/licenses/onholdlicense', 'On Hold!', 'An error occurred while unverifing this license.');
		},

		updateLicensing (vendorId, url, successMessage, failMessage) {
			$.ajax({
				url: url,
				contentType: 'application/json',
				data: { id: vendorId },
				type: 'POST',
				dataType: 'json'
			})
				.done(_.bind(this.showSuccessMessage, this, vendorId, successMessage))
				.fail(_.bind(this.showFailMessage, this, failMessage));
		},

		showSuccessMessage (vendorId, message) {
			this.$('#' + vendorId + '_msg').html(message);
		},

		showFailMessage (message) {
			alert(message);
		},

		updateLicensesFilter (event) {
			licensesListFilters = this.$(event.target).val() !== '' ? this.$('#licenses_list_filter_form').serializeArray() : [];
			licensesListObj.fnDraw();
		},

		updateUserLicensesFilter (event) {
			userLicensesListFilters = this.$(event.target).val() !== '' ? this.$('#userlicenses_list_filter_form').serializeArray() : [];
			userLicensesListObj.fnDraw();
		}
	});

	// Run the filters on load.
	licensesListFilters = $('#licenses_list_filter_form').serializeArray();
	userLicensesListFilters = $('#licenses_list_filter_form').serializeArray();

	function renderActionCell (data, type, val, metaData) {
		return $('#action-cell-tmpl').tmpl({
			meta: meta[metaData.row]
		}).html();
	}

	function renderActionCell2 (data, type, val, metaData) {
		return $('#action2-cell-tmpl').tmpl({
			meta: meta2[metaData.row]
		}).html();
	}

	function renderUserLicenseNumberCell (data, type, val, metaData) {
		return $('#userlicensenumber-cell-tmpl').tmpl({
			data: data,
			meta: meta2[metaData.row]
		}).html();
	}

	licensesListObj = $('#licenses_list').dataTable({
		'sPaginationType': 'full_numbers',
		'bLengthChange': true,
		'bFilter': false,
		'bStateSave': false,
		'bProcessing': true,
		'bServerSide': true,
		'iDisplayLength': 50,
		'aaSorting': [[1, 'desc']],
		'sAjaxSource': 'unverified_license',
		'aoColumnDefs': [
			{'bSortable': false, 'aTargets': [6]},
			{'mRender': renderActionCell, 'aTargets': [6]}
		],
		'fnServerData': function (sSource, aoData, fnCallback) {
			// Apply filters.
			for (let i = 0, size = licensesListFilters.length; i < size; i++) {
				aoData.push(licensesListFilters[i]);
			}
			$.getJSON(sSource, aoData, function (json) {
				if (json.aaData.length === 0) {
					$('#table_licenses').hide();
					$('.table_licenses_msg').html('There are no unverified licenses.');
				} else {
					$('#table_licenses').show();
					$('.table_licenses_msg').html('');
				}
				meta = json.aMeta;
				for (let i = 0, size = json.aaData.length; i < size; i++) {
					json.aaData[i][2] = '<a href="/profile/' + json.aMeta[i].creator_user_number + '">' + json.aaData[i][2] + '<\/a>';
				}
				fnCallback(json);
			});
		}
	});

	userLicensesListObj = $('#userlicenses_list').dataTable({
		'sPaginationType': 'full_numbers',
		'bLengthChange': true,
		'bFilter': true,
		'bStateSave': false,
		'bProcessing': true,
		'bServerSide': true,
		'iDisplayLength': 50,
		'aaSorting': [[1, 'desc']],
		'sAjaxSource': 'unverified_userlicenses',
		'aoColumnDefs': [
			{'bSortable': false, 'aTargets': [7]},
			{'mRender': renderUserLicenseNumberCell, 'aTargets': [4]},
			{'mRender': renderActionCell2, 'aTargets': [7]}
		],
		'fnServerData' (sSource, aoData, fnCallback) {
			// Apply filters.
			for (let i = 0, size = userLicensesListFilters.length; i < size; i++) {
				aoData.push(userLicensesListFilters[i]);
			}
			$.getJSON(sSource, aoData, function (json) {
				if (json.aaData.length === 0) {
					$('#table_userlicenses').hide();
					$('.table_userlicenses_msg').html('There are no unverified user licenses.');
				} else {
					$('#table_userlicenses').show();
					$('.table_userlicenses_msg').html('');
				}
				meta2 = json.aMeta;
				for (let i = 0, size = json.aaData.length; i < size; i++) {
					json.aaData[i][2] = '<a href="/profile/' + json.aMeta[i].user_number + '">' + json.aaData[i][2] + '<\/a>';
				}
				fnCallback(json);
			});
		}
	});

	new LicensesView();
};

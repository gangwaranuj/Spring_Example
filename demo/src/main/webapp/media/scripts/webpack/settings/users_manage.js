import $ from 'jquery';
import 'datatables.net';
import React from 'react';
import { render } from 'react-dom';
import HidePricingView from './employee_pricing_view';
import wmModal from '../funcs/wmModal';
import InvalidTaxInfoWarningModalTemplate from '../settings/templates/invalid_tax_info_warning_modal.hbs';
import EmployeeUploader from './components/add_employee_uploader';

export default function (hasBusinessTaxInfo) {
	let meta;

	const cancelEditTaxIfo = (event) => {
		if ($(event).attr('data-modal-accept')) {
			window.location.href = '/account/tax';
		}
	};

	$('#add-new-emlpoyee-btn').click(() => {
		if (hasBusinessTaxInfo) {
			window.location.href = '/users/add_user';
		} else {
			wmModal({
				root: $('.page-header'),
				title: 'Action Required',
				autorun: true,
				destroyOnClose: true,
				content: InvalidTaxInfoWarningModalTemplate()
			});
			$('.page-header [data-modal-close]').on('click', function onClickFunc () {
				cancelEditTaxIfo(this);
			});
		}
	});

	function renderEmployeeBulkUploader () {
		if (hasBusinessTaxInfo) {
			render(
				<EmployeeUploader />, document.getElementById('add-new-emlpoyee-btn-bulk')
			);
		}
	}

	renderEmployeeBulkUploader();

	const table = $('#user_list').dataTable({
		sPaginationType: 'full_numbers',
		bLengthChange: false,
		bSort: false,
		bFilter: false,
		bStateSave: false,
		bProcessing: true,
		bServerSide: true,
		aaSorting: [],
		iDisplayLength: 25,
		sAjaxSource: '/users/list',
		aoColumnDefs: [
			{
				mRender: (data, type, val, metaData) => {
					return $('#name-cell-tmpl').tmpl({
						data,
						meta: meta[metaData.row]
					}).html();
				},
				aTargets: [0]
			},
			{
				mRender: (data) => {
					return data.replace('Dispatcher', 'Team Agent');
				},
				aTargets: [1]
			},
			{
				mRender: (data, type, val, metaData) => {
					return $('#login-cell-tmpl').tmpl({
						data,
						meta: meta[metaData.row]
					}).html();
				},
				aTargets: [2]
			},
			{
				mRender: (data, type, val, metaData) => {
					return $('#stats-cell-tmpl').tmpl({
						data,
						meta: meta[metaData.row]
					}).html();
				},
				aTargets: [3] }
		],
		fnServerData (sSource, aoData, fnCallback) {
			$.each($('[data-behavior=filter]').serializeArray(), (i, item) => {
				aoData.push(item);
			});

			$.getJSON(sSource, aoData, (json) => {
				meta = json.aMeta;
				fnCallback(json);
			});
		}
	});

	$('[data-behavior=filter]').on('change', () => {
		table.fnDraw();
	});

	$('.toggle-active-outlet').on('click', function onClickFunc () {
		const v = parseInt($('[name=inactive]').val(), 10);
		$(this).text(v ? 'Show Inactive Employees' : 'Show Active Employees');
		$('[name=inactive]').val(!v ? 1 : 0).trigger('change');
	});

	new HidePricingView(); // eslint-disable-line no-new
}

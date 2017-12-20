'use strict';

import $ from 'jquery';
import Application from '../core';
import CreationModal from '../assignments/creation_modal';
import Backbone from 'backbone';
import moment from 'moment';
import Chart from 'chart.js';
import wmNotify from '../funcs/wmNotify';
import 'datatables.net';
import '../dependencies/jquery.tmpl';
import 'jquery-ui';
import '../dependencies/jquery.bootstrap-dropdown';
import 'jquery-form/jquery.form';
import '../config/datepicker';

const updateProject = (url, element) => {
	url += $(element).attr('data-id');
	$.ajax({
		url,
		type: 'POST',
		headers: {
			'X-CSRF-Token': Application.CSRFToken
		}
	})
		.then(
			() => window.location.reload(),
			({ message }) => wmNotify({ message, type: 'danger' })
		);
}

const Router = Backbone.Router.extend({
	routes: {
		'projects(/)'            : 'index',
		'projects/add'           : 'add',
		'projects/edit/:project' : 'add',
		'projects/view/:project' : 'view'
	},

	initialize: function () {
		Backbone.history.start({ pushState: true });
	},

	index: function () {
		let meta;
		let $projectList = $('#project_list');

		const cellRenderer = (template) => {
			return (data, type, val, { row }) => {
				return $(template).tmpl({
					data,
					meta: meta[row]
				}).html();
			};
		};

		const cellClientRenderer = () => {
			return (data) => {
				let date;

				if (data.indexOf('DELETED_ON_') >= 0) {
					let deletedOnRegex = /DELETED_ON_(\d*)/g,
						match = deletedOnRegex.exec(data),
						timestamp = match[1];
					// needed to differentiate between seconds & milliseconds due to this change that affected
					// the way the timestamp gets created:
					// https://github.com/workmarket/application/commit/90cedb08043f8a2129f6266953fbd9302dd76654
					if (timestamp.length >= 13) {
						date = moment(parseInt(timestamp)).format('dddd, MMMM D, YYYY');
					} else {
						date = moment.unix(parseInt(timestamp)).format('dddd, MMMM D, YYYY');
					}

					data = data.replace(/DELETED_ON_.*/g, '');
				}

				return date ? `${data} DELETED ON: ${date}` : data;
			};
		};

		let aoColumnDefs = [
			{
				mRender: cellRenderer('#cell-title-tmpl'),
				aTargets: [0]
			},
			{
				mRender: cellClientRenderer(),
				aTargets: [1]
			},
			{
				mRender: cellRenderer('#cell-edit-tmpl'),
				aTargets: [5],
				bSortable: false,
				bVisible: !Application.Features.hasProjectPermission || Application.Features.hasProjectAccess
			},
			{
				mRender: cellRenderer('#cell-delete-tmpl'),
				aTargets: [6],
				bSortable: false
			},
			{
				mRender: cellRenderer('#cell-active-tmpl'),
				aTargets: [7],
				bSortable: false
			}
		];

		// if (!Application.Features.hasProjectBudgetEnabled) {
		// 	// Hide remaining budget column
		// 	invisibleColumn.push(4);
		// }

		$projectList.dataTable({
			sPaginationType: 'full_numbers',
			bLengthChange: false,
			bFilter: false,
			bStateSave: false,
			bProcessing: true,
			bServerSide: true,
			sAjaxSource: '/projects/list',
			iDisplayLength: 25,
			aoColumnDefs,
			fnServerData: (sSource, aoData, fnCallback) => {
				$.getJSON(sSource, aoData, (json) => {
					$('.custom-table-header-outlet').html($('#custom-header-outlet-tmpl').tmpl());
					meta = json.aMeta;
					fnCallback(json);
				});
			}
		});

		let cellUpdate = (url, context) => {
			$projectList.on('click', context, ({ target }) => updateProject(url, target));
		};

		cellUpdate('/projects/deactivate/', '.deactivate-project');
		cellUpdate('/projects/delete/', '.delete-project');
	},

	add: function () {
		let $projectForm = $('#form_project');

		$projectForm.on('click', '[name="budgetEnabledFlag"]', () => $('#budget-amount').toggle());

		$('#add-new-client').on('click', () => {
			if ($('#new-client').is(':visible')) {
				closeNewClientInputs();
			} else {
				openNewClientInputs();
			}
		});

		// Toggle Form
		function openNewClientInputs() {
			$('#new-client').slideDown().fadeIn();
			$('a#add-new-client').html('Hide Add New Client');
		}

		function closeNewClientInputs() {
			$('#new-client').slideUp().fadeOut();
			$('a#add-new-client').html('Add New Client');
		}

		// Create New Client
		$('#newclient_form_submit').on('click', (event) => {
			event.preventDefault();
			saveNewClientCompany($projectForm.find('.newclient').fieldSerialize());
		});

		const addNewClientCompanyToSelects = (id, name) => {
			$('select#project_client_company_list')
				.removeProp('selected')
				.append(`<option value="${id}" selected="selected">${name}</option>`);
		}

		function saveNewClientCompany(data) {
			$.ajax({
				url: '/assignments/addclientcompany',
				type: 'POST',
				data: { data },
				dataType: 'json',
				headers: {
					'X-CSRF-Token': Application.CSRFToken
				},
				success: ({ successful, errors, data }) => {
					if (successful) {
						let message = 'Success! This new client is available immediately to use with this project.';
						wmNotify({ message });
						closeNewClientInputs();
						$projectForm.find('.newclient').clearFields();

						// Add the new client company to the selects.
						let { id, name } = data;
						addNewClientCompanyToSelects(id, name);
					} else {
						let [message] = errors;
						wmNotify({ message, type: 'danger' });
					}
				}
			});
		}

		$('#project_start_date').datepicker({
			showOptions: {
				direction: 'up'
			}
		});
		$('#project_due_date').datepicker({
			showOptions: {
				direction: 'up'
			}
		});
	},

	view: function (project) {
		$('#project_list').dataTable({
			sPaginationType: 'full_numbers',
			bLengthChange: false,
			bFilter: false,
			bStateSave: false,
			bProcessing: true,
			bServerSide: true,
			sAjaxSource: '/projects/view',
			iDisplayLength: 50,
			aoColumns: [null, { 'bSortable': false }, null, null, null, { 'bSortable': false }],
			fnServerData: (sSource, aoData, fnCallback) => {
				aoData.push({
					name: 'id',
					value: project
				});
				$.getJSON(sSource, aoData, (json) => {
					let { aaData, aMeta } = json;
					aaData.forEach((data, index) => data[0] = `<a href="/assignments/details/${aMeta[index].id}">${data[0]}</a>`)
					fnCallback(json);
				});
			}
		});

		if (Application.Features.hasBudgetEnabled) {
			//Get the context of the canvas element we want to select
			let ctx = document.getElementById('myChart').getContext('2d');
			let data = {
				labels: [
					'Work In Paid Percentage',
					'Work In Process Percentage',
					'Spent Percentage'
				],
				datasets: [
					{
						data: [
							parseFloat(Application.Data.workInPaidPercentage),
							parseFloat(Application.Data.workInProcessPercentage),
							100 - parseFloat(Application.Data.spentPercentage)
						],
						backgroundColor: [
							'#F38630',
							'#999966',
							'#69D2E7'
						]
					}
				]
			};
			new Chart(ctx, { type: 'pie', data });
		}

		$('.sidebar').on('click', '.deactivate-project', ({ target }) => updateProject('/projects/deactivate/', target))
		
		$('#new_project_assignment_action').on('click', (e) => {
			e.preventDefault();
			new CreationModal({
				projectId: project,
				clientCompanyId: config.data.clientCompanyId,
			});
		});
	}
});

Application.init(config, Router);

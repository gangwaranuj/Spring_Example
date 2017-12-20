import 'datatables.net';
import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import wmSelect from '../funcs/wmSelect';
import wmNotify from '../funcs/wmNotify';
import wmMaskInput from '../funcs/wmMaskInput';
import wmModal from '../funcs/wmModal';

export default Backbone.View.extend({
	el: 'body',

	events: {
		'click a.cta-manage-client': 'openManageClient',
		'click a.cta-delete-client': 'deleteClient',
		'click #client_submit_form': 'submitFormClient'
	},

	initialize (options) {
		this.options = options || {};
		this.loadTables();
		this.customFormMessage = '#custom_form_message';
		this.$clientList = $('#client_list');
	},

	showSuccessMessage (message) {
		wmNotify({ message });
		this.datatableObjClients.fnDraw(false);
	},

	showErrorMessage (message) {
		wmNotify({
			message,
			type: 'danger'
		});
	},

	deleteClient (e) {
		e.preventDefault();
		const url = $(e.currentTarget).attr('href');

		if (confirm('Are you sure you want to remove this client?')) { // eslint-disable-line no-alert
			$.ajax({
				url,
				type: 'DELETE',
				dataType: 'json',
				context: this,
				success (data) {
					if (data.successful) {
						this.showSuccessMessage('You have successfully deleted the client');
						this.$clientList.trigger('removeClient', [data.data.clientId]);
					} else {
						this.showErrorMessage((data.messages) ? data.messages.join('<br>') : 'The client could not be deleted');
					}
				}
			});
		}
	},

	submitFormClient (event) {
		event.preventDefault();
		const $form = $('#form_client_manage');
		const action = $('.wm-modal--title').attr('rel');

		$.ajax({
			type: 'POST',
			url: $form.attr('action'),
			data: $('.wm-modal--content').find('form').serialize(),
			dataType: 'json',
			context: this,
			success (data) {
				if (data.successful) {
					if ($('.wm-modal--title').attr('rel') === 'add') {
						this.showSuccessMessage('You have successfully added the client');
						this.$clientList.trigger('addClient', [data.data.clientData]);
					} else {
						this.showSuccessMessage('You have successfully edited the client');
						this.$clientList.trigger('editClient', [data.data.clientData]);
						this.$clientList.trigger('redrawLocationTable');
						this.$clientList.trigger('redrawContactTable');
					}
					this.clientCompanyModal.destroy();
				} else {
					this.showErrorMessage((data.errors) ? data.errors.join('<br>') : `The client could not be ${(action === 'add') ? 'added' : 'edited'}`);
				}
			}
		});
	},

	openManageClient (event) {
		event.preventDefault();
		const action = $(event.currentTarget).attr('rel');

		$.ajax({
			type: 'GET',
			url: $(event.currentTarget).attr('href'),
			context: this,
			success (response) {
				if (!_.isEmpty(response)) {
					this.clientCompanyModal = wmModal({
						autorun: true,
						title: `${(action === 'edit') ? 'Edit' : 'Create New'} Client`,
						destroyOnClose: true,
						content: response
					});

					const options = {
						selector: $('.controls').find('#industry_name')
					};
					wmSelect(options);

					wmMaskInput({ selector: '#client-phone' });
					wmMaskInput({ selector: '#client-phone-ext' }, '0');
					$('.wm-modal--title').attr('rel', action);
				}
			}

		});
	},

	loadTables () {
		let meta;

		this.datatableObjClients = $('#client_list').dataTable({
			sPaginationType: 'full_numbers',
			bLengthChange: true,
			bFilter: false,
			bStateSave: false,
			bProcessing: true,
			bServerSide: true,
			iDisplayLength: 50,
			sDom: 'rtip',
			aoColumnDefs: [
				{
					bSortable: false,
					aTargets: [2, 3, 4, 5, 6, 7, 8]
				},
				{
					mRender: (data, type, val, metaData) => {
						return $('#name-client-cell-tmpl').tmpl({
							meta: meta[metaData.row]
						}).html();
					},
					aTargets: [0]
				},
				{
					mRender: (data, type, val, metaData) => {
						return $('#edit-client-cell-tmpl').tmpl({
							meta: meta[metaData.row]
						}).html();
					},
					aTargets: [7]
				},
				{
					mRender: (data, type, val, metaData) => {
						return $('#delete-client-cell-tmpl').tmpl({
							meta: meta[metaData.row],
							row: metaData.row
						}).html();
					},
					aTargets: [8]
				}
			],
			bSort: true,
			sAjaxSource: '/addressbook/client/get_all',
			fnServerData (sSource, aoData, fnCallback) {
				$.getJSON(sSource, aoData, (json) => {
					meta = json.aMeta;
					fnCallback(json);
				});
			}
		});
	}
});

import Backbone from 'backbone';
import $ from 'jquery';
import 'jquery-form/jquery.form';
import _ from 'underscore';
import InvoiceListView from './invoices_list_view';
import InvoiceListCollection from './invoice_list_collection';
import wmSelect from '../funcs/wmSelect';
import wmModal from '../funcs/wmModal';
import wmNotify from '../funcs/wmNotify';
import getCSRFToken from '../funcs/getCSRFToken';
import Template from './templates/statement-detail.hbs';

export default Backbone.View.extend({
	el: 'body',

	events: {
		'click #bundle-selected-outlet'        : 'bundleSelected',
		'click #add-to-bundle-selected-outlet' : 'addToBundleSelected',
		'click #pay-selected-outlet'           : 'paySelected',
		'click #print-selected-outlet'         : 'printSelected',
		'click #export-filtered-outlet'        : 'exportFiltered',
		'click #print-filtered-outlet'         : 'printFiltered',
		'click #export-selected-outlet'        : 'exportSelected',
		'click #pay-statement-outlet'          : 'payStatement',
		'click #print-statement-outlet'        : 'printStatement',
		'change [name=statementId]'            : 'applyStatementFilters',
		'click button[type=button]'            : 'applyFilters',
		'click button[type=reset]'             : 'resetFilters',
		'click #cta-sort-status'               : 'sortByStatus',
		'click #show-adv-filters'              : 'showAdvFilters'
	},

	initialize: function (options) {
		this.list = new InvoiceListView({
			el: this.el,
			parent: this,
			collection: new InvoiceListCollection(),
			currentView: options.current_view,
			mmwAutoPayEnabled: options.mmwAutoPayEnabled
		});

		this.resourcesDropdownSelect = wmSelect({
			selector: '#resources-dropdown',
			root: this.el
		}, {
			valueField: 'id',
			labelField: 'name',
			searchField: 'name',
			options: [],
			persist: false,
			loadThrottle: 200,
			create: false,
			allowEmptyOption: false,
			render: {
				option: (item) => `<div>${item.name}, ID: ${item.userNumber} | ${item.address}</div>`,
				item: (item) => `<div>${item.name}</div>`
			},
			load: function (query, callback) {
				if (!query.length) {
					return callback();
				}

				$.ajax({
					url: '/assignments/assigned_resources',
					type: 'GET',
					dataType: 'json',
					data: {
						term: query
					},
					error: callback,
					success: callback
				});
			}
		})[0].selectize;

		wmSelect();

		$('[name="fromDate"]').datepicker({dateFormat: 'mm/dd/yy'});
		$('[name="toDate"]').datepicker({dateFormat: 'mm/dd/yy'});

		//if there are statements options
		var $statementId = $('#statementId');
		if ($statementId.length > 0) {
			$statementId.prepend($('<option>').val('').addClass('current_invoices').text('Current Invoices'));
			$statementId.append($('<option>').val('').addClass('all_statements').text('All Statements'));
		}

		if (this.options.show_current_invoice_view){
			this.setCurrentInvoicesFilters();
			this.setUnpaidStatus();
		} else if (this.options.show_all_statements_view){
			this.setAllStatementsFilters();
			this.setUnpaidStatus();
		} else if (this.options.show_statements_id_view){
			this.setUnpaidStatementsIdFilters();
			this.setUnpaidStatus();
		}
		this.applyFilters();
	},

	render: function() {
		this.togglePayStatement();
		this.updateCurrentStatementInfo();
		return this;
	},

	bundleSelected: function (e) {
		var self = this;

		this.showModal(e, {
			href: e.currentTarget.href,
			data: null,
			controls: [
				{
					text: 'Cancel',
					close: true,
					classList: ''
				},
				{
					text: 'Create Bundle',
					primary: true
				}
			],
			onComplete: function () {
				var selected = self.list.getSelected();
				var bundleForm = $('#form_bundle_invoices');
				var data = _.map(selected, function(item) {
					return {name:'invoice_ids', value: item};
				});
				const modal = $('.wm-modal .-active');

				modal.find('.-primary').on('click', () => {
					bundleForm.ajaxSubmit({
						data: data,
						dataType: 'json',
						success: function (data) {
							if (data.successful) {
								modal.find('.wm-modal--control').trigger('click');
								wmNotify({ message: data.messages[0] });
								self.list.clearSelected();
								self.list.refetch();
							} else {
								wmNotify({
									message: data.messages[0],
									type: 'danger'
								});
							}
						}
					});
				});
			}
		});
	},

	showModal: function(e, params) {
		if ($(e.currentTarget).is('.disabled')) {
			e.preventDefault();
			return false;
		}

		$.ajax({
			type: 'GET',
			url: params.href,
			context: this,
			data: params.data,
			success: function (response) {
				if (!_.isEmpty(response)) {
					wmModal({
						autorun: true,
						title: $(e.currentTarget).attr('title') || $(e.currentTarget).text(),
						destroyOnClose: true,
						content: response,
						controls: params.controls
					});
					if (typeof params.onComplete === 'function') {
						params.onComplete();
					}
				}
			}
		});

		e.preventDefault();
		return false;
	},

	addToBundleSelected: function(e) {
		var self = this;
		var bundleIds = [];
		var bundleNames = [];
		$.each($('.bundle input'), function( index, value ) {
			bundleIds[index] = value.value;
		});
		$.each($('.bundle input').parent().siblings('.bundle-details').find('.bundle-row .invoice-detail strong'), function( index, value ) {
			bundleNames[index] = $(value).text();
		});
		var params = {
			'bundleIds'   : bundleIds,
			'bundleNames' : bundleNames
		};


		this.showModal(e, {
			href: e.currentTarget.href,
			data: params,
			controls: [
				{
					text: 'Cancel',
					close: true,
					classList: ''
				},
				{
					text: 'Add to bundle',
					primary: true
				}
			],
			onComplete: function() {
				let selected = self.list.getSelected();
				let data = _.map(selected, function(item) {
					return {name:'invoice_ids', value: item};
				});

				const modal = $('.wm-modal .-active');
				modal.find('.-primary').on('click', () => {
					$('#form_bundle_invoices').ajaxSubmit({
						data: data,
						dataType: 'json',
						success: function(data) {
							if (data.successful) {
								modal.find('.wm-modal--control').trigger('click');
								wmNotify({ message: data.messages[0] });
								self.list.clearSelected();
								self.list.refetch();
							} else {
								wmNotify({
									message: data.messages[0],
									type: 'danger'
								});
							}
						}
					});
				});
			}
		});
	},

	paySelected: function(e) {
		var self = this;
		var params = {'ids': this.list.getSelected()};

		this.showModal(e, {
			href: e.currentTarget.href,
			data: params,
			controls: [
				{
					text: 'Cancel',
					close: true,
					classList: ''
				},
				{
					text: 'Pay',
					primary: true
				}
			],
			onComplete: function() {
				const modal = $('.wm-modal .-active');
				modal.find('.-primary').one('click', () => {
					$('#form_pay_invoices').ajaxSubmit({
						dataType: 'json',
						success: function (data) {
							if (data.successful) {
								modal.find('.wm-modal--control').trigger('click');
								wmNotify({message: data.messages[0]});
								self.list.clearSelected();
								self.list.refetch();
							} else {
								wmNotify({
									message: data.messages[0],
									type: 'danger'
								});
							}
						}
					});
				});
			}
		});
	},

	payStatement: function(e) {
		var id = this.$('[name="statementId"]').val();

		this.showModal(e, {
			href: e.currentTarget.href + '/' + id,
			data: null
		});
	},

	printSelected: function (e) {
		this._redirectWithSelected(e);
	},

	printFiltered: function (e) {
		this._redirectWithFiltered(e);
	},

	printStatement: function (e) {
		e.preventDefault();
		this.redirect($(e.target).attr('href') + '/' + $('select[name="statementId"]').val());
	},

	exportSelected: function (e) {
		this._redirectWithSelected(e);
	},

	exportFiltered: function (e) {
		this._redirectWithFiltered(e);
	},

	_redirectWithSelected: function(e) {
		e.preventDefault();
		var params = _.map(this.list.getSelected(), function(item) {
			return {name:'ids[]', value:item};
		});

		this.redirect($(e.target).attr('href') + '?' + $.param(params));
	},

	_redirectWithFiltered: function(e) {
		e.preventDefault();
		var filters = $('[ref="filter"]').fieldSerialize();

		this.redirect($(e.target).attr('href') + '?' + filters);
	},

	redirect: function (url, msg, type) {
		if (msg) {
			var e = $('<form class="dn"></form>');
			e.attr({
				'action': '/message/create',
				'method': 'POST'
			});
			if (typeof msg === 'string') { msg = [msg]; }
			for (var i=0; i < msg.length; i++) {
				e.append(
					$('<input>').attr({
						'name': 'message[]',
						'value': msg[i]
					}));
			}
			e.append(
				$('<input>').attr({
					'name': 'type',
					'value': type
				}));
			e.append(
				$('<input>').attr({
					'name': 'url',
					'value': url
				}));
			e.append(
				$('<input>').attr({
					'name':'_tk',
					'value':getCSRFToken()
				}));
			$('body').append(e);
			e.submit();
		} else {
			window.location = url;
		}
	},

	togglePayStatement: function() {
		if($('#statementId').val()){
			var id = this.$('#statementId').val();
			var paid = this.options.statements_details[id].isPaid;
			this.$('#pay-statement-outlet')
				.toggleClass('disabled', paid)
				.toggleClass('tooltipped tooltipped-n', paid)
				.attr('aria-label', (paid) ? 'Statement is paid' : '');
		}
	},

	applyFilters: function() {
		this.render();
		this.list.clearSelected();
		this.list.refetch();
	},

	resetFilters: function() {
		this.$('form').trigger('reset');
		$('#fromDate').val('');
		$('#client_company')[0].selectize.clear(true);
		$('#project-dropdown')[0].selectize.clear(true);
		$('#internal-owner-dropdown')[0].selectize.clear(true);
		$('#resources-dropdown')[0].selectize.clear(true);
		this.applyFilters();
	},

	sortByStatus: function () {
		if ($('[name="sortDirection"]').val() === 'asc') {
			$('[name="sortDirection"]').val('desc');
			$('#cta-sort-status').addClass('sorting_asc').removeClass('sorting_desc');
		} else {
			$('[name="sortDirection"]').val('asc');
			$('#cta-sort-status').addClass('sorting_desc').removeClass('sorting_asc');
		}
		this.applyFilters();
	},

	updateCurrentStatementInfo: function () {
		if ($('#statementId').length > 0) {
			//Remove old info
			$('#statementDetail div,#statementDetail h3').remove();

			if ($('#statementId').val()){
				var statementBalance = this.options.statements_details[$('#statementId').val()].balance,
					remainingBalance = this.options.statements_details[$('#statementId').val()].remainingBalance;

				$('#statementDetail').prepend(Template({
					statementNumber:  this.options.statements_details[$('#statementId').val()].statementNumber,
					statementBalance: statementBalance,
					statementDueDate: this.options.statements_details[$('#statementId').val()].dueDate,
					remainingBalance: remainingBalance,
					totalPaid: statementBalance - remainingBalance
				}));
			}
		}
	},

	resetFiltersWithoutReload: function () {
		this.$('form').trigger('reset');
	},

	setUnpaidStatus: function () {
		$('#paidStatus').get(0).selectize.setValue(this.options.invoice_paid_status);
	},

	setUnpaidStatementsIdFilters: function () {
		$('[name="ignoreStatements"]').attr('checked', true);
		$('[name="bundledInvoices"]').attr('checked', true);

		$.each(this.options.statements_details, function(key, elem) {
			if(!elem.isPaid){
				$('#statementId').val(key);
				return false;
			}
		});
	},

	setCurrentInvoicesFilters: function () {
		this.resetFiltersWithoutReload();
		$('#statementId .current_invoices').attr('selected', 'selected');
		$('[name="ignoreStatements"]').attr('checked', true);
		$('[name="bundledInvoices"]').attr('checked', false);
	},

	setAllStatementsFilters: function() {
		this.resetFiltersWithoutReload();
		$('#statementId .all_statements').attr('selected', 'selected');
		$('[name="ignoreStatements"]').attr('checked', false);
		$('[name="bundledInvoices"]').attr('checked', false);
	},

	setStatementsIdFilters: function() {
		$('[name="ignoreStatements"]').attr('checked', true);
		$('[name="bundledInvoices"]').attr('checked', true);
	},

	applyStatementFilters: function () {
		if ($('#statementId .current_invoices').is(':selected')){
			this.setCurrentInvoicesFilters();
		} else if($('#statementId .all_statements').is(':selected')){
			this.setAllStatementsFilters();
		} else {
			this.setStatementsIdFilters();
		}
		this.applyFilters();
	},

	showAdvFilters: function() {
		if ($('#advFilters').is(':visible')) {
			$('#advFilters').hide();
			$('#show-adv-filters').html('Advanced Filters &#9660;');
		}
		else {
			$('#advFilters').show();
			$('#show-adv-filters').html('Hide Advanced Filters &#9650;');
		}
	}
});

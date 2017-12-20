'use strict';

import $ from 'jquery';
import _ from 'underscore';
import Backbone from 'backbone';
import PaginationSelectionView from '../pagination/selection_view';
import wmNotify from '../funcs/wmNotify';
import getCSRFToken from '../funcs/getCSRFToken';
import 'datatables.net';
import 'jquery-form/jquery.form';

export default Backbone.View.extend({
	el: 'body',

	events: {
		'change select[name="groups"]'     : 'selectGroup',
		'click button[name="auto-send"]'   : 'selectAutoSend',
		'click button[data-action="send"]' : 'send'
	},

	totalResults: 0,
	visibleResults: 0,
	defaultDisplaySize: 50,

	resultsToSendMap: new Backbone.Collection(),

	initialize: function (options) {

		var meta,
			TABLE_CHECKBOX_COLUMN = 0,
			TABLE_TITLE_COLUMN = 1,
			TABLE_DATE_COLUMN = 2,
			TABLE_CITY_COLUMN = 3,
			TABLE_SPEND_COLUMN = 4,
			TABLE_SEND_TO_COLUMN = 5,
			DATA_TITLE_COLUMN = 1,
			DATA_SPEND_COLUMN = 4,
			DATA_TYPE_COLUMN = 5,
			DATA_TYPE_CHILD_COUNT = 6;

		const templateRenderer = (data, type, val, metaData, template) => {
			return $(template).tmpl({
				data,
				meta: meta[metaData.row]
			}).html();
		};

		const bundleLinkRenderer = (data, type, val, metaData, template) => {
			if (template === '#cell-send-to-tmpl') {
				return '<a href="/assignments/details/' + meta[metaData.row].work_number + '">Route this bundle</a>';
			} else {
				return '&nbsp;';
			}
		};

		const cellRenderer = (template) => {
			return  (data, type, val, metaData) => {
				if (data === 'B') {
					return bundleLinkRenderer(data, type, val, metaData, template);
				} else {
					return templateRenderer(data, type, val, metaData, template);
				}
			};
		};

		var titleRenderer = function (data, type, val, metaData) {
			var pre = '';
			if (data === 'B') {
				pre = '<span class="label label-bundle tooltipped tooltipped-n" aria-label="Part of a Bundle with ' + data + ' assignments.">B</span> ';
			}
			return pre + data;
		};

		var spendRenderer = function (data, type, val, metaData) {
			var pre = '';
			var suf = '';
			if (data === 'B') {
				pre = '<span class="label label-bundle tooltipped tooltipped-n" aria-label="Total spend for Bundle with ' + data + ' assignments.">';
				suf = '</span>';
			}
			return pre + data + suf;
		};

		this.table = $('#work-table').dataTable({
			'sPaginationType': 'full_numbers',
			'bLengthChange': false,
			'bFilter': false,
			'bSort': false,
			'iDisplayLength': this.defaultDisplaySize,
			'bProcessing': true,
			'bServerSide': true,
			'sAjaxSource': '/assignments/bulk_send/list.json',
			'sDom': '<"custom-table-header-wrapper"l<"custom-table-header-outlet">>frtip',
			'aoColumnDefs': [
				{'bSortable': false, 'aTargets': [TABLE_CHECKBOX_COLUMN]},
				{'mRender': cellRenderer('#cell-select-tmpl'), 'aTargets': [TABLE_CHECKBOX_COLUMN]},
				{'mRender': titleRenderer, 'aTargets': [TABLE_TITLE_COLUMN]},
				{'mRender': cellRenderer('#cell-send-to-tmpl'), 'aTargets': [TABLE_SEND_TO_COLUMN]},
				{'mRender': spendRenderer, 'aTargets': [TABLE_SPEND_COLUMN]},
				{'sClass': 'nowrap', 'aTargets': [TABLE_DATE_COLUMN, TABLE_CITY_COLUMN]}
			],
			'fnServerData': _.bind(function (sSource, aoData, fnCallback) {
				$('#actions-header').html($('#custom-header-outlet-tmpl').tmpl());

				$.each(this.options.ids, function (i, item) {
					aoData.push({name: 'ids', value: item});
				});

				$.ajax({
					context: this,
					url: sSource,
					type: 'post',
					dataType: 'json',
					data: aoData
				}).success(function (json) {
					meta = json.aMeta;

					this.paginationSelection.reset();
					this.paginationSelection.setTotalCount(json.iTotalRecords);
					this.paginationSelection.setVisibleCount(this.defaultDisplaySize);

					if (!json.iTotalDisplayRecords) {
						this.redirect('/assignments#status/sent/manage');
						return;
					}

					fnCallback(json);
					this.render();
				});
			}, this)
		});

		this.paginationSelection = new PaginationSelectionView({
			resultsSelector: '#work-table tbody'
		});
		this.paginationSelection.setResults(this.options.ids);
	},

	redirect: function (url, msg, type) {
		if (msg) {
			var e = $("<form class='dn'></form>");
			e.attr({
				'action': '/message/create',
				'method': 'POST'
			});
			if (typeof msg === 'string') { msg = [msg]; }
			for (var i=0; i < msg.length; i++) {
				e.append(
					$("<input>").attr({
						'name': 'message[]',
						'value': msg[i]
					}));
			}
			e.append(
				$("<input>").attr({
					'name': 'type',
					'value': type
				}));
			e.append(
				$("<input>").attr({
					'name': 'url',
					'value': url
				}));
			e.append(
				$("<input>").attr({
					'name':'_tk',
					'value':getCSRFToken()
				}));
			$('body').append(e);
			e.submit();
		} else {
			window.location = url;
		}
	},

	render: function () {
		this.paginationSelection.render();

		this.$('.selected-send-outlet').text('');
		this.resultsToSendMap.each(function (item) {
			var names = item.get('groupNames');
			var isAutoSend = item.get('isAutoSend');
			if (names) {
				this.$('input[type="checkbox"][value="' + item.id + '"]').closest('tr').find('.selected-send-outlet').text(names.join(', '));
			} else if (isAutoSend) {
				this.$('input[type="checkbox"][value="' + item.id + '"]').closest('tr').find('.selected-send-outlet').html('<strong><span class="orange-brand" style="color:#f7961D;">Work</span><span class="gray-brand">Send</span>&trade;</strong>');
			}
		}, this);

		this.updateSendOptions();
		this.paginationSelection.bind('select:render', this.updateAttachOptions, this);
		this.paginationSelection.bind('select:visible', this.updateAttachOptions, this);
		this.paginationSelection.bind('select:all', this.updateAttachOptions, this);

		return this;
	},

	selectGroup: function (e) {
		var option = $(e.currentTarget).find(':selected');
		var groupId = parseInt(option.val());
		var groupName = option.text(); // or self.groups[id]

		this.$('#work-table tbody input[type="checkbox"]:checked').each(_.bind(function (i, item) {
			var id = $(item).val();
			var model = this.resultsToSendMap.get(id);
			if (model && !model.has('isAutoSend')) {
				this.resultsToSendMap.remove(model);
			}
			if (!_.isNaN(groupId)) {
				this.resultsToSendMap.add([{
					id: id,
					groupIds: [groupId],
					groupNames: [groupName]
				}]);
			}
		}, this));

		this.paginationSelection.clear();
		this.render();

		$(e.currentTarget).val(null);
	},

	selectAutoSend: function (e) {
		e.stopPropagation();

		this.$('#work-table tbody input[type="checkbox"]:checked').each(_.bind(function (i, item) {
			var id = $(item).val();
			var model = this.resultsToSendMap.get(id);
			if (model) {
				this.resultsToSendMap.remove(model);
			}
			if (model == null || !model.has('isAutoSend')) {
				this.resultsToSendMap.add([{
					id: id,
					isAutoSend: true
				}]);
			}
		}, this));

		this.paginationSelection.clear();
		this.render();

		$(e.currentTarget).val(null);
	},

	send: function (e) {
		e.stopPropagation();

		var data = [];

		this.resultsToSendMap.each(function(item) {
			data.push({name:'ids', value: item.id});
			var groupIds = item.get('groupIds'),
				isAutoSend = item.get('isAutoSend');
			if (groupIds) {
				$.each(groupIds, function (i, gid) {
					 data.push({name:'work[' + item.id + '].groupIds', value: gid});
				});
			}
			else if (isAutoSend) {
				data.push({name:'work[' + item.id + '].isAutoSend', value: true});
			}
		});

		$('#bulk-send-form').ajaxForm({
			context: this,
			data: data,
			success: function (response) {
				if (response.successful) {
					var ids = this.resultsToSendMap.pluck('id');

					this.options.ids = _.difference(this.options.ids, ids);

					this.paginationSelection.reset();
					this.resultsToSendMap.reset();

					this.table.fnDraw();
					wmNotify({
						message: response.messages[0]
					});
					this.updateSendOptions();
				} else {
					wmNotify({
						message: response.messages[0],
						type: 'danger'
					});
				}
			}
		});
	},

	// if any items have routing options, enable Send
	updateSendOptions: function () {
		$('button[data-action="send"]').enable(this.resultsToSendMap.length > 0);
	},

	// enable attach buttons if anything is selected
	updateAttachOptions: function () {
		var enableAttach = this.paginationSelection.selectedCheckboxes().length > 0;
		$('select[name="groups"]').enable(enableAttach);
		$('button[name="auto-send"]').enable(enableAttach);
	}
});

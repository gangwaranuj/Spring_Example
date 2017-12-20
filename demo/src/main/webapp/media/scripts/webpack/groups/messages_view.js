'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import 'datatables.net';
import '../dependencies/jquery.tmpl';
import '../funcs/jquery-helpers';

export default Backbone.View.extend({
	el: '#messages',
	events: {
		'click .summary-toggle': 'toggleSummary'
	},

	initialize: function () {
		let meta = '';

		const renderMessageCell = (data, type, val, { row }) => $('#message-tmpl').tmpl(meta[row]).html();

		$('#group-messages').dataTable({
			sPaginationType: 'full_numbers',
			bLengthChange: false,
			bFilter: false,
			iDisplayLength: 25,
			bProcessing: true,
			bServerSide: true,
			bSort: false,
			bDestroy: true,
			aoColumnDefs: [
				{
					bSortable: false,
					aTargets: [0,1]
				},
				{
					mRender: renderMessageCell,
					aTargets: [0]
				}
			],
			oLanguage: {
				sEmptyTable: 'No talent pool messages.'
			},
			sAjaxSource: `/groups/${this.model.id}/legacy_messages`,
			fnServerData: (sSource, aoData, fnCallback) => {
				$.getJSON(sSource, aoData, function (json) {
					meta = json.aMeta;
					fnCallback(json);
					if (json.aaData.length === 0) {
						$('#send-message-tab').removeClass('-active');
						$('#messages-tab').addClass('-active');
					}
				});
			}
		});
	},

	toggleSummary: function ({ target }) {
		const link = $(target);
		link.closest(link.is('.show') ? '.summary' : '.fulltext').hide().siblings().show().find('.summary-toggle').show();
	}
});

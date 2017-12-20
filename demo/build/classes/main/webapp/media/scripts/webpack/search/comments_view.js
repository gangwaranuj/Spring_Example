'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import wmNotify from '../funcs/wmNotify';
import wmModal from '../funcs/wmModal';
import Template from '../profile/templates/delete_comment_modal.hbs';
import 'datatables.net';

export default Backbone.View.extend({

	events: {
		'click .delete_user_comment'          : 'showDeleteCommentModal',
		'click #delete_comment_confirm_popup' : 'deleteComment'
	},

	initialize: function (options) {
		this.userNumber = options.userNumber;
		this.$table = this.$el.find('table');
		this.buildTable();
	},

	showDeleteCommentModal: function (event) {
		event.preventDefault();
		this.commentToRemoveId = $(event.currentTarget).data('comment-id');
		this.deleteCommentModal = wmModal({
			root: this.el,
			title: 'Delete Comment',
			content: Template(),
			destroyOnClose: true
		});
		this.deleteCommentModal.show();
	},

	deleteComment: function () {
		$.ajax({
			url: '/profile/delete_user_comment',
			type: 'POST',
			data: ({ id: this.commentToRemoveId }),
			dataType: 'json',
			success: _.bind(function (response) {
				if (response.successful) {
					this.$table.fnDraw();
					this.deleteCommentModal.destroy();
					wmNotify({ message: response.messages[0] });
				} else {
					wmNotify({
						message: response.messages[0],
						type: 'danger'
					});
				}
			}, this)
		});
	},

	buildTable: function () {
		this.$table.dataTable({
			'sPaginationType': 'full_numbers',
			'bLengthChange': false,
			'bFilter': false,
			'bStateSave': false,
			'bProcessing': true,
			'bServerSide': true,
			'sAjaxSource': ('/profile/get_user_comments?id=' + this.userNumber),
			'iDisplayLength': 10,
			'aaSorting': [
				[ 0, 'desc' ]
			],
			'aoColumnDefs': [
				{'bSortable': false, 'aTargets': [1, 2, 3]},
				{'sClass': 'actions', 'aTargets': [3]}
			],
			'fnServerData': function (sSource, aoData, fnCallback) {
				var self = this;
				$.getJSON(sSource, aoData, function (json) {
					var wrapper = self.closest('.dataTables_wrapper');
					if (json && json.aaData.length) {
						for (var i = 0, size = json.aaData.length; i < size; i++) {
							json.aaData[i][3] = '<a class="delete_user_comment" data-comment-id="' + json.aMeta[i].id + '"><i class="wm-icon-trash icon-large muted"></i></a>';
						}
						fnCallback(json);
						wrapper.show().next().hide();
					} else {
						wrapper.hide().next().show();
					}
				});
			}
		});
	}
});

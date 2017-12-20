'use strict';

import $ from 'jquery';
import _ from 'underscore';
import Backbone from 'backbone';
import WorkUploadListCollection from './work_upload_list_collection';
import PaginationView from '../pagination/pagination_view';
import wmModal from '../funcs/wmModal';
import '../dependencies/jquery.tmpl';

export default Backbone.View.extend({
	tagName: 'div',
	template: $('#tmpl-preview').template(),

	initialize: function () {

		this.$el.html($.tmpl(this.template, {}));

		this.pagination = new PaginationView({
			el: this.$('.pagination'),
			limit: 1,
			total: this.collection.length
		});
		this.pagination.bind('pagination:next', this.render, this);
		this.pagination.bind('pagination:previous', this.render, this);

		this.render();
	},

	render: function () {
		this.pagination.render();

		if (this.pagination.getCurrent() === 25) {
			this.$('.next').addClass('disabled');
		} else if (this.pagination.getCurrent() > 25) {
			this.pagination.showPage(25);
		}

		if (this.options.response.uploadCount > 25) {
			this.$('#preview_limited').show();
		}

		var upload = this.collection.at(this.pagination.getCurrent() - 1);
		var values = _.filter(upload.get('values'), function (item) {
			return item.type.code !== 'ignore';
		});
		var html = this.$('#mapping-preview');

		html.empty();
		_.each(values, function (item) {
			if (item.type.code === 'template_id') {
				return;
			}

			$('#tmpl-preview-row').tmpl({
				key: this.options.parentView.allFields[item.type.code],
				value: item.value,
				newValue: !item.fromTemplate
			}).appendTo(html);
		}, this);

		return this;
	},

	show: function () {
		wmModal({
			autorun: true,
			title: 'Assignment Upload Preview',
			destroyOnClose: true,
			content: $(this.$el).html()
		});

		this.pagination.delegateEvents();
	},

	updateCollection: function (values) {
		this.collection = new WorkUploadListCollection(values);
	}
});

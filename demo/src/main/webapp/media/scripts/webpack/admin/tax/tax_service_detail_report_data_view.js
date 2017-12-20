'use strict';

import $ from 'jquery';
import _ from 'underscore';
import Backbone from 'backbone';

export default Backbone.View.extend({
	el: '#tax_service_detail_published',
	itemTemplate: $('#tax_service_detail_report_published_row_template').html(),

	events: {},

	initialize: function () {
		_.bindAll(this, 'addItem');
		this.render();
	},

	render: function () {
		this.$('.report_table').empty();
		_.each(this.options.taxServiceReportDetailSets, this.addItem);

		return this;
	},

	addItem: function (rpt) {
		var item = $.tmpl(this.itemTemplate, rpt);
		this.$('.report_table').append(item);
	}
});

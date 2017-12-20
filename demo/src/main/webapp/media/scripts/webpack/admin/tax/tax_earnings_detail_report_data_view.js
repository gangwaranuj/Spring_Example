'use strict';

import $ from 'jquery';
import _ from 'underscore';
import Backbone from 'backbone';
import '../../dependencies/jquery.tmpl';

export default Backbone.View.extend({
	el: '#earnings_detail_published',
	itemTemplate: $('#earnings_detail_report_published_row_template').html(),

	events: {},

	initialize: function () {
		_.bindAll(this, 'addItem');
		this.render();
	},

	render: function () {
		this.$('.report_table').empty();
		_.each(this.options.earningReportDetailSets, this.addItem);

		return this;
	},

	addItem: function (rpt) {
		var item = $.tmpl(this.itemTemplate, rpt);
		this.$('.report_table').append(item);
	}
});

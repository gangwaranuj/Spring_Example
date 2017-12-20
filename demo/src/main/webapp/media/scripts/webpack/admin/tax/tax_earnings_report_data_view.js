'use strict';

import $ from 'jquery';
import _ from 'underscore';
import Backbone from 'backbone';

export default Backbone.View.extend({
	el: '#earnings_published',
	itemTemplate: $('#earnings_report_published_row_template').html(),

	events: {},

	initialize: function () {
		_.bindAll(this, 'addItem');
		this.render();
	},

	render: function () {
		this.$('.report_table').empty();
		_.each(this.options.earningReportSets, this.addItem);

		return this;
	},

	addItem: function (rpt) {
		var item = $.tmpl(this.itemTemplate, rpt);
		this.$('.report_table').append(item);
	}
});

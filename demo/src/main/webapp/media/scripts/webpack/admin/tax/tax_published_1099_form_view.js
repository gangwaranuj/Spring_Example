'use strict';

import $ from 'jquery';
import _ from 'underscore';
import Backbone from 'backbone';

export default Backbone.View.extend({
	el: '#form1099_published',
	itemTemplate: $('#tax_form_published_row_template').html(),

	events: {},

	initialize: function () {
		_.bindAll(this, 'addItem');
		this.render();
	},

	render: function () {
		this.$('.form_table').empty();
		_.each(this.options.taxReportSets, this.addItem);

		return this;
	},

	addItem: function (rpt) {
		var item = $.tmpl(this.itemTemplate, rpt);
		this.$('.form_table').append(item);
	}
});

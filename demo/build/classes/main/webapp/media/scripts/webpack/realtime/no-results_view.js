'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import '../dependencies/jquery.tmpl';

export default Backbone.View.extend({
	tagName: 'tr',
	template: $('#tmpl-noresults_row').template(),

	render: function () {
		$(this.el).html($.tmpl(this.template));
		return this;
	}
});

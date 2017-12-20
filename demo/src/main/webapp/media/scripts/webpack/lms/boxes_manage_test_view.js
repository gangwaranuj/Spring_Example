'use strict';

import $ from 'jquery';
import Backbone from 'backbone';

export default Backbone.View.extend({
	template: $('#box_template').html(),

	render: function () {
		$(this.el).html($.tmpl(this.template, this.model.attributes));
		return this.el;
	}
});

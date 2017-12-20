'use strict';

import Backbone from 'backbone';
import Template from './templates/invoice-row-noresults.hbs';

export default Backbone.View.extend({
	tagName: 'tr',
	template: Template,

	render: function () {
		this.$el.html(this.template());
		return this;
	}
});

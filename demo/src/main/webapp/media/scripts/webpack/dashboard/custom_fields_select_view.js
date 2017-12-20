'use strict';

import Backbone from 'backbone';
import _ from 'underscore';
import Template from './templates/bulk_custom_field_select.hbs';

export default Backbone.View.extend({
	tagName: 'div',
	className: 'lineitem',
	render: function () {
		var options = this.model.defaultValue.split(/\s*,\s*/);
		this.$el.html(Template({
			model: _.extend({ options: options }, this.model)
		}));

		return this;
	}
});

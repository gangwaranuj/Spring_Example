'use strict';

import Backbone from 'backbone';
import Template from './templates/bulk_custom_field_input.hbs';

export default Backbone.View.extend({
	render: function () {
		this.$el.html(Template({
			model: this.model
		}));

		return this;
	}
});


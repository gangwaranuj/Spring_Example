'use strict';

import Backbone from 'backbone';
import _ from 'underscore';
import Template from '../account/templates/vendor-search-status.hbs';

export default Backbone.View.extend({
	el: '.vendor-search-status',
	template: Template,
	events: {
		'click .vendor-search-status--list'   : 'list',
		'click .vendor-search-status--remove' : 'remove'
	},

	initialize: function () {
		this.listenTo(this.model, 'change', this.render);
		this.model.fetch();
	},

	render: function () {
		var model = _.extend({ canList: this.model.canList() }, this.model.toJSON());
		this.$el.html(this.template(model));
	},

	list: function () {
		this.model.save({ isInVendorSearch: true });
	},

	remove: function () {
		this.model.save({ isInVendorSearch: false });
	}
});

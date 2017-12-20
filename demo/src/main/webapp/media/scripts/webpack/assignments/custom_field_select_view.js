'use strict';

import Template from './templates/details/custom_field_select.hbs';
import Backbone from 'backbone';
import _ from 'underscore';

export default Backbone.View.extend({
	tagName: 'tr',
	className: 'lineitem',
	render: function () {
		var options = this.model.defaultValue.split(/\s*,\s*/);

		this.$el.html(Template({
			model: _.extend({ options: options }, this.model),
			isRequiredCustomField: this.model.isRequired && ((this.model.isActiveResource && this.model.type === 'resource') || (this.model.isAdmin && this.model.type === 'owner')),
			isReadOnly: this.model.readOnly || (this.model.isActiveResource && this.model.type === 'owner'),
			isRequiredAndTypeWorker: this.model.isRequired && ((this.model.isActiveResource || this.model.isAdmin) && this.model.type === 'resource'),
			isAdminRequiredAndWorkerField: this.model.isAdmin && this.model.isRequired && this.model.type === 'resource'
		}));
		return this;
	}
});

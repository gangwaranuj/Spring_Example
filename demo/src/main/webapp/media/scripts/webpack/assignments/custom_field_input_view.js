'use strict';

import Template from './templates/details/custom_field_input.hbs';
import $ from 'jquery';
import Backbone from 'backbone';

export default Backbone.View.extend({
	tagName: 'tr',
	className: 'lineitem',
	render: function () {
		this.$el.html(Template({
			model: this.model,
			isRequiredCustomField: this.model.isRequired && ((this.model.isActiveResource && this.model.type === 'resource') || (this.model.isAdmin && this.model.type === 'owner')),
			isDisabled: (this.model.readOnly || (this.model.isActiveResource && this.model.type === 'owner')),
			isRequiredCustomFieldAndTypeWorker: this.model.isRequired && ((this.model.isActiveResource || this.model.isAdmin) && this.model.type === 'resource')
		}));

		if (/(http:\/\/|www\.)\S+/i.test(this.model.value)) {
			var link = this.model.value.replace(/.*?:\/\//g, '');
			this.$el.find('a').removeClass('dn').attr('href','http://' + link);
		}
		return this;
	}
});

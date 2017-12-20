'use strict';

import Template from './templates/select-options.hbs';
import _ from 'underscore';
import OnboardView from './onboard_view';
import wmFloatLabel from '../funcs/wmFloatLabel';

export default OnboardView.extend({
	template: Template,
	events: _.defaults({
		'change': 'setData'
	}, OnboardView.prototype.events),

	initialize: function () {
		this.listenTo(this.collection, 'add', this.render);
		this.listenTo(this.collection, 'remove', this.render);
		this.listenTo(this.collection, 'change', this.hideError);

		this.render();
	},

	render: function () {
		this.$el.html(this.template({
			options: this.collection,
			isSomeChecked: _.some(this.collection.pluck('checked')),
			selectedOption: this.collection.isOptional ? this.collection.defaultOption + ' (optional)' : this.collection.defaultOption
		}));

		// Trigger all the floating label animations for populated fields
		wmFloatLabel({ root: this.el, selector: null });

		return this;
	},

	setData: function (event) {
		var selectedOption = $(event.currentTarget);
		this.collection.each(function (option) {
			if (option.get('value') === selectedOption.val()) {
				option.set('checked', true);
			} else {
				option.set('checked', false, { silent: true });
			}
		});

		// Prevent event bubbling
		return false;
	}
});

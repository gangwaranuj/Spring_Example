'use strict';

import _ from 'underscore';
import Template from './templates/phones.hbs';
import OnboardCollectionView from './onboard_collection_view';
import wmSelect from '../funcs/wmSelect';
import wmFloatLabel from '../funcs/wmFloatLabel';

export default OnboardCollectionView.extend({
	el: '.phones',
	template: Template,
	events: _.defaults({
		'input input[name="number"]': 'setData',
		'change [data-onboarding]': 'setData'
	}, OnboardCollectionView.prototype.events),

	initialize: function () {
		this.render();
	},

	render: function () {
		this.collection.disabledPhoneTypes = _.intersection(this.collection.phoneTypes, this.collection.pluck('type'));
		this.$el.html(this.template({
			phones: this.collection.models,
			countryCodes: this.options.countryCodes
		}));

		// Trigger all the floating label animations for populated fields
		wmFloatLabel({ root: this.el });
		wmSelect({ root: this.el });
		this.trigger('render');
		return this;
	},

	setData: function (event) {
		const input = this.$(event.target);
		const index = input.parents('.phone').index('.phone');
		const model = this.collection.at(index);
		const name = input.attr('name');
		const value = _.isUndefined(input.data('mask')) ? input.val() : input.cleanVal();

		model.set(name, value);
		model.trigger('change');
	}
});

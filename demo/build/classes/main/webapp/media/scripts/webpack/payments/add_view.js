'use strict';

import Backbone from 'backbone';
import _ from 'underscore';
import calculateAssignmentPrice from './calculate_assignment_price';
import wmSelect from '../funcs/wmSelect';
import wmMaskInput from '../funcs/wmMaskInput';
import AddTemplate from './templates/add.hbs';

export default Backbone.View.extend({
	template: AddTemplate,
	events: {
		'change [name]' : 'setValue'
	},

	initialize() {
		this.render();
	},

	render() {
		const data = this.model.toJSON();
		const { mode } = data;
		const { fee, companyPrice, workerPrice } = calculateAssignmentPrice(data);
		_.extend(data, {
			companyPrice: companyPrice.toFixed(2),
			workerPrice: workerPrice.toFixed(2),
			fee: fee * 100,
			isCompanyMode: mode === 'company',
			isWorkerMode: mode === 'worker'
		});
		this.$el.html(this.template(data));

		const root = this.el;
		wmSelect({ root });
		wmMaskInput({ root }, 'usd');
	},

	setValue(event) {
		let { name, value } = event.target;
		name = name.replace(/^payments-/,'');

		this.model.set(name, value);
		this.render();
	},

	save(event) {
		event.preventDefault();

		const json = this.$el.serializeArray().reduce((memo, input) => {
			const { name, value } = input;
			memo[name] = value;
			return memo;
		}, {});

		this.model.save(json);
	}
});

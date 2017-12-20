import Backbone from 'backbone';
import EmployeeModel from '../settings/employee_model';

export default Backbone.View.extend({
	el: '#hide-pricing',

	events: {
		change: 'save'
	},

	initialize () {
		this.model = new EmployeeModel();
		this.listenTo(this.model, 'sync', this.render);
		this.model.fetch();
	},

	render () {
		this.$el.prop('checked', this.model.get('hidePricing'));
		return this;
	},

	save () {
		this.model.save({ hidePricing: this.$el.prop('checked') });
	}
});

import Backbone from 'backbone';

export default Backbone.Model.extend({
	defaults: {
		active: true
	},

	comparator: function (requirementSet) {
		return requirementSet.get('name');
	}
});

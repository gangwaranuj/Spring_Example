import Backbone from 'backbone';

export default Backbone.Collection.extend({
	url: function () {
		return '/profile-edit/insurancelist?industry=' + this.industryId;
	},

	comparator: function (insurance) {
		return insurance.get('name');
	},

	parse: function (response) {
		// the root property of the json response is 'list'
		return response.list;
	}
});

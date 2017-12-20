import Backbone from 'backbone';

export default Backbone.Collection.extend({
	url: '/countries',
	comparator: country => country.get('name'),

	parse(response) {
		response.forEach(country => country.name = this.titleize(country.name));

		return response;
	},

	titleize(string='') {
		return String(string).toLowerCase().replace(/(?:^|\s)\S/g, c => c.toUpperCase());
	}
});

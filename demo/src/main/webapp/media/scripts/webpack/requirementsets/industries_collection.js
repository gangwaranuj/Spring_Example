import Backbone from 'backbone';

export default Backbone.Collection.extend({
	url: '/industries-list',

	comparator: function (industry) {
		return industry.get('name');
	}
});

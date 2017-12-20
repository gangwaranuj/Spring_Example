import Backbone from 'backbone';

export default Backbone.Collection.extend({
	url: '/documents',
	comparator: function(document) {
		return document.get('name');
	}
});

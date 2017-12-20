import Backbone from 'backbone';

export default Backbone.Collection.extend({
	url: '/group_memberships',
	comparator: function (group) {
		return group.get('id');
	}
});

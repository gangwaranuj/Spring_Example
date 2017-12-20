import Backbone from 'backbone';

export default Backbone.Model.extend({
	sync: Backbone.syncWithJSON,

	initialize (options) {
		this.options = options;
	},

	url () {
		return this.options.url;
	}
});

import Backbone from 'backbone';

export default Backbone.Model.extend({
	url: '/mmw/integration/save_web_hook',
	sync: Backbone.syncWithJSON
});

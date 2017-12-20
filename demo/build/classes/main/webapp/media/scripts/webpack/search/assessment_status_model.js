import Backbone from 'backbone';

export default Backbone.Model.extend({
	defaults: {
		passed     : 'PASSED',
		failed     : 'FAILED',
		invited    : 'INVITED',
		notInvited : 'NOT_INVITED'
	}
});

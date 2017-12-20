import Backbone from 'backbone';

export default Backbone.Model.extend({
	defaults: {
		member         : 'MEMBER',
		memberOverride : 'MEMBER_OVERRIDE',
		invited        : 'INVITED',
		declined       : 'DECLINED',
		pending        : 'PENDING',
		pendingFailed  : 'PENDING_FAILED'
	}
});


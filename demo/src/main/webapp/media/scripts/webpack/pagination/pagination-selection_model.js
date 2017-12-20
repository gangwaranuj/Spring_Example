'use strict';

import Backbone from 'backbone';
import AjaxSendInit from '../funcs/ajaxSendInit';

export default Backbone.Model.extend({
	initialize: function (options) {
		this.options = options;
		AjaxSendInit();
	},

	url: function () {
		return '/assignments/full_select_all?is_select_all=' + this.is_select_all;
	}
});

'use strict';

import Backbone from 'backbone';
import BoxesManageButtonsView from './boxes_manage_buttons_view';

export default Backbone.View.extend({
	initialize: function () {
		new BoxesManageButtonsView();
	}
});

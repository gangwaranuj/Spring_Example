'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import ajaxSendInit from '../../../funcs/ajaxSendInit';

export default Backbone.View.extend({
	el: '.venues',

	events: {
		'click input[type="checkbox"]' : 'selectVenue'
	},

	selectVenue: function (event) {
		ajaxSendInit();
		var checkbox = this.$(event.currentTarget),
			method = checkbox.prop('checked') ? '/add' : '/remove';

		$.post('/admin/manage/company/admissions/' + this.$el.data('company-id') + method, {
			venue: checkbox.val()
		});
	}
});

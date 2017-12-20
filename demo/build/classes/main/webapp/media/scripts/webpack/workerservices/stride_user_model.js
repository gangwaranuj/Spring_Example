'use strict';
import $ from 'jquery';
import Backbone from 'backbone';
import ajaxSendInit from '../funcs/ajaxSendInit';

export default Backbone.Model.extend({

	initialize() {
		ajaxSendInit();
	},

	checkActiveUser() {
		//see if a user has signed up as a partner for stride
		return $.get('/stride/user/active');
	},

	provisionUser() {
		//provision a new stride health user
		return $.post('/stride/user');
	},

	getStrideUserUrl() {
		return $.get('/stride/user/url');
	},

	getStrideGuideUrl() {
		return $.get('/stride/user/guideurl');
	},

	dismissPromo() {
		return $.post('/stride/user/promo', { dismissed: 1 });
	}
});


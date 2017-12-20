'use strict';
import $ from 'jquery';
import _ from 'underscore';
import Backbone from 'backbone';
import availableServices from './available-services';
import indexTemplate from './templates/index.hbs';
import stride from './templates/stride.hbs';
import intuit from './templates/intuit.hbs';
import sterlingBackgroundCheck from './templates/sterling-background-check.hbs';
import sterlingDrugTest from './templates/sterling-drug-screen.hbs';
import insureon from './templates/insureon.hbs';
import simplilearn from './templates/simplilearn.hbs';
import comptia from './templates/comptia.hbs';
import incorporate from './templates/incorporate.hbs';
import careeracademy from './templates/careeracademy.hbs';
import ibm from './templates/ibm.hbs';
import p1099 from './templates/p1099.hbs';

export default Backbone.View.extend({
	el: '#worker-services-hub-content',

	initialize(options) {
		if (!(_.isUndefined(options)) && !(_.isUndefined(options.service))) {
			this.template = this.assignTemplate(options.service);
			this.mode = 'detail';
		} else {
			this.template = indexTemplate;
			this.mode = 'list';
		}
		this.data = {
			availableServices
		};
		if (config.email) {
			this.data.email = config.email;
		}
		this.render();
	},
	
	assignTemplate(service) {
		switch (service) {
			case 'stride':
				return stride;
			case 'intuit':
				return intuit;
			case 'sterling-background-check':
				return sterlingBackgroundCheck;
			case 'sterling-drug-screen':
				return sterlingDrugTest;
			case 'simplilearn':
				return simplilearn;
			case 'insureon':
				return insureon;
			case 'comptia':
				return comptia;
			case 'incorporate':
				return incorporate;
			case 'careeracademy':
				return careeracademy;
			case 'ibm':
				return ibm;
			case 'p1099':
				return p1099;
			case 'index':
				return indexTemplate;
		}
	},

	render() {
		this.$el.html(this.template(this.data));
		setTimeout(() => this.$('.worker-services').addClass('animate-in'));
	}
});


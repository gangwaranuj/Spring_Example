'use strict';

import Backbone from 'backbone';
import _ from 'underscore';

export default Backbone.Model.extend({
	sync: Backbone.syncWithJSON,
	urlRoot: '/profile',
	idAttribute: 'userNumber',

	url: function() {
		var url = this.urlRoot + '/' + this.get('userNumber');
		if (this.get('isVendor')) {
			url += "?isVendor='true'";
		}
		return url;
	},

	parse: function (response) {
		_.defaults(response, {
			allScorecard: { values: [] },
			companyScorecard: { values: [] }
		});

		// Compile some score card data from the controller payload
		response.scoreCard = {
			abandoned: getValues('ABANDONED_WORK'),
			cancelled: getValues('CANCELLED_WORK'),
			paidAssignments: getValues('COMPLETED_WORK'),
			deliverables: getValues('DELIVERABLE_ON_TIME_PERCENTAGE'),
			onTime: getValues('ON_TIME_PERCENTAGE'),
			satisfaction: getValues('SATISFACTION_OVER_ALL'),
			paidAssignmentsForCompany: response.paidassignforcompany
		};

		// Satisfaction is returned to us as a decimal, we need it as a percentage
		_.chain(response.scoreCard)
			.pick('satisfaction', 'onTime', 'deliverables')
			.each(function (value) {
				value.all.all = Math.round(value.all.all * 100);
				value.all.net90 = Math.round(value.all.net90 * 100);
				value.company.all = Math.round(value.company.all * 100);
				value.company.net90 = Math.round(value.company.net90 * 100);
			});

		return response;

		function getValues(property) {
			if (response.allScorecard) {
				return {
					all: _.pick(response.allScorecard.values[property], 'all', 'net90'),
					company: _.pick(response.companyScorecard.values[property], 'all', 'net90')
				};
			} else {
				return {
					company: _.pick(response.companyScorecard.values[property], 'all', 'net90')
				}
			}
		}
	},

	toJSON: function (){
		var json = Backbone.Model.prototype.toJSON.call(this),
			// Calculate profile's random background
			bkg = 'bkg-' + (Math.floor(Math.random() * 4) + 1),
			publicGroups  = json.facade.publicGroups || [],
			privateGroups = json.facade.privateGroups || [];

		_.extend(json, {
			profileBackground: bkg,
			totalGroups: publicGroups.length + privateGroups.length
		});

		return json;
	}
});

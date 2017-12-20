'use strict';

import Backbone from 'backbone';
import TeamView from '../search/team_view';
import TeamCollection from '../search/team_collection';

export default Backbone.View.extend({

	events: {
		'click' : 'showTeam'
	},

	showTeam: function () {
		if (!this.teamsLoaded) {
			var companyNumber = this.options.companyNumber,
				el = '#user-team-' + companyNumber;
			new TeamView({
				el: el,
				collection: new TeamCollection({
					companyId: this.options.companyId
				}),
				companyNumber: companyNumber
			});
			this.teamsLoaded = true;
		}
	}
});

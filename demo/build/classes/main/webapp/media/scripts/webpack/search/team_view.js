'use strict';

import Backbone from 'backbone';
import _ from 'underscore';
import Template from './templates/results_item.hbs';
import jdenticon from '../dependencies/jquery.jdenticon';

export default Backbone.View.extend({

	initialize: function () {
		this.listenTo(this.collection, 'reset', this.render);
		this.$table = this.$el.find('table');
		this.collection.fetch({reset: true});
	},

	render: function () {
		var profileCardTemplate = Template;

		this.collection.each(function (user) {
			user = user.toJSON();

			_.defaults(user, {
				isExistingWorker            : '',
				isDeclinedWorker            : '',
				isAppliedWorker             : '',
				isInvitedVendor             : false,
				isDeclinedVendor            : false,
				mode                        : '',
				pricing_type                : '',
				work_number                 : '',
				group_id                    : '',
				searchModes                 : '',
				memberStatus                : '',
				assessmentStatuses          : '',
				isGroupAdmin                : false,
				isDispatch                  : false,
				isVendor                    : false,
				disableActions              : true,
				user                        : user,
				publicWorkers               : false,
				lastWork                    : false,
				assignToFirstWorker         : false,
				eligibility                 : '',
				avatarAssetUri              : this.stripProtocol(user.avatar_asset_uri),
				isUsa                       : user.country === 'United States' || user.country === 'USA',
				isValidEmail                : user.lane === (0,1,2,3),
				orUserNumberOrCompanyNumber : user.userNumber || user.companyNumber,
				firstGroup                  : (user.groups !== null && typeof user.groups !== 'undefined') ? user.groups[0] : '',
				firstCompanyAssessment      : (user.company_assessments !== null && typeof user.company_assessments !== 'undefined') ? user.company_assessments[0] : '',
				firstCertifications         : (user.certifications !== null && typeof user.certifications !== 'undefined') ? user.certifications[0] : '',
				firstLicenses               : (user.licenses !== null && typeof user.licenses !== 'undefined') ? user.licenses[0] : '',
				isCheckboxBlocked           : true
			});

			user.isVendor = false;
			this.$el.append(profileCardTemplate(user));
		}, this);

		jdenticon();
	},

	stripProtocol: function (str) {
		if (str) {
			str = str.replace('http://', '//');
			str = str.replace('https://', '//');
		}

		return str;
	}
});

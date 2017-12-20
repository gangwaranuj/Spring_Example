'use strict';

import OnboardModel from './onboard_model';
import ProfileInfoModel from './profile_info_model';
import LocationModel from './location_model';
import IndustriesModel from './industries_model';
import GetStartedModel from './get_started_model';

export default OnboardModel.extend({
	initialize: function (attrs, options) {
		this.profileInfo = new ProfileInfoModel(attrs, options);
		this.location = new LocationModel(attrs, options);
		this.industries = new IndustriesModel(attrs, options);
		this.getStarted = new GetStartedModel(attrs, options);
		this.id = attrs.id;
	}
});

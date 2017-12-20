'use strict';

import _ from 'underscore';
import OnboardCollection from './onboard_collection';
import SkillModel from './skill_model';

export default OnboardCollection.extend({
	model: SkillModel,

	isValid: function () {
		return true;
	}
});

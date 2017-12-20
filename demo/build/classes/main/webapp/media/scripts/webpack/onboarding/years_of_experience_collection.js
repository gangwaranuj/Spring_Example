'use strict';

import OnboardCollection from './onboard_collection';
import YearsOfExperienceModel from './years_of_experience_model';

export default OnboardCollection.extend({
	defaultOption: 'Years of Work Experience',
	isOptional: true,
	model: YearsOfExperienceModel
});

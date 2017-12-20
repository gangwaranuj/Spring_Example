'use strict';

import Backbone from 'backbone';
import DeliverableRequirementModel from './deliverable_requirement_model';

export default Backbone.Collection.extend({
	model: DeliverableRequirementModel
});

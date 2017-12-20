'use strict';

import Backbone from 'backbone';
import Model from './rule_model';

export default Backbone.Collection.extend({
	model: Model
});

'use strict';

import Backbone from 'backbone';
import Model from './work_upload_model'; 

export default Backbone.Collection.extend({
	model: Model
});

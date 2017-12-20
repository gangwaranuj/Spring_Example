'use strict';

import Backbone from 'backbone';

export default Backbone.Model.extend({
	defaults: {
		0 : 'Internal Only',
		1 : 'Employees',
		2 : 'Contractors',
		3 : 'Third Parties',
		4 : 'Everyone Else'
	}
});

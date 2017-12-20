'use strict';

import _ from 'underscore';

export default(type) => {
	let extensions = ['gif', 'jpeg', 'jpg', 'png', 'bmp', 'tiff'];
	return _.contains(extensions, type);
};

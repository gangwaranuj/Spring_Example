import $ from 'jquery';
import '../funcs/ago';

export default (createdOn) => {
	'use strict';

	return $.ago(new Date(createdOn).valueOf(), $.now())
}

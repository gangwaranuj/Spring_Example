import _ from 'underscore';
import 'underscore.inflection';

export default (word, count) => {
	'use strict';
	return _.pluralize(word, count);
};

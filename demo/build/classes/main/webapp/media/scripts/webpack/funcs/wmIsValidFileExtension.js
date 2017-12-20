'use strict';

import _ from 'underscore';
import isValidImageExtension from './wmIsValidImageExtension';

export default (type) => {

	var extensions = [
		'txt',
		'csv',
		'pdf',
		'xls',
		'xlsx',
		'xlsm',
		'doc',
		'docx',
		'docm',
		'form',
		'mp4',
		'm4v',
		'f4v',
		'mov',
		'flv',
		'm4a',
		'f4a',
		'mp3',
		'zip'
	];
	return (_.contains(extensions, type) || isValidImageExtension(type));
};

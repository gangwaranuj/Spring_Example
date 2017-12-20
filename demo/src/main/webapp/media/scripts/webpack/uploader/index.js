'use strict';

import Application from '../core';
import UploaderRouter from './uploader_router';
import UploaderView from './uploader_view';
import BulkSendView from './bulk_send_view';

Application.init({ name: 'uploader', features: config }, () => {});

if (config.mode === 'setup') {
	new UploaderRouter();
} else if (config.mode === 'mapping') {
	new UploaderView({
		response: config.response,
		initialFieldTypes: config.initialFieldTypes,
		initialFieldCategories: config.initialFieldCategories
	});
} else {
	new BulkSendView({
		ids: config.ids
	});
}
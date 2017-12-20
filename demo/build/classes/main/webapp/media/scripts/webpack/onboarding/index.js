'use strict';

import MainModel from './main_model';
import MainView from './main_view';

export default new MainView({
	model: new MainModel({
		id: config.profileId
	})
});

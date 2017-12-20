'use strict';

import MainView from './main_view';
import MainModel from './main_model';
import AddRoutingContainer from './containers/add_routing';
import RoutingReducer from './reducers/routing';

class Main {
	constructor({ el, properties, workNumber }) {
		const model = new MainModel(properties);
		this.view = new MainView({ el, model, workNumber });
	}

	getRoutingObject() {
		return {
			routing: {
				groupIds              : this.view.$(this.view.routingGroupIds).val(),
				assignToFirstGroupIds : this.view.$(this.view.assignToFirstGroupIds).val(),
				needToApplyGroupIds   : this.view.$(this.view.routingGroupIds).val(),
				resourceIds           : this.view.$(this.view.routingResourceIds).val(),
				companyIds            : this.view.$(this.view.routingCompanyIds).val(),
				showInFeed            : this.view.$(this.view.showInFeed).is(':checked'),
				assignToFirstTalent   : this.view.$(this.view.assignToFirstTalent).is(':checked'),
				assignToFirstGroup    : this.view.$(this.view.assignToFirstGroup).is(':checked'),
				assignToFirstVendor   : this.view.$(this.view.assignToFirstVendor).is(':checked'),
				assignToFirstGlobal   : this.view.$(this.view.assignToFirstGlobal).is(':checked'),
				sendType              : this.view.$(this.view.sendType + ':checked').val(),
				bulkPublish           : this.view.$(this.view.bulkPublish + ':checked').val()
			}
		};
	}

	setInternalPricing(value) {
		this.view.model.set("internalPricing", value);
	}
}

export default {
	Main,
	AddRoutingContainer,
	RoutingReducer
};

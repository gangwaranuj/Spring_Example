import Backbone from 'backbone';
import CSRFToken from './csrf';

export default function (method, model, options) {
	Backbone.sync.call(this, method, model, Object.assign({}, options, {
		emulateJSON: false,
		emulateHTTP: true,
		headers: {
			'X-CSRF-Token': CSRFToken
		}
	}));
}

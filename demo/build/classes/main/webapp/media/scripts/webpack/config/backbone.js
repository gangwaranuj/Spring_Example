import Backbone from '../../../../../../../node_modules/backbone/backbone.js';
import _ from 'underscore';

'use strict';

// Compatibility override - Backbone 1.1 got rid of the automatic 'options' binding
// on Views - we need to keep that.
Backbone.View = (function (View) {
	return View.extend({
		constructor: function (options) {
			this.options = options || {};
			View.apply(this, arguments);
		}
	});
})(Backbone.View);

Backbone.Collection = (function (Collection) {
	return Collection.extend({
		fetch: function (options) {
			return Collection.prototype.fetch.call(this, _.extend(options, { reset: true }));
		}
	});
})(Backbone.Collection);

Backbone.syncWithJSON = function (method, model, options) {
	Backbone.sync.call(this, method, model, _.extend(options, { emulateJSON: false }));
};

// Taken from TrackJS
// https://docs.trackjs.com/Examples/Integrating_with_Backbone
(function trackJs() {
	'use strict';

	if (!window.trackJs) {
		return;
	}

	['View','Model','Collection','Router'].forEach(function (klass) {
		var Klass = Backbone[klass];
		Backbone[klass] = Klass.extend({
			constructor: function () {
				// NOTE: This allows you to set _trackJs = false for any individual object
				//       that you want excluded from tracking
				if (typeof this._trackJs === 'undefined') {
					this._trackJs = true;
				}

				if (this._trackJs) {
					// Additional parameters are excluded from watching. Constructors and Comparators
					// have a lot of edge-cases that are difficult to wrap so we'll ignore them.
					window.trackJs.watchAll(this, 'model', 'constructor', 'comparator');
				}

				return Klass.prototype.constructor.apply(this, arguments);
			}
		});
	});
})();

export default Backbone;

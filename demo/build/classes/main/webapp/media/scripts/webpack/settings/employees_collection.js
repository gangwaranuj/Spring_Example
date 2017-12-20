import Backbone from 'backbone';
import _ from 'underscore';
import Model from '../onboarding/user_model';

export default Backbone.Collection.extend({
	sync: Backbone.syncWithJSON,
	model: Model,
	url: '/user',

	/* Upon adding a user to the collection, we want to fetch its data from the
	 * server. We also don't want to call a fetch if the user is already in the
	 * collection, that means we already have its data.
	 */
	add (models, options) {
		const previousLength = this.length;
		const newModels = Backbone.Collection.prototype.add.call(this, models, options);
		const byDefaultShouldFetch = _.isUndefined(options) || _.isUndefined(options.fetch);
		const explicitlyShouldFetch = !_.isUndefined(options) && options.fetch;
		const didResetModels = !_.isUndefined(options) && !_.isUndefined(options.previousModels);
		const didResetNewModels = didResetModels && this.length !== options.previousModels.length;
		const didAddNewModels = !didResetModels && this.length !== previousLength;
		const shouldFetchModels = byDefaultShouldFetch || explicitlyShouldFetch;
		const shouldFetchNewModels = (didResetNewModels || didAddNewModels) && shouldFetchModels;

		if (shouldFetchNewModels || explicitlyShouldFetch) {
			if (_.isArray(newModels)) {
				this.fetch({
					data: { userNumbers: _.pluck(newModels, 'id') },
					traditional: true
				});
			} else {
				newModels.fetch();
			}
		}

		return newModels;
	}
});

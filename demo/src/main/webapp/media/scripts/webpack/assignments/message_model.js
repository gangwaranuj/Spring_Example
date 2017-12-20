'use strict';

import Backbone from 'backbone';
import _ from 'underscore';
import Users from './users_collection';
import UserModel from '../user/user_model';
import moment from 'moment';

let Model = Backbone.Model.extend({
	sync: function (method, model, options) {
		Backbone.sync.call(this, method, model, _.extend(options, { emulateJSON: false, emulateHTTP: false }));
	},

	defaults: function () {
		return {
			creator: new UserModel(),
			responses: [],
			isQuestion: false
		};
	},

	constructor: function (models, options) {
		return Backbone.Model.prototype.constructor.call(this, models, _.defaults(options, { parse: true }));
	},

	parse: function (response) {
		response = _.omit(response, _.isNull);

		if (!_.isUndefined(response.creatorNumber)) {
			// If there already exists a User with this ID, then `add` will return
			// that User, otherwise the User will be created and added to the
			// collection.
			response.creator = Users.add({ id: response.creatorNumber });

			// If the User changes at all, trigger a change event on this model so
			// that any views can update.
			this.listenTo(response.creator, 'change', function () { this.trigger('change'); });
		}

		if (!_.isUndefined(response.onBehalfUserNumber)) {
			// If there already exists a User with this ID, then `add` will return
			// that User, otherwise the User will be created and added to the
			// collection.
			response.onBehalfUser = Users.add({ id: response.onBehalfUserNumber });

			// If the User changes at all, trigger a change event on this model so
			// that any views can update.
			this.listenTo(response.onBehalfUser, 'change', function () { this.trigger('change'); });
		}

		if (!_.isUndefined(response.createdOn)) {
			response.createdOnDate = moment(response.createdOn).format('M/DD/YY h:mma');
			response.createdOn = moment(response.createdOn).unix();
		}

		if (!_.isUndefined(response.responses)) {
			response.responses = _.map(response.responses, function (reply) {
				var response = new Model(reply, { parse: true });
				this.listenTo(response, 'change', function () { this.trigger('change:reply'); });
				return response;
			}, this);
		}

		return response;
	},

	validate: function (attributes) {
		var isContentEmpty = !this.get('isQuestion') && (_.isUndefined(attributes.content) || attributes.content === '') && !this.get('content'),
			isQuestionEmpty = this.get('isQuestion') && (_.isUndefined(attributes.question) || attributes.question === '') && !this.get('content'),
			isAnswerEmpty = attributes.answer === '';

		if (!attributes.isPublic && !attributes.isPrivileged && !attributes.isPrivate) {
			return 'Message needs a privacy level.';
		}

		if (isContentEmpty || isQuestionEmpty || isAnswerEmpty) {
			return 'Message cannot be empty.';
		}
	},

	toJSON: function () {
		var json = Backbone.Model.prototype.toJSON.call(this);
		json.creator = json.creator.toJSON();
		json.responses = _.map(json.responses, function (response) { return response.toJSON(); });

		if (json.onBehalfUser) {
			json.onBehalfUser = json.onBehalfUser.toJSON();
		}

		return json;
	}
});

export default Model;

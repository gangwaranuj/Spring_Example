import Backbone from 'backbone';
import Application from '../core';

export default Backbone.Model.extend({
	sync: Application.Sync,
	url: '/employee_settings'
});

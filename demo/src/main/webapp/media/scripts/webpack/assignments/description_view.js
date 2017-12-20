import Backbone from 'backbone';
import $ from 'jquery';

export default Backbone.View.extend({

	initialize (config) {
		this.fetchAndRender(config);
	},
	fetchAndRender (config) {
		$.ajax({
			url: `/assignments/details/${config.workNumber}/description`,
			success (data) {
				$('#description_div').html(data);
			}
		});
	}
});

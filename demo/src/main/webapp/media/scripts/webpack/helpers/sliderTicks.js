import Handlebars from 'handlebars';
import _ from 'underscore';

export default (context, options) => {
	'use strict';
	let numberOfSteps = (context.max - context.min) / context.step <= 10 ? (context.max - context.min) / context.step : 10,
		ticksWidth = 100 / numberOfSteps,
		data;
	if (options.data) {
		data = Handlebars.createFrame(options.data);
	}
	_.extend(data, {
		ticksWidth: ticksWidth,
		numberOfSteps: numberOfSteps + 1,
		arrayOfTicks: _.range(numberOfSteps + 1),
		ticksMargin: ticksWidth / -2
	});
	return options.fn(context, { data: data });
};

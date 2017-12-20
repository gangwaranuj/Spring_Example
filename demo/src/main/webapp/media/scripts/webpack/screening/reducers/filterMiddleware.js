import fields from '../fields';

const filterMiddleware = store => next => action => {
	if (action.name && action.value) {
		const filter = fields[action.name].filter;
		if (filter) {
			action.value = filter(action.value);
		}
	}

	return next(action);
};

export default filterMiddleware;

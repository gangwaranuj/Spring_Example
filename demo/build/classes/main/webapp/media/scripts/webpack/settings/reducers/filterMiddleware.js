/* eslint-disable no-param-reassign, no-unused-vars */
import all from '../fields';

const filterMiddleware = store => next => (action) => {
	if (action.name && action.value) {
		const filter = all[action.name] ? all[action.name].filter : undefined;
		if (filter) {
			action.value = filter(action.value, store.getState().tax.toJS());
		}
	}

	return next(action);
};

export default filterMiddleware;

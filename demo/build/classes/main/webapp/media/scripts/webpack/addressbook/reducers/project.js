import { UPDATE_PROJECT_ID } from '../constants/locationActionTypes';

const projectId = (state = null, { type, value }) => {
	switch (type) {
	case UPDATE_PROJECT_ID:
		return value;
	default:
		return state;
	}
};

export default projectId;

import { ADD_DOCUMENT, UPDATE_DOCUMENT, REMOVE_DOCUMENT } from '../constants/actionTypes';

const documents = (state = [], action) => {
	switch (action.type) {
		case ADD_DOCUMENT:
			return [
				...state,
				{
					id: action.id,
					uuid: action.uuid,
					name: action.name,
					mime_type: action.mime_type,
					visibilityType: action.visibilityType,
					description: action.description,
					uploaded: action.uploaded
				}
			];
		case UPDATE_DOCUMENT:
			return state.map(doc =>
				doc.uuid === action.uuid ?
					Object.assign({}, doc, {
						visibilityType: action.visibilityType ? action.visibilityType : doc.visibilityType,
						description: action.description ? action.description : doc.description
					}) :
					doc
				);
		case REMOVE_DOCUMENT:
			return state.filter(doc =>
				doc.uuid !== action.uuid
			);
		default:
			return state;
	}
};

export default documents;

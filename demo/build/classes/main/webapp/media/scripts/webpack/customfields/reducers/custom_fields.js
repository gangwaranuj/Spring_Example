import {
	ADD_CUSTOM_FIELDS,
	RECEIVE_CUSTOM_FIELDS,
	UPDATE_CUSTOM_FIELD_VALUE,
	UPDATE_CUSTOM_FIELD_DEFAULT,
	REMOVE_CUSTOM_FIELD_GROUP,
	TOGGLE_CUSTOM_FIELD_REQUIRED
} from '../constants/actionTypes';

const customFieldGroups = (state = [], action) => {
	switch (action.type) {
		case ADD_CUSTOM_FIELDS:
			return [
				...state,
				{
					id: action.id,
					name: action.name,
					required: action.required,
					position: state.length,
					fields: action.fields
				}
			];
		case RECEIVE_CUSTOM_FIELDS:
			return [
				...state,
				{
					id: parseInt(action.customFieldGroup.id, 10),
					name: action.customFieldGroup.name,
					required: action.customFieldGroup.required,
					position: state.length,
					fields: action.customFields
				}
			];
		case UPDATE_CUSTOM_FIELD_VALUE:
		case UPDATE_CUSTOM_FIELD_DEFAULT: {
			let updatedField = (action.type === UPDATE_CUSTOM_FIELD_DEFAULT) ? { defaults: action.value } : { value: action.value },
				groupToUpdate = state.find(fieldGroup =>
					fieldGroup.id === action.groupId
				);

			groupToUpdate.fields = groupToUpdate.fields.map(field =>
				field.id === action.fieldId ?
					Object.assign({}, field, updatedField) :
					field
			);

			return state.map(fieldGroup =>
				fieldGroup.id === groupToUpdate.id ?
					Object.assign({}, fieldGroup, groupToUpdate) :
					fieldGroup
			);
		}
		case REMOVE_CUSTOM_FIELD_GROUP:
			return state.filter(fieldGroup =>
				fieldGroup.id !== action.groupId
			).map((fieldGroup, index) =>
				Object.assign({}, fieldGroup, { position: index })
			);
		case TOGGLE_CUSTOM_FIELD_REQUIRED:
			return state.map((fieldGroup, index) =>
					index === action.index ?
					Object.assign({}, fieldGroup, { required: !fieldGroup.required }) : fieldGroup
			);
		default:
			return state;
	}
};

export default customFieldGroups;

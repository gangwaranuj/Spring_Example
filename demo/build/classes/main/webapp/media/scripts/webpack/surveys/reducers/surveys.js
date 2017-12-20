'use strict';
import { ADD_SURVEY, REMOVE_SURVEY, TOGGLE_REQUIRED } from '../constants/actionTypes';

const surveys = (state = [], action) => {
		switch (action.type) {
			case ADD_SURVEY:
				return [
					...state,
					{
						id: action.id,
						name: action.name,
						required: false
					}
				];

			case REMOVE_SURVEY:
				return state.filter((survey, index) =>
					index !== action.index
				);

			case TOGGLE_REQUIRED:
				return state.map((survey, index) =>
						index === action.index ?
						Object.assign({}, survey, { required: !survey.required }) : survey
				);

			default:
				return state;
		}
};

export default surveys;

'use strict';
import * as types from '../constants/actionTypes';

export const updateFollowerIds = (value) => {
	return {
		type: types.UPDATE_FOLLOWER_IDS,
		followerIds: value
	};
};

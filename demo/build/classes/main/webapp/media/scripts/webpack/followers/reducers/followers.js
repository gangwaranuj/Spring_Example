'use strict';
import Redux from 'redux';
import Actions from '../actions/followers';
import { UPDATE_FOLLOWER_IDS } from '../constants/actionTypes';

const followerIds = (state = [], action) => {
	switch (action.type) {
		case UPDATE_FOLLOWER_IDS:
			return action.followerIds;
		default:
			return state;
	}
};

export default followerIds;

'use strict';

import { connect } from 'react-redux';
import Component from '../components/add_followers';
import { updateFollowerIds } from '../actions/followers';

const mapStateToProps = ({ followerIds }) => {
	return { followerIds };
};

const mapDispatchToProps = (dispatch) => {
	return {
		updateFollowerIds: (value) => dispatch(updateFollowerIds(value))
	};
};

export default connect(mapStateToProps, mapDispatchToProps)(Component);

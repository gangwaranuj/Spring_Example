'use strict';

import Application from '../../core';
import React from 'react';
import ReactDOM from 'react-dom';
import wmEmployeeList from '../../funcs/wmEmployeeList';

export default class AddFollowersComponent extends React.Component {
	componentDidMount() {
		const root = ReactDOM.findDOMNode(this);
		const selector = '[name="followerIds"]';
		const { companyId } = Application.UserInfo;
		this.followerList = wmEmployeeList({ root, selector, companyId }, {
			onChange: this.props.updateFollowerIds
		})[0].selectize;
	}

	componentDidUpdate() {
		this.followerList.clear(true);
		this.followerList.setValue(this.props.followerIds, true);
	}

	render() {
		return (
			<div className="assignment-creation--container">
				<label className="assignment-creation--label" htmlFor="followerIds-list">Followers</label>
				<div className="assignment-creation--field">
					<select id="followerIds-list" className="wm-select" name="followerIds" multiple></select>
				</div>
			</div>
		);
	}
}

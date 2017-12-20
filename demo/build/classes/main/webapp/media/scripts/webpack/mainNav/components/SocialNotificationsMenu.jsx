import React, { Component } from 'react';
import fetch from 'isomorphic-fetch';
import moment from 'moment';
import _ from 'underscore';
import $ from 'jquery';
import IconMenu from 'material-ui/IconMenu';
import MenuItem from 'material-ui/MenuItem';
import Divider from 'material-ui/Divider';
import utils from '../utilities';
import SocialNotificationIconButtonWithAlert from './SocialNotificationIconButtonWithAlert';
import AjaxSendInit from '../../funcs/ajaxSendInit';

export default class SocialNotificationsMenu extends Component {
	constructor (props) {
		super(props);
		this.state = {
			notifications: [],
			unreadCount: 0,
			unreadStartId: 0,
			unreadEndId: 0
		};
	}

	componentDidMount () {
		this.loadNotifications();
		this.getUnreadNotificationCount();
	}

	loadNotifications () {
		fetch('/notifications/list', {
			credentials: 'same-origin'
		}).then(utils.checkStatus)
			.then(res => res.json())
			.then((unprocessedNotifications) => {
				const parseHref = str => {
					const matches = str.match(/href="([^"]*)/);
					return matches ? matches[1] : "";
				};
				const preparedNotifications = unprocessedNotifications.map((item) => {
					return {
						primaryText: item.display_message.replace(/<[^>]*>/g, ''),
						href: parseHref(item.display_message),
						createdOn: item.created_on,
						viewed: item.viewed
					};
				});
				this.setState({ notifications: preparedNotifications });
			});
	}

	getUnreadNotificationCount () {
		fetch('/notifications/unread_notifications', {
			credentials: 'same-origin'
		}).then(utils.checkStatus)
			.then(res => res.json())
			.then((res) => {
				if (res.data !== undefined && !_.isUndefined(res.data.notifications)) {
					const unreadNotificationsInfo = res.data.notifications;
					this.setState({ unreadCount: unreadNotificationsInfo.unreadCount });
					this.setState({ unreadStartId: unreadNotificationsInfo.startUuid });
					this.setState({ unreadEndId: unreadNotificationsInfo.endUuid });
				} else {
					this.setState({ unreadCount: 0 });
					this.setState({ unreadStartId: 0 });
					this.setState({ unreadEndId: 0 });
				}
			});
	}

	updateViewedNotifications () {
		if (this.state.unreadStartId !== 0 && this.state.unreadEndId !== 0) {
			AjaxSendInit();
			$.ajax({
				type: 'POST',
				url: '/notifications/all_viewed',
				dataType: 'json',
				data: {
					startUuid: this.state.unreadStartId,
					endUuid: this.state.unreadEndId
				},
				success: () => {
					this.setState({ unreadStartId: 0 });
					this.setState({ unreadEndId: 0 });
					this.setState({ unreadCount: 0 });
				}
			});
		}
	}

	render () {
		return (
			<IconMenu
				width={ 360 }
				menuStyle={ { fontFamily: 'Open Sans', zIndex: 1200, maxHeight: '470px', overflow: 'auto' } }
				iconButtonElement={
					<SocialNotificationIconButtonWithAlert
						onClick={ this.updateViewedNotifications.bind(this) }
						newNotificationCount={ this.state.unreadCount || 0 }/> }
				anchorOrigin={ { horizontal: 'right', vertical: 'top' } }
				targetOrigin={ { horizontal: 'right', vertical: 'top' } }
			>
				<MenuItem
					primaryText="Notifications"
					secondaryText={ (
						<a style={ { fontFamily: 'Open Sans', color: '#1890e0' } } href="/notifications/active">
							View All
						</a>
					) }
					disabled={ true }
					style={ { color: '#646b6f' } }
				/>

				{this.state.notifications.length ?
					this.state.notifications.map((item, index) => (
						<div key={ index }>
							<Divider
								style={ { width: '100%' } }
							/>
							<MenuItem
								style={ { fontFamily: 'Open Sans', color: '#646b6f', lineHeight: '28px', whiteSpace: 'normal', paddingTop: '10px' } }
								href={ item.href }
								primaryText={
									<div>
										<p style={ { lineHeight: '20px', marginBottom: '0px', fontFamily: 'Open Sans' } } dangerouslySetInnerHTML={ { __html: item.primaryText } } />
										<small style={ item.viewed ? { fontFamily: 'Open Sans', color: '#8D9092' } : { fontFamily: 'Open Sans', color: '#F79626' } } className="meta">{!item.viewed && <i className="icon-circle" id="notification_status"></i>} {moment(item.createdOn).fromNow()}</small>
									</div>
							}
							/>
						</div>)
					) :
					<div>
						<Divider
							style={ { width: '100%' } }
						/>
						<MenuItem
							style={ { fontFamily: 'Open Sans', color: '#646b6f' } }
							primaryText={
							(<span style={ { fontFamily: 'Open Sans' } }>
								No new notifications. <br />You may configure your notifications settings
									<a href="/mysettings/notifications"> here</a>.
							</span>) }
						/>
					</div>
				}
			</IconMenu>
		)
			;
	}
}

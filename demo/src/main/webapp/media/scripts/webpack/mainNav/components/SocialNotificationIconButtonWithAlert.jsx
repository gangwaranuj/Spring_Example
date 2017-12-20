import React, { Component } from 'react';
import Radium from 'radium';
import IconButton from 'material-ui/IconButton';
import SocialNotifications from 'material-ui/svg-icons/social/notifications';

class SocialNotificationIconButtonWithAlert extends Component {
		render() {
			const styles = {
				span: {
						display: 'inline-block',
						position: 'absolute',
						backgroundColor: '#3e91e0',
						border: 'solid 1px #ffffff',
						fontFamily: 'OpenSans-Semibold',
						fontSize: '10px',
						color: '#ffffff',
						lineHeight: '1.1',
						borderRadius: '14px',
						marginLeft: '-16px',
						padding: '1px 3px'
					}
			};

			return (
				<div>
					<IconButton {...this.props} style={ { verticalAlign: 'middle' } }>
						<SocialNotifications color='#ffffff' />
					</IconButton>
					<span style={styles.span} >
						{
							this.props.newNotificationCount
						}
					</span>
				</div>
			);
		}
}

export default SocialNotificationIconButtonWithAlert = Radium(SocialNotificationIconButtonWithAlert);

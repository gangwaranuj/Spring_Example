import React, { Component } from 'react';
import Radium from 'radium';
import utils from '../utilities';
import IconMenu from 'material-ui/IconMenu';
import MenuItem from 'material-ui/MenuItem';
import IconButton from 'material-ui/IconButton';
import SocialPerson from 'material-ui/svg-icons/social/person';
import Divider from 'material-ui/Divider';
import fetch from 'isomorphic-fetch';
import $ from 'jquery';
import wmNotify from '../../funcs/wmNotify';

class SocialUserAvatar extends Component {
	render() {

		const styles = {
			img: {
				position: "absolute",
				marginTop: "-17px",
				marginLeft: "-17px",
				width: "32px",
				height: "32px",
				border: "solid 0.5px #cecece"
			}
		};

		return (
			this.props.avatar ?
				<img style={styles.img}
				src={this.props.avatar}/> :
				<SocialPerson color={'#ffffff'} />
			);
	}
}

class SocialUserMenu extends Component {

	updatePersona(type) {
		let params = {
			seller: type === 'perform_work',
			buyer: type === 'create_work',
			dispatcher: type === 'dispatch_work'
		};
		$.ajax({
			url: `/profile-edit/persona-toggle/${type}`,
			type: 'post',
			data: params,
			beforeSend: (xhr) => {
				xhr.setRequestHeader('X-CSRF-TOKEN', utils.getCSRFToken());
			},
			success: () => {
				window.location = '/home';
			}
		});
	}

	render() {
		const styles = {
			menuItem: {fontFamily: 'Open Sans', color: '#646b6f'},
			menuItemActive: {fontFamily: 'Open Sans', color: '#F79626'}
		};

		return (
			<IconMenu
				iconButtonElement={
					<IconButton style={ { verticalAlign: 'middle' } }>
						<SocialUserAvatar avatar={this.props.user.smallAvatarUri || 0} />
					</IconButton>
				}
				anchorOrigin={{horizontal: 'right', vertical: 'top'}}
				targetOrigin={{horizontal: 'right', vertical: 'top'}} >
				<MenuItem
					disabled={true}
					primaryText={this.props.user.email || 'your@email.here'} />
				<Divider />
				{this.props.user.userMenu.map((item,index) => (
					item.divider ? <Divider /> : (
						item.personatype ? (
								item.personatype === 'perform_work' ?
								<div key={index} >
									<MenuItem
										onClick={this.updatePersona.bind(this, item.personatype)}
										primaryText={item.title }
										style={this.props.user.seller ? styles.menuItemActive : styles.menuItem} />
								</div>
								:
								item.personatype === 'create_work' ?
									<div key={index} >
										<MenuItem
											onClick={this.updatePersona.bind(this, item.personatype)}
											primaryText={item.title}
											style={this.props.user.buyer ? styles.menuItemActive : styles.menuItem} />
									</div>
									:
									<div key={index} >
										<MenuItem
											onClick={this.updatePersona.bind(this, item.personatype)}
											primaryText={item.title}
											style={this.props.user.dispatcher ? styles.menuItemActive : styles.menuItem} />
									</div>
							)
							:
							<MenuItem
								key={index}
								href={item.href}
								primaryText={item.title}
								style={styles.menuItem} />
						)
					)
				)}
			</IconMenu>
		);
	}
}

export default SocialUserMenu = Radium(SocialUserMenu);

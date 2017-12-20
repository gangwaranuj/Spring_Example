import React, { Component } from 'react';
import Radium from 'radium';
import AppBar from 'material-ui/AppBar';
import Drawer from 'material-ui/Drawer';
import LeftNavMenuGroup from './LeftNavMenuGroup';
import RightNavMenuGroup from './RightNavMenuGroup';
import IconButton from 'material-ui/IconButton';
import ContentClear from 'material-ui/svg-icons/content/clear';
import Snackbar from 'material-ui/Snackbar'
import LinearProgress from 'material-ui/LinearProgress';
import utils from '../utilities';
import $ from 'jquery';
import _ from 'underscore';
import AjaxSendInit from '../../funcs/ajaxSendInit';

class Navigation extends Component {
	constructor(props){
		super(props);
		this.state = {
			open: props.config.isNavPinnedOpen
		};
	}

	componentDidMount() {
		/*Oh Jquery Monolith Shimmy Shim Shim Mounting Shim*/
		$(document).ready(() => this.jQueryContentMoveShim(this.props.config.isNavPinnedOpen));
	}

	handleToggleNavMenu() {
		this.setState({open: !this.state.open });
		AjaxSendInit();
		$.post('/nav/set_preferences', { open: !this.state.open });
		this.jQueryContentMoveShim(!this.state.open);
	}

	jQueryContentMoveShim(isOpen) {
		/*Oh Jquery Monolith Shimmy Shim Shim */
		if (isOpen) {
			$('#outer-container').css('margin-left','220px');
			$('.site-footer').css('margin-left','220px');
		} else {
			$('#outer-container').css('margin-left','0px');
			$('.site-footer').css('margin-left','0px');
		}
	}

	render() {
		const styles = {
			Drawer: { position: 'fixed', top: '64px', zIndex: '1200', paddingBottom: '64px', bottom: '0', height: 'auto' },
			AppBar: { backgroundColor: '#f7961d', position: 'fixed' },
			LeftNavMobileHeader: {height: '64px', backgroundColor: '#f7961d', paddingTop: '8px'},
			LeftNavMobileHeaderLogo: { paddingTop: '7px',paddingLeft: '14px', float: 'left' },
			LeftNavMobileHeaderIcon: { float: 'right' },
			logo: {
				position: 'absolute',
				top: '50%',
				transform: 'translateY(-50%)'
			}
		};

		return (
			<div>
				<Drawer
					docked={true}
					open={this.state.open}
					width={220}
					containerStyle={styles.Drawer} >
					<LeftNavMenuGroup  navMenuItems={this.props.config.navMenuItems} activePage={this.props.config.breadcrumbPage} />
				</Drawer>
				<AppBar
					title={
						<a href='/home'>
							<img
								alt="Work Market Logo"
								src={ this.props.config.logoURI }
								style={ styles.logo }
							/>
						</a>
					}
					style={styles.AppBar}
					iconElementRight={<RightNavMenuGroup user={this.props.config.currentUser} />}
					onLeftIconButtonTouchTap={this.handleToggleNavMenu.bind(this)} />
				<WMUploader status={this.props.config.uploadStatus} message={this.props.config.uploadMessage}/>

			</div>
		);
	}
}

class WMUploader extends Component {
	constructor(props){
		super(props);
		this.state = {
			status: props.status
		};
	}
	componentDidMount() {
		this.checkStatus();
	}
	checkStatus() {
			$.getJSON('/uploadProgress/progress', (res) => {
				if (res && res.data && !_.isUndefined(res.data.uploadProgress)) {
					setTimeout(()=> {
						this.setState({status: res.data.uploadProgress});
					}, 2000)

					if (res.data.uploadProgress === 1) {
						setTimeout(() => {
							this.setState({status: 0});
						}, 12000)
					}
				} else {
					setTimeout(this.checkStatus, 5000);
				}
			});
	}
	render() {
		if (this.props.status > 0 && this.props.status < 1) {
			return (
				<Snackbar style={{zIndex: '1200'}} bodyStyle={{backgroundColor:'rgba(0, 0, 0, 0.74)'}}
					open={!!this.state.status}
					message={
						this.state.status === 1 ?
							<div>
								<span>{this.props.message.done.title}</span>
								<span style={{paddingLeft:'20px'}}>
									<a href={this.props.message.done.link.href}> {this.props.message.done.link.text}</a>
								</span>
							</div> :
							<div>
								<span>{this.props.message.start.title}</span>
								<LinearProgress style={{
										position: 'absolute',
										top: '24px',
										width: '150px',
										margin: '0 0 0 96px'
									}}
									color='#F79626'
									mode="indeterminate"
									/>
							</div>
					}
					/>
			)
		}
		return null
	}
}

export default Navigation = Radium(Navigation);

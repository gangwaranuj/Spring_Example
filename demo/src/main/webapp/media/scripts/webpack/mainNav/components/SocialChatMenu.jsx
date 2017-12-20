import React, { Component } from 'react';
import IconMenu from 'material-ui/IconMenu';
import MenuItem from 'material-ui/MenuItem';
import IconButton from 'material-ui/IconButton';
import HardwareHeadsetMic from 'material-ui/svg-icons/hardware/headset-mic';

export default class SocialChatMenu extends Component {
	render(){
		const styles = {
			item: {fontFamily: 'Open Sans', color: '#646b6f'}
		};
		return (
			<IconMenu
				iconButtonElement={<IconButton style={ { verticalAlign: 'middle' } }><HardwareHeadsetMic color={'#ffffff'} /></IconButton>}
				anchorOrigin={{horizontal: 'right', vertical: 'top'}}
				targetOrigin={{horizontal: 'right', vertical: 'top'}} >
				<MenuItem
					href="javascript:void(0);"
					className="chat-action"
					onClick={()=> SnapEngage && SnapEngage.startChat('How can I help you today?')}
					primaryText="Chat with us"
					style={styles.item} />
				<MenuItem
					href="mailto:support@workmarket.com"
					primaryText="support@workmarket.com"
					style={styles.item} />
				<MenuItem
					href="tel:+12122299675"
					primaryText="212-229-WORK (9675)"
					style={styles.item} />
				<MenuItem
					href="https://workmarket.zendesk.com/hc/en-us"
					target="_blank"
					primaryText="Help Center"
					style={styles.item} />
			</IconMenu>
		);
	}
}

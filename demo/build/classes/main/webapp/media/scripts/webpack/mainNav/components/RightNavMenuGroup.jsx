import React, { Component } from 'react';
import SearchBar from './SearchBar';
import SocialChatMenu from './SocialChatMenu';
import SocialNotificationsMenu from './SocialNotificationsMenu';
import SocialUserMenu from './SocialUserMenu';
import OrgModeSelector from './OrgModeSelector';
import IconButton from 'material-ui/IconButton';
import NavigationMoreVert from 'material-ui/svg-icons/navigation/more-vert';
import utils from '../utilities';

export default class RightMenuGroup extends Component {
	render() {
		return ( <div>
			{ utils.browser.isSmallMobile() ?
				/* Need design for this */
				<div>
					<SearchBar />
					<IconButton><NavigationMoreVert color={'#ffffff'} /></IconButton>
				</div> :
				<div>
					<SearchBar />
			        <SocialChatMenu />
					<SocialNotificationsMenu />
			  	    <SocialUserMenu user={ this.props.user } />
					{ this.props.user.orgModes && this.props.user.orgModes.length > 0 &&
						<OrgModeSelector orgModes={ this.props.user.orgModes } value={ this.props.user.savedOrgMode } />
					}
				</div>
			}
		</div>);
	}
}

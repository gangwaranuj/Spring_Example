import React, { Component } from 'react';
import {
	commonStyles,
	WMPaper,
	WMFontIcon,
	WMModal,
} from '@workmarket/front-end-components';

const videoLink = 'https://player.vimeo.com/video/194086972';
const { colors: { baseColors } } = commonStyles;
const styles = {
	container: {
		display: 'flex',
		border: '2px solid #53b3f3',
		padding: '0 25px 0 20px',
		alignItems: 'center',
		height: '60px',
		margin: '0 0 20px'
	},
	infoIcon: {
		width: '22px',
		height: '22px',
		fontSize: '22px',
		cursor: 'pointer',
		margin: '0 10px 0 0'
	},
	playContainer: {
		margin: '0 0 0 auto',
		display: 'flex',
		alignItems: 'center',
		color: baseColors.lightBlue,
		cursor: 'pointer'
	},
	playIcon: {
		width: '22px',
		height: '22px',
		fontSize: '22px',
		cursor: 'pointer',
		margin: '0 10px 0 10px'
	}
};

class UnifiedSearchBanner extends Component {
	constructor () {
		super();
		this.state = {
			modalOpen: false
		};
	}

	showVideo () {
		this.setState({
			modalOpen: true
		});
	}

	render () {
		const { modalOpen } = this.state;
		return (
			<div>
				<WMPaper
					rounded
					style={ styles.container }
				>
					<WMFontIcon
						id="unified-search-banner__info-icon"
						className="material-icons"
						color="#53b3f3"
						style={ styles.infoIcon }
					>
						info_outline
					</WMFontIcon>
					<span>
						{
							'Welcome to the new search experience. ' +
							'Find all your talent in one place by searching or filtering below.'
						}
					</span>
					<div
						onClick={ () => this.setState({ modalOpen: true }) }
						style={ styles.playContainer }
					>
						<WMFontIcon
							id="unified-search-banner__play-icon"
							className="material-icons"
							color="#53b3f3"
							style={ styles.playIcon }
						>
							play_circle_outline
						</WMFontIcon>
						<span>
							{ 'LEARN MORE (0:59)' }
						</span>
					</div>
				</WMPaper>
				<WMModal
					open={ modalOpen }
					onRequestClose={ () => this.setState({
						modalOpen: false
					}) }
				>
					<iframe
						src={ videoLink }
						width="640"
						height="360"
						frameBorder="0"
						allowFullScreen
					/>
				</WMModal>
			</div>
		);
	}
}

export default UnifiedSearchBanner;

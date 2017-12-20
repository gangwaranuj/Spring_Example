import PropTypes from 'prop-types';
import React, { Component } from 'react';
import { WMModal } from '@workmarket/front-end-components';
import WMHomeTile from '../WMHomeTile';
import WMOnboardingProgressBar from '../WMOnboardingProgressBar';
import content from './content';
import styles from './styles';

class WMHome extends Component {
	constructor () {
		super();
		this.state = {
			modalOpen: false,
			videoURL: ''
		};
	}

	showVideo = (videoLink) => {
		this.setState({
			modalOpen: true,
			videoURL: videoLink
		});
	}

	render () {
		const { modalOpen, videoURL } = this.state;
		const { showProgressBar } = this.props;
		return (
			<div>
				{ showProgressBar && (
				<WMOnboardingProgressBar
					showVideo={ this.showVideo }
				/>
				) }
				<div
					style={ styles.container }
				>
					{ content.map((tileContent, index) => {
						const {
							icon,
							title,
							contentCopy,
							buttonText,
							buttonLink,
							videoLink,
							videoTime,
							tileLink
						} = tileContent;

						return (
							<WMHomeTile
								key={ index } // eslint-disable-line react/no-array-index-key
								icon={ icon }
								title={ title }
								contentCopy={ contentCopy }
								buttonText={ buttonText }
								buttonLink={ buttonLink }
								videoLink={ videoLink }
								videoTime={ videoTime }
								showVideo={ this.showVideo }
								tileLink={ tileLink }
							/>
						);
					}) }
				</div>

				<WMModal
					open={ modalOpen }
					onRequestClose={ () => this.setState({
						modalOpen: false,
						videoURL: ''
					}) }
				>
					{ videoURL && (
						<iframe
							title={ `${videoURL}` }
							src={ videoURL }
							width="640"
							height="360"
							frameBorder="0"
							allowFullScreen
						/>
					) }
				</WMModal>
			</div>
		);
	}
}

export default WMHome;

WMHome.propTypes = {
	showProgressBar: PropTypes.bool.isRequired
};

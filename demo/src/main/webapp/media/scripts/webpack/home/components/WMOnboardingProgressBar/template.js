/* eslint-disable react/require-default-props */
import PropTypes from 'prop-types';
import React, { Component } from 'react';
import { Map } from 'immutable';
import {
	WMPaper,
	WMFontIcon,
	WMRaisedButton,
	WMLinearProgress
} from '@workmarket/front-end-components';
import styles from './styles';

const videoLink = 'https://player.vimeo.com/video/187984990';

class WMOnboardingProgressBar extends Component {
	componentWillMount () {
		const { getOnboardingProgress } = this.props;
		getOnboardingProgress();
	}

	render () {
		const { showVideo } = this.props;
		const progress = this.props.onboardingProgress.get('progress');
		// don't show the bar if progress hasn't been received yet or if onboarding is complete
		// progress cannot be 0
		if (progress < 100 && progress > 0) {
			return (
				<WMPaper
					rounded
					style={ styles.container }
				>
					<div
						style={ Object.assign({}, styles.row, styles.topRow) }
						onClick={ () => showVideo(videoLink) }
					>
						<WMFontIcon
							id={ 'home__play-icon-welcome' }
							className="material-icons"
							color="#53b3f3"
							style={ styles.videoIcon }
						>
							play_circle_outline
						</WMFontIcon>
						<div
							style={ styles.welcomeText }
						>
							WELCOME TO WORK MARKET
						</div>
					</div>
					<div
						style={ styles.row }
					>
						<div
							id="home__onboardingPercentageCompleteText"
							style={ styles.progressText }
						>
							{ 'Your account configuration is ' }
							<span style={ styles.progressPercentageText }>{progress}% Complete</span>
						</div>
						<WMLinearProgress
							value={ progress }
							mode="determinate"
							style={ styles.progressBar }
						/>
						<WMRaisedButton
							href="/settings/onboarding"
							label="COMPLETE SETUP"
							backgroundColor="#5BC75D"
							labelColor="#FFFFFF"
							linkButton
							style={ styles.button }
						/>
					</div>
				</WMPaper>
			);
		}
		return (<div />);
	}
}

export default WMOnboardingProgressBar;

WMOnboardingProgressBar.propTypes = {
	getOnboardingProgress: PropTypes.func.isRequired,
	onboardingProgress: PropTypes.instanceOf(Map),
	showVideo: PropTypes.func.isRequired
};

/* eslint-disable react/require-default-props */
import PropTypes from 'prop-types';
import React from 'react';
import {
	commonStyles,
	WMCard,
	WMCardText,
	WMFontIcon,
	WMRaisedButton
} from '@workmarket/front-end-components';
import styles from './styles';

const { baseColors } = commonStyles.colors;

const WMHomeTile = ({
	icon,
	title,
	contentCopy,
	buttonText,
	buttonLink,
	videoLink,
	videoTime,
	showVideo,
	tileLink
}) => {
	return (
		<WMCard
			style={ styles.card }
		>
			<WMCardText>
				<div
					style={ styles.cardTextInner }
				>
					<div
						onClick={ () => window.open(tileLink, '_self') }
						style={ styles.tileClickableArea }
					>
						<img
							src={ icon }
							alt={ title }
							style={ styles.icon }
						/>
						<div
							style={ styles.cardTitle }
						>
							{ title }
						</div>

						<div
							style={ styles.cardSeparator }
						/>

						<div
							style={ styles.cardCopy }
						>
							{ contentCopy }
						</div>
					</div>

					{ typeof buttonLink === 'string' ? (
						<WMRaisedButton
							id={ `home__${title}-button` }
							backgroundColor="#53b3f3"
							label={ buttonText }
							labelColor={ baseColors.white }
							labelStyle={ { paddingLeft: 0, paddingRight: 0 } }
							linkButton={ typeof buttonLink === 'string' }
							href={ buttonLink }
							style={ styles.cardButton }
						/>
					) : (
						<WMRaisedButton
							backgroundColor="#53b3f3"
							label={ buttonText }
							labelColor={ baseColors.white }
							labelStyle={ { paddingLeft: 0, paddingRight: 0 } }
							linkButton={ false }
							style={ styles.cardButton }
							onClick={ () => {
								const clickAction = new buttonLink(); // eslint-disable-line
							} }
						/>
					) }
					{ videoLink && (
						<div
							style={ styles.videoLinkContainer }
							onClick={ () => showVideo(videoLink) }
						>
							<WMFontIcon
								id={ `home__play-icon-${title}` }
								className="material-icons"
								color="#53b3f3"
								style={ styles.videoIcon }
							>
								play_circle_outline
							</WMFontIcon>
							<div>
								{ `LEARN MORE (${videoTime})` }
							</div>
						</div>
					) }
				</div>
			</WMCardText>
		</WMCard>
	);
};

export default WMHomeTile;

WMHomeTile.propTypes = {
	icon: PropTypes.string.isRequired,
	title: PropTypes.string.isRequired,
	contentCopy: PropTypes.string,
	buttonText: PropTypes.string.isRequired,
	buttonLink: PropTypes.oneOfType([
		PropTypes.string.isRequired,
		PropTypes.func.isRequired
	]),
	videoLink: PropTypes.string,
	videoTime: PropTypes.string,
	showVideo: PropTypes.func,
	tileLink: PropTypes.string
};

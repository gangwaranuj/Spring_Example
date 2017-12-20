import React, { Component } from 'react';
import Radium from 'radium';
import IconButton from 'material-ui/IconButton';
import Menu from 'material-ui/Menu';
import MenuItem from 'material-ui/MenuItem';
import SvgIcon from 'material-ui/SvgIcon';
import NavigationClose from 'material-ui/svg-icons/navigation/close';

class LeftNavMenuGroup extends Component {

	render() {
		const styles = {
			heading: {
				style: {
					marginTop:'18px',
					lineHeight: '30px',
					maxHeight: '30px',
					height: '30px',
					minHeight: '30px',
					color: 'rgba(0, 0, 0, 0.5)',
					fontSize: '14px',
					fontFamily: 'Open Sans',
					fontWeight: 600,
					cursor: 'default',
					backgroundColor: 'transparent'
				},
				innerDivStyle: (href) => (
					location.pathname === href ?
						{
							paddingLeft: '66px',
							fontSize: '14px',
							fontFamily: 'Open Sans',
							color: '#f7961d'
						} :
						{
							paddingLeft: '66px',
							fontSize: '14px',
							fontFamily: 'Open Sans',
							color: '#8d9092'
						}
				),
				leftIconStyle: {
					marginTop: '0px',
					marginBottom: '0px',
					top: '1px',
					left: '8px'
				}
			},
			list: {
				itemStyle: {
					lineHeight: '25px',
					maxHeight: '25px',
					height: '25px',
					minHeight: '25px'
				},
				innerDivStyle: (href)=> (
					location.pathname === href ?
					{
						fontFamily: 'Open Sans',
						fontSize: '14px',
						color: '#f7961d',
						paddingLeft: '66px'
					} :
					{
						fontFamily: 'Open Sans',
						fontSize: '14px',
						color: '#8d9092',
						paddingLeft: '66px'
					}
				)
			}
		};

		if (this.props.navMenuItems.length < 1) {
			return <div> loading... </div>;
		}

		return (
			<div>{
				this.props.navMenuItems.map((el,index) => (
					<div key={index}>
						<MenuItem
							style={styles.heading.style}
							innerDivStyle={styles.heading.innerDivStyle(el.href)}
							href={el.href}
							leftIcon={
								<SvgIcon style={styles.heading.leftIconStyle}
									color="#8d9092">
									<path d={el.iconPath} />
								</SvgIcon>
							}
							primaryText={el.label} />
						{
							el.items.map((item) => (
								<MenuItem
									style={styles.list.itemStyle}
									innerDivStyle={styles.list.innerDivStyle(item.href)}
									href={item.href}
									key={item.title}
									primaryText={item.title} />
							))
						}
					</div>)
				)
			}
		</div>);
	}
}

export default LeftNavMenuGroup = Radium(LeftNavMenuGroup);

import React, { Component } from 'react';
import Radium from 'radium';
import utils from '../utilities';
import SearchIcon from 'material-ui/svg-icons/action/search';
import TextField from 'material-ui/TextField';
import IconButton from 'material-ui/IconButton';
import translate from '../../funcs/translation';

class SearchBar extends Component {
	constructor(props){
		super(props);
		this.state = {
			searchValue: "",
			displaySearch: false
		};
	}

	updateSearchValue(el) {
		this.setState({searchValue: el.target.value});
	}

	searchAssignments() {
		window.location = `/assignments/search?keyword=${this.state.searchValue}`;
	}

	onSearchIconClick() {
		this.state.searchValue ?
			this.searchAssignments() :
			this.setState({displaySearch: !this.state.displaySearch });
	}

	render() {
		const styles = {
			outerDiv: {float: "left"}
		};
		utils.browser.isSmallMobile() ?
			styles.TextField =	{
				style: { fontFamily: 'Open Sans', color: '#ffffff', width: '152px' },
				inputStyle: {color: '#ffffff', boxShadow: 'none', width: '152px'},
				underlineFocusStyle:{ borderColor: '#ffffff', width: '152px'}
			} :
			styles.TextField = {
				style: {
					color: '#ffffff'
				},
				inputStyle: {color:'#ffffff', boxShadow: 'none' },
				underlineFocusStyle: { borderColor: '#ffffff'},
				hintStyle: {fontFamily: 'Open Sans', size: '14px', color: '#FBCA8E'}
			};

		return (
			<div style={styles.outerDiv}>
				<IconButton onClick={this.onSearchIconClick.bind(this)} >
					<SearchIcon color={'#ffffff'} />
				</IconButton>
				{this.state.displaySearch ?
					<TextField
						hintText={utils.browser.isSmallMobile()?translate("translation.search"):translate("translation.search_my_assignments")}
						onKeyDown={(e)=> e.keyCode === 13 && this.searchAssignments()}
						onChange={(e)=> this.updateSearchValue(e)}
						inputStyle={styles.TextField.inputStyle}
						style={styles.TextField.style}
						hintStyle={styles.TextField.hintStyle}
						value={this.state.searchValue}
						underlineFocusStyle={styles.TextField.underlineFocusStyle}/> :
						<span></span>
				}
			</div>
		);
	}
}

export default SearchBar = Radium(SearchBar);

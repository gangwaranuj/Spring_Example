import fetch from 'isomorphic-fetch';
import PropTypes from 'prop-types';
import React from 'react';
import ReactDOM from 'react-dom';
import { WMModal, WMFormRow, WMTextField, WMFlatButton, WMMessageBanner, WMFontIcon } from '@workmarket/front-end-components';
import Application from '../../core';
import wmSelect from '../../funcs/wmSelect';
import ModuleSwitcher from './ModuleSwitcher';

export default class Creation extends React.Component {
	constructor (props) {
		super(props);

		this.state = {
			activeModuleId: 'basic',
			scrolledModuleId: '',
			nextModuleId: '',
			showModuleSwitcher: false,
			showAllOptionalModules: false,
			isRoutable: false,
			canSaveDraft: false,
			flipSwitcher: {
				flipped: false,
				position: 0
			}
		};

		this.state.modules = this.props.modules.map((module) => {
			module.isValid = false;
			return module;
		});

		// needed to determine whether to show module switcher
		this.canSwitchModules = Application.UserInfo.isAdmin || Application.UserInfo.isManager;

		this.loadTemplateData = props.loadTemplateData;

		this.toggleModule = this.toggleModule.bind(this);
		this.toggleOptionalModules = this.toggleOptionalModules.bind(this);
		this.receiveUserConfig = this.receiveUserConfig.bind(this);
		this.setModuleValidation = this.setModuleValidation.bind(this);
		this.toggleModuleSwitcher = this.toggleModuleSwitcher.bind(this);
	}

	componentDidMount () {
		const root = '.wm-modal--content';

		// retrieve user config from API
		this.getUserConfig();

		this.container = this.refs.mainContainer;

		this.initStickyHeaders();

		this.checkModuleHeight();

		this.numberOfCopies = wmSelect({ selector: '[name="assignment-creation--number-of-copies"]', root }, {
			onChange: (value) => {
				if (value) {
					const intVal = parseInt(value, 10);
					this.props.updateNumberOfCopies(intVal);
				}
			}
		})[0].selectize;
		this.templates = wmSelect({ selector: '[name="assignment-creation-template"]', root }, {
			valueField: 'id',
			searchField: ['id', 'name'],
			sortField: 'name',
			labelField: 'name',
			openOnFocus: true,
			preload: true,
			onChange: (templateId) => {
				this.props.fetchTemplate(templateId);
			},
			onLoad: () => {
				if (!this.templates.items[0]) {
					const templateId = this.props.templateInfo.id;
					this.templates.setValue(templateId);
				}
			},
			load: (query, callback) => fetch('/employer/v2/assignments/templates', {
				credentials: 'same-origin'
			})
				.then(res => res.json())
				.then(res => callback(res.results))

		})[0].selectize;

		if (this.props.scrollToTab) {
			this.scrollToModule(this.state.modules.find(module => module.id === this.props.scrollToTab));
		}
    }

	componentWillReceiveProps (nextProps) {
		const { templateInfo: { id } } = nextProps;
		if (id && this.templates
		&& (this.templates.items[0] !== id)) {
			this.templates.setValue(id, true);
		}
	}

	componentDidUpdate (prevProps, prevState) {
		const config = this.props.configuration;
		this.checkModuleHeight();
		if (config !== prevProps.configuration) {
			this.receiveUserConfig(config);
		}
		if (this.props.saveMode !== prevProps.saveMode) {
			this.props.updateSaveMode(this.props.saveMode);
		}
		if (this.props.title.length && this.props.description.length && !this.state.canSaveDraft) {
			const canSaveDraft = true;
			const saveDraftButton = document.getElementsByClassName('-save-draft')[0];

			saveDraftButton.classList.remove('-disabled');

			this.setState({ canSaveDraft });
		} else if (!this.props.title.length && !this.props.description.length && this.state.canSaveDraft) {
			const canSaveDraft = false;
			const saveDraftButton = document.getElementsByClassName('-save-draft')[0];

			this.setState({ canSaveDraft });
		}
	}

	getUserConfig () {
		fetch('/employer/v2/assignments/configuration', {
			credentials: 'same-origin'
		})
			.then(res => res.json())
			.then((res) => {
				this.receiveUserConfig(res.results[0]);
				this.props.updateUserConfig(res.results[0]);
			});
	}

	receiveUserConfig (userConfig) {
		const {
			deliverablesEnabled,
			surveysEnabled,
			customFieldsEnabled,
			shipmentsEnabled,
			requirementSetsEnabled,
			followersEnabled,
			documentsEnabled
		} = userConfig;
		const modules = this.state.modules.map((module) => {
			switch (module.id) {
			case 'followerIds':
				module.isEnabled = followersEnabled;
				break;
			case 'deliverablesGroup':
				module.isEnabled = deliverablesEnabled;
				break;
			case 'surveys':
				module.isEnabled = surveysEnabled;
				break;
			case 'customFieldGroups':
				module.isEnabled = customFieldsEnabled;
				break;
			case 'shipmentGroup':
				module.isEnabled = shipmentsEnabled;
				this.props.configuration.shipmentsEnabled = shipmentsEnabled;
				break;
			case 'requirementSetIds':
				module.isEnabled = requirementSetsEnabled;
				break;
			case 'documents':
				this.props.configuration.documentsEnabled = documentsEnabled;
				module.isEnabled = documentsEnabled;
				break;
			default:
				break;
			}
			return module;
		});

		this.setState({ modules });
	}

	initStickyHeaders () {
		const container = this.refs.mainContainer;

		const calcContainerScroll = () => {
			const containerPosition = container.getBoundingClientRect().top + 10;
			const parkedModule = this.state.scrolledModuleId.length ? container.querySelector(`#assignment-creation--${this.state.scrolledModuleId}`) : '';
			const activeModule = container.querySelector(`#assignment-creation--${this.state.activeModuleId}`);
			const nextModule = this.state.nextModuleId.length ? this.container.querySelector(`#assignment-creation--${this.state.nextModuleId}`) : activeModule.nextElementSibling;
			const headerHeight = 0;
			// detect when beginning to scroll down into next module
			if (activeModule && nextModule && nextModule.getBoundingClientRect().top - containerPosition <= headerHeight) {
				this.setState({ scrolledModuleId: activeModule.getAttribute('data-content') });
			}

			// detect when scrolled down into next module
			if (nextModule && nextModule.getBoundingClientRect().top - containerPosition <= 0) {
				this.setState({
					activeModuleId: nextModule.getAttribute('data-content'),
					nextModuleId: nextModule.nextElementSibling ? nextModule.nextElementSibling.getAttribute('data-content') : ''
				});
			}

			// detect when scrolling back up into previous module
			if (activeModule && activeModule.getBoundingClientRect().top - containerPosition > 0) {
				this.setState({
					nextModuleId: activeModule.getAttribute('data-content'),
					activeModuleId: ''
				});
			}

			if (parkedModule && parkedModule.getBoundingClientRect().top + parkedModule.offsetHeight - containerPosition > headerHeight) {
				this.setState({
					activeModuleId: parkedModule.getAttribute('data-content'),
					scrolledModuleId: parkedModule.previousElementSibling ? parkedModule.previousElementSibling.getAttribute('data-content') : ''
				});
			}
		};

		// remove any previous listeners
		// needed for when modules are added / removed
		this.container.removeEventListener('scroll', calcContainerScroll);

		calcContainerScroll();
		container.addEventListener('scroll', calcContainerScroll);
	}

	scrollToModule (module) {
		const { id, isEnabled } = module;
		if (isEnabled) {
			const container = this.container;
			const moduleEl = container.getElementsByClassName(`assignment-creation--${id}`)[0];
			const headerHeight = 58;

			function scrollTo (element, to, duration) {
				if (duration <= 0) {
					return;
				}
				let difference = to - element.scrollTop,
					perTick = difference / duration * 10;

				setTimeout(() => {
					element.scrollTop += perTick;
					if (element.scrollTop === to) {
								// this is to trigger the scroll event that sets new state for active modules
						element.scrollTop += 1;
						return;
					} else {
						scrollTo(element, to, duration - 10);
					}
				}, 10);
			}

			scrollTo(container, Math.round(container.scrollTop + moduleEl.getBoundingClientRect().top - container.getBoundingClientRect().top - headerHeight), 1000);
		} else {
			return;
		}
	}

	/**
	 * checks if the last module in the modal is tall enough to fill the container
	 */
	checkModuleHeight () {
		const modules = this.container.getElementsByClassName('assignment-creation--section');
		const lastModule = modules[modules.length - 1];
		const containerHeight = this.container.offsetHeight;
		const heightDiff = containerHeight - lastModule.offsetHeight + 10;

		if (heightDiff >= 0) {
			// last module needs padding to fill modal
			this.container.style.paddingBottom = `${heightDiff}px`;
		}
	}

	checkSwitcherPosition (viewportEl, addModuleTab) {
		const viewportHeight = viewportEl.offsetHeight;
		const addModuleTabHeight = addModuleTab.offsetHeight;
		const addModuleTabTopOffset = addModuleTab.getBoundingClientRect().top - viewportEl.getBoundingClientRect().top;
		const addModuleTabBottomOffset = viewportHeight - addModuleTabTopOffset - addModuleTabHeight;
		const SWITCHER_HEIGHT = 200;
		const SWITCHER_PADDING = 15;
		const shouldFlip = (SWITCHER_HEIGHT + SWITCHER_PADDING) > addModuleTabBottomOffset;

		let newPosition = 0;

		if (shouldFlip) {
			newPosition = addModuleTabHeight;
		}

		const newFlipSwitcher = {
			flipped: shouldFlip,
			position: newPosition
		};

		return newFlipSwitcher;
	}

	toggleModuleSwitcher () {
		let isEnabled = !this.state.showModuleSwitcher,
			prevFlipSwitcher = this.state.flipSwitcher,
			newFlipSwitcher;

		if (isEnabled) {
			let viewportEl = ReactDOM.findDOMNode(this),
				addModuleTab = viewportEl.querySelector('.assignment-creation--tab[data-content="#assignment-creation--moduleSwitcher"]');

			newFlipSwitcher = Object.assign({}, prevFlipSwitcher, this.checkSwitcherPosition(viewportEl, addModuleTab));
		} else {
			newFlipSwitcher = prevFlipSwitcher;
		}

		this.setState({
			showModuleSwitcher: isEnabled,
			flipSwitcher: newFlipSwitcher
		});
	}

	toggleModule (id) {
		const modules = this.state.modules.map((module) => {
			if (module.id === id) {
				module.isEnabled = !module.isEnabled;

				if (module.id === 'shipmentGroup') {
					const userConfiguration = this.props.configuration;
					userConfiguration.shipmentsEnabled = module.isEnabled;
					this.props.updateUserConfig(userConfiguration);
        }

				if (module.id === 'documents') {
					const userConfiguration = this.props.configuration;
					userConfiguration.documentsEnabled = module.isEnabled;
					this.props.updateUserConfig(userConfiguration);
        }

				if (module.id === 'surveys') {
					const userConfiguration = this.props.configuration;
					userConfiguration.surveysEnabled = module.isEnabled;
					this.props.updateUserConfig(userConfiguration);
        }

				if (module.id === 'deliverablesGroup') {
					const userConfiguration = this.props.configuration;
					userConfiguration.deliverablesEnabled = module.isEnabled;
					this.props.updateUserConfig(userConfiguration);
        }

				if (module.id === 'requirementSetIds') {
					const userConfiguration = this.props.configuration;
					userConfiguration.requirementSetsEnabled = module.isEnabled;
					this.props.updateUserConfig(userConfiguration);
        }

				return module;
			}
			return module;
		});
		const all = this.state.modules.map(module => module.isEnabled === false).indexOf(true) === -1;
		this.setState({ modules, showAllOptionalModules: all });
	}

	toggleOptionalModules () {
		let showAllOptionalModules = !this.state.showAllOptionalModules,
			modules = this.state.modules.map((module) => {
				if (module.optional && module.id !== 'recurrence') {
					module.isEnabled = showAllOptionalModules;
					return module;
				}
				return module;
			});
		this.setState({ modules, showAllOptionalModules, showModuleSwitcher: false });
	}

	checkIsRoutable (modules) {
		let validCount = 0,
			VALID_THRESHOLD = 4;

		for (const module of modules) {
			validCount = ((module.id === 'basic' || module.id === 'location' || module.id === 'schedule' || module.id === 'pricing' || module.id === 'routing') && module.isValid) ? validCount + 1 : validCount;
		}
		return validCount > VALID_THRESHOLD;
	}

	setModuleValidation (isValid, moduleId) {
		const moduleIndex = this.state.modules.findIndex((el) => {
			return el.id === moduleId;
		});

		// update the individual module in the state
		// we dont want to use setState here because there can be async setState calls
		// coming from many modules at once (e.g. when loading assignments)
		// setting all modules @ once with setState would rewrite all modules' validation state
		// the setState at the end of this function will take care of re-rendering
		this.state.modules[moduleIndex].isValid = isValid;

		const isRoutable = this.checkIsRoutable(this.state.modules);

		this.setState({ isRoutable });
	}

	render () {
		const moduleSwitcherStyle = {
			bottom: this.state.flipSwitcher.position
		};
		const isTemplateModalOpen = this.props.isSavingTemplate;
		const saveTemplateActions = [
			<WMFlatButton
				label="CANCEL"
				secondary
				onClick={ () => this.props.toggleTemplateModal() }
			/>,
			<WMFlatButton
				label="SAVE"
				primary
				onClick={ () => {
					this.props.saveTemplate(this.props.modal);
				} }
			/>
		];
		const canSwitchModules = this.canSwitchModules;
		const { errors, assignmentStatus, setValue, numberOfCopies } = this.props;

		return (
			<div className="assignment-creation">
				<div className="assignment-creation--sidebar" ref="sidebar">
					<div className="assignment-creation--templates">
							Templates
						<select
							defaultValue={ this.props.templateInfo.id }
							className="wm-select"
							name="assignment-creation-template"
							id="assignment-creation-template"
						/>
					</div>
					<div className="assignment-creation--copies">
						Create Copies
						<select
							defaultValue={ numberOfCopies }
							className="wm-select"
							name="assignment-creation--number-of-copies"
							id="assignment-creation--number-of-copies"
						>
							<option value={ 1 }>1</option>
							<option value={ 2 }>2</option>
							<option value={ 3 }>3</option>
							<option value={ 4 }>4</option>
							<option value={ 5 }>5</option>
							<option value={ 6 }>6</option>
							<option value={ 7 }>7</option>
							<option value={ 8 }>8</option>
							<option value={ 9 }>9</option>
							<option value={ 10 }>10</option>
						</select>
					</div>
					{ this.state.modules.map((module) => {
						const { id, title, isEnabled, optional, isValid } = module;

						if (isEnabled && (id !== 'moduleSwitcher' || (id === 'moduleSwitcher' && canSwitchModules))) {
							return (
								<div
									key={ id }
									className={ `assignment-creation--tab ${this.state.activeModuleId === id ? '-active' : ''} ${id === 'moduleSwitcher' && this.state.showModuleSwitcher ? '-active-switcher' : ''}` }
									data-content={ `#assignment-creation--${id}` }
									onClick={ () => id !== 'moduleSwitcher' ? this.scrollToModule(module) : this.toggleModuleSwitcher() }
								>
									{ title }
									{ ((assignmentStatus.length && assignmentStatus === 'sent' && !isValid && !optional && id !== 'moduleSwitcher') ||
										((assignmentStatus === 'draft' && id === 'basic' && !isValid && !optional && id !== 'moduleSwitcher'))) ? (
											<WMFontIcon
												id={ `assignment-creation--${id}` }
												className="material-icons"
												color={ 'red' }
												style={ { fontSize: '20px' } }
											>
												warning
											</WMFontIcon>
									) : '' }

									{id !== 'moduleSwitcher' && canSwitchModules && optional ? (
										<div
											className="assignment-creation--module-close"
											onClick={ () => this.toggleModule(id) }
										>
											<i className="wm-icon-x" />
										</div>
									) : '' }
								</div>
							);
						} else {
							return '';
						}
					})}
					{ this.state.showModuleSwitcher && this.state.modules ? (
						<ModuleSwitcher
							modules={ this.state.modules.filter(module => module.id !== 'recurrence') }
							toggleOptionalModules={ this.toggleOptionalModules }
							toggleModule={ this.toggleModule }
							showAllOptionalModules={ this.state.showAllOptionalModules }
							toggleSwitcher={ this.toggleModuleSwitcher }
							style={ this.state.flipSwitcher.flipped ? moduleSwitcherStyle : {} }
						/>
					) : '' }
				</div>
				<main className="assignment-creation--main" ref="mainContainer">
					{this.state.modules.map((module) => {
						const { id, title, isEnabled, Component, ...otherProps } = module;

						if (Component !== null && isEnabled) {
							return (
								<section
									key={ id }
									id={ `assignment-creation--${id}` }
									className={ `assignment-creation--section ${this.state.activeModuleId === id ? '-active' : ''} ${this.state.scrolledModuleId === id ? '-parked' : ''} ` }
									data-content={ id }
								>
									<div className="assignment-creation--header-wrapper">
										<h2 className={ `assignment-creation--header ${this.state.activeModuleId === id ? '-active' : ''} ${this.state.scrolledModuleId === id ? '-parked' : ''} ` }>{title}</h2>
									</div>
									<div className={ `assignment-creation--content assignment-creation--${id}` }>
										{ (!module.optional && errors.length && !module.isValid) ? (
											errors.map((error) => {
												const { message, field } = error;
												if (field === id) {
													return <WMMessageBanner key={ 'title' } status="error"> { message } </WMMessageBanner>;
												} else if (field === 'title' && id === 'basic') {
													return <WMMessageBanner key={ 'title' } status="error"> { message } </WMMessageBanner>;
												} else if (field === 'description' && id === 'basic') {
													return <WMMessageBanner key={ 'description' } status="error"> { message } </WMMessageBanner>;
												} else if (field === 'scheduling' && id === 'schedule') {
													return <WMMessageBanner key={ 'schedule' } status="error"> { message } </WMMessageBanner>;
												} else if (field === 'industry' && id === 'basic') {
													return <WMMessageBanner key={ 'industry' } status="error"> { message } </WMMessageBanner>;
												}
											})
										) : ''}
										{ (assignmentStatus === 'sent' && !module.isValid && !module.optional && !this.props.routing.isValid) ? (
											<WMMessageBanner key={ id } status="error"> { title } section is invalid. </WMMessageBanner>
										) : ''}
										<Component
											setValue={ setValue }
											id={ id }
											setModuleValidation={ this.setModuleValidation }
											{ ...otherProps }
										/>
									</div>
								</section>
							);
						} else {
							return false;
						}
					})}
				</main>

				<WMModal
					open={ isTemplateModalOpen }
					title={ 'Save Assignment Template' }
					style={ {
						zIndex: '10001'
					} }
					modal={ false }
					actions={ saveTemplateActions }
				>
					{ (errors.length) ? (
						errors.map((error) => {
							const { message, field } = error;
							return <WMMessageBanner key={ field } status="error"> { message } </WMMessageBanner>;
						})
					) : '' }
					<WMFormRow
						labelText="Name"
						required
						id="template-name-field-row"
					>
						<WMTextField
							id="template-name-field"
							name="template-name"
							value={ this.props.templateInfo.name }
							onChange={ (event, value) => this.props.updateTemplateName(value) }
						/>
					</WMFormRow>

					<WMFormRow
						labelText="Description"
						id="template-description-field-row"
					>
						<WMTextField
							id="template-description-field"
							name="template-description"
							value={ this.props.templateInfo.description }
							onChange={ (event, value) => this.props.updateTemplateDescription(value) }
						/>
					</WMFormRow>
				</WMModal>
			</div>
		);
	}
}

Creation.propTypes = {
	assignmentStatus: PropTypes.string,
	numberOfCopies: PropTypes.number,
	setValue: PropTypes.func,
	templateInfo: PropTypes.shape({
		name: PropTypes.string.isRequired,
		description: PropTypes.string,
		id: PropTypes.number
	}),
	errors: PropTypes.arrayOf(PropTypes.shape({
		message: PropTypes.string,
		field: PropTypes
	}))
};

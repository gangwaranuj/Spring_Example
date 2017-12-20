'use strict';

import Application from '../core';
import PhotoCropPage from './photocrop_page';
import InsurancePage from './insurance_page';
import SkillsPage from './skills_page';
import PhotoPage from './photo_page';
import CertificationAddPage from './certification_add_page';
import EmploymentPage from './employment_page';
import IndexPage from './profileedit';
import IndustriesPage from './industries';
import LanguagesPage from './languages';
import LicensesPage from './licenses';
import RatesLocationsPage from './ratesLocations';
import Certifications from './certifications';
import QualificationsPage from './qualifications_page';

Application.init({ name: 'profileedit', features: config }, () => {});

switch (config.type) {
	case 'rates_locations': 
		RatesLocationsPage();
		break;
	case 'licenses':
		LicensesPage(config.state, config.licenseId);
		break;
	case 'index':
		IndexPage();
		break;
	case 'industries':
		IndustriesPage();
		break;
	case 'languages':
		LanguagesPage();
		break;
	case 'photocrop':
		PhotoCropPage({
			_x1: config._x1,
			_y1: config._y1,
			_x2: config._x2,
			_y2: config._y2,
			_width: config._width,
			_height: config._height
		});
		break;
	case 'photo':
		PhotoPage();
		break;
	case 'skills':
		SkillsPage({
			skills: config.skills
		});
		break;
	case 'certifications':
		Certifications(config.providerId, config.certificationId);
		break;
	case 'certification_add':
		CertificationAddPage();
		break;
	case 'specialties':
		SkillsPage({ 
			skills: config.skills
		},{
			saveLink: '/profile-edit/save_specialties',
			browseLink: '/profile-edit/browse_specialties',
			suggestLink: '/profile-edit/suggest_specialties'
		});
		break;
	case 'tools':
		SkillsPage({
			skills: config.skills
		},{
			saveLink: '/profile-edit/save_tools',
			browseLink: '/profile-edit/browse_tools',
			suggestLink: '/profile-edit/suggest_tools'
		});
		break;
	case 'insurance':
		InsurancePage();
		break;
	case 'employment':
		EmploymentPage();
		break;
	case 'qualifications':
		QualificationsPage({
			qualifications: config.skills,
			jobTitle: config.jobTitle,
			industry: config.industry,
			skills: {}
		});
		break;
}

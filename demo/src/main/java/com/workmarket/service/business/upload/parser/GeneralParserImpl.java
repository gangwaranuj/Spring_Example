package com.workmarket.service.business.upload.parser;

import com.google.common.collect.Lists;
import com.workmarket.dao.UserDAO;
import com.workmarket.dao.crm.ClientCompanyDAO;
import com.workmarket.dao.industry.IndustryDAO;
import com.workmarket.dao.profile.ProfileDAO;
import com.workmarket.domains.work.dao.project.ProjectDAO;
import com.workmarket.domains.model.Profile;
import com.workmarket.domains.model.crm.ClientCompany;
import com.workmarket.domains.work.model.project.Project;
import com.workmarket.domains.work.service.upload.WorkUploadColumn;
import com.workmarket.service.business.IndustryService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.thrift.core.Company;
import com.workmarket.thrift.core.Industry;
import com.workmarket.thrift.core.User;
import com.workmarket.thrift.work.Template;
import com.workmarket.thrift.work.Work;
import com.workmarket.thrift.work.exception.WorkRowParseError;
import com.workmarket.thrift.work.exception.WorkRowParseErrorType;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.workmarket.service.business.upload.parser.ParseUtils.createErrorRow;
import static org.apache.commons.lang.StringUtils.isNumeric;

@Component
public class GeneralParserImpl extends BaseParser implements GeneralParser {

	@Autowired private AuthenticationService authn;

	@Autowired private UserDAO userDAO;
	@Autowired private ProfileDAO profileDAO;
	@Autowired private IndustryDAO industryDAO;
	@Autowired private ProjectDAO projectDAO;
	@Autowired private ClientCompanyDAO clientCompanyDAO;
	@Autowired private IndustryService industryService;

	@Override
	public void build(WorkUploaderBuildResponse response, WorkUploaderBuildData buildData) {
		Work work = response.getWork();
		Map<String,String> types = buildData.getTypes();

		List<WorkRowParseError> errors = Lists.newArrayList();

		// It's probable that we should simply assert that currentUser is set here
		com.workmarket.domains.model.User currentUser = authn.getCurrentUser();

		if (!work.isSetCompany()) {
			if (currentUser != null) {
				work.setCompany(new Company().setId(currentUser.getCompany().getId()));
			}
		}

		if (WorkUploadColumn.containsAll(types, WorkUploadColumn.TITLE)) {
			work.setTitle(types.get(WorkUploadColumn.TITLE.getUploadColumnName()));
		}
		if (WorkUploadColumn.containsAll(types, WorkUploadColumn.DESCRIPTION)) {
			work.setDescription(types.get(WorkUploadColumn.DESCRIPTION.getUploadColumnName()));
		}
		if (WorkUploadColumn.containsAll(types, WorkUploadColumn.INSTRUCTIONS)) {
			work.setInstructions(types.get(WorkUploadColumn.INSTRUCTIONS.getUploadColumnName()));
		}
		if (WorkUploadColumn.containsAll(types, WorkUploadColumn.DESIRED_SKILLS)) {
			work.setDesiredSkills(types.get(WorkUploadColumn.DESIRED_SKILLS.getUploadColumnName()));
		}

		if (WorkUploadColumn.containsAny(types, WorkUploadColumn.OWNER_USER_NUMBER, WorkUploadColumn.OWNER_EMAIL)) {
			Long userId = null;
			if (WorkUploadColumn.isNotEmpty(types, WorkUploadColumn.OWNER_USER_NUMBER)) {
				String value = WorkUploadColumn.get(types, WorkUploadColumn.OWNER_USER_NUMBER);
				if (isNumeric(value)) {
					com.workmarket.domains.model.User user = userDAO.findUserByUserNumber(value, false);
					if (user != null && work.getCompany().getId() == user.getCompany().getId()) {
						userId = user.getId();
					}
				}
				if (userId != null) {
					work.setBuyer(new User().setId(userId).setUserNumber(value));
				} else {
					errors.add(
						ParseUtils.createErrorRow(
							value,
							"Assignment Owner ID \"" + value + "\" not found.",
							WorkRowParseErrorType.INVALID_DATA,
							WorkUploadColumn.OWNER_USER_NUMBER
						)
					);
				}
			} else {
				String value = WorkUploadColumn.get(types, WorkUploadColumn.OWNER_EMAIL);
				com.workmarket.domains.model.User user = userDAO.findUserByEmail(value);
				if (user != null && work.getCompany().getId() == user.getCompany().getId()) {
					userId = user.getId();
				}
				if (userId != null) {
					work.setBuyer(new User().setId(userId).setEmail(value));
				} else {
					errors.add(
						ParseUtils.createErrorRow(
							value,
							"Assignment Owner email \"" + value + "\" not found.",
							WorkRowParseErrorType.INVALID_DATA,
							WorkUploadColumn.OWNER_EMAIL
						)
					);
				}
			}
		} else {
			if (!work.isSetBuyer()) {
				if (currentUser == null) {
					addBuyerNotSetError(errors, work);
				} else {
					work.setBuyer(new User().setId(currentUser.getId()).setEmail(currentUser.getEmail()));
				}
			}
		}

		if (WorkUploadColumn.containsAny(types, WorkUploadColumn.SUPPORT_CONTACT_USER_NUMBER, WorkUploadColumn.SUPPORT_CONTACT_EMAIL)) {
			Long userId = null;
			if (WorkUploadColumn.isNotEmpty(types, WorkUploadColumn.SUPPORT_CONTACT_USER_NUMBER)) {
				String value = WorkUploadColumn.get(types, WorkUploadColumn.SUPPORT_CONTACT_USER_NUMBER);
				if (isNumeric(value)) {
					userId = userDAO.findUserId(value);
				}
				if (userId != null) {
					work.setSupportContact(new User().setId(userId).setUserNumber(value));
				} else {
					errors.add(ParseUtils.createErrorRow(value, "Support Contact ID \"" + value + "\" not found.", WorkRowParseErrorType.INVALID_DATA, WorkUploadColumn.SUPPORT_CONTACT_USER_NUMBER));
				}
			} else {
				String value = WorkUploadColumn.get(types, WorkUploadColumn.SUPPORT_CONTACT_EMAIL);
				userId = userDAO.findUserIdByEmail(value);
				if (userId != null) {
					work.setSupportContact(new User().setId(userId).setEmail(value));
				} else {
					errors.add(ParseUtils.createErrorRow(value, "Support Contact email \"" + value + "\" not found.", WorkRowParseErrorType.INVALID_DATA, WorkUploadColumn.SUPPORT_CONTACT_EMAIL));
				}
			}
		} else {
			if (!work.isSetSupportContact()) {
				if (currentUser == null) {
					addBuyerNotSetError(errors, work);
				} else {
					work.setSupportContact(new User().setId(currentUser.getId()).setEmail(currentUser.getEmail()));
				}
			}
		}

		if (WorkUploadColumn.isNotEmpty(types, WorkUploadColumn.CLIENT_NAME)) {
			work.setClientCompany(null);
			String value = types.get(WorkUploadColumn.CLIENT_NAME.getUploadColumnName());
			if (work.isSetCompany()) {
				ClientCompany client = clientCompanyDAO.findClientCompanyByName(work.getCompany().getId(), value);
				if (client != null) {
					work.setClientCompany(new Company().setId(client.getId()).setName(client.getName()));
				} else {
					String errorMessage = "Client \"" + value + "\" not found.";
					errors.add(createErrorRow(value, errorMessage, WorkRowParseErrorType.INVALID_DATA, WorkUploadColumn.CLIENT_NAME));
				}
			} else {
				errors.add(createErrorRow(value, "Client requires a company", WorkRowParseErrorType.INVALID_DATA, WorkUploadColumn.CLIENT_NAME));
			}
		}

		if (WorkUploadColumn.containsAny(types, WorkUploadColumn.INDUSTRY_ID, WorkUploadColumn.INDUSTRY_NAME)) {
			work.setIndustry(null);
			if (WorkUploadColumn.isNotEmpty(types, WorkUploadColumn.INDUSTRY_NAME)) {
				String industryName = WorkUploadColumn.get(types, WorkUploadColumn.INDUSTRY_NAME);
				com.workmarket.domains.model.Industry industry = industryDAO.findIndustryByName(industryName);
				if (industry != null) {
					work.setIndustry(new Industry().setId(industry.getId()).setName(industryName));
				} else {
					errors.add(createErrorRow(industryName, "Industry Name \"" + industryName + "\" not found.", WorkRowParseErrorType.INVALID_DATA, WorkUploadColumn.INDUSTRY_NAME));
				}
			}
			if (WorkUploadColumn.isNotEmpty(types, WorkUploadColumn.INDUSTRY_ID)) {
				String industryName = WorkUploadColumn.get(types, WorkUploadColumn.INDUSTRY_ID);
				try {
					Long industryId = Long.parseLong(types.get(WorkUploadColumn.INDUSTRY_ID.getUploadColumnName()));
					com.workmarket.domains.model.Industry industry = industryDAO.get(industryId);
					if (industry != null) {
						work.setIndustry(new Industry().setId(industry.getId()).setName(industry.getName()));
					} else {
						errors.add(createErrorRow(industryId.toString(), "Industry ID \"" + industryId + "\" not found.", WorkRowParseErrorType.INVALID_DATA, WorkUploadColumn.INDUSTRY_ID));
					}
				} catch (NumberFormatException nfe) {
					String value = types.get(WorkUploadColumn.INDUSTRY_ID.getUploadColumnName());
					String errorMessage = "Invalid number format \"" + industryName + "\" for Industry ID.";
					errors.add(createErrorRow(value, errorMessage, WorkRowParseErrorType.INVALID_DATA, WorkUploadColumn.INDUSTRY_ID));
				}
			}
		} else {
			// If industry was not specified anywhere, default industry to user's preferred industry
			if (!work.isSetIndustry()) {
				if (work.isSetBuyer() && work.getBuyer().isSetId()) {
					Profile profile = profileDAO.findByUser(work.getBuyer().getId());
					com.workmarket.domains.model.Industry defaultIndustry = industryService.getDefaultIndustryForProfile(profile.getId());
					work.setIndustry(new Industry().setId(defaultIndustry.getId()).setName(defaultIndustry.getName()));
				}
			}
		}

		if (WorkUploadColumn.isNotEmpty(types, WorkUploadColumn.PROJECT_NAME)) {
			work.setProject(null);
			String projectName = types.get(WorkUploadColumn.PROJECT_NAME.getUploadColumnName());

			if (StringUtils.isNotEmpty(projectName)) {
				if (!work.isSetCompany()) {
					errors.add(createErrorRow(projectName, "Company is required when specifying project name.", WorkRowParseErrorType.INVALID_DATA, WorkUploadColumn.PROJECT_NAME));
				} else if (!work.isSetClientCompany()) {
					errors.add(createErrorRow(projectName, "Client name is required when specifying project name.", WorkRowParseErrorType.INVALID_DATA, WorkUploadColumn.PROJECT_NAME));
				} else {
					Project project = projectDAO.findByNameCompanyAndClient(projectName, work.getCompany().getId(), work.getClientCompany().getId());
					if (project != null) {
						work.setProject(new com.workmarket.thrift.work.Project().setId(project.getId()).setName(projectName));
					} else {
						errors.add(createErrorRow(projectName, "Project \"" + projectName + "\" not found.", WorkRowParseErrorType.INVALID_DATA, WorkUploadColumn.PROJECT_NAME));
					}
				}
			}
		}

		if (WorkUploadColumn.isNotEmpty(types, WorkUploadColumn.TEMPLATE_NUMBER)) {
			String templateNumber = WorkUploadColumn.get(types, WorkUploadColumn.TEMPLATE_NUMBER);
			Long templateId = buildData.getTemplateLookup().get(templateNumber);
			if (templateId != null) {
				work.setTemplate(new Template()
						.setId(templateId)
						.setWorkNumber(templateNumber));
			} else {
				errors.add(createErrorRow(templateNumber, "Invalid template ID.", WorkRowParseErrorType.INVALID_DATA, WorkUploadColumn.TEMPLATE_NUMBER));
			}
		}

		// ignore routing for now if it's a bundle
		if (
			WorkUploadColumn.isNotEmpty(types, WorkUploadColumn.USER_NUMBER) &&
			WorkUploadColumn.isEmpty(types, WorkUploadColumn.NEW_BUNDLE_NAME) &&
			WorkUploadColumn.isEmpty(types, WorkUploadColumn.EXISTING_BUNDLE_ID)
		) {
			addRouting(work, types, errors);
		}

		if (WorkUploadColumn.isNotEmpty(types, WorkUploadColumn.UNIQUE_EXTERNAL_ID)) {
			String uniqueExternalIdValue = types.get(WorkUploadColumn.UNIQUE_EXTERNAL_ID.getUploadColumnName());
			work.setUniqueExternalIdValue(uniqueExternalIdValue);
		}
		response.addToRowParseErrors(errors);
	}

	private void addBuyerNotSetError(Collection<WorkRowParseError> errors, Work work) {
		addBuyerErrorColumn(errors, WorkUploadColumn.OWNER_EMAIL);
		addBuyerErrorColumn(errors, WorkUploadColumn.OWNER_USER_NUMBER);
	}

	private void addBuyerErrorColumn(Collection<WorkRowParseError> errors, WorkUploadColumn errorColumn) {
		String errorMessage = "The buyer cannot be set because there was no value " +
				"for a user ID and a user number. Additionally, there was no value " +
				"from the authentication service set.";
		WorkRowParseError error = new WorkRowParseError();
		error.setMessage(errorMessage);
		error.setColumn(errorColumn);
		error.setErrorType(WorkRowParseErrorType.MISSING_PARAMETER);
		error.setData("null or error");
		errors.add(error);
	}
}

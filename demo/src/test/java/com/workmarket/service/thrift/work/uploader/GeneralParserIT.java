package com.workmarket.service.thrift.work.uploader;

import com.workmarket.domains.model.Industry;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.crm.ClientCompany;
import com.workmarket.domains.work.model.project.Project;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.CRMService;
import com.workmarket.domains.work.service.project.ProjectService;
import com.workmarket.service.business.UserService;
import com.workmarket.domains.work.service.upload.WorkUploadColumn;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.business.InvariantDataService;
import com.workmarket.test.IntegrationTest;
import com.workmarket.thrift.work.Work;
import com.workmarket.service.business.upload.parser.GeneralParser;
import com.workmarket.service.business.upload.parser.WorkUploaderBuildData;
import com.workmarket.service.business.upload.parser.WorkUploaderBuildResponse;
import com.workmarket.utility.CollectionUtilities;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class GeneralParserIT extends BaseServiceIT {

	@Autowired private AuthenticationService authn;
	@Autowired private UserService userService;
	@Autowired private InvariantDataService invariants;
	@Autowired private CRMService crm;
	@Autowired private ProjectService projectService;
	@Autowired private GeneralParser generalParser;

	@Test
	public void testSimpleValues() throws Exception {
		authn.unsetCurrentUser();

		WorkUploaderBuildResponse response = new WorkUploaderBuildResponse(new Work());
		generalParser.build(response, new WorkUploaderBuildData().setTypes(CollectionUtilities.newStringMap(
			WorkUploadColumn.TITLE.getUploadColumnName(), "Testing",
			WorkUploadColumn.DESCRIPTION.getUploadColumnName(), "One. Two. Three.",
			WorkUploadColumn.INSTRUCTIONS.getUploadColumnName(), "Four little piggies went to market.",
			WorkUploadColumn.DESIRED_SKILLS.getUploadColumnName(), "The fifth was born that way."
		)));

		Assert.assertTrue(response.getWork().isSetTitle());
		Assert.assertTrue(response.getWork().isSetDescription());
		Assert.assertTrue(response.getWork().isSetInstructions());
		Assert.assertTrue(response.getWork().isSetDesiredSkills());
	}

	@Test
	@Transactional
	public void testSetCompanyViaAuthn() throws Exception {
		authn.setCurrentUser(ANONYMOUS_USER_ID);

		WorkUploaderBuildResponse response = new WorkUploaderBuildResponse(new Work());
		generalParser.build(response, new WorkUploaderBuildData().setTypes(CollectionUtilities.newStringMap()));

		Assert.assertTrue(response.getWork().isSetCompany());
		Assert.assertEquals(authn.getCurrentUser().getCompany().getId().longValue(), response.getWork().getCompany().getId());
	}

	@Test
	@Transactional
	public void testSetBuyerViaAuthn() throws Exception {
		authn.setCurrentUser(ANONYMOUS_USER_ID);

		WorkUploaderBuildResponse response = new WorkUploaderBuildResponse(new Work());
		generalParser.build(response, new WorkUploaderBuildData().setTypes(CollectionUtilities.newStringMap()));

		Assert.assertTrue(response.getWork().isSetBuyer());
		Assert.assertEquals(authn.getCurrentUser().getId().longValue(), response.getWork().getBuyer().getId());
	}

	@Test
	@Transactional
	public void testSetBuyerViaUserNumber() throws Exception {
		User u = userService.findUserById(ANONYMOUS_USER_ID);
		authn.setCurrentUser(u);

		WorkUploaderBuildResponse response = new WorkUploaderBuildResponse(new Work());
		generalParser.build(response, new WorkUploaderBuildData().setTypes(CollectionUtilities.newStringMap(
			WorkUploadColumn.OWNER_USER_NUMBER.getUploadColumnName(), u.getUserNumber()
		)));

		Assert.assertTrue(response.getWork().isSetBuyer());
		Assert.assertEquals(u.getId().longValue(), response.getWork().getBuyer().getId());
	}

	@Test
	@Transactional
	public void testSetBuyerViaEmail() throws Exception {
		User u = userService.findUserById(ANONYMOUS_USER_ID);
		authn.setCurrentUser(u);

		WorkUploaderBuildResponse response = new WorkUploaderBuildResponse(new Work());
		generalParser.build(response, new WorkUploaderBuildData().setTypes(CollectionUtilities.newStringMap(
			WorkUploadColumn.OWNER_EMAIL.getUploadColumnName(), u.getEmail()
		)));

		Assert.assertTrue(response.getWork().isSetBuyer());
		Assert.assertEquals(u.getId().longValue(), response.getWork().getBuyer().getId());
	}

	@Test
	@Transactional
	public void testBuyerNotSet() throws Exception {
		authn.unsetCurrentUser();

		WorkUploaderBuildResponse response = new WorkUploaderBuildResponse(new Work());
		generalParser.build(response, new WorkUploaderBuildData().setTypes(CollectionUtilities.newStringMap()));

		Assert.assertFalse(response.getWork().isSetBuyer());
		Assert.assertFalse(response.getErrors().isEmpty());
	}

	@Test
	@Transactional
	public void testSetIndustryViaId() throws Exception {
		authn.unsetCurrentUser();

		Industry i = invariants.findIndustry(INDUSTRY_ID);

		WorkUploaderBuildResponse response = new WorkUploaderBuildResponse(new Work());
		generalParser.build(response, new WorkUploaderBuildData().setTypes(CollectionUtilities.newStringMap(
			WorkUploadColumn.INDUSTRY_ID.getUploadColumnName(), i.getId().toString()
		)));

		Assert.assertTrue(response.getWork().isSetIndustry());
		Assert.assertEquals(i.getId().longValue(), response.getWork().getIndustry().getId());
	}

	@Test
	@Transactional
	public void testSetIndustryViaName() throws Exception {
		authn.unsetCurrentUser();

		Industry i = invariants.findIndustry(INDUSTRY_ID);

		WorkUploaderBuildResponse response = new WorkUploaderBuildResponse(new Work());
		generalParser.build(response, new WorkUploaderBuildData().setTypes(CollectionUtilities.newStringMap(
			WorkUploadColumn.INDUSTRY_NAME.getUploadColumnName(), i.getName()
		)));

		Assert.assertTrue(response.getWork().isSetIndustry());
		Assert.assertEquals(i.getId().longValue(), response.getWork().getIndustry().getId());
	}

	@Test
	@Transactional
	public void testSetIndustryViaBuyerDefault() throws Exception {
		authn.setCurrentUser(ANONYMOUS_USER_ID);

		WorkUploaderBuildResponse response = new WorkUploaderBuildResponse(new Work());
		generalParser.build(response, new WorkUploaderBuildData().setTypes(CollectionUtilities.newStringMap()));

		Assert.assertNotNull(response.getWork().getIndustry());
	}

	@Test
	@Transactional
	public void testSetIndustryViaInvalidId() throws Exception {
		authn.unsetCurrentUser();

		WorkUploaderBuildResponse response = new WorkUploaderBuildResponse(new Work());
		generalParser.build(response, new WorkUploaderBuildData().setTypes(CollectionUtilities.newStringMap(
			WorkUploadColumn.INDUSTRY_ID.getUploadColumnName(), "00412394"
		)));

		Assert.assertFalse(response.getWork().isSetIndustry());
		Assert.assertFalse(response.getErrors().isEmpty());
	}

	@Test
	@Transactional
	public void testSetIndustryViaInvalidName() throws Exception {
		authn.unsetCurrentUser();

		WorkUploaderBuildResponse response = new WorkUploaderBuildResponse(new Work());
		generalParser.build(response, new WorkUploaderBuildData().setTypes(CollectionUtilities.newStringMap(
			WorkUploadColumn.INDUSTRY_NAME.getUploadColumnName(), "Fake industry name"
		)));

		Assert.assertFalse(response.getWork().isSetIndustry());
		Assert.assertFalse(response.getErrors().isEmpty());
	}

	@Test
	@Transactional
	public void testSetIndustryViaInvalidBuyerDefault() throws Exception {
		authn.unsetCurrentUser();

		WorkUploaderBuildResponse response = new WorkUploaderBuildResponse(new Work());
		generalParser.build(response, new WorkUploaderBuildData().setTypes(CollectionUtilities.newStringMap()));

		Assert.assertFalse(response.getWork().isSetIndustry());
		Assert.assertFalse(response.getErrors().isEmpty());
	}

	@Test
	@Transactional
	@Ignore
	public void testSetClientViaName() throws Exception {
		authn.setCurrentUser(ANONYMOUS_USER_ID);

		ClientCompany c = crm.findClientCompanyById(CLIENT_COMPANY_ID);

		WorkUploaderBuildResponse response = new WorkUploaderBuildResponse(new Work());
		generalParser.build(response, new WorkUploaderBuildData().setTypes(CollectionUtilities.newStringMap(
			WorkUploadColumn.CLIENT_NAME.getUploadColumnName(), c.getName()
		)));

		Assert.assertNotNull(response.getWork().getClientCompany());
		Assert.assertEquals(c.getId().longValue(), response.getWork().getClientCompany().getId());
	}

	@Test
	@Transactional
	public void testSetClientViaInvalidName() throws Exception {
		authn.setCurrentUser(ANONYMOUS_USER_ID);

		WorkUploaderBuildResponse response = new WorkUploaderBuildResponse(new Work());
		generalParser.build(response, new WorkUploaderBuildData().setTypes(CollectionUtilities.newStringMap(
			WorkUploadColumn.CLIENT_NAME.getUploadColumnName(), "Some fake client name that should not exist"
		)));

		Assert.assertFalse(response.getWork().isSetClientCompany());
		Assert.assertFalse(response.getErrors().isEmpty());
	}

	@Test
	@Transactional
	@Ignore
	public void testSetProjectViaName() throws Exception {
		authn.setCurrentUser(ANONYMOUS_USER_ID);

		ClientCompany c = crm.findClientCompanyById(CLIENT_COMPANY_ID);
		Project p = projectService.findById(PROJECT_ID);

		WorkUploaderBuildResponse response = new WorkUploaderBuildResponse(new Work());
		generalParser.build(response, new WorkUploaderBuildData().setTypes(CollectionUtilities.newStringMap(
			WorkUploadColumn.PROJECT_NAME.getUploadColumnName(), p.getName(),
			WorkUploadColumn.CLIENT_NAME.getUploadColumnName(), c.getName()
		)));

		Assert.assertTrue(response.getWork().isSetProject());
		Assert.assertEquals(p.getId().longValue(), response.getWork().getProject().getId());
	}

	@Test
	@Transactional
	public void testSetProjectViaInvalidName() throws Exception {
		authn.unsetCurrentUser();

		WorkUploaderBuildResponse response = new WorkUploaderBuildResponse(new Work());
		generalParser.build(response, new WorkUploaderBuildData().setTypes(CollectionUtilities.newStringMap(
			WorkUploadColumn.PROJECT_NAME.getUploadColumnName(), "Some fake project name that should not exist"
		)));

		Assert.assertFalse(response.getWork().isSetProject());
		Assert.assertFalse(response.getErrors().isEmpty());
	}

	@Test
	@Transactional
	public void testSetProjectViaNameWithoutClient() throws Exception {
		authn.unsetCurrentUser();

		Project p = projectService.findById(PROJECT_ID);

		WorkUploaderBuildResponse response = new WorkUploaderBuildResponse(new Work());
		generalParser.build(response, new WorkUploaderBuildData().setTypes(CollectionUtilities.newStringMap(
			WorkUploadColumn.PROJECT_NAME.getUploadColumnName(), p.getName()
		)));

		Assert.assertFalse(response.getWork().isSetProject());
		Assert.assertFalse(response.getErrors().isEmpty());
	}

}

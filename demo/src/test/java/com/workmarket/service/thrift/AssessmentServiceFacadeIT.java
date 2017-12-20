package com.workmarket.service.thrift;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.workmarket.domains.model.MimeType;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.assessment.AbstractAssessment;
import com.workmarket.domains.model.assessment.AssessmentStatusType;
import com.workmarket.domains.model.assessment.AttemptStatusType;
import com.workmarket.service.business.AssessmentService;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.infra.business.UploadService;
import com.workmarket.test.IntegrationTest;
import com.workmarket.thrift.assessment.*;
import com.workmarket.thrift.core.Asset;
import com.workmarket.thrift.core.Industry;
import com.workmarket.thrift.core.Skill;
import com.workmarket.thrift.core.Upload;
import com.workmarket.utility.FileUtilities;
import com.workmarket.utility.RandomUtilities;
import net.jcip.annotations.NotThreadSafe;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
@NotThreadSafe
public class AssessmentServiceFacadeIT extends BaseServiceIT {

	@Autowired private AssessmentService assessmentService;
	@Autowired private AssessmentServiceFacade service;
	@Autowired private UploadService uploadService;

	@Test
	public void findAssessment_AssessmentExists_Result() throws Exception {
		AbstractAssessment assessment = newAssessmentWithItems();

		AssessmentRequest request = new AssessmentRequest()
				.setUserId(assessment.getUser().getId())
				.setAssessmentId(assessment.getId());

		AssessmentResponse response = service.findAssessment(request);

		Assert.assertNotNull(response);
		assertEquals(assessment.getId().longValue(), response.getAssessment().getId());
	}

	@Test
	public void findAssessmentForGrading_statusActive_returnsResult() throws Exception {
		User user = newContractor();
		AbstractAssessment assessment = newAssessmentForUser(user, null, false);
		com.workmarket.domains.model.assessment.Attempt attempt =
				assessmentService.saveAttemptForAssessment(user.getId(), assessment.getId());
		assessmentService.completeAttemptForAssessment(attempt.getId());

		AssessmentGradingRequest request = new AssessmentGradingRequest()
				.setUserId(user.getId())
				.setAssessmentId(assessment.getId())
				.setAttemptId(attempt.getId());

		AssessmentResponse response = service.findAssessmentForGrading(request);

		assertNotNull(response);
	}

	@Test
	public void findAssessmentForGrading_statusRemoved_throwsException() throws Exception {
		User user = newContractor();
		AbstractAssessment assessment = newAssessmentForUser(user, null, false);
		com.workmarket.domains.model.assessment.Attempt attempt =
				assessmentService.saveAttemptForAssessment(user.getId(), assessment.getId());
		assessmentService.completeAttemptForAssessment(attempt.getId());

		AssessmentGradingRequest request = new AssessmentGradingRequest()
				.setUserId(user.getId())
				.setAssessmentId(assessment.getId())
				.setAttemptId(attempt.getId());

		assessmentService.updateAssessmentStatus(assessment.getId(), AssessmentStatusType.REMOVED);

		try {
			service.findAssessmentForGrading(request);
		} catch (AssessmentRequestException e) {
			assertNotNull(e);
		}
	}

	@Test
	public void saveOrUpdateAssessment_NoItems() throws Exception {
		Assessment a = new Assessment();
		a.setName("New Quiz");
		a.setDescription("A description.");
		a.setIndustry(new Industry().setId(INDUSTRY_1000_ID));
		a.setType(AssessmentType.GRADED);
		a.setApproximateDurationMinutes(90);

		a.addToSkills(new Skill().setId(newSkill().getId()));
		a.addToSkills(new Skill().setId(newSkill().getId()));

		AssessmentOptions o = new AssessmentOptions();
		o.setPassingScore(90);
		o.setPassingScoreShared(true);
		o.setRetakesAllowed(3);
		o.setDurationMinutes(120);
		o.setResultsSharedWithPassers(true);
		o.setResultsSharedWithFailers(false);
		o.setStatisticsShared(true);
		o.addToNotificationRecipients(new com.workmarket.thrift.core.User().setId(ANONYMOUS_USER_ID));
		o.addToNotifications(new NotificationTypeConfiguration(NotificationType.NEW_ATTEMPT));
		o.addToNotifications(new NotificationTypeConfiguration(NotificationType.ATTEMPT_UNGRADED).setDays(5));
		o.setFeatured(true);

		a.setConfiguration(o);

		AssessmentSaveRequest request = new AssessmentSaveRequest();
		request.setUserId(ANONYMOUS_USER_ID);
		request.setAssessment(a);

		AssessmentResponse response = service.saveOrUpdateAssessment(request);

		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getAssessment().getId());
		assertEquals("New Quiz", response.getAssessment().getName());
		assertEquals("A description.", response.getAssessment().getDescription());
		assertEquals(AssessmentType.GRADED, response.getAssessment().getType());
		assertEquals(90, response.getAssessment().getApproximateDurationMinutes());
		assertEquals(2, response.getAssessment().getSkillsSize());

		response = service.findAssessment(new AssessmentRequest()
				.setUserId(ANONYMOUS_USER_ID)
				.setAssessmentId(response.getAssessment().getId())
				.setIncludes(ImmutableSet.of(AssessmentRequestInfo.CONTEXT_INFO)));

		assertEquals(Double.valueOf(90), Double.valueOf(response.getAssessment().getConfiguration().getPassingScore()));
		assertTrue(response.getAssessment().getConfiguration().isPassingScoreShared());
		assertTrue(response.getAssessment().getConfiguration().isFeatured());
		assertEquals(3, response.getAssessment().getConfiguration().getRetakesAllowed());
		assertEquals(120, response.getAssessment().getConfiguration().getDurationMinutes());
		assertTrue(response.getAssessment().getConfiguration().isResultsSharedWithPassers());
		assertFalse(response.getAssessment().getConfiguration().isResultsSharedWithFailers());
		assertTrue(response.getAssessment().getConfiguration().isStatisticsShared());

		assertEquals(1, response.getAssessment().getConfiguration().getNotificationRecipientsSize());
		assertEquals(ANONYMOUS_USER_ID.longValue(), response.getAssessment().getConfiguration().getNotificationRecipients().get(0).getId());
		assertEquals(2, response.getAssessment().getConfiguration().getNotificationsSize());
	}

	@Test
	public void saveOrUpdateAssessment_NoItemsUpdateSkills() throws Exception {
		Assessment a = new Assessment();
		a.setName("New Quiz");
		a.setDescription("A description.");
		a.setIndustry(new Industry().setId(INDUSTRY_1000_ID));
		a.setType(AssessmentType.GRADED);
		a.setConfiguration(new AssessmentOptions()
				.setPassingScore(90));

		a.addToSkills(new Skill().setId(newSkill().getId()));
		a.addToSkills(new Skill().setId(newSkill().getId()));

		AssessmentSaveRequest request = new AssessmentSaveRequest();
		request.setUserId(ANONYMOUS_USER_ID);
		request.setAssessment(a);

		AssessmentResponse response = service.saveOrUpdateAssessment(request);

		response = service.findAssessment(new AssessmentRequest()
				.setUserId(ANONYMOUS_USER_ID)
				.setAssessmentId(response.getAssessment().getId())
				.setIncludes(ImmutableSet.of(AssessmentRequestInfo.CONTEXT_INFO)));

		assertEquals(2, response.getAssessment().getSkillsSize());

		a = response.getAssessment();
		a.setSkills(Lists.newArrayList(
				response.getAssessment().getSkills().get(1),
				new Skill().setId(newSkill().getId()),
				new Skill().setId(newSkill().getId()),
				new Skill().setId(newSkill().getId())
		));

		request.setAssessment(a);

		response = service.saveOrUpdateAssessment(request);

		assertEquals(4, response.getAssessment().getSkillsSize());
	}

	@Test
	public void saveOrUpdateAssessment_NoItemsUpdateNotifications() throws Exception {
		Assessment a = new Assessment();
		a.setName("New Quiz");
		a.setDescription("A description.");
		a.setIndustry(new Industry().setId(INDUSTRY_1000_ID));
		a.setType(AssessmentType.GRADED);

		AssessmentOptions o = new AssessmentOptions();
		o.addToNotifications(new NotificationTypeConfiguration(NotificationType.NEW_ATTEMPT));
		o.addToNotifications(new NotificationTypeConfiguration(NotificationType.ATTEMPT_UNGRADED).setDays(5));

		a.setConfiguration(o);

		AssessmentSaveRequest request = new AssessmentSaveRequest();
		request.setUserId(ANONYMOUS_USER_ID);
		request.setAssessment(a);

		AssessmentResponse response = service.saveOrUpdateAssessment(request);

		response = service.findAssessment(new AssessmentRequest()
				.setUserId(ANONYMOUS_USER_ID)
				.setAssessmentId(response.getAssessment().getId())
				.setIncludes(ImmutableSet.of(AssessmentRequestInfo.CONTEXT_INFO)));

		assertEquals(2, response.getAssessment().getConfiguration().getNotificationsSize());

		o.getNotifications().clear();
		a = response.getAssessment();
		a.setConfiguration(o);
		request.setAssessment(a);

		response = service.saveOrUpdateAssessment(request);
		response = service.findAssessment(new AssessmentRequest()
				.setUserId(ANONYMOUS_USER_ID)
				.setAssessmentId(response.getAssessment().getId())
				.setIncludes(ImmutableSet.of(AssessmentRequestInfo.CONTEXT_INFO)));

		assertEquals(0, response.getAssessment().getConfiguration().getNotificationsSize());
	}

	@Test
	public void saveOrUpdateAssessment_WithItems() throws Exception {
		Assessment a = new Assessment();
		a.setName("Get to know you survey");
		a.setDescription("A description.");
		a.setIndustry(new Industry().setId(INDUSTRY_1000_ID));
		a.setType(AssessmentType.SURVEY);
		a.setConfiguration(new AssessmentOptions());

		AssessmentSaveRequest saveRequest = new AssessmentSaveRequest();
		saveRequest.setUserId(ANONYMOUS_USER_ID);
		saveRequest.setAssessment(a);

		AssessmentResponse response = service.saveOrUpdateAssessment(saveRequest);

		response = service.findAssessment(new AssessmentRequest()
				.setUserId(ANONYMOUS_USER_ID)
				.setAssessmentId(response.getAssessment().getId())
				.setIncludes(ImmutableSet.of(
						AssessmentRequestInfo.CONTEXT_INFO,
						AssessmentRequestInfo.ITEM_INFO)));

		ItemSaveRequest itemSaveRequest = new ItemSaveRequest();
		itemSaveRequest.setUserId(ANONYMOUS_USER_ID);
		itemSaveRequest.setAssessmentId(response.getAssessment().getId());

		Item i = new Item()
				.setType(ItemType.SINGLE_LINE_TEXT)
				.setPrompt("What's your name?")
				.setGraded(false);
		itemSaveRequest.setItem(i);
		service.addOrUpdateItem(itemSaveRequest);

		i = new Item()
				.setType(ItemType.MULTIPLE_LINE_TEXT)
				.setPrompt("Tell us a little bit about yourself")
				.setDescription("E.g. interests, hobbies, skills, etc.")
				.setGraded(false);
		itemSaveRequest.setItem(i);
		service.addOrUpdateItem(itemSaveRequest);

		i = new Item()
				.setType(ItemType.EMAIL)
				.setPrompt("What's your email?");
		itemSaveRequest.setItem(i);
		service.addOrUpdateItem(itemSaveRequest);

		i = new Item()
				.setType(ItemType.DATE)
				.setPrompt("What's your date of birth?");
		itemSaveRequest.setItem(i);
		service.addOrUpdateItem(itemSaveRequest);

		i = new Item()
				.setType(ItemType.PHONE)
				.setPrompt("What's your telephone number?");
		itemSaveRequest.setItem(i);
		service.addOrUpdateItem(itemSaveRequest);

		i = new Item()
				.setType(ItemType.NUMERIC)
				.setPrompt("How many siblings do you have?")
				.setGraded(false);
		itemSaveRequest.setItem(i);
		service.addOrUpdateItem(itemSaveRequest);

		i = new Item()
				.setType(ItemType.DIVIDER);
		itemSaveRequest.setItem(i);
		service.addOrUpdateItem(itemSaveRequest);

		i = new Item()
				.setType(ItemType.MULTIPLE_CHOICE)
				.setPrompt("Which animals do you like?")
				.setGraded(false);
		i.addToChoices(new Choice().setValue("Monkeys").setCorrect(true));
		i.addToChoices(new Choice().setValue("Horses").setCorrect(true));
		i.addToChoices(new Choice().setValue("Cows").setCorrect(true));
		i.addToChoices(new Choice().setValue("Chickens").setCorrect(true));
		i.addToChoices(new Choice().setValue("Dogs").setCorrect(true));
		i.addToChoices(new Choice().setValue("Cats").setCorrect(true));
		i.addToChoices(new Choice().setValue("Muskrats").setCorrect(true));
		i.addToChoices(new Choice().setValue("Llamas").setCorrect(true));
		i.addToChoices(new Choice().setValue("Alpacas").setCorrect(true));
		itemSaveRequest.setItem(i);
		service.addOrUpdateItem(itemSaveRequest);

		i = new Item()
				.setType(ItemType.SINGLE_CHOICE_LIST)
				.setPrompt("If you could identify with one, which would it be?")
				.setGraded(false);
		i.addToChoices(new Choice().setValue("Monkeys").setCorrect(true));
		i.addToChoices(new Choice().setValue("Horses").setCorrect(true));
		i.addToChoices(new Choice().setValue("Cows").setCorrect(true));
		i.addToChoices(new Choice().setValue("Chickens").setCorrect(true));
		i.addToChoices(new Choice().setValue("Dogs").setCorrect(true));
		i.addToChoices(new Choice().setValue("Cats").setCorrect(true));
		i.addToChoices(new Choice().setValue("Muskrats").setCorrect(true));
		i.addToChoices(new Choice().setValue("Llamas").setCorrect(true));
		i.addToChoices(new Choice().setValue("Alpacas").setCorrect(true));
		itemSaveRequest.setItem(i);
		service.addOrUpdateItem(itemSaveRequest);

		i = new Item()
				.setType(ItemType.SINGLE_CHOICE_RADIO)
				.setPrompt("Was this a useless survey?")
				.setGraded(false);
		i.addToChoices(new Choice().setValue("Yes").setCorrect(true));
		i.addToChoices(new Choice().setValue("No").setCorrect(true));
		itemSaveRequest.setItem(i);
		service.addOrUpdateItem(itemSaveRequest);


		AssessmentRequest request = new AssessmentRequest();
		request.setUserId(ANONYMOUS_USER_ID);
		request.setAssessmentId(response.getAssessment().getId());
		request.addToIncludes(AssessmentRequestInfo.ITEM_INFO);

		response = service.findAssessment(new AssessmentRequest()
				.setUserId(ANONYMOUS_USER_ID)
				.setAssessmentId(response.getAssessment().getId())
				.setIncludes(ImmutableSet.of(
						AssessmentRequestInfo.CONTEXT_INFO,
						AssessmentRequestInfo.ITEM_INFO)));

		assertEquals(AssessmentType.SURVEY, response.getAssessment().getType());
		assertEquals(10, response.getAssessment().getItemsSize());

		assertEquals(ItemType.SINGLE_LINE_TEXT, response.getAssessment().getItems().get(0).getType());
		assertEquals(ItemType.MULTIPLE_LINE_TEXT, response.getAssessment().getItems().get(1).getType());
		assertEquals(ItemType.EMAIL, response.getAssessment().getItems().get(2).getType());
		assertEquals(ItemType.DATE, response.getAssessment().getItems().get(3).getType());
		assertEquals(ItemType.PHONE, response.getAssessment().getItems().get(4).getType());
		assertEquals(ItemType.NUMERIC, response.getAssessment().getItems().get(5).getType());
		assertEquals(ItemType.DIVIDER, response.getAssessment().getItems().get(6).getType());
		assertEquals(ItemType.MULTIPLE_CHOICE, response.getAssessment().getItems().get(7).getType());
		assertEquals(ItemType.SINGLE_CHOICE_LIST, response.getAssessment().getItems().get(8).getType());
		assertEquals(ItemType.SINGLE_CHOICE_RADIO, response.getAssessment().getItems().get(9).getType());

		assertEquals(9, response.getAssessment().getItems().get(7).getChoicesSize());
		assertEquals(9, response.getAssessment().getItems().get(8).getChoicesSize());
		assertEquals(2, response.getAssessment().getItems().get(9).getChoicesSize());
	}

	@Test
	public void saveOrUpdateAssessment_WithItemsWithUpload() throws Exception {
		String uniqueId = RandomUtilities.generateAlphaNumericString(10);
		initializeTestFile(uniqueId);
		com.workmarket.domains.model.asset.Upload upload = uploadService.storeUpload(STORAGE_TEST_FILE + uniqueId, "filename", MimeType.MS_WORD.getMimeType());

		Assessment a = new Assessment();
		a.setName("Get to know you survey");
		a.setDescription("A description.");
		a.setIndustry(new Industry().setId(INDUSTRY_1000_ID));
		a.setType(AssessmentType.SURVEY);
		a.setConfiguration(new AssessmentOptions());

		AssessmentSaveRequest saveRequest = new AssessmentSaveRequest();
		saveRequest.setUserId(ANONYMOUS_USER_ID);
		saveRequest.setAssessment(a);

		AssessmentResponse response = service.saveOrUpdateAssessment(saveRequest);

		ItemSaveRequest itemSaveRequest = new ItemSaveRequest();
		itemSaveRequest.setUserId(ANONYMOUS_USER_ID);
		itemSaveRequest.setAssessmentId(response.getAssessment().getId());

		Item i = new Item()
				.setType(ItemType.SINGLE_LINE_TEXT)
				.setPrompt("What's your name?")
				.setGraded(false);
		i.addToUploads(new Upload().setUuid(upload.getUUID()).setDescription("An upload"));
		itemSaveRequest.setItem(i);

		i = service.addOrUpdateItem(itemSaveRequest);

		assertEquals(0, i.getPosition());
		assertEquals(1, i.getAssetsSize());
		assertEquals("An upload", i.getAssets().get(0).getDescription());

		AssessmentRequest request = new AssessmentRequest();
		request.setUserId(ANONYMOUS_USER_ID);
		request.setAssessmentId(response.getAssessment().getId());
		request.addToIncludes(AssessmentRequestInfo.ITEM_INFO);

		response = service.findAssessment(new AssessmentRequest()
				.setUserId(ANONYMOUS_USER_ID)
				.setAssessmentId(response.getAssessment().getId())
				.setIncludes(ImmutableSet.of(
						AssessmentRequestInfo.CONTEXT_INFO,
						AssessmentRequestInfo.ITEM_INFO)));

		assertEquals(1, response.getAssessment().getItemsSize());
		assertEquals(1, response.getAssessment().getItems().get(0).getAssetsSize());

		deleteTestFile(uniqueId);
	}

	@Test
	public void saveOrUpdateAssessment_WithItemsWithAsset() throws Exception {
		String uniqueId = RandomUtilities.generateAlphaNumericString(10);
		initializeTestFile(uniqueId);
		com.workmarket.domains.model.asset.Upload upload = uploadService.storeUpload(STORAGE_TEST_FILE + uniqueId, "filename", MimeType.MS_WORD.getMimeType());

		Assessment a = new Assessment();
		a.setName("Get to know you survey");
		a.setDescription("A description.");
		a.setIndustry(new Industry().setId(INDUSTRY_1000_ID));
		a.setType(AssessmentType.SURVEY);
		a.setConfiguration(new AssessmentOptions());

		AssessmentSaveRequest saveRequest = new AssessmentSaveRequest();
		saveRequest.setUserId(ANONYMOUS_USER_ID);
		saveRequest.setAssessment(a);

		AssessmentResponse response = service.saveOrUpdateAssessment(saveRequest);

		ItemSaveRequest itemSaveRequest = new ItemSaveRequest();
		itemSaveRequest.setUserId(ANONYMOUS_USER_ID);
		itemSaveRequest.setAssessmentId(response.getAssessment().getId());

		Item i = new Item()
				.setType(ItemType.SINGLE_LINE_TEXT)
				.setPrompt("What's your name?")
				.setGraded(false);
		i.addToUploads(new Upload().setUuid(upload.getUUID()).setDescription("An upload"));
		itemSaveRequest.setItem(i);

		i = service.addOrUpdateItem(itemSaveRequest);

		assertEquals(0, i.getPosition());
		assertEquals(1, i.getAssetsSize());
		assertEquals("An upload", i.getAssets().get(0).getDescription());

		i.getUploads().clear();
		i.getAssets().clear();
		itemSaveRequest.setItem(i);

		i = service.addOrUpdateItem(itemSaveRequest);

		assertEquals(0, i.getAssetsSize());

		AssessmentRequest request = new AssessmentRequest();
		request.setUserId(ANONYMOUS_USER_ID);
		request.setAssessmentId(response.getAssessment().getId());
		request.addToIncludes(AssessmentRequestInfo.ITEM_INFO);

		response = service.findAssessment(new AssessmentRequest()
				.setUserId(ANONYMOUS_USER_ID)
				.setAssessmentId(response.getAssessment().getId())
				.setIncludes(ImmutableSet.of(
						AssessmentRequestInfo.CONTEXT_INFO,
						AssessmentRequestInfo.ITEM_INFO)));

		assertEquals(1, response.getAssessment().getItemsSize());
		assertEquals(0, response.getAssessment().getItems().get(0).getAssetsSize());

		deleteTestFile(uniqueId);
	}

	@Test
	public void saveOrUpdateAssessment_WithItemsEditItem() throws Exception {
		Assessment a = new Assessment();
		a.setName("Get to know you survey");
		a.setDescription("A description.");
		a.setIndustry(new Industry().setId(INDUSTRY_1000_ID));
		a.setType(AssessmentType.SURVEY);
		a.setConfiguration(new AssessmentOptions());

		AssessmentSaveRequest saveRequest = new AssessmentSaveRequest();
		saveRequest.setUserId(ANONYMOUS_USER_ID);
		saveRequest.setAssessment(a);

		AssessmentResponse response = service.saveOrUpdateAssessment(saveRequest);

		ItemSaveRequest itemSaveRequest = new ItemSaveRequest();
		itemSaveRequest.setUserId(ANONYMOUS_USER_ID);
		itemSaveRequest.setAssessmentId(response.getAssessment().getId());

		Item i = new Item()
				.setType(ItemType.SINGLE_LINE_TEXT)
				.setPrompt("What's your name?")
				.setGraded(false);
		itemSaveRequest.setItem(i);
		service.addOrUpdateItem(itemSaveRequest);

		AssessmentRequest request = new AssessmentRequest();
		request.setUserId(ANONYMOUS_USER_ID);
		request.setAssessmentId(response.getAssessment().getId());
		request.addToIncludes(AssessmentRequestInfo.ITEM_INFO);

		response = service.findAssessment(request);

		assertEquals(1, response.getAssessment().getItemsSize());

		i.setId(response.getAssessment().getItems().get(0).getId());
		i.setType(ItemType.MULTIPLE_LINE_TEXT);
		i.setPrompt("Tell me about yourself.");

		itemSaveRequest = new ItemSaveRequest();
		itemSaveRequest.setUserId(ANONYMOUS_USER_ID);
		itemSaveRequest.setAssessmentId(response.getAssessment().getId());
		itemSaveRequest.setItem(i);

		service.addOrUpdateItem(itemSaveRequest);

		response = service.findAssessment(new AssessmentRequest()
				.setUserId(ANONYMOUS_USER_ID)
				.setAssessmentId(response.getAssessment().getId())
				.setIncludes(ImmutableSet.of(
						AssessmentRequestInfo.CONTEXT_INFO,
						AssessmentRequestInfo.ITEM_INFO)));

		assertEquals(1, response.getAssessment().getItemsSize());
		assertEquals(ItemType.MULTIPLE_LINE_TEXT, response.getAssessment().getItems().get(0).getType());
		assertEquals("Tell me about yourself.", response.getAssessment().getItems().get(0).getPrompt());
	}

	@Test
	public void saveOrUpdateAssessment_WithItemsEditItemWithChoices() throws Exception {
		Assessment a = new Assessment();
		a.setName("Get to know you survey");
		a.setDescription("A description.");
		a.setIndustry(new Industry().setId(INDUSTRY_1000_ID));
		a.setType(AssessmentType.SURVEY);
		a.setConfiguration(new AssessmentOptions());

		AssessmentSaveRequest saveRequest = new AssessmentSaveRequest();
		saveRequest.setUserId(ANONYMOUS_USER_ID);
		saveRequest.setAssessment(a);

		AssessmentResponse response = service.saveOrUpdateAssessment(saveRequest);

		ItemSaveRequest itemSaveRequest = new ItemSaveRequest();
		itemSaveRequest.setUserId(ANONYMOUS_USER_ID);
		itemSaveRequest.setAssessmentId(response.getAssessment().getId());

		Item i = new Item()
				.setType(ItemType.MULTIPLE_CHOICE)
				.setPrompt("Which animals do you like?")
				.setGraded(false);
		i.addToChoices(new Choice().setValue("Monkeys").setCorrect(true));
		i.addToChoices(new Choice().setValue("Horses").setCorrect(true));
		i.addToChoices(new Choice().setValue("Cows").setCorrect(true));
		i.addToChoices(new Choice().setValue("Chickens").setCorrect(true));
		i.addToChoices(new Choice().setValue("Dogs").setCorrect(true));
		i.addToChoices(new Choice().setValue("Cats").setCorrect(true));
		i.addToChoices(new Choice().setValue("Muskrats").setCorrect(true));
		i.addToChoices(new Choice().setValue("Llamas").setCorrect(true));
		i.addToChoices(new Choice().setValue("Alpacas").setCorrect(true));
		itemSaveRequest.setItem(i);
		service.addOrUpdateItem(itemSaveRequest);

		AssessmentRequest request = new AssessmentRequest();
		request.setUserId(ANONYMOUS_USER_ID);
		request.setAssessmentId(response.getAssessment().getId());
		request.addToIncludes(AssessmentRequestInfo.ITEM_INFO);

		response = service.findAssessment(new AssessmentRequest()
				.setUserId(ANONYMOUS_USER_ID)
				.setAssessmentId(response.getAssessment().getId())
				.setIncludes(ImmutableSet.of(
						AssessmentRequestInfo.CONTEXT_INFO,
						AssessmentRequestInfo.ITEM_INFO)));

		assertEquals(1, response.getAssessment().getItemsSize());
		assertEquals(9, response.getAssessment().getItems().get(0).getChoicesSize());

		i.setId(response.getAssessment().getItems().get(0).getId());
		i.setChoices(Lists.newArrayList(
				response.getAssessment().getItems().get(0).getChoices().get(8),
				response.getAssessment().getItems().get(0).getChoices().get(7),
				response.getAssessment().getItems().get(0).getChoices().get(6),
				response.getAssessment().getItems().get(0).getChoices().get(5),
				response.getAssessment().getItems().get(0).getChoices().get(4),
				response.getAssessment().getItems().get(0).getChoices().get(3)
		));

		itemSaveRequest = new ItemSaveRequest();
		itemSaveRequest.setUserId(ANONYMOUS_USER_ID);
		itemSaveRequest.setAssessmentId(response.getAssessment().getId());
		itemSaveRequest.setItem(i);

		service.addOrUpdateItem(itemSaveRequest);

		response = service.findAssessment(new AssessmentRequest()
				.setUserId(ANONYMOUS_USER_ID)
				.setAssessmentId(response.getAssessment().getId())
				.setIncludes(ImmutableSet.of(
						AssessmentRequestInfo.CONTEXT_INFO,
						AssessmentRequestInfo.ITEM_INFO)));

		assertEquals(1, response.getAssessment().getItemsSize());
		assertEquals(6, response.getAssessment().getItems().get(0).getChoicesSize());
		assertEquals("Alpacas", response.getAssessment().getItems().get(0).getChoices().get(0).getValue());
		assertEquals("Llamas", response.getAssessment().getItems().get(0).getChoices().get(1).getValue());
		assertEquals("Muskrats", response.getAssessment().getItems().get(0).getChoices().get(2).getValue());
		assertEquals("Cats", response.getAssessment().getItems().get(0).getChoices().get(3).getValue());
		assertEquals("Dogs", response.getAssessment().getItems().get(0).getChoices().get(4).getValue());
		assertEquals("Chickens", response.getAssessment().getItems().get(0).getChoices().get(5).getValue());
	}

	@Test
	public void saveOrUpdateAssessment_WithItemsRemoveItem() throws Exception {
		Assessment a = new Assessment();
		a.setName("Get to know you survey");
		a.setDescription("A description.");
		a.setIndustry(new Industry().setId(INDUSTRY_1000_ID));
		a.setType(AssessmentType.SURVEY);
		a.setConfiguration(new AssessmentOptions());

		AssessmentSaveRequest saveRequest = new AssessmentSaveRequest();
		saveRequest.setUserId(ANONYMOUS_USER_ID);
		saveRequest.setAssessment(a);

		AssessmentResponse response = service.saveOrUpdateAssessment(saveRequest);

		ItemSaveRequest itemSaveRequest = new ItemSaveRequest();
		itemSaveRequest.setUserId(ANONYMOUS_USER_ID);
		itemSaveRequest.setAssessmentId(response.getAssessment().getId());

		Item i = new Item()
				.setType(ItemType.SINGLE_LINE_TEXT)
				.setPrompt("What's your name?")
				.setGraded(false);
		itemSaveRequest.setItem(i);
		service.addOrUpdateItem(itemSaveRequest);

		i = new Item()
				.setType(ItemType.MULTIPLE_LINE_TEXT)
				.setPrompt("Tell us a little bit about yourself")
				.setDescription("E.g. interests, hobbies, skills, etc.")
				.setGraded(false);
		itemSaveRequest.setItem(i);
		service.addOrUpdateItem(itemSaveRequest);

		i = new Item()
				.setType(ItemType.EMAIL)
				.setPrompt("What's your email?");
		itemSaveRequest.setItem(i);
		service.addOrUpdateItem(itemSaveRequest);

		i = new Item()
				.setType(ItemType.DATE)
				.setPrompt("What's your date of birth?");
		itemSaveRequest.setItem(i);
		service.addOrUpdateItem(itemSaveRequest);

		AssessmentRequest request = new AssessmentRequest();
		request.setUserId(ANONYMOUS_USER_ID);
		request.setAssessmentId(response.getAssessment().getId());
		request.addToIncludes(AssessmentRequestInfo.ITEM_INFO);

		response = service.findAssessment(new AssessmentRequest()
				.setUserId(ANONYMOUS_USER_ID)
				.setAssessmentId(response.getAssessment().getId())
				.setIncludes(ImmutableSet.of(
						AssessmentRequestInfo.CONTEXT_INFO,
						AssessmentRequestInfo.ITEM_INFO)));

		assertEquals(4, response.getAssessment().getItemsSize());

		ItemRemoveRequest itemRemoveRequest = new ItemRemoveRequest();
		itemRemoveRequest.setUserId(ANONYMOUS_USER_ID);
		itemRemoveRequest.setAssessmentId(response.getAssessment().getId());
		itemRemoveRequest.setItem(new Item().setId(response.getAssessment().getItems().get(2).getId()));

		service.removeItem(itemRemoveRequest);

		response = service.findAssessment(new AssessmentRequest()
				.setUserId(ANONYMOUS_USER_ID)
				.setAssessmentId(response.getAssessment().getId())
				.setIncludes(ImmutableSet.of(
						AssessmentRequestInfo.CONTEXT_INFO,
						AssessmentRequestInfo.ITEM_INFO)));

		assertEquals(3, response.getAssessment().getItemsSize());
	}

	@Test
	public void saveOrUpdateAssessment_WithItemsReorderItems() throws Exception {
		Assessment a = new Assessment();
		a.setName("Get to know you survey");
		a.setDescription("A description.");
		a.setIndustry(new Industry().setId(INDUSTRY_1000_ID));
		a.setType(AssessmentType.SURVEY);
		a.setConfiguration(new AssessmentOptions());

		AssessmentSaveRequest saveRequest = new AssessmentSaveRequest();
		saveRequest.setUserId(ANONYMOUS_USER_ID);
		saveRequest.setAssessment(a);

		AssessmentResponse response = service.saveOrUpdateAssessment(saveRequest);

		ItemSaveRequest itemSaveRequest = new ItemSaveRequest();
		itemSaveRequest.setUserId(ANONYMOUS_USER_ID);
		itemSaveRequest.setAssessmentId(response.getAssessment().getId());

		Item i = new Item()
				.setType(ItemType.SINGLE_LINE_TEXT)
				.setPrompt("What's your name?")
				.setGraded(false);
		itemSaveRequest.setItem(i);
		service.addOrUpdateItem(itemSaveRequest);

		i = new Item()
				.setType(ItemType.MULTIPLE_LINE_TEXT)
				.setPrompt("Tell us a little bit about yourself")
				.setDescription("E.g. interests, hobbies, skills, etc.")
				.setGraded(false);
		itemSaveRequest.setItem(i);
		service.addOrUpdateItem(itemSaveRequest);

		i = new Item()
				.setType(ItemType.EMAIL)
				.setPrompt("What's your email?");
		itemSaveRequest.setItem(i);
		service.addOrUpdateItem(itemSaveRequest);

		AssessmentRequest request = new AssessmentRequest();
		request.setUserId(ANONYMOUS_USER_ID);
		request.setAssessmentId(response.getAssessment().getId());
		request.addToIncludes(AssessmentRequestInfo.ITEM_INFO);

		response = service.findAssessment(request);

		assertEquals(ItemType.SINGLE_LINE_TEXT, response.getAssessment().getItems().get(0).getType());
		assertEquals(ItemType.MULTIPLE_LINE_TEXT, response.getAssessment().getItems().get(1).getType());
		assertEquals(ItemType.EMAIL, response.getAssessment().getItems().get(2).getType());

		ItemReorderRequest itemReorderRequest = new ItemReorderRequest();
		itemReorderRequest.setUserId(ANONYMOUS_USER_ID);
		itemReorderRequest.setAssessmentId(response.getAssessment().getId());
		itemReorderRequest.setItems(Lists.newArrayList(
				response.getAssessment().getItems().get(1),
				response.getAssessment().getItems().get(2),
				response.getAssessment().getItems().get(0)
		));

		service.reorderItems(itemReorderRequest);

		response = service.findAssessment(request);

		assertEquals(ItemType.MULTIPLE_LINE_TEXT, response.getAssessment().getItems().get(0).getType());
		assertEquals(ItemType.EMAIL, response.getAssessment().getItems().get(1).getType());
		assertEquals(ItemType.SINGLE_LINE_TEXT, response.getAssessment().getItems().get(2).getType());
	}

	@Test
	public void testCopyAssessment_ExistingAssessment_NewAssessmentWithNewId() throws Exception {
		AssessmentCopyRequest copyRequest = new AssessmentCopyRequest();
		copyRequest.setUserId(ANONYMOUS_USER_ID);
		copyRequest.setAssessmentId(ASSESSMENT_ID);

		AssessmentResponse response = service.copyAssessment(copyRequest);

		assertFalse(ASSESSMENT_ID == response.getAssessment().getId());
		assertTrue(startsWith(response.getAssessment().getName(), "Copy of"));
	}

	@Test
	public void submitMultipleItemResponses_WithResponses() throws Exception {
		AbstractAssessment assessment = newAssessmentWithItems();

		AssessmentRequest findRequest = new AssessmentRequest();
		findRequest.setUserId(ANONYMOUS_USER_ID);
		findRequest.setAssessmentId(assessment.getId());
		findRequest.addToIncludes(AssessmentRequestInfo.ITEM_INFO);

		AssessmentResponse response = service.findAssessment(new AssessmentRequest()
				.setUserId(ANONYMOUS_USER_ID)
				.setAssessmentId(assessment.getId())
				.setIncludes(ImmutableSet.of(
						AssessmentRequestInfo.CONTEXT_INFO,
						AssessmentRequestInfo.ITEM_INFO)));

		AttemptStartRequest startRequest = new AttemptStartRequest();
		startRequest.setUserId(ANONYMOUS_USER_ID);
		startRequest.setAssessmentId(response.getAssessment().getId());

		AttemptMultipleItemResponsesRequest responsesRequest = new AttemptMultipleItemResponsesRequest();
		responsesRequest.setUserId(ANONYMOUS_USER_ID);
		responsesRequest.setAssessmentId(response.getAssessment().getId());

		ItemResponses itemResponses = new ItemResponses().setItemId(response.getAssessment().getItems().get(0).getId());
		itemResponses.addToResponses(
				new Response().setChoice(
						new Choice().setId(response.getAssessment().getItems().get(0).getChoices().get(2).getId())
				)
		);

		responsesRequest.addToItemResponses(itemResponses);

		AttemptCompleteRequest completeRequest = new AttemptCompleteRequest();
		completeRequest.setUserId(ANONYMOUS_USER_ID);
		completeRequest.setAssessmentId(response.getAssessment().getId());

		service.startAttempt(startRequest);
		List<Response> responses = service.submitMultipleItemResponses(responsesRequest);
		Attempt attempt = service.completeAttempt(completeRequest);

		assertTrue(responses.size() > 0);
		Assert.assertNotNull(attempt);
		assertEquals(AttemptStatusType.GRADED, attempt.getStatus().getCode());
	}

	@Test
	public void submitItemResponses_WithUploadsAndUpdated() throws Exception {
		String uniqueId = RandomUtilities.generateAlphaNumericString(10);
		initializeTestFile(uniqueId);
		AbstractAssessment assessment = newAssessmentWithAssetItem();

		AssessmentRequest findRequest = new AssessmentRequest();
		findRequest.setUserId(ANONYMOUS_USER_ID);
		findRequest.setAssessmentId(assessment.getId());
		findRequest.addToIncludes(AssessmentRequestInfo.ITEM_INFO);
		findRequest.addToIncludes(AssessmentRequestInfo.LATEST_ATTEMPT_INFO);

		AssessmentResponse response = service.findAssessment(findRequest);

		// Start attempt

		AttemptStartRequest startRequest = new AttemptStartRequest();
		startRequest.setUserId(ANONYMOUS_USER_ID);
		startRequest.setAssessmentId(response.getAssessment().getId());
		service.startAttempt(startRequest);

		// Submit response w/upload

		String file1 = FileUtilities.generateTemporaryFileName();
		String file2 = FileUtilities.generateTemporaryFileName();
		FileUtils.copyFile(new File(STORAGE_TEST_FILE + uniqueId), new File(file1));
		FileUtils.copyFile(new File(STORAGE_TEST_FILE + uniqueId), new File(file2));

		com.workmarket.domains.model.asset.Upload upload1 = uploadService.storeUpload(file1, "filename1", MimeType.MS_WORD.getMimeType());
		com.workmarket.domains.model.asset.Upload upload2 = uploadService.storeUpload(file2, "filename2", MimeType.MS_WORD.getMimeType());

		Response itemResponse = new Response();
		itemResponse.addToUploads(
				new Upload()
						.setUuid(upload1.getUUID())
						.setDescription("An upload")
		);

		ItemResponses itemResponses = new ItemResponses().setItemId(response.getAssessment().getItems().get(0).getId());
		itemResponses.addToResponses(itemResponse);

		AttemptMultipleItemResponsesRequest responsesRequest = new AttemptMultipleItemResponsesRequest();
		responsesRequest.setUserId(ANONYMOUS_USER_ID);
		responsesRequest.setAssessmentId(response.getAssessment().getId());
		responsesRequest.addToItemResponses(itemResponses);

		service.submitMultipleItemResponses(responsesRequest);
		List<Response> responses = service.findAssessment(findRequest).getLatestAttempt().getResponses();

		assertTrue(responses.size() > 0);
		assertFalse(responses.get(0).getAssets().isEmpty());
		assertEquals(1, responses.get(0).getAssets().size());

		// Resubmit response to same item w/new upload and existing asset

		itemResponse = new Response();
		itemResponse.addToUploads(
				new Upload()
						.setUuid(upload2.getUUID())
						.setDescription("An upload")
		);
		itemResponse.addToAssets(
				new Asset()
						.setId(responses.get(0).getAssets().get(0).getId())
						.setUuid(responses.get(0).getAssets().get(0).getUuid())
		);

		itemResponses.getResponses().clear();
		itemResponses.addToResponses(itemResponse);

		responsesRequest.getItemResponses().clear();
		responsesRequest.addToItemResponses(itemResponses);

		service.submitMultipleItemResponses(responsesRequest);
		responses = service.findAssessment(findRequest).getLatestAttempt().getResponses();

		assertTrue(responses.size() > 0);
		assertFalse(responses.get(0).getAssets().isEmpty());
		assertEquals(2, responses.get(0).getAssets().size());

		// Resubmit response to same item w/out new uploads and existing assets

		itemResponse = new Response();
		itemResponse.addToAssets(
				new Asset()
						.setId(responses.get(0).getAssets().get(0).getId())
						.setUuid(responses.get(0).getAssets().get(0).getUuid())
		);
		itemResponse.addToAssets(
				new Asset()
						.setId(responses.get(0).getAssets().get(1).getId())
						.setUuid(responses.get(0).getAssets().get(1).getUuid())
		);

		responsesRequest.getItemResponses().clear();
		responsesRequest.addToItemResponses(itemResponses);

		service.submitMultipleItemResponses(responsesRequest);
		responses = service.findAssessment(findRequest).getLatestAttempt().getResponses();

		assertTrue(responses.size() > 0);
		assertFalse(responses.get(0).getAssets().isEmpty());
		assertEquals(2, responses.get(0).getAssets().size());

		// Resubmit response to same item w/out one asset

		itemResponse = new Response();
		itemResponse.addToAssets(
				new Asset()
						.setId(responses.get(0).getAssets().get(0).getId())
						.setUuid(responses.get(0).getAssets().get(0).getUuid())
		);

		itemResponses.getResponses().clear();
		itemResponses.addToResponses(itemResponse);
		responsesRequest.getItemResponses().clear();
		responsesRequest.addToItemResponses(itemResponses);

		service.submitMultipleItemResponses(responsesRequest);
		responses = service.findAssessment(findRequest).getLatestAttempt().getResponses();

		assertTrue(responses.size() > 0);
		assertFalse(responses.get(0).getAssets().isEmpty());
		assertEquals(1, responses.get(0).getAssets().size());

		deleteTestFile(uniqueId);
	}
}

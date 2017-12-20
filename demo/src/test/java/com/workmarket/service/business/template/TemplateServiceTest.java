package com.workmarket.service.business.template;

import com.codahale.metrics.Meter;
import com.google.common.collect.Maps;
import com.workmarket.common.metric.MetricRegistryFacade;
import com.workmarket.common.template.email.EmailTemplate;
import com.workmarket.common.template.email.NotificationEmailTemplate;
import com.workmarket.common.template.notification.NotificationUserNotificationTemplate;
import com.workmarket.common.template.push.PushTemplate;
import com.workmarket.common.template.voice.VoiceTemplate;
import com.workmarket.service.infra.business.UserRoleService;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.service.business.UserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.velocity.VelocityEngineUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TemplateServiceTest {

	@Mock
	UserService userService;
	@Mock
	UserRoleService userRoleService;
	@Mock
	MetricRegistryFacade metricRegistryFacade;
	@InjectMocks
	TemplateServiceImpl templateService = spy(new TemplateServiceImpl());

	// assumes working directory is application/backend/
	public static final String
		TEMPLATE_DIR = "src/main/resources/template",
		VELOCITY_ROOT = "src/main/resources";
	private static final Long USER_ID = 1L;

	private VelocityEngine velocityEngine;
	private Map<String, Object> model;
	private User user;
	private Company company;
	private VoiceTemplate template;

	@Before
	public void setup() {
		// properties out of velocity.xml
		Properties properties = new Properties();
		properties.setProperty("resource.loader", "class");
		properties.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

		try {
			velocityEngine = new VelocityEngine(properties);
		} catch (Exception e) {
			Assert.fail("Could not load velocity engine");
		}

		model = Maps.newHashMap();
		user = mock(User.class);
		company = mock(Company.class);
		template = mock(VoiceTemplate.class);
		doReturn(model).when(templateService).makeMap();
		when(userService.getUser(USER_ID)).thenReturn(user);
		when(userService.findUserById(USER_ID)).thenReturn(user);
		when(user.getId()).thenReturn(USER_ID);
		when(user.getCompany()).thenReturn(company);
		when(template.getToId()).thenReturn(USER_ID);
		when(metricRegistryFacade.meter(any(String.class))).thenReturn(mock(Meter.class));
	}

	@Test
	public void render_userNotificationTemplate_addShowPricingFlag() {
		NotificationUserNotificationTemplate template = mock(NotificationUserNotificationTemplate.class);
		when(template.getToId()).thenReturn(USER_ID);

		templateService.render(template);

		assertTrue(model.containsKey("showPricing"));
	}

	@Test
	public void render_emailTemplate_addShowPricingFlag() {
		EmailTemplate template = mock(EmailTemplate.class);
		when(template.getToId()).thenReturn(USER_ID);

		templateService.render(template);

		assertTrue(model.containsKey("showPricing"));
	}

	@Test
	public void render_voiceTemplate_addShowPricingFlag() {
		templateService.render(template);

		assertTrue(model.containsKey("showPricing"));
	}

	@Test
	public void render_notificationEmailTemplate_addShowPricingFlag() {
		NotificationEmailTemplate template = mock(NotificationEmailTemplate.class);
		when(template.getToId()).thenReturn(USER_ID);

		templateService.render(template);

		assertTrue(model.containsKey("showPricing"));
	}

	@Test
	public void render_pushTemplate_addShowPricingFlag() {
		PushTemplate template = mock(PushTemplate.class);
		when(template.getToId()).thenReturn(USER_ID);

		templateService.render(template);

		assertTrue(model.containsKey("showPricing"));
	}

	@Test
	public void render_userIdIsNull_earlyReturn() {
		when(template.getToId()).thenReturn(null);

		templateService.render(template);

		verify(userService, never()).findUserById(anyLong());
	}

	@Test(expected = IllegalArgumentException.class)
	public void render_userDoesNotExist_throwException() {
		when(userService.findUserById(USER_ID)).thenReturn(null);

		templateService.render(template);
	}

	@Test(expected = IllegalArgumentException.class)
	public void render_userCompanyDoesNotExist_throwException() {
		when(user.getCompany()).thenReturn(null);

		templateService.render(template);
	}

	@Test
	public void render_isHidePricingFalse_showPricingTrue() {
		templateService.render(template);

		assertTrue((boolean) model.get("showPricing"));
	}


	@Test
	public void render_isHidePricingTrue_isNotAllowedToViewPrice_showPricingFalse() {
		when(company.isHidePricing()).thenReturn(true);
		when(userRoleService.hasAclRole(eq(user), anyLong())).thenReturn(false);

		templateService.render(template);

		Assert.assertFalse((boolean) model.get("showPricing"));
	}

	@Test
	public void render_isHidePricingTrue_isAdmin_showPricingTrue() {
		when(company.isHidePricing()).thenReturn(true);
		when(userRoleService.hasAclRole(user, AclRole.ACL_ADMIN)).thenReturn(true);

		templateService.render(template);

		assertTrue((boolean) model.get("showPricing"));
	}

	@Test
	public void render_isHidePricingTrue_isManager_showPricingTrue() {
		when(company.isHidePricing()).thenReturn(true);
		when(userRoleService.hasAclRole(user, AclRole.ACL_MANAGER)).thenReturn(true);

		templateService.render(template);

		assertTrue((boolean) model.get("showPricing"));
	}

	@Test
	public void render_isHidePricingTrue_isDispatcher_showPricingTrue() {
		when(company.isHidePricing()).thenReturn(true);
		when(userRoleService.hasAclRole(user, AclRole.ACL_DISPATCHER)).thenReturn(true);

		templateService.render(template);

		assertTrue((boolean) model.get("showPricing"));
	}

	@Test
	public void validateAllTemplates() throws Exception {
		List<File> templates = new ArrayList<>();

		findAllTemplates(new File(TEMPLATE_DIR), templates);

		List<String> brokenTemplates = new ArrayList<>();

		for (File template : templates) {
			String expectedPath = template.getPath();

			// fix windows back slashes in path
			expectedPath = expectedPath.replace('\\', '/');

			// move to velocity root
			expectedPath = expectedPath.replaceFirst(VELOCITY_ROOT, "");

			try {
				VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, expectedPath, TemplateServiceImpl.ENCODING, null);
			} catch (VelocityException e) {
				brokenTemplates.add(expectedPath);
			}
		}

		assertTrue("The following templates have invalid syntax: " + StringUtils.join(brokenTemplates, '\n'), brokenTemplates.isEmpty());
	}

	private void findAllTemplates(File directory, List<File> templates) {
		File[] contents = directory.listFiles();
		List<File> directories = new ArrayList<>();

		if (contents == null)
			Assert.fail("Failed to read templates... is your working directory correct? This test requires it to be /application/backend");

		for (File content : contents) {
			if (content.isDirectory()) {
				directories.add(content);
			} else if (content.getName().endsWith(".vm")) {
				templates.add(content);
			}
		}

		for (File subDirectory : directories) {
			findAllTemplates(subDirectory, templates);
		}
	}

	@Test
	public void getRenderErrors_validNotification() {
		final List<String> errors = templateService.getRenderErrors("Everything looks good\nhere, you guys!");
		assertTrue(errors.isEmpty());
	}

	@Test
	public void getRenderErrors_dollarSignsAreOk() {
		final List<String> errors = templateService.getRenderErrors("The price of beef is $ALLTHEMONEY");
		assertTrue(errors.isEmpty());
	}

	@Test
	public void getRenderErrors_openingButNotClosingBrace() {
		final List<String> errors = templateService.getRenderErrors("Oh what ${happensNow?");
		assertTrue(errors.isEmpty());
	}

	@Test
	public void getRenderErrors_closingButNotOpeningBrace() {
		final List<String> errors = templateService.getRenderErrors("Oh what happensNow}?");
		assertTrue(errors.isEmpty());
	}

	@Test
	public void getRenderErrors_singleUnrenderedVariable() {
		final String unrenderedVariable = "${bad_thing_here}";
		final List<String> errors = templateService.getRenderErrors(
			String.format("Oh no I found something\n%s Here it was!\n", unrenderedVariable));
		assertEquals(1, errors.size());
		assertEquals(errors.get(0), unrenderedVariable);
	}

	@Test
	public void getRenderErrors_threeUnrenderedVariables() {
		final String unrenderedVariable1 = "${bad_thing_here}";
		final String unrenderedVariable2 = "${oh_no}";
		final String unrenderedVariable3 = "${dont_forget_about_me}";
		final List<String> errors = templateService.getRenderErrors(
			String.format(
				"Oh no I found something\n%s Here it was! (and another: %s)\nMaybe a third: %s",
				unrenderedVariable1,
				unrenderedVariable2,
				unrenderedVariable3));
		assertEquals(3, errors.size());
		assertEquals(errors.get(0), unrenderedVariable1);
		assertEquals(errors.get(1), unrenderedVariable2);
		assertEquals(errors.get(2), unrenderedVariable3);
	}
}


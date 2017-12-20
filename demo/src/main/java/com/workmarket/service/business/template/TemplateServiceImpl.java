package com.workmarket.service.business.template;

import com.codahale.metrics.MetricRegistry;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.workmarket.common.kafka.KafkaClient;
import com.workmarket.common.kafka.KafkaData;
import com.workmarket.common.kafka.KafkaUtil;
import com.workmarket.common.kafkametadata.DataAccessType;
import com.workmarket.common.metric.MetricRegistryFacade;
import com.workmarket.common.metric.WMMetricRegistryFacade;
import com.workmarket.common.template.NotificationModel;
import com.workmarket.common.template.Template;
import com.workmarket.common.template.UserNotification;
import com.workmarket.common.template.email.EmailTemplate;
import com.workmarket.common.template.email.NotificationEmailTemplate;
import com.workmarket.common.template.notification.UserNotificationTemplate;
import com.workmarket.common.template.pdf.InvoicePDFTemplate;
import com.workmarket.common.template.pdf.PDFTemplate;
import com.workmarket.common.template.push.PushTemplate;
import com.workmarket.common.template.voice.VoiceTemplate;
import com.workmarket.configuration.ConfigurationService;
import com.workmarket.service.infra.business.UserRoleService;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.account.InvoicePaymentTransaction;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.domains.model.changelog.user.UserChangeLog;
import com.workmarket.domains.model.changelog.work.WorkChangeLog;
import com.workmarket.domains.model.invoice.AbstractInvoice;
import com.workmarket.domains.model.invoice.CreditMemoAudit;
import com.workmarket.domains.payments.service.AccountRegisterService;
import com.workmarket.domains.payments.service.CreditMemoAuditService;
import com.workmarket.service.business.UserService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.tools.generic.DateTool;
import org.apache.velocity.tools.generic.DisplayTool;
import org.apache.velocity.tools.generic.EscapeTool;
import org.apache.velocity.tools.generic.LinkTool;
import org.apache.velocity.tools.generic.MathTool;
import org.apache.velocity.tools.generic.NumberTool;
import org.apache.velocity.tools.generic.ResourceTool;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import javax.annotation.PostConstruct;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang.StringUtils.isNotBlank;

@Service
public class TemplateServiceImpl implements TemplateService {

	@Autowired private UserService userService;
	@Autowired private UserRoleService userRoleService;
	@Autowired @Qualifier("accountRegisterServicePaymentTermsImpl")
	private AccountRegisterService accountRegisterServicePaymentTerms;

	@Autowired
	private CreditMemoAuditService creditMemoAuditService;

	@Autowired
	@Qualifier("AppKafkaClient")
	private KafkaClient kafkaClient;
	private static final String RENDERING_BASE_TOPIC = "template_rendering";
	private static final String RENDER_ERROR_LOG_TOPIC = RENDERING_BASE_TOPIC + ".error_log";
	private static final Pattern UNRENDERED_NOTIFICATION_PATTERN = Pattern.compile("\\$\\{[^}]*\\}");

	@Autowired
	private MetricRegistry metricRegistry;
	private MetricRegistryFacade metricRegistryFacade;

	public static final String ENCODING = "UTF-8";
	private static final Locale DEFAULT_LOCALE = Locale.US;
	private static final String srcImageLogo = "images/logo.png";

	@Value("${baseurl}")
	public String baseurl;

	@Value("${assignment.details.url}")
	private String assignment_details_url;

	private static final Log logger = LogFactory.getLog(TemplateService.class);

	// use this to render non-HTML templates
	@Autowired @Qualifier("velocityEngine") private VelocityEngine velocityEngine;

	// use this when rendering HTML templates to automatically escape HTML for all variables
	// for example <script> becomes &lt;script&gt;
	@Autowired @Qualifier("htmlVelocityEngine") private VelocityEngine htmlVelocityEngine;

	@PostConstruct
	private void init() {
		metricRegistryFacade = new WMMetricRegistryFacade(metricRegistry, RENDERING_BASE_TOPIC);
	}

	public Map<String, Object> makeMap() {
		return Maps.newHashMap();
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private Map<String, Object> newModel(Template template, Locale locale) {
		Map model = makeMap();

		model.put("locale", locale);
		model.put("baseurl", baseurl);
		model.put("number", new NumberTool());
		model.put("number", new NumberTool());
		model.put("date", new DateTool());
		model.put("resource", new ResourceTool());
		model.put("display", new DisplayTool());
		model.put("escape", new EscapeTool());
		model.put("link", new LinkTool());
		model.put("math", new MathTool());
		model.put("logoURL", ClassUtils.getDefaultClassLoader().getResource(srcImageLogo).getPath());

		if (template instanceof NotificationModel) {
			model.put("template", template.getModel());
		} else {
			model.put("template", template);
		}

		// Hydrate the destination user so we have access to personalize the
		// message and hash entity ids
		// TODO Refactor this, probably into EmailTemplate

		Long toUserId = null;
		if (template instanceof UserNotification) {
			UserNotification userNotification = (UserNotification) template;
			Assert.notNull(userNotification.getToId());
			User toUser = userService.getUser(userNotification.getToId());
			model.put("toUser", toUser);

			toUserId = toUser.getId();

		} else if (template instanceof EmailTemplate) {
			EmailTemplate emailTemplate = (EmailTemplate) template;

			// Set recipient user (if found)
			toUserId = emailTemplate.getToId() != null
					? emailTemplate.getToId()
					: userService.findUserIdByEmail(emailTemplate.getToEmail());

			if (toUserId != null) {
				model.put("toUser", userService.getUser(toUserId));
			}

			// Set sender user (if found)
			if (emailTemplate.getFromId() != null) {
				model.put("fromUser", userService.getUser(emailTemplate.getFromId()));
			}
		}

		if (template instanceof VoiceTemplate) {
			VoiceTemplate voiceTemplate = (VoiceTemplate) template;
			model.put("callbackUri", voiceTemplate.getCallbackURI());
			model.put("wmSupportPhoneNumber", ConfigurationService.WM_SUPPORT_PHONE_NUMBER);

			toUserId = voiceTemplate.getToId();
		}

		if (template instanceof NotificationEmailTemplate) {
			NotificationEmailTemplate emailTemplate = (NotificationEmailTemplate) template;
			if (emailTemplate.getFromId() != null && emailTemplate.getFromId() > 0) {
				User fromUser = userService.getUser(emailTemplate.getFromId());
				model.put("fromUser", fromUser);
				model.put("fromUserCompanyIdPadded", StringUtils.leftPad(String.valueOf(fromUser.getCompany().getId()), 4, '0'));
			}

			if (emailTemplate.getOnBehalfOfId() != null) {
				User onBehalfOfUser = userService.getUser(emailTemplate.getOnBehalfOfId());
				model.put("onBehalfOfUser", onBehalfOfUser);
			}

			toUserId = emailTemplate.getToId();
		}

		if (template instanceof PushTemplate) {
			PushTemplate pushTemplate = (PushTemplate) template;
			if (pushTemplate.getToId() != null) {
				model.put("toUser", userService.getUser(pushTemplate.getToId()));
				toUserId = pushTemplate.getToId();
			}

			if (pushTemplate.getFromId() != null) {
				model.put("fromUser", userService.getUser(pushTemplate.getFromId()));
			}
		}

		addShowPricingFlag(toUserId, model);

		// Special formatter for unit price which can now be entered with 3 decimal places
		DecimalFormat unitPriceFormat = new DecimalFormat();
		unitPriceFormat.setMinimumFractionDigits(2);
		unitPriceFormat.setMaximumFractionDigits(3);
		unitPriceFormat.setRoundingMode(RoundingMode.HALF_UP);
		model.put("unitPriceFormat", unitPriceFormat);

		return model;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private void addShowPricingFlag(Long toUserId, Map<String, Object> model) {
		Assert.notNull(model);

		if (toUserId == null) { return; }

		User toUser = userService.findUserById(toUserId);
		Assert.notNull(toUser);
		Assert.notNull(toUser.getCompany());

		if (toUser.getCompany().isHidePricing()) {
			boolean isAllowedToViewPricing =
				userRoleService.hasAclRole(toUser, AclRole.ACL_ADMIN) ||
				userRoleService.hasAclRole(toUser, AclRole.ACL_MANAGER) ||
				userRoleService.hasAclRole(toUser, AclRole.ACL_DISPATCHER);
			model.put("showPricing", isAllowedToViewPricing);
		} else {
			if (userService.isEmployeeWorker(toUser)) {
				model.put("showPricing", false);
			} else {
				model.put("showPricing", true);
			}
		}
	}

	@Override
	public String render(Template template) {
		return render(template, DEFAULT_LOCALE);
	}

	@Override
	public String renderSubject(Template template) {
		return renderSubject(template, DEFAULT_LOCALE);
	}

	@Override
	public <T extends WorkChangeLog> String renderChangeLogTemplate(T changeLog) {
		Assert.notNull(changeLog);

		Map<String, Object> m = Maps.newHashMap();
		m.put("template", changeLog);

		return VelocityEngineUtils.mergeTemplateIntoString(htmlVelocityEngine, "/template/changelog/work/"
				+ changeLog.getClass().getSimpleName() + ".vm", ENCODING, m);
	}

	@Override
	public <T extends UserChangeLog> String renderChangeLogTemplate(T changeLog) {
		Assert.notNull(changeLog);

		Map<String, Object> m = Maps.newHashMap();
		m.put("template", changeLog);

		return VelocityEngineUtils.mergeTemplateIntoString(htmlVelocityEngine, "/template/changelog/user/"
				+ changeLog.getClass().getSimpleName() + ".vm", ENCODING, m);
	}

	@Override
	public String renderWorkCalendarTemplate(Object model) {
		Assert.notNull(model);

		Map<String, Object> m = Maps.newHashMap();
		m.put("display", new DisplayTool());
		m.put("work", model);
		m.put("baseurl", baseurl);
		m.put("assignment_details_url", assignment_details_url);

		return VelocityEngineUtils.mergeTemplateIntoString(velocityEngine,
				"/template/calendar/" + model.getClass().getSimpleName() + ".vm", ENCODING, m);
	}

	@Override
	public String renderPDFTemplate(PDFTemplate pdfTemplate){
		Map<String, Object> model = preparePDFTemplate(pdfTemplate, null);
		return VelocityEngineUtils.mergeTemplateIntoString(htmlVelocityEngine, pdfTemplate.getPath()
			+ pdfTemplate.getClass().getSimpleName() + ".vm", ENCODING, model);
	}

	@Override
	public String renderPDFTemplate(PDFTemplate pdfTemplate, AbstractInvoice invoice) {
		Map<String, Object> model = preparePDFTemplate(pdfTemplate, invoice);
		return VelocityEngineUtils.mergeTemplateIntoString(htmlVelocityEngine, pdfTemplate.getPath()
				+ pdfTemplate.getClass().getSimpleName() + ".vm", ENCODING, model);
	}

	String render(Template template, Locale locale) {
		Assert.notNull(template);

		Map<String, Object> model = newModel(template, locale);

		StringBuilder stringBuilder = new StringBuilder();

		VelocityEngine velocityEngineForTemplate;

		if (template instanceof EmailTemplate || template instanceof PDFTemplate || template instanceof UserNotificationTemplate) {
			// escape html entities for html templates
			velocityEngineForTemplate = htmlVelocityEngine;
		} else {
			// templates that aren't html (e.g. SMS) don't need escaping
			velocityEngineForTemplate = velocityEngine;
		}

		if (StringUtils.isNotBlank(template.getHeaderTemplate())) {
			stringBuilder.append(
				VelocityEngineUtils.mergeTemplateIntoString(
					velocityEngineForTemplate, template.getHeaderTemplatePath(), ENCODING, model));
		}

		if (StringUtils.isNotBlank(template.getTemplateTemplate())) {
			stringBuilder.append(
				VelocityEngineUtils.mergeTemplateIntoString(
					velocityEngineForTemplate, template.getTemplateTemplatePath(), ENCODING, model));
		}
		if (StringUtils.isNotBlank(template.getFooterTemplate())) {
			stringBuilder.append(
				VelocityEngineUtils.mergeTemplateIntoString(
					velocityEngineForTemplate, template.getFooterTemplatePath(), ENCODING, model));
		}

		final String renderedNotification = stringBuilder.toString();
		final String templateName = template.getTemplateTemplate();
		final List<String> renderErrors = getRenderErrors(renderedNotification);

		if (renderErrors.isEmpty()) {
			metricRegistryFacade.meter(templateName + ".success").mark();
		} else {
			metricRegistryFacade.meter(templateName + ".failure").mark();
			final Map<String, Object> metadata = KafkaUtil.makeMetadata(
				null,
				DataAccessType.CREATE,
				"rendered_template");

			final ImmutableMap<String, Object> data = ImmutableMap.of(
				"templateName", templateName,
				"errors", renderErrors,
				"time", DateTime.now(DateTimeZone.UTC).toString(),
				"time-eastern", DateTime.now(DateTimeZone.forID("America/New_York")).toString());
			final KafkaData kafkaData = new KafkaData(data, metadata);
			kafkaClient.send(RENDER_ERROR_LOG_TOPIC, kafkaData);
		}
		return renderedNotification;
	}

	/**
	 * Pull out the render errors from renderedNotification. Our goal is to return all the un-replaced variables.
	 * If there are no errors, an empty list is returned.
	 * Anything in the rendered template that has a ${...} block is considered an error.
	 *
	 * @param renderedNotification
	 * @return a list containing all the variables that were not replaced.
     */
	@VisibleForTesting
	List<String> getRenderErrors(String renderedNotification) {
		final List<String> errors = new ArrayList<>();

		final Matcher matcher = UNRENDERED_NOTIFICATION_PATTERN.matcher(renderedNotification);
		while (matcher.find()) {
			errors.add(matcher.group());
		}

		return errors;
	}

	String renderSubject(Template template, Locale locale) {
		Assert.notNull(template);

		Map<String, Object> model = newModel(template, locale);

		StringBuilder stringBuilder = new StringBuilder();

		if (isNotBlank(template.getSubjectTemplatePath()) && isNotBlank(template.getSubjectTemplate())) {
			stringBuilder.append(
				VelocityEngineUtils.mergeTemplateIntoString(
					velocityEngine, template.getSubjectTemplatePath(), ENCODING, model));
		} else {
			logger.error("Subject template file does not exist: " + template.getSubjectTemplatePath());
		}

		return stringBuilder.toString();
	}

	public Map<String, Object> preparePDFTemplate(PDFTemplate pdfTemplate, AbstractInvoice invoice) {

		Map<String, Object> model = Maps.newHashMap();
		model.put("template", pdfTemplate);
		model.put("baseurl", baseurl);
		model.put("number", new NumberTool());
		model.put("date", new DateTool());
		model.put("math", new MathTool());
		model.put("logoURL", ClassUtils.getDefaultClassLoader().getResource(srcImageLogo).getPath());
		if(pdfTemplate instanceof InvoicePDFTemplate && invoice != null){
			InvoicePaymentTransaction invoicePaymentTransaction = accountRegisterServicePaymentTerms.findInvoicePaymentTransactionByInvoice(invoice);
			CreditMemoAudit creditMemoAudit = creditMemoAuditService.findByOriginalInvoiceId(invoice.getId());
			model.put("isCreditMemoIssuedForUnpaidInvoice", creditMemoAudit != null && invoicePaymentTransaction == null);
			model.put("creditMemo", creditMemoAudit != null?creditMemoAudit.getCreditMemo():null);
		}
		return model;
	}
}

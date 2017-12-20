
package com.workmarket.common.template.voice;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.common.template.Template;
import com.workmarket.configuration.Constants;

public class VoiceTemplate extends Template {

	/**
	 *
	 */
	private static final long serialVersionUID = -7571642888564282048L;

	private final String templateTemplate;
	private final String templateTemplatePath;

	private Long fromId;
	private Long toId;
	private String toNumber;
	private String msg;
	private NotificationType notificationType;
	private String currentState = "start";
	private String callbackURI;

	public VoiceTemplate() {
		this.templateTemplate = canonicalizeClassName(this.getClass().getSimpleName());
		this.templateTemplatePath = makeVoiceTemplatePath(getTemplateTemplate());
	}

	public VoiceTemplate(String toNumber, String msg) {
		this.templateTemplate = canonicalizeClassName(this.getClass().getSimpleName());
		this.templateTemplatePath = makeVoiceTemplatePath(getTemplateTemplate());
		this.toNumber = toNumber;
		this.msg = msg;
	}

	public VoiceTemplate(Long fromId, Long toId, NotificationType notificationType) {
		this.templateTemplate = canonicalizeClassName(this.getClass().getSimpleName());
		this.templateTemplatePath = makeVoiceTemplatePath(getTemplateTemplate());
		this.fromId = fromId;
		this.toId = toId;
		this.notificationType = notificationType;
	}

	public String getCurrentState() {
		return currentState;
	}

	public void setCurrentState(String currentState) {
		this.currentState = currentState;
	}

	public Long getFromId() {
		return fromId;
	}

	public void setFromId(Long fromId) {
		this.fromId = fromId;
	}

	public Long getToId() {
		return toId;
	}

	public void setToId(Long toId) {
		this.toId = toId;
	}

	public String getToNumber() {
		return toNumber;
	}

	public void setToNumber(String toNumber) {
		this.toNumber = toNumber;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public void setNotificationType(NotificationType notificationType) {
		this.notificationType = notificationType;
	}

	public NotificationType getNotificationType() {
		return notificationType;
	}

	public String getHeaderTemplate() {
		return null;
		// return Constants.VOICE_HEADER_TEMPLATE;
	}

	public String getTemplateTemplate() {
		return this.templateTemplate;
	}

	public String getFooterTemplate() {
		return null;
		// return Constants.VOICE_FOOTER_TEMPLATE;
	}

	public String getHeaderTemplatePath() {
		return null;
		// return Constants.VOICE_TEMPLATE_DIRECTORY_PATH + "/" + getHeaderTemplate() + Constants.VOICE_TEMPLATE_EXTENSION;
	}

	public String getTemplateTemplatePath() {
		return this.templateTemplatePath;
	}

	public String getFooterTemplatePath() {
		return null;
		// return Constants.VOICE_TEMPLATE_DIRECTORY_PATH + "/" + getFooterTemplate() + Constants.VOICE_TEMPLATE_EXTENSION;
	}

	public String getCallbackURI() {
		return callbackURI;
	}

	public void setCallbackURI(String callbackURI) {
		this.callbackURI = callbackURI;
	}

	@Override
	public String getPath() {
		return "/template/voice/";
	}

	static String makeVoiceTemplatePath(final String pathName) {
		return Constants.VOICE_TEMPLATE_DIRECTORY_PATH +
			"/" +
			pathName +
			Constants.VOICE_TEMPLATE_EXTENSION;
	}
}

package com.workmarket.domains.model.integration.webhook;

import com.google.common.collect.Sets;

import com.workmarket.domains.model.DeletableEntity;
import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.domains.model.integration.IntegrationEventType;
import com.workmarket.domains.work.model.EnsuresUuid;
import com.workmarket.domains.work.model.HasUuid;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Where;

import java.io.Serializable;
import java.net.URL;
import java.util.Set;
import java.util.regex.Pattern;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity(name = "webHook")
@Table(name = "web_hook")
@AuditChanges
@EntityListeners(EnsuresUuid.class)
public class WebHook extends DeletableEntity implements Serializable, HasUuid {

	public static final String CUSTOM_FIELD_PREFIX = "custom_field_";
	public static final int MAX_CONSECUTIVE_ERRORS = 100;
	public static final String CONTENT_TYPE = "Content-Type";
	public static final Pattern VARIABLE_PATTERN = Pattern.compile("\\$\\{[\\w]+\\}");

	public enum ContentType {
		JSON("application/json"),
		XML("application/xml"),
		HTML("text/html"),
		FORM_ENCODED("application/x-www-form-urlencoded"),
		MULTIPART_FORM("multipart/form-data; boundary=---nextPart"),
		OCTET_STREAM("application/octet-stream"),
		SOAP_XML("application/soap+xml"),
		TEXT_XML("text/xml");

		private String value;

		private ContentType(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		public static ContentType newInstance(String name) {
			return ContentType.find(name);
		}

		private static ContentType find(String name) {
			for (ContentType type : ContentType.values())
				if (type.toString().equalsIgnoreCase(name))
					return type;
			return null;
		}
	}

	public enum MethodType {
		POST,
		PATCH,
		PUT,
		DELETE;

		public static MethodType newInstance(String name) {
			return MethodType.find(name);
		}

		private static MethodType find(String name) {
			for (MethodType type : MethodType.values())
				if (type.toString().equalsIgnoreCase(name))
					return type;
			return null;
		}
	}

	private AbstractWebHookClient webHookClient;
	private boolean enabled = true;
	private IntegrationEventType integrationEventType;
	private URL url;
	private MethodType methodType;
	private String body;
	private ContentType contentType;
	private Set<WebHookHeader> webHookHeaders = Sets.newHashSet();
	private Integer callOrder;
	private boolean suppressApiEvents; // do not initiate this webhook call if the caller is from the API
	private String uuid;

	@ManyToOne(cascade = CascadeType.ALL)
	@Fetch(FetchMode.JOIN)
	@JoinColumn(name = "web_hook_client_id", referencedColumnName = "id", updatable = false)
	public AbstractWebHookClient getWebHookClient() {
		return webHookClient;
	}

	public WebHook setWebHookClient(AbstractWebHookClient webHookClient) {
		this.webHookClient = webHookClient;
		return this;
	}

	@Column(name="url")
	public URL getUrl() {
		return url;
	}

	public WebHook setUrl(URL url) {
		this.url = url;
		return this;
	}

	@Column(name="uuid")
	public String getUuid() {
		return uuid;
	}

	public void setUuid(final String uuid) {
		this.uuid = uuid;
	}

	@Column(name="suppress_api_events")
	public boolean isSuppressApiEvents() {
		return suppressApiEvents;
	}

	public void setSuppressApiEvents(final boolean suppressFromApi) {
		this.suppressApiEvents = suppressFromApi;
	}

	/**
	 * Convenience wrapper.
	 * @return
	 */
	@Transient
	public boolean suppressApiEvents() {
		return (webHookClient != null && webHookClient.isSuppressApiEvents()) || suppressApiEvents;
	}

	@ManyToOne
	@Fetch(FetchMode.JOIN)
	@JoinColumn(name = "event_type_code", referencedColumnName = "code", updatable = false)
	public IntegrationEventType getIntegrationEventType() {
		return integrationEventType;
	}

	public WebHook setIntegrationEventType(IntegrationEventType integrationEventType) {
		this.integrationEventType = integrationEventType;
		return this;
	}

	@Column(name="enabled")
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Column(name="method_type")
	@Enumerated(EnumType.STRING)
	public MethodType getMethodType() {
		return methodType;
	}

	public WebHook setMethodType(MethodType methodType) {
		this.methodType = methodType;
		return this;
	}

	@Column(name="body")
	public String getBody() {
		return body;
	}

	public WebHook setBody(String body) {
		this.body = body;
		return this;
	}

	@Column(name="content_type")
	@Enumerated(EnumType.STRING)
	public ContentType getContentType() {
		return contentType;
	}

	public WebHook setContentType(ContentType contentType) {
		this.contentType = contentType;
		return this;
	}

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "webHook")
	@Where(clause = "deleted = 0")
	public Set<WebHookHeader> getWebHookHeaders() {
		return webHookHeaders;
	}

	public WebHook setWebHookHeaders(Set<WebHookHeader> webHookHeaders) {
		this.webHookHeaders = webHookHeaders;
		return this;
	}

	@Column(name="call_order")
	public Integer getCallOrder() {
		return callOrder;
	}

	public void setCallOrder(Integer callOrder) {
		this.callOrder = callOrder;
	}

	@Override
	public String toString() {
		return "WebHook{" +
				"id=" + getId() +
				", integrationEventType=" + integrationEventType +
				", url=" + url +
				", methodType=" + methodType +
				", contentType=" + contentType +
				", webHookHeaders=" + webHookHeaders +
				", integrationEventType=" + integrationEventType +
				", clientType=" + ((webHookClient == null) ? "" : webHookClient.getClass().getSimpleName()) +
				", companyId=" + ((webHookClient == null) ? "" : webHookClient.getCompany().getId()) +
				"}";
	}
}

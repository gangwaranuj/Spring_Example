package com.workmarket.domains.model.integration.webhook;

import com.workmarket.domains.model.DeletableEntity;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

@Entity(name = "webHookHeader")
@Table(name = "web_hook_header")
@AuditChanges
public class WebHookHeader extends DeletableEntity implements Serializable {

	WebHook webHook;
	String name;
	String value;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "web_hook_id")
	public WebHook getWebHook() {
		return webHook;
	}

	public WebHookHeader setWebHook(WebHook webHook) {
		this.webHook = webHook;
		return this;
	}

	@Column(name="name")
	public String getName() {
		return name;
	}

	public WebHookHeader setName(String name) {
		this.name = name;
		return this;
	}

	@Column(name="value")
	public String getValue() {
		return value;
	}

	public WebHookHeader setValue(String value) {
		this.value = value;
		return this;
	}

	@Override
	public String toString() {
		return "WebHookHeader{" +
				"name=" + name +
				", value=" + value +
				"}";
	}
}

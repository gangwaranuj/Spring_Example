package com.workmarket.helpers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

// The <T extends ResponseBuilderBase<T>> is what allows us to
// chain method calls with subclasses.
//
// The only ceremony is that subclasses will need to be declared like:
// public class AjaxResponseBuilder extends ResponseBuilderBase<AjaxResponseBuilder>
//
// The little bit of redundancy has the payoff of being able to chain method
// calls from the either the super class or sub classes without any other
// casting or generics syntax required.
public class ResponseBuilderBase<T extends ResponseBuilderBase<T>> implements Serializable {
	private static final long serialVersionUID = -5431506162047424749L;

	private boolean successful;
	private List<String> messages;
	private Map<String, Object> data;

	@SuppressWarnings("unchecked") @JsonIgnore
	private final T self = (T)this;

	public boolean isSuccessful() {
		return successful;
	}

	public T setSuccessful(boolean successful) {
		this.successful = successful;
		return self;
	}

	public List<String> getMessages() {
		return messages;
	}

	public T setMessages(List<String> messages) {
		this.messages = messages;
		return self;
	}

	public T addMessage(String message) {
		if (messages == null) messages = Lists.newArrayList();
		messages.add(message);
		return self;
	}

	public boolean hasMessages() {
		return CollectionUtils.isNotEmpty(messages);
	}

	public Map<String, Object> getData() {
		return data;
	}

	public T setData(Map<String, Object> data) {
		this.data = data;
		return self;
	}

	public T addData(String key, Object value) {
		if (data == null) { data = Maps.newLinkedHashMap(); }
		data.put(key, value);
		return self;
	}
}

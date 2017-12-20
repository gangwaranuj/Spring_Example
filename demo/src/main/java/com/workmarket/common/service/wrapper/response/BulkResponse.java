package com.workmarket.common.service.wrapper.response;

import com.google.common.collect.Maps;
import com.workmarket.common.service.status.BaseStatus;
import com.workmarket.common.service.status.ResponseStatus;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Map;

import static org.apache.commons.collections.MapUtils.isEmpty;

/**
 * Created by nick on 4/5/13 4:36 PM
 *
 * K is a key that is used to look up responses (usually a property on a bean)
 * V is the value.
 *
 */
public abstract class BulkResponse<K, V extends Response> implements Response {

	protected Map<K, V> responses = Maps.newLinkedHashMap();

	// if bulkStatus is not set explicitly, it is not used
	protected ResponseStatus bulkStatus = null;

	public BulkResponse() {
		super();
	}

	public BulkResponse(ResponseStatus bulkStatus) {
		super();
		this.bulkStatus = bulkStatus;
	}

	public V getResponseFor(K key) {
		return responses.get(key);
	}


	public Map<K, V> getResponses() {
		return responses;
	}

	public Collection<V> getAllResponses() {
		return responses.values();
	}

	public void setResponses(Map<K, V> responses) {
		this.responses = responses;
	}

	public void addResponse(K key, V value) {
		responses.put(key, value);
	}

	public void setBulkStatus(ResponseStatus bulkStatus) {
		this.bulkStatus = bulkStatus;
	}

	@Override
	public void setStatus(ResponseStatus status) {
		this.setBulkStatus(status);
	}

	@Override
	public ResponseStatus getStatus() {
		if (bulkStatus != null)
			return bulkStatus;

		if (isEmpty(responses))
			return BaseStatus.FAILURE;

		for (V r : responses.values()) {
			if (!r.isSuccessful())
				return BaseStatus.FAILURE;
		}
		return BaseStatus.SUCCESS;
	}

	@Override
	public boolean isSuccessful() {
		return getStatus().isSuccessful();
	}

	@SuppressWarnings("unchecked")
	public <V extends Response> V getResponse(Object o) {
		if (o == null || !(o.getClass().isAssignableFrom(this.getKeyClass())))
			return null;
		try {
			return (V) responses.get(BeanUtils.getProperty(getKeyClass().cast(o), getKeyName()));
		} catch (IllegalAccessException ignored) {
		} catch (InvocationTargetException ignored) {
		} catch (NoSuchMethodException ignored) {
		}
		return null;
	}

	public abstract Class<?> getKeyClass();

	public abstract String getKeyName();
}

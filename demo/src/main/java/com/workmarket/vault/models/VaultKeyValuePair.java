package com.workmarket.vault.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * Represents a key/value pair from the Vault.
 */
@ApiModel
public class VaultKeyValuePair {
	private final String id;
	private final String value;

	public VaultKeyValuePair() {
		this("","");
	}

	public VaultKeyValuePair(String id, String value) {
		this.id = id;
		this.value = value;
	}

	@ApiModelProperty(name = "id")
	public String getId() {
		return this.id;
	}

	@ApiModelProperty(name = "value")
	public String getValue() {
		return this.value;
	}

	public String toString() {
		return this.id + "=" + this.value;
	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (value != null ? value.hashCode() : 0);
		return result;
	}

	@Override
	public boolean equals(Object var1) {
		if (this == var1) {
			return true;
		} else if (!(var1 instanceof VaultKeyValuePair)) {
			return false;
		} else {
			VaultKeyValuePair var2 = (VaultKeyValuePair) var1;

			return Objects.equals(this.id, var2.id) && Objects.equals(this.value, var2.value);
		}
	}

	public VaultKeyValuePair makeCopy() {
		return new VaultKeyValuePair(id, value);
	}


	@JsonIgnore
	public boolean isEmpty() {
		return StringUtils.isEmpty(getId()) || StringUtils.isEmpty(getValue());
	}
}

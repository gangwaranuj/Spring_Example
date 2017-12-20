package com.workmarket.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.workmarket.domains.model.pricing.FullPricingStrategy;
import com.workmarket.domains.model.pricing.PricingStrategyType;

import java.lang.reflect.Type;

/**
 * Author: rocio
 */
public class FullPricingStrategyDeserializer implements JsonDeserializer<FullPricingStrategy> {

	private static final FullPricingStrategyDeserializer INSTANCE = new FullPricingStrategyDeserializer();

	private FullPricingStrategyDeserializer() {
	}

	public static FullPricingStrategyDeserializer getInstance() {
		return INSTANCE;
	}

	@Override
	public FullPricingStrategy deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		FullPricingStrategy fullPricingStrategy = new FullPricingStrategy();

		JsonElement field = jsonObject.get("pricingStrategyType");
		if (field != null && !field.isJsonNull()) {
			fullPricingStrategy.setPricingStrategyType(PricingStrategyType.valueOf(field.getAsString()));
		}
		field = jsonObject.get("flatPrice");
		if (field != null && !field.isJsonNull()) {
			fullPricingStrategy.setFlatPrice(field.getAsBigDecimal());
		}
		field = jsonObject.get("maxFlatPrice");
		if (field != null && !field.isJsonNull()) {
			fullPricingStrategy.setMaxFlatPrice(field.getAsBigDecimal());
		}
		field = jsonObject.get("perHourPrice");
		if (field != null && !field.isJsonNull()) {
			fullPricingStrategy.setPerHourPrice(field.getAsBigDecimal());
		}
		field = jsonObject.get("maxNumberOfHours");
		if (field != null && !field.isJsonNull()) {
			fullPricingStrategy.setMaxNumberOfHours(field.getAsBigDecimal());
		}
		field = jsonObject.get("perUnitPrice");
		if (field != null && !field.isJsonNull()) {
			fullPricingStrategy.setPerUnitPrice(field.getAsBigDecimal());
		}
		field = jsonObject.get("maxNumberOfUnits");
		if (field != null && !field.isJsonNull()) {
			fullPricingStrategy.setMaxNumberOfUnits(field.getAsBigDecimal());
		}
		field = jsonObject.get("initialPerHourPrice");
		if (field != null && !field.isJsonNull()) {
			fullPricingStrategy.setInitialPerHourPrice(field.getAsBigDecimal());
		}
		field = jsonObject.get("initialNumberOfHours");
		if (field != null && !field.isJsonNull()) {
			fullPricingStrategy.setInitialNumberOfHours(field.getAsBigDecimal());
		}
		field = jsonObject.get("additionalPerHourPrice");
		if (field != null && !field.isJsonNull()) {
			fullPricingStrategy.setAdditionalPerHourPrice(field.getAsBigDecimal());
		}
		field = jsonObject.get("maxBlendedNumberOfHours");
		if (field != null && !field.isJsonNull()) {
			fullPricingStrategy.setMaxBlendedNumberOfHours(field.getAsBigDecimal());
		}
		field = jsonObject.get("initialPerUnitPrice");
		if (field != null && !field.isJsonNull()) {
			fullPricingStrategy.setInitialPerUnitPrice(field.getAsBigDecimal());
		}
		field = jsonObject.get("initialNumberOfUnits");
		if (field != null && !field.isJsonNull()) {
			fullPricingStrategy.setInitialNumberOfUnits(field.getAsBigDecimal());
		}
		field = jsonObject.get("additionalPerUnitPrice");
		if (field != null && !field.isJsonNull()) {
			fullPricingStrategy.setAdditionalPerUnitPrice(field.getAsBigDecimal());
		}
		field = jsonObject.get("maxBlendedNumberOfUnits");
		if (field != null && !field.isJsonNull()) {
			fullPricingStrategy.setMaxBlendedNumberOfUnits(field.getAsBigDecimal());
		}
		field = jsonObject.get("salesTaxCollectedFlag");
		if (field != null && !field.isJsonNull()) {
			fullPricingStrategy.setSalesTaxCollectedFlag(field.getAsBoolean());
		}
		field = jsonObject.get("salesTaxRate");
		if (field != null && !field.isJsonNull()) {
			fullPricingStrategy.setSalesTaxRate(field.getAsBigDecimal());
		}
		field = jsonObject.get("additionalExpenses");
		if (field != null && !field.isJsonNull()) {
			fullPricingStrategy.setAdditionalExpenses(field.getAsBigDecimal());
		}
		field = jsonObject.get("bonus");
		if (field != null && !field.isJsonNull()) {
			fullPricingStrategy.setBonus(field.getAsBigDecimal());
		}
		field = jsonObject.get("overridePrice");
		if (field != null && !field.isJsonNull()) {
			fullPricingStrategy.setOverridePrice(field.getAsBigDecimal());
		}
		return fullPricingStrategy;

	}
}

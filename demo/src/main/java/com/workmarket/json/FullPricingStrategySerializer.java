package com.workmarket.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.workmarket.domains.model.pricing.FullPricingStrategy;

import java.lang.reflect.Type;

/**
 * Author: rocio
 */
public class FullPricingStrategySerializer implements JsonSerializer<FullPricingStrategy> {

	private static final FullPricingStrategySerializer INSTANCE = new FullPricingStrategySerializer();

	private FullPricingStrategySerializer() {}

	public static FullPricingStrategySerializer getInstance() {
		return INSTANCE;
	}

	@Override
	public JsonElement serialize(FullPricingStrategy fullPricingStrategy, Type type, JsonSerializationContext jsonSerializationContext) {
		final JsonObject jsonObject = new JsonObject();

		if (fullPricingStrategy.getPricingStrategyType() != null) {
			jsonObject.addProperty("pricingStrategyType", fullPricingStrategy.getPricingStrategyType().toString());
		}
		if (fullPricingStrategy.getFlatPrice() != null) {
			jsonObject.addProperty("flatPrice", fullPricingStrategy.getFlatPrice());
		}
		if (fullPricingStrategy.getMaxFlatPrice() != null) {
			jsonObject.addProperty("maxFlatPrice", fullPricingStrategy.getMaxFlatPrice());
		}
		if (fullPricingStrategy.getPerHourPrice() != null) {
			jsonObject.addProperty("perHourPrice", fullPricingStrategy.getPerHourPrice());
		}
		if (fullPricingStrategy.getMaxNumberOfHours() != null) {
			jsonObject.addProperty("maxNumberOfHours", fullPricingStrategy.getMaxNumberOfHours());
		}
		if (fullPricingStrategy.getPerUnitPrice() != null) {
			jsonObject.addProperty("perUnitPrice", fullPricingStrategy.getPerUnitPrice());
		}
		if (fullPricingStrategy.getMaxNumberOfUnits() != null) {
			jsonObject.addProperty("maxNumberOfUnits", fullPricingStrategy.getMaxNumberOfUnits());
		}
		if (fullPricingStrategy.getInitialPerHourPrice() != null) {
			jsonObject.addProperty("initialPerHourPrice", fullPricingStrategy.getInitialPerHourPrice());
		}
		if (fullPricingStrategy.getInitialNumberOfHours() != null) {
			jsonObject.addProperty("initialNumberOfHours", fullPricingStrategy.getInitialNumberOfHours());
		}
		if (fullPricingStrategy.getAdditionalPerHourPrice() != null) {
			jsonObject.addProperty("additionalPerHourPrice", fullPricingStrategy.getAdditionalPerHourPrice());
		}
		if (fullPricingStrategy.getMaxBlendedNumberOfHours() != null) {
			jsonObject.addProperty("maxBlendedNumberOfHours", fullPricingStrategy.getMaxBlendedNumberOfHours());
		}
		if (fullPricingStrategy.getInitialPerUnitPrice() != null) {
			jsonObject.addProperty("initialPerUnitPrice", fullPricingStrategy.getInitialPerUnitPrice());
		}
		if (fullPricingStrategy.getInitialNumberOfUnits() != null) {
			jsonObject.addProperty("initialNumberOfUnits", fullPricingStrategy.getInitialNumberOfUnits());
		}
		if (fullPricingStrategy.getAdditionalPerUnitPrice() != null) {
			jsonObject.addProperty("additionalPerUnitPrice", fullPricingStrategy.getAdditionalPerUnitPrice());
		}
		if (fullPricingStrategy.getMaxBlendedNumberOfUnits() != null) {
			jsonObject.addProperty("maxBlendedNumberOfUnits", fullPricingStrategy.getMaxBlendedNumberOfUnits());
		}
		if (fullPricingStrategy.getSalesTaxCollectedFlag() != null) {
			jsonObject.addProperty("salesTaxCollectedFlag", fullPricingStrategy.getSalesTaxCollectedFlag());
		}
		if (fullPricingStrategy.getSalesTaxRate() != null) {
			jsonObject.addProperty("salesTaxRate", fullPricingStrategy.getSalesTaxRate());
		}
		if (fullPricingStrategy.getAdditionalExpenses() != null) {
			jsonObject.addProperty("additionalExpenses", fullPricingStrategy.getAdditionalExpenses());
		}
		if (fullPricingStrategy.getBonus() != null) {
			jsonObject.addProperty("bonus", fullPricingStrategy.getBonus());
		}
		if (fullPricingStrategy.getOverridePrice() != null) {
			jsonObject.addProperty("overridePrice", fullPricingStrategy.getOverridePrice());
		}
		return jsonObject;
	}
}

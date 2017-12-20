package com.workmarket.json.work;


import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.workmarket.web.forms.assignments.WorkNegotiationForm;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class WorkNegotiationFormDeserializer implements JsonDeserializer<WorkNegotiationForm> {
	private static final WorkNegotiationFormDeserializer INSTANCE = new WorkNegotiationFormDeserializer();

	private WorkNegotiationFormDeserializer() {
	}

	public static WorkNegotiationFormDeserializer getInstance() {
		return INSTANCE;
	}

	@Override public WorkNegotiationForm deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		WorkNegotiationForm workNegotiationForm = new WorkNegotiationForm();

		JsonElement field = jsonObject.get("price_negotiation");
		if (field != null && !field.isJsonNull() && field.getAsString().length() != 0) {
			workNegotiationForm.setPrice_negotiation(field.getAsBoolean());
		}
		field = jsonObject.get("schedule_negotiation");
		if (field != null && !field.isJsonNull() && field.getAsString().length() != 0) {
			workNegotiationForm.setSchedule_negotiation(field.getAsBoolean());
		}
		field = jsonObject.get("reschedule_option");
		if (field != null && !field.isJsonNull() && field.getAsString().length() != 0) {
			workNegotiationForm.setReschedule_option(field.getAsString());
		}
		field = jsonObject.get("offer_expiration");
		if (field != null && !field.isJsonNull() && field.getAsString().length() != 0) {
			workNegotiationForm.setOffer_expiration(field.getAsBoolean());
		}
		field = jsonObject.get("pricing");
		if (field != null && !field.isJsonNull() && field.getAsString().length() != 0) {
			workNegotiationForm.setPricing(field.getAsLong());
		}
		field = jsonObject.get("flat_price");
		if (field != null && !field.isJsonNull() && field.getAsString().length() != 0) {
			workNegotiationForm.setFlat_price(field.getAsDouble());
		}
		field = jsonObject.get("per_hour_price");
		if (field != null && !field.isJsonNull() && field.getAsString().length() != 0) {
			workNegotiationForm.setPer_hour_price(field.getAsDouble());
		}
		field = jsonObject.get("max_number_of_hours");
		if (field != null && !field.isJsonNull() && field.getAsString().length() != 0) {
			workNegotiationForm.setMax_number_of_hours(field.getAsDouble());
		}
		field = jsonObject.get("per_unit_price");
		if (field != null && !field.isJsonNull() && field.getAsString().length() != 0) {
			workNegotiationForm.setPer_unit_price(field.getAsDouble());
		}
		field = jsonObject.get("max_number_of_units");
		if (field != null && !field.isJsonNull() && field.getAsString().length() != 0) {
			workNegotiationForm.setMax_number_of_units(field.getAsDouble());
		}
		field = jsonObject.get("initial_per_hour_price");
		if (field != null && !field.isJsonNull() && field.getAsString().length() != 0) {
			workNegotiationForm.setInitial_per_hour_price(field.getAsDouble());
		}
		field = jsonObject.get("initial_number_of_hours");
		if (field != null && !field.isJsonNull() && field.getAsString().length() != 0) {
			workNegotiationForm.setInitial_number_of_hours(field.getAsDouble());
		}
		field = jsonObject.get("additional_per_hour_price");
		if (field != null && !field.isJsonNull() && field.getAsString().length() != 0) {
			workNegotiationForm.setAdditional_per_hour_price(field.getAsDouble());
		}
		field = jsonObject.get("max_blended_number_of_hours");
		if (field != null && !field.isJsonNull() && field.getAsString().length() != 0) {
			workNegotiationForm.setMax_blended_number_of_hours(field.getAsDouble());
		}
		field = jsonObject.get("additional_expenses");
		if (field != null && !field.isJsonNull() && field.getAsString().length() != 0) {
			workNegotiationForm.setAdditional_expenses(field.getAsDouble());
		}
		field = jsonObject.get("bonus");
		if (field != null && !field.isJsonNull() && field.getAsString().length() != 0) {
			workNegotiationForm.setBonus(field.getAsDouble());
		}
		field = jsonObject.get("note");
		if (field != null && !field.isJsonNull() && field.getAsString().length() != 0) {
			workNegotiationForm.setNote(field.getAsString());
		}
		field = jsonObject.get("workerNumber");
		if (field != null && !field.isJsonNull() && field.getAsString().length() != 0) {
			workNegotiationForm.setWorkerNumber(field.getAsString());
		}
		field = jsonObject.get("from");
		if (field != null && !field.isJsonNull() && field.getAsString().length() != 0) {
			DateFormat df = new SimpleDateFormat("MM/dd/yy");
			try {
				workNegotiationForm.setFrom(df.parse(field.getAsString()));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		field = jsonObject.get("fromtime");
		if (field != null && !field.isJsonNull() && field.getAsString().length() != 0) {
			DateFormat df = new SimpleDateFormat("hh:mmaa");
			try {
				workNegotiationForm.setFromtime(df.parse(field.getAsString()));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		field = jsonObject.get("to");
		if (field != null && !field.isJsonNull() && field.getAsString().length() != 0) {
			DateFormat df = new SimpleDateFormat("MM/dd/yy");
			try {
				workNegotiationForm.setTo(df.parse(field.getAsString()));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		field = jsonObject.get("totime");
		if (field != null && !field.isJsonNull() && field.getAsString().length() != 0) {
			DateFormat df = new SimpleDateFormat("hh:mmaa");
			try {
				workNegotiationForm.setTotime(df.parse(field.getAsString()));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		field = jsonObject.get("variable_from");
		if (field != null && !field.isJsonNull() && field.getAsString().length() != 0) {
			DateFormat df = new SimpleDateFormat("MM/dd/yy");
			try {
				workNegotiationForm.setVariable_from(df.parse(field.getAsString()));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		field = jsonObject.get("variable_fromtime");
		if (field != null && !field.isJsonNull() && field.getAsString().length() != 0) {
			DateFormat df = new SimpleDateFormat("hh:mmaa");
			try {
				workNegotiationForm.setVariable_fromtime(df.parse(field.getAsString()));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		field = jsonObject.get("expires_on");
		if (field != null && !field.isJsonNull() && field.getAsString().length() != 0) {
			DateFormat df = new SimpleDateFormat("MM/dd/yy");
			try {
				workNegotiationForm.setExpires_on(df.parse(field.getAsString()));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		field = jsonObject.get("expires_on_time");
		if (field != null && !field.isJsonNull() && field.getAsString().length() != 0) {
			DateFormat df = new SimpleDateFormat("hh:mmaa");
			try {
				workNegotiationForm.setExpires_on_time(df.parse(field.getAsString()));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		return workNegotiationForm;
	}
}
